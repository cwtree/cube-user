package com.cube.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cube.aop.auth.Auth;
import com.cube.common.Constants;
import com.cube.common.GlobalVar;
import com.cube.common.RegexConts;
import com.cube.config.MyConfig;
import com.cube.pojo.MyResp;
import com.cube.pojo.Resp;
import com.cube.pojo.doo.PhoenixUser;
import com.cube.pojo.dto.UserListQueryDTO;
import com.cube.pojo.dto.UserLoginDTO;
import com.cube.pojo.vo.PublicKeyVO;
import com.cube.pojo.vo.UserListVO;
import com.cube.service.UserService;
import com.cube.session.MyCookie;
import com.cube.session.SessionCache;
import com.cube.session.UserSession;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.GifCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.digest.DigestUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 
 * @author phoenix
 * @date 2021年2月10日
 */
@RestController
@Api("用户操作")
@RequestMapping("/user")
@Slf4j
public class UserController {

	@Resource
	private MyConfig myConfig;

	@Resource
	private UserService userService;

	@Resource
	private ApplicationEventPublisher publisher;

	@Resource
	private SessionCache sessionCache;

	/**
	 * 获取图形验证码 如果一个系统有多种类型的图形验证码，比如登录、申请、注册， 可以共用这一个接口，多个实现类
	 * 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param request
	 * @param response
	 */
	@ApiOperation("生成图形验证码")
	@GetMapping("/captcha")
	public void getCaptcha(HttpServletRequest request, HttpServletResponse response) {
		if (log.isInfoEnabled()) {
			log.info("生成图形验证码");
		}
		MyCookie cm = new MyCookie(request, response);
		String cookieValue = cm.getCookieValue(Constants.CAPTCHA_CODE_COOKIE_KEY);
		if (StrUtil.isBlank(cookieValue)) {
			// 请求验证码时，cookie没有，则生成cookie
			cookieValue = request.getSession().getId();
			cm.setCookie(Constants.CAPTCHA_CODE_COOKIE_KEY, cookieValue,
					URLUtil.toURI(myConfig.getSystemPath()).getHost());
		}
		try {
			GifCaptcha captcha = CaptchaUtil.createGifCaptcha(80, 30);
			captcha.setGenerator(new RandomGenerator(Constants.CAPTCHA_BASE, 5));
			sessionCache.cacheCaptcha(cookieValue, captcha.getCode());
			captcha.write(response.getOutputStream());
			if (log.isInfoEnabled()) {
				log.info("验证码内容为 {}", captcha.getCode());
			}
		} catch (Exception e) {
			log.error("验证生成错误", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取密码加密的公钥信息
	 * 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @return
	 */
	@ApiOperation("获取公钥")
	@GetMapping("/publicKey")
	public MyResp publicKey() {
		return MyResp.builder().code(Resp.SUCCESS.getCode()).msg(Resp.SUCCESS.getMsg()).data(
				PublicKeyVO.builder().modulus(GlobalVar.MODULUS).publicExponent(GlobalVar.PUBLICK_EXPONENT).build())
				.build();
	}

	/**
	 * 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param userLoginDTO
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation("用户登录")
	@PostMapping("/login")
	public MyResp login(@RequestBody @Validated UserLoginDTO userLoginDTO, HttpServletRequest request,
			HttpServletResponse response) {
		if (log.isInfoEnabled()) {
			log.info("进入用户登录接口，入参 {}", userLoginDTO);
		}
		MyCookie cm = new MyCookie(request, response);
		String cookieValue = cm.getCookieValue(Constants.CAPTCHA_CODE_COOKIE_KEY);
		if (StrUtil.isBlank(cookieValue)) {
			// 登录校验时仍然得不到验证码请求的cookie，则提示开启浏览器cookie
			log.warn("登录校验读取不到验证码的cookie");
			return MyResp.result(Resp.CAPTCHA_CODE_ERROR);
		}
		// 验证码校验
		String captcha = sessionCache.getCacheCaptcha(cookieValue);
		if (!StrUtil.equals(userLoginDTO.getCaptcha(), captcha)) {
			// 需要清除验证码缓存?重新请求验证码会覆盖session中的验证码数据，不需要显示清空
			return MyResp.result(Resp.CAPTCHA_CODE_ERROR);
		}
		try {
			userLoginDTO.setPwd(GlobalVar.RSA.decryptStr(userLoginDTO.getPwd(), KeyType.PrivateKey));
			if (!ReUtil.isMatch(RegexConts.PWD, userLoginDTO.getPwd())) {
				log.error("用户明文密码正则不匹配 {}", userLoginDTO.getPwd());
				return MyResp.result(Resp.USERNAME_PWD_ERROR);
			}
		} catch (Exception e) {
			log.error("密码解密异常 {} -- {}", userLoginDTO.getPwd(), e);
			return MyResp.result(Resp.USERNAME_PWD_ERROR);
		}
		PhoenixUser pu = userService.getUserByName(userLoginDTO.getUsername());
		if (ObjectUtil.isNull(pu)) {
			return MyResp.result(Resp.USERNAME_PWD_ERROR);
		}
		// 提交密码哈希后比较
		String hashPwd = DigestUtil.sha256Hex(userLoginDTO.getPwd() + pu.getSalt());
		if (!StrUtil.equals(pu.getPassword(), hashPwd)) {
			return MyResp.result(Resp.USERNAME_PWD_ERROR);
		}
		// TODO 增加登录失败后锁定逻辑
		// 登录成功后，生成usersession
		UserSession us = UserSession.builder().sessionId(request.getSession().getId()).username(pu.getName())
				.phone(pu.getPhoneNumber()).build();
		sessionCache.cacheLoginInfo(us, cm);
		// 登录成功后清除验证码缓存
		sessionCache.deleteCacheCaptcha(cm, cookieValue);
		// TODO 登录成功后发布一个事件publishevent
		return MyResp.builder().code(Resp.SUCCESS.getCode()).msg(Resp.SUCCESS.getMsg()).data(us).build();
	}

	/**
	 * 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation("用户登出")
	@GetMapping("/logout")
	@Auth
	public MyResp logout(HttpServletRequest request, HttpServletResponse response) {
		MyCookie cm = new MyCookie(request, response);
		sessionCache.deleteLoginInfo(cm);
		if (log.isInfoEnabled()) {
			log.info("用户登出");
		}
		return MyResp.builder().code(Resp.SUCCESS.getCode()).msg(Resp.SUCCESS.getMsg()).build();
	}

	/**
	 * 用户列表查询
	 * 
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param dto
	 * @return
	 */
	@ApiOperation("用户列表查询")
	@PostMapping("/list")
	@Auth
	public MyResp list(@RequestBody @Validated UserListQueryDTO dto) {
		if (log.isInfoEnabled()) {
			log.info("进入用户列表查询接口，入参 {}", dto);
		}
		UserListVO ulv = userService.queryUserList(dto.getUsername(), dto.getPhone(), dto.getCurrentPage());
		return MyResp.builder().code(Resp.SUCCESS.getCode()).msg(Resp.SUCCESS.getMsg()).data(ulv).build();
	}

}
