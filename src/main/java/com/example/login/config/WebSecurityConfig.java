package com.example.login.config;

import com.example.login.model.Oauth2User;
import com.example.login.service.UserService;
import com.example.login.service.impl.Oauth2UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserService userService;

    @Autowired
    private Oauth2UserServiceImpl oauth2UserService;

    @Autowired
    public WebSecurityConfig(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Override
    protected void configure(HttpSecurity http) {
        try {
            http.csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/login-google/**","/register/**", "/forgot-password/**", "/reset-password/**", "/oauth/**").permitAll()
//                    .antMatchers("/home/**").hasAnyRole("USER")
                    .anyRequest().authenticated()
                    .and()
                    .formLogin()
                    .loginPage("/login").defaultSuccessUrl("/home").permitAll()
                    .and()
//                    .oauth2Login()
//                    .loginPage("/login")
//                    .userInfoEndpoint().userService(oauth2UserService)
//                    .and()
//                    .successHandler((request, response, authentication) -> {
//                        Oauth2User oauth2User = (Oauth2User) authentication.getPrincipal();
//                        userService.processOAuthPostLogin(oauth2User);
//                        response.sendRedirect("/home");
//                    })
//                    .failureHandler((request, response, exception) -> response.sendRedirect("/login?error"))
//                    .and()
                    .logout()
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .logoutSuccessUrl("/login?logout").permitAll()
                    .and()
                    .exceptionHandling().accessDeniedPage("/403");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }
}
