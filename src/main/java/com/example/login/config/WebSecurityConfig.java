package com.example.login.config;

import com.example.login.config.security.FakeBookUserDetailService;
import com.example.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.session.SimpleRedirectSessionInformationExpiredStrategy;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig{
    private static UserService userService;

//    @Autowired
//    public WebSecurityConfig(@Lazy UserService userService) {
//        WebSecurityConfig.userService = userService;
//    }

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public static DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userService);
//        authProvider.setPasswordEncoder(passwordEncoder());
//        return authProvider;
//    }

//    @Bean
//    public HttpSessionEventPublisher httpSessionEventPublisher() {
//        return new HttpSessionEventPublisher();
//    }
    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }

    @Bean
    public static SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public static ConcurrentSessionFilter concurrencyFilter() {
        return new ConcurrentSessionFilter(sessionRegistry(), new SimpleRedirectSessionInformationExpiredStrategy("/loginAdmin?expired"));
    }

    @Bean
    public static CompositeSessionAuthenticationStrategy compositeSessionAuthenticationStrategy(){
        ConcurrentSessionControlAuthenticationStrategy concurrentSessionControlAuthenticationStrategy = new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry());
        concurrentSessionControlAuthenticationStrategy.setMaximumSessions(1);
        concurrentSessionControlAuthenticationStrategy.setExceptionIfMaximumExceeded(true);
        SessionFixationProtectionStrategy sessionFixationProtectionStrategy = new SessionFixationProtectionStrategy();
        RegisterSessionAuthenticationStrategy registerSessionStrategy = new RegisterSessionAuthenticationStrategy(sessionRegistry());
        return new CompositeSessionAuthenticationStrategy(Arrays.asList(concurrentSessionControlAuthenticationStrategy, sessionFixationProtectionStrategy, registerSessionStrategy));
    }

    @Configuration
    @Order(1)
    public static class AdminConfigurationAdapter extends WebSecurityConfigurerAdapter {

        @Bean
        public UserDetailsService adminDetailsService(){
            return new AdminDetailService();
        }

        public DaoAuthenticationProvider authenticationProvider(){
            DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
            authenticationProvider.setUserDetailsService(adminDetailsService());
            authenticationProvider.setPasswordEncoder(passwordEncoder());

            return authenticationProvider;
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(authenticationProvider());
        }

        @Override
        protected AuthenticationManager authenticationManager() throws Exception {
            return super.authenticationManager();
        }

        @Override
        protected void configure(HttpSecurity http) {
            try {
                http.antMatcher("/admin/**")
                        .authorizeRequests()
                        .anyRequest().hasAuthority("ADMIN")
                        .and().httpBasic()

                        .and()
                        .formLogin()
                        .loginPage("/admin/login")
                        .failureUrl("/admin/login?error")
                        .defaultSuccessUrl("/admin/home", true)
                        .permitAll()

                        .and()
                        .logout()
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")

                        .and()
                        .exceptionHandling()
                        .accessDeniedPage("/403")

                        .and()
                        .csrf().disable()
                        .sessionManagement()
                        .sessionAuthenticationStrategy(compositeSessionAuthenticationStrategy())
                        .and()
                        .addFilter(concurrencyFilter());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Configuration
    @Order(2)
    public static class UserConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Bean
        public UserDetailsService userDetailsService(){
         return new FakeBookUserDetailService();
        }

        public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;
    }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());}


        @Override
        protected AuthenticationManager authenticationManager() throws Exception {
            return super.authenticationManager();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .antMatchers("/change-profile", "/cart", "/purchase", "/checkout").authenticated()
                    .antMatchers("/change-profile", "/cart", "/purchase", "/checkout")
                    .hasAuthority("USER")

                    .antMatchers("/admin/*").denyAll()

                    .anyRequest()
                    .permitAll()

                    .and()
                    .formLogin()
                        .loginPage("/loginUser")
                        .loginProcessingUrl("/user_login")
                        .defaultSuccessUrl("/user/home", true)
                        .permitAll()

                    .and()
                    .logout()
                    .logoutUrl("/user_logout")
                    .logoutSuccessUrl("/loginUser")

                    .and()
                        .rememberMe().key("AbcDeFGHIjkLmnOp_123456789")
                        .tokenValiditySeconds(3*24*60*60)

                    .and()
                    .exceptionHandling()
                    .accessDeniedPage("/403")

                        .and()
                        .csrf().disable()
                        .sessionManagement()
                        .sessionAuthenticationStrategy(compositeSessionAuthenticationStrategy())
                        .and()
                        .addFilter(concurrencyFilter());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        }
}