package com.example.springcloudgateway.filter;

/**
 * @Author: liaozhiwei
 * @Description: TODO
 * @Date: Created in 11:21 2022/8/25
 */


public class OAuthRequestContainer {
    private static ThreadLocal<OAuthRequest> local = new InheritableThreadLocal<>();

    private OAuthRequestContainer() {
    }

    public static void set(OAuthRequest request) {
        local.set(request);
    }

    public static OAuthRequest get() {
        return local.get();
    }

    public static void remove() {
        local.remove();
    }

    public static void rewriteOAuthRequestContainer(ThreadLocal<OAuthRequest> request) {
        local = request;
    }
}
