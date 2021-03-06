package com.skg.smodel.zuul.config;

import com.skg.smodel.zuul.jwt.JwtAuthenticationTokenFilter;
import com.skg.smodel.zuul.service.GateUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private GateUserDetailsService detailsService;
  @Value("${gate.ignore.startWith}")
  private String startWith;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
  /*    http.requestMatchers()
            .antMatchers("/api*//**","/back*//**")
            .and()
            .authorizeRequests()
            .antMatchers("*//**").permitAll().and()
            .httpBasic();*/

    http.csrf().disable();
    http.headers().frameOptions().disable();
    //http.httpBasic();
    // 添加JWT filter
    //http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
    // 禁用缓存
    http.headers().cacheControl();
    http.headers().contentTypeOptions().disable();
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers("/resources/**");
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    //super.configure(auth);
    auth.userDetailsService(detailsService).passwordEncoder(new BCryptPasswordEncoder());
  }
/*  @Bean
  public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
    return new JwtAuthenticationTokenFilter();
  }*/
}
