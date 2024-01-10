package com.example.mqtt.dependency;

public class Result<T> {

    private T data;
    private boolean isSuccess;
    private String errorMessage;

    public Result(){

    }
    public Result(T data){
        this.data=data;
        isSuccess=true;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        if (errorMessage == null)
            this.errorMessage = "Something went wrong, contact support";
        isSuccess = false;
        this.errorMessage = errorMessage;
    }

    public final static class StringError extends Result {
        private String error;

        public StringError(String error) {
            this.error = error;
        }

        public String getError() {
            return this.error;
        }

        @Override
        public Object getData() {
            return null;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }


    }

    public final static class Success<T> extends Result {
        private T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return this.data;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }


    }
}
