package com.demo.http;


/**
 * @author tang
 * @date 2019/1/29
 */

public class EventMessage {
    int code;
    Object obj;

    public EventMessage(int code, Object obj) {
        this.code = code;
        this.obj = obj;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
