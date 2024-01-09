package com.example.mqtt.dependency;

public class SuccessResult<T> extends Result<T> {
    private T data;

    public SuccessResult(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return true;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    @Override
    public T getData() {
        return data;
    }


}
