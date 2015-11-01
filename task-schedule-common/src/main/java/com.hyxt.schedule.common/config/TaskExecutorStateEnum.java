package com.hyxt.schedule.common.config;

/**
 * Created by rocky on 2015/10/21.
 */
public enum TaskExecutorStateEnum {
    DOING {
       public String toString() {
           return "0";
       }
    } ,
    DONE {
        public String toString() {
            return "1";
        }
    }
}
