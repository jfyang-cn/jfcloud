package com.jfyang.jfcloud.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.jfyang.jfcloud.common.BaseResult;

@RestController
@RequestMapping("/jfcloud/user")
public class UserController {

	@RequestMapping("/list")
	public BaseResult list(@RequestBody JSONObject jsonObject){
		
		return new BaseResult(0, "success");
	}
}
