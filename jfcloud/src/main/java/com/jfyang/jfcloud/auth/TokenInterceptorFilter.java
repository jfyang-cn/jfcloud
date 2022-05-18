package com.jfyang.jfcloud.auth;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alibaba.fastjson.JSON;
import com.jfyang.jfcloud.common.BaseRespT;

import lombok.extern.slf4j.Slf4j;

//@WebFilter(urlPatterns = {"/jfcloud/user/*"},filterName = "TokenInterceptorFilter")
//@Order(value = 1)
public class TokenInterceptorFilter implements Filter {

	static Logger logger = LoggerFactory.getLogger(TokenInterceptorFilter.class.getName());
	
	@Override
	public void destroy() {

	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		logger.info("token filter");
		
		//设置允许跨域的配置
		//这里填写允许进行跨域的主机IP（正式上线时可以动态配置具体允许的域名和IP）
		resp.setHeader("Access-Control-Allow-Origin", "*");
		//允许的访问方法
		resp.setHeader("Access-Control-Allow-Methods","POST, GET, PUT, OPTIONS, DELETE, PATCH");
		//Access-Control-Max-Age 用于 CORS 相关配置的缓存
		resp.setHeader("Access-Control-Max-Age", "3600");
		resp.setHeader("Access-Control-Allow-Headers","token,Origin, X-Requested-With, Content-Type, Accept");
				
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json; charset=utf-8");

		String method = req.getMethod();
		if (method.equals("OPTIONS")) {
			resp.setStatus(HttpServletResponse.SC_OK);
			return;
		}
		
	    // Extract the token from the request
		String token = req.getHeader("token");
		if (token != null && token.startsWith("Bearer ")) {
            String authToken = token.substring(7);
            if (authToken.equals("123")) {
    			logger.info("token filter过滤ok!");
    			chain.doFilter(req, resp);
    			return;
            } 
		}
		
		// fail		
		PrintWriter writer = null;
		OutputStreamWriter osw = null;
		try {
			osw = new OutputStreamWriter(resp.getOutputStream(),
					"UTF-8");
			writer = new PrintWriter(osw, true);
			String jsonStr = JSON.toJSONString(new BaseRespT<String>(1, "auth fail"));
			writer.write(jsonStr);
			writer.flush();
			writer.close();
			osw.close();
		} catch (UnsupportedEncodingException e) {
			logger.error("过滤器返回信息失败:" + e.getMessage(), e);
		} catch (IOException e) {
			logger.error("过滤器返回信息失败:" + e.getMessage(), e);
		} finally {
			if (null != writer) {
				writer.close();
			}
			if (null != osw) {
				osw.close();
			}
		}		
	}
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

}
