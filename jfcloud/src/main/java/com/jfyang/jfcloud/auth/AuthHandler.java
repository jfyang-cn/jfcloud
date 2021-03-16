package com.jfyang.jfcloud.auth;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

//@Component
//public class AuthHandler {
//	public Mono<ServerResponse> getName(ServerRequest request) {
//        Mono<String> name = Mono.just(request.pathVariable("/jfcloud/user/list"));
//        return ok().body(name, String.class);
//    }
//}
