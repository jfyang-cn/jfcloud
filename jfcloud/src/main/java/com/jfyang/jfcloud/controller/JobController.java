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
import com.jfyang.jfcloud.common.BaseResponse;
import com.jfyang.jfcloud.common.BaseResult;
import com.jfyang.jfcloud.jobpool.Job;
import com.jfyang.jfcloud.jobpool.JobManager;

@RestController
@RequestMapping("/jfcloud/jobpool")
@DependsOn(value = "jobManager")
public class JobController {

    @Autowired
    private JobManager jobManager;
    
	@RequestMapping("/error")
	public BaseResult error(@RequestParam(value="error", required=false) Integer error){
		if (error == null) {
			return new BaseResult(-1, "unknown error");
		}
		
		return new BaseResult(error, "error");
	}	
    
	@RequestMapping("/{type}/list")
	public List<Job> list(@PathVariable("type") String type){
		
		return jobManager.get(type).list();
	}
	
	@RequestMapping("/{type}/put")
	public BaseResult put(@PathVariable("type") String type, @RequestBody JSONObject jsonObject){
		String ref = jsonObject.getString("ref");
		String content = jsonObject.getString("content");
		int ret = jobManager.get(type).put(type, ref, content);		
		return new BaseResult(ret, "");
	}
	
	@RequestMapping("/{type}/take")
	public BaseResult take(@PathVariable("type") String type, @RequestBody JSONObject jsonObject){
		String worker = jsonObject.getString("worker");
		Job job = jobManager.get(type).take(worker);
		
		BaseResult ret = new BaseResult(-1, "no queued job");
		
		if (job == null) {
			return ret;
		}
		
		ret.setCode(0);
		ret.setReason("success");
		ret.setTotal(1);
		ret.setRecord(job);
		
		return ret;
	}
	
	@RequestMapping("/{type}/get")
	public BaseResult get(@PathVariable("type") String type, @RequestBody JSONObject jsonObject){
		String id = jsonObject.getString("id");
		Job job = jobManager.get(type).get(id);
		
		BaseResult ret = new BaseResult(-1, "job does not exist");
		
		if (job == null) {
			return ret;
		}
		
		ret.setCode(0);
		ret.setReason("success");
		ret.setTotal(1);
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
