package com.cube.pojo.doo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tk.mybatis.mapper.annotation.KeySql;
import tk.mybatis.mapper.annotation.Version;

/**
 * 
 * 
 * @author phoenix
 * @date 2021-3-1
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "voucher_publish")
public class VoucherPublish implements Serializable {
	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */
	private static final long serialVersionUID = 6925203830751524347L;

	/**
	 * 主键
	 */
	@Id
	@KeySql(useGeneratedKeys = true)
	private Long id;

	/**
	 * 用户id
	 */
	private Long userId;

	/**
	 * 商家
	 */
	private String merchant;

	/**
	 * 商家剩余代金券金额
	 */
	private Long voucherAmount;

	/**
	 * 版本号，乐观锁
	 */
	@Version
	private Long version;

	/**
	 * 记录创建时间
	 */
	private Date createTime;

	/**
	 * 记录更新时间
	 */
	private Date updateTime;

}
