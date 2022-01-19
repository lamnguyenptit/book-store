package com.example.login.config;

import com.example.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig{
    private static UserService userService;

    @Autowired
    public WebSecurityConfig(@Lazy UserService userService) {
        WebSecurityConfig.userService = userService;
    }

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public static DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Configuration
    @Order(1)
    public static class AdminConfigurationAdapter extends WebSecurityConfigurerAdapter {
        public AdminConfigurationAdapter() {
            super();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) {
            auth.authenticationProvider(authenticationProvider());
        }

        @Override
        protected void configure(HttpSecurity http) {
            try {
                http.antMatcher("/admin*")
                        .authorizeRequests()
                        .antMatchers("/admin/**")
                        .hasAuthority("ADMIN")
                        .anyRequest()
                        .authenticated()

                        .and()
                        .formLogin()
                        .loginPage("/loginAdmin")
                        .loginProcessingUrl("/admin_login")
                        .failureUrl("/loginAdmin?error")
                        .defaultSuccessUrl("/admin/home", true)
                        .permitAll()

                        .and()
                        .logout()
                        .logoutUrl("/admin_logout")
                        .logoutSuccessUrl("/loginAdmin?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")

                        .and()
                        .exceptionHandling()
                        .accessDeniedPage("/403")

                        .and()
                        .csrf().disable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Configuration
    public static class UserConfigurationAdapter extends WebSecurityConfigurerAdapter {
        public UserConfigurationAdapter() {
            super();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) {
            auth.authenticationProvider(authenticationProvider());
        }

        @Override
        protected void configure(HttpSecurity http) {
            try {
                http
                        .authorizeRequests()
                        .antMatchers("/loginAdmin","/login-google/**","/register/**", "/forgot-password/**", "/reset-password/**", "/oauth/**")
                        .permitAll()
                        .antMatchers("/user/**")
                        .hasAuthority("USER")
                        .anyRequest()
                        .authenticated()

                        .and()
                        .formLogin()
                        .loginPage("/loginUser")
                        .loginProcessingUrl("/user_login")
                        .failureUrl("/loginUser?error")
                        .defaultSuccessUrl("/user/home", true)
                        .permitAll()

                        .and()
                        .logout()
                        .logoutUrl("/user_logout")
                        .logoutSuccessUrl("/loginUser?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")

                        .and()
                        .exceptionHandling()
                        .accessDeniedPage("/403")

                        .and()
                        .csrf().disable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}