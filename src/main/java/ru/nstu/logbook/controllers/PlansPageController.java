package ru.nstu.logbook.controllers;

import java.io.*;
import java.sql.SQLException;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ru.nstu.logbook.net.DBManager;

public class PlansPageController extends PageController{

    @FXML
    TextArea contentArea;
    @FXML
    Label date;

    String path = "./src/main/resources/ru/nstu/logbook/";
    File file = new File(path, "plans.bin");

    public void save(){
        if(file.exists()) {

            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                out.writeObject(plan);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    void initialize() {
        date.setText(current.toString());
        if (!file.exists()) {
            try {
                file.createNewFile();
                DBManager.addPlanForUser(PageController.getUserId(), plan);
                save();
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
            plan = "";
        } else {
            try (var in = new ObjectInputStream(new FileInputStream(file))) {
                plan = (String) in.readObject();
                contentArea.setText(plan);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        deleteButton.setDisable(true);
        deleteButton.setVisible(false);
        contentArea.setWrapText(true);
        contentArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                plan = newValue;
                save();
            }
        }  );
    }
}
