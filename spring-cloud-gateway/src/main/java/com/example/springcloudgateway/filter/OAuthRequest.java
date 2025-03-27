package com.example.springcloudgateway.filter;

import java.util.Map;
import java.util.Set;

/**
 * @Author: liaozhiwei
 * @Description: TODO
 * @Date: Created in 11:20 2022/8/25
 */

public class OAuthRequest {
    /**
     * 请求参数
     */
    private Map<String, String> parameters;
    /**
     * 请求头
     */
    private Map<String, String> headers;
    /**
     * 请求方式：POST、GET、PUT、DELETE
     */
    private String method;
    /**
     * 请求全路径
     */
    private String requestURL;
    /**
     * 请求路径
     */
    private String requestURI;
    /**
     * 请求地址参数
     */
    private String queryString;
    /**
     * 请求来源地址
     */
    private String remoteHost;

    public OAuthRequest() {
    }

    public OAuthRequest(Map<String, String> parameters, Map<String, String> headers, String method, String requestURL, String requestURI, String queryString, String remoteHost) {
        this.parameters = parameters;
        this.headers = headers;
        this.method = method;
        this.requestURL = requestURL;
        this.requestURI = requestURI;
        this.queryString = queryString;
        this.remoteHost = remoteHost;
    }


    /**
     * 获取请求参数
     *
     * @param name 参数名
     * @return 请求参数
     */
    public String getParameter(String name) {
        return parameters.get(name);
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public OAuthRequest setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
        return this;
    }

    /**
     * 获取请求头
     *
     * @param name 参数名
     * @return 请求头信息
     */
    public String getHeader(String name) {
        return headers.get(name);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public OAuthRequest setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public OAuthRequest setMethod(String method) {
        this.method = method;
        return this;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public OAuthRequest setRequestURL(String requestURL) {
        this.requestURL = requestURL;
        return this;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public OAuthRequest setRequestURI(String requestURI) {
        this.requestURI = requestURI;
        return this;
    }

    public String getQueryString() {
        return queryString;
    }

    public OAuthRequest setQueryString(String queryString) {
        this.queryString = queryString;
        return this;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public OAuthRequest setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
        return this;
    }

    public OAuthRequest narrowScope(Set<String> scope) {
        this.parameters.put("scope", String.join(",", scope.toArray(new String[]{})));
        return this;
    }
}


