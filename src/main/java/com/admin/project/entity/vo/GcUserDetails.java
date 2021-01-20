package com.admin.project.entity.vo;

import com.admin.project.entity.GcUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

/**
 * 会员详情封装
 */
public class GcUserDetails implements UserDetails {
    private GcUser gcUser;

    public GcUserDetails(GcUser gcUser) {
        this.gcUser = gcUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //返回当前用户的权限
        return Arrays.asList(new SimpleGrantedAuthority("TEST"));
    }

    @Override
    public String getPassword() {
        return gcUser.getPassword();
    }

    @Override
    public String getUsername() {
        return gcUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return gcUser.getStatus()==1;
    }

    public GcUser getGcUser() {
        return gcUser;
    }
}
