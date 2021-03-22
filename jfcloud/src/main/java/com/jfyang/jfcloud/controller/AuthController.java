package com.jfyang.jfcloud.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.jfyang.jfcloud.auth.AuthApp;
import com.jfyang.jfcloud.auth.AuthManager;
import com.jfyang.jfcloud.auth.AuthToken;
import com.jfyang.jfcloud.common.BaseResult;

@RestController
@RequestMapping("/jfcloud/auth")
@DependsOn(value = "authManager")
public class AuthController {

	static Logger logger = LoggerFactory.getLogger(AuthController.class.getName());

    @Autowired
    private AuthManager authManager;
    
	@RequestMapping("/error")
	public BaseResult error(@RequestParam(value="error", required=false) Integer error){
		if (error == null) {
			return new BaseResult(-1, "unknown error");
		}
		
		return new BaseResult(error, "error");
	}
	
	@RequestMapping("/app/create")
	public AuthApp createApp(@RequestBody JSONObject jsonObject) {
		String userId = jsonObject.getString("userId");
		logger.info("userId: {}", userId);
		AuthApp ret = authManager.createApp(userId);
		return ret;
	}
	
	@RequestMapping("/token/get")
	public AuthToken getToken(@RequestBody JSONObject jsonObject) {
		String appKey = jsonObject.getString("appKey");
		String appSecret = jsonObject.getString("appSecret");
		logger.info("appKey: {}", appKey);
		AuthToken ret = authManager.createToken(appKey, 1);
		return ret;
	}
}
