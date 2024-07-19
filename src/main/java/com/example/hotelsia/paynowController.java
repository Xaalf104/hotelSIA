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
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class paynowController implements Initializable {

    @FXML
    private Text dateTime;

    @FXML
    private Button cancel;

    @FXML
    private ChoiceBox<String> paymentOption;

    @FXML
    private Text remainingBal;

    @FXML
    private Text changeText;

    @FXML
    private TextField ref_No;

    @FXML
    private TextField amountPaid;

    @FXML
    private Text totalAmount;

    private String roomStatus = "Available";

    private Connection conn;
    private DbConnect connect;

    DataSingleton data = DataSingleton.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connect = new DbConnect();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
        String formattedNow = now.format(formatter);

        // Create an ObservableList of items
        ObservableList<String> items = FXCollections.observableArrayList("GCash", "Cash");

        // Add the items to the ChoiceBox
        paymentOption.setItems(items);
        paymentOption.setValue("Cash");
        remainingBal.setText(String.valueOf(data.getRemainingBal()));
        totalAmount.setText(String.valueOf(data.getTotalRoomPrice()));
        dateTime.setText(formattedNow);

        paymentOption.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Check if the new value is not null
            if (newValue != null) {
                if(newValue.equals("GCash")){
                    ref_No.setDisable(false);
                } else if (newValue.equals("Cash")) {
                    ref_No.setDisable(true);
                }
            }
        });

        amountPaid.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                int amountPaid = Integer.parseInt(newValue);
                int remainingBalance = Integer.parseInt(remainingBal.getText());

                double change = amountPaid - remainingBalance;
                if (change < 0) {
                    change = 0;
                }

                changeText.setText(String.format("%.2f", change));
            } else {
                // Clear the change text if the amount paid is empty
                changeText.setText("");
            }
        });


        // Create a TextFormatter that only allows numeric characters
        TextFormatter<String> numericFormatter = new TextFormatter<>(change -> {
            if (change.isAdded() && !change.getControlNewText().matches("[0-9]*")) {
                return null;
            }
            return change;
        });
        amountPaid.setTextFormatter(numericFormatter);
    }


    public void cancel(){
        Stage stage = (Stage) cancel.getScene().getWindow(); // get the stage associated with the scene
        stage.close();
    }

    public void officialReceipt() throws IOException, SQLException {
        if(data.getRemainingBal() == Integer.parseInt(amountPaid.getText())){
            data.setRemainingBal(0);
        }else{

        }
       if(paymentOption.getValue() == null){
           // create and show the alert dialog here
           Alert alert = new Alert(Alert.AlertType.WARNING);
           alert.setTitle("Information Dialog");
           alert.setHeaderText("Payment");
           alert.setContentText("Please fill all data.");
           alert.showAndWait();
       }else{
           if(Integer.parseInt(amountPaid.getText()) < data.getRemainingBal()){
               // create and show the alert dialog here
               Alert alert = new Alert(Alert.AlertType.INFORMATION);
               alert.setTitle("Information Dialog");
               alert.setHeaderText("Payment");
               alert.setContentText("The payment is insufficient.");
               alert.showAndWait();
           }else{
               int totalAmountPaid = data.getInitialPay() + Integer.parseInt(amountPaid.getText());
               data.setAmtPaid(Integer.parseInt(amountPaid.getText()));
               // Load the FXML file and create a new stage
               FXMLLoader loader = new FXMLLoader(getClass().getResource("receipt.fxml"));
               Stage confirmationStage = new Stage();
               confirmationStage.setResizable(false);
               confirmationStage.initStyle(StageStyle.UTILITY);
               confirmationStage.setScene(new Scene(loader.load()));

               // Show the stage and wait for it to be closed
               confirmationStage.showAndWait();

               if(data.getCheckOut() != null){
                   //check out is not empty
                   String checkInFormat = data.getCheckIn();
                   DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
                   LocalDateTime dateTime = LocalDateTime.parse(checkInFormat, inputFormatter);
                   DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                   String checkIn = dateTime.format(outputFormatter);

                   String checkOutFormat = data.getCheckOut();
                   DateTimeFormatter inputFormatter2 = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
                   LocalDateTime dateTime2 = LocalDateTime.parse(checkOutFormat, inputFormatter2);
                   DateTimeFormatter outputFormatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                   String checkOut = dateTime2.format(outputFormatter2);

                   //Submit to transactions
                   conn = connect.getConnection();

                   String delete = "DELETE FROM guest WHERE room_No = ?";
                   String query = "INSERT INTO transaction (room_No, room_Type, name, checkin, checkout, total_hours, extended_Hours, timeLeft, total_price, initial_payment, final_payment, total_paid, Mode_of_payment, Mode_of_payment2, Ref_No, Ref_No2, type_of_transaction, status, remarks) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                   String updQuery = "UPDATE rooms SET status = 'Available' WHERE room_No = ?";
                   String updReservation = "UPDATE reservation SET status = 'Done' WHERE room_No = ?";

                   PreparedStatement statement = conn.prepareStatement(query);
                   PreparedStatement reservationStatus = conn.prepareStatement(updReservation);
                   PreparedStatement upd = conn.prepareStatement(updQuery);
                   PreparedStatement deleteStatement = conn.prepareStatement(delete);

                   statement.setInt(1, data.getRoomNo());
                   statement.setString(2, data.getRoomType());
                   statement.setString(3, data.getFullname());
                   statement.setString(4, checkIn);
                   statement.setString(5, checkOut);
                   statement.setInt(6, data.getTotalHours());
                   statement.setInt(7, data.getHourcounter1());
                   statement.setString(8, data.getTimeRemaining());
                   statement.setInt(9, data.getTotalRoomPrice());
                   statement.setInt(10, data.getInitialPay());
                   statement.setInt(11, Integer.parseInt(amountPaid.getText()));
                   statement.setInt(12, totalAmountPaid);
                   statement.setString(13, data.getMop1());
                   statement.setString(14, paymentOption.getSelectionModel().getSelectedItem());
                   statement.setString(15, data.getRefNo1());
                   statement.setString(16, ref_No.getText());
                   statement.setString(17, data.getTransacType());
                   statement.setString(18, "Completed");
                   statement.setString(19, data.getNotes());

                   statement.executeUpdate();

                   upd.setInt(1, data.getRoomNo());
                   upd.executeUpdate();

                   deleteStatement.setInt(1, data.getRoomNo());
                   deleteStatement.executeUpdate();

                   reservationStatus.setInt(1, data.getRoomNo());
                   reservationStatus.executeUpdate();

                   conn.close();

                   data.clearData();

                   Stage stage = (Stage) cancel.getScene().getWindow(); // get the stage associated with the scene
                   stage.close();
               }else{
                   //Submit to guests
               }

           }
       }
    }
}
