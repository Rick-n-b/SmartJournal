package ru.nstu.logbook.Client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.nstu.logbook.Client.net.Client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class AuthorisationPageController {

    @FXML
    TextField nick;
    @FXML
    PasswordField password;
    @FXML
    Button authButton;
    @FXML
    Button backButton;

    Stage stage;
    Client client;

    Scene mainScene;
    MainPageController mainController;

    Scene authPageScene;
    AuthorisationPageController authPageController;

    @FXML
    public void auth(ActionEvent event) throws IOException {
        client.connect(nick.getText());
    }

    @FXML
    public void back(ActionEvent event){
        stage.close();
    }

    public void show(){
        stage.show();
    }

    public void init(Stage stage, Client client,
                     Scene mainScene, MainPageController mainController,
                     Scene authPageScene, AuthorisationPageController authPageController)
    {
        this.stage = stage;
        this.client = client;
        this.mainScene = mainScene;
        this.mainController = mainController;
        this.authPageScene = authPageScene;
        this.authPageController = authPageController;
        stage.setScene(authPageScene);
        stage.setResizable(false);

        authButton.disableProperty().bind(nick.textProperty().isNotEmpty().and(password.textProperty().isNotEmpty()));
    }
    @FXML
    public void initialize(){

    }
}
