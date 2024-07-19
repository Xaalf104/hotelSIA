package com.example.hotelsia;

import javafx.fxml.Initializable;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;


public class officialReceiptController implements Initializable {
    @FXML
    private Text FullNameText;

    @FXML
    private AnchorPane ap;

    @FXML
    private Text checkInDate;

    @FXML
    private Text checkOutDate;

    @FXML
    private Text chnge;

    @FXML
    private Text currentDate;

    @FXML
    private Text extraHours;

    @FXML
    private Text roomPriceCnfrm;

    @FXML
    private Text roomTypeConfirm;

    @FXML
    private Text totalAmountPaid;

    @FXML
    private Text totalBill;

    @FXML
    private Text totalHours;

    DataSingleton data = DataSingleton.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //get current date
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
        String formattedNow = now.format(formatter);

            FullNameText.setText(data.getFullname());
            roomTypeConfirm.setText(data.getRoomType());
            roomPriceCnfrm.setText(String.valueOf(data.getRoomPrice()));
            totalHours.setText(String.valueOf(data.getTotalHours()));
            totalBill.setText(String.valueOf(data.getTotalRoomPrice()));
            totalAmountPaid.setText(String.valueOf(data.getAmtPaid() + data.getInitialPay()));
            int check = data.getAmtPaid() - data.getTotalRoomPrice();
            System.out.println(data.getTotalRoomPrice());
            if(check < 0 ){
                chnge.setText("0");
            }else{
                chnge.setText(String.valueOf(check));
            }
            checkInDate.setText(data.getCheckIn());
            if(data.getCheckOut() != null){
                checkOutDate.setText(data.getCheckOut());
            }else{
                checkOutDate.setText("------");
            }
            currentDate.setText(formattedNow);
            extraHours.setText(String.valueOf(data.getHourcounter1()));

    }
}
