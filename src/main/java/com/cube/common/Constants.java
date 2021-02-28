package com.cube.common;

/**
 * 
 * 
 * @author phoenix
 * @date 2021-2-23
 */
public interface Constants {

	String CAPTCHA_BASE = "ABCDEFGHKMNPRWXZabcdefhjkmnwxz23456789";

	String LOGIN_COOKIE_KEY = "login#cookie";

	String CAPTCHA_CODE_COOKIE_KEY = "CAPTCHA_CODE_COOKIE";

	String CAPTCHA_CODE_CACHE_KEY = "CAPTCHA_CODE_KEY";

	int LOGIN_CACHE_VALID_TIME = 30 * 60;

	int CAPTCHA_CODE_VALID_TIME = 5 * 60;

}
