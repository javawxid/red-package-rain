package com.example.springcloudsecurityoauth2.config;

//import com.example.springcloudsecurityoauth2demo.filter.JWTAuthenticationEntryPoint;
//import com.example.springcloudsecurityoauth2demo.filter.JWTAuthenticationFilter;
//import com.example.springcloudsecurityoauth2demo.filter.JWTAuthorizationFilter;
import com.example.springcloudsecurityoauth2.service.AppUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * @Author: liaozhiwei
 * @Description: 配置SpringSecurity，“将Spring Security与Spring Gateway一起使用时出现无法访问javax.servlet.Filter”错误”，
 *  * 把Spring Gateway和Spring Security放在一起，因为我想保护我的网关。但是在实现了以下扩展WebSecurityConfigurerAdapter的类之后，
 *  * 项目抛出java:无法访问javax.servlet.Filter
 *  * 从Spring Cloud Gateway文档中：Spring Cloud Gateway需要Spring Boot和Spring Webflux提供的Netty运行时。
 *  * 它不能在传统的Servlet容器中工作，也不能在构建为WAR时工作。扩展WebSecurityConfigurerAdapter是为了基于servlet的应用程序
 * @Date: Created in 18:11 2022/8/23
 */
@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AppUserDetailsService userDetailService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        // oauth2 密码模式需要拿到这个bean
        return super.authenticationManagerBean();
    }

    /**
     * @Description 密码模式
     * @MethodReturnType org.springframework.security.crypto.password.PasswordEncoder
     * @Author zhiwei Liao
     * @Date 2021/8/17 15:46
     **/
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //第一种方式
        http.formLogin().permitAll()
                .and().authorizeRequests()
                .antMatchers("/oauth/**").permitAll()//不拦截
                .anyRequest()
                .authenticated()
                .and().logout().permitAll()//退出放行
                .and().csrf().disable();//关闭CSRF保护
        //第二种方式
/*        http.cors()
                .and().authorizeRequests()
                .antMatchers("/user/**").hasRole("admin")
                .anyRequest().permitAll()
                // 添加JWT登录拦截器
                .and().addFilter(new JWTAuthenticationFilter(authenticationManager()))
                // 添加JWT鉴权拦截器
                .addFilter(new JWTAuthorizationFilter(authenticationManager()))
                // 设置Session的创建策略为：Spring Security不创建HttpSession
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // 匿名用户访问无权限资源时的异常处理
                .and().exceptionHandling().authenticationEntryPoint(new JWTAuthenticationEntryPoint())
                .and().csrf().disable();//关闭CSRF保护*/
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 注册跨域配置
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
}

