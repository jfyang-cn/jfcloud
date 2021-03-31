package com.jfyang.jfcloud.common;

public class BaseResponse<T> {
	
	private int code;
	private String reason;
	private int total;
	private T record;
	
	public BaseResponse(int code, String reason, int total, T record) {
		this.code = code;
		this.reason = reason;
		this.total = total;
		this.record = record;
	}
}
