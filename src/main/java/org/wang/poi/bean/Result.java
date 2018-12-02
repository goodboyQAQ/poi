package org.wang.poi.bean;

import lombok.Data;

@Data
public class Result<T> {
    private String msg;
    private boolean success;
    private T data;

    public Result(){
        this.success=false;
        this.msg="系统错误";
    }
}
