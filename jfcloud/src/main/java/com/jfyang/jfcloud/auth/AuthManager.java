package com.jfyang.jfcloud.auth;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.jfyang.jfcloud.db.DbManager;
import com.jfyang.jfcloud.rocksdb.RocksDao;
import com.jfyang.jfcloud.rocksdb.RocksdbImpl;
import com.jfyang.jfcloud.util.CommonUtil;

@Component
@DependsOn(value = "dbManager")
@ComponentScan(basePackages = {"com.jfyang.jfcloud.auth"})
public class AuthManager {
	
	static Logger logger = LoggerFactory.getLogger(AuthManager.class.getName());
	
    @Autowired
    private DbManager dbManager;
    
    private RocksdbImpl db = null;
    
    private String authAppTbl = "authApp"; 
    private String authTokenTbl = "authToken";
    
    private RocksDao<AuthApp> authAppDao = null;
    private RocksDao<AuthToken> authTokenDao = null;
    
    @PostConstruct
    public void init() {
		logger.info("auth manager init");
    	db = dbManager.getDefaultDb();
    	authAppDao = new RocksDao<AuthApp>(AuthApp.class, db, authAppTbl);
    	authAppDao.init();
    	authTokenDao = new RocksDao<AuthToken>(AuthToken.class, db, authTokenTbl);
    	authTokenDao.init();
    	return;
    }
	
	public AuthApp createApp(String userId) {
		
		String appKey = CommonUtil.strUUID();
		String appSecret = CommonUtil.strUUID();
		
		AuthApp app = new AuthApp();
		app.setAppKey(appKey);
		app.setAppSecret(appSecret);
		app.setUserId(userId);
		app.setCreateTime(CommonUtil.currentTimeInSec());

		authAppDao.put(appKey, app);
		
		return app;
	}
	
	public AuthApp getApp(String appKey) {
		return authAppDao.get(appKey);
	}
	
	public int removeApp(String appKey) {
		return authAppDao.del(appKey);		
	}
		
	public AuthToken createToken(String appKey, int type) {
		
		long currentTime = CommonUtil.currentTimeInSec();
		long expireTime = currentTime + 7*24*60*60; // sec
		String data = appKey+","+expireTime+","+type;
		String token = DigestUtils.sha256Hex(data);
		
		AuthToken authToken = new AuthToken();		
		authToken.setAppKey(appKey);
		authToken.setCreateTime(currentTime);
		authToken.setExpireTime(expireTime);
		authToken.setToken(token);
		authToken.setType(type);
				
		authTokenDao.put(token, authToken);
		
		return authToken;
	}
	
	public AuthToken getToken(String token) {
		return authTokenDao.get(token);
	}

	public int removeToken(String token) {
		return authTokenDao.del(token);
	}
	
	public int checkToken(String token) {
		AuthToken authToken = authTokenDao.get(token);
		if (authToken == null) {
			return -1;
		}
		
		return 0;
	}
	
    public void validateToken() {
    	List<AuthToken> tokens = authTokenDao.getAll();
    	long currentTime = CommonUtil.currentTimeInSec();
    	tokens.forEach( token -> {
    		long expireTime = token.getExpireTime();
    		if (expireTime < currentTime) {
    			authTokenDao.del(token.getToken());
    		}
    	});
    }
}
