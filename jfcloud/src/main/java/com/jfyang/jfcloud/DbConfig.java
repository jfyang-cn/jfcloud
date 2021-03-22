package com.jfyang.jfcloud;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.jfyang.jfcloud"})
public class DbConfig {

    @Value("${db.path}")
	private String dbPath;
    @Value("${db.name}")
    private String dbName;
    
    public String getDbName() {
    	return dbName;
    }
    
    public String getDbPath() {
    	return dbPath;
    }
}
