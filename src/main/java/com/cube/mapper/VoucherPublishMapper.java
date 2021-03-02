package com.cube.mapper;

import com.cube.pojo.doo.VoucherPublish;

import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * <p>
 * User Mapper
 * </p>
 *
 * @description: User Mapper
 * @author phoenix
 * @date 2020年2月8日
 */
public interface VoucherPublishMapper
		extends Mapper<VoucherPublish>, IdsMapper<VoucherPublish>, MySqlMapper<VoucherPublish> {

}
