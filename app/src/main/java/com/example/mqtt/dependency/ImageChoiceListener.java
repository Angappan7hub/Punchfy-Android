package com.example.mqtt.dependency;

public interface ImageChoiceListener<T> {

    void onImageChoosed(T selectedItem, int position);
}
