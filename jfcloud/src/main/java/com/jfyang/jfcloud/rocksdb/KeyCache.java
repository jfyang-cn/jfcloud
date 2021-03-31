package com.jfyang.jfcloud.rocksdb;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyCache {

	static Logger logger = LoggerFactory.getLogger(KeyCache.class.getName());
	
	private Map<String, String> tbl = null;
	
	public KeyCache() {
		tbl = new LinkedHashMap<String, String>();
	}
	
	public void put(String key, String value) {
		tbl.put(key, value);
	}
	
	public void remove(String key) {
		tbl.remove(key);
	}
	
	public Set<String> seek(String value) {
		Set<String> ret = new HashSet<String>();
        Set<Map.Entry<String,String>> set = tbl.entrySet();
        Iterator<Map.Entry<String,String>> it = set.iterator();
        while (it.hasNext()){
            Map.Entry<String,String> e = it.next();
            if (e.getValue().indexOf(value) != -1) {
            	ret.add(e.getKey());
            }
        }
        
        return ret;
	}
	
	public Set<String> filter(String value) {
		Set<String> ret = new HashSet<String>();
        Set<Map.Entry<String,String>> set = tbl.entrySet();
        Iterator<Map.Entry<String,String>> it = set.iterator();
        while (it.hasNext()){
            Map.Entry<String,String> e = it.next();
            if (e.getValue().equals(value)) {
            	ret.add(e.getKey());
            }
        }
        
        return ret;
	}
	
	public String first(String value) {
        Set<Map.Entry<String,String>> set = tbl.entrySet();
        Iterator<Map.Entry<String,String>> it = set.iterator();
        while (it.hasNext()){
            Map.Entry<String,String> e = it.next();
            logger.info("{} {} {}", e.getKey(), e.getValue(), value);
            if (e.getValue().equals(value)) {
            	return e.getKey();
            }
        }
        
        return null;
	}
}
