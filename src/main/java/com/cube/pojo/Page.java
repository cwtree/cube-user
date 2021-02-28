package com.cube.pojo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * 
 * @author phoenix
 * @date 2021年2月10日
 */
@Getter
@Setter
@ToString
@Builder
public class Page {

	/**
	 * 第几页
	 */
	private int currentPage;
	/**
	 * 每页大小
	 */
	private int pageSize;

	/**
	 * 总记录数
	 */
	private long total;

}
