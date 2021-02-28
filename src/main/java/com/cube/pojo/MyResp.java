package com.cube.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 前后端交互统一数据格式
 * 
 * @author phoenix
 * @date 2020年2月4日
 */
@Getter
@Setter
@ToString
@Builder
@ApiModel("报文响应完整结构体")
public class MyResp {

	@ApiModelProperty("响应码")
	private int code;
	@ApiModelProperty("响应信息描述")
	private String msg;
	@ApiModelProperty("响应数据体")
	private Object data;
	/**
	 * 该字段用来赋值具体的错误exception等信息，方便通过返回能直接定位到接口错误原因
	 * 避免登服务器查日志，该字段前端不用处理，留给后端研发通过接口返回查看
	 */
	@ApiModelProperty("错误明细，前端可忽略")
	private Object innerMsg;

	public static MyResp result(Resp resp) {
		return MyResp.builder().code(resp.getCode()).msg(resp.getMsg()).build();
	}

	public static MyResp result(Resp resp, Object data) {
		return MyResp.builder().code(resp.getCode()).msg(resp.getMsg()).data(data).build();
	}

	public static MyResp result(Resp resp, String innerMsg) {
		return MyResp.builder().code(resp.getCode()).msg(resp.getMsg()).innerMsg(innerMsg).build();
	}

}
