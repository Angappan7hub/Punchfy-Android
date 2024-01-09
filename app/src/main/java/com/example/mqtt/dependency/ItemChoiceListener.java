package com.example.mqtt.dependency;

public interface ItemChoiceListener<T> {
    String POSITION = "position";

    void onItemChoosed(T selectedItem, int position);
}
