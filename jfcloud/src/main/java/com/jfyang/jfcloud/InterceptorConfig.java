package com.jfyang.jfcloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import org.springframework.web.method.support.HandlerMethodArgumentResolver;
//
//import com.jfyang.jfcloud.auth.AuthInterceptor;

//@Configuration
//@ComponentScan("com.jfyang.jcloud.auth")
//public class InterceptorConfig implements WebMvcConfigurer {
//	static Logger logger = LoggerFactory.getLogger(InterceptorConfig.class.getName());
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//    	logger.info("auth interceptor register");
//        registry.addInterceptor(new AuthInterceptor()).addPathPatterns("/jfcloud/user/*");
//    }
//    
////    @Override
////    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
////    	resolvers.add(userContextResolver);
////    }
//
//}
