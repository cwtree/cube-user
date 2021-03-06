package com.cube.cache;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义封装manager缓存统一管理
 * 
 * @author phoenix
 * @date 2020年2月10日
 */
@Aspect
@Component
@Slf4j
@Order(2)
public class MyCacheInterceptor {

	public static final String EXEC = "execution(* com.cube.manager.*.* (..))";
	// + "&& !execution(* com.cmcc.hy.phoenix.manager.impl.*.get*(..))"
	// + "&& !execution(* com.cmcc.hy.phoenix.manager.impl.*.set*(..))";
	/**
	 * 缓存key分隔符
	 */
	public static final String SPILIT = "#";
	/**
	 * 系统统一缓存key前缀
	 */
	public static final String CACHE_PREFIX = "CUBE" + SPILIT;
	private static ExpressionParser parser = new SpelExpressionParser();
	/**
	 * 防缓存穿透的空缓存内容
	 */
	public static final String CACHE_NULL = "NULL";

	@Resource(name = "dalRedis")
	private RedisTemplate<String, Object> redis;

	/**
	 *
	 */
	@Pointcut(EXEC)
	public void cache() {
	}

	/**
	 * 切记，这三个缓存注解，不可同时在一个方法上使用 如果出现，一定是你的业务设计有问题
	 * 
	 * @数据操作可归结为 CRUD 操作（增加、读取、更新、删除）
	 * @组合操作 MyCache
	 * @增加：可能需要CachePut
	 * @读取：可能需要CacheGet
	 * @更新：可能需要CacheDel 这里不要加CachePut，简化操作，简单就是高效
	 * @删除：可能需要CacheDel
	 * 
	 * @param point
	 * @return
	 * @throws Throwable
	 */
	@Around("cache()")
	public Object doAround(ProceedingJoinPoint point) throws Throwable {
		Method method = ((MethodSignature) point.getSignature()).getMethod();
		if (log.isInfoEnabled()) {
			log.info("进入manager层 {} 方法 {}", point.getTarget().getClass().getName(), point.getSignature().getName());
		}
		CacheGet get = method.getAnnotation(CacheGet.class);
		if (get != null) {
			if (log.isInfoEnabled()) {
				log.info("检查到独立注解 @CacheGet");
			}
			return getHandle(point, get);
		}
		Object obj = point.proceed();
		// 先清数据库，再清除缓存
		CacheDel del = method.getAnnotation(CacheDel.class);
		if (del != null) {
			if (log.isInfoEnabled()) {
				log.info("检查到独立注解 @CacheDel");
			}
			// 判断返回大于0，说明的确发生了数据库记录删除，再操作缓存
			// 但是在save事务回滚时也需要复用该方法，但是数据库操作返回0
			if ((int) obj > 0) {
				delHandle(point, del);
			}
		}
		return obj;
	}

	private void delHandle(ProceedingJoinPoint point, CacheDel del) throws Throwable {
		// 说明要删除缓存
		if (log.isInfoEnabled()) {
			log.info("CacheDel操作");
		}
		CacheKey[] ck = del.del();
		if (ArrayUtil.isEmpty(ck)) {
			log.error("【致命错误】方法 {} 的CacheDel注解下没有CacheKey注解，使用错误，系统退出！", point.getSignature().getName());
			System.exit(-1);
		}
		for (int i = 0; i < ck.length; i++) {
			CacheKey cacheKey = ck[i];
			String key = genKey(point, del.clazz(), cacheKey);
			redis.delete(key);
		}
	}

	@SuppressWarnings("unchecked")
	private Object getHandle(ProceedingJoinPoint point, CacheGet get) throws Throwable {
		// 说明是读数据操作，优先读取缓存
		if (log.isInfoEnabled()) {
			log.info("CacheGet操作");
		}
		CacheKey ck = get.get();
		Method method = ((MethodSignature) point.getSignature()).getMethod();
		Class<? extends Serializable> clazz = (Class<? extends Serializable>) method.getReturnType();
		String key = genKey(point, clazz, ck);
		int ttl = RandomUtil.randomInt(ck.ttl());
		Object obj = redis.opsForValue().get(key);
		if (ObjectUtil.isEmpty(obj)) {
			obj = point.proceed();
			if (ObjectUtil.isNotEmpty(obj)) {
				redis.opsForValue().set(key, clazz.cast(obj));
				if (log.isInfoEnabled()) {
					log.info("数据库查询数据并设置缓存 {}", key);
				}
			} else {
				// 开启防缓存穿透，设置空缓存
				if (ck.cacheNull()) {
					redis.opsForValue().set(key, CACHE_NULL, ttl, TimeUnit.SECONDS);
					if (log.isInfoEnabled()) {
						log.info("设置空缓存 {}，防缓存穿透", key);
					}
				}
			}
		} else if (CACHE_NULL.equals(obj)) {
			// 缓存查询对象不为空,判断是否是空缓存
			obj = null;
		}
		return obj;
	}

	private String genKey(ProceedingJoinPoint point, Class<?> clazz, CacheKey ck) {
		StringBuilder sb = new StringBuilder();
		String clazzName = clazz.getSimpleName();
		sb.append(CACHE_PREFIX).append(clazzName);
		EvaluationContext context = getContext(point.getArgs(), point);
		String[] keys = ck.key();
		String[] values = ck.value();
		if (ArrayUtil.isEmpty(keys) || ArrayUtil.isEmpty(values)
				|| ArrayUtil.length(keys) != ArrayUtil.length(values)) {
			log.error("【致命错误】方法 {} 的CacheKey注解下key和value配置错误，系统退出！", point.getSignature().getName());
			System.exit(-1);
		}
		for (int i = 0; i < keys.length; i++) {
			sb.append(SPILIT).append(keys[i]).append(SPILIT).append(getValue(context, values[i], String.class));
		}
		if (log.isInfoEnabled()) {
			log.info("key = {}", sb.toString());
		}
		return sb.toString();
	}

	private <T> T getValue(EvaluationContext context, String key, Class<T> clazz) {
		Expression expression = parser.parseExpression(key);
		return expression.getValue(context, clazz);
	}

	private EvaluationContext getContext(Object[] arguments, ProceedingJoinPoint point) {
		String[] parameterNames = new LocalVariableTableParameterNameDiscoverer()
				.getParameterNames(((MethodSignature) point.getSignature()).getMethod());
		if (parameterNames == null) {
			throw new RuntimeException("参数列表不能为null");
		}
		EvaluationContext context = new StandardEvaluationContext();
		for (int i = 0; i < arguments.length; i++) {
			context.setVariable(parameterNames[i], arguments[i]);
		}
		return context;
	}

}
