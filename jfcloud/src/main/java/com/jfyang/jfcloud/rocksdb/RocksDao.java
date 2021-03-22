package com.jfyang.jfcloud.rocksdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

public class RocksDao<T> {
	
	private Class<T> entityClass;
	
	private RocksdbImpl db = null;
	private String tableName = "";
	
	public RocksDao(Class<T> entityClass, RocksdbImpl db, String tableName) {
		this.db = db;
		this.tableName = tableName;
		this.entityClass = entityClass; 
		db.createTable(tableName);
	}
	
	public T get(String key) {
		String val = db.getValue(tableName, key);
		if (val == null) {
			return null;
		}
		T ret = JSON.parseObject(val, entityClass);
		return ret;
	}
	
	public int put(String key, String value) {
		return db.putValue(tableName, key, value);
	}
	
	public int del(String key) {
		return db.delValue(tableName, key);		
	}
	
	public List<T> getAll() {
		Map<String,String> data = db.getTableData(tableName);
		List<T> ret = new ArrayList<T>();		
		data.forEach((key,val) -> {
			T e =  JSON.parseObject(val, entityClass);
			ret.add(e);
		});
		
		return ret;
	}
}
