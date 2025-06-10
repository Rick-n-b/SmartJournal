package ru.nstu.logbook.Client.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ru.nstu.logbook.Client.net.Client;
import ru.nstu.logbook.Client.notes.Reminder;
import ru.nstu.logbook.Client.utils.NoteStorage;
import ru.nstu.logbook.Client.utils.RemindStorage;

public class RemindPageController {


    @FXML
    private Button authorizationButton;

    @FXML
    private Label authorizedName;

    @FXML
    private Button backButton;

    @FXML
    private TextArea contentArea;

    @FXML
    private DatePicker dateScroll;

    @FXML
    private Button deleteButton;

    @FXML
    private AnchorPane menuPane;

    @FXML
    private Button plansButton;

    @FXML
    private Button registrationButton;

    @FXML
    private DatePicker remindDate;

    @FXML
    private TextField remindTime;

    @FXML
    private ListView<Reminder> remindsList;

    @FXML
    private Button scrollAdmitButton;

    @FXML
    private TextField topicText;

    Stage stage;
    Client client;

    MainPageController mainPageController;
    Scene mainScene;

    Reminder reminder;

    public void setRemind(Reminder reminder) {
        this.reminder = reminder;
        deleteButton.setDisable(reminder.getTopic().isEmpty() && reminder.getContent().isEmpty());

        topicText.setText(reminder.getTopic());
        contentArea.setText(reminder.getContent());
        remindDate.setValue(reminder.getExpirationDate());

    }

    void save() {
        RemindStorage.getInstance().save(reminder);
        RemindStorage.getInstance().reminds.add(reminder);
        deleteButton.setDisable(false);
    }

    @FXML
    void authShow(ActionEvent event) {
        save();
    }

    @FXML
    void plansShow(ActionEvent event) {
        save();
    }

    @FXML
    void registrationShow(ActionEvent event) {
        save();
    }

    @FXML
    void remindsListEvent(MouseEvent event) {

    }

    @FXML
    void scrollShow(ActionEvent event) {
        save();
    }

    @FXML
    void back(ActionEvent event) {
        stage.setScene(mainScene);
        mainPageController.drawCalendar();
    }

    @FXML
    void delete(ActionEvent event) {
        deleteButton.setDisable(true);
        System.out.println(RemindStorage.getInstance().delete(reminder));
    }

    @FXML
    void initialize() {

        contentArea.setWrapText(true);
        topicText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                Reminder oldReminder = reminder;
                oldReminder.setTopic(oldValue);
                RemindStorage.getInstance().delete(oldReminder);
                reminder.setTopic(topicText.getText());
                save();
            }
        });
        contentArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                Reminder oldReminder = reminder;
                oldReminder.setContent(oldValue);
                RemindStorage.getInstance().delete(oldReminder);
                reminder.setContent(contentArea.getText());
                save();
            }
        });
    }

    public void init(Stage stage, Client client, Scene mainScene, MainPageController mainPageController) {
        this.stage = stage;
        this.client = client;
        this.mainScene = mainScene;
        this.mainPageController = mainPageController;
    }

}
