package com.cube.session;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.cube.common.Constants;
import com.cube.config.MyConfig;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

/**
 * 
 * 
 * @author phoenix
 * @date 2021-2-23
 */
@Component
@Slf4j
public class SessionCache {

	private static final String SESSION_USERNAME_PREFIX = "session#username#";

	@Resource(name = "systemRedis")
	private RedisTemplate<String, Object> redis;

	@Resource
	private MyConfig myConfig;

	/**
	 * 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param cm
	 * @return
	 */
	public UserSession getLoginInfo(MyCookie cm) {
		String cookieValue = cm.getCookieValue(Constants.LOGIN_COOKIE_KEY);
		UserSession us = null;
		if (StrUtil.isNotBlank(cookieValue)) {
			Object obj = redis.opsForValue().get(cookieValue);
			us = obj == null ? null : (UserSession) obj;
			if (us != null) {
				redis.expire(cookieValue, Constants.LOGIN_CACHE_VALID_TIME, TimeUnit.SECONDS);
				redis.expire(SESSION_USERNAME_PREFIX + us.getUsername(), Constants.LOGIN_CACHE_VALID_TIME,
						TimeUnit.SECONDS);
			}
		}
		return us;
	}

	/**
	 * 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param username
	 * @return
	 */
	public String getSessionIdCache(String username) {
		String str = null;
		if (StrUtil.isNotBlank(username)) {
			Object obj = redis.opsForValue().get(SESSION_USERNAME_PREFIX + username);
			str = obj == null ? null : (String) obj;
		}
		return str;
	}

	/**
	 * 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param us
	 * @param cm
	 */
	public void cacheLoginInfo(UserSession us, MyCookie cm) {
		String sessionId = us.getSessionId();
		log.info("当前登录用户username {} sessionId {}", us.getUsername(), sessionId);
		// cookie中存储用户的sessionId信息，用来查询用户登录的session信息
		String sessionKey = DigestUtil.md5Hex(us.getUsername() + sessionId);
		// 根据手机号查询用户信息，初始化UserSession
		redis.opsForValue().set(sessionKey, us, Constants.LOGIN_CACHE_VALID_TIME, TimeUnit.SECONDS);
		// 还需要存储sessionId信息，用来比较
		redis.opsForValue().set(SESSION_USERNAME_PREFIX + us.getUsername(), sessionId, Constants.LOGIN_CACHE_VALID_TIME,
				TimeUnit.SECONDS);
		cm.setCookie(Constants.LOGIN_COOKIE_KEY, sessionKey, URLUtil.toURI(myConfig.getSystemPath()).getHost());
		log.info("设置登录缓存，设置cookie");
	}

	/**
	 * 清除登录缓存信息
	 *
	 * @param cm
	 */
	public void deleteLoginInfo(MyCookie cm) {
		String cookieValue = cm.getCookieValue(Constants.LOGIN_COOKIE_KEY);
		if (StrUtil.isNotBlank(cookieValue)) {
			// 清除登录缓存session
			redis.delete(cookieValue);
			cm.clearCookie(Constants.LOGIN_COOKIE_KEY, URLUtil.toURI(myConfig.getSystemPath()).getHost());
			log.info("清除浏览器cookie username，清除登录缓存");
		}
	}

	/**
	 * 缓存图形验证码
	 * 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param sessionId
	 * @param code
	 */
	public void cacheCaptcha(String sessionId, String code) {
		redis.opsForValue().set(Constants.CAPTCHA_CODE_CACHE_KEY + sessionId, code, Constants.CAPTCHA_CODE_VALID_TIME,
				TimeUnit.SECONDS);
		log.info("缓存图形验证码 {}", code);
	}

	/**
	 * 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param sessionId
	 * @return
	 */
	public String getCacheCaptcha(String sessionId) {
		Object obj = redis.opsForValue().get(Constants.CAPTCHA_CODE_CACHE_KEY + sessionId);
		return obj == null ? null : (String) obj;
	}

	/**
	 * 清除验证码的cookie和缓存
	 * 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param cm
	 * @param sessionId
	 */
	public void deleteCacheCaptcha(MyCookie cm, String sessionId) {
		// 清除cookie
		cm.clearCookie(Constants.CAPTCHA_CODE_COOKIE_KEY, URLUtil.toURI(myConfig.getSystemPath()).getHost());
		redis.delete(Constants.CAPTCHA_CODE_CACHE_KEY + sessionId);
	}

}