package com.ruoyi.system.domain;

import java.io.Serializable;

public class BaseResponse<T> {
    private T data;
    private String msg;

    public BaseResponse(T data, String msg) {
        this.data = data;
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
