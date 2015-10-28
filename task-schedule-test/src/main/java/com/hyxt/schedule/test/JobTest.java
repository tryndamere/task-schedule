package com.hyxt.schedule.test;

import com.hyxt.schedule.client.annotation.TaskDefinitionAndHandler;
import org.springframework.stereotype.Component;

/**
 * Created by rocky on 2015/10/27.
 */
@Component
public class JobTest {

    @TaskDefinitionAndHandler(key = "a" , cronExpress = "5 * * * * ?")
    public void test() {
        System.out.println("=========================");
    }
}
