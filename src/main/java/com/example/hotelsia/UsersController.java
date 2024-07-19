package com.example.hotelsia;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class UsersController implements Initializable {
    @FXML
    private TableColumn<users, Integer> customerID;

    @FXML
    private TableColumn<users, LocalDateTime> dateCreate;

    @FXML
    private TableColumn<users, String> email;

    @FXML
    private TableColumn<users, String> firstName;

    @FXML
    private TableColumn<users, String> lastName;

    @FXML
    private TableColumn<users, String> middleName;

    @FXML
    private TableColumn<users, Integer> phone;

    @FXML
    private ImageView refreshBtn;

    @FXML
    private TextField searchField;

    @FXML
    private TableColumn<users, String> suffix;

    @FXML
    private TableView<users> usersTableView;

    private DbConnect connect;
    private Connection conn;

    private ObservableList<users> list;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadUsersTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterRooms(newValue);
        });
    }

    private void filterRooms(String searchText) {
        FilteredList<users> filteredData = new FilteredList<>(list, p -> true);

        if (searchText == null || searchText.isEmpty()) {
            usersTableView.setItems(list);
        } else {
            String lowerCaseSearchText = searchText.toLowerCase();

            filteredData.setPredicate(users  -> {
                if(users.getFirstName().toLowerCase().contains(lowerCaseSearchText)) {
                    return true; // match found in room number column
                } else if (users.getLastName().toLowerCase().contains(lowerCaseSearchText)) {
                    return true;
                } else if (Integer.toString(users.getCustomer_id()).contains(lowerCaseSearchText)) {
                    return true;
                }else if (users.getMiddleName().toLowerCase().contains(lowerCaseSearchText)) {
                    return true; // match found in name column
                }else if (users.getSuffix().contains(lowerCaseSearchText)) {
                    return true;
                }else if ((users.getDateCreated()).toString().contains(lowerCaseSearchText)) {
                    return true;
                }else if ((users.getEmail()).contains(lowerCaseSearchText)) {
                    return true;
                }else if ((users.getPhone()).contains(lowerCaseSearchText)) {
                    return true;
                }
                return false; // no match found
            });

            usersTableView.setItems(filteredData);
        }
    }


    public void loadUsersTable() throws SQLException {
        conn = connect.getConnection();
        list = FXCollections.observableArrayList();

        String query = "SELECT id, FirstName, MiddleName, LastName, Suffix, EmailAddress, PhoneNumber, DateCreated FROM user";

        ResultSet set = conn.createStatement().executeQuery(query);

        while (set.next()) {

            int userid = set.getInt("id");
            String first = set.getString("FirstName");
            String middle = set.getString("MiddleName");
            String last = set.getString("LastName");
            String suffixS = set.getString("Suffix");
            String emailad = set.getString("EmailAddress");
            String phoneNo = set.getString("PhoneNumber");
            LocalDateTime date = set.getTimestamp("DateCreated").toLocalDateTime();

            users user = new users(userid, first, middle, last, suffixS, emailad, phoneNo, date);
            list.add(user);

            customerID.setCellValueFactory(new PropertyValueFactory<>("customer_id"));
            firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            middleName.setCellValueFactory(new PropertyValueFactory<>("middleName"));
            lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            suffix.setCellValueFactory(new PropertyValueFactory<>("suffix"));
            phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
            email.setCellValueFactory(new PropertyValueFactory<>("email"));
            dateCreate.setCellValueFactory(new PropertyValueFactory<>("formattedDateCreated"));

            usersTableView.setItems(list);
        }
    }

    @FXML
    void refreshTable(MouseEvent event) throws SQLException {
        loadUsersTable();
    }
}
