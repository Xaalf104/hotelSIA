package com.example.hotelsia;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class confirmationController implements Initializable {

    @FXML
    private Text TypeCnfrm;

    @FXML
    private Text amtpaidCnfrm;

    @FXML
    private Text checkInCnfrm;

    @FXML
    private Text chnge;

    @FXML
    private Text refNo;


    @FXML
    private Text hoursCnfrm;

    @FXML
    private Text name;

    @FXML
    private Button no;

    @FXML
    private Text priceCnfrm;

    @FXML
    private Text roomNumberConfirm;

    @FXML
    private Button yes;

    private Connection conn;
    private DbConnect connect;

    String formattedDate;
    String suffix;

    private String roomStatus = "Occupied";
    private String mop;

    DataSingletonForRoom data = DataSingletonForRoom.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connect = new DbConnect();
        if(data.getSuffix() == null){
            suffix = "";
        }
        name.setText(data.getFirstName() + " "+ data.getMiddleName() + " " + data.getLastName() + suffix);
        TypeCnfrm.setText(data.getRoomType());
        roomNumberConfirm.setText(String.valueOf(data.getRoomNumber()));
        checkInCnfrm.setText(data.getCheckInDate());

        String dateStr = data.getCheckInDate();
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy HH:mm", Locale.ENGLISH);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        LocalDateTime dateTime = LocalDateTime.parse(dateStr, inputFormatter);
        formattedDate = dateTime.format(outputFormatter);

        hoursCnfrm.setText(data.getHours());
        priceCnfrm.setText(String.valueOf(data.getRoomPrice()));
        amtpaidCnfrm.setText(String.valueOf(data.getPaid()));
        chnge.setText(String.valueOf(data.getChange()));
        refNo.setText(data.getRefNumber());
    }

    public void cancel(){
        Stage stage = (Stage) no.getScene().getWindow(); // get the stage associated with the scene
        stage.close();
    }

    public void checkIn() throws SQLException{
        conn = connect.getConnection();
        if(data.getTransacType() == "Walk In"){
            try{
                String updQuery = "UPDATE rooms r SET r.status = (SELECT g.status FROM guest g WHERE g.room_No = r.room_No LIMIT 1) WHERE EXISTS (SELECT 1 FROM guest g WHERE g.room_No = r.room_No)";
                String guestQuery = "INSERT INTO `guest`(`room_No`, `room_type`, `headcount`,  `status`, `firstName`, `middleName`,`lastName`, `suffix`,`usage_hours`, `check_in`, `timer`, `balance` ,`type` , `remarks` ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
                String paymentQuery = "INSERT INTO `payment`(`customer_ID`, `Ref_No`, `totalPrice`, `initial_payment`, `amount_change`, `MOP`,`date_paid`) VALUES (?,?,?,?,?,?,?)";
                PreparedStatement upd = conn.prepareStatement(updQuery);

                PreparedStatement guestPs = conn.prepareStatement(guestQuery, Statement.RETURN_GENERATED_KEYS);

                guestPs.setString(1, roomNumberConfirm.getText());
                guestPs.setString(2, TypeCnfrm.getText());
                guestPs.setInt(3, data.getHeadCount());
                guestPs.setString(4, roomStatus);
                guestPs.setString(5, data.getFirstName());
                guestPs.setString(6, data.getMiddleName());
                guestPs.setString(7, data.getLastName());
                guestPs.setString(8, suffix);
                guestPs.setString(9, hoursCnfrm.getText());
                guestPs.setString(10, formattedDate);
                guestPs.setInt(11, data.getSeconds());
                guestPs.setDouble(12, Double.parseDouble(String.valueOf(data.getBalance())));
                guestPs.setString(13, data.getTransacType());
                guestPs.setString(14, data.getNotes());
                guestPs.executeUpdate();
                upd.executeUpdate();

                ResultSet guestKeys = guestPs.getGeneratedKeys();

                if (guestKeys.next()) {
                    int customerId = guestKeys.getInt(1);
                    PreparedStatement paymentPs = conn.prepareStatement(paymentQuery);
                    paymentPs.setInt(1, customerId);
                    paymentPs.setString(2, refNo.getText());
                    paymentPs.setDouble(3, Double.parseDouble(priceCnfrm.getText()));
                    paymentPs.setDouble(4, Double.parseDouble(amtpaidCnfrm.getText()));
                    paymentPs.setDouble(5, Double.parseDouble(chnge.getText()));
                    paymentPs.setString(6, data.getMop());
                    paymentPs.setString(7, LocalDate.now().toString());
                    paymentPs.executeUpdate();
                }

                Stage stage = (Stage) yes.getScene().getWindow(); // get the stage associated with the scene
                stage.close();
            }
            catch (SQLException e) {
                if (e.getErrorCode() == 1062) { // 1062 is the MySQL error code for duplicate entry
                    // handle the duplicate entry error
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Duplicate Entry");
                    alert.setContentText("The record already exists.");
                    alert.showAndWait();
                } else {
                    // handle other SQL errors
                    e.printStackTrace();
                }
            }
            Stage stage = (Stage) no.getScene().getWindow(); // get the stage associated with the scene
            stage.close();
        }else if(data.getTransacType() == "Reservation"){
            String reservationQuery = "INSERT INTO `reservation` (`reservation_date`, `firstname`, `lastname`, `middlename`, `suffix`, `hours`, `room_type`, `room_No`, `Total_Price`, `Amount_Paid`, `Balance`, `mode_of_payment`, `gcash_refno`, `date_paid`, `status`, `gcash_Amount`, `date_created`, `headcount`, `remarks`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            String updateRoomQuery = "UPDATE rooms SET status = 'Reserved' WHERE room_No = ?;";

            try {
                // Insert into the reservation table
                PreparedStatement reservationPs = conn.prepareStatement(reservationQuery, Statement.RETURN_GENERATED_KEYS);
                reservationPs.setString(1, String.valueOf(data.getDateReserved())); // assuming reservationDate is a DatePicker
                reservationPs.setString(2, data.getFirstName());
                reservationPs.setString(3, data.getLastName());
                reservationPs.setString(4, data.getMiddleName());
                reservationPs.setString(5, suffix);
                reservationPs.setString(6, hoursCnfrm.getText());
                reservationPs.setString(7, TypeCnfrm.getText());
                reservationPs.setString(8, roomNumberConfirm.getText());
                reservationPs.setDouble(9, Double.parseDouble(priceCnfrm.getText()));
                reservationPs.setDouble(10, Double.parseDouble(amtpaidCnfrm.getText()));
                reservationPs.setDouble(11, data.getBalance());
                reservationPs.setString(12, data.getMop());
                reservationPs.setString(13, data.getRefNumber());
                reservationPs.setString(14, LocalDate.now().toString());
                reservationPs.setString(15, "Approved");
                reservationPs.setString(16, String.valueOf(data.getGcashAmount()));
                reservationPs.setString(17, LocalDate.now().toString());
                reservationPs.setInt(18, data.getHeadCount());
                reservationPs.setString(19, data.getNotes());
                reservationPs.executeUpdate();

                // Update the room status
                PreparedStatement updateRoomPs = conn.prepareStatement(updateRoomQuery);
                updateRoomPs.setString(1, roomNumberConfirm.getText());
                updateRoomPs.executeUpdate();


            } catch (SQLException e) {
                if (e.getErrorCode() == 1062) { // 1062 is the MySQL error code for duplicate entry
                    // handle the duplicate entry error
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Duplicate Entry");
                    alert.setContentText("The record already exists.");
                    alert.showAndWait();
                } else {
                    // handle other SQL errors
                    e.printStackTrace();
                }
            }
            Stage stage = (Stage) no.getScene().getWindow(); // get the stage associated with the scene
            stage.close();
        }
    }
}
