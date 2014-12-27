package com.dr.yokohamarally.cores;


import java.io.Serializable;

public class Envelope<T> implements Serializable{

    private String status;
    private String message;
    private T data;

    Envelope() {

    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

}
