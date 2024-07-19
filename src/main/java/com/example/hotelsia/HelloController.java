package com.example.hotelsia;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    private Stage stage;
    private Scene scene;

    @FXML
    private TextField username;
    @FXML
    private Text msg;
    @FXML
    private PasswordField pws;
    @FXML
    private Button loginBtn;
    @FXML
    private AnchorPane loginPane;

    // connection
    Connection con = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    DataSingleton data = DataSingleton.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        loginPane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // Simulate a click event on the login button
                login(new ActionEvent());
                loginBtn.setStyle("-fx-background-color: #61c2a2");

                // Consume the event to prevent it from propagating further
                event.consume();
            }
        });

    }
    public void login(ActionEvent event){
        con = DbConnect.getConnection();
        String user = username.getText().toString();
        String pw = pws.getText().toString();

        //query
        String sql = "SELECT * FROM acctinfo WHERE username = ? AND password = ?";

        try {
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, user);
            preparedStatement.setString(2, pw);
            resultSet = preparedStatement.executeQuery();
            if(!resultSet.next()){
                System.err.println("Wrong login");
                msg.setText("Wrong Login/Password");
            } else {
                String role = resultSet.getString("role");
                System.out.println("Success");

                Parent root = null;
                try {
                    if (role.equals("admin")) {
                        root = FXMLLoader.load(getClass().getResource("adminDashboard.fxml"));
                        data.setRole("Admin");
                        data.setQuery("In Queue");
                    } else if (role.equals("employee")) {
                        root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
                        data.setRole("Employee");
                        data.setQuery("Approved");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                stage = (Stage) loginPane.getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.setResizable(true);
                stage.show();
            }
            con.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }


    public void switchToLogin(ActionEvent event) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("start.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
