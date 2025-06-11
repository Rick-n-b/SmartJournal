package ru.nstu.logbook.Client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ru.nstu.logbook.Client.controllers.*;
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

        FXMLLoader fxmlRemindPageLoader = new FXMLLoader(App.class.getResource("RemindPage.fxml"));
        var remindPageScene = new Scene(fxmlRemindPageLoader.load());
        var remindPageController = (RemindPageController) fxmlRemindPageLoader.getController();

        FXMLLoader fxmlPlansPageLoader = new FXMLLoader(App.class.getResource("PlansPage.fxml"));
        var plansPageScene = new Scene(fxmlPlansPageLoader.load());
        var plansPageController = (PlansPageController) fxmlPlansPageLoader.getController();

        FXMLLoader fxmlScrollPageLoader = new FXMLLoader(App.class.getResource("ScrollPage.fxml"));
        var scrollPageScene = new Scene(fxmlScrollPageLoader.load());
        var scrollPageController = (ScrollPageController) fxmlScrollPageLoader.getController();

        FXMLLoader fxmlAuthPageLoader = new FXMLLoader(App.class.getResource("AuthorisationPage.fxml"));
        var authPageScene = new Scene(fxmlAuthPageLoader.load());
        var authPageController = (AuthorisationPageController) fxmlAuthPageLoader.getController();

        notePageController.init(stage, client,
                mainScene, mainController,
                notePageScene, notePageController,
                remindPageScene, remindPageController,
                plansPageScene, plansPageController,
                scrollPageScene, scrollPageController,
                authPageScene, authPageController);

        remindPageController.init(stage, client,
                mainScene, mainController,
                notePageScene, notePageController,
                remindPageScene, remindPageController,
                plansPageScene, plansPageController,
                scrollPageScene, scrollPageController,
                authPageScene, authPageController);

        mainController.init(stage, client,
                mainScene, mainController,
                notePageScene, notePageController,
                remindPageScene, remindPageController,
                plansPageScene, plansPageController,
                scrollPageScene, scrollPageController,
                authPageScene, authPageController);

        plansPageController.init(stage, client,
                mainScene, mainController,
                notePageScene, notePageController,
                remindPageScene, remindPageController,
                plansPageScene, plansPageController,
                scrollPageScene, scrollPageController,
                authPageScene, authPageController);

        scrollPageController.init(stage, client,
                mainScene, mainController,
                notePageScene, notePageController,
                remindPageScene, remindPageController,
                plansPageScene, plansPageController,
                scrollPageScene, scrollPageController,
                authPageScene, authPageController);

        Stage authStage = new Stage();
        authStage.initModality(Modality.WINDOW_MODAL);
        authStage.initOwner(stage.getScene().getWindow());

        authPageController.init(authStage, client,
                mainScene, mainController,
                authPageScene, authPageController);


        stage.show();

        stage.setOnCloseRequest(e-> {
                NoteStorage.getInstance().saveConf();
                Platform.exit();
                System.exit(0);
            }
        );
    }

    public static void main(String[] args) {
        launch();
    }
}