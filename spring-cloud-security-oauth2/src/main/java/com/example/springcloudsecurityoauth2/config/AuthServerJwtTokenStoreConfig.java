package com.example.springcloudsecurityoauth2.config;

import com.example.springcloudsecurityoauth2.service.AppUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: liaozhiwei
 * @Description: 第二种方式：基于jwt.jks文件授权服务器存储第三方客户端的信息
 * @Date: Created in 18:17 2022/8/23
 */
@Configuration
@EnableAuthorizationServer
public class AuthServerJwtTokenStoreConfig extends AuthorizationServerConfigurerAdapter {


     //第二种方式：使用密码模式需要配置（使用jwt文件的方式）
    @Autowired
    @Qualifier("jwtTokenStore")
    private TokenStore jwtTokenStore;

    @Value("${host.ip}")
    private String hostIp;
    @Value("${server.port}")
    private String port;

    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Autowired
    private AppUserDetailsService userDetailService;

    @Autowired
    private AuthenticationManager authenticationManagerBean;

    @Autowired
    private AuthTokenEnhancer authTokenEnhancer;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * @Description 第三方信息的存储
     **/
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        /**
         * 第一种方式：授权码模式(安全级别最高)获取access_token
         * 第一步
         * http://127.0.0.1:8807/oauth/authorize?response_type=code&client_id=client&redirect_uri=http://www.baidu.com&scope=all
         *或者
         * http://127.0.0.1:8807/oauth/authorize?response_type=code&client_id=client
         * 第二步
         * 获取access_token
         * http://127.0.0.1:8807/oauth/token?grant_type=authorization_code&client_id=client&client_secret=client_secret&code=JQFXW7

         * 第二种方式：password模式获取access_token
         * http://127.0.0.1:8807/oauth/token?username=liaozhiwei&password=123456&grant_type=password&client_id=client&client_secret=client_secret&scope=all
         * 刷新令牌获取access_token
         * http://127.0.0.1:8807/oauth/token?grant_type=refresh_token&client_id=client&client_secret=client_secret&refresh_token=         */

        /**
         * 发起请求使用access_token进行授权获取登录用户信息
         * http://127.0.0.1:8807/securityOauth2/getUserByTokenStore?access_token=
         * http://127.0.0.1:8807/securityOauth2/getUserByAuthentication?access_token=
         * http://127.0.0.1:8807/securityOauth2/getUserByRequest?access_token=
         * */

        clients.inMemory()
                //配置client_id
                .withClient("client_id")
                //配置client-secret
                .secret(passwordEncoder.encode("client_secret"))
                //配置访问token的有效期
                .accessTokenValiditySeconds(864000)
                //配置刷新token的有效期
                .refreshTokenValiditySeconds(864000)
                //配置redirect_uri，用于授权成功后跳转,可以配置多个，例如：.redirectUris("http://localhost:8081/login","http://localhost:8082/login")
                .redirectUris("http://" + hostIp + ":" + port + "/securityOauth2/redirectUris")
                //自动授权配置
                .autoApprove(true)
                //配置申请的权限范围
                .scopes("all")
                /**
                 * 配置grant_type，表示授权类型
                 * authorization_code: 授权码
                 * password： 密码
                 * client_credentials: 客户端
                 * refresh_token: 更新令牌
                 */
                .authorizedGrantTypes("authorization_code","password","refresh_token");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        //配置JWT的内容增强器
        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> delegates = new ArrayList<>();
        delegates.add(authTokenEnhancer);
        delegates.add(jwtAccessTokenConverter);
        enhancerChain.setTokenEnhancers(delegates);

        //第二种：使用密码模式需要配置（使用jwt文件的方式）
        endpoints.authenticationManager(authenticationManagerBean) //使用密码模式需要配置
                .reuseRefreshTokens(false)  //refresh_token是否重复使用
                .userDetailsService(userDetailService) //刷新令牌授权包含对用户信息的检查
                .tokenStore(jwtTokenStore)  //配置存储令牌策略（使用jwt文件存储的方式）
                .accessTokenConverter(jwtAccessTokenConverter)
                .tokenEnhancer(enhancerChain) //配置tokenEnhancer
                .allowedTokenEndpointRequestMethods(HttpMethod.GET,HttpMethod.POST,HttpMethod.DELETE,HttpMethod.PUT); //支持GET,POST请求,DELETE请求，PUT请求

    }

    /**
     * 授权服务器安全配置
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        // 允许客户表单认证,不加的话/oauth/token无法访问
        security.allowFormAuthenticationForClients()
                // 对于CheckEndpoint控制器[框架自带的校验]的/oauth/token端点允许所有客户端发送器请求而不会被Spring-security拦截
                // 开启/oauth/token_key验证端口无权限访问
                .tokenKeyAccess("permitAll()")
                // 要访问/oauth/check_token必须设置为permitAll()，但这样所有人都可以访问了，设为isAuthenticated()又导致访问不了，这个问题暂时没找到解决方案
                // 开启/oauth/check_token验证端口认证权限访问
                .checkTokenAccess("permitAll()")
//                //第三方客户端校验token需要带入 clientId 和clientSecret来校验
                .checkTokenAccess("isAuthenticated()")
                .tokenKeyAccess("isAuthenticated()");//来获取我们的tokenKey需要带入clientId,clientSecret
        //允许客户表单认证,不加的话/oauth/token无法访问
        security.allowFormAuthenticationForClients();
    }


}

