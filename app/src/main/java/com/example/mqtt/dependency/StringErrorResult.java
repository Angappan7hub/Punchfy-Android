package com.example.mqtt.dependency;

public class StringErrorResult<T> extends Result<T> {
    private String errorMessage;

    public StringErrorResult(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public T getData() {
        return null;
    }
}
