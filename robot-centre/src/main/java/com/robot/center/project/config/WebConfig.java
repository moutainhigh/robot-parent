package com.robot.center.project.config;

import com.robot.center.interceptor.LoginLogInterceptor;
import com.robot.center.interceptor.UnLoginLogInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public abstract class WebConfig implements WebMvcConfigurer {
    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        return corsConfiguration;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }

    @Bean
    public HandlerInterceptor getLoginLogInterceptor(){
        return new LoginLogInterceptor();
    }

    @Bean
    public HandlerInterceptor getUnLoginLogInterceptor(){
        return new UnLoginLogInterceptor();
    }

    //添加拦截器
    public void addInterceptors(InterceptorRegistry registry) {
        String[] path = {"/**/addRobot", "/**/deleteRobot", "/**/updateRobot", "/**/pageRobot", "/**/getRobotById", "/**/closeRobot"};
        registry.addInterceptor(getLoginLogInterceptor())
                .addPathPatterns(path);
        registry.addInterceptor(getUnLoginLogInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(path);
    }
}