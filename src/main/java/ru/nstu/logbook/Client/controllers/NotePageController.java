package ru.nstu.logbook.Client.controllers;

import java.net.URL;
import java.util.ResourceBundle;

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
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ru.nstu.logbook.Client.net.Client;
import ru.nstu.logbook.Client.notes.Note;
import ru.nstu.logbook.Client.notes.Reminder;
import ru.nstu.logbook.Client.utils.NoteStorage;

public class NotePageController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button authorizationButton;

    @FXML
    private Label authorizedName;

    @FXML
    private TextArea contentArea;

    @FXML
    private Label date;

    @FXML
    private DatePicker dateScroll;

    @FXML
    private AnchorPane menuPane;

    @FXML
    private Button plansButton;

    @FXML
    private Button registrationButton;

    @FXML
    private ListView<Reminder> remindsList;

    @FXML
    private Button scrollAdmitButton;

    @FXML
    private TextField topicText;

    Stage stage;
    Scene mainScene;
    Client client;

    Note note;

    public void setNote(Note note) {
        this.note = note;
        topicText.setText(note.getTopic());
        contentArea.setText(note.getContent());
        date.setText(note.getDate().toString());
    }

    void save(){
        NoteStorage.getInstance().save(note);
        NoteStorage.getInstance().notes.put(note.getDate(), note);
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
    void back(ActionEvent event){
        save();
        stage.setScene(mainScene);
    }

    @FXML
    void initialize() {

        contentArea.setWrapText(true);
        topicText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                note.setTopic(topicText.getText());
                save();
            }
        });
        contentArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                note.setContent(contentArea.getText());
                save();
            }
        });


    }

    public void init(Stage stage, Client client, Scene scene) {
        this.stage = stage;
        this.client = client;
        this.mainScene = scene;
    }

}
