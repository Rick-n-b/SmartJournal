package ru.nstu.logbook.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class ScrollPageController extends PageController{

    @FXML
    VBox mainBox;

    public LocalDate sinceDate = current;

    public LocalDate toDate = current;

    public void setDates(LocalDate sinceDate, LocalDate toDate){
        this.sinceDate = sinceDate;
        this.toDate = toDate;
        noteStorage.loadPeriod(sinceDate, toDate);
        mainBox.getChildren().clear();
        for(var note : noteStorage.notes.values()){
            VBox container = new VBox();
            container.setMinWidth(630);
            container.setMinHeight(50);
            Label topicLabel = new Label(note.getDate().toString() + note.getTopic());
            topicLabel.setWrapText(true);
            topicLabel.setMinWidth(630);
            Label contentLabel = new Label(note.getContent());
            contentLabel.setMinWidth(630);
            contentLabel.setWrapText(true);
            container.getChildren().addAll(topicLabel, contentLabel);
            container.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(event.getButton() == MouseButton.PRIMARY){
                        if(event.getClickCount() == 2){
                            showNote(note.getDate(), note);
                        }
                    }
                }
            });
            mainBox.getChildren().add(container);

        }

    }

    @FXML
    public void initialize(){

    }
}
