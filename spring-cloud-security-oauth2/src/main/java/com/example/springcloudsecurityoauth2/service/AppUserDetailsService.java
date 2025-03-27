package com.example.springcloudsecurityoauth2.service;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.example.springcloudsecurityoauth2.constants.UserConstants;
import com.example.springcloudsecurityoauth2.entity.AppUserinfoEntity;
import com.example.springcloudsecurityoauth2.entity.base.BaseResponse;
import com.example.springcloudsecurityoauth2.fegin.UserInfoFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;


@Service
@Slf4j
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        //第一种方式：写死直接返回
//        String password = passwordEncoder.encode("123456");
//        log.info(password);
//        return new User("liaozhiwei",password, AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));//AuthorityUtils.commaSeparatedStringToAuthorityList("admin")用来为用户分配权限
        // 第二种方式：  查数据库获取用户信息   rpc调用
        // 加载用户信息
        if (StringUtils.isEmpty(username)) {
            log.warn("用户登陆用户名为空:{}", username);
            throw new UsernameNotFoundException("用户名不能为空");
        }
        AppUserinfoEntity userUserinfo = getByUserName(username);
        if (null == userUserinfo) {
            log.warn("根据用户名没有查询到对应的用户信息:{}", username);
        }
//        log.info("根据用户名:{}获取用户登陆信息:{}", username, userUserinfo.toString());
        // 用户信息的封装 implements UserDetails
        UserInfoDetails memberDetails = new UserInfoDetails(userUserinfo);
        return memberDetails;
    }

    @Autowired
    private UserInfoFeignService userInfoFeignService;


    public AppUserinfoEntity getByUserName(String username) {
        AppUserinfoEntity appUserinfoEntity = new AppUserinfoEntity();
        // fegin获取用户信息
        BaseResponse resultData = userInfoFeignService.getUserByUserName(username);
        log.info("用户信息：{}",resultData);
        LinkedHashMap data = (LinkedHashMap) resultData.getData();
        appUserinfoEntity.setId((Integer) data.get("id"));
        appUserinfoEntity.setUsername((String) data.get("username"));
        appUserinfoEntity.setPassword((String) data.get("password"));
        appUserinfoEntity.setUserStatus((Integer) data.get("userStatus"));
        return appUserinfoEntity;
    }
}


