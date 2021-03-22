package com.jfyang.jfcloud.auth;

import lombok.Data;

@Data
public class AuthApp {
	
	private String appKey;
	private String appSecret;
	private String userId;
	private long createTime;

}
