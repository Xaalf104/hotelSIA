package com.example.hotelsia;

import com.mysql.cj.jdbc.result.ResultSetMetaData;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

public class TransactionController implements Initializable {

    @FXML
    private TableColumn<transactions, String> timeLeft;
    @FXML
    private TableColumn<transactions, Integer> MOP1;

    @FXML
    private TableColumn<transactions, Integer> MOP2;

    @FXML
    private TableColumn<transactions, LocalDateTime> checkIn;

    @FXML
    private DatePicker checkInRange;

    @FXML
    private TableColumn<transactions, LocalDateTime> checkOut;

    @FXML
    private DatePicker checkOutRange;

    @FXML
    private TableColumn<transactions, Integer> extHours;

    @FXML
    private TableColumn<transactions, Integer> finalPay;

    @FXML
    private TableColumn<transactions, String> fullName;

    @FXML
    private TableColumn<transactions, Integer> initialPay;

    @FXML
    private TableColumn<transactions, String> refNo1;

    @FXML
    private TableColumn<transactions, String> refNo2;

    @FXML
    private TableColumn<transactions, Integer> roomNo;

    @FXML
    private TableColumn<transactions, String> roomType;

    @FXML
    private TextField searchField;

    @FXML
    private ImageView archive;
    @FXML
    private TableColumn<transactions, String> status;

    @FXML
    private TableColumn<transactions, Integer> totalHours;

    @FXML
    private TableColumn<transactions, Integer> totalPaid;

    @FXML
    private TableColumn<transactions, Integer> totalPrice;

    @FXML
    private TableColumn<transactions, String> transacType;
    @FXML
    private TableColumn<transactions, String> remarks;

    @FXML
    private TableView<transactions> transactionsTableView;

    @FXML
    private Button sort;
    private ObservableList<transactions> list;

    //Db connection
    private DbConnect connect;
    private Connection conn;

    DataSingleton data = DataSingleton.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connect = new DbConnect();
        if(data.getRole() == "Employee"){
            archive.setVisible(false);
            archive.setDisable(true);
        }else if (data.getRole() == "Admin"){
            archive.setVisible(true);
            archive.setDisable(false);
        }

        try {
            loadRecordsTalble();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterRooms(newValue);
        });

        sort.setOnAction(event -> {
            FilteredList<transactions> filteredData = new FilteredList<>(list);

            LocalDate fromDate = checkInRange.getValue();
            LocalDate toDate = checkOutRange.getValue();

            if (fromDate != null && toDate != null) {
                filteredData.setPredicate(data -> {
                    LocalDate checkInDate = LocalDate.from(data.getCheckIn());
                    LocalDate checkOutDate = LocalDate.from(data.getCheckOut());

                    return (checkInDate.isEqual(fromDate) || checkInDate.isAfter(fromDate))
                            && (checkOutDate.isEqual(toDate) || checkOutDate.isBefore(toDate));
                });
            } else if (fromDate != null) {
                filteredData.setPredicate(data -> {
                    LocalDate checkInDate = LocalDate.from(data.getCheckIn());

                    return checkInDate.isEqual(fromDate);
                });
            } else if (toDate != null) {
                filteredData.setPredicate(data -> {
                    LocalDate checkOutDate = LocalDate.from(data.getCheckOut());

                    return checkOutDate.isEqual(toDate);
                });
            }

            transactionsTableView.setItems(filteredData);
        });
    }

    private void filterRooms(String searchText) {
        FilteredList<transactions> filteredData = new FilteredList<>(list, p -> true);

        if (searchText == null || searchText.isEmpty()) {
            // If there's no search text, filter based on the selected date range (if any)
            filteredData = getFilteredDataBasedOnDateRange(filteredData);
        } else {
            String lowerCaseSearchText = searchText.toLowerCase();

            // Filter based on the search text and the selected date range (if any)
            filteredData.setPredicate(transactions -> {
                boolean isInDateRange = isInDateRange(transactions);
                if (isInDateRange && Integer.toString(transactions.getRoomNo()).contains(lowerCaseSearchText)) {
                    return true; // match found in room number column
                } else if (isInDateRange && transactions.getRoomType().toLowerCase().contains(lowerCaseSearchText)) {
                    return true; // match found in room type column
                } else if (isInDateRange && Integer.toString(transactions.getTotalPrice()).contains(lowerCaseSearchText)) {
                    return true; // match found in roomprice column
                } else if (isInDateRange && transactions.getFullName().toLowerCase().contains(lowerCaseSearchText)) {
                    return true; // match found in name column
                }else if (isInDateRange && Integer.toString(transactions.getTotalHours()).contains(lowerCaseSearchText)) {
                    return true; // match found in hours column
                }else if (isInDateRange && transactions.getCheckIn().toString().toLowerCase().contains(lowerCaseSearchText)) {
                    return true; // match found in check-in column
                }else if (isInDateRange && transactions.getCheckOut().toString().toLowerCase().contains(lowerCaseSearchText)) {
                    return true; // match found in check-in column
                }
                return false; // no match found
            });
        }

        transactionsTableView.setItems(filteredData);
    }

    private FilteredList<transactions> getFilteredDataBasedOnDateRange(FilteredList<transactions> data) {
        FilteredList<transactions> filteredData = new FilteredList<>(data, p -> true);

        LocalDate fromDate = checkInRange.getValue();
        LocalDate toDate = checkOutRange.getValue();

        if (fromDate != null && toDate != null) {
            filteredData.setPredicate(transactions -> {
                LocalDate transactionDate = LocalDate.from(transactions.getCheckIn());
                return transactionDate.isAfter(fromDate.minusDays(1)) && transactionDate.isBefore(toDate.plusDays(1));
            });
        }

        return filteredData;
    }

    private boolean isInDateRange(transactions transactions) {
        LocalDate fromDate = checkInRange.getValue();
        LocalDate toDate = checkOutRange.getValue();

        if (fromDate != null && toDate != null) {
            LocalDate transactionDate = LocalDate.from(transactions.getCheckIn());
            return transactionDate.isAfter(fromDate.minusDays(1)) && transactionDate.isBefore(toDate.plusDays(1));
        }

        return true;
    }

    public void loadRecordsTalble() throws SQLException {
        list = FXCollections.observableArrayList();

        conn = connect.getConnection();
        String query ="SELECT room_No, room_Type, name, checkin, checkout, total_hours, extended_Hours, timeLeft, total_price, initial_payment, final_payment, total_paid, Mode_of_payment, Mode_of_payment2, Ref_No, Ref_No2, type_of_transaction, status, remarks FROM transaction";

        ResultSet set = conn.createStatement().executeQuery(query);
        while (set.next()){
            int roomNo = set.getInt("room_No");
            String roomType = set.getString("room_type");
            String name = set.getString("name");
            LocalDateTime checkIn = set.getTimestamp("checkin").toLocalDateTime();
            Timestamp checkOutTimestamp = set.getTimestamp("checkout");
            LocalDateTime checkOut = null;
            if (checkOutTimestamp != null) {
                checkOut = checkOutTimestamp.toLocalDateTime();
            }

            int totalHours = set.getInt("total_hours");
            int extendedHours = set.getInt("extended_Hours");
            String timeRemaining = set.getString("timeLeft");
            int totalPrice = set.getInt("total_price");
            int initialPay = set.getInt("initial_payment");
            int finalPay = set.getInt("final_payment");
            int totalPaid = set.getInt("total_paid");
            String mop1 = set.getString("Mode_of_payment");
            String mop2 = set.getString("Mode_of_payment2");
            String refNo1 = set.getString("Ref_No");
            String refNo2 = set.getString("Ref_No2");
            String transacType = set.getString("type_of_transaction");
            String transacStatus = set.getString("status");
            String notes = set.getString("remarks");

            //format date and time
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
            String formattedCheckIn = checkIn.format(formatter);
            String formattedCheckOut = checkOut.format(formatter);


            transactions transac = new transactions(notes, roomNo, roomType, name, checkIn, checkOut, totalHours, extendedHours, timeRemaining,totalPrice, initialPay, finalPay, totalPaid, mop1, mop2, refNo1, refNo2, transacType, transacStatus);
            list.add(transac);
        }

        roomNo.setCellValueFactory(new PropertyValueFactory<>("roomNo"));
        roomType.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        fullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        checkIn.setCellValueFactory(new PropertyValueFactory<>("formattedCheckIn"));
        checkOut.setCellValueFactory(new PropertyValueFactory<>("formattedCheckOut"));
        totalHours.setCellValueFactory(new PropertyValueFactory<>("totalHours"));
        extHours.setCellValueFactory(new PropertyValueFactory<>("extendedHours"));
        timeLeft.setCellValueFactory(new PropertyValueFactory<>("timeLeft"));
        totalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        initialPay.setCellValueFactory(new PropertyValueFactory<>("initialPay"));
        finalPay.setCellValueFactory(new PropertyValueFactory<>("finalPay"));
        totalPaid.setCellValueFactory(new PropertyValueFactory<>("totalPaid"));
        MOP1.setCellValueFactory(new PropertyValueFactory<>("mop1"));
        MOP2.setCellValueFactory(new PropertyValueFactory<>("mop2"));
        refNo1.setCellValueFactory(new PropertyValueFactory<>("refNo1"));
        refNo2.setCellValueFactory(new PropertyValueFactory<>("refNo2"));
        transacType.setCellValueFactory(new PropertyValueFactory<>("transacType"));
        status.setCellValueFactory(new PropertyValueFactory<>("Status"));
        remarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));

        transactionsTableView.setItems(list);
        conn.close();
    }

    public void refreshTable() throws SQLException {
        loadRecordsTalble();
    }

    public void export(){
        // Create a confirmation dialog box with custom text and options
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to export the data?");
        confirmationDialog.setHeaderText(null);
        confirmationDialog.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    exportRecords();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }else{
                confirmationDialog.close();
            }
        });
    }
    public void exportRecords() throws SQLException {
        conn = connect.getConnection();

        // Create a loading screen or show a confirmation message
        Alert loadingAlert = new Alert(Alert.AlertType.INFORMATION, "Exporting data. Please wait...", ButtonType.OK);
        loadingAlert.show();

        // Create a background task for the export process
        Task<Void> exportTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Create a new Workbook
                    Workbook workbook = new XSSFWorkbook();

                    // Retrieve the table names from the database metadata
                    DatabaseMetaData rsMetaData = conn.getMetaData();
                    ResultSet tablesResultSet = rsMetaData.getTables(null, null, null, new String[]{"TABLE"});
                    final List<String> tableNames = new ArrayList<>();
                    while (tablesResultSet.next()) {
                        tableNames.add(tablesResultSet.getString("TABLE_NAME"));
                    }

                    tablesResultSet.close();

                    // Export data for each table
                    for (String tableName : tableNames) {
                        Sheet sheet = workbook.createSheet(tableName);
                        ResultSet resultSet = conn.createStatement().executeQuery("SELECT * FROM " + tableName);
                        ResultSetMetaData metaData = (ResultSetMetaData) resultSet.getMetaData();

                        // Create the header row
                        Row headerRow = sheet.createRow(0);
                        for (int k = 0; k < metaData.getColumnCount(); k++) {
                            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(k);
                            cell.setCellValue(metaData.getColumnName(k + 1));
                        }

                        // Populate the data rows
                        int rowNumber = 1;
                        while (resultSet.next()) {
                            Row row = sheet.createRow(rowNumber++);
                            for (int k = 0; k < metaData.getColumnCount(); k++) {
                                String columnName = metaData.getColumnName(k + 1);

                                // Skip the "description" column
                                if (columnName.equalsIgnoreCase("description")) {
                                    continue;
                                }

                                org.apache.poi.ss.usermodel.Cell cell = row.createCell(k);
                                String cellValue = resultSet.getString(k + 1);

                                // Check if cellValue is null
                                if (cellValue != null) {
                                    // Truncate cell contents if necessary
                                    if (cellValue.length() > 32767) {
                                        cellValue = cellValue.substring(0, 32767);
                                    }
                                }

                                cell.setCellValue(cellValue);
                            }
                        }

                        resultSet.close();
                    }

                    // Auto-size columns
                    for (String tableName : tableNames) {
                        Sheet sheet = workbook.getSheet(tableName);
                        if (sheet != null) {
                            for (int k = 0; k < sheet.getRow(0).getLastCellNum(); k++) {
                                sheet.autoSizeColumn(k);
                            }
                        }
                    }

                    // Show the file save dialog on the JavaFX Application Thread
                    Platform.runLater(() -> {
                        // Create a FileChooser object
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("Save Excel File");
                        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

                        // Show the file save dialog
                        File file = fileChooser.showSaveDialog(null);

                        if (file != null) {
                            try {
                                // Save the workbook to the selected file
                                FileOutputStream fileOut = new FileOutputStream(file);
                                workbook.write(fileOut);
                                fileOut.close();

                                // Display a success message or perform any other desired actions
                                Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Database exported successfully to: " + file.getAbsolutePath(), ButtonType.OK);
                                successAlert.showAndWait();
                            } catch (IOException e) {
                                // Handle any IO exceptions
                                e.printStackTrace();
                            }
                        }

                        // Display a success message or perform any other desired actions
                        System.out.println("Database exported successfully.");
                    });

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return null;
            }
        };

        // Start the background task
        Thread exportThread = new Thread(exportTask);
        exportThread.start();

        // Close the loading screen or confirmation message once the export task is completed
        exportTask.setOnSucceeded(event -> loadingAlert.close());
    }

    private void exportOnly(Connection connection, Workbook workbook) throws SQLException {
        String[] tableNames = {"rooms", "pricing", "user", "guest", "payment", "reservation", "transaction"};

        for (String tableName : tableNames) {
            Sheet sheet = workbook.createSheet(tableName);
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM " + tableName);
            ResultSetMetaData metaData = (ResultSetMetaData) resultSet.getMetaData();

            // Create the header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(metaData.getColumnName(i + 1));
            }

            // Populate the data rows
            int rowNumber = 1;
            while (resultSet.next()) {
                Row row = sheet.createRow(rowNumber++);
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    org.apache.poi.ss.usermodel.Cell cell = row.createCell(i);
                    cell.setCellValue(resultSet.getString(i + 1));
                }
            }

            // Auto-size columns
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            resultSet.close();
        }
    }


    private void exportDatabase(Connection connection, Workbook workbook) throws SQLException {
        String[] tableNames = {"rooms", "pricing", "user", "guest", "payment", "reservation", "transaction"};

        for (String tableName : tableNames) {
            Sheet sheet = workbook.createSheet(tableName);
            ResultSet resultSet;
            ResultSetMetaData metaData;

            if (tableName.equals("reservation")) {
                // Retrieve transactions from the previous year
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.YEAR, -1);
                int previousYear = calendar.get(Calendar.YEAR);

                resultSet = connection.createStatement().executeQuery("SELECT * FROM reservation WHERE YEAR(date_created) = " + previousYear);
                metaData = (ResultSetMetaData) resultSet.getMetaData();
            } else if(tableName.equals("transaction")){
                // Retrieve reservation from the previous year
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.YEAR, -1);
                int previousYear = calendar.get(Calendar.YEAR);

                resultSet = connection.createStatement().executeQuery("SELECT * FROM transaction WHERE YEAR(checkin) = " + previousYear);
                metaData = (ResultSetMetaData) resultSet.getMetaData();
            } else {
                resultSet = connection.createStatement().executeQuery("SELECT * FROM " + tableName);
                metaData = (ResultSetMetaData) resultSet.getMetaData();
            }

            // Create the header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(metaData.getColumnName(i + 1));
            }

            // Populate the data rows
            int rowNumber = 1;
            while (resultSet.next()) {
                Row row = sheet.createRow(rowNumber++);
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    org.apache.poi.ss.usermodel.Cell cell = row.createCell(i);
                    cell.setCellValue(resultSet.getString(i + 1));
                }
            }

            // Auto-size columns
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

        }
    }

    public void archive() throws SQLException {
        conn = connect.getConnection();
        // Create a confirmation dialog box with custom text and options
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to ARCHIVE the data? (Archiving the data also DELETES the old records.)");
        confirmationDialog.setHeaderText(null);
        confirmationDialog.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    archiveRecords();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                try {
                    deleteOldTransactions(conn);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }else{
                confirmationDialog.close();
            }
        });
    }

    public void archiveRecords() throws SQLException {
        conn = connect.getConnection();

        // Create a loading screen or show a confirmation message
        Alert loadingAlert = new Alert(Alert.AlertType.INFORMATION, "Exporting data. Please wait...", ButtonType.OK);
        loadingAlert.show();

        // Create a background task for the export process
        Task<Void> exportTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Create a new Workbook (assuming you have a Workbook object named "workbook")
                    Workbook workbook = new XSSFWorkbook();

                    // Call the exportDatabase method passing the connection and workbook objects
                    exportDatabase(conn, workbook);

                    // Show the file save dialog on the JavaFX Application Thread
                    Platform.runLater(() -> {
                        // Create a FileChooser object
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("Save Excel File");
                        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

                        // Show the file save dialog
                        File file = fileChooser.showSaveDialog(null);

                        if (file != null) {
                            try {
                                // Save the workbook to the selected file
                                FileOutputStream fileOut = new FileOutputStream(file);
                                workbook.write(fileOut);
                                fileOut.close();

                                // Display a success message or perform any other desired actions
                                Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Database exported successfully to: " + file.getAbsolutePath(), ButtonType.OK);
                                successAlert.showAndWait();
                            } catch (IOException e) {
                                // Handle any IO exceptions
                                e.printStackTrace();
                            }
                        }

                        // Display a success message or perform any other desired actions
                        System.out.println("Database exported successfully.");
                    });

                } catch (SQLException e) {
                    // Handle any SQL exceptions
                    e.printStackTrace();
                }

                return null;
            }
        };

        // Start the background task
        Thread exportThread = new Thread(exportTask);
        exportThread.start();

        // Close the loading screen or confirmation message once the export task is completed
        exportTask.setOnSucceeded(event -> loadingAlert.close());

    }

    //Deleting the old transaction
    private void deleteOldTransactions(Connection connection) throws SQLException {
        // Retrieve transactions from the previous year
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        int previousYear = calendar.get(Calendar.YEAR);

        // Delete the records from the transaction table that are one year old
        String deleteQuery = "DELETE FROM transaction WHERE YEAR(checkin) = ?";
        PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
        deleteStatement.setInt(1, previousYear);
        deleteStatement.executeUpdate();
    }
}