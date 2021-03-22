package com.jfyang.jfcloud.auth;

import lombok.Data;

@Data
public class AuthToken {

	private String token;
	private String appKey;
	private int type;
	private long createTime;
	private long expireTime;
}
