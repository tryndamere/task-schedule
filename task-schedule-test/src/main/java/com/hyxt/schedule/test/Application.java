package com.hyxt.schedule.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CountDownLatch;

/**
 * Created by rocky on 2015/10/27.
 */
@Configuration
@ComponentScan(basePackages = "com.hyxt")
public class Application {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(Application.class);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await();
    }

}
