package com.cube.pojo.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
@ApiModel("用户列表查询请求参数")
public class UserListQueryDTO {

	@ApiModelProperty("页号")
	@Min(value = 1, message = "页号参数有误")
	@Max(value = 1, message = "页号参数有误")
	private Integer currentPage;

	@ApiModelProperty("用户名模糊查询")
	@Pattern(regexp = "^[0-9a-zA-Z_]{0,12}$", message = "用户名查询参数有误")
	private String username;

	@ApiModelProperty("用户手机号模糊查询")
	@Pattern(regexp = "^[0-9]{0,11}$", message = "手机号查询参数有误")
	private String phone;

}
