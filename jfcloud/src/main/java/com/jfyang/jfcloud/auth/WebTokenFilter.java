package com.jfyang.jfcloud.auth;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.UriComponentsBuilder;

import reactor.core.publisher.Mono;

// web filter
@Component
@DependsOn(value = "authManager")
@Order(value = 2)
public class WebTokenFilter implements WebFilter {

	static Logger logger = LoggerFactory.getLogger(WebTokenFilter.class.getName());
	
    @Autowired
	AuthManager authManager;
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		String token = request.getHeaders().getFirst("token");
		URI reuri = request.getURI();
		
		logger.info("web token filter runs");
		
		// 以下url不需要认证
		if (reuri.getPath().equals("/jfcloud/auth/token/get") 
				|| reuri.getPath().startsWith("/jfcloud/auth/error")) {
			return chain.filter(exchange);
		}
		
		int ret = 0;
		if (token != null) {
			ret = authManager.checkToken(token);
			if (ret == 0)
				return chain.filter(exchange); 
		}
		
		URI uri = UriComponentsBuilder.fromUriString("/jfcloud/auth/error?error="+ret).build().toUri();
		ServerHttpRequest authErrorReq = request.mutate().uri(uri).build();
        //erverWebExchange.mutate类似，构建一个新的ServerWebExchange
        ServerWebExchange authErrorExchange = exchange.mutate().request(authErrorReq).build();
        return chain.filter(authErrorExchange);
	}
}
