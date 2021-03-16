package com.jfyang.jfcloud.auth;

import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Component
@Order(value = 2)
public class AuthFilter implements WebFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		String token = request.getHeaders().getFirst("token");		
		if (token == null) {
			ServerHttpRequest authErrorReq = request.mutate().path("/auth/error").build();
            //erverWebExchange.mutate类似，构建一个新的ServerWebExchange
            ServerWebExchange authErrorExchange = exchange.mutate().request(authErrorReq).build();
            return chain.filter(authErrorExchange);
        }
	        
		return chain.filter(exchange);
	}

	

}
