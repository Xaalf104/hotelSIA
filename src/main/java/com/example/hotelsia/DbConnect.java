package com.example.hotelsia;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.sql.*;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

public class DbConnect {
    private static final String DB_URL = "jdbc:mysql://mysql5045.site4now.net/db_a9951b_nicehot";
    private static final String DB_USERNAME = "a9951b_nicehot";
    private static final String DB_PASSWORD = "Nicehotel0123";
    private static final int TIMER_DELAY = 0;
    private static int TIMER_PERIOD = 300000;
    private static final String TIMER_PROP_FILE = "timer.properties";
    private static final String TIMER_PROP_KEY = "timer";

    private boolean notificationShown = false;

    private static Connection con = null;
    private Timer timer = new Timer();
    private int timerValue = 0;


    Image image = new Image("file:/C:/Users/alsha/IdeaProjects/hotelSIA/src/main/resources/com/example/hotelsia/exclamation.png");

    // Establish the connection
    public DbConnect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        loadTimerValue();
        startTimer();
    }

    public static Connection getConnection(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://mysql5045.site4now.net/db_a9951b_nicehot", "a9951b_nicehot", "Nicehotel0123");
            System.out.println("Connection successful!");
            return con;
        } catch (Exception ex){
            System.err.println(ex.getMessage());
            return null;
        }
    }

    // Starts the timer in the database when DbConnect is established
    private void startTimer() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    Statement stmt = con.createStatement();

                    // Select all rows where timer is 1800 or more
                    String selectQuery = "SELECT * FROM guest WHERE status = 'Occupied' AND timer = 1800";
                    ResultSet rs = stmt.executeQuery(selectQuery);

                    // Loop through the result set and send a notification for each row
                    Platform.runLater(() -> {
                        try {
                            while (rs.next()) {
                                String roomNumber = rs.getString("room_No");
                                Notifications.create()
                                        .title("Room " + roomNumber + " has 30 minutes left")
                                        .text("Please check the guest.")
                                        .graphic(new ImageView(image))
                                        .hideAfter(Duration.seconds(3))
                                        .hideCloseButton()
                                        .position(Pos.TOP_RIGHT)
                                        .show();
                                notificationShown = true;
                            }
                            stmt.executeUpdate("UPDATE guest SET timer = timer - 300 WHERE status = 'Occupied' AND timer > 0");
                            stmt.close();
                            timerValue -= 300;
                            saveTimerValue();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, TIMER_DELAY, TIMER_PERIOD);
        // create a new thread that checks the value of notificationsDisplayed every second
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    if (notificationShown) {
                        DashboardController.getInstance().getTotalNumber();
                        GuestController.getInstance().loadRoomTable();
                        notificationShown = false; // reset the flag
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void stopTimer() {
        timer.cancel();
        saveTimerValue();
    }

    // Loads the saved timer value from the file
    private void loadTimerValue() {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream(TIMER_PROP_FILE)) {
            props.load(input);
            String valueStr = props.getProperty(TIMER_PROP_KEY, "0");
            timerValue = Integer.parseInt(valueStr);
        } catch (IOException | NumberFormatException ex) {
            ex.printStackTrace();
        }
    }

    // Saves the timer value in the file
    private void saveTimerValue() {
        Properties props = new Properties();
        props.setProperty(TIMER_PROP_KEY, Integer.toString(timerValue));
        try (OutputStream output = new FileOutputStream(TIMER_PROP_FILE)) {
            props.store(output, null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void resetNotif() throws SQLException, IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
        Parent root = loader.load();
        DashboardController dashboardController = loader.getController();

        String query = "SELECT COUNT(*) FROM guest WHERE timer <= 1800";
        ResultSet set = con.createStatement().executeQuery(query);

        while(set.next()){
            int count = set.getInt(1);
            dashboardController.notif.setText(String.valueOf(count));
        }
        con.close();
    }
}
