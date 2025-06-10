package ru.nstu.logbook.Client.controllers;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import ru.nstu.logbook.Client.net.Client;
import ru.nstu.logbook.Client.notes.Note;
import ru.nstu.logbook.Client.notes.Reminder;
import ru.nstu.logbook.Client.utils.NoteStorage;

public class MainPageController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button authorizationButton;

    @FXML
    private Label authorizedName;

    @FXML
    private GridPane calendar;

    @FXML
    private DatePicker datePicker;

    @FXML
    private DatePicker dateScroll;

    @FXML
    private AnchorPane menuPane;

    @FXML
    private Label monthLabel;

    @FXML
    private Button nextMonthButton;

    @FXML
    private Button plansButton;

    @FXML
    private Button prevMonthButton;

    @FXML
    private Button registrationButton;

    @FXML
    private ListView<Reminder> remindsList;

    @FXML
    private Button scrollAdmitButton;

    @FXML
    private Label yearLabel;

    LocalDate current = LocalDate.now();
    LocalDate calendarDate = LocalDate.now();
    NoteStorage noteStorage = NoteStorage.getInstance();

    Stage stage;
    Client client;

    Scene notePageScene;
    NotePageController notePageController;

    @FXML
    void nextMonth(ActionEvent event) {
        calendarDate = calendarDate.plusMonths(1);
        drawCalendar();
    }

    @FXML
    void prevMonth(ActionEvent event) {
        calendarDate = calendarDate.minusMonths(1);
        drawCalendar();
    }

    @FXML
    void remindsListEvent(MouseEvent event) {

    }

    @FXML
    void authShow(ActionEvent event) {

    }

    @FXML
    void registrationShow(ActionEvent event) {

    }

    @FXML
    void plansShow(ActionEvent event) {

    }

    @FXML
    void scrollShow(ActionEvent event) {

    }

    @FXML
    void goToDate(ActionEvent event) {
        calendarDate = datePicker.valueProperty().getValue();
        drawCalendar();
    }

    private void drawCalendar(){
        monthLabel.setText(calendarDate.getMonth().name());
        yearLabel.setText(String.valueOf(calendarDate.getYear()));

        int size = calendar.getChildren().size();

        for(int i = 8; i < size; i++){
            calendar.getChildren().removeLast();
        }

        int days = 1;

        LocalDate localDate = LocalDate.of(calendarDate.getYear(), calendarDate.getMonth(), 1);
        noteStorage.loadMonth(localDate);
        //noteStorage.notes.put(current, new Note());
        for(int i = 1; i < 7; i++){
            for(int j = 1; j <= 7; j++){
                if(localDate.getDayOfWeek() == DayOfWeek.of(j)){
                    var dayLabel = new Label(String.valueOf(days));
                    dayLabel.setMinHeight(70);
                    dayLabel.setMinWidth(90);
                    dayLabel.setMaxHeight(70);
                    dayLabel.setMaxWidth(90);
                    dayLabel.setPrefHeight(70);
                    dayLabel.setPrefWidth(90);

                    dayLabel.setBackground(Background.fill(Paint.valueOf("cyan")));


                    LocalDate finalLocalDate = localDate;
                    dayLabel.setOnMouseClicked(e ->  {
                        showNote(finalLocalDate ,noteStorage.notes.get(finalLocalDate));
                    });
                    if(noteStorage.notes.containsKey(localDate))
                        dayLabel.setText(days + "\n" + noteStorage.notes.get(localDate).getTopic());
                    dayLabel.setAlignment(Pos.CENTER);
                    calendar.add(dayLabel, j - 1,  i);

                    if(days == localDate.lengthOfMonth())
                        return;
                    localDate = localDate.plusDays(1);
                    days++;
                }
            }
        }
    }

    void showNote(LocalDate noteDate, Note note){
        if(note == null){
            note = new Note();
            note.setDate(noteDate);
        }
        notePageController.setNote(note);
        stage.setScene(notePageScene);
    }

    public void init(Stage stage,
                     Client client,
                     Scene notePageScene,
                     NotePageController notePageController)
    {
        this.stage = stage;
        this.client = client;
        this.notePageScene = notePageScene;
        this.notePageController = notePageController;


        datePicker.valueProperty().setValue(current);
        monthLabel.setText(current.getMonth().name());
        yearLabel.setText(String.valueOf(current.getYear()));

        for (int i = 0; i < 7; i++)
            calendar.add(new Label(DayOfWeek.of(i + 1).name()), i, 0);

        drawCalendar();
        stage.getScene();
        noteStorage.loadConf();

    }
}
