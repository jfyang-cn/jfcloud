package com.jfyang.jfcloud.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

public class GatewayTokenFilter implements GlobalFilter, Ordered {
	static Logger logger = LoggerFactory.getLogger(GatewayTokenFilter.class.getName());

	@Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		logger.info( "check token" );
        Object token = exchange.getRequest().getHeaders().get("token");
        if (token == null || token.toString().isEmpty()) {
        	logger.info( "token is empty..." );
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
