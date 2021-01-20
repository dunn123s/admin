package com.admin.config;

import com.admin.project.service.GcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class adminSecurityConfig extends SecurityConfig {

    @Autowired
    private GcUserService gcUserService;

    /**
     * 获取当前登录在用户信息
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> gcUserService.loadUserByUsername(username);
    }
}
