package com.jfyang.jfcloud.jobpool;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfyang.jfcloud.rocksdb.Pair;
import com.jfyang.jfcloud.rocksdb.RocksDao;
import com.jfyang.jfcloud.rocksdb.RocksdbImpl;
import com.jfyang.jfcloud.util.CommonUtil;

public class JobPool {
	
	static Logger logger = LoggerFactory.getLogger(JobPool.class.getName());

    private RocksdbImpl db = null;
    private String tbl = "jobPool";
    private RocksDao<Job> dao = null;
    private String type = "";

    final static public int ERROR = 0;
    final static public int INIT = 1;
    final static public int ASSIGN = 2;
    final static public int DONE = 3;
    final static public int TIMEOUT = 4;
    
    public JobPool(RocksdbImpl db, String type) {
    	this.type = type;
    	this.db = db;
    	dao = new RocksDao<Job>(Job.class, db, tbl+"-"+type);
    	dao.init();
    }    
	
	public int put(String type, String ref, String content) {
    	Job job = new Job();
    	job.setId(CommonUtil.strUUID());
    	job.setCreateTime(CommonUtil.currentTimeInSec());
    	job.setRef(ref);
    	job.setContent(content);
    	job.setStatus(INIT);
    	dao.put(job.getId(), job);
    	return 0;
    }
    
    public Job take(String worker) {
    	String keyname = String.valueOf(INIT);
    	// 取第一个INIT状态的
    	Pair<String, Job> ret = dao.first("status", keyname);
    	if (ret == null) {
    		logger.info("no waiting job");
    		return null;
    	}

    	Job job = ret.getRight();
    	job.setTakeTime(CommonUtil.currentTimeInSec());
    	job.setWorker(worker);
    	job.setStatus(ASSIGN);
    	dao.put(job.getId(), job);
    	
    	return job;
    }
    
    public Job get(String id) {
    	return dao.get(id);
    }
    
    public int remove(String id) {
    	return dao.del(id);
    }
    
    public int finish(String id, String feedback) {
    	Job job = dao.get(id);
    	job.setFinishTime(CommonUtil.currentTimeInSec());
    	job.setFeedback(feedback);
    	job.setStatus(DONE);
    	dao.put(job.getId(), job);
    	return 0;
    }
    
    public int error(String id) {
    	Job job = dao.get(id);
    	job.setFinishTime(CommonUtil.currentTimeInSec());
    	job.setStatus(ERROR);
    	dao.put(job.getId(), job);
    	return 0;
    }
    
    public List<Job> list() {
    	return dao.getAll();
    }
    
    public void errorProcess() {
    	
    }
    
    public void periodProcess() {
    	
    }
}
