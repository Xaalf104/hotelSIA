package com.example.hotelsia;

import java.time.LocalDate;

public class DataSingletonForRoom {
    private static final DataSingletonForRoom instance = new DataSingletonForRoom();
    private String firstName;
    private String lastName;
    private String middleName;

    private String suffix;
    private String roomType;
    private String checkInDate;
    private int roomNumber;
    private String hours;

    private String refNumber;
    private int roomPrice;
    private int paid;

    private int balance;
    private int change;

    private int seconds;

    private String mop;

    private int gcashAmount;

    private LocalDate dateReserved;
    private String transacType;

    private int headCount;

    private String notes;

    private DataSingletonForRoom(){}
    public static DataSingletonForRoom getInstance(){
        return instance;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


    public int getHeadCount() {
        return headCount;
    }

    public void setHeadCount(int headCount) {
        this.headCount = headCount;
    }

    public int getGcashAmount() {
        return gcashAmount;
    }

    public void setGcashAmount(int gcashAmount) {
        this.gcashAmount = gcashAmount;
    }

    public LocalDate getDateReserved() {
        return dateReserved;
    }

    public void setDateReserved(LocalDate dateReserved) {
        this.dateReserved = dateReserved;
    }


    public String getTransacType() {
        return transacType;
    }

    public void setTransacType(String transacType) {
        this.transacType = transacType;
    }

    public String getMop() {
        return mop;
    }

    public void setMop(String mop) {
        this.mop = mop;
    }

    public String getRefNumber() {
        return refNumber;
    }

    public void setRefNumber(String refNumber) {
        this.refNumber = refNumber;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        if(suffix == "N/A"){
            suffix = "";
            this.suffix = suffix;
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }
    public int getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(int roomPrice) {
        this.roomPrice = roomPrice;
    }

    public int getPaid() {
        return paid;
    }

    public void setPaid(int paid) {
        this.paid = paid;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int paid) {
        this.seconds = seconds;
    }

    public int getChange() {
        return change;
    }

    public void setChange(int change) {
        this.change = change;
    }


}
