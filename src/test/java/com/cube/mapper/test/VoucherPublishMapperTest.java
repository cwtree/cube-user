package com.cube.mapper.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

import com.cube.mapper.VoucherPublishMapper;
import com.cube.pojo.doo.VoucherPublish;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

/**
 * 
 * 
 * @author phoenix
 * @date 2021年3月2日
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class VoucherPublishMapperTest {

	@Resource
	private VoucherPublishMapper voucherPublishMapper;

	@Resource(name = "smallPool")
	private ThreadPoolTaskExecutor tpte;

	/**
	 * merchant字段使用
	 */
	private static String SPECIAL = "CUBETEST";

	private static int COUNTER = 1;

	private static int LOCK_LOOP_QUIT_COUNTER = 5;
	private static int THREAD_NUM = 10;

	private void insert() {
		log.info("插入 {} 条数据", COUNTER);
		for (int i = 0; i < COUNTER; i++) {
			VoucherPublish vp = VoucherPublish.builder().merchant(SPECIAL + i).userId(1L).voucherAmount(1000L).build();
			int temp = voucherPublishMapper.insertSelective(vp);
			assertEquals(temp, 1);
			// 保存后，id有数据
		}
	}

	@Test
	public void test() {
		log.info("test");
		insert();
		Example example = new Example(VoucherPublish.class);
		Criteria c = example.createCriteria();
		c.andLike("merchant", "%" + SPECIAL + "%");
		List<VoucherPublish> list = voucherPublishMapper.selectByExample(example);
		VoucherPublish vp = list.get(0);
		/**
		 * 并发更新 UPDATE voucher_publish SET voucher_amount = ?,version = ?,update_time =
		 * ? WHERE id = ? AND version = ?
		 */
		for (int i = 0; i < THREAD_NUM; i++) {
			tpte.execute(() -> {
				update(vp);
			});
		}
		try {
			Thread.sleep(5000L);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("线程休眠异常", e);
		}
	}

	private void update(VoucherPublish vp) {
		int counter = 0;
		while (counter < LOCK_LOOP_QUIT_COUNTER) {
			vp.setVoucherAmount(vp.getVoucherAmount() - RandomUtil.randomInt(10, 500));
			// 设置null的目的是让update语句不要更新这些字段
			vp.setCreateTime(null);
			vp.setMerchant(null);
			vp.setUserId(null);
			int res = voucherPublishMapper.updateByPrimaryKeySelective(vp);
			counter++;
			if (res > 0) {
				// 更新成功
				break;
			}
			Example example = new Example(VoucherPublish.class);
			Criteria c = example.createCriteria();
			c.andLike("merchant", "%" + SPECIAL + "%");
			List<VoucherPublish> list = voucherPublishMapper.selectByExample(example);
			vp = list.get(0);
		}
		if (counter >= LOCK_LOOP_QUIT_COUNTER) {
			log.error("数据更新失败");
		}
	}

	@After
	public void clearData() {
		Example example = new Example(VoucherPublish.class);
		Criteria c = example.createCriteria();
		c.andLike("merchant", "%" + SPECIAL + "%");
		int temp = voucherPublishMapper.deleteByExample(example);
		log.info("清空测试数据 {}", temp);
	}

}
