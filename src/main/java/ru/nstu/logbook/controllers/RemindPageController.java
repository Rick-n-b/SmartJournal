package ru.nstu.logbook.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ru.nstu.logbook.net.DBManager;
import ru.nstu.logbook.notes.Reminder;
import ru.nstu.logbook.utils.RemindStorage;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private static String oldTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

    ChangeListener<String> topicListener = new ChangeListener<String>() {
        @Override
        public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
            deleteButton.setDisable(false);

            if(oldValue != null){
                Reminder oldReminder = reminder;
                oldReminder.setTopic(oldValue);
                RemindStorage.getInstance().delete(oldReminder);
            }

            reminder.setTopic(newValue);
            RemindStorage.getInstance().reminds.put(reminder.getId(), reminder);
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
            reminder.setContent(newValue);
            RemindStorage.getInstance().reminds.put(reminder.getId(), reminder);
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
                System.out.println(RemindStorage.getInstance().delete(oldReminder));
            }
            if(remindDate.getValue().isAfter(current.minusDays(1))){
                reminder.setExpirationDate(newValue);
                RemindStorage.getInstance().reminds.put(reminder.getId(), reminder);
                save(reminder);
                drawList();
            }
        }
    };

    ChangeListener<String> timeListener = new ChangeListener<String>() {
        @Override
        public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {

            if(oldValue.matches("2[0-3]:[0-5][0-9]$") || oldValue.matches("[0-1][0-9]:[0-5][0-9]$")){
                oldTime = oldValue;
            }

            if(newValue.matches("2[0-3]:[0-5][0-9]$") || newValue.matches("[0-1][0-9]:[0-5][0-9]$")){
                deleteButton.setDisable(false);
                if(oldTime != null){
                    Reminder oldReminder = reminder;
                    var timeToDelete = LocalTime.parse(oldTime, DateTimeFormatter.ofPattern("HH:mm"));
                    oldReminder.setExpirationTime(timeToDelete);
                    RemindStorage.getInstance().delete(oldReminder);
                }
                reminder.setExpirationTime(LocalTime.parse(newValue, DateTimeFormatter.ofPattern("HH:mm")));
                RemindStorage.getInstance().reminds.put(reminder.getId(), reminder);
                save(reminder);
                drawList();
            }
        }
    };

    Reminder reminder = new Reminder();

    public void setRemind(Reminder rem) {
        if (rem == null) {
            this.reminder = new Reminder(++remindStorage.localId);
        } else {
            reminder = rem;
        }
        topicText.setText(reminder.getTopic());
        contentArea.setText(reminder.getContent());
        remindDate.setValue(reminder.getExpirationDate());
        remindTime.setText(reminder.getExpirationTime().format(DateTimeFormatter.ofPattern("HH:mm")));

        deleteButton.setDisable(reminder.getTopic().isEmpty() && reminder.getContent().isEmpty());

        addListeners();
    }

    @FXML
    public void delete(ActionEvent event) {
        removeListeners();
        deleteButton.setDisable(true);
        del(reminder);
        addListeners();
        drawList();
    }

    @Override
    public void del(Reminder rem) {
        if(rem.getId() == reminder.getId()){
            topicText.clear();
            contentArea.clear();
            remindDate.setValue(LocalDate.now().plusDays(1));
            remindTime.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
        if(PageController.getUserId() != -1){
            try {
                DBManager.deleteReminderForUser(PageController.getUserId(),  reminder);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        remindStorage.delete(rem);
        remindsList.getItems().remove(rem);
        drawList();
    }

    @FXML
    public void back(ActionEvent event){
        removeListeners();
        remindDate.setValue(LocalDate.now().plusDays(1));
        remindTime.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        topicText.clear();
        contentArea.clear();
        stage.setScene(mainScene);
        mainPageController.drawList();
    }

    void removeListeners(){
        topicText.textProperty().removeListener(topicListener);
        contentArea.textProperty().removeListener(contentListener);
        remindDate.valueProperty().removeListener(dateListener);
        remindTime.textProperty().removeListener(timeListener);
    }
    void addListeners(){
        topicText.textProperty().addListener(topicListener);
        contentArea.textProperty().addListener(contentListener);
        remindDate.valueProperty().addListener(dateListener);
        remindTime.textProperty().addListener(timeListener);
    }

    @FXML
    void initialize() {
        remindTime.setPrefColumnCount(5);
        contentArea.setWrapText(true);
        remindDate.setValue(LocalDate.now().plusDays(1));
        remindTime.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
    }
}
