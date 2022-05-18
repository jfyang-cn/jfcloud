package com.jfyang.jfcloud.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.jfyang.jfcloud.common.BaseRespT;

@RestController
@RequestMapping("/jfcloud/user")
public class UserController {

	@RequestMapping("/list")
	public BaseRespT<?> list(@RequestBody JSONObject jsonObject){
		
		return new BaseRespT<Object>(0, "success");
	}
}
