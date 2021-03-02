package com.cube.manager.test;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.cube.manager.PhoenixUserManager;
import com.cube.pojo.doo.PhoenixUser;
import com.github.pagehelper.PageInfo;

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

	private static String TEST = "CUBE-TEST";

	/**
	 * 测试删除用户
	 * 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 */
	@Test
	public void test() {
		log.info("测试保存、删除用户");
		// 先保存一个用户
		PhoenixUser pu = PhoenixUser.builder().status(1).email(TEST).password("pwd").salt("salt").phoneNumber(TEST)
				.name(TEST).build();
		int res = phoenixUserManager.savePu(pu);
		assertEquals(res, 1);
		PhoenixUser idUser = phoenixUserManager.getPuById(pu.getId());
		assertEquals(TEST, idUser.getName());

		idUser = phoenixUserManager.getPuByName(TEST);
		assertEquals(TEST, idUser.getName());

		idUser = phoenixUserManager.getPuByPhone(TEST);
		assertEquals(TEST, idUser.getPhoneNumber());

		PageInfo<PhoenixUser> userList = phoenixUserManager.queryFuzzy(TEST, TEST, -1);
		assertTrue(userList.getList().size() > 0);
		userList = phoenixUserManager.queryFuzzy("", TEST, 1);
		assertTrue(userList.getList().size() > 0);
		userList = phoenixUserManager.queryFuzzy(TEST, "", 10);
		assertTrue(userList.getList().size() <= 0);
		res = phoenixUserManager.deleteUser(pu);
		assertEquals(res, 1);
	}

}
