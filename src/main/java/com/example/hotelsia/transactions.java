package com.example.hotelsia;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class transactions {

    private int roomNo;
    private String roomType;
    private String fullName;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private int totalHours;
    private int extendedHours;
    private String timeLeft;
    private int totalPrice;
    private int initialPay;
    private int finalPay;
    private int totalPaid;
    private String mop1;
    private String mop2;
    private String refNo1;
    private String refNo2;
    private String transacType;
    private String Status;

    private String formattedCheckIn;
    private String formattedCheckOut;
    private String remarks;

    public transactions(String remarks, int roomNo, String roomType,  String fullName, LocalDateTime checkIn, LocalDateTime checkOut, int totalHours, int extendedHours, String timeLeft, int totalPrice, int initialPay, int finalPay, int totalPaid, String mop1, String mop2, String refNo1, String refNo2, String transacType, String Status){
        this.remarks = remarks;
        this.roomNo = roomNo;
        this.roomType = roomType;
        this.fullName = fullName;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalHours = totalHours;
        this.extendedHours = extendedHours;
        this.timeLeft = timeLeft;
        this.totalPrice = totalPrice;
        this.initialPay = initialPay;
        this.finalPay = finalPay;
        this.totalPaid = totalPaid;
        this.mop1 = mop1;
        this.mop2 = mop2;
        this.refNo1 = refNo1;
        this.refNo2 = refNo2;
        this.transacType = transacType;
        this.Status = Status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getFormattedCheckIn() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        return checkIn.format(formatter);
    }

    public String getFormattedCheckOut() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        return checkOut.format(formatter);
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDateTime getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDateTime checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDateTime getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDateTime checkOut) {
        this.checkOut = checkOut;
    }

    public int getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(int totalHours) {
        this.totalHours = totalHours;
    }

    public int getExtendedHours() {
        return extendedHours;
    }

    public void setExtendedHours(int extendedHours) {
        this.extendedHours = extendedHours;
    }

    public String getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(String timeLeft) {
        this.timeLeft = timeLeft;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getInitialPay() {
        return initialPay;
    }

    public void setInitialPay(int initialPay) {
        this.initialPay = initialPay;
    }

    public int getFinalPay() {
        return finalPay;
    }

    public void setFinalPay(int finalPay) {
        this.finalPay = finalPay;
    }

    public int getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(int totalPaid) {
        this.totalPaid = totalPaid;
    }

    public String getMop1() {
        return mop1;
    }

    public void setMop1(String mop1) {
        this.mop1 = mop1;
    }

    public String getMop2() {
        return mop2;
    }

    public void setMop2(String mop2) {
        this.mop2 = mop2;
    }

    public String getRefNo1() {
        return refNo1;
    }

    public void setRefNo1(String refNo1) {
        this.refNo1 = refNo1;
    }

    public String getRefNo2() {
        return refNo2;
    }

    public void setRefNo2(String refNo2) {
        this.refNo2 = refNo2;
    }

    public String getTransacType() {
        return transacType;
    }

    public void setTransacType(String transacType) {
        this.transacType = transacType;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
