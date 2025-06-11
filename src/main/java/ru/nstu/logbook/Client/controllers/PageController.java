package ru.nstu.logbook.Client.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ru.nstu.logbook.Client.net.Client;
import ru.nstu.logbook.Client.notes.Note;
import ru.nstu.logbook.Client.notes.Reminder;
import ru.nstu.logbook.Client.utils.NoteStorage;
import ru.nstu.logbook.Client.utils.RemindStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PageController {

    @FXML
    public Button authorizationButton;

    @FXML
    public Label authorizedName;

    @FXML
    public DatePicker scrollDateSince;

    @FXML
    public DatePicker scrollDateTo;

    @FXML
    public AnchorPane menuPane;

    @FXML
    public Button deleteButton;

    @FXML
    public Button backButton;

    @FXML
    public Button registrationButton;

    @FXML
    public ListView<Reminder> remindsList;

    @FXML
    public Button scrollAcceptButton;

    LocalDate current = LocalDate.now();
    LocalDate calendarDate = LocalDate.now();

    NoteStorage noteStorage = NoteStorage.getInstance();
    RemindStorage remindStorage = RemindStorage.getInstance();

    public Stage stage;
    public Client client;

    public Scene mainScene;
    public MainPageController mainPageController;

    public Scene notePageScene;
    public NotePageController notePageController;

    public Scene remindPageScene;
    public RemindPageController remindPageController;

    public Scene plansPageScene;
    public PlansPageController plansPageController;

    public Scene scrollPageScene;
    public ScrollPageController scrollPageController;


    @FXML
    public void remindsListEvent(MouseEvent event) {

    }

    @FXML
    public void authShow(ActionEvent event) {

    }

    @FXML
    public void registrationShow(ActionEvent event) {

    }

    @FXML
    public void plansShow(ActionEvent event) {
        stage.setScene(plansPageScene);
        plansPageController.drawList();
    }

    @FXML
    public void scrollShow(ActionEvent event) {
        if(scrollDateSince.getValue() != null && scrollDateTo.getValue() == null){
            if(!scrollDateSince.getValue().isAfter(current)){
                scrollPageController.setDates(scrollDateSince.getValue(), current);
                stage.setScene(scrollPageScene);
            }
        }else if(scrollDateSince.getValue() != null && scrollDateTo.getValue() != null){
            if(!scrollDateSince.getValue().isAfter(scrollDateTo.getValue()))
            {
                scrollPageController.setDates(scrollDateSince.getValue(), scrollDateTo.getValue());
                stage.setScene(scrollPageScene);
            }
        }
    }

    public void showNote(LocalDate noteDate, Note note){
        if(note == null){
            note = new Note();
            note.setDate(noteDate);
        }
        notePageController.setNote(note);
        stage.setScene(notePageScene);
    }

    public void save(Reminder reminder) {
        RemindStorage.getInstance().save(reminder);
        RemindStorage.getInstance().reminds.add(reminder);
        deleteButton.setDisable(false);
    }

    public void save(Note note) {
        NoteStorage.getInstance().save(note);
        NoteStorage.getInstance().notes.put(note.getDate(), note);
        deleteButton.setDisable(false);
    }

    @FXML
    public void back(ActionEvent event) {
        drawList();
        stage.setScene(mainScene);
        mainPageController.drawCalendar();
        mainPageController.drawList();
    }

    @FXML
    public void delete(ActionEvent event) {
        drawList();
    }

    public void drawList() {
        remindsList.getItems().clear();
        RemindStorage.getInstance().loadMonth(current);
        ObservableList<Reminder> observableList = FXCollections.observableList(
            RemindStorage.getInstance().reminds
        );
        System.out.println("------ " + this + " ------");
        for(var rem : observableList){
            System.out.println(rem.toString() + "\n");
        }
        System.out.println("----------------------");


        remindsList.setItems(observableList);

        System.out.println("------ " + "list" + " ------");
        for(var rem : remindsList.getItems()){
            System.out.println(rem.toString() + "\n");
        }
        System.out.println("----------------------");
    }

    public void showReminder(Reminder reminder) {
        remindPageController.setRemind(reminder);
        stage.setScene(remindPageScene);
    }

    public ContextMenu listCM = new ContextMenu();

    public MenuItem listAdd = new MenuItem("Add");
    public MenuItem listChange = new MenuItem("Change");
    public MenuItem listDelete = new MenuItem("Delete");

    public ListCell<Reminder> createList(ListView<Reminder> listView){
        return new ListCell<>(){
            @Override
            protected void updateItem(Reminder reminder, boolean empty){
                super.updateItem(reminder, empty);
                if(empty || reminder == null){
                    setText(null);
                }
                else{
                    setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            if(event.getButton().equals(MouseButton.PRIMARY)){
                                if(event.getClickCount() == 2){
                                    if(getItem()!= null){
                                        showReminder(reminder);
                                    }
                                }
                            }
                        }
                    });
                    setWrapText(true);
                    setText(reminder.getExpirationDate().toString() + " " +
                            reminder.getExpirationTime().format(DateTimeFormatter.ofPattern("H:m")) + " " +
                            reminder.getTopic());
                    ContextMenu cellCM = new ContextMenu();
                    MenuItem add = new MenuItem("Add");
                    MenuItem change = new MenuItem("Change");
                    MenuItem delete = new MenuItem("Delete");
                    add.setOnAction(e -> showReminder(null));
                    change.setOnAction(e -> showReminder(reminder));
                    delete.setOnAction(e -> del(reminder));
                    cellCM.getItems().addAll(add, change, delete);
                    setContextMenu(cellCM);
                }
            }
        };
    }

    public void del(Reminder rem){
        if(this == remindPageController){
            remindPageController.del(rem);
        }else{
            remindStorage.delete(rem);
            drawList();
        }
    }

    public void init(Stage stage,
                     Client client,
                     Scene mainScene,
                     MainPageController mainPageController,
                     Scene notePageScene,
                     NotePageController notePageController,
                     Scene remindPageScene,
                     RemindPageController remindPageController,
                     Scene plansPageScene,
                     PlansPageController plansPageController,
                     Scene scrollPageScene,
                     ScrollPageController scrollPageController)
    {
        this.stage = stage;
        this.client = client;

        this.mainScene = mainScene;
        this.mainPageController = mainPageController;
        this.notePageScene = notePageScene;
        this.notePageController = notePageController;
        this.remindPageScene = remindPageScene;
        this.remindPageController = remindPageController;
        this.plansPageScene = plansPageScene;
        this.plansPageController = plansPageController;
        this.scrollPageScene = scrollPageScene;
        this.scrollPageController = scrollPageController;

        if(this != mainPageController){
            scrollDateTo.valueProperty().bindBidirectional(mainPageController.scrollDateTo.valueProperty());
            scrollDateSince.valueProperty().bindBidirectional(mainPageController.scrollDateSince.valueProperty());
        }

        if(this != remindPageController){
            remindsList.itemsProperty().bindBidirectional(remindPageController.remindsList.itemsProperty());
        }

        remindsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        remindsList.setCellFactory(r -> createList(this.remindsList));
        remindsList.setEditable(false);
        remindsList.setFocusTraversable(false);
        remindsList.setTooltip(new Tooltip("Double click to create reminder"));
        remindsList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.PRIMARY)
                    if(event.getClickCount() == 2)
                        showReminder(null);
            }
        });

        listAdd.setOnAction(e -> {
            showReminder(null);
        });
        listCM.getItems().addAll(listAdd, listChange, listDelete);
        listCM.getItems().get(2).setDisable(true);
        listCM.getItems().get(1).setDisable(true);
        remindsList.setContextMenu(listCM);
        drawList();
    }
}
