package com.jfyang.jfcloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.jfyang.jfcloud.auth.GatewayTokenFilter;
import com.jfyang.jfcloud.auth.TokenInterceptorFilter;

@Configuration
public class ApplicationConfig {

	static Logger logger = LoggerFactory.getLogger(ApplicationConfig.class.getName());

    @Bean  
    public FilterRegistrationBean<TokenInterceptorFilter>  filterRegistrationBean() {
    	logger.info("application filter register");
        FilterRegistrationBean<TokenInterceptorFilter> registrationBean = new FilterRegistrationBean<TokenInterceptorFilter>();  
        TokenInterceptorFilter tokenAuthorFilter = new TokenInterceptorFilter();  
        registrationBean.setFilter(tokenAuthorFilter);
        registrationBean.addUrlPatterns("/jfcloud/user/*");
        registrationBean.setOrder(-1);
        return registrationBean;  
    }

    @Bean
    public GatewayTokenFilter tokenFilter(){
    	logger.info("gateway token filter register");
    	return new GatewayTokenFilter();
    }
    
//    @Bean
//    public RouterFunction<ServerResponse> route(AuthHandler authHandler) {
//        return RouterFunctions
//        		.route(null, null)
//          .filter(new AuthFilter());
//    }
}
