package com.jfyang.jfcloud.jobpool;

import com.jfyang.jfcloud.rocksdb.Index;

import lombok.Builder;
import lombok.Data;

@Data
public class Job {
	
	@Index
	String id;			// 任务的唯一标识
	@Index
	String ref;			// 参考ID，调用者将通过该ID回溯自己的数据
	@Index
	Integer status;		// 任务的状态，0错误，1初始，2执行中，3完成，4超时
	
	long createTime;	// 任务创建的时间
	long takeTime;		// 任务被worker执行的时间
	long finishTime;	// worker完成任务的时间
	String content;		// 任务的具体内容，JSON格式定义
	String worker;		// worker的名字，比如某个节点
	String feedback;	// worker完成任务的结果反馈
	long period;		// 任务的刷新周期，周期执行的任务使用，0表示只执行一次。单位秒
	long expired;		// 任务过期的时间，以创建时间为基准，计算经历的时间。单位秒，0表示永不过期
	Integer tryAgain;	// 失败时重试的次数 
}
