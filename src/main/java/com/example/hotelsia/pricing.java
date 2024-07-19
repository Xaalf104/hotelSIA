package com.example.hotelsia;

import java.time.LocalDateTime;

public class pricing {

    private int price;
    private int capacity;
    private String roomType;

    public pricing (int price, int capacity, String roomType){
        this.price = price;
        this.capacity = capacity;
        this.roomType = roomType;
    }

    public pricing(int userid, String first, String middle, String last, String suffix, String emailad, int phone, LocalDateTime date) {
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }
}
