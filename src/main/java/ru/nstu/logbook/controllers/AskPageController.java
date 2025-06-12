package ru.nstu.logbook.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    RemindStorage remindStorage =  RemindStorage.getInstance();
    NoteStorage noteStorage = NoteStorage.getInstance();
    @FXML
    public void save(ActionEvent event) {
        try {
            DBManager.deleteAllNoteForUser(PageController.getUserId());
            DBManager.deleteAllReminderForUser(PageController.getUserId());
            DBManager.deletePlanForUser(PageController.getUserId());

            synchronized (noteStorage.notes){
                for(var note : noteStorage.loadAll().values())
                    DBManager.addNoteForUser(PageController.getUserId(), note);

            }
            synchronized (remindStorage.reminds){
                for(var remind : remindStorage.loadAll().values())
                    DBManager.addReminderForUser(PageController.getUserId(), remind);
            }
            DBManager.addPlanForUser(PageController.getUserId(), PageController.plan);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        mainController.drawCalendar();
        mainController.drawList();
        mainController.pushButton.setVisible(true);

        stage.close();
    }

    @FXML
    public void download(ActionEvent event) {
        try {
            synchronized (noteStorage.notes) {
                noteStorage.deleteAll();
            }
            synchronized (remindStorage.reminds){
                remindStorage.deleteAll();
            }

            synchronized (noteStorage.notes){
                noteStorage.notes.putAll(DBManager.getNotesForUser(PageController.getUserId()));
                noteStorage.saveAll();
            }

            synchronized (remindStorage.reminds){
                remindStorage.reminds.putAll(DBManager.getRemindsForUser(PageController.getUserId()));
                remindStorage.saveAll();
                for(var id : remindStorage.reminds.keySet()){
                    remindStorage.localId = id < remindStorage.localId ? remindStorage.localId  : id;
                }
            }

            PageController.plan = DBManager.getPlanForUser(PageController.getUserId());

            mainController.drawCalendar();
            mainController.drawList();
            mainController.pushButton.setVisible(true);

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
