package com.skg.smodel.regist.listener;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import com.netflix.eureka.EurekaServerContextHolder;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceCanceledEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRenewedEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaRegistryAvailableEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
/**
 * 用于监听Eureka服务停机通知
 **/
@Configuration
@EnableScheduling
public class EurekaInstanceCanceledListener implements ApplicationListener {

    private static Logger log = LoggerFactory.getLogger(EurekaInstanceCanceledListener.class);
    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        //服务挂掉自动通知
        if(applicationEvent instanceof EurekaInstanceCanceledEvent){
            EurekaInstanceCanceledEvent event = (EurekaInstanceCanceledEvent)applicationEvent;
            //获取当前Eureka示例中的节点信息
            PeerAwareInstanceRegistry registry = EurekaServerContextHolder.getInstance().getServerContext().getRegistry();
            Applications applications = registry.getApplications();
            //便利获取已注册节点中与当前失效节点ID一致 的节点信息
          /*  applications.getRegisteredApplications().forEach(registryApplication -> {
                registryApplication.getInstances().forEach(instance -> {
                    if(Objects.equals(instance.getInstanceId(),event.getServerId())){
                        //log.info("服务:{}挂啦。。。",instance.getAppName());
                    }
                });
            })*/

            List <Application> ls= applications.getRegisteredApplications();

            for (Application application:ls) {
                for (InstanceInfo instanceInfo: application.getInstances()) {
                    if(instanceInfo.getInstanceId().equals(((EurekaInstanceCanceledEvent) applicationEvent).getServerId())){
                        //System.out.println(instanceInfo.getAppName()+"---服务--->挂啦！");
                        log.info("服务:{}挂啦。。。",instanceInfo.getAppName());
                    }
                }
            }
        }
        if(applicationEvent instanceof EurekaInstanceRegisteredEvent){
            EurekaInstanceRegisteredEvent event = (EurekaInstanceRegisteredEvent)applicationEvent;
            log.info("服务:{}注册成功啦。。。",event.getInstanceInfo().getAppName());
            //System.out.println(event.getInstanceInfo().getAppName()+"---服务--->注册成功！");
        }
        if (applicationEvent instanceof EurekaInstanceRenewedEvent) {
            EurekaInstanceRenewedEvent event = (EurekaInstanceRenewedEvent) applicationEvent;
            log.info("心跳检测服务：{}。。。" ,event.getInstanceInfo().getAppName());
            //System.out.println(event.getInstanceInfo().getAppName()+"---服务-->心跳监测！");
        }
        if (applicationEvent instanceof EurekaRegistryAvailableEvent) {
           // log.info("服务 Aualiable。。。");
        }
    }
}
