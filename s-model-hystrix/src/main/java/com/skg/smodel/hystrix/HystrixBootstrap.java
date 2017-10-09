package com.skg.smodel.hystrix;


import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;
import de.codecentric.boot.admin.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableHystrix
@EnableHystrixDashboard
@EnableAdminServer
@EnableDiscoveryClient
public class HystrixBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(HystrixBootstrap.class, args);
    }
    @Bean
    public HystrixCommandAspect hystrixAspect() {
        return new HystrixCommandAspect();
    }

}
