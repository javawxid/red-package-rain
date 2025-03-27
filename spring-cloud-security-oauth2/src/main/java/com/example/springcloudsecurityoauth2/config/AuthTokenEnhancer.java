package com.example.springcloudsecurityoauth2.config;

import com.example.springcloudsecurityoauth2.service.UserInfoDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

public class AuthTokenEnhancer  implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        UserInfoDetails userinfoDetails = (UserInfoDetails) authentication.getPrincipal();
        final Map<String, Object> additionalInfo = new HashMap<>();
        final Map<String, Object> retMap = new HashMap<>();
        //todo 这里暴露userId到Jwt的令牌中,后期可以根据自己的业务需要 进行添加字段
        additionalInfo.put("username",userinfoDetails.getUsername());
        additionalInfo.put("password",userinfoDetails.getPassword());
        additionalInfo.put("userId",userinfoDetails.getUserId());
        retMap.put("additionalInfo",additionalInfo);
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(retMap);
        return accessToken;
    }
}

