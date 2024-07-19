package com.example.hotelsia;

import javafx.scene.control.Button;

import java.time.LocalDate;

public class guests {
    private int roomNo;
    private int usageHours;

    private int extendedHours;

    private int roomPrice;
    private int amountOfPay;
    private int amountOfChange;
    private String roomType;
    private String fullName;
    private String refNo;
    private String type;

    private String mop;
    private LocalDate checkIn;
    private int timer;

    private int balance;
    private Button button;

    private int headcount;

    private String remarks;
    public guests(String remarks, int roomNo, String roomType, String fullName, int usageHours, int headcount, int extendedHours, int roomPrice, int amountOfPay, int amountOfChange, LocalDate checkIn, int timer, int balance, String refNo, String type, String mop) {
        this.remarks = remarks;
        this.roomNo = roomNo;
        this.roomType = roomType;
        this.usageHours = usageHours;
        this.headcount = headcount;
        this.extendedHours = extendedHours;
        this.roomPrice = roomPrice;
        this.amountOfPay = amountOfPay;
        this.amountOfChange = amountOfChange;
        this.checkIn = checkIn;
        this.timer = timer;
        this.balance = balance;
        this.refNo = refNo;
        this.fullName = fullName;
        this.type = type;
        this.mop =  mop;
        this.button = new Button("Extend");
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getHeadcount() {
        return headcount;
    }

    public void setHeadcount(int headcount) {
        this.headcount = headcount;
    }

    public String getMop() {
        return mop;
    }

    public void setMop(String mop) {
        this.mop = mop;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public void setButton(Button button){
        this.button = button;
    }

    public Button getButton(){
        return button;
    }
    public int getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(int roomNo) {
        this.roomNo = roomNo;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }


    public int getUsageHours() {
        return usageHours;
    }

    public void setUsageHours(int usageHours) {
        this.usageHours = usageHours;
    }

    public int getExtendedHours() {
        return extendedHours;
    }

    public void setExtendedHours(int extendedHours) {
        this.extendedHours = extendedHours;
    }

    public int getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(int roomPrice) {
        this.roomPrice = roomPrice;
    }

    public int getAmountOfPay() {
        return amountOfPay;
    }

    public void setAmountOfPay(int amountOfPay) {
        this.amountOfPay = amountOfPay;
    }

    public int getAmountOfChange() {
        return amountOfChange;
    }

    public void setAmountOfChange(int amountOfChange) {
        this.amountOfChange = amountOfChange;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }


    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }
    public int getTimer() {
        return timer;
    }

    public void setTimer(Integer timer) {
        this.timer = timer;
    }

    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

}

