package chen.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import chen.domain.Person;
import chen.service.PersonService;

@RestController
@RequestMapping("/person")
public class PersonController {

	protected static Logger logger = LoggerFactory.getLogger(PersonController.class);

	@Autowired
	private PersonService personService;

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() {
		return "login";
	}

	@ResponseBody
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Boolean login(String user, String password, HttpServletRequest request) {
		List<Person> persons = personService.findByNameAndPassword(user, password);
		if (persons.size() > 0) {
			HttpSession session = request.getSession();
			session.invalidate();
			session = request.getSession(true);
			session.setAttribute("user", user);
			return true;
		}
		return false;
	}

	@RequestMapping("/logout")
	public void logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		session.removeAttribute("user");
		/** 注销session */
		session.invalidate();
		try {
			response.sendRedirect(request.getContextPath() + "/login.jsp");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 测试person保存
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/savePerson")
	public String savePerson() throws Exception {
		Person p = new Person();
		p.setId((long) 4);
		p.setName("chen");
		p.setAge(28);
		p.setAddress("aaaa");
		personService.save(p);

		return "success";
	}

	@ResponseBody
	@RequestMapping("/find/name/{name}/password/{password}")
	public boolean find(@PathVariable("name") String name, @PathVariable("password") String password) {

		logger.info("查询条件：名字{}", name);
		List<Person> persons = personService.findByNameAndPassword(name, password);

		if (persons.size() > 0) {
			return true;
		}
		return false;
	}

	@RequestMapping("/find/department/{id}")
	public List<Person> findByDepartment_IdAndName(HttpServletRequest request, @PathVariable("id") Long id) {

		logger.info("查询条件：部门id{}", id);

		return personService.findByDepartmentId(id);
	}

}
