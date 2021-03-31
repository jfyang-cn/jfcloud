package com.jfyang.jfcloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ServletComponentScan("com.jfyang.jfcloud.auth")
@ComponentScan({"com.jfyang.jfcloud","com.jfyang.jfcloud.auth","com.jfyang.jfcloud.jobpool"})
@EnableScheduling
public class JFCloudApplication {

	static Logger logger = LoggerFactory.getLogger(JFCloudApplication.class.getName());

	public static void main(String[] args) {
    	SpringApplication.run(JFCloudApplication.class, args);
    }	
	
	public void destroy() throws Exception {
		logger.info("application destroy");
	}
}
