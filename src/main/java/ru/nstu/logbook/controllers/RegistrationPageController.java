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
import ru.nstu.logbook.notes.Reminder;
import ru.nstu.logbook.utils.NoteStorage;
import ru.nstu.logbook.utils.RemindStorage;

import java.sql.SQLException;

import static ru.nstu.logbook.controllers.PageController.plan;

public class RegistrationPageController {

    @FXML
    TextField nick;
    @FXML
    PasswordField password;
    @FXML
    PasswordField repeatedPassword;
    @FXML
    Button regButton;
    @FXML
    Button cancelButton;
    @FXML
    Label messageLabel;

    Stage stage;

    Scene mainScene;
    MainPageController mainController;

    Scene regPageScene;
    RegistrationPageController registrationPageController;

    @FXML
    public void reg(ActionEvent event) {
        String username = nick.getText();
        String pass = password.getText();
        String repeatedPass = repeatedPassword.getText();
        if(pass.equals(repeatedPass)){
            try {
                boolean userExists = DBManager.checkUserExists(username);
                if (userExists) {
                    messageLabel.setVisible(true);
                    messageLabel.setText("Username already exists");
                } else {
                    PageController.setUserId(DBManager.registerUser(username, pass));
                    PageController.setUserName(nick.getText());

                    mainController.authorizedName.setText(nick.getText());
                    messageLabel.setVisible(true);
                    messageLabel.setText("User registered successfully");
                    for(var note : NoteStorage.getInstance().notes.values()){
                        DBManager.addNoteForUser(PageController.getUserId(), note);
                    }
                    for(var remind : RemindStorage.getInstance().reminds){
                        DBManager.addReminderForUser(PageController.getUserId(), remind);
                    }
                    DBManager.addPlanForUser(PageController.getUserId(), plan);

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            messageLabel.setVisible(true);
            messageLabel.setText("Passwords do not match");
        }
    }

    @FXML
    public void cancel(ActionEvent event){
        stage.close();
        messageLabel.setText("");
        messageLabel.setVisible(false);
    }

    public void show(){
        stage.show();
    }

    public void init(Stage stage,
                     Scene mainScene, MainPageController mainController,
                     Scene regPageScene, RegistrationPageController registrationPageController)
    {
        this.stage = stage;
        this.mainScene = mainScene;
        this.mainController = mainController;
        this.regPageScene = regPageScene;
        this.registrationPageController = registrationPageController;
        stage.setScene(regPageScene);
        stage.setResizable(false);
        stage.setTitle("Registration");

        regButton.disableProperty().bind(nick.textProperty().isEmpty().or(password.textProperty().isEmpty()).or(repeatedPassword.textProperty().isEmpty()));

        try {
            DBManager.getConnection();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
