package com.yunxi.user.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author zhiweiLiao
 * @Description
 * @Date create in 2022/10/13 0013 17:43
 */
public class WeChatConstants {

    public static final String JSCODE2SESSION = "https://api.weixin.qq.com/sns/jscode2session";
    public static final String APPID = "appid";
    public static final String SECRET = "secret";
    public static final String JS_CODE = "js_code";
    public static final String GRANT_TYPE = "grant_type";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String CLIENT_CREDENTIAL = "client_credential";
    public static final String AUTHORIZATION_CODE = "authorization_code";
    public static final String TOKEN = "https://api.weixin.qq.com/cgi-bin/token";
    public static final String GET_USER_PHONENUMBER = "https://api.weixin.qq.com/wxa/business/getuserphonenumber";

}
