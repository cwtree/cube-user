package com.cube.aop;

import java.lang.reflect.Method;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cube.config.MyConfig;
import com.cube.pojo.MyResp;
import com.cube.pojo.Resp;
import com.cube.session.MyCookie;
import com.cube.session.SessionCache;
import com.cube.session.UserSession;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 全局登录及权限统一拦截器 Order 数字越小，优先级越高
 * 
 * @author phoenix
 * @date 2021-2-27
 */
@Aspect
@Component
@Slf4j
@Order(1)
public class MyAuthInterceptor {

	private static String HEADER_REFERER = "referer";

	@Resource
	private MyConfig myConfig;

	@Resource
	private SessionCache sessionCache;

	/**
	 * 拦截com.cube.controller包中所有类的public方法，不包含子包
	 */
	public static final String EXEC = "execution(* com.cube.controller.*.* (..))";

	/**
	 * 定义切面执行方法
	 * 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 */
	@Pointcut(EXEC)
	public void interceptor() {
	}

	/**
	 * 包路径下且注解标注的方法进行拦截
	 * 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param point
	 * @return
	 * @throws Throwable
	 */
	@Around("interceptor() && @annotation(com.cube.aop.auth.Auth)")
	public Object doAround(ProceedingJoinPoint point) throws Throwable {
		Method method = ((MethodSignature) point.getSignature()).getMethod();
		if (log.isInfoEnabled()) {
			log.info("进入用户权限统一拦截器 {}", method.getName());
		}
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getResponse();
		// CSRF
		String referer = request.getHeader(HEADER_REFERER);
		if (StrUtil.isBlank(referer)) {
			return MyResp.result(Resp.ACCESS_DENIED_ERROR);
		}
		if (!StrUtil.equals(URLUtil.toURI(referer).getHost(), URLUtil.toURI(myConfig.getSystemPath()).getHost())) {
			log.warn("请求referer {}", referer);
			return MyResp.result(Resp.ACCESS_DENIED_ERROR);
		}
		// 校验是否登录了
		MyCookie cm = new MyCookie(request, response);
		UserSession us = sessionCache.getLoginInfo(cm);
		if (ObjectUtil.isNull(us)) {
			// 未登录，需要跳转到登录首页，跟前端约定一个返回码
			return MyResp.result(Resp.SESSION_INVALID_ERROR);
		} else {
			// 登录过，是否需要强制下线
			String sessionIdCache = sessionCache.getSessionIdCache(us.getUsername());
			if (StrUtil.isBlank(sessionIdCache)) {
				// 登录了，但是sessionIdCache为空，属于异常，要重新登录
				sessionCache.deleteLoginInfo(cm);
				return MyResp.result(Resp.SESSION_INVALID_ERROR);
			}
			if (!StrUtil.equals(sessionIdCache, us.getSessionId())) {
				// sessionId变了，重新登录了，需要强制下线前一个登录的
				sessionCache.deleteLoginInfo(cm);
				return MyResp.result(Resp.FORCE_OFFLINE_ERROR);
			}
		}
		// TODO 是否越权
		// TODO 强制下线
		return point.proceed();
	}

}
