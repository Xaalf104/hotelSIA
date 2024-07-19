package com.example.hotelsia;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.StringEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.mail.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;


public class ReservationController implements Initializable {

    @FXML
    private TableColumn<reservations, String> MOP;

    @FXML
    private TableColumn<reservations, String> RefNo;

    @FXML
    private TableColumn<reservations, Integer> userid;

    @FXML
    private TableColumn<reservations, Integer> reservationID;

    @FXML
    private TableColumn<reservations, Integer> balance;

    @FXML
    private TableColumn<reservations, LocalDateTime> datePaid;

    @FXML
    private TableColumn<reservations, LocalDateTime> dateReservation;

    @FXML
    private TableColumn<reservations, String> firstName;
    @FXML
    private TableColumn<reservations, String> middleName;
    @FXML
    private TableColumn<reservations, String> lastName;
    @FXML
    private TableColumn<reservations, String> suffixColumn;

    @FXML
    private TableColumn<reservations, String> remarks;

    @FXML
    private TableColumn<reservations, Integer> initialPay;

    @FXML
    private TableColumn<reservations, Integer> headcount;

    @FXML
    private ImageView refreshBtn;

    @FXML
    private TableView<reservations> reservationListTableView;

    @FXML
    private TableColumn<reservations, Integer> roomNo;

    @FXML
    private TableColumn<reservations, Integer> roomPrice;

    @FXML
    private TableColumn<reservations, String> roomType;

    @FXML
    private TextField searchField;


    @FXML
    private Button Approve;

    @FXML
    private Button checkInButton;

    @FXML
    private CheckBox selectAllCheckBox;

    @FXML
    private TableColumn<reservations, Integer> usageHours;

    private ObservableList<reservations> list;
    private ObservableList<reservations> items;

    //Db connection
    private DbConnect connect;
    private Connection conn;

    private int index, hc, remainingBal, firstPay, rPrice, hours, roomNumber, mobile_id, user_id;
    private String rType,mop,refNo,dateReserve,datepay, first, middle, last, suffix2, notes;
    String roomNoMobile = "";

    private static final String API_KEY = "BFF16EC24C31FB5E7743609A62CD78B719AA77AA09D505BBEBFBF1446EA7685EEC55E5106F0EF5B4AAA18C4219A25944";

    DataSingleton data = DataSingleton.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connect = new DbConnect();
        reservationListTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            selectAllCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {

                    items = reservationListTableView.getItems();

                    for(reservations item : items){
                        if(selectAllCheckBox.isSelected())
                            item.getFirstName();
                    }
                }
            });

            selectAllCheckBox.setOnAction(event -> {
                boolean selectAll = selectAllCheckBox.isSelected();
                reservationListTableView.getItems().forEach(item -> item.setSelected(selectAll));
            });


        try {
            loadReservationTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if(data.getRole() == "Employee"){
            checkInButton.setDisable(false);
            checkInButton.setVisible(true);
            Approve.setVisible(false);
            Approve.setDisable(true);
        } else if (data.getRole() == "Admin") {
            checkInButton.setDisable(true);
            checkInButton.setVisible(false);
            Approve.setVisible(true);
            Approve.setDisable(false);
        }else{
            //Do nothing
        }

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterRooms(newValue);
        });
    }

    //Method for search function
    private void filterRooms(String searchText) {
        FilteredList<reservations> filteredData = new FilteredList<>(list, p -> true);

        if (searchText == null || searchText.isEmpty()) {
            reservationListTableView.setItems(list);
        } else {
            String lowerCaseSearchText = searchText.toLowerCase();

            filteredData.setPredicate(reserve -> {
                if (Integer.toString(reserve.getRoomNo()).contains(lowerCaseSearchText)) {
                    return true; // match found in room number column
                } else if (reserve.getRoomType().toLowerCase().contains(lowerCaseSearchText)) {
                    return true; // match found in room type column
                } else if (Integer.toString(reserve.getTotalPrice()).contains(lowerCaseSearchText)) {
                    return true; // match found in roomprice column
                } else if (reserve.getFirstName().toLowerCase().contains(lowerCaseSearchText)) {
                    return true; // match found in name column
                }else if (reserve.getLastName().toLowerCase().contains(lowerCaseSearchText)) {
                    return true; // match found in name column
                }else if (reserve.getMiddleName().toLowerCase().contains(lowerCaseSearchText)) {
                    return true; // match found in name column
                }else if (Integer.toString(reserve.getHours()).contains(lowerCaseSearchText)) {
                    return true; // match found in hours column
                }else if (reserve.getDateReserved().toString().toLowerCase().contains(lowerCaseSearchText)) {
                    return true; // match found in check-in column
                }else if (Integer.toString(reserve.getUserid()).contains(lowerCaseSearchText) || (Integer.toString(reserve.getReserveid()).contains(lowerCaseSearchText))) {
                    return true; // match found in check-in column
                }  return false;
            });

            reservationListTableView.setItems(filteredData);
        }
    }

    public void loadReservationTable() throws SQLException {
       try{
           list = FXCollections.observableArrayList();
           conn = connect.getConnection();

           String query = "SELECT user_id, reservation_id, room_No, room_type, firstname, middlename, lastname, suffix, headcount, hours, Total_Price, Amount_Paid, balance, mode_of_payment, gcash_refno, reservation_date, date_paid, status, remarks FROM reservation WHERE status = '" + data.getQuery() + "'";

           ResultSet set = conn.createStatement().executeQuery(query);
           while (set.next()) {
               int roomNo = set.getInt("room_No");
               String roomType = set.getString("room_type");
               String suffix = set.getString("suffix");
               if (suffix.equals("N/A")){
                   suffix = "";
               }
               String firstName = set.getString("firstname");
               String middleName = set.getString("middlename");
               String lastName = set.getString("lastname");
               int headCount = set.getInt("headcount");
               String name = firstName + " " + middleName + " " + lastName + " " + suffix;
               LocalDateTime dateReserved = set.getTimestamp("reservation_date").toLocalDateTime();
               int totalHours = set.getInt("hours");
               int totalPrice = set.getInt("Total_Price");
               int initialPay = set.getInt("Amount_Paid");
               int balance = set.getInt("balance");
               String mop = set.getString("mode_of_payment");
               String refNo = set.getString("gcash_refno");
               String datePaid = set.getString("date_paid");
               String notes = set.getString("remarks");
               int user = set.getInt("user_id");
               int reserveid = set.getInt("reservation_id");

               // format date and time
               DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
               String formattedReservation = dateReserved.format(formatter);

               reservations reserve = new reservations(reserveid, user, notes, roomNo, roomType, firstName, middleName, lastName, suffix, headCount, totalHours, totalPrice, initialPay, balance, mop, refNo, dateReserved, datePaid);
               list.add(reserve);
           }
           userid.setCellValueFactory(new PropertyValueFactory<>("userid"));
           reservationID.setCellValueFactory(new PropertyValueFactory<>("reserveid"));
           roomNo.setCellValueFactory(new PropertyValueFactory<>("roomNo"));
           roomType.setCellValueFactory(new PropertyValueFactory<>("roomType"));
           firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
           middleName.setCellValueFactory(new PropertyValueFactory<>("middleName"));
           lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
           suffixColumn.setCellValueFactory(new PropertyValueFactory<>("suffix"));
           headcount.setCellValueFactory(new PropertyValueFactory<>("headcount"));
           usageHours.setCellValueFactory(new PropertyValueFactory<>("hours"));
           roomPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
           initialPay.setCellValueFactory(new PropertyValueFactory<>("initialPay"));
           balance.setCellValueFactory(new PropertyValueFactory<>("balance"));
           MOP.setCellValueFactory(new PropertyValueFactory<>("MOP"));
           RefNo.setCellValueFactory(new PropertyValueFactory<>("refNo"));
           dateReservation.setCellValueFactory(new PropertyValueFactory<>("formattedReservation"));
           datePaid.setCellValueFactory(new PropertyValueFactory<>("datePaid"));
           remarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));

           reservationListTableView.setItems(list);
           conn.close();
       }catch (SQLException ex){

       }finally {
           if (conn != null){
               try{
                   conn.close();
               }catch (SQLException ex){
                   ex.printStackTrace();
               }
           }
       }
    }


    @FXML
    void getItem(MouseEvent event) {
        ObservableList<Integer> selectedIndices = reservationListTableView.getSelectionModel().getSelectedIndices();

        for (int index : selectedIndices) {
            // Get the data from the selected row
            roomNumber = roomNo.getCellData(index);
            rType = roomType.getCellData(index);
            first = firstName.getCellData(index);
            middle = middleName.getCellData(index);
            last = lastName.getCellData(index);
            suffix2 = suffixColumn.getCellData(index);
            hc = headcount.getCellData(index);
            hours = usageHours.getCellData(index);
            rPrice = roomPrice.getCellData(index);
            firstPay = initialPay.getCellData(index);
            remainingBal = balance.getCellData(index);
            mop = MOP.getCellData(index);
            refNo = RefNo.getCellData(index);
            dateReserve = String.valueOf(dateReservation.getCellData(index));
            datepay = String.valueOf(datePaid.getCellData(index));
            notes = remarks.getCellData(index);
            user_id = userid.getCellData(index);

        }
    }
    void archiveSelectedRows() {
        List<reservations> selectedItems = reservationListTableView.getItems().filtered(reservations::isSelected);

        for (reservations item : selectedItems) {
            // Retrieve the column values from the item
            roomNumber = item.getRoomNo();
            rType = item.getRoomType();
            first = item.getFirstName();
            middle = item.getMiddleName();
            last = item.getLastName();
            suffix2 = item.getSuffix();
            hc = item.getHeadcount();
            hours = item.getHours();
            rPrice = item.getTotalPrice();
            firstPay = item.getInitialPay();
            remainingBal = item.getBalance();
            mop = item.getMOP();
            refNo = item.getRefNo();
            dateReserve = String.valueOf(item.getDateReserved());
            datepay = String.valueOf(item.getDatePaid());
            notes = item.getRemarks();
            user_id = item.getUserid();
            // ... retrieve other column values from the item

            // Execute your archive logic for the item
            try {
                archive();
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle the exception if needed
            }
        }
    }

    @FXML
    public void ifArchive() throws SQLException {
        if(selectAllCheckBox.isSelected()){
            archiveSelectedRows();
        }else{
            archive();
        }
    }

    //Archiving/Cancelling the record
    public void archive() throws SQLException {
        //Update json f=

        String fullname = first +" " + middle + " " + last + " " + suffix2;

        // Define input and output formatters
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Get the date string from the table
        String dateReserve = String.valueOf(dateReservation.getCellData(index));

        // Parse the date string using inputFormatter
        LocalDateTime dateTime = LocalDateTime.parse(dateReserve.replace(" ", " "), inputFormatter);

        // Format the date/time as a string using outputFormatter
        String formattedDateTime = dateTime.format(outputFormatter);


        conn = connect.getConnection();
        try {
            String deletequery = "DELETE FROM reservation WHERE room_No = ?";
            String transacquery = "INSERT INTO transaction (user_id, room_No, room_Type, name, checkin, total_hours, extended_Hours, timeLeft, total_price, initial_payment, final_payment, total_paid, Mode_of_payment, Mode_of_payment2, Ref_No, Ref_No2, type_of_transaction, status, remarks) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String updQuery = "UPDATE rooms SET status = 'Available' WHERE room_No = ?";
            String updateStatus = "UPDATE reservation SET status = 'Voided' WHERE room_No = ?";

            PreparedStatement delete = conn.prepareStatement(deletequery);
            PreparedStatement statement = conn.prepareStatement(transacquery);
            PreparedStatement upd = conn.prepareStatement(updQuery);
            PreparedStatement updStatus = conn.prepareStatement(updateStatus);

            //from web
            if(mobile_id == 0){
                statement.setInt(1, user_id);
                statement.setInt(2, roomNumber);
                statement.setString(3, rType);
                statement.setString(4, fullname);
                statement.setString(5, formattedDateTime);
                statement.setInt(6, hours);
                statement.setInt(7, 0);
                statement.setString(8, String.valueOf(0));
                statement.setInt(9, rPrice);
                statement.setInt(10, firstPay);
                statement.setInt(11, 0);
                statement.setInt(12, firstPay);
                statement.setString(13, mop);
                statement.setString(14, "N/A");
                statement.setString(15, refNo);
                statement.setString(16, "N/A");
                statement.setString(17, "Reservation");
                statement.setString(18, "Voided");
                statement.setString(19, notes);
                statement.executeUpdate();

                delete.setInt(1, roomNumber);
                delete.executeUpdate();

                upd.setInt(1, roomNumber);
                upd.executeUpdate();
            }else {
                reject();

                statement.setInt(1, user_id);
                statement.setInt(2, roomNumber);
                statement.setString(3, rType);
                statement.setString(4, fullname);
                statement.setString(5, formattedDateTime);
                statement.setInt(6,  hours);
                statement.setInt(7, 0);
                statement.setString(8, String.valueOf(0));
                statement.setInt(9, rPrice);
                statement.setInt(10, firstPay);
                statement.setInt(11, 0);
                statement.setInt(12, firstPay);
                statement.setString(13, mop);
                statement.setString(14, "N/A");
                statement.setString(15, refNo);
                statement.setString(16, "N/A");
                statement.setString(17, "Reservation");
                statement.setString(18, "Voided");
                statement.setString(19, notes);
                statement.executeUpdate();

                updStatus.setInt(1, roomNumber);
                updStatus.executeUpdate();

                upd.setInt(1, roomNumber);
                upd.executeUpdate();
            }

        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

        loadReservationTable();
    }

        //If the guest has arrived
    public void checkIn() throws SQLException {
        conn = connect.getConnection();

        int seconds = hours * 3600;

        int change = firstPay - rPrice;
        if(change <= 0){
            change = 0;
        }

            try{
                String updQuery = "UPDATE rooms r SET r.status = (SELECT g.status FROM guest g WHERE g.room_No = r.room_No LIMIT 1) WHERE EXISTS (SELECT 1 FROM guest g WHERE g.room_No = r.room_No)";
                String guestQuery = "INSERT INTO `guest`(`room_No`, `room_type`, `status`, `firstName`, `middleName`,`lastName`, `suffix`,`usage_hours`, `check_in`, `timer`, `balance` ,`type`, `remarks` ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
                String paymentQuery = "INSERT INTO `payment`(`customer_ID`, `Ref_No`, `totalPrice`, `initial_payment`, `amount_change`, `MOP`,`date_paid`) VALUES (?,?,?,?,?,?,?)";
                String deleteQuery = "DELETE FROM reservation WHERE room_No = ?";

                PreparedStatement upd = conn.prepareStatement(updQuery);
                PreparedStatement delete = conn.prepareStatement(deleteQuery);
                PreparedStatement guestPs = conn.prepareStatement(guestQuery, Statement.RETURN_GENERATED_KEYS);

                guestPs.setString(1, String.valueOf(roomNumber));
                guestPs.setString(2, rType);
                guestPs.setString(3, "Occupied");
                guestPs.setString(4, first);
                guestPs.setString(5, middle);
                guestPs.setString(6, last);
                guestPs.setString(7, suffix2);
                guestPs.setString(8, String.valueOf(hours));
                guestPs.setString(9, LocalDate.now().toString());
                guestPs.setInt(10, seconds);
                guestPs.setDouble(11, remainingBal);
                guestPs.setString(12, "Reservation");
                guestPs.setString(13, notes);
                guestPs.executeUpdate();
                upd.executeUpdate();

                ResultSet guestKeys = guestPs.getGeneratedKeys();

                delete.setInt(1, roomNumber);
                delete.executeUpdate();

                if (guestKeys.next()) {
                    int customerId = guestKeys.getInt(1);
                    PreparedStatement paymentPs = conn.prepareStatement(paymentQuery);
                    paymentPs.setInt(1, customerId);
                    paymentPs.setString(2, refNo);
                    paymentPs.setDouble(3, rPrice);
                    paymentPs.setDouble(4, firstPay);
                    paymentPs.setDouble(5, change);
                    paymentPs.setString(6, mop);
                    paymentPs.setString(7, LocalDate.now().toString());
                    paymentPs.executeUpdate();
                }
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
            loadReservationTable();
            conn.close();
    }



    @FXML
    void refreshTable(MouseEvent event) throws SQLException {
        loadReservationTable();
    }

    void reject() throws IOException {
        int id = mobile_id; // replace with the actual value
        String status = "Voided"; // replace with the actual value
        String urlString = "https://transudatory-detent.000webhostapp.com/update-status.php";
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write("id=" + id + "&status=" + status + "&room_no=" + roomNumber);
        writer.flush();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder responseBuilder = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                responseBuilder.append(inputLine);
            }
            in.close();

            String response = responseBuilder.toString();
            System.out.println("Response: " + response);
        }
    }

    public void approveInQueue() throws SQLException {
        conn = connect.getConnection();
        try {
            int id = mobile_id; // replace with the actual value
            String status = "Approved"; // replace with the actual value
            String urlString = "https://transudatory-detent.000webhostapp.com/update-status.php";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write("id=" + id + "&status=" + status + "&room_no=" + roomNumber);
            writer.flush();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder responseBuilder = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    responseBuilder.append(inputLine);
                }
                in.close();

                String response = responseBuilder.toString();
                System.out.println("Response: " + response);
            } else {
                System.out.println("HTTP request failed with response code: " + responseCode);
            }

            String updateStatusQuery = "UPDATE reservation SET status = 'Approved' WHERE room_No = ?;";
            String updateRoomStatus = "UPDATE rooms SET status = 'Reserved' WHERE room_No = ?;";
            PreparedStatement statusQuery = conn.prepareStatement(updateStatusQuery);
            PreparedStatement roomStatusUpdate = conn.prepareStatement(updateRoomStatus);

            statusQuery.setInt(1, roomNumber);
            statusQuery.executeUpdate();

            roomStatusUpdate.setInt(1, roomNumber);
            roomStatusUpdate.executeUpdate();


        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
        loadReservationTable();
        conn.close();
    }
}