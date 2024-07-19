package com.example.hotelsia;

import javax.xml.crypto.Data;
import java.time.LocalDate;

public class DataSingleton {

    private static final DataSingleton instance = new DataSingleton();

    private String fullname;

    private int initialPay;
    private String checkIn;
    private int timeLeft;

    private String timeRemaining;
    private int totalRoomPrice;
    private int amtPaid;
    private int remainingBal;
    private int roomPrice;
    private int roomNo;
    private int basePrice;
    private int addtPay;
    private int hourcounter1;
    private String checkOut;

    private String mop1;
    private String mop2;

    public String getRefNo1() {
        return refNo1;
    }

    public void setRefNo1(String refNo1) {
        this.refNo1 = refNo1;
    }

    private String refNo1;
    private int totalHours;
    private String formattedTime;
    private String roomType;

    private String query;

    private String notes;
    private int headCount;
    private String transacType;

    private String role;
    private DataSingleton(){}

    public static DataSingleton getInstance(){
        return instance;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public String getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(String timeRemaining) {
        this.timeRemaining = timeRemaining;
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

    public int getInitialPay() {
        return initialPay;
    }

    public void setInitialPay(int initialPay) {
        this.initialPay = initialPay;
    }

    public String getTransacType() {
        return transacType;
    }

    public void setTransacType(String transacType) {
        this.transacType = transacType;
    }


    public String getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(String checkOut) {
        this.checkOut = checkOut;
    }


    public int getRemainingBal() {
        return remainingBal;
    }

    public int getTotalHours() {
        return totalHours;
    }


    public String getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public void setTotalHours(int totalHours) {
        this.totalHours = totalHours;
    }


    public void setRemainingBal(int remainingBal) {
        this.remainingBal = remainingBal;
    }


    public int getAmtPaid() {
        return amtPaid;
    }

    public void setAmtPaid(int amtPaid) {
        this.amtPaid = amtPaid;
    }

    public int getHourcounter1() {
        return hourcounter1;
    }

    public void setHourcounter1(int hourcounter1) {
        this.hourcounter1 = hourcounter1;
    }

    public int getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(int roomPrice) {
        this.roomPrice = roomPrice;
    }


    public String getFullname(){
        return  fullname;
    }
    public int getRoomNo(){
        return roomNo;
    }

    public int getTimeLeft(){
        return timeLeft;
    }

    public String getRoomType(){
        return roomType;
    }
    public String getFormattedTime(){
        return formattedTime;
    }

    public int getBasePrice(){return basePrice;}

    public int getAddtPay() {
        return addtPay;
    }

    public void setAddtPay(int addtPay) {
        this.addtPay = addtPay;
    }

    public int getTotalRoomPrice(){return totalRoomPrice;}

    public void setTotalRoomPrice(int totalRoomPrice){this.totalRoomPrice = totalRoomPrice;}

    public void setFullname(String fullname){
        this.fullname = fullname;
    }

    public void setRoomNo(Integer roomNo){
        this.roomNo = roomNo;
    }

    public void setTimeLeft(Integer timeLeft){
        this.timeLeft = timeLeft;

    }
    public void setBasePrice(Integer basePrice){
        this.basePrice = basePrice;

    }
    public void setRoomType(String roomType){
        this.roomType = roomType;

    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }


    public void setFormattedTime(String formattedTime){
        this.formattedTime = formattedTime;
    }

    public void clearData() {
        fullname = null;
        roomType = null;
        roomNo = 0;
        roomPrice = 0;
        totalHours = 0;
        totalRoomPrice = 0;
        hourcounter1 = 0;
        addtPay = 0;
        amtPaid = 0;
        basePrice = 0;
        checkIn = null;
        checkOut = null;
        remainingBal = 0;
        formattedTime = null;
        timeLeft = 0;
        transacType = null;
        mop1 = null;
        mop2 = null;
        initialPay = 0;
        refNo1 = null;
    }

}
