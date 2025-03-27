package com.example.springcloudsecurityoauth2.controller;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

/**
 * @Author: liaozhiwei
 * @Description: 获取当前登录用户信息
 * @Date: Created in 09:24 2022/8/24
 */
@RestController
@RequestMapping("/securityOauth2")
public class SecurityOauth2Controller {


    /**
     * 根据重定向的返回地址获取授权码
     * @return
     */
    @RequestMapping("/getCodeByCallback")
    public Object getCodeByCallback() {
        return "这是一个回调方法（授权码模式：A网站提供一个链接，用户点击后就会跳转到B网站，用户跳转后，B网站会要求用户登录，然后询问是否同意给予A网站授权，用户表示同意，这时B网站就会跳回redirect_uri参数指定的网址，也就是当前这个接口，跳转时，会传回一个授权码";
    }

    /**
     * 回调方法
     * @return
     */
    @RequestMapping("/redirectUris")
    public Object redirectUris(HttpServletRequest request) {
        return "这是一个回调方法，第三方应用授权登录之后，回调到这里，我们可以进行自己系统登录业务，将用户的登录信息录入";
    }

    @Autowired
    private TokenStore tokenService;


    /**
     * 获取当前登录用户信息
     * @param token
     * @return
     */
    @RequestMapping("/getUserByTokenStore")
    public Object getUserByTokenStore(@RequestParam("access_token") String token) {
        OAuth2Authentication oAuth2Authentication =  tokenService.readAuthentication(token);
        return oAuth2Authentication.getUserAuthentication().getPrincipal();
    }

    /**
     * 获取当前登录用户信息
     * @param request
     * @return
     */
    @GetMapping("/getUserByRequest")
    public Object getUserByRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String token;
        if(header!=null){
            token = header.substring(header.indexOf("bearer") + 7);
        }else {
            token = request.getParameter("access_token");
        }
        return Jwts.parser()
                .setSigningKey("123123".getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();

    }

}
