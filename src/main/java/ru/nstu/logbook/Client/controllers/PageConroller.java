package ru.nstu.logbook.Client.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import ru.nstu.logbook.Client.net.Client;
import ru.nstu.logbook.Client.notes.Note;
import ru.nstu.logbook.Client.notes.Reminder;
import ru.nstu.logbook.Client.utils.NoteStorage;
import ru.nstu.logbook.Client.utils.RemindStorage;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class PageConroller {

    @FXML
    public Button authorizationButton;

    @FXML
    public Label authorizedName;

    @FXML
    public DatePicker dateScroll;

    @FXML
    public AnchorPane menuPane;

    @FXML
    public Button deleteButton;

    @FXML
    public Button backButton;

    @FXML
    public Button registrationButton;

    @FXML
    public ListView<Reminder> remindsList;

    @FXML
    public Button scrollAdmitButton;


    LocalDate current = LocalDate.now();
    LocalDate calendarDate = LocalDate.now();
    NoteStorage noteStorage = NoteStorage.getInstance();
    RemindStorage remindStorage = RemindStorage.getInstance();

    public Stage stage;
    public Client client;

    public Scene mainScene;
    public MainPageController mainPageController;

    public Scene notePageScene;
    public NotePageController notePageController;

    public Scene remindPageScene;
    public RemindPageController remindPageController;


    @FXML
    public void remindsListEvent(MouseEvent event) {

    }

    @FXML
    public void authShow(ActionEvent event) {

    }

    @FXML
    public void registrationShow(ActionEvent event) {

    }

    @FXML
    public void plansShow(ActionEvent event) {

    }

    @FXML
    public void scrollShow(ActionEvent event) {

    }

    @FXML
    public void back(ActionEvent event) {
        stage.setScene(mainScene);
        mainPageController.drawCalendar();
    }

    @FXML
    public void delete(ActionEvent event) {

    }

    public void drewList() {
        ObservableList<Reminder> observableList = FXCollections.observableList(
                RemindStorage.getInstance().reminds
        );
        remindsList.setItems(observableList);

        MultipleSelectionModel<Reminder> reminderMultipleSelectionModel = remindsList.getSelectionModel();
        reminderMultipleSelectionModel.selectedItemProperty().addListener(new ChangeListener<Reminder>() {

            public void changed(ObservableValue<? extends Reminder> changed, Reminder oldValue, Reminder newValue) {
                showReminder(newValue);
            }
        });
    }

    void showNote(LocalDate noteDate, Note note) {
        if (note == null) {
            note = new Note();
            note.setDate(noteDate);
        }
        notePageController.setNote(note);
        stage.setScene(notePageScene);
    }

    void showReminder(Reminder reminder) {
        remindPageController.setRemind(reminder);
        stage.setScene(remindPageScene);
    }

    public void init(Stage stage,
                     Client client,
                     Scene mainScene,
                     MainPageController mainPageController,
                     Scene notePageScene,
                     NotePageController notePageController,
                     Scene remindPageScene,
                     RemindPageController remindPageController) {
        this.stage = stage;
        this.client = client;

        this.mainScene = mainScene;
        this.mainPageController = mainPageController;
        this.notePageScene = notePageScene;
        this.notePageController = notePageController;
        this.remindPageScene = remindPageScene;
        this.remindPageController = remindPageController;
    }
}
