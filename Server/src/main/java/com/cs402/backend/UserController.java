package com.cs402.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	private static final String greeting = "Hello,%s!";
	private final AtomicLong counter = new AtomicLong();
	
	@GetMapping("/all")
	public RespondJson<Iterable<User>> getAllUser() {
		return RespondJson.out(RespondCodeEnum.SUCCESS,userRepository.findAll());
	}
	
	@GetMapping("/{uid}")
	public Object test(@PathVariable Long uid) {
		try{
			User user = uidSearch(uid).get(0);
			String username = user.getUsername();
			RespondJson<User> ret = RespondJson.out(RespondCodeEnum.SUCCESS,user);
			ret.setMsg(String.format(greeting,username));
			return ret;
		}catch (IndexOutOfBoundsException e){
			e.printStackTrace();
			return RespondJson.out(RespondCodeEnum.FAIL_USER_NOT_FOUND);
		}
		catch (Exception e){
			e.printStackTrace();
			RespondCodeEnum res = RespondCodeEnum.FAIL;
			res.setMgs(e.toString());
			return RespondJson.out(res);
		}
	}
	
	
	@GetMapping("/getUserByID")
	public Object findUserById(@RequestParam Long uid) {
		List<User> list = uidSearch(uid);
		if (list.isEmpty()) {
			return RespondJson.out(RespondCodeEnum.FAIL_USER_NOT_FOUND);
		}else {
			return RespondJson.out(RespondCodeEnum.SUCCESS,list.get(0));
		}
	}
	
	public List<User> uidSearch(Long uid) {
		List<User> list = userRepository.findUserById(uid);
		log.debug("[/getUserbyID] " + list.toString());
		return list;
	}
	

	@GetMapping("/login")
	public Object login(@RequestParam String username, @RequestParam String password) {
		List<User> list = userRepository.login(username, password);
		log.debug(list.toString());
		if (!list.isEmpty()) {
			User userinfo = list.get(0);
			return RespondJson.out(RespondCodeEnum.SUCCESS,userinfo);
		}
		return RespondJson.out(RespondCodeEnum.FAIL);
	}
	
	//test only, please use /register
	@RequestMapping(path = "/add")
	public String addNewUser(@RequestParam String username, @RequestParam String password, @RequestParam String category) {
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user.setCategory(category);
		userRepository.save(user);
		return "[" + Utility.getServerTime() + "] : User [" + username + "] added!";
	}
	
	@GetMapping(value = "/userlist")
	public Object getUserlist() {
		return RespondJson.out(RespondCodeEnum.SUCCESS,userRepository.getUserlist(0));
	}
	

	//true: not used
	@ResponseBody
	@GetMapping("/checkUsernameNotUsed")
	public Boolean checkUsernameNotUsed(@RequestParam String username) {
		List<User> list = userRepository.checkIfExist(username);
		if (list.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@RequestMapping(path = "/register")
	@ResponseBody
	public String register(@RequestParam String username, @RequestParam String password, @RequestParam String category) {
		if (!checkUsernameNotUsed(username)) {
			return "[" + Utility.getServerTime() + "] " + "Username is already used!";
		}
		else if (!Utility.validateCategory(category)) {
			return "[" + Utility.getServerTime() + "] " + "Wrong User Category!";
		}
		else {
			User user = new User();
			user.setUsername(username);
			user.setPassword(password);
			user.setCategory(category);
			userRepository.save(user);
			return "[" + Utility.getServerTime() + "] : User [" + username + "] added!";
		}
	}
	
}
