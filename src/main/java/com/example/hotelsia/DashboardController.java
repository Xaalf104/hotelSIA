package com.example.hotelsia;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.ResourceBundle;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class DashboardController implements Initializable {
    @FXML
    private BorderPane bp;
    @FXML
    private Pane ap;
    @FXML
    public Text notif;

    //buttons
    @FXML
    private Button guestsbtn;

    @FXML
    private Button homebtn;

    @FXML
    private Button roomsbtn;

    @FXML
    private Button reserveBtn;
    @FXML
    private BarChart<String, Number> reservationStatusBar;
    @FXML
    private LineChart<String, Number> occupancyRateChart;
    @FXML
    private BarChart<String, Number> roomRevenueBar;
    @FXML
    private LineChart<String, Number> averageStayLine;

    @FXML
    private Button transactionbtn;
    Connection con = null;
    private DbConnect connect;
    private Connection conn;

    private static DashboardController instance;

    public DashboardController(){
        instance = this;
    }

    public static DashboardController getInstance(){
        return instance;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connect = new DbConnect();
        try {
            getTotalNumber();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        homebtn.setStyle("-fx-background-color: #61c2a2");

        //BarChart for Reservation
        loadReservationStatus();
        // Add tooltip behavior to the chart
        for (XYChart.Series<String, Number> s : reservationStatusBar.getData()) {
            for (XYChart.Data<String, Number> d : s.getData()) {
                Node node = d.getNode();
                Tooltip tooltip = new Tooltip(String.format("%.2f", d.getYValue().doubleValue()));
                Tooltip.install(node, tooltip);
                node.setOnMouseEntered(event -> {
                    tooltip.show(node, event.getScreenX(), event.getScreenY() + 10);
                });
                node.setOnMouseExited(event -> {
                    tooltip.hide();
                });
            }
        }


        //Occupancy Rate
        try {
            populateOccupancyRateChart();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // Add tooltip behavior to the chart
        for (XYChart.Series<String, Number> s : occupancyRateChart.getData()) {
            for (XYChart.Data<String, Number> d : s.getData()) {
                Node node = d.getNode();
                Tooltip tooltip = new Tooltip(String.format("%.2f", d.getYValue().doubleValue()));
                Tooltip.install(node, tooltip);
                node.setOnMouseEntered(event -> {
                    node.setStyle("-fx-background-color: white;");
                    tooltip.show(node, event.getScreenX(), event.getScreenY() + 10);
                });
                node.setOnMouseExited(event -> {
                    node.setStyle("");
                    tooltip.hide();
                });
            }
        }

        //Room Revenue
        try {
            populateRoomRevenueChart();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Add tooltip behavior to the chart
        for (XYChart.Series<String, Number> s : roomRevenueBar.getData()) {
            for (XYChart.Data<String, Number> d : s.getData()) {
                Node node = d.getNode();
                Tooltip tooltip = new Tooltip(String.format("%.2f", d.getYValue().doubleValue()));
                Tooltip.install(node, tooltip);
                node.setOnMouseEntered(event -> {
                    tooltip.show(node, event.getScreenX(), event.getScreenY() + 10);
                });
                node.setOnMouseExited(event -> {
                    tooltip.hide();
                });
            }
        }


        //Average Stay
        try {
            populateAverageStayLineChart();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Add tooltip behavior to the chart
        for (XYChart.Series<String, Number> s : averageStayLine.getData()) {
            for (XYChart.Data<String, Number> d : s.getData()) {
                Node node = d.getNode();
                Tooltip tooltip = new Tooltip(String.format("%.2f", d.getYValue().doubleValue()));
                Tooltip.install(node, tooltip);
                node.setOnMouseEntered(event -> {
                    node.setStyle("-fx-background-color: lightgreen;");
                    tooltip.show(node, event.getScreenX(), event.getScreenY() + 10);
                });
                node.setOnMouseExited(event -> {
                    node.setStyle("");
                    tooltip.hide();
                });
            }
        }
    }

    public void refreshTable() throws SQLException {
        reservationStatusBar.getData().clear();
        averageStayLine.getData().clear();
        occupancyRateChart.getData().clear();
        roomRevenueBar.getData().clear();

        loadReservationStatus();
        populateAverageStayLineChart();
        populateOccupancyRateChart();
        populateRoomRevenueChart();

        // Add tooltip behavior to the occupancy chart
        for (XYChart.Series<String, Number> series : occupancyRateChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node node = data.getNode();
                Tooltip tooltip = new Tooltip(String.format("%.2f", data.getYValue().doubleValue()));
                Tooltip.install(node, tooltip);
                node.setOnMouseEntered(event -> {
                    node.setStyle("-fx-background-color: white;");
                    tooltip.show(node, event.getScreenX(), event.getScreenY() + 10);
                });
                node.setOnMouseExited(event -> {
                    node.setStyle("");
                    tooltip.hide();
                });
            }
        }

        // Add tooltip behavior to the reservation chart
        for (XYChart.Series<String, Number> s : reservationStatusBar.getData()) {
            for (XYChart.Data<String, Number> d : s.getData()) {
                Node node = d.getNode();
                Tooltip tooltip = new Tooltip(String.format("%.2f", d.getYValue().doubleValue()));
                Tooltip.install(node, tooltip);
                node.setOnMouseEntered(event -> {
                    tooltip.show(node, event.getScreenX(), event.getScreenY() + 10);
                });
                node.setOnMouseExited(event -> {
                    tooltip.hide();
                });
            }
        }

        // Add tooltip behavior to the average stay chart
        for (XYChart.Series<String, Number> s : averageStayLine.getData()) {
            for (XYChart.Data<String, Number> d : s.getData()) {
                Node node = d.getNode();
                Tooltip tooltip = new Tooltip(String.format("%.2f", d.getYValue().doubleValue()));
                Tooltip.install(node, tooltip);
                node.setOnMouseEntered(event -> {
                    node.setStyle("-fx-background-color: lightgreen;");
                    tooltip.show(node, event.getScreenX(), event.getScreenY() + 10);
                });
                node.setOnMouseExited(event -> {
                    node.setStyle("");
                    tooltip.hide();
                });
            }
        }

        // Add tooltip behavior to the room revenue chart
        for (XYChart.Series<String, Number> s : roomRevenueBar.getData()) {
            for (XYChart.Data<String, Number> d : s.getData()) {
                Node node = d.getNode();
                Tooltip tooltip = new Tooltip(String.format("%.2f", d.getYValue().doubleValue()));
                Tooltip.install(node, tooltip);
                node.setOnMouseEntered(event -> {
                    tooltip.show(node, event.getScreenX(), event.getScreenY() + 10);
                });
                node.setOnMouseExited(event -> {
                    tooltip.hide();
                });
            }
        }
    }

    public void getTotalNumber() throws SQLException {
        String query = "SELECT COUNT(*) FROM guest WHERE timer <= 1800";
        conn = connect.getConnection();
        ResultSet set = conn.createStatement().executeQuery(query);

        while(set.next()){
            int count = set.getInt(1);
            notif.setText(String.valueOf(count));
        }
        conn.close();
    }

    @FXML
    private void home(MouseEvent e){
        bp.setCenter(ap);
        homebtn.setStyle("-fx-background-color: #61c2a2");
        roomsbtn.setStyle("-fx-background-color: #ffffff");
        guestsbtn.setStyle("-fx-background-color: #ffffff");
        reserveBtn.setStyle("-fx-background-color: #ffffff");
        transactionbtn.setStyle("-fx-background-color: #ffffff");
    }
    @FXML
    private void room(MouseEvent e) throws IOException {
        loadPage("Rooms");
        homebtn.setStyle("-fx-background-color: #ffffff");
        roomsbtn.setStyle("-fx-background-color: #61c2a2");
        guestsbtn.setStyle("-fx-background-color: #ffffff");
        reserveBtn.setStyle("-fx-background-color: #ffffff");
        transactionbtn.setStyle("-fx-background-color: #ffffff");
    }
    @FXML
    private void guests(MouseEvent e) throws IOException{
        loadPage("Guests");
        homebtn.setStyle("-fx-background-color: #ffffff");
        roomsbtn.setStyle("-fx-background-color: #ffffff");
        guestsbtn.setStyle("-fx-background-color: #61c2a2");
        reserveBtn.setStyle("-fx-background-color: #ffffff");
        transactionbtn.setStyle("-fx-background-color: #ffffff");
    }
    @FXML
    private void transactions(MouseEvent e) throws IOException{
        loadPage("Transactions");
        homebtn.setStyle("-fx-background-color: #ffffff");
        roomsbtn.setStyle("-fx-background-color: #ffffff");
        guestsbtn.setStyle("-fx-background-color: #ffffff");
        reserveBtn.setStyle("-fx-background-color: #ffffff");
        transactionbtn.setStyle("-fx-background-color: #61c2a2");
    }

    @FXML
    private void reservation(MouseEvent e) throws IOException{
        loadPage("reservationList");
        homebtn.setStyle("-fx-background-color: #ffffff");
        roomsbtn.setStyle("-fx-background-color: #ffffff");
        guestsbtn.setStyle("-fx-background-color: #ffffff");
        reserveBtn.setStyle("-fx-background-color: #61c2a2");
        transactionbtn.setStyle("-fx-background-color: #ffffff");
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


    private void loadPage(String page) throws IOException {
        Parent root = null;

        root = FXMLLoader.load(getClass().getResource(page+".fxml"));
        bp.setCenter(root);

    }

    //Start of the Chart methods

    // Get the number of rooms for each status from the database
    private int getAvailableRooms() {
        int availableRooms = 0;
        try {
            Connection conn = connect.getConnection();
            String query = "SELECT COUNT(*) FROM rooms WHERE status = 'Available'";
            ResultSet rs = conn.createStatement().executeQuery(query);
            if (rs.next()) {
                availableRooms = rs.getInt(1);
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return availableRooms;
    }

    public void loadReservationStatus(){
        // Set up the data for the chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Available", getAvailableRooms()));
        series.getData().add(new XYChart.Data<>("Reserved", getReservedRooms()));
        series.getData().add(new XYChart.Data<>("Occupied", getOccupiedRooms()));

        // Add the series to the chart
        reservationStatusBar.getData().addAll(series);

        // Set the color for each status
        for (int i = 0; i < series.getData().size(); i++) {
            XYChart.Data<String, Number> data = series.getData().get(i);
            String status = data.getXValue();
            if (status.equals("Available")) {
                data.getNode().setStyle("-fx-bar-fill: green;");
                reservationStatusBar.setLegendVisible(false);
            } else if (status.equals("Reserved")) {
                data.getNode().setStyle("-fx-bar-fill: yellow;");
                reservationStatusBar.setLegendVisible(false);
            } else if (status.equals("Occupied")) {
                data.getNode().setStyle("-fx-bar-fill: red;");
                reservationStatusBar.setLegendVisible(false);
            }
        }

    }

    private int getReservedRooms() {
        int reservedRooms = 0;
        try {
            Connection conn = connect.getConnection();
            String query = "SELECT COUNT(*) FROM rooms WHERE status = 'Reserved'";
            ResultSet rs = conn.createStatement().executeQuery(query);
            if (rs.next()) {
                reservedRooms = rs.getInt(1);
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return reservedRooms;
    }

    private int getOccupiedRooms() {
        int occupiedRooms = 0;
        try {
            Connection conn = connect.getConnection();
            String query = "SELECT COUNT(*) FROM rooms WHERE status = 'Occupied'";
            ResultSet rs = conn.createStatement().executeQuery(query);
            if (rs.next()) {
                occupiedRooms = rs.getInt(1);
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return occupiedRooms;
    }

    //Occupancy rate
    public void populateOccupancyRateChart() throws SQLException {
        conn = connect.getConnection();
        // Set up the data for the chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // Retrieve occupancy rate data from transaction table for the past week
        // Example SQL query: SELECT DATE(checkin) AS date, COUNT(*)/7*100 AS occupancy_rate FROM transaction WHERE checkin BETWEEN date_sub(NOW(), INTERVAL 7 DAY) AND NOW() GROUP BY DATE(checkin)
        try {
            String query = "SELECT DATE(checkin) AS date, COUNT(*)/7*100 AS occupancy_rate FROM transaction WHERE checkin BETWEEN date_sub(NOW(), INTERVAL 7 DAY) AND NOW() AND status = 'Completed' GROUP BY DATE(checkin)";
            ResultSet result = conn.createStatement().executeQuery(query);
            while (result.next()) {
                String date = result.getString("date");
                double occupancyRate = result.getDouble("occupancy_rate");
                series.getData().add(new XYChart.Data<>(date, occupancyRate));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Add the series to the chart
        occupancyRateChart.getData().addAll(series);
        occupancyRateChart.setLegendVisible(false);
        // Set the color of the line to red
        series.getNode().setStyle("-fx-stroke: red;");

        // Set the label for the x-axis
        ((CategoryAxis) occupancyRateChart.getXAxis()).setLabel("Date");

        // Set the label for the y-axis
        ((NumberAxis) occupancyRateChart.getYAxis()).setLabel("Occupancy Rate (%)");

        // Add tooltip behavior to the chart
        for (XYChart.Series<String, Number> s : occupancyRateChart.getData()) {
            for (XYChart.Data<String, Number> d : s.getData()) {
                Tooltip.install(d.getNode(), new Tooltip(String.format("%.2f", d.getYValue().doubleValue())));
            }
        }

        conn.close();
    }

    //RoomRevenue
    public void populateRoomRevenueChart() throws SQLException {
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();

        conn = connect.getConnection();
        // Set up the data for the chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // Retrieve revenue data for each room for the given month and year
        // Example SQL query: SELECT room_number, SUM(total_price) AS revenue FROM transaction WHERE YEAR(checkin) = ? AND MONTH(checkin) = ? GROUP BY room_number
        try {
            String query = "SELECT room_No, SUM(total_price) AS revenue FROM transaction WHERE YEAR(checkin) = ? AND MONTH(checkin) = ? AND status = 'Completed' GROUP BY room_No";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, year);
            stmt.setInt(2, month);
            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                String roomNumber = result.getString("room_No");
                double revenue = result.getDouble("revenue");
                series.getData().add(new XYChart.Data<>(roomNumber, revenue));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Add the series to the chart
        roomRevenueBar.getData().addAll(series);

        // Remove symbols from the bars
        roomRevenueBar.setLegendVisible(false);

        // Set the color of the bars to light green
        for (XYChart.Data<String, Number> data : series.getData()) {
            Node bar = data.getNode();
            bar.setStyle("-fx-bar-fill: lightgreen;");
        }

        // Set the label for the x-axis
        ((CategoryAxis) roomRevenueBar.getXAxis()).setLabel("Room Number");

        // Set the label for the y-axis
        ((NumberAxis) roomRevenueBar.getYAxis()).setLabel("Revenue");

        // Set the title of the chart
        roomRevenueBar.setTitle("Room Revenue for " + Month.of(month).name() + " " + year);

        conn.close();
    }

    //Average stay
    public void populateAverageStayLineChart() throws SQLException {
        conn = connect.getConnection();
        // Set up the data for the chart
        XYChart.Series series = new XYChart.Series();

        // Retrieve average length of stay data from transaction table for the past week
        // Example SQL query: SELECT DATE_FORMAT(checkin, '%Y-%v') AS week, AVG(total_hours) AS avg_length_of_stay FROM transaction WHERE checkin BETWEEN date_sub(NOW(), INTERVAL 7 DAY) AND NOW() GROUP BY week ORDER BY week
        try {
            String query = "SELECT DATE_FORMAT(checkin, '%Y-%v') AS week, AVG(total_hours) AS avg_length_of_stay FROM transaction WHERE checkin BETWEEN date_sub(NOW(), INTERVAL 7 DAY) AND NOW() AND status='Completed' GROUP BY week ORDER BY week";
            ResultSet result = conn.createStatement().executeQuery(query);
            while (result.next()) {
                String week = result.getString("week");
                double avgLengthOfStay = result.getDouble("avg_length_of_stay");
                series.getData().add(new XYChart.Data<>(week, avgLengthOfStay));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Add the series to the chart
        averageStayLine.getData().addAll(series);
        averageStayLine.setLegendVisible(false);
        // Set the color of the line to red
        series.getNode().setStyle("-fx-stroke: red;");

        // Set the label for the x-axis
        ((CategoryAxis) averageStayLine.getXAxis()).setLabel("Week");

        // Set the label for the y-axis
        ((NumberAxis) averageStayLine.getYAxis()).setLabel("Average Length of Stay (Hours)");

        conn.close();
    }


}
