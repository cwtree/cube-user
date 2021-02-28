package com.cube.pojo.dto;

import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * 
 * @author phoenix
 * @date 2021-2-23
 */
@Getter
@Setter
@ToString
@Builder
@ApiModel("用户名密码登录请求参数")
public class UserLoginDTO {

	@ApiModelProperty("用户名")
	@Pattern(regexp = "^[0-9a-zA-Z_]{4,12}$", message = "用户名密码错误")
	private String username;

	/**
	 * 提交密文，base64编码
	 */
	@ApiModelProperty("用户密码")
	@Pattern(regexp = "^[0-9A-Za-z/=+]+$", message = "用户名密码错误")
	private String pwd;

	@ApiModelProperty("图形验证码")
	@Pattern(regexp = "^[0-9a-zA-Z]{5}$", message = "验证码错误")
	private String captcha;

}
