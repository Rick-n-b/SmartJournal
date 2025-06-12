package ru.nstu.logbook.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.nstu.logbook.net.DBManager;
import ru.nstu.logbook.utils.NoteStorage;
import ru.nstu.logbook.utils.RemindStorage;

import java.sql.SQLException;

public class AskPageController {


    @FXML
    Button downloadButton;
    @FXML
    Button saveButton;
    @FXML
    Button cancelButton;
    @FXML
    Label messageLabel;

    Stage stage;

    Scene mainScene;
    MainPageController mainController;

    Scene authPageScene;
    AuthorisationPageController authPageController;

    @FXML
    public void save(ActionEvent event) {
        try {
            DBManager.deleteAllNoteForUser(PageController.getUserId());
            DBManager.deleteAllReminderForUser(PageController.getUserId());
            DBManager.deletePlanForUser(PageController.getUserId());

            for(var note : NoteStorage.getInstance().loadAll().values())
                DBManager.addNoteForUser(PageController.getUserId(), note);
            for(var remind : RemindStorage.getInstance().loadAll())
                DBManager.addReminderForUser(PageController.getUserId(), remind);
            DBManager.addPlanForUser(PageController.getUserId(), PageController.plan);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        mainController.drawCalendar();
        mainController.drawList();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                synchronized (mainController.updateThread){
                    mainController.updateThread.notify();
                }
            }
        });
        stage.close();
    }

    @FXML
    public void download(ActionEvent event) {
        try {
            NoteStorage.getInstance().deleteAll();
            RemindStorage.getInstance().deleteAll();

            NoteStorage.getInstance().notes.putAll(DBManager.getNotesForUser(PageController.getUserId()));
            NoteStorage.getInstance().saveAll();

            RemindStorage.getInstance().reminds.addAll(DBManager.getRemindsForUser(PageController.getUserId()));
            RemindStorage.getInstance().saveAll();
            PageController.plan = DBManager.getPlanForUser(PageController.getUserId());
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    synchronized (mainController.updateThread){
                        mainController.updateThread.notify();
                    }
                }
            });

            mainController.drawCalendar();
            mainController.drawList();
            stage.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void cancel(ActionEvent event){
        PageController.setUserId(-1);
        PageController.setUserName("Unreg");
        mainController.authorizedName.setText("Unreg");
        stage.setScene(authPageScene);
    }



    public void init(Stage stage,
                     Scene mainScene, MainPageController mainController,
                     Scene authPageScene, AuthorisationPageController authPageController)
    {
        this.stage = stage;
        this.mainScene = mainScene;
        this.mainController = mainController;
        this.authPageScene = authPageScene;
        this.authPageController = authPageController;
        stage.setScene(authPageScene);
        stage.setResizable(false);
        stage.setTitle("Authorization");

        stage.setOnCloseRequest(e -> cancel(new ActionEvent()));
    }
}
