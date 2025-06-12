package ru.nstu.logbook.controllers;

import java.time.DayOfWeek;
import java.time.LocalDate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;

public class MainPageController extends PageController {


    @FXML
    private GridPane calendar;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Label monthLabel;

    @FXML
    private Button nextMonthButton;

    @FXML
    private Button prevMonthButton;

    @FXML
    private Label yearLabel;

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
    void goToDate(ActionEvent event) {
        calendarDate = datePicker.valueProperty().getValue();
        drawCalendar();
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

    @FXML
    void initialize(){
        datePicker.valueProperty().setValue(current);
        monthLabel.setText(current.getMonth().name());
        yearLabel.setText(String.valueOf(current.getYear()));

        for (int i = 0; i < 7; i++)
            calendar.add(new Label(DayOfWeek.of(i + 1).name()), i, 0);

        calendar.setAlignment(Pos.CENTER);
        drawCalendar();
        noteStorage.loadConf();
    }
}
