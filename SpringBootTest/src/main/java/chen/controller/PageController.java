package chen.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import chen.config.WiselySettings;

/**
 * 测试
 * 
 */
@Controller
@RequestMapping("/page")
public class PageController {

	protected static Logger logger = LoggerFactory.getLogger(PersonController.class);

	// 从 application.properties 中读取配置，如取不到默认值为Hello Shanhy
	@Value("${application.hello:Hello Angel}")
	private String hello;
	@Value("${login_user}")
	private String login_user;
	@Value("${login_password}")
	private String login_password;

	@RequestMapping("/helloJsp")
	public String helloJsp(Map<String, Object> map) {
		logger.info("HelloController.helloJsp().hello={}", hello);
		map.put("hello", hello);
		return "helloJsp";
	}

	@ResponseBody
	@RequestMapping("/login")
	public boolean login(String user, String password) {

		if (login_user.contains(user + ",") && login_password.equals(password)) {
			return true;
		}
		return false;
	}
}