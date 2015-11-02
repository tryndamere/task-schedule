# task-schedule
task-schedule是一个中小型的统一调度系统，主要通过zookeeper订阅与发布来实现此功能。目前版本为V1.0.0版本，主要分为3部分内容：  
* task-schedule-client   客户端，主要职责是将客户端的job注册到zookeeper中，供服务器端调用。
* task-schedule-provider 调度中心，主要职责是通过读取zookeeper中节点信息，生成job任务。调度中心目前支持集群，通过zookeeper做leader选举，此处为master与slave。
* task-schedule-web      管理中心，管理zookeeper中各个节点信息与job任务相关内容。

## zookeeper目录 
![图片](https://github.com/tryndamere/task-schedule/raw/master/doc/zookeeper.png)

## 整体流程
![注册流程](https://github.com/tryndamere/task-schedule/raw/master/doc/register.png)
![执行流程](https://github.com/tryndamere/task-schedule/raw/master/doc/executor.png)

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
任务调度中心，通过读取zookeeper的注册信息生成job。任务调度支持集群，主要是通过zookeeper的leader选举来完成。
当A节点获取leader权限时，此节点将一直占用着，直到A节点断开为止，B节点才能获取leader权限。
获取leader权限后，需要做如下事情：
* 读取zookeeper中job注册信息并生成job
* 添加leader失效事件，当连接断开时，zookeeper通知leader节点，leader节点将关闭所有job。

## task-schedule-web
任务管理中心，主要包含：注册任务节点维护，日志等功能。



