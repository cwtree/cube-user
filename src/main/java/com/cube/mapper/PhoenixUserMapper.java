package com.cube.mapper;

import java.util.List;

import com.cube.pojo.doo.AgeGroup;
import com.cube.pojo.doo.PhoenixUser;

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
public interface PhoenixUserMapper extends Mapper<PhoenixUser>, IdsMapper<PhoenixUser>, MySqlMapper<PhoenixUser> {

	/**
	 * 按照年龄分组查询
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return
	 */
	List<AgeGroup> selectAgeGroup();

}
