package com.example.springcloudgateway.filter;

/**
 * @Author: liaozhiwei
 * @Description: TODO
 * @Date: Created in 11:21 2022/8/25
 */

import com.alibaba.nacos.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

/**
 * @desc: 请求解析工厂类
 */
public abstract class OAuthRequestFactory {
    private static final Logger logger = LoggerFactory.getLogger(OAuthRequestFactory.class);

    /**
     * 构造请求实体
     *
     * @param httpRequest SpringMvc下传入HttpServletRequest
     * @return {@link OAuthRequest} 请求实体
     */
    public abstract OAuthRequest createRequest(Object httpRequest);

    /**
     * 构造封装请求实体
     *
     * @param headers     请求头信息
     * @param parameters  请求参数
     * @param remoteHost  请求来源IP
     * @param method      请求方式：POST、GET...
     * @param requestURL  请求全路径
     * @param requestURI  请求路径
     * @param queryString 请求路径参数
     */
    protected OAuthRequest buildRequest(Map<String, String> parameters, Map<String, String> headers, String method, String requestURL, String requestURI, String queryString, String remoteHost) {
        final String token = headers.get("HEADER_TOKEN.toLowerCase()");
        final String clientToken = headers.get("HEADER_TOKEN.toLowerCase()");
        // 判断是否包含认证OAuthAuthentication字段
        if (StringUtils.isNotEmpty(token)) {
            // TODO 解析令牌
            //final OAuthAuthentication authentication = resourceServerTokenServices.loadAuthentication(token);
            if (StringUtils.isNotEmpty(clientToken)) {
                // TODO 解析请求Client令牌
            }
            return new OAuthRequest(parameters, headers, method, requestURL, requestURI, queryString, remoteHost);
        }
        return new OAuthRequest(parameters, headers, method, requestURL, requestURI, queryString, remoteHost);
    }
}


