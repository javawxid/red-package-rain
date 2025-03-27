//package com.example.springcloudsecurityoauth2demo.config;
//
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//import com.example.springcloudsecurityoauth2demo.service.AppUserDetailsService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
//import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
//import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
//import org.springframework.security.oauth2.provider.token.TokenEnhancer;
//import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
//import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @Author: liaozhiwei
// * @Description: 第三种方式：基于Redis模式配置授权服务器存储第三方客户端的信息
// * @Date: Created in 18:17 2022/8/23
// */
//@Configuration
//@EnableAuthorizationServer
//public class AuthServerRedisTokenStoreConfig extends AuthorizationServerConfigurerAdapter {
//
//    //使用密码模式需要配置（使用redis的方式）
//    @Autowired
//    @Qualifier("redisTokenStore")
//    private TokenStore redisTokenStore;
//
//    @Autowired
//    private JwtAccessTokenConverter jwtAccessTokenConverter;
//
//    @Autowired
//    private AppUserDetailsService userDetailService;
//
//    @Autowired
//    private AuthenticationManager authenticationManagerBean;
//
//    @Autowired
//    private AuthTokenEnhancer authTokenEnhancer;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    /**
//     * @Description 第三方信息的存储
//     **/
//    @Override
//    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//        /**
//         *授权码模式
//         *http://localhost:8807/oauth/authorize?response_type=code&client_id=client&redirect_uri=http://www.baidu.com&scope=all
//         *或者
//         *http://localhost:8807/oauth/authorize?response_type=code&client_id=client
//         *password模式
//         *http://localhost:8807/oauth/token?username=liaozhiwei&password=123456&grant_type=password&client_id=client&client_secret=client-secret&scope=all
//         *刷新令牌
//         *http://localhost:8080/oauth/token?grant_type=refresh_token&client_id=client&client_secret=client-secret&refresh_token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJsaWFvemhpd2VpIiwic2NvcGUiOlsiYWxsIl0sImF0aSI6IjJiYjg5NDY5LWIyNmYtNGQwNC05YTZjLTJjYmZkYjIzMDgyNyIsImV4cCI6MTY2MjE3ODkzMiwiYXV0aG9yaXRpZXMiOlsiYWRtaW4iXSwianRpIjoiNzBmOTc0NWItNGZmOS00ZTY5LThiOWMtNzY5ZGNkODc5NTcwIiwiY2xpZW50X2lkIjoiY2xpZW50In0.Uy6s5uIT2h_vv6DezssqzA1d7iNpqyfNReiAygmYrapPuc1Beyetoxbf3_zduD8BhZkKva7Qna_L9lFQKZuzzSx25RLgG07YzDZnuUPkCVYisxZ4bhmOuJYndKzrZmZBqmK2P9MQwLhasEgcpZoR5RJurV15fZOO5IvOI6xvgM0XkqalnEwYWf5e5JYLEEBqQqTpkEoP6wH3SSBsRFuH10l6qKqUXFd_nO37hO1p-d2uX-qMBTGPZ57xiaz97x5FLGxh2dbskmxyTnf-jAiTHlRdrfvIHDh312uW4iyENZpg8HEg3OjUHYc-7OY4U9UrHQx0YQbJ01SuKLCFEIx-aA
//         */
//
//        clients.inMemory()
//                //配置client_id
//                .withClient("client")
//                //配置client-secret
//                .secret(passwordEncoder.encode("client-secret"))
//                //配置访问token的有效期
//                .accessTokenValiditySeconds(3600)
//                //配置刷新token的有效期
//                .refreshTokenValiditySeconds(864000)
//                //配置redirect_uri，用于授权成功后跳转,可以配置多个，例如：.redirectUris("http://localhost:8081/login","http://localhost:8082/login")
//                .redirectUris("http://www.baidu.com")
//                //配置申请的权限范围
//                .scopes("all")
//                //自动授权配置
//                .autoApprove(true)
//                /**
//                 * 配置grant_type，表示授权类型
//                 * authorization_code: 授权码
//                 * password： 密码
//                 * client_credentials: 客户端
//                 * refresh_token: 更新令牌
//                 * implicit：简化模式
//                 */
//                .authorizedGrantTypes("authorization_code","password","refresh_token");
//    }
//
//    @Override
//    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//        //配置JWT的内容增强器
//        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
//        List<TokenEnhancer> delegates = new ArrayList<>();
//        delegates.add(authTokenEnhancer);
//        delegates.add(jwtAccessTokenConverter);
//        enhancerChain.setTokenEnhancers(delegates);
//
//        //使用密码模式需要配置（使用redis的方式）
//        endpoints.authenticationManager(authenticationManagerBean) //使用密码模式需要配置
//                .reuseRefreshTokens(false)  //refresh_token是否重复使用
//                .userDetailsService(userDetailService) //刷新令牌授权包含对用户信息的检查
//                .tokenStore(redisTokenStore)  //指定token存储到redis
//                .accessTokenConverter(jwtAccessTokenConverter)
//                .allowedTokenEndpointRequestMethods(HttpMethod.GET,HttpMethod.POST,HttpMethod.DELETE,HttpMethod.PUT); //支持GET,POST请求,DELETE请求，PUT请求
//    }
//
//    /**
//     * 授权服务器安全配置
//     * @param security
//     * @throws Exception
//     */
//    @Override
//    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
//        //第三方客户端校验token需要带入 clientId 和clientSecret来校验
//        security.checkTokenAccess("isAuthenticated()")
//                .tokenKeyAccess("isAuthenticated()");//来获取我们的tokenKey需要带入clientId,clientSecret
//        //允许表单认证
//        security.allowFormAuthenticationForClients();
//    }
//
//
//}
//
