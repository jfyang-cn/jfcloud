package com.jfyang.jfcloud.rocksdb;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

public class RocksDao<T> implements RocksDataInterface {
	
	static Logger logger = LoggerFactory.getLogger(RocksDao.class.getName());
	
	private Class<T> entityClass;
	
	private RocksdbImpl db = null;
	private String tableName = "";

	private Map<String, KeyCache> indexTbls = null;	// <索引名称，索引表名的名称>	
	
	public RocksDao(Class<T> entityClass, RocksdbImpl db, String tableName) {
		this.db = db;
		this.tableName = tableName;
		this.entityClass = entityClass;

		if (db != null && tableName != null) {
			db.createTable(tableName);
		}
	}
	
	public void init() {
    	logger.info("rocks dao init {}", tableName);
		// 创建索引表
		indexTbls = new LinkedHashMap<String, KeyCache>();
		Field[] flds = entityClass.getDeclaredFields();
		if (flds != null && flds.length != 0) {
			for (int i=0; i<flds.length; i++){	    	
	    		Field f = flds[i];
	    		if (f.isAnnotationPresent(Index.class)) {
	    			String index = f.getName();
	    			indexTbls.put(index, new KeyCache());
	    		}	    	
			}
		}
		db.buildTableIndex(tableName, this);
	}
	
	public void buildIndex(String key, String value) {
		T elm =  JSON.parseObject(value, entityClass);
		// 更新索引表
		if (!indexTbls.isEmpty()) {
			// 添加新索引
			indexTbls.forEach((index, cache) -> {
				Field field;
				try {
					field = elm.getClass().getDeclaredField(index);
					field.setAccessible(true);
					String val;
					if (field.getType() == Integer.class) {
						val = String.valueOf(field.get(elm));
					} else {
						val = (String)(field.get(elm));
					}
					logger.info("{} {} {} {}", index, key, val, value);
					cache.put(key, val);
				} catch (Exception ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			});
		}
	}
	
	public T get(String key) {
		String val = db.getValue(tableName, key);
		if (val == null) {
			return null;
		}
		logger.info(val);
		T ret = JSON.parseObject(val, entityClass);
		return ret;
	}
	
	public int put(String key, T value) {
		
		// 更新索引表
		if (indexTbls!=null && !indexTbls.isEmpty()) {
			// 删除旧索引
			indexTbls.forEach((index, cache) -> {
				cache.remove(key);			
			});
			
			// 添加新索引
			indexTbls.forEach((index, cache) -> {
				Field field;
				try {
					field = value.getClass().getDeclaredField(index);
					field.setAccessible(true);
					String val;
					if (field.getType() == Integer.class) {
						val = String.valueOf(field.get(value));
					} else {
						val = (String)(field.get(value));
					}
					cache.put(key, val);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		}
		
		return db.putValue(tableName, key, JSON.toJSONString(value));
	}

	public int del(String key) {
		// 更新索引表
		indexTbls.forEach((index, cache) -> {
			cache.remove(key);
		});
		return db.delValue(tableName, key);		
	}

	public List<T> getAll() {
		Map<String,String> data = db.getTableData(tableName);
		List<T> ret = new ArrayList<T>();		
		data.forEach((key,val) -> {
//			logger.info(key, val);
			T e =  JSON.parseObject(val, entityClass);
			ret.add(e);
		});
		
		return ret;
	}

	public List<T> seek(String keyname) {
		Map<String,String> data = db.seek(tableName, keyname);
		List<T> ret = new ArrayList<T>();		
		data.forEach((key,val) -> {
			T e =  JSON.parseObject(val, entityClass);
			ret.add(e);
		});
		
		return ret;
	}
	
	public Pair<String, T> first(String keyname) {
		Pair<String,String> data = db.first(tableName, keyname);
		if (data == null)
			return null;
		
		return new Pair<String, T>(data.getLeft(), JSON.parseObject(data.getRight(), entityClass));		
	}
	
	public Pair<String, T> first(String field, String value) {
		KeyCache cache =  indexTbls.get(field);
		if (cache != null) {
			String key = cache.first(value);
			if (key != null) {
				Pair<String,String> data = db.first(tableName, key);
				if (data != null)
					return new Pair<String, T>(data.getLeft(), JSON.parseObject(data.getRight(), entityClass));
			}
		}
		
		return null;
	}
	
	public List<T> filter(String field, String value) {
		List<T> ret = new ArrayList<T>();		
		List<String> objs = null;
		KeyCache cache =  indexTbls.get(field);
		if (cache != null) {
			Set<String> keys = cache.filter(value);
			objs = db.getValues(tableName, keys);
		}
		
		if (objs != null) {
			objs.forEach(val -> {
				T e =  JSON.parseObject(val, entityClass);
				ret.add(e);
			});
		}
		
		return ret;
	}
}
