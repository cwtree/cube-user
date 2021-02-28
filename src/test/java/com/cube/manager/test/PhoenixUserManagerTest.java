package com.cube.manager.test;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.cube.manager.PhoenixUserManager;
import com.cube.pojo.Page;
import com.cube.pojo.doo.PhoenixUser;
import com.cube.pojo.vo.UserListVO;
import com.cube.pojo.vo.UserVO;
import com.github.pagehelper.PageInfo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 
 * @author phoenix
 * @date 2021-2-27
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class PhoenixUserManagerTest {

	@Resource
	private PhoenixUserManager phoenixUserManager;

	@Test
	public void test() {
		PageInfo<PhoenixUser> pageInfo = phoenixUserManager.queryList("", "", -1);
		log.info("原始pageInfo {}", pageInfo);
		Page page = Page.builder().currentPage(pageInfo.getPageNum()).pageSize(pageInfo.getPageSize())
				.total(pageInfo.getTotal()).build();
		List<UserVO> list = CollUtil.newArrayList();
		for (PhoenixUser pu : pageInfo.getList()) {
			UserVO uv = UserVO.builder()
					.createTime(DateUtil.format(pu.getCreateTime(), DatePattern.NORM_DATETIME_PATTERN))
					.username(pu.getName()).email(pu.getEmail()).phone(pu.getPhoneNumber()).build();
			list.add(uv);
		}
		UserListVO ulv = UserListVO.builder().list(list).page(page).build();
		log.info("ULV {}", ulv);
	}

}
