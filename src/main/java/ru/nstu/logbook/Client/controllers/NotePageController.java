package ru.nstu.logbook.Client.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ru.nstu.logbook.Client.net.Client;
import ru.nstu.logbook.Client.notes.Note;
import ru.nstu.logbook.Client.utils.NoteStorage;
import ru.nstu.logbook.Shared.trades.TradeInners;
import ru.nstu.logbook.Shared.trades.Transaction;

public class NotePageController extends PageController {


    @FXML
    private TextArea contentArea;

    @FXML
    private Label date;

    @FXML
    private TextField topicText;

    @FXML
    private Button deleteButton;

    Note note;

    public void setNote(Note note) {
        if(note == null)
            this.note = new Note();
        else
            this.note = note;

        deleteButton.setDisable(this.note.getTopic().isEmpty() && this.note.getContent().isEmpty());
        topicText.setText(this.note.getTopic());
        contentArea.setText(this.note.getContent());
        date.setText(this.note.getDate().toString());
    }

    void save() {
        NoteStorage.getInstance().save(note);
        NoteStorage.getInstance().notes.put(note.getDate(), note);
        deleteButton.setDisable(false);
        if(client.getStatus() == Client.Status.CONNECTED){
            for(var neighbour : client.getNeighbours())
                client.offerTrade(neighbour.id, new TradeInners(new Transaction("Note", note)));
        }
    }

    @FXML
    public void delete(ActionEvent event) {
        deleteButton.setDisable(true);
        System.out.println(NoteStorage.getInstance().delete(note));
    }

    @FXML
    void initialize() {

        contentArea.setWrapText(true);
        topicText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                note.setTopic(topicText.getText());
                save();
            }
        });
        contentArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                note.setContent(contentArea.getText());
                save();
            }
        });


    }
}
