package ru.nstu.logbook.Client.controllers;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import ru.nstu.logbook.Client.net.Client;
import ru.nstu.logbook.Client.notes.Note;
import ru.nstu.logbook.Client.notes.Reminder;
import ru.nstu.logbook.Client.utils.NoteStorage;
import ru.nstu.logbook.Client.utils.RemindStorage;

public class MainPageController {

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
    RemindStorage remindStorage = RemindStorage.getInstance();

    Stage stage;
    Client client;

    Scene notePageScene;
    NotePageController notePageController;

    Scene remindPageScene;
    RemindPageController remindPageController;


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

    @FXML
    void back(ActionEvent event) {

    }

    @FXML
    void delete(ActionEvent event) {

    }

    public void drawCalendar() {
        monthLabel.setText(calendarDate.getMonth().name());
        yearLabel.setText(String.valueOf(calendarDate.getYear()));

        int size = calendar.getChildren().size();

        for (int i = 8; i < size; i++) {
            calendar.getChildren().removeLast();
        }

        int days = 1;

        LocalDate localDate = LocalDate.of(calendarDate.getYear(), calendarDate.getMonth(), 1);
        noteStorage.loadMonth(localDate);
        //noteStorage.reminds.put(current, new Note());
        for (int i = 1; i < 7; i++) {
            for (int j = 1; j <= 7; j++) {
                if (localDate.getDayOfWeek() == DayOfWeek.of(j)) {
                    var dayLabel = new Label(String.valueOf(days));
                    dayLabel.setMaxSize(90, 70);
                    dayLabel.setMinSize(90, 70);
                    dayLabel.setPrefSize(90, 70);
                    dayLabel.setTextAlignment(TextAlignment.CENTER);
                    dayLabel.setWrapText(true);
                    dayLabel.setAlignment(Pos.TOP_CENTER);
                    dayLabel.setStyle("fx-background-color: rgba(255, 2, 2, .7);");//не робит, нужно подключать CSS
                    LocalDate finalLocalDate = localDate;
                    dayLabel.setOnMouseClicked(e -> {
                        showNote(finalLocalDate, noteStorage.notes.get(finalLocalDate));
                    });
                    if (noteStorage.notes.containsKey(localDate))
                        dayLabel.setText(days + "\n" + noteStorage.notes.get(localDate).getTopic());

                    calendar.add(dayLabel, j - 1, i);

                    if (days == localDate.lengthOfMonth())
                        return;
                    localDate = localDate.plusDays(1);
                    days++;
                }
            }
        }
    }

    public void drewList(){
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

    void showNote(LocalDate noteDate, Note note){
        if(note == null){
            note = new Note();
            note.setDate(noteDate);
        }
        notePageController.setNote(note);
        stage.setScene(notePageScene);
    }

    void showReminder(Reminder reminder){
        stage.setScene(remindPageScene);
    }

    public void init(Stage stage,
                     Client client,
                     Scene notePageScene,
                     NotePageController notePageController,
                     Scene remindPageScene,
                     RemindPageController remindPageController)
    {
        this.stage = stage;
        this.client = client;
        this.notePageScene = notePageScene;
        this.notePageController = notePageController;
        this.remindPageScene = remindPageScene;
        this.remindPageController = remindPageController;


        datePicker.valueProperty().setValue(current);
        monthLabel.setText(current.getMonth().name());
        yearLabel.setText(String.valueOf(current.getYear()));

        for (int i = 0; i < 7; i++)
            calendar.add(new Label(DayOfWeek.of(i + 1).name()), i, 0);

        calendar.setAlignment(Pos.CENTER);
        drawCalendar();
        stage.getScene();
        noteStorage.loadConf();

        remindsList.setEditable(false);



    }
}
