package com.skg.smodel.zuul.biz;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.skg.smodel.zuul.model.UserInfo;
import com.skg.smodel.zuul.service.rpc.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;


@Service
public class UserSecurity {
    @Lazy
    @Autowired
    private IUserService userService;

    @HystrixCommand(fallbackMethod = "fallbackMethod")
    public UserInfo getUserByUsername(String username){
        return userService.getUserByUsername(username);
    }
    public UserInfo fallbackMethod(String username){
        return new UserInfo();
    }
}
