package com.cube.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.ToString;

/**
 * 
 * 
 * @author phoenix
 * @date 2021-2-24
 */
@Getter
@Setter
@ToString
@Builder
public class PublicKeyVO {

	private String modulus;
	private String publicExponent;

}
