package ru.nstu.logbook.Client.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ru.nstu.logbook.Client.notes.Reminder;
import ru.nstu.logbook.Client.utils.RemindStorage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class RemindPageController extends PageController {

    @FXML
    private TextArea contentArea;

    @FXML
    private DatePicker remindDate;

    @FXML
    private TextField remindTime;

    @FXML
    private TextField topicText;


    ChangeListener<String> topicListener = new ChangeListener<String>() {
        @Override
        public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
            deleteButton.setDisable(false);

            if(oldValue != null){
                Reminder oldReminder = reminder;
                oldReminder.setTopic(oldValue);
                RemindStorage.getInstance().delete(oldReminder);
            }

            reminder.setTopic(topicText.getText());
            save(reminder);
            drawList();

        }
    };
    ChangeListener<String> contentListener =  new ChangeListener<String>() {
        @Override
        public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {

            deleteButton.setDisable(false);
            if(oldValue != null){
                Reminder oldReminder = reminder;
                oldReminder.setContent(oldValue);
                RemindStorage.getInstance().delete(oldReminder);
            }
            reminder.setContent(contentArea.getText());
            save(reminder);
            drawList();
        }
    };
    ChangeListener<LocalDate> dateListener = new ChangeListener<LocalDate>() {
        @Override
        public void changed(final ObservableValue<? extends LocalDate> observable, final LocalDate oldValue, final LocalDate newValue) {

            deleteButton.setDisable(false);
            if(oldValue != null) {
                Reminder oldReminder = reminder;
                oldReminder.setExpirationDate(oldValue);
                RemindStorage.getInstance().delete(oldReminder);
            }
            if(remindDate.getValue().isAfter(current.minusDays(1))){
                reminder.setExpirationDate(remindDate.getValue());
                remindDate.setValue(current);
            }

            save(reminder);
            drawList();
        }
    };
    ChangeListener<String> timeListener = new ChangeListener<String>() {
        @Override
        public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
            if (remindTime.getText().matches("[0-2][0-4]:[0-5][0-9]$")) {
                deleteButton.setDisable(false);
                if(oldValue != null) {
                    Reminder oldReminder = reminder;
                    var time = LocalTime.parse(oldValue);
                    oldReminder.setExpirationTime(time);
                    RemindStorage.getInstance().delete(oldReminder);
                }
                reminder.setExpirationTime(LocalTime.parse(newValue));
                save(reminder);
                drawList();
            }
        }
    };

    Reminder reminder = new Reminder();

    public void setRemind(Reminder rem) {
        if (rem == null) {
            this.reminder = new Reminder();
        } else {
            this.reminder = rem;
            topicText.setText(rem.getTopic());
            contentArea.setText(rem.getContent());
            remindDate.setValue(rem.getExpirationDate());
            remindTime.setText(rem.getExpirationTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        }

        deleteButton.setDisable(this.reminder.getTopic().isEmpty() && this.reminder.getContent().isEmpty());

        topicText.textProperty().addListener(topicListener);
        contentArea.textProperty().addListener(contentListener);
        remindDate.valueProperty().addListener(dateListener);
        remindTime.textProperty().addListener(timeListener);

    }

    @FXML
    public void delete(ActionEvent event) {
        deleteButton.setDisable(true);
        RemindStorage.getInstance().delete(reminder);
        drawList();
    }

    @FXML
    public void back(ActionEvent event){
        drawList();
        topicText.textProperty().removeListener(topicListener);
        contentArea.textProperty().removeListener(contentListener);
        remindDate.valueProperty().removeListener(dateListener);
        remindTime.textProperty().removeListener(timeListener);
        stage.setScene(mainScene);
        mainPageController.drawList();
    }

    @FXML
    void initialize() {

        contentArea.setWrapText(true);
        remindDate.setValue(LocalDate.now().plusDays(1));
        remindTime.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));


    }
}
