package com.cube.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

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
@NoArgsConstructor
@AllArgsConstructor
public class UserSession implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -2646867971487860905L;
	private long id;
	private String username;
	private String phone;
	private String createdBy;
	private String sessionId;

}
