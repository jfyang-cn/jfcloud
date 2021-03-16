package com.jfyang.jfcloud.common;

import com.alibaba.fastjson.JSONObject;

public class BaseResponse {
	
    public static JSONObject Create(int code, String reason) {
        JSONObject obj = new JSONObject();
        obj.put("code", code);
        obj.put("reason", reason);
        return obj;
    }
}
