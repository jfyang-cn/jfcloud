package com.jfyang.jfcloud.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.jfyang.jfcloud.common.BaseResult;
import com.jfyang.jfcloud.db.DbManager;
import com.jfyang.jfcloud.rocksdb.RocksdbImpl;

@RestController
@RequestMapping("/rocksdb")
@DependsOn(value = "dbManager")
public class RocksdbController {

	static Logger logger = LoggerFactory.getLogger(RocksdbController.class.getName());
	
    @Autowired
    private DbManager dbManager;
	
	@RequestMapping("/error")
	public BaseResult error(){
		
		return new BaseResult(1, "auth error");
	}
	
	@RequestMapping("/{dbName}/create")
	public BaseResult createDb(@PathVariable("dbName") String dbName) {
		logger.info("dbName: "+dbName);
		int ret = dbManager.createDb(dbName);
		return new BaseResult(ret, "");
	}
	
	@RequestMapping("/{dbName}/drop")
	public BaseResult dropDb(@PathVariable("dbName") String dbName) {
		logger.info("dbName: "+dbName);
		int ret = dbManager.dropDb(dbName);		
		return new BaseResult(ret, "");
	}
	
	@RequestMapping("/{dbName}/tables")
	public List<String> tables(@PathVariable("dbName") String dbName) {		
		logger.info("dbName: "+dbName);		
		RocksdbImpl db = dbManager.getDb(dbName);
		List<String> tableList = db.getTables();		
		return tableList;
	}
	
	@RequestMapping("/{dbName}/table/{tableName}/create")
	public BaseResult createTable(@PathVariable("dbName") String dbName,
			@PathVariable("tableName") String tableName
			) {
		logger.info("dbName: "+dbName+", tableName: "+tableName);
		RocksdbImpl db = dbManager.getDb(dbName);
		int ret = db.createTable(tableName);
		return new BaseResult(ret, "");
	}
	
	@RequestMapping("/{dbName}/table/{tableName}/drop")
	public BaseResult dropTable(@PathVariable("dbName") String dbName,
			@PathVariable("tableName") String tableName
			) {
		logger.info("dbName: "+dbName+", tableName: "+tableName);
		RocksdbImpl db = dbManager.getDb(dbName);
		int ret = db.dropTable(tableName);
		return new BaseResult(ret, "");
	}
	
	@RequestMapping("/{dbName}/table/{tableName}/show")
	public Map<String,String> tableShow(@PathVariable("dbName") String dbName,
			@PathVariable("tableName") String tableName
			) {
		logger.info("dbName: "+dbName+", tableName: "+tableName);
		RocksdbImpl db = dbManager.getDb(dbName);
		Map<String,String> data = db.getTableData(tableName);
		return data;
	}
	
	@RequestMapping("/{dbName}/table/{tableName}/seek")
	public Map<String,String> tableShow(@PathVariable("dbName") String dbName,
			@PathVariable("tableName") String tableName,
			@RequestParam(value="keyname", required=true) String keyname
			) {
		logger.info("dbName: {}, tableName: {}, keyname: {}", dbName, tableName, keyname);
		if (keyname == null)
			return null;
		
		RocksdbImpl db = dbManager.getDb(dbName);
		Map<String,String> data = db.seek(tableName, keyname);
		return data;
	}
	
	@RequestMapping(value="/{dbName}/table/{tableName}/get")
	public String getValue(@PathVariable("dbName") String dbName,
			@PathVariable("tableName") String tableName,
			@RequestBody JSONObject jsonObject
			) {
		String key = jsonObject.getString("key");
		logger.info("dbName: {}, tableName: {}, key: {}", dbName, tableName, key);
		RocksdbImpl db = dbManager.getDb(dbName);
		String val = db.getValue(tableName, key);
		return val;
	}
	
	@RequestMapping(value="/{dbName}/table/{tableName}/put")
	public int putValue(@PathVariable("dbName") String dbName,
			@PathVariable("tableName") String tableName,
			@RequestBody JSONObject jsonObject
			) {
		String key = jsonObject.getString("key");
		String value = jsonObject.getString("value");
		logger.info("dbName: {}, tableName: {}, key: {}, value: {}", 
				dbName, tableName, key, value);
		RocksdbImpl db = dbManager.getDb(dbName);
		db.putValue(tableName, key, value);
		return 0;
	}
}
