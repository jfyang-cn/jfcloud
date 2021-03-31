package com.jfyang.jfcloud.jobpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jfyang.jfcloud.jobpool.JobManager;

@Component
@DependsOn(value = "jobManager")
public class JobErrorProcess {

	static Logger logger = LoggerFactory.getLogger(JobErrorProcess.class.getName());
	
    @Autowired
    private JobManager jobManager;

	 /**
     * 每隔5*60秒执行, 单位：ms
     */
    @Scheduled(fixedRate = 5*60*1000)
    public void process() {
    	//jobManager.errorProcess();
    }
}
