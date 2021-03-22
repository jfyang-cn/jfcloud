package com.jfyang.jfcloud.db;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.jfyang.jfcloud.DbConfig;
import com.jfyang.jfcloud.rocksdb.RocksdbImpl;

@Component
@DependsOn(value = "dbConfig")
@ComponentScan(basePackages = {"com.jfyang.jfcloud.db"})
public class DbManager {
	
	static Logger logger = LoggerFactory.getLogger(DbManager.class.getName());
	
    @Autowired
    private DbConfig dbConfig;

    private HashMap<String, RocksdbImpl> dbList;
       
    @PostConstruct
	public void init() {
		logger.info("db manager init");
    	dbList = new HashMap<String, RocksdbImpl>();		
		logger.info("db config: {}, {}", dbConfig.getDbPath(), dbConfig.getDbName());
		createDb(dbConfig.getDbName());
		return;
	}
    
	public RocksdbImpl getDefaultDb() {
		return getDb(dbConfig.getDbName());
	}		
	
	public int createDb(String dbName) {
		
		if(dbList.containsKey(dbName)) {
			return -1;
		}
		
		RocksdbImpl rocksdb = new RocksdbImpl();
		String dbPath = StringUtils.appendIfMissing(dbConfig.getDbPath(), "/") 
				+ dbName;
		rocksdb.initDb(dbPath);
		dbList.put(dbName, rocksdb);
		return 0;
	}
	
	public int dropDb(String dbName) {
		RocksdbImpl rocksdb = getDb(dbName);
		rocksdb.closeDb();
		
		// remove local data
		try {
			String dbPath = StringUtils.appendIfMissing(dbConfig.getDbPath(), "/") + dbName;
			Path path = Paths.get(dbPath);
	        if (Files.exists(path)){
	        	logger.info("rocksdb {} exists, will be remove");
	        }
			Files.deleteIfExists(path);
		} catch (IOException e) {
			StringWriter error = new StringWriter();
			e.printStackTrace(new PrintWriter(error));
			logger.error(error.toString());
		}
		
		return 0;
	}
		
	public RocksdbImpl getDb(String dbName) {
		return dbList.get(dbName);
	}    
}
