package com.example.hotelsia;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;


import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class adminRoom implements Initializable {

    @FXML
    private BorderPane bp;

    @FXML
    private Pane ap;

    @FXML
    private Button homebtn;

    @FXML
    private Button reservationBtn;

    @FXML
    private Button transactionBtn;

    @FXML
    private Button usersBtn;


    @FXML
    private TableColumn<pricing, Integer> capacityPricing;

    @FXML
    private TableColumn<pricing, Integer> basePrice;

    @FXML
    private TableColumn<pricing, String> roomTypePricing;

    @FXML
    private TableView<pricing> pricingTableView;

    @FXML
    private TextField price;

    @FXML
    private TextField capacity;

    @FXML
    private TextField capacityPrice;

    @FXML
    private TextField roomNumber;


    @FXML
    private TextField roomTypePrice;


    @FXML
    private TableColumn<adminrooms, Integer> roomCapacity;

    @FXML
    private TableColumn<adminrooms, Integer> roomNo;

    @FXML
    private TableColumn<adminrooms, Integer> roomPrice;

    @FXML
    private TableColumn<adminrooms, String> roomStatus;

    @FXML
    private TableView<adminrooms> roomTableView;

    @FXML
    private TableColumn<adminrooms, String> roomType;

    @FXML
    private ChoiceBox<String> statuses;

    @FXML
    private ChoiceBox<String> roomTypeChoiceBox;

    @FXML
    private TextField searchField;

    private Connection conn;
    private ObservableList<adminrooms> list;

    private ObservableList<pricing> pricinglist;

    private DbConnect connect;

    private int index, indexPricing;

    private String status, placeholder;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
            connect = new DbConnect();
        try {
            loadRoomTable();
            loadPricingTable();
            selectRoomTypes();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //Check roomType choicebox
        setCapacity();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterRooms(newValue);
        });

        // Create an ObservableList of items
        ObservableList<String> state = FXCollections.observableArrayList("Available", "Under Maintenance");

        statuses.setItems(state);

        //format
        TextFormatter<String> forRoomType = new TextFormatter<>(change -> {
            if (change.isAdded() && !change.getControlNewText().matches("[a-zA-Z\\- ]*")) {
                return null;
            }
            return change;
        });
        // Create a TextFormatter that only allows numeric characters
        TextFormatter<String> forCapacityPrice = new TextFormatter<>(change -> {
            if (change.isAdded() && !change.getControlNewText().matches("[0-9]*")) {
                return null;
            }
            return change;
        });
        // Create a TextFormatter that only allows numeric characters
        TextFormatter<String> forRoomNumber = new TextFormatter<>(change -> {
            if (change.isAdded() && !change.getControlNewText().matches("[0-9]*")) {
                return null;
            }
            return change;
        });
        // Create a TextFormatter that only allows numeric characters
        TextFormatter<String> forPrice = new TextFormatter<>(change -> {
            if (change.isAdded() && !change.getControlNewText().matches("[0-9]*")) {
                return null;
            }
            return change;
        });

        roomNumber.setTextFormatter(forRoomNumber);
        price.setTextFormatter(forPrice);
        capacityPrice.setTextFormatter(forCapacityPrice);
        roomTypePrice.setTextFormatter(forRoomType);
    }

    private void filterRooms(String searchText) {
        FilteredList<adminrooms> filteredData = new FilteredList<>(list, p -> true);

        if (searchText == null || searchText.isEmpty()) {
            roomTableView.setItems(list);
        } else {
            String lowerCaseSearchText = searchText.toLowerCase();

            filteredData.setPredicate(adminrooms  -> {
                if (Integer.toString(adminrooms.getRoomNo()).contains(lowerCaseSearchText)) {
                    return true; // match found in room number column
                } else if (adminrooms.getRoomType().toLowerCase().contains(lowerCaseSearchText)) {
                    return true; // match found in room type column
                } else if (Integer.toString(adminrooms.getRoomPrice()).contains(lowerCaseSearchText)) {
                    return true; // match found in roomprice column
                } else if (Integer.toString(adminrooms.getCapacity()).contains(lowerCaseSearchText)) {
                    return true; // match found in roomprice column
                }
                return false; // no match found
            });

            roomTableView.setItems(filteredData);
        }
    }

    @FXML
    private void home(ActionEvent e){
        bp.setCenter(ap);
        homebtn.setStyle("-fx-background-color: #61c2a2");
        usersBtn.setStyle("-fx-background-color: #ffffff");
        reservationBtn.setStyle("-fx-background-color: #ffffff");
        transactionBtn.setStyle("-fx-background-color: #ffffff");
    }

    @FXML
    private void transactions(ActionEvent e) throws IOException {
        loadPage("Transactions");
        homebtn.setStyle("-fx-background-color: #ffffff");
        usersBtn.setStyle("-fx-background-color: #ffffff");
        reservationBtn.setStyle("-fx-background-color: #ffffff");
        transactionBtn.setStyle("-fx-background-color: #61c2a2");
    }
    @FXML
    private void reservation(ActionEvent e) throws IOException{
        loadPage("reservationList");
        homebtn.setStyle("-fx-background-color: #ffffff");
        usersBtn.setStyle("-fx-background-color: #ffffff");
        reservationBtn.setStyle("-fx-background-color: #61c2a2");
        transactionBtn.setStyle("-fx-background-color: #ffffff");
    }

    @FXML
    private void users(ActionEvent e) throws IOException{
        loadPage("users");
        homebtn.setStyle("-fx-background-color: #ffffff");
        usersBtn.setStyle("-fx-background-color: #61c2a2");
        reservationBtn.setStyle("-fx-background-color: #ffffff");
        transactionBtn.setStyle("-fx-background-color: #ffffff");
    }


    private void loadPage(String page) throws IOException {
        Parent root = null;

        root = FXMLLoader.load(getClass().getResource(page+".fxml"));
        bp.setCenter(root);

    }

    private void loadPricingTable() throws SQLException{
        pricinglist = FXCollections.observableArrayList();
        String query = "SELECT room_Type, base_price, capacity FROM pricing";
        conn = connect.getConnection();
        ResultSet set = conn.createStatement().executeQuery(query);

        while (set.next()) {

            String roomType = set.getString("room_Type");
            int basePricing = set.getInt("base_price");
            int capacity = set.getInt("capacity");

            pricing price = new pricing(basePricing, capacity, roomType);
            pricinglist.add(price);

            roomTypePricing.setCellValueFactory(new PropertyValueFactory<>("roomType"));
            capacityPricing.setCellValueFactory(new PropertyValueFactory<>("capacity"));
            basePrice.setCellValueFactory(new PropertyValueFactory<>("price"));

            pricingTableView.setItems(pricinglist);
        }
    }
    private void loadRoomTable() throws SQLException {
        list = FXCollections.observableArrayList();
        String query = "SELECT rooms.room_No, rooms.room_Type, pricing.base_price, rooms.capacity, rooms.status "
                + "FROM rooms "
                + "INNER JOIN pricing "
                + "ON rooms.room_Type = pricing.room_Type";

        conn = connect.getConnection();
        ResultSet set = conn.createStatement().executeQuery(query);

        while (set.next()){

            int roomNo = set.getInt("room_No");
            String roomType = set.getString("room_Type");
            int roomPrice = set.getInt("base_price");
            int roomCapacity = set.getInt("capacity");
            String roomAvailability = set.getString("status");

            adminrooms room = new adminrooms(roomNo, roomPrice, roomCapacity, roomType, roomAvailability);
            list.add(room);
        }
        roomNo.setCellValueFactory(new PropertyValueFactory<>("roomNo"));
        roomType.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        roomPrice.setCellValueFactory(new PropertyValueFactory<>("roomPrice"));
        roomCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        roomStatus.setCellValueFactory(new PropertyValueFactory<>("roomAvailability"));

        roomTableView.setItems(list);

    }

    @FXML
    void refreshTable(MouseEvent event) throws SQLException {
        loadRoomTable();
        selectRoomTypes();
        loadPricingTable();
    }

    @FXML
    void getItem(MouseEvent event) {

        index = roomTableView.getSelectionModel().getSelectedIndex();

        if (index <= -1){
            return;
        }
        roomNumber.setText(String.valueOf(roomNo.getCellData(index)));
        roomTypeChoiceBox.setValue(String.valueOf(roomType.getCellData(index)));
        capacity.setText(String.valueOf(roomCapacity.getCellData(index)));
    }

    @FXML
    void getItemPrice(MouseEvent event){
        indexPricing = pricingTableView.getSelectionModel().getSelectedIndex();

        if(indexPricing <= -1){
            return;
        }
        placeholder = roomTypePricing.getCellData(indexPricing);
        capacityPrice.setText(String.valueOf(capacityPricing.getCellData(indexPricing)));
        roomTypePrice.setText(String.valueOf(roomTypePricing.getCellData(indexPricing)));
        price.setText(String.valueOf(basePrice.getCellData(indexPricing)));

    }

    @FXML
    private void logOut(MouseEvent e) throws IOException {
        // Create a confirmation dialog box with custom text and options
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to log out?");
        confirmationDialog.setHeaderText(null);
        confirmationDialog.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        // Show the dialog box and wait for the user's response
        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                // Perform logout action
                System.out.println("Logging out...");
                try {
                    // Load the login page FXML file and create a new scene
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("start.fxml"));
                    AnchorPane root = loader.load();
                    Scene loginScene = new Scene(root, 1070, 653);

                    // Get the current stage and set the new scene
                    Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    stage.setScene(loginScene);
                    stage.setResizable(false);

                    // Center the window on the screen
                    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                    stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
                    stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
                } catch (Exception ex) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("An error occurred while logging out. Please try again.");
                    errorAlert.showAndWait();
                    ex.printStackTrace();
                }
            }
        });
    }

    public void selectRoomTypes() throws SQLException {
        conn = connect.getConnection();

        String query = "SELECT room_Type FROM pricing";
        PreparedStatement statement = conn.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();

        // Create a list to store the room types
        List<String> roomTypes = new ArrayList<>();

        // Fetch the room types from the result set
        while (resultSet.next()) {
            String roomType = resultSet.getString("room_Type");
            roomTypes.add(roomType);
        }

        roomTypeChoiceBox.setItems(FXCollections.observableArrayList(roomTypes));

        // Close the resources
        resultSet.close();
        statement.close();
        conn.close();
    }

    public void setCapacity(){
        roomTypeChoiceBox.setOnAction(event -> {
            String selectedRoomType = roomTypeChoiceBox.getValue();

            try {
                conn = connect.getConnection();
                String query = "SELECT capacity FROM pricing WHERE room_Type = ?";
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1, selectedRoomType);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    int capacityvalue = resultSet.getInt("capacity");
                    capacity.setText(String.valueOf(capacityvalue));
                } else {
                    // Handle the case when no capacity is found for the selected room type
                    capacity.setText("");
                }

                // Close the resources
                resultSet.close();
                statement.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle the SQL exception
            }
        });
    }

    public void updateRooms() throws SQLException {
        conn = connect.getConnection();
        try {
            String query = "UPDATE rooms SET room_Type = ?, capacity = ?, status = ? WHERE room_No = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, roomTypeChoiceBox.getSelectionModel().getSelectedItem());
            statement.setInt(2, Integer.parseInt(capacity.getText()));
            statement.setString(3, statuses.getSelectionModel().getSelectedItem());
            statement.setInt(4, Integer.parseInt(roomNumber.getText()));

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Room updated successfully.");
            } else {
                System.out.println("No room found with room number: " + roomNo);
            }

            statement.close();

        }  catch (SQLIntegrityConstraintViolationException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Duplicate found. Please enter a unique room number");
            alert.showAndWait();
            e.printStackTrace();
            // Handle the duplicate entry exception
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the SQL exception
        }
        loadRoomTable();
        conn.close();
    }

    public void createRooms() throws SQLException {
        conn = connect.getConnection();

        try {
            String query = "INSERT INTO rooms (room_No, room_Type, capacity, status) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, roomNumber.getText());
            statement.setString(2, roomTypeChoiceBox.getSelectionModel().getSelectedItem());
            statement.setInt(3, Integer.parseInt(capacity.getText()));
            statement.setString(4, statuses.getSelectionModel().getSelectedItem());

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("New room inserted successfully.");
            } else {
                System.out.println("Failed to insert new room.");
            }

            statement.close();
            conn.close();
        }  catch (SQLIntegrityConstraintViolationException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Already Exists.");
            alert.showAndWait();
            e.printStackTrace();
            // Handle the duplicate entry exception
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the SQL exception
        }
        loadRoomTable();
        conn.close();
    }

    public void createPrice() throws SQLException {
        conn = connect.getConnection();

        try {
            String query = "INSERT INTO pricing (room_Type, base_price, capacity) VALUES (?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, roomTypePrice.getText());
            statement.setInt(2, Integer.parseInt(price.getText()));
            statement.setInt(3, Integer.parseInt(capacityPrice.getText()));

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("New room type inserted successfully.");
            } else {
                System.out.println("Failed to insert new room type.");
            }

            statement.close();
            conn.close();
        } catch (SQLIntegrityConstraintViolationException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Duplicate found. Please enter a unique room type");
            alert.showAndWait();
            e.printStackTrace();
            // Handle the duplicate entry exception
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the SQL exception
        }
        loadPricingTable();
        selectRoomTypes();
        conn.close();
    }
    public void updatePrice() throws SQLException {
        conn = connect.getConnection();
        try {
            String query = "UPDATE pricing SET room_Type = ?, capacity = ?, base_price WHERE room_Type = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, roomTypeChoiceBox.getSelectionModel().getSelectedItem());
            statement.setInt(2, Integer.parseInt(capacityPrice.getText()));
            statement.setInt(3, Integer.parseInt(price.getText()));

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Price updated successfully.");
            } else {
                System.out.println("No room type found");
            }

            statement.close();

        }  catch (SQLIntegrityConstraintViolationException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Already Exists.");
            alert.showAndWait();
            e.printStackTrace();
            // Handle the duplicate entry exception
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the SQL exception
        }
        loadPricingTable();
        conn.close();
    }
}
