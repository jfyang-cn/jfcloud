package com.jfyang.jfcloud.jobpool;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.jfyang.jfcloud.db.DbManager;
import com.jfyang.jfcloud.rocksdb.RocksdbImpl;

@Component
@DependsOn(value = "dbManager")
@ComponentScan(basePackages = {"com.jfyang.jfcloud.jobpool"})
public class JobManager {
	
	static Logger logger = LoggerFactory.getLogger(JobManager.class.getName());
	
	@Autowired
    private DbManager dbManager;
    
    private RocksdbImpl db = null;
    
    private Map<String, JobPool> pools = null;

    @PostConstruct
    public void init() {
		logger.info("job manager init");
    	db = dbManager.getDefaultDb();
    	pools = new LinkedHashMap<String, JobPool>();
    	return;
    }
    
    public JobPool get(String type) {
    	if (pools.containsKey(type)) {
    		return pools.get(type);
    	}
    	
    	JobPool pool = new JobPool(db, type);
    	pools.put(type,  pool);
    	return pool;
    }

}
