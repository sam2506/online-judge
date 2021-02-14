package com.online.judge.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("sam")
                .password("abcdabcd")
                .roles("USER");
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.httpBasic();
//        http.authorizeRequests()
//                .antMatchers("/viewSubmission/1")
//                .permitAll()
//                .and()
//                .formLogin()
//                .and()
//                .logout()
//                .and()
//                .httpBasic();
//        http.authorizeRequests()
//                .anyRequest().authenticated()
//                .and()
//                .httpBasic();
//        http.authorizeRequests()
//                .antMatchers("/viewSubmission/*")
//                .permitAll()
//                .and()
//                .formLogin()
//                .and()
//                .logout();
//        http.authorizeRequests()
//                .antMatchers("/problems").hasRole("USER")
//                .and()
//                .formLogin()
//                .and()
//                .logout();
    }
}
