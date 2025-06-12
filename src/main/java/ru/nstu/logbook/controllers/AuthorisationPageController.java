package ru.nstu.logbook.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.nstu.logbook.net.DBManager;

import java.sql.SQLException;

public class AuthorisationPageController {

    @FXML
    TextField nick;
    @FXML
    PasswordField password;
    @FXML
    Button authButton;
    @FXML
    Button cancelButton;
    @FXML
    Label messageLabel;

    Stage stage;

    Scene mainScene;
    MainPageController mainController;

    Scene authPageScene;
    AuthorisationPageController authPageController;

    Scene askScene;
    AskPageController askPageController;

    @FXML
    public void auth(ActionEvent event) {
        String username = nick.getText();
        String pass = password.getText();
        try {
            int userId = DBManager.authenticateUser(username, pass);
            if (userId != -1) {
                PageController.setUserId(userId);
                PageController.setUserName(nick.getText());
                mainController.authorizedName.setText(nick.getText());
                messageLabel.setVisible(true);
                messageLabel.setText("Authorized successfully");
                stage.setScene(askScene);
            } else {
                messageLabel.setVisible(true);
                messageLabel.setText("Incorrect username or password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void cancel(ActionEvent event){
        stage.close();
    }

    public void show(){
        stage.show();
    }

    public void init(Stage stage,
                     Scene mainScene, MainPageController mainController,
                     Scene authPageScene, AuthorisationPageController authPageController,
                     Scene askScene, AskPageController askPageController)
    {
        this.stage = stage;
        this.mainScene = mainScene;
        this.mainController = mainController;
        this.authPageScene = authPageScene;
        this.authPageController = authPageController;
        this.askScene = askScene;
        this.askPageController = askPageController;

        stage.setScene(authPageScene);
        stage.setResizable(false);
        stage.setTitle("Authorization");

        authButton.disableProperty().bind(nick.textProperty().isEmpty().or(password.textProperty().isEmpty()));

        try {
            DBManager.getConnection();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
