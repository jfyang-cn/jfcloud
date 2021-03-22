package com.jfyang.jfcloud.util;

import java.util.Date;
import java.util.UUID;

public class CommonUtil {
	
	public static String strUUID() {
        return UUID.randomUUID().toString().replace("-", "");
	}
	
	public static long currentTimeInSec() {
		return new Date().getTime()/1000;
	}
}
