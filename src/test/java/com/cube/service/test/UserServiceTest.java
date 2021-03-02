package com.cube.service.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.cube.manager.PhoenixUserManager;
import com.cube.pojo.doo.PhoenixUser;
import com.cube.pojo.vo.UserListVO;
import com.cube.service.impl.UserServiceImpl;
import com.github.pagehelper.PageInfo;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 
 * @author phoenix
 * @date 2021年3月2日
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UserServiceTest {

	@Mock
	private PhoenixUserManager phoenixUserManager;

	@InjectMocks
	private UserServiceImpl userService = new UserServiceImpl();

	private static PhoenixUser pu = null;
	private static String TEST = "CUBE-TEST";

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		pu = PhoenixUser.builder().status(1).email(TEST).password("pwd").salt("salt").phoneNumber(TEST).name(TEST)
				.build();
	}

	@Test
	public void test() {
		log.info("UserService测试");
		Mockito.when(phoenixUserManager.getPuById(Mockito.anyLong())).thenReturn(pu);
		assertEquals(userService.getUserById(0).getName(), TEST);

		Mockito.when(phoenixUserManager.getPuByName(Mockito.anyString())).thenReturn(pu);
		assertEquals(userService.getUserByName("").getName(), TEST);

		Mockito.when(phoenixUserManager.getPuByPhone(Mockito.anyString())).thenReturn(pu);
		assertEquals(userService.getUserByPhone("").getName(), TEST);

		Mockito.when(phoenixUserManager.savePu(Mockito.any(PhoenixUser.class))).thenReturn(1);
		userService.saveUser(pu);

		Mockito.when(phoenixUserManager.deleteUser(Mockito.any(PhoenixUser.class))).thenReturn(1);
		userService.deleteUser(pu);

		PageInfo<PhoenixUser> pageInfo = new PageInfo<>();
		pageInfo.setPageSize(1);
		pageInfo.setPageNum(1);
		pageInfo.setTotal(1);
		List<PhoenixUser> list = CollectionUtil.newArrayList();
		list.add(pu);
		pageInfo.setList(list);
		Mockito.when(phoenixUserManager.queryFuzzy(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(pageInfo);
		UserListVO ulv = userService.queryUserList(TEST, TEST, 0);
		assertEquals(ulv.getList().get(0).getUsername(), TEST);
		assertEquals(ulv.getPage().getTotal(), 1);
	}

}
