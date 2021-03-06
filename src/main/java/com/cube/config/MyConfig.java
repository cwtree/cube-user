package com.cube.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * 
 * @author phoenix
 * @date 2021年2月10日
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cube.boot")
public class MyConfig {

	private String systemName;
	private String swaggerSwitch;
	private String requestDecSwitch;
	private String responseEncSwitch;
	private String reqRespAesKey;
	private String systemPath;

}
