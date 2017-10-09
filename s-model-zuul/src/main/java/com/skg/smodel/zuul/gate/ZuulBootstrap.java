package com.skg.smodel.zuul.gate;

import com.skg.smodel.zuul.filter.SessionAccessFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.session.data.redis.RedisFlushMode;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableZuulProxy
@EnableRedisHttpSession(redisFlushMode = RedisFlushMode.IMMEDIATE)
public class ZuulBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(ZuulBootstrap.class, args);
    }


    @Bean
    public SessionAccessFilter sessionAccessFilter(){
        return new SessionAccessFilter();
    }
}
