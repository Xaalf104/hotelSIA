package com.example.hotelsia;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class update_confirmationController implements Initializable {

    @FXML
    private Text FullNameText;

    @FXML
    private Text roomTypeConfirm;


    @FXML
    private Text roomNoConfirm;
    @FXML
    private ChoiceBox<String> extendHours;
    @FXML
    private Text timeLeftConfirm;

    @FXML
    private Button extend;
    private int basePrice;
    private int seconds;

    private int hoursCounter;

    private Connection conn;
    private DbConnect connect;


    DataSingleton data = DataSingleton.getInstance();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){

        //db connection
        connect = new DbConnect();

        basePrice = data.getBasePrice();
        // Create an ObservableList of items
        ObservableList<String> items = FXCollections.observableArrayList("1hr", "2hrs", "3hrs", "4hrs", "5hrs", "6hrs", "7hrs", "8hrs");

        // Add the items to the ChoiceBox
        extendHours.setItems(items);

        //for getting the data from the singleton
        FullNameText.setText(data.getFullname());
        roomNoConfirm.setText(String.valueOf(data.getRoomNo()));
        roomTypeConfirm.setText(data.getRoomType());
        int i = data.getTimeLeft();
        int timeRemaining = Math.max(i, 0);

        // Calculate the time left in hours and minutes
        int hoursLeft = timeRemaining / 3600;
        int minutesLeft = (timeRemaining % 3600) / 60;

        String timeLeft = String.format("%02d:%02d", hoursLeft, minutesLeft);

        timeLeftConfirm.setText(timeLeft);

        //Execute value from hour selection
        extendHours.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Check if the new value is not null
            if (newValue != null) {
                if(newValue.equals("1hr")){
                    seconds = seconds + 3600;
                    basePrice =  basePrice + 60;
                    hoursCounter = 1;
                    System.out.println(seconds);
                } else if (newValue.equals("2hrs")) {
                    seconds = seconds + 7200;
                    basePrice = basePrice + 120;
                    hoursCounter = 2;
                }else if (newValue.equals("3hrs")) {
                    seconds = seconds + 10800;
                    basePrice = basePrice +  180;
                    hoursCounter = 3;
                }else if (newValue.equals("4hrs")) {
                    seconds = seconds + 14400;
                    basePrice =  basePrice + 240;
                    hoursCounter = 4;
                }else if (newValue.equals("5hrs")) {
                    seconds = seconds + 18000;
                    basePrice = basePrice + 300;
                    hoursCounter = 5;
                }else if (newValue.equals("6hrs")) {
                    seconds = seconds + 21600;
                    basePrice =  basePrice + 370;
                    hoursCounter = 6;
                }else if (newValue.equals("7hrs")) {
                    seconds = seconds + 25200;
                    basePrice =  basePrice + 440;
                    hoursCounter = 7;
                }else if (newValue.equals("8hrs")) {
                    seconds = seconds + 28800;
                    basePrice =  basePrice + 500;
                    hoursCounter = 8;
                }
            }
        });
    }


    @FXML
    public void extendHours() throws SQLException, IOException {
        if(extendHours.getValue() == null){
            // create and show the alert dialog here
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Extend Hours");
            alert.setContentText("Please fill all DATA.");
            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Please confirm details.");
            // Get the DialogPane associated with the Aleadmin  rt
            DialogPane dialogPane = alert.getDialogPane();


            // Set the content text
            alert.setContentText("Are you sure? Please check information before proceeding with this transaction.");
            ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
            alert.getButtonTypes().setAll(yesButton, noButton);

            Optional<ButtonType> result = alert.showAndWait();

            if (((Optional<?>) result).isPresent() && result.get() == yesButton) {
                //User clicked yes
                conn = connect.getConnection();
                String query = "UPDATE `guest` SET `timer` = `timer` + " + seconds + ",`extended_Hours` = `extended_Hours` + "+ hoursCounter +", `balance` = `balance` + " + basePrice + " WHERE `room_No` = '" + roomNoConfirm.getText() + "'";
                PreparedStatement upd = conn.prepareStatement(query);
                upd.executeUpdate();
                upd.close();
                data.setAddtPay(basePrice);
                // Execute a SELECT query to retrieve the new value of the 'price' column
                String selectQuery = "SELECT `totalPrice` FROM `payment` WHERE `customer_ID` = (SELECT `customer_ID` FROM `guest` WHERE `room_No` = '" + roomNoConfirm.getText() + "')";
                PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    int newPrice = rs.getInt("totalPrice");
                    data.setTotalRoomPrice(newPrice);
                }
                conn.close();

                DashboardController.getInstance().getTotalNumber();
                GuestController.getInstance().loadRoomTable();
                Stage stage = (Stage) extend.getScene().getWindow(); // get the stage associated with the scene
                stage.close();


            }else {
                //if user clicked no
            }
        }
    }
}
