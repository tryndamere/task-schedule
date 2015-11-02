# task-schedule
task-schedule是一个中小型的统一调度系统，主要通过zookeeper订阅与发布来实现此功能。目前版本为V1.0.0版本，主要分为3部分内容：  
1.task-schedule-client   客户端，主要职责是将客户端的job注册到zookeeper中，供服务器端调用。  
2.task-schedule-provider 调度中心，主要职责是通过读取zookeeper中节点信息，生成job任务。调度中心目前支持集群，通过zookeeper做leader选举，此处为master与slave。  
3.task-schedule-web      管理中心，管理zookeeper中各个节点信息与job任务相关内容。  

## zookeeper目录 
![图片](https://github.com/tryndamere/task-schedule/raw/master/image.png)

## task-schedule-client
任务注册通过 @TaskDefinitionAndHandler 来注册。

```java
    @Component
    public class JobTest {
        @TaskDefinitionAndHandler(key = "a" , cronExpress = "5 * * * * ?")
        //cronExpress为cron通用表达式
        public void test() {
            System.out.println("=========================");
        }    
    }
```

## task-schedule-provider


## task-schedule-web



