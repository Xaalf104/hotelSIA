package com.example.hotelsia;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class CheckOutController implements Initializable {
    @FXML
    private Text FullNameText;

    @FXML
    private Text addtPay;

    @FXML
    private AnchorPane ap;

    @FXML
    private Button cancel;

    @FXML
    private Text checkInDate;

    @FXML
    private Text initialPay;

    @FXML
    private Text remainingBal;

    @FXML
    private Text checkOutDate;

    @FXML
    private Text currentDate;

    @FXML
    private Text roomPriceCnfrm;

    @FXML
    private Text roomTypeConfirm;

    @FXML
    private Text totalBill;

    @FXML
    private Text totalHours;

    private Connection conn;
    private DbConnect connect;
    String hourFormat;

    String formattedNow;
    private int hoursCombined;
    DataSingleton data = DataSingleton.getInstance();
    DataSingletonForRoom data2 =  DataSingletonForRoom.getInstance();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //db connection
        connect = new DbConnect();

        //get current date
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
        formattedNow = now.format(formatter);
        checkOutDate.setText(formattedNow);
        currentDate.setText(formattedNow);
        //getCheckInDate from db
        try {
            getCheckInDate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //add the totalhours of extended and base hour
        getTotalHours();
        FullNameText.setText(data.getFullname());
        roomTypeConfirm.setText(data.getRoomType());
        roomPriceCnfrm.setText(String.valueOf(data.getRoomPrice()));
        totalHours.setText(String.valueOf(hoursCombined));
        addtPay.setText(String.valueOf(data.getAddtPay()));
        totalBill.setText(String.valueOf(data.getAddtPay() + data.getRoomPrice()));
        initialPay.setText(String.valueOf(data.getInitialPay()));
        remainingBal.setText(String.valueOf(data.getRemainingBal()));

    }

    public void getTotalHours(){
        conn = connect.getConnection();
        String selectQuery = "SELECT (`usage_hours` + `extended_hours`) AS `total_hours` FROM `guest` WHERE `room_No` = '" + data.getRoomNo() + "'";
        PreparedStatement selectStmt = null;
        try {
            selectStmt = conn.prepareStatement(selectQuery);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ResultSet rs = null;
        try {
            rs = selectStmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            if (rs.next()) {
                hoursCombined = rs.getInt("total_hours");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void getCheckInDate() throws SQLException {
        conn = connect.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT check_in FROM guest WHERE room_No = ?");
        stmt.setString(1, String.valueOf(data.getRoomNo())); // Set the parameter value for the prepared statement
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            Timestamp checkIn = rs.getTimestamp("check_in");
            LocalDateTime checkInConverted = checkIn.toLocalDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
            String formattedCheckIn = checkInConverted.format(formatter);
            data.setCheckIn(formattedCheckIn);
            checkInDate.setText(formattedCheckIn);
        }
        rs.close();
        stmt.close();
        conn.close();
    }

    @FXML
    public void payNow() throws IOException {

        data.setRemainingBal(Integer.parseInt(remainingBal.getText()));
        data.setTotalRoomPrice(Integer.parseInt(totalBill.getText()));
        data.setTotalHours(hoursCombined);
        data.setCheckOut(formattedNow);
        // Load the FXML file and create a new stage
        FXMLLoader loader = new FXMLLoader(getClass().getResource("paynowForm.fxml"));
        Stage confirmationStage = new Stage();
        confirmationStage.setResizable(false);
        confirmationStage.initStyle(StageStyle.UTILITY);
        confirmationStage.setScene(new Scene(loader.load()));

        // Show the stage and wait for it to be closed
        confirmationStage.showAndWait();


        Stage stage = (Stage) cancel.getScene().getWindow(); // get the stage associated with the scene
        stage.close();
    }

    @FXML
    public void cancel(){
        Stage stage = (Stage) cancel.getScene().getWindow(); // get the stage associated with the scene
        stage.close();
    }
}
