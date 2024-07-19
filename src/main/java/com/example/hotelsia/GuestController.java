package com.example.hotelsia;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.converter.CharacterStringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class GuestController implements Initializable {
    private String Full_name;
    private int roomNoUpdate;
    private int timeLeftUpdate;
    private String roomTypeUpdate;

    private String formattedTime;

    DataSingleton data = DataSingleton.getInstance();

    @FXML
    private TextField searchField;
    @FXML
    private TableView<guests> guestsTableView;

    @FXML
    private TableColumn<guests, String> refNoColumn;

    @FXML
    private TableColumn<guests, String> transacType;

    @FXML
    private TableColumn<guests, String> remarks;
    @FXML
    private TableColumn<guests, Integer> roomNo;

    @FXML
    private TableColumn<guests, Integer> headCount;

    @FXML
    private TableColumn<guests, Integer> remainingBalance;

    @FXML
    private TableColumn<guests, String> roomType;

    @FXML
    private TableColumn<guests, String> guestfullname;
    @FXML
    private TableColumn<guests, Integer> usageHours;

    @FXML
    private TableColumn<guests, Integer> extendedHours;

    @FXML
    private TableColumn<guests, Integer> roomPrice;
    @FXML
    private TableColumn<guests, Integer> amountOfPay;
    @FXML
    private TableColumn<guests, Integer> amountOfChange;
    @FXML
    private TableColumn<guests, LocalDate> checkIn;
    @FXML
    private TableColumn<guests, String> modeOfPayment;

    @FXML
    private TableColumn<guests, String> timeLeft;

    private Connection conn;
    private ObservableList<guests> list;
    private DbConnect connect;
    private int index;

    private static GuestController instance;
    private String roomStatus = "Available";

    String formattedDateTime;

    public GuestController(){
        instance = this;
    }

    public static GuestController getInstance(){
        return instance;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connect = new DbConnect();
        try {
            loadRoomTable();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterRooms(newValue);
        });


        timeLeft.setCellValueFactory(cellData -> {
            int timerValue = cellData.getValue().getTimer();
            int hours = timerValue / 3600;
            int minutes = (timerValue % 3600) / 60;
            String formattedTime = String.format("%02d:%02d", hours, minutes);
            SimpleStringProperty property = new SimpleStringProperty(formattedTime);
            return property;
        });

        timeLeft.setCellFactory(new Callback<TableColumn<guests, String>, TableCell<guests, String>>() {
            @Override
            public TableCell<guests, String> call(TableColumn<guests, String> column) {
                return new TableCell<guests, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                        if (!empty) {
                            String[] parts = item.split(":");
                            int hours = Integer.parseInt(parts[0]);
                            int minutes = Integer.parseInt(parts[1]);
                            int totalTime = hours * 3600 + minutes * 60;
                            if (totalTime <= 1740) {
                                setTextFill(Color.RED);
                            } else {
                                setTextFill(Color.BLACK);
                            }
                        }
                    }
                };
            }
        });

    }

    public void refreshTable(MouseEvent event) throws SQLException, IOException {
        loadRoomTable();
    }

    //Method for search function
    private void filterRooms(String searchText) {
        FilteredList<guests> filteredData = new FilteredList<>(list, p -> true);

        if (searchText == null || searchText.isEmpty()) {
            guestsTableView.setItems(list);
        } else {
            String lowerCaseSearchText = searchText.toLowerCase();

            filteredData.setPredicate(guest -> {
                if (Integer.toString(guest.getRoomNo()).contains(lowerCaseSearchText)) {
                    return true; // match found in room number column
                } else if (guest.getRoomType().toLowerCase().contains(lowerCaseSearchText)) {
                    return true; // match found in room type column
                } else if (Integer.toString(guest.getRoomPrice()).contains(lowerCaseSearchText)) {
                    return true; // match found in roomprice column
                } else if (guest.getFullName().toLowerCase().contains(lowerCaseSearchText)) {
                    return true; // match found in name column
                }else if (Integer.toString(guest.getUsageHours()).contains(lowerCaseSearchText)) {
                    return true; // match found in hours column
                }else if (guest.getCheckIn().toString().toLowerCase().contains(lowerCaseSearchText)) {
                    return true; // match found in check-in column
                }
                return false; // no match found
            });

            guestsTableView.setItems(filteredData);
        }
    }

    public void loadRoomTable() throws SQLException, IOException {
        list = FXCollections.observableArrayList();
        String query = "SELECT guest.room_No, guest.room_type, guest.headcount,CONCAT(guest.firstName, ' ', guest.middleName, ' ', guest.lastName, ' ', guest.suffix) AS name, guest.usage_hours, payment.Ref_No, payment.MOP, payment.totalPrice, payment.initial_payment, payment.amount_change, guest.extended_Hours, guest.check_in, guest.timer, guest.balance, guest.type, guest.remarks FROM guest LEFT JOIN payment ON guest.customer_ID = payment.customer_ID ORDER BY guest.timer ASC";

        conn = connect.getConnection();
        ResultSet set = conn.createStatement().executeQuery(query);

        while (set.next()){
            int roomNo = set.getInt("room_No");
            String roomType = set.getString("room_type");
            int hc = set.getInt("headcount");
            String name = set.getString("name");
            int usageHours = set.getInt("usage_hours");
            int roomPrice = set.getInt("totalPrice");
            int amountOfPay = set.getInt("initial_payment");
            int amountOfChange = set.getInt("amount_change");
            LocalDate checkIn = set.getDate("check_in").toLocalDate();
            int timer = set.getInt("timer");
            int balance = set.getInt("balance");
            int extendedHours = set.getInt("extended_Hours");
            String refNo = set.getString("Ref_No");
            String type = set.getString("type");
            String mop = set.getString("MOP");
            String notes = set.getString("remarks");

            guests guest = new guests(notes, roomNo, roomType, name, usageHours, hc, extendedHours, roomPrice, amountOfPay, amountOfChange,checkIn, timer, balance, refNo, type, mop);
            list.add(guest);
        }

        roomNo.setCellValueFactory(new PropertyValueFactory<>("roomNo"));
        roomType.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        guestfullname.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        usageHours.setCellValueFactory(new PropertyValueFactory<>("usageHours"));
        extendedHours.setCellValueFactory(new PropertyValueFactory<>("extendedHours"));
        roomPrice.setCellValueFactory(new PropertyValueFactory<>("roomPrice"));
        amountOfPay.setCellValueFactory(new PropertyValueFactory<>("amountOfPay"));
        amountOfChange.setCellValueFactory(new PropertyValueFactory<>("amountOfChange"));
        checkIn.setCellValueFactory(new PropertyValueFactory<>("checkIn"));
        remarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));
        timeLeft.setCellFactory(column -> new TableCell<guests, String>() {
            @Override
            protected void updateItem(String formattedTime, boolean empty) {
                super.updateItem(formattedTime, empty);
                if (empty || formattedTime == null) {
                    setText(null);
                } else {
                    setText(formattedTime);
                }
            }
        });
        timeLeft.setCellValueFactory(cellData -> {
            guests guest = cellData.getValue();
            int usageHours = guest.getUsageHours();
            int timer = guest.getTimer();
            int timeRemaining = Math.max(timer, 0);

            // Calculate the time left in hours and minutes
            int hoursLeft = timeRemaining / 3600;
            int minutesLeft = (timeRemaining % 3600) / 60;

            // Format the time left as "hh:mm"
            String formattedTime = String.format("%02d:%02d", hoursLeft, minutesLeft);

            // Return a SimpleStringProperty with the formatted time left
            return new SimpleStringProperty(formattedTime);
        });
        timeLeft.setCellFactory(new Callback<TableColumn<guests, String>, TableCell<guests, String>>() {
            @Override
            public TableCell<guests, String> call(TableColumn<guests, String> column) {
                return new TableCell<guests, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                        if (!empty) {
                            String[] parts = item.split(":");
                            int hours = Integer.parseInt(parts[0]);
                            int minutes = Integer.parseInt(parts[1]);
                            int totalTime = hours * 3600 + minutes * 60;
                            if (totalTime <= 1740) {
                                setTextFill(Color.RED);
                            } else {
                                setTextFill(Color.BLACK);
                            }
                        }
                    }
                };
            }
        });

        remainingBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
        refNoColumn.setCellValueFactory(new PropertyValueFactory<>("refNo"));
        modeOfPayment.setCellValueFactory(new PropertyValueFactory<>("mop"));
        transacType.setCellValueFactory(new PropertyValueFactory<>("type"));
        headCount.setCellValueFactory(new PropertyValueFactory<>("headcount"));

        guestsTableView.setItems(list);
        conn.close();
    }

    @FXML
    void getItem(MouseEvent event) throws SQLException {
        conn = connect.getConnection();

        index = guestsTableView.getSelectionModel().getSelectedIndex();

        if (index <= -1){
            return;
        }
        roomNoUpdate = roomNo.getCellData(index);
        // Retrieve the timer value from the selected row in the database
        ResultSet set = conn.createStatement().executeQuery("SELECT timer, check_in FROM guest WHERE room_No = '" + roomNoUpdate + "'");
        if (set.next()) {
            int timerValue = set.getInt("timer");
            String checkInDate = set.getString("check_in");
            // Do something with the timer value
            timeLeftUpdate =  timerValue;
            data.setCheckIn(checkInDate);
        }

        String inputString = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime dateTime = LocalDateTime.parse(inputString, inputFormatter);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        formattedDateTime = dateTime.format(outputFormatter);

        data.setCheckOut(formattedDateTime);

        Full_name = guestfullname.getCellData(index);
        roomTypeUpdate = roomType.getCellData(index);
        int roomPriceUpdate = roomPrice.getCellData(index);
        int amountPaid = amountOfPay.getCellData(index);
        int extended = extendedHours.getCellData(index);
        String transac = transacType.getCellData(index);
        int hours = usageHours.getCellData(index);
        int totalHours = hours + extended;
        int balance = remainingBalance.getCellData(index);
        String mop1 = modeOfPayment.getCellData(index);
        String refNo =  refNoColumn.getCellData(index);
        String timeRemaining = timeLeft.getCellData(index);
        String notes = remarks.getCellData(index);

        data.setRefNo1(refNo);
        data.setTimeRemaining(timeRemaining);
        data.setMop1(mop1);
        data.setHourcounter1(extended);
        data.setFullname(Full_name);
        data.setRoomNo(roomNoUpdate);
        data.setTimeLeft(timeLeftUpdate);
        data.setRoomType(roomTypeUpdate);
        data.setRoomPrice(roomPriceUpdate);
        data.setTotalHours(totalHours);
        data.setInitialPay(amountPaid);
        data.setTransacType(transac);
        data.setTotalRoomPrice(roomPriceUpdate + balance);
        data.setRemainingBal(balance);
        data.setNotes(notes);
        System.out.println(timeLeftUpdate);

    }

    public void extendTime () {
        int basePrice;
        // Perform a database query to retrieve the base price for the selected room type
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("SELECT base_price FROM pricing WHERE room_Type = ?");
            stmt.setString(1, roomTypeUpdate);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                basePrice = rs.getInt("base_price");
                data.setBasePrice(basePrice);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            // Load the FXML file and create a new stage
            FXMLLoader loader = new FXMLLoader(getClass().getResource("extend.fxml"));
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UTILITY);

            // Show the stage and wait for it to be closed
            stage.setScene(new Scene(loader.load()));
            stage.setResizable(false);
            stage.showAndWait();
            // Access the controller of the FXML file and call a method
            try {
                loadRoomTable();
                timeLeft.setCellFactory(new Callback<TableColumn<guests, String>, TableCell<guests, String>>() {
                    @Override
                    public TableCell<guests, String> call(TableColumn<guests, String> column) {
                        return new TableCell<guests, String>() {
                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                setText(item);
                                if (!empty) {
                                    String[] parts = item.split(":");
                                    int hours = Integer.parseInt(parts[0]);
                                    int minutes = Integer.parseInt(parts[1]);
                                    int totalTime = hours * 3600 + minutes * 60;
                                    if (totalTime <= 1740) {
                                        setTextFill(Color.RED);
                                    } else {
                                        setTextFill(Color.BLACK);
                                    }
                                }
                            }
                        };
                    }
                });
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (IOException ex) {

            System.err.println("Error loading FXML file: " + ex.getMessage());
        }


        int timeRemaining = Math.max(timeLeftUpdate, 0);

        // Calculate the time left in hours and minutes
        int hoursLeft = timeRemaining / 3600;
        int minutesLeft = (timeRemaining % 3600) / 60;

        // Format the time left as "hh:mm"
        formattedTime = String.format("%02d:%02d", hoursLeft, minutesLeft);

        data.setFormattedTime(formattedTime);

    }

    @FXML
    public void checkOut() throws SQLException, IOException {
        conn = connect.getConnection();
        String query = "SELECT `balance` FROM `guest` WHERE `room_No` ='" + roomNoUpdate + "'";
        PreparedStatement bal = conn.prepareStatement(query);
        ResultSet rs = bal.executeQuery();
        if(rs.next()){
            int remainingBalance = rs.getInt("balance");
            data.setAddtPay(remainingBalance);
        }
        conn.close();

        if(data.getAddtPay() > 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Please confirm details.");
            // Get the DialogPane associated with the Aleadmin  rt
            DialogPane dialogPane = alert.getDialogPane();


            // Set the content text
            alert.setContentText("We have detected that this guest has unpaid balances.");
            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(okButton);

            Optional<ButtonType> result = alert.showAndWait();

            if (((Optional<?>) result).isPresent() && result.get() == okButton) {
                // Load the FXML file and create a new stage
                FXMLLoader loader = new FXMLLoader(getClass().getResource("checkOutInvoice.fxml"));
                Stage confirmationStage = new Stage();
                confirmationStage.setResizable(false);
                confirmationStage.initStyle(StageStyle.UTILITY);
                confirmationStage.setScene(new Scene(loader.load()));

                // Show the stage and wait for it to be closed
                confirmationStage.showAndWait();

                loadRoomTable();
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Please confirm details.");
            // Get the DialogPane associated with the Alert
            DialogPane dialogPane = alert.getDialogPane();


            // Set the content text
            alert.setContentText("Are you sure that this transaction is completed?");
            ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
            alert.getButtonTypes().setAll(yesButton, noButton);

            Optional<ButtonType> result = alert.showAndWait();

            if (((Optional<?>) result).isPresent() && result.get() == yesButton) {
                // Load the FXML file and create a new stage
                FXMLLoader loader = new FXMLLoader(getClass().getResource("receipt.fxml"));
                Stage confirmationStage = new Stage();
                confirmationStage.setResizable(false);
                confirmationStage.initStyle(StageStyle.UTILITY);
                confirmationStage.setScene(new Scene(loader.load()));

                // Show the stage and wait for it to be closed
                confirmationStage.showAndWait();

                //Submit to transactions
                conn = connect.getConnection();
                String delete = "DELETE FROM guest WHERE room_No = ?";
                String transacquery = "INSERT INTO transaction (room_No, room_Type, name, checkin, checkout, total_hours, extended_Hours, timeLeft, total_price, initial_payment, final_payment, total_paid, Mode_of_payment, Mode_of_payment2, Ref_No, Ref_No2, type_of_transaction, status, remarks) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                String updQuery = "UPDATE rooms SET status = 'Available' WHERE room_No = ?";
                String updReservation = "UPDATE reservation SET status = 'Done' WHERE room_No = ?";

                PreparedStatement upd = conn.prepareStatement(updQuery);
                PreparedStatement statement = conn.prepareStatement(transacquery);
                PreparedStatement deleteStatement = conn.prepareStatement(delete);
                PreparedStatement reservationStatus = conn.prepareStatement(updReservation);

                statement.setInt(1, data.getRoomNo());
                statement.setString(2, data.getRoomType());
                statement.setString(3, data.getFullname());
                statement.setString(4, data.getCheckIn());
                statement.setString(5, formattedDateTime);
                statement.setInt(6, data.getTotalHours());
                statement.setInt(7, data.getHourcounter1());
                statement.setString(8, data.getTimeRemaining());
                statement.setInt(9, data.getTotalRoomPrice());
                statement.setInt(10, data.getInitialPay());
                statement.setInt(11, 0);
                statement.setInt(12, data.getInitialPay());
                statement.setString(13, data.getMop1());
                statement.setString(14, "N/A");
                statement.setString(15, data.getRefNo1());
                statement.setString(16, "N/A");
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
                loadRoomTable();
                data.clearData();
            }else{
                alert.close();
            }
        }
    }
}
