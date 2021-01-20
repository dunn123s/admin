package com.admin.common.base;

import com.admin.project.entity.GcUser;
import com.admin.project.entity.vo.GcUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 服务层实现 基础类
 */
public class BaseServiceImpl {

    /**
     * 获取当前登录的会员
     */
    public GcUser getCurrentGcUser() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        Authentication auth = ctx.getAuthentication();
        GcUserDetails gcUserDetails = (GcUserDetails) auth.getPrincipal();
        return gcUserDetails.getGcUser();
    }

    /**
     * 获取当前登录会员的id
     */
    public Integer getUserId() {
        Integer id = getCurrentGcUser().getId();
        return id;
    }

    /**
     * 获取当前登录的会员名称
     */
    public String getUsername() {
        String username = getCurrentGcUser().getUsername();
        return username;
    }
}
