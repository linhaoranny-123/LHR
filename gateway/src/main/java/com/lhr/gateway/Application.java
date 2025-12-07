package com.lhr.gateway;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("网关服务启动成功，端口：8088");
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            System.out.println("检查Spring Boot中注册的控制器：");
            String[] beanNames = ctx.getBeanDefinitionNames();
            for (String beanName : beanNames) {
                Object bean = ctx.getBean(beanName);
                // 检查是否是控制器（包括@RestController和@Controller）
                if (bean.getClass().isAnnotationPresent(org.springframework.web.bind.annotation.RestController.class) ||
                        bean.getClass().isAnnotationPresent(org.springframework.stereotype.Controller.class)) {
                    System.out.println("控制器: " + beanName + " - " + bean.getClass());
                }
            }
        };
    }
}