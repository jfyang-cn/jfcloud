package com.jfyang.jfcloud.common;

import lombok.Data;

@Data
public class BaseResult {
	private int code;
	private String reason;
	private int total;
	private Object record;
	
	public BaseResult(int code, String reason) {
		super();
		this.code = code;
		this.reason = reason;
		this.total = 0;
		this.record = new String("");;
	}
}
