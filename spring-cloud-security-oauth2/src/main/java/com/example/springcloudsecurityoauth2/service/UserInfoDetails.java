package com.example.springcloudsecurityoauth2.service;


import com.example.springcloudsecurityoauth2.entity.AppUserinfoEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collection;

public class UserInfoDetails implements UserDetails {

    private AppUserinfoEntity userUserinfo;

    public UserInfoDetails(AppUserinfoEntity userUserinfo) {
        this.userUserinfo = userUserinfo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //返回当前用户的权限  BRAC   user  role  authority
        return Arrays.asList(new SimpleGrantedAuthority("role"));
    }

    // 获取用户密码(凭证) 数据库中的密码已经加密存储了
    @Override
    public String getPassword() {
        return userUserinfo.getPassword();
    }

    // 获取用户名
    @Override
    public String getUsername() {
        return userUserinfo.getUsername();
    }

    // 判断帐号是否已经过期
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 判断帐号是否已被锁定
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 判断用户凭证是否已经过期
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 判断用户帐号是否已启用 用户状态 1正常 2冻结 3注销
    @Override
    public boolean isEnabled() {
        return userUserinfo.getUserStatus() == 1;
    }

    public Integer getUserId(){
        return userUserinfo.getId();
    }
}
