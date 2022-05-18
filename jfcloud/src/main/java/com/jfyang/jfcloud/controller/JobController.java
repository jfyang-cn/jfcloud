package com.jfyang.jfcloud.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.jfyang.jfcloud.common.BasePageRespT;
import com.jfyang.jfcloud.common.BaseRespT;
import com.jfyang.jfcloud.jobpool.Job;
import com.jfyang.jfcloud.jobpool.JobManager;

@RestController
@RequestMapping("/jfcloud/jobpool")
@DependsOn(value = "jobManager")
public class JobController {

    @Autowired
    private JobManager jobManager;
    
	@RequestMapping("/error")
	public BaseRespT<String> error(@RequestParam(value="error", required=false) Integer error){
		if (error == null) {
			return new BaseRespT<String>(-1, "unknown error");
		}
		
		return new BaseRespT<String>(error, "error");
	}	
    
	@RequestMapping("/{type}/list")
	public BasePageRespT<Job> list(@PathVariable("type") String type){
		List<Job> jobs = jobManager.get(type).list();
		
		return new BasePageRespT<Job>(0, "success", jobs.size(), jobs); 
	}
	
	@RequestMapping("/{type}/put")
	public BaseRespT<String> put(@PathVariable("type") String type, @RequestBody JSONObject jsonObject){
		String ref = jsonObject.getString("ref");
		String content = jsonObject.getString("content");
		int ret = jobManager.get(type).put(type, ref, content);		
		return new BaseRespT<String>(ret, "");
	}
	
	@RequestMapping("/{type}/take")
	public BaseRespT<Job> take(@PathVariable("type") String type, @RequestBody JSONObject jsonObject){
		String worker = jsonObject.getString("worker");
		Job job = jobManager.get(type).take(worker);
		
		BaseRespT<Job> ret = new BaseRespT<Job>(-1, "no queued job");
		
		if (job == null) {
			return ret;
		}
		
		ret.setCode(0);
		ret.setReason("success");		
		ret.setRecord(job);
		
		return ret;
	}
	
	@RequestMapping("/{type}/get")
	public BaseRespT<Job> get(@PathVariable("type") String type, @RequestBody JSONObject jsonObject){
		String id = jsonObject.getString("id");
		Job job = jobManager.get(type).get(id);
		
		BaseRespT<Job> ret = new BaseRespT<Job>(-1, "job does not exist");
		
		if (job == null) {
			return ret;
		}
		
		ret.setCode(0);
		ret.setReason("success");
		ret.setRecord(job);
		
		return ret;
	}
	
	@RequestMapping("/{type}/finish")
	public Integer finish(@PathVariable("type") String type, @RequestBody JSONObject jsonObject){
		String id = jsonObject.getString("id");
		String feedback = jsonObject.getString("feedback");
		return jobManager.get(type).finish(id, feedback);		
	}
	
	@RequestMapping("/{type}/error")
	public Integer error(@PathVariable("type") String type, @RequestBody JSONObject jsonObject){
		String id = jsonObject.getString("id");
		return jobManager.get(type).error(id);		
	}
}
