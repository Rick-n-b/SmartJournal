package ru.nstu.logbook.Client.controllers;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import ru.nstu.logbook.Client.notes.ShortNote;
import ru.nstu.logbook.Client.reminds.ShortReminder;
import ru.nstu.logbook.Client.utils.CalendarWeek;

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
    private TableView<CalendarWeek> calendar;

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
    private ListView<ShortReminder> remindsList;

    @FXML
    private Button scrollAdmitButton;

    @FXML
    private Label yearLabel;

    LocalDate current = LocalDate.now();

    List<TableColumn<CalendarWeek, ShortNote>> columns = new ArrayList<TableColumn<CalendarWeek, ShortNote>>();


    @FXML
    void nextMonth(ActionEvent event) {
        current = current.plusMonths(1);
        monthLabel.setText(current.getMonth().name());
        yearLabel.setText(String.valueOf(current.getYear()));
    }

    @FXML
    void prevMonth(ActionEvent event) {
        current = current.minusMonths(1);
        monthLabel.setText(current.getMonth().name());
        yearLabel.setText(String.valueOf(current.getYear()));
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

    }

    @FXML
    void initialize() {

        datePicker.valueProperty().setValue(current);
        monthLabel.setText(current.getMonth().name());
        yearLabel.setText(String.valueOf(current.getYear()));


        for (int i = 1; i <= 7; i++) {
            columns.add(new TableColumn<CalendarWeek, ShortNote>(DayOfWeek.of(i).name()));
            columns.getLast().setCellValueFactory(new PropertyValueFactory<CalendarWeek, ShortNote>("note" + String.valueOf(i)));
            columns.getLast().setSortable(false);
            columns.getLast().setReorderable(false);
            columns.getLast().setPrefWidth(90);
            columns.getLast().setEditable(false);
            calendar.getColumns().add(columns.getLast());
        }
        calendar.setEditable(false);
        var week1 = new CalendarWeek();
        week1.note1 = new ShortNote(LocalDate.of(2025, 6, 12));
        week1.note2 = new ShortNote(LocalDate.of(2025, 6, 13));
        week1.note6 = new ShortNote(LocalDate.of(2025, 6, 16));
        var week2 = new CalendarWeek();
        week2.note1 = new ShortNote(LocalDate.of(2025, 6, 19));
        week2.note2 = new ShortNote(LocalDate.of(2025, 6, 20));

        ObservableList<CalendarWeek> notes = FXCollections.observableArrayList(
                week1,
                week2
        );
        calendar.setItems(notes);


    }

}
