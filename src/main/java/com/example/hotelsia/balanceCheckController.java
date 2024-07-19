package com.example.hotelsia;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class balanceCheckController implements Initializable {

    @FXML
    private AnchorPane ap;

    @FXML
    private TextField change;

    @FXML
    private TextField initialPayment;

    @FXML
    private TextField remainingBalance;

    @FXML
    private TextField referenceNo;
    @FXML
    private Text totalRoomPrice;

    @FXML
    private TextArea remarks;

    @FXML
    private ChoiceBox<String> modeOfPayment;

    @FXML
    private Button cancelBtn;

    DataSingletonForRoom data = DataSingletonForRoom.getInstance();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        totalRoomPrice.setText(String.valueOf(data.getRoomPrice()));
        // Create an ObservableList of items
        ObservableList<String> items = FXCollections.observableArrayList("GCash", "Cash");

        // Add the items to the ChoiceBox
        modeOfPayment.setItems(items);
        modeOfPayment.setValue("Cash");

        modeOfPayment.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Check if the new value is not null
            if (newValue != null) {
                if(newValue.equals("GCash")){
                    referenceNo.setDisable(false);
                } else if (newValue.equals("Cash")) {
                    referenceNo.setDisable(true);
                }
            }
        });

        //Automatic Calculation for payment and balances
        initialPayment.textProperty().addListener((observable, oldValue, newValue) -> {
            int costOfRoom = Integer.parseInt(totalRoomPrice.getText());
            // convert the amountPaidTextField input to a double
            int amountPaid;
            try {
                amountPaid = Integer.parseInt(newValue);
            } catch (NumberFormatException e) {
                // handle invalid input, e.g. non-numeric input
                // optionally clear the text field or display an error message
                return;
            }

            // calculate the change
            int balanceAmount = costOfRoom - amountPaid;
            int changeAmount = amountPaid - costOfRoom;

            //balance checking
            if(data.getTransacType() == "Walk In"){
                if(balanceAmount <=0){
                    remainingBalance.setText("0");
                }else{
                    remainingBalance.setText(String.valueOf(balanceAmount));
                }
            }else if (data.getTransacType() == "Reservation"){
                if(balanceAmount <=0){
                    remainingBalance.setText("0");
                }else{
                    remainingBalance.setText(String.valueOf(balanceAmount));
                }
            }

            if(changeAmount <=0){
                change.setText("0");
            }else{
                change.setText(String.valueOf(changeAmount));
            }

        });

        // Create a TextFormatter that only allows numeric characters
        TextFormatter<String> numericFormatter = new TextFormatter<>(change -> {
            if (change.isAdded() && !change.getControlNewText().matches("[0-9]*")) {
                return null;
            }
            return change;
        });
        initialPayment.setTextFormatter(numericFormatter);
    }

    public void cancel(){
        Stage stage = (Stage) cancelBtn.getScene().getWindow(); // get the stage associated with the scene
        stage.close();
    }
    public void submitToConfirmation() throws IOException {
        data.setPaid(Integer.parseInt(initialPayment.getText()));
        data.setChange(Integer.parseInt(change.getText()));
        data.setBalance(Integer.parseInt(remainingBalance.getText()));
        data.setMop(modeOfPayment.getValue());
        data.setRefNumber(referenceNo.getText());
        data.setNotes(remarks.getText().trim());

        if(modeOfPayment.getValue() == "GCash"){
            data.setGcashAmount(Integer.parseInt(initialPayment.getText()));
        }

        if(data.getTransacType() == "Walk In"){
            if(initialPayment.getText().isBlank() || modeOfPayment.getValue().isEmpty()){
                // create and show the alert dialog here
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Information Dialog");
                alert.setHeaderText("Payment");
                alert.setContentText("Please fill all DATA.");
                alert.showAndWait();
            }else{
                if((Integer.parseInt(totalRoomPrice.getText()) > Integer.parseInt(initialPayment.getText()))){
                    // create and show the alert dialog here
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText("Payment");
                    alert.setContentText("The payment is insufficient.");
                    alert.showAndWait();
                }else{
                    // Load the FXML file and create a new stage
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("confirmation.fxml"));
                    Stage confirmationStage = new Stage();
                    confirmationStage.setResizable(false);
                    confirmationStage.initStyle(StageStyle.UTILITY);
                    confirmationStage.setScene(new Scene(loader.load()));
                    confirmationStage.setResizable(false);
                    // Show the stage and wait for it to be closed
                    confirmationStage.showAndWait();

                    Stage stage = (Stage) cancelBtn.getScene().getWindow(); // get the stage associated with the scene
                    stage.close();
                }
            }
        }else if(data.getTransacType() == "Reservation"){
            if(initialPayment.getText().isBlank() || modeOfPayment.getValue().isEmpty()){
                // create and show the alert dialog here
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Information Dialog");
                alert.setHeaderText("Payment");
                alert.setContentText("Please fill all DATA.");
                alert.showAndWait();
            }else{
                int initialPaymentValue = Integer.parseInt(initialPayment.getText().trim());
                int totalRoomPriceValue = Integer.parseInt(totalRoomPrice.getText().trim());


                System.out.println("initialPaymentValue: " + initialPaymentValue);
                System.out.println("totalRoomPriceValue: " + totalRoomPriceValue);
                if (initialPaymentValue < ((double) totalRoomPriceValue) * 0.5){
                    // create and show the alert dialog here
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText("Payment");
                    alert.setContentText("The payment is insufficient. Must be half(50%) of the total room price.");
                    alert.showAndWait();
                }else{
                    // Load the FXML file and create a new stage
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("confirmation.fxml"));
                    Stage confirmationStage = new Stage();
                    confirmationStage.setResizable(false);
                    confirmationStage.initStyle(StageStyle.UTILITY);
                    confirmationStage.setScene(new Scene(loader.load()));
                    confirmationStage.setResizable(false);
                    // Show the stage and wait for it to be closed
                    confirmationStage.showAndWait();

                    Stage stage = (Stage) cancelBtn.getScene().getWindow(); // get the stage associated with the scene
                    stage.close();
                }
            }
        }
    }

}
