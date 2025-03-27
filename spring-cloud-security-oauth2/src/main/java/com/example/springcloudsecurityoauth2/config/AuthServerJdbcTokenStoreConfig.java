//package com.example.springcloudsecurityoauth2demo.config;
//
//import com.example.springcloudsecurityoauth2demo.service.AppUserDetailsService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
//import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
//import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
//import org.springframework.security.oauth2.provider.ClientDetailsService;
//import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
//import org.springframework.security.oauth2.provider.token.TokenEnhancer;
//import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
//import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
//import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
//import javax.sql.DataSource;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @Author: liaozhiwei
// * @Description: 第一种方式：基于DB模式配置授权服务器存储第三方客户端的信息
// * @Date: Created in 18:17 2022/8/23
// */
//@Configuration
//@EnableAuthorizationServer
//public class AuthServerJdbcTokenStoreConfig extends AuthorizationServerConfigurerAdapter {
//
//      //第一种方式：使用密码模式需要配置（使用jdbc的方式）
//    @Autowired
//    private DataSource dataSource;
//    @Bean
//    public JdbcTokenStore jdbcTokenStore(){
//        return new JdbcTokenStore(dataSource);
//    }
//    @Bean
//    public ClientDetailsService clientDetailsService(){
//        return new JdbcClientDetailsService(dataSource);
//    }
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
//    /**
//     * @Description 第三方信息的存储
//     **/
//    @Override
//    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//        /**
//         * 第一步
//         *授权码模式(安全级别最高)
//         *http://localhost:8807/oauth/authorize?response_type=code&client_id=client&redirect_uri=http://www.baidu.com&scope=all
//         *或者
//         *http://localhost:8807/oauth/authorize?response_type=code&client_id=client
//         *password模式
//         *http://localhost:8807/oauth/token?username=liaozhiwei&password=123456&grant_type=password&client_id=client&client_secret=client_secret&scope=all
//         *刷新令牌
//         *http://localhost:8080/oauth/token?grant_type=refresh_token&client_id=client&client_secret=client_secret&refresh_token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJsaWFvemhpd2VpIiwic2NvcGUiOlsiYWxsIl0sImF0aSI6IjJiYjg5NDY5LWIyNmYtNGQwNC05YTZjLTJjYmZkYjIzMDgyNyIsImV4cCI6MTY2MjE3ODkzMiwiYXV0aG9yaXRpZXMiOlsiYWRtaW4iXSwianRpIjoiNzBmOTc0NWItNGZmOS00ZTY5LThiOWMtNzY5ZGNkODc5NTcwIiwiY2xpZW50X2lkIjoiY2xpZW50In0.Uy6s5uIT2h_vv6DezssqzA1d7iNpqyfNReiAygmYrapPuc1Beyetoxbf3_zduD8BhZkKva7Qna_L9lFQKZuzzSx25RLgG07YzDZnuUPkCVYisxZ4bhmOuJYndKzrZmZBqmK2P9MQwLhasEgcpZoR5RJurV15fZOO5IvOI6xvgM0XkqalnEwYWf5e5JYLEEBqQqTpkEoP6wH3SSBsRFuH10l6qKqUXFd_nO37hO1p-d2uX-qMBTGPZ57xiaz97x5FLGxh2dbskmxyTnf-jAiTHlRdrfvIHDh312uW4iyENZpg8HEg3OjUHYc-7OY4U9UrHQx0YQbJ01SuKLCFEIx-aA
//         */
//        /**
//         * 第二步
//         * 获取access_token
//         * http://localhost:8807/oauth/token?grant_type=authorization_code&client_id=client&client_secret=client_secret&code=07Sn4f
//         */
//        // 第三方信息的存储   基于jdbc
//        clients.withClientDetails(clientDetailsService());
//    }
//
//    @Override
//    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
//        //配置JWT的内容增强器
//        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
//        List<TokenEnhancer> delegates = new ArrayList<>();
//        delegates.add(authTokenEnhancer);
//        delegates.add(jwtAccessTokenConverter);
//        enhancerChain.setTokenEnhancers(delegates);
//
//        //第一种：使用密码模式需要配置（使用jdbc的方式）
//        endpoints.authenticationManager(authenticationManagerBean)
//                .reuseRefreshTokens(false)  //refresh_token是否重复使用
//                .userDetailsService(userDetailService) //刷新令牌授权包含对用户信息的检查
//                .tokenStore(new JdbcTokenStore(dataSource))  //指定token存储策略是jwt,存储到mysql
//                .accessTokenConverter(jwtAccessTokenConverter)
//                .tokenEnhancer(enhancerChain) //配置tokenEnhancer
//                .allowedTokenEndpointRequestMethods(HttpMethod.GET,HttpMethod.POST,HttpMethod.DELETE,HttpMethod.PUT); //支持GET,POST请求,DELETE请求，PUT请求
//    }
//
//    /**
//     * 授权服务器安全配置
//     * @param security
//     * @throws Exception
//     */
//    @Override
//    public void configure(AuthorizationServerSecurityConfigurer security) {
//        //第三方客户端校验token需要带入 clientId 和clientSecret来校验
//        security.checkTokenAccess("isAuthenticated()")
//                .tokenKeyAccess("isAuthenticated()");//来获取我们的tokenKey需要带入clientId,clientSecret
//        //允许表单认证
//        security.allowFormAuthenticationForClients();
//    }
//}
//
