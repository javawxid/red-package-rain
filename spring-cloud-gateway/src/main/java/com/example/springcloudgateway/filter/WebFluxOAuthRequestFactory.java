package com.example.springcloudgateway.filter;

/**
 * @Author: liaozhiwei
 * @Description: TODO
 * @Date: Created in 11:21 2022/8/25
 */

import com.alibaba.nacos.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import java.net.URI;
import java.util.*;


public class WebFluxOAuthRequestFactory extends OAuthRequestFactory {
    private static final Logger logger = LoggerFactory.getLogger(WebFluxOAuthRequestFactory.class);


    /**
     * 构造请求实体
     *
     * @param httpRequest SpringMvc下传入HttpServletRequest
     * @return {@link OAuthRequest} 请求实体
     */
    @Override
    public OAuthRequest createRequest(Object httpRequest) {
        ServerHttpRequest request = (ServerHttpRequest) httpRequest;
        final String sourceIp = analysisSourceIp(request);
        final URI uri = request.getURI();
        final String url = uri.getHost() + ":" + uri.getPort() + uri.getPath() + "?" + uri.getQuery();
        final Map<String, String> headersMap = getHeadersMap(request);
        return this.buildRequest(null, headersMap, request.getMethodValue().toUpperCase(), url, uri.getPath(), uri.getQuery(), sourceIp);
    }

    /**
     * 获取客户端真实IP
     */
    protected String analysisSourceIp(ServerHttpRequest request) {
        String ip = null;
        //X-Forwarded-For：Squid 服务代理
        String ipAddresses = request.getHeaders().getFirst("X-Forwarded-For");
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {        //Proxy-Client-IP：apache 服务代理
            ipAddresses = request.getHeaders().getFirst("Proxy-Client-IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {        //WL-Proxy-Client-IP：weblogic 服务代理
            ipAddresses = request.getHeaders().getFirst("WL-Proxy-Client-IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {        //HTTP_CLIENT_IP：有些代理服务器
            ipAddresses = request.getHeaders().getFirst("HTTP_CLIENT_IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {        //X-Real-IP：nginx服务代理
            ipAddresses = request.getHeaders().getFirst("X-Real-IP");
        }    //有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
        if (ipAddresses != null && ipAddresses.length() != 0) {
            ip = ipAddresses.split(",")[0];
        }    //还是不能获取到，最后再通过request.getRemoteAddr();获取
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ip = Objects.requireNonNull(request.getRemoteAddress()).getHostString();
        }
        return ip;
    }

    /**
     * 获取所有Header信息
     */
    private Map<String, String> getHeadersMap(ServerHttpRequest request) {
        final HashMap<String, String> headerMap = new HashMap<>();
        for (String key : request.getHeaders().keySet()) {
            final List<String> stringList = request.getHeaders().get(key);
            headerMap.put(key, stringList != null && !stringList.isEmpty() ? StringUtils.join(Arrays.asList(stringList.toArray()), ",") : null);
        }
        return headerMap;
    }
}
