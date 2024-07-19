package com.example.hotelsia;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class reservations {

    private int roomNo;
    private String roomType;
    private String firstName, middleName, lastName, suffix;
    private int hours;
    private int totalPrice;
    private int initialPay;
    private int balance;
    private String MOP;

    private int headcount;

    private String refNo;
    private LocalDateTime dateReserved;
    private String datePaid;

    private String formattedReservation;
    private String remarks;

    private int reserveid;

    private int userid;

    private final BooleanProperty selected;

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
    public reservations(int reserveid, int userid, String remarks, int roomNo, String roomType, String firstName, String middleName, String lastName, String suffix, int headcount, int hours, int totalPrice, int initialPay, int balance, String MOP, String refNo, LocalDateTime dateReserved, String datePaid){
        this.selected = new SimpleBooleanProperty(false);
        this.userid = userid;
        this.reserveid = reserveid;
        this.remarks = remarks;
        this.roomNo = roomNo;
        this.roomType = roomType;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.suffix = suffix;
        this.headcount = headcount;
        this.hours = hours;
        this.totalPrice = totalPrice;
        this.initialPay = initialPay;
        this.balance = balance;
        this.MOP = MOP;
        this.refNo = refNo;
        this.dateReserved = dateReserved;
        this.datePaid = datePaid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getReserveid() {
        return reserveid;
    }

    public void setReserveid(int reserveid) {
        this.reserveid = reserveid;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public int getHeadcount() {
        return headcount;
    }

    public void setHeadcount(int headcount) {
        this.headcount = headcount;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public String getFormattedReservation() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        return dateReserved.format(formatter);
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


    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
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

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getMOP() {
        return MOP;
    }

    public void setMOP(String MOP) {
        this.MOP = MOP;
    }

    public LocalDateTime getDateReserved() {
        return dateReserved;
    }

    public void setDateReserved(LocalDateTime dateReserved) {
        this.dateReserved = dateReserved;
    }

    public String getDatePaid() {
        return datePaid;
    }

    public void setDatePaid(String datePaid) {
        this.datePaid = datePaid;
    }
}
