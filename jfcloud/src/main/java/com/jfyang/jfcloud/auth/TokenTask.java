package com.jfyang.jfcloud.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@DependsOn(value = "authManager")
public class TokenTask {

	static Logger logger = LoggerFactory.getLogger(TokenTask.class.getName());
	
    @Autowired
    private AuthManager authManager;    
        
	 /**
     * 每隔3*60秒执行, 单位：ms
     */
    @Scheduled(fixedRate = 3*60*1000)
    public void validateToken() {
    	authManager.validateToken();
    }
}
