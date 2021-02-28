package com.cube.session;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.StrUtil;

/**
 * 
 * 
 * @author phoenix
 * @date 2021-2-23
 */
public class MyCookie {

	private static char CLIENT_IP_STRING_SPLIT = ',';

	private HttpServletRequest request;
	private HttpServletResponse response;

	public MyCookie(HttpServletRequest req, HttpServletResponse res) {
		request = req;
		response = res;
	}

	public String getRemoteIp() {
		String ip = request.getHeader("X-Forwarded-For");
		if (isEffective(ip) && ip.indexOf(CLIENT_IP_STRING_SPLIT) > -1) {
			List<String> list = StrUtil.split(ip, CLIENT_IP_STRING_SPLIT);
			for (String str : list) {
				if (isEffective(str)) {
					ip = str;
					break;
				}
			}

		}
		if (!isEffective(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (!isEffective(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (!isEffective(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (!isEffective(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (!isEffective(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	private boolean isEffective(String remoteAddr) {
		return (null != remoteAddr) && (!"".equals(remoteAddr.trim()))
				&& (!"unknown".equalsIgnoreCase(remoteAddr.trim()));
	}

	/**
	 * 设置cookie
	 *
	 * @param name
	 * @param value
	 * @param domain
	 * @param expire
	 */
	public void setCookie(String name, String value, String domain, int expire) {
		Cookie cookie = new Cookie(name, value);
		cookie.setDomain(domain);
		cookie.setPath("/");
		if (expire >= 0) {
			cookie.setMaxAge(expire);
		}
		cookie.setHttpOnly(true);
		response.addCookie(cookie);
	}

	/**
	 * 获取某个cookie值
	 *
	 * @param name
	 * @return
	 */
	public String getCookieValue(String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return null;
		}
		Cookie cookie = null;
		for (int i = 0; i < cookies.length; i++) {
			cookie = cookies[i];
			if (cookie.getName().equalsIgnoreCase(name)) {
				return cookie.getValue();
			}
		}
		return null;
	}

	/**
	 * setCookie:(). <br/>
	 *
	 * @param name
	 * @param value
	 * @param domain
	 * @author chiwei
	 * @since JDK 1.6
	 */
	public void setCookie(String name, String value, String domain) {
		// maxage=-1表示关闭浏览器则cookie失效
		setCookie(name, value, domain, -1);
	}

	/**
	 * @param name
	 * @param domain
	 */
	public void clearCookie(String name, String domain) {
		setCookie(name, "", domain, 0);
	}

	/**
	 * clearSession:(). <br/>
	 *
	 * @param name
	 * @return
	 * @author chiwei
	 * @since JDK 1.6
	 */
	public void clearSession(String name) {
		request.getSession().removeAttribute(name);
	}
}
