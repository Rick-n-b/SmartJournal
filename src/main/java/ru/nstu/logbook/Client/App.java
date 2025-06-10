package ru.nstu.logbook.Client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ru.nstu.logbook.Client.controllers.MainPageController;
import ru.nstu.logbook.Client.controllers.NotePageController;
import ru.nstu.logbook.Client.controllers.RemindPageController;
import ru.nstu.logbook.Client.net.Client;
import ru.nstu.logbook.Client.utils.NoteStorage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("MainPage.fxml"));
        Scene mainScene = new Scene(fxmlLoader.load());
        stage.setTitle("One idiot's logbook");
        stage.setScene(mainScene);
        stage.setResizable(false);

        MainPageController mainController = fxmlLoader.getController();

        Client client = new Client();

        FXMLLoader fxmlNotePageLoader = new FXMLLoader(App.class.getResource("NotePage.fxml"));
        var notePageScene = new Scene(fxmlNotePageLoader.load());
        var notePageController = (NotePageController) fxmlNotePageLoader.getController();
        notePageController.init(stage, client, mainScene, mainController);

        FXMLLoader fxmlRemindPageLoader = new FXMLLoader(App.class.getResource("RemindPage.fxml"));
        var remindPageScene = new Scene(fxmlRemindPageLoader.load());
        var remindPageController = (RemindPageController) fxmlRemindPageLoader.getController();
        remindPageController.init(stage, client, mainScene, mainController);


        mainController.init(stage, client, notePageScene, notePageController, remindPageScene, remindPageController);
        stage.show();



        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                NoteStorage.getInstance().saveConf();
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}