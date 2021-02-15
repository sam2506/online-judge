package com.online.judge.config.security;

import com.online.judge.auth.services.JudgeUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JudgeUserDetailsService judgeUserDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider());
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(judgeUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/contests").authenticated()
                .antMatchers(HttpMethod.POST, "/contests/{contestId}/problem/{problemId}").authenticated()
                .antMatchers(HttpMethod.DELETE, "/contests/{contestId}/problem/{problemId}").authenticated()
                .antMatchers(HttpMethod.POST, "/contests/{contestId}/problem/{problemId}/submit").authenticated()
                .antMatchers(HttpMethod.PUT, "/problems/{problemId}/edit").authenticated()
                .antMatchers(HttpMethod.DELETE, "/problems/{problemId}").authenticated()
                .antMatchers(HttpMethod.POST, "/problems/{problemId}/uploadTestCases").authenticated()
                .antMatchers(HttpMethod.POST, "/problems/{problemId}/submit").authenticated()
                .anyRequest().permitAll()
            .and()
            .httpBasic();
    }
}
