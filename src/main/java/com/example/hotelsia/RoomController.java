package com.example.hotelsia;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.converter.CharacterStringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class RoomController implements Initializable {

    DataSingletonForRoom data = DataSingletonForRoom.getInstance();
    DataSingleton data2 = DataSingleton.getInstance();
    @FXML
    private ChoiceBox<String> roomNumber;

    @FXML
    private ChoiceBox<String> roomType;

    @FXML
    private ChoiceBox<String> usageHoursChoice;


    @FXML
    private ChoiceBox<String> transactionType;

    @FXML
    private TextField payment;

    @FXML
    private TextField roomPrice;
    @FXML
    private TextField suffixId;
    @FXML
    private TextField middleNameId;

    @FXML
    private TextField lastNameId;

    @FXML
    private TextField firstNameId;


    @FXML
    private TextField searchField;

    @FXML
    private TextField changeAmount;

    @FXML
    private TextField headCount;

    @FXML
    private TableView<rooms> roomTableView;
    @FXML
    private TableColumn<rooms, Integer> col_room_no;

    @FXML
    private TableColumn<rooms, String> col_room_type;

    @FXML
    private TableColumn<rooms, Integer> col_capacity;

    @FXML
    private TableColumn<rooms, String> col_status;

    @FXML
    private DatePicker reservationDate;

    private Connection conn;
    private ObservableList<rooms> list;

    private SortedList<rooms> sortedList;
    private DbConnect connect;

    private String selectedroomType = null;

    //for the texboxes/input

    int basePrice = 0;

    int capacity = 0;
    private String usage_hours;

    private String showAllQuery = "SELECT * FROM `rooms` WHERE 1";
    private String showAllQuery2 = "SELECT * FROM `rooms` WHERE 1 AND room_Type = '"+ selectedroomType + "'";
    @FXML
    private CheckBox showAllData;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connect = new DbConnect();

        // Initialize sortedList with the original data source
        if (list != null) {
            sortedList = new SortedList<>(list);
            sortedList.comparatorProperty().bind(roomTableView.comparatorProperty());
            roomTableView.setItems(sortedList);
        }

        // Create an ObservableList of items
        ObservableList<String> items = FXCollections.observableArrayList("1hr", "2hrs", "3hrs", "4hrs", "5hrs", "6hrs", "7hrs", "8hrs");
        ObservableList<String> reservationHours = FXCollections.observableArrayList("6hrs", "7hrs", "8hrs");
        ObservableList<String> type = FXCollections.observableArrayList("Walk In" , "Reservation");

        // Add the items to the ChoiceBox
        usageHoursChoice.setItems(items);
        transactionType.setItems(type);

        try {
            populateRoomTypeChoiceBox();
            loadRoomTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterRooms(newValue);
        });

        transactionType.valueProperty().addListener((observable, oldValue, newValue) -> {
                    // Check if the new value is not null
                    if (newValue != null) {
                        if (newValue.equals("Walk In")) {
                            reservationDate.setDisable(true);
                            usageHoursChoice.setItems(items);
                            data.setTransacType("Walk In");
                        } else if (newValue.equals("Reservation")) {
                            reservationDate.setDisable(false);
                            usageHoursChoice.setItems(reservationHours);
                            data.setTransacType("Reservation");
                        }
                    }
                });


        usageHoursChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Check if the new value is not null
            if (newValue != null) {
                if(newValue.equals("1hr")){
                    try {
                        pricing();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                   basePrice =  basePrice + 60;
                    usage_hours = "1";
                    roomPrice.setText(String.valueOf(basePrice));
                } else if (newValue.equals("2hrs")) {
                    try {
                        pricing();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    basePrice = basePrice + 120;
                    usage_hours = "2";
                    roomPrice.setText(String.valueOf(basePrice));
                }else if (newValue.equals("3hrs")) {
                    try {
                        pricing();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    basePrice = basePrice +  180;
                    usage_hours = "3";
                    roomPrice.setText(String.valueOf(basePrice));
                }else if (newValue.equals("4hrs")) {
                    try {
                        pricing();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    basePrice =  basePrice + 240;
                    usage_hours = "4";
                    roomPrice.setText(String.valueOf(basePrice));
                }else if (newValue.equals("5hrs")) {
                    try {
                        pricing();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    basePrice = basePrice + 300;
                    usage_hours = "5";
                    roomPrice.setText(String.valueOf(basePrice));
                }else if (newValue.equals("6hrs")) {
                    try {
                        pricing();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    basePrice =  basePrice + 370;
                    usage_hours = "6";
                    roomPrice.setText(String.valueOf(basePrice));
                }else if (newValue.equals("7hrs")) {
                    try {
                        pricing();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    usage_hours = "7";
                    basePrice =  basePrice + 440;
                    roomPrice.setText(String.valueOf(basePrice));
                }else if (newValue.equals("8hrs")) {
                    try {
                        pricing();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    usage_hours = "8";
                    basePrice =  basePrice + 510;
                    roomPrice.setText(String.valueOf(basePrice));
                }

            }
        });

        //Prevent textfields from receiving letters
        // Create a TextFormatter that only allows alphabetical characters
        TextFormatter<String> firstNameFormat = new TextFormatter<>(change -> {
            if (change.isAdded() && !change.getControlNewText().matches("[a-zA-Z\\- ]*")) {
                return null;
            }
            return change;
        });
        TextFormatter<String> middleNameFormat = new TextFormatter<>(change -> {
            if (change.isAdded() && !change.getControlNewText().matches("[a-zA-Z\\- ]*")) {
                return null;
            }
            return change;
        });
        TextFormatter<String> lastNameFormat = new TextFormatter<>(change -> {
            if (change.isAdded() && !change.getControlNewText().matches("[a-zA-Z\\- ]*")) {
                return null;
            }
            return change;
        });

        // Set the TextFormatter on the TextField
        firstNameId.setTextFormatter(firstNameFormat);
        lastNameId.setTextFormatter(middleNameFormat);
        middleNameId.setTextFormatter(lastNameFormat);

        TextFormatter<String> forSuffix = new TextFormatter<>(change -> {
            if (change.isAdded() && !change.getControlNewText().matches("[a-zA-Z/-]*")) {
                return null;
            }
            return change;
        });

        suffixId.setTextFormatter(forSuffix);

        // Create a TextFormatter that only allows numeric characters
        TextFormatter<String> numericFormatter = new TextFormatter<>(change -> {
            if (change.isAdded() && !change.getControlNewText().matches("[0-9]*")) {
                return null;
            }
            return change;
        });
        headCount.setTextFormatter(numericFormatter);
    }

    private void filterRooms(String searchText) {
        FilteredList<rooms> filteredData = new FilteredList<>(list, p -> true);

        if (searchText == null || searchText.isEmpty()) {
            roomTableView.setItems(sortedList); // restore original sorted list
        } else {
            String lowerCaseSearchText = searchText.toLowerCase();

            filteredData.setPredicate(room -> {
                if (Integer.toString(room.getRoom_No()).contains(lowerCaseSearchText)) {
                    return true; // match found in room number column
                } else if (room.getRoom_type().toLowerCase().contains(lowerCaseSearchText)) {
                    return true; // match found in room type column
                } else if (Integer.toString(room.getCapacity()).contains(lowerCaseSearchText)) {
                    return true; // match found in capacity column
                } else if (room.getStatus().toLowerCase().contains(lowerCaseSearchText)) {
                    return true; // match found in status column
                }
                return false; // no match found
            });

            SortedList<rooms> newSortedList = new SortedList<>(filteredData);
            newSortedList.comparatorProperty().bind(roomTableView.comparatorProperty());
            roomTableView.setItems(newSortedList);
        }
    }



    public void choiceAction(ActionEvent event) {
         selectedroomType = roomType.getValue();
        if (selectedroomType == null) {
            System.out.println("No room type selected");
        } else {
            System.out.println("Selected room type: " + selectedroomType);
            if (showAllData.isSelected()) {
                try {
                    loadTableByCategory(selectedroomType);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    loadTableAvailableOnly(selectedroomType);
                    populateRoomNumberChoiceBox();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void refreshTable(MouseEvent event) throws SQLException {
        showAllData.setSelected(false);
        populateRoomTypeChoiceBox();
        loadRoomTable();
    }

    public void populateRoomTypeChoiceBox() throws SQLException {
        List<String> roomTypes = new ArrayList<>();
        try {
            conn = connect.getConnection();
            String query = "SELECT DISTINCT room_Type FROM rooms WHERE status = 'Available'";
            ResultSet rs = conn.createStatement().executeQuery(query);
            while (rs.next()) {
                roomTypes.add(rs.getString("room_Type"));
            }
            roomType.getItems().addAll(roomTypes);
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void pricing() throws SQLException{
        String selectedRoomType = roomType.getValue();
        conn = connect.getConnection();
        if(selectedRoomType == null){
            System.out.println("No room type selected");
        } else {
            // Perform a database query to retrieve the base price for the selected room type
            PreparedStatement stmt = conn.prepareStatement("SELECT base_price, capacity FROM pricing WHERE room_Type = ?");
            stmt.setString(1, selectedRoomType);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                basePrice = rs.getInt("base_price");
                capacity = rs.getInt("capacity");
                // Set the value of the roomPrice TextField based on the retrieved base price
                roomPrice.setText(String.valueOf(basePrice));
            } else {
                System.out.println("No base price found for selected room type: " + selectedRoomType);
            }
            rs.close();
            stmt.close();
        }
    }
    public void populateRoomNumberChoiceBox() throws SQLException {
        String selectedRoomType = roomType.getValue();
        if (selectedRoomType == null) {
            System.out.println("No room type selected");
            return;
        }
        List<String> roomNumbers = new ArrayList<>();
        try {
            conn = connect.getConnection();
            String query = "SELECT room_No FROM rooms WHERE status = 'Available' AND room_Type = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, selectedRoomType);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                roomNumbers.add(rs.getString("room_No"));
            }
            roomNumber.getItems().clear();
            roomNumber.getItems().addAll(roomNumbers);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    private void loadTableAvailableOnly(String roomType) throws SQLException{
        list = FXCollections.observableArrayList();
        String query = "SELECT * FROM `rooms` WHERE status = 'Available' AND room_Type = '"+ roomType + "'";
        conn = connect.getConnection();


        ResultSet set = conn.createStatement().executeQuery(query);

        while (set.next()){
            rooms room = new rooms();
            room.setRoom_No(set.getInt("room_No"));
            room.setRoom_type(set.getString("room_Type"));
            room.setCapacity(set.getInt("capacity"));
            room.setStatus(set.getString("status"));

            list.add(room);
        }
        col_room_no.setCellValueFactory(new PropertyValueFactory<>("room_No"));
        col_room_type.setCellValueFactory(new PropertyValueFactory<>("room_type"));
        col_capacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        col_status.setCellValueFactory(new PropertyValueFactory<>("status"));

        roomTableView.setItems(list);
    }

    private void loadTableByCategory(String roomType) throws SQLException{
        list = FXCollections.observableArrayList();
        String query = "SELECT * FROM `rooms` WHERE 1 AND room_Type = '"+ roomType + "'";
        conn = connect.getConnection();


        ResultSet set = conn.createStatement().executeQuery(query);

        while (set.next()){
            rooms room = new rooms();
            room.setRoom_No(set.getInt("room_No"));
            room.setRoom_type(set.getString("room_Type"));
            room.setCapacity(set.getInt("capacity"));
            room.setStatus(set.getString("status"));

            list.add(room);
        }
        col_room_no.setCellValueFactory(new PropertyValueFactory<>("room_No"));
        col_room_type.setCellValueFactory(new PropertyValueFactory<>("room_type"));
        col_capacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        col_status.setCellValueFactory(new PropertyValueFactory<>("status"));

        roomTableView.setItems(list);
    }

    public void checkboxAction(ActionEvent event){
        if(showAllData.isSelected()){
            if(selectedroomType == null){
                try {
                    loadRoomTableShowAll(showAllQuery);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }else{
                try {
                    loadRoomTableShowAll(showAllQuery2);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }else{
            if(selectedroomType == null){
                try {
                    loadRoomTable(); //Available only
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }else{
                try {
                    loadTableAvailableOnly(selectedroomType);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if(selectedroomType != null){
            if(showAllData.isSelected()){
                try {
                    loadTableByCategory(selectedroomType);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }
    private void loadRoomTableShowAll(String showAll) throws SQLException{
        list = FXCollections.observableArrayList();
        conn = connect.getConnection();


        ResultSet set = conn.createStatement().executeQuery(showAll);

        while (set.next()){
            rooms room = new rooms();
            room.setRoom_No(set.getInt("room_No"));
            room.setRoom_type(set.getString("room_Type"));
            room.setCapacity(set.getInt("capacity"));
            room.setStatus(set.getString("status"));

            list.add(room);
        }
        col_room_no.setCellValueFactory(new PropertyValueFactory<>("room_No"));
        col_room_type.setCellValueFactory(new PropertyValueFactory<>("room_type"));
        col_capacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        col_status.setCellValueFactory(new PropertyValueFactory<>("status"));

        roomTableView.setItems(list);
    }
    private void loadRoomTable() throws SQLException {
        list = FXCollections.observableArrayList();
        String query="SELECT * FROM `rooms` WHERE status = 'Available'";

        conn = connect.getConnection();
        ResultSet set = conn.createStatement().executeQuery(query);

        while (set.next()){
            rooms room = new rooms();
            room.setRoom_No(set.getInt("room_No"));
            room.setRoom_type(set.getString("room_Type"));
            room.setCapacity(set.getInt("capacity"));
            room.setStatus(set.getString("status"));

            list.add(room);
        }
        col_room_no.setCellValueFactory(new PropertyValueFactory<>("room_No"));
        col_room_type.setCellValueFactory(new PropertyValueFactory<>("room_type"));
        col_capacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        col_status.setCellValueFactory(new PropertyValueFactory<>("status"));

        roomTableView.setItems(list);
    }

    public void submitGuestData() throws SQLException {
        String selectedRoomType = roomType.getValue();
        String selectedTransac = transactionType.getValue();
        String roomNo = roomNumber.getValue();
        String roomPrc = roomPrice.getText().trim();
        usageHoursChoice.getValue();
        String checkInDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM d, uuuu hh:mm"));
        String firstName = firstNameId.getText().trim();
        String middleName = middleNameId.getText().trim();
        String lastName = lastNameId.getText().trim();
        String suffix = suffixId.getText().trim();
        String hc = headCount.getText().trim();

        int usageHours = 0; // Initialize to a default value
        if (usage_hours != null && !usage_hours.isEmpty()) {
            usageHours = Integer.parseInt(usage_hours);
        }
        int totalSeconds = usageHours * 3600;

        final int[] initialTimeRemaining = {totalSeconds};

// Countdown timer
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            initialTimeRemaining[0]--;
            try {
                PreparedStatement updateTimer = conn.prepareStatement("UPDATE guest SET timer=? WHERE room_No=? AND status='Occupied'");
                updateTimer.setInt(1, initialTimeRemaining[0]);
                updateTimer.setString(2, roomNo);
                updateTimer.executeUpdate();
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        if (firstName.isBlank() || middleName.isBlank() || lastName.isBlank() || suffix.isBlank() || roomNo.isBlank() || roomPrc.isBlank() || usage_hours == null || selectedRoomType == null || selectedTransac == null ||headCount == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please fill all DATA");
            alert.showAndWait();
        } else {
            data.setFirstName(firstName);
            data.setLastName(lastName);
            data.setRoomType(selectedRoomType);
            data.setRoomNumber(Integer.parseInt(roomNo));
            data.setRoomPrice(Integer.parseInt(roomPrice.getText().trim()));
            data.setCheckInDate(checkInDate);
            data.setHours(usage_hours);
            data.setSeconds(totalSeconds);
            data.setSuffix(suffix);
            data.setMiddleName(middleNameId.getText().trim());
            LocalDate reservedDate = reservationDate.getValue();
            data.setDateReserved(reservedDate);
            data.setHeadCount(Integer.parseInt(hc));

            if(Integer.parseInt(headCount.getText()) > capacity) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Capacity limit exceeded.");
                alert.showAndWait();
            }else{
                try {
                    // Load the FXML file and create a new stage
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("balanceCheck.fxml"));
                    Stage stage = new Stage();
                    stage.initStyle(StageStyle.UTILITY);

                    // Show the stage and wait for it to be closed
                    stage.setScene(new Scene(loader.load()));
                    stage.setResizable(false);
                    stage.showAndWait();

                    clearForm();
                    if (selectedroomType == null) {
                        System.out.println("Nothing selected");
                    } else {
                        System.out.println("Selected radio button: " + selectedroomType);
                        if(showAllData.isSelected()){
                            try {
                                loadTableByCategory(selectedroomType);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }else{
                            try {
                                loadTableAvailableOnly(selectedroomType);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void clearForm(){
        roomType.getSelectionModel().clearSelection();
        headCount.clear();
        transactionType.getSelectionModel().clearSelection();
        firstNameId.clear();
        middleNameId.clear();
        lastNameId.clear();
        suffixId.clear();
        roomNumber.getSelectionModel().clearSelection();
        roomPrice.clear();
        usageHoursChoice.getSelectionModel().clearSelection();
        showAllData.setSelected(false);
        try {
            loadRoomTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
