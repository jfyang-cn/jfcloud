package com.jfyang.jfcloud.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jfyang.jfcloud.common.BaseResult;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@RequestMapping("/error")
	public BaseResult error(){
		
		return new BaseResult(1, "auth error");
	}
}
