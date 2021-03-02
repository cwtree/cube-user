package com.cube.mapper.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.cube.mapper.PhoenixUserMapper;
import com.cube.pojo.Page;
import com.cube.pojo.doo.PhoenixUser;
import com.cube.pojo.vo.UserListVO;
import com.cube.pojo.vo.UserVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

/**
 * 实际项目里mapper不在单独写单元测试，都在manager的单测里覆盖了mapper的所有方法
 * 
 * @author phoenix
 * @date 2021-2-27
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class PhoenixUserMapperTest {

	private static int COUNTER = 33;

	@Resource
	private PhoenixUserMapper phoenixUserMapper;
	/**
	 * name字段使用
	 */
	private static String SPECIAL = "CUBETEST";

	private void insert() {
		List<PhoenixUser> list = CollUtil.newArrayList();
		log.info("插入 {} 条数据", COUNTER);
		for (int i = 0; i < COUNTER; i++) {
			PhoenixUser pu = PhoenixUser.builder().name(SPECIAL + "chiwei" + i).createTime(DateUtil.date())
					.email("mail" + i).phoneNumber("phone" + i).password("pwd" + i).salt("salt" + i).status(1).build();
			int temp = phoenixUserMapper.insert(pu);
			assertEquals(temp, 1);
			// 保存后，id有数据
			list.add(pu);
		}
	}

	@Test
	public void test() {
		// INSERT
		insert();
		// 分页列表查询
		Example example = new Example(PhoenixUser.class);
		Criteria c = example.createCriteria();
		c.andLike("name", "%chiwei%");
		c.andLike("phoneNumber", "%phone%");
		// 设置分页信息，排序
		PageHelper.startPage(0 * 10, 10, "name asc");
		PageInfo<PhoenixUser> pageList = new PageInfo<PhoenixUser>(phoenixUserMapper.selectByExample(example));
		log.info("分页列表查询第1页 {} -- {}", pageList, pageList.getList());
		PageHelper.startPage(1 * 10, 10, "name asc");
		pageList = new PageInfo<PhoenixUser>(phoenixUserMapper.selectByExample(example));
		log.info("分页列表查询第2页 {} -- {}", pageList, pageList.getList());
		PageHelper.startPage(2 * 10, 10, "name asc");
		pageList = new PageInfo<PhoenixUser>(phoenixUserMapper.selectByExample(example));
		log.info("分页列表查询第3页 {} -- {}", pageList, pageList.getList());
		PageHelper.startPage(3 * 10, 10, "name asc");
		pageList = new PageInfo<PhoenixUser>(phoenixUserMapper.selectByExample(example));
		log.info("分页列表查询第4页 {} -- {}", pageList, pageList.getList());
		// 列表查询
		example.clear();
		// Example example = new Example(PhoenixUser.class);
		c = example.createCriteria();
		c.andLike("name", "%chiwei%");
		c.andLike("phoneNumber", "%phone%");
		List<PhoenixUser> list2 = phoenixUserMapper.selectByExample(example);
		log.info("列表查询 {}", list2);
		//
	}

	@Test
	public void clearData() {
		Example example = new Example(PhoenixUser.class);
		Criteria c = example.createCriteria();
		c.andLike("name", "%" + SPECIAL + "%");
		int temp = phoenixUserMapper.deleteByExample(example);
		log.info("清空测试数据 {}", temp);
	}

	@Test
	public void testDeleteEntity() {
		List<PhoenixUser> list = phoenixUserMapper.selectAll();
		for (PhoenixUser pu : list) {
			phoenixUserMapper.delete(pu);
		}
	}

	@Test
	public void testQuery() {
		Example example = new Example(PhoenixUser.class);
		Criteria c = example.createCriteria();
		c.andLike("name", "%chiwei%");
		PageInfo<PhoenixUser> list = PageHelper.startPage(0 * 2, 2).setOrderBy("name asc")
				.doSelectPageInfo(() -> phoenixUserMapper.selectByExample(example));
		log.info("条件查询返回list {}", list);
		for (PhoenixUser pu : list.getList()) {
			log.info("数据 {}", pu);
		}
	}

	@Test
	public void testRead() {
		Example example = new Example(PhoenixUser.class);
		Criteria c = example.createCriteria();
		c.andLike("name", "%chiwei%");
		PageInfo<PhoenixUser> pageInfo = PageHelper.startPage(4, 10).setOrderBy("id asc")
				.doSelectPageInfo(() -> phoenixUserMapper.selectByExample(example));
		log.info("PAGEINFO {}", pageInfo);
		Page page = Page.builder().currentPage(pageInfo.getPageNum()).pageSize(pageInfo.getPageSize())
				.total(pageInfo.getTotal()).build();
		List<UserVO> list = CollUtil.newArrayList();
		for (PhoenixUser pu : pageInfo.getList()) {
			UserVO uv = UserVO.builder().id(pu.getId())
					.createTime(DateUtil.format(pu.getCreateTime(), DatePattern.NORM_DATETIME_PATTERN))
					.username(pu.getName()).email(pu.getEmail()).phone(pu.getPhoneNumber()).build();
			list.add(uv);
		}
		UserListVO ulv = UserListVO.builder().list(list).page(page).build();
		log.info("ULV {}", ulv);
	}

}
