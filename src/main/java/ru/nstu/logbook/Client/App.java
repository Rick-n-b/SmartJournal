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

        MainPageController controller = fxmlLoader.getController();

        Client client = new Client();

        FXMLLoader fxmlNotePageLoader = new FXMLLoader(App.class.getResource("NotePage.fxml"));
        var notePageScene = new Scene(fxmlNotePageLoader.load());
        var notePageController = (NotePageController) fxmlNotePageLoader.getController();
        notePageController.init(stage, client, mainScene);


        controller.init(stage, client, notePageScene, notePageController);
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