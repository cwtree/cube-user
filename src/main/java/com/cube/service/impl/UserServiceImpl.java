package com.cube.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cube.manager.PhoenixUserManager;
import com.cube.pojo.Page;
import com.cube.pojo.doo.PhoenixUser;
import com.cube.pojo.vo.UserListVO;
import com.cube.pojo.vo.UserVO;
import com.cube.service.UserService;
import com.github.pagehelper.PageInfo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 
 * @author phoenix
 * @date 2021年2月10日
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

	@Resource
	private PhoenixUserManager phoenixUserManager;

	@Override
	public PhoenixUser getUserById(long id) {
		// TODO Auto-generated method stub
		return phoenixUserManager.getPuById(id);
	}

	@Override
	public void saveUser(PhoenixUser user) {
		// TODO Auto-generated method stub
		log.info("保存用户 {}", user);
		phoenixUserManager.savePu(user);
	}

	@Override
	public PhoenixUser getUserByName(String name) {
		// TODO Auto-generated method stub
		return phoenixUserManager.getPuByName(name);
	}

	@Override
	public PhoenixUser getUserByPhone(String phone) {
		// TODO Auto-generated method stub
		return phoenixUserManager.getPuByPhone(phone);
	}

	@Override
	public int deleteUser(PhoenixUser user) {
		// TODO Auto-generated method stub
		return phoenixUserManager.deleteUser(user);
	}

	@Override
	public UserListVO queryUserList(String username, String phone, int currentPage) {
		// TODO Auto-generated method stub
		PageInfo<PhoenixUser> pageInfo = phoenixUserManager.queryFuzzy(username, phone, currentPage);
		Page page = Page.builder().currentPage(pageInfo.getPageNum()).pageSize(pageInfo.getPageSize())
				.total(pageInfo.getTotal()).build();
		List<UserVO> list = CollUtil.newArrayList();
		for (PhoenixUser pu : pageInfo.getList()) {
			UserVO uv = UserVO.builder()
					.createTime(DateUtil.format(pu.getCreateTime(), DatePattern.NORM_DATETIME_PATTERN))
					.username(pu.getName()).email(pu.getEmail()).phone(pu.getPhoneNumber()).build();
			list.add(uv);
		}
		return UserListVO.builder().list(list).page(page).build();
	}

}
