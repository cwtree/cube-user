package com.cube.common;

/**
 * 正则表达式
 * 
 * @author phoenix
 * @date 2021-2-24
 */
public interface RegexConts {

	String PWD = "^(?![0-9]+$)(?![a-zA-Z]+$)(?![~!@#$%^&*()_+]+$)[0-9a-zA-Z~!@#$%^&*()_+]{8,16}$";

}
