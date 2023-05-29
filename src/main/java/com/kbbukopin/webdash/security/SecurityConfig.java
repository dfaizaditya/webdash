package com.kbbukopin.webdash.security;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        auth
//                .inMemoryAuthentication()
//                .passwordEncoder(passwordEncoder)
//                .withUser("user")
//                .password(passwordEncoder.encode("password"))
//                .roles("USER");
//        System.out.println(passwordEncoder.encode("password"));
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
            .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
//            .and()
//            .authorizeRequests()
//            .antMatchers(HttpMethod.POST, "/api/projects/**").authenticated()
//            .antMatchers(HttpMethod.PUT, "/api/projects/**").authenticated()
//            .antMatchers(HttpMethod.DELETE, "/api/projects/**").authenticated()
//            .antMatchers(HttpMethod.GET, "/api/projects/**").authenticated()
//            .anyRequest().permitAll()
//            .and()
//            .httpBasic();
    }
}

