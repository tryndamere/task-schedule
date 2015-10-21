package com.link.schedule.client.config;

/**
 * Created by rocky on 2015/10/21.
 */
public enum TaskExecutorStateEnum {
    DOING {
       public String getState() {
           return "0";
       }
    } ,
    DONE {
        public String getState() {
            return "1";
        }
    }
}
