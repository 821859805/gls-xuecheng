package com.gls.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author 郭林赛
 * @version 1.0
 * @description 跨域过滤器配置类
 * @createDate 2024/6/27 11:13
 **/

@Configuration
public class GlobalCorsConfig {
    @Bean
    public CorsFilter corsFilter(){
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");           //允许白名单域名跨域调用
        config.setAllowCredentials(true);       //允许跨域发送cookie
        config.addAllowedHeader("*");           //放行全部原始头信息
        config.addAllowedMethod("*");           //允许所有请求方法跨域调用
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",config);
        return new CorsFilter(source);
    }
}
