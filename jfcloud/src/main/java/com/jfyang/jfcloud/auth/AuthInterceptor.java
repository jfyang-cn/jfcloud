package com.jfyang.jfcloud.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.ModelAndView;
//
//public class AuthInterceptor implements HandlerInterceptor {
//	static Logger logger = LoggerFactory.getLogger(AuthInterceptor.class.getName());
//
//	@Override
//	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//		logger.info( "check token" );
//        String token = request.getHeader("token");
//        if (token == null || token.isEmpty()) {
//        	logger.info( "token is empty..." );
//        	response.setStatus(HttpStatus.UNAUTHORIZED.value());
//            return false;
//        }
//	    return true;
//	}
//	
//	@Override
//	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//	
//	}
//	
//	@Override
//	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//	
//	}
//	
//}
