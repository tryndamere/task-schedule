# task-schedule

## zookeeper目录 
![图片](https://github.com/tryndamere/task-schedule/raw/master/image.png)

## 任务注册过程
任务注册通过 @TaskDefinitionAndHandler 来注册。

```java
    @TaskDefinitionAndHandler(key = "a" , cronExpress = "5 * * * * ?")  
    public void test() {  
        System.out.println("=========================");  
    }
```



