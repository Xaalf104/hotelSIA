package com.example.hotelsia;

public class adminrooms {

    int roomNo;
    String roomType;
    int roomPrice;
    int capacity;
    String roomAvailability;

    public adminrooms(int roomNo, int roomPrice, int capacity, String roomType, String roomAvailability){
        this.roomAvailability = roomAvailability;
        this.roomPrice = roomPrice;
        this.roomNo = roomNo;
        this.capacity = capacity;
        this.roomType = roomType;
    }

    public int getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(int roomNo) {
        this.roomNo = roomNo;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public int getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(int roomPrice) {
        this.roomPrice = roomPrice;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getRoomAvailability() {
        return roomAvailability;
    }

    public void setRoomAvailability(String roomAvailability) {
        this.roomAvailability = roomAvailability;
    }
}
