package ru.nstu.logbook;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.nstu.logbook.controllers.*;
import ru.nstu.logbook.net.DBManager;
import ru.nstu.logbook.utils.NoteStorage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
        Scene mainScene = new Scene(fxmlLoader.load());
        stage.setTitle("One idiot's logbook");
        stage.setScene(mainScene);
        stage.setResizable(false);

        MainPageController mainController = fxmlLoader.getController();

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

        FXMLLoader fxmlRegPageLoader = new FXMLLoader(App.class.getResource("RegistrationPage.fxml"));
        var regPageScene = new Scene(fxmlRegPageLoader.load());
        var registrationPageController = (RegistrationPageController) fxmlRegPageLoader.getController();

        notePageController.init(stage,
                mainScene, mainController,
                notePageScene, notePageController,
                remindPageScene, remindPageController,
                plansPageScene, plansPageController,
                scrollPageScene, scrollPageController,
                authPageScene, authPageController,
                regPageScene, registrationPageController);

        remindPageController.init(stage,
                mainScene, mainController,
                notePageScene, notePageController,
                remindPageScene, remindPageController,
                plansPageScene, plansPageController,
                scrollPageScene, scrollPageController,
                authPageScene, authPageController,
                regPageScene, registrationPageController);

        mainController.init(stage,
                mainScene, mainController,
                notePageScene, notePageController,
                remindPageScene, remindPageController,
                plansPageScene, plansPageController,
                scrollPageScene, scrollPageController,
                authPageScene, authPageController,
                regPageScene, registrationPageController);

        plansPageController.init(stage,
                mainScene, mainController,
                notePageScene, notePageController,
                remindPageScene, remindPageController,
                plansPageScene, plansPageController,
                scrollPageScene, scrollPageController,
                authPageScene, authPageController,
                regPageScene, registrationPageController);

        scrollPageController.init(stage,
                mainScene, mainController,
                notePageScene, notePageController,
                remindPageScene, remindPageController,
                plansPageScene, plansPageController,
                scrollPageScene, scrollPageController,
                authPageScene, authPageController,
                regPageScene, registrationPageController);

        Stage authStage = new Stage();
        authStage.initModality(Modality.WINDOW_MODAL);
        authStage.initOwner(stage.getScene().getWindow());

        Stage regStage = new Stage();
        regStage.initModality(Modality.WINDOW_MODAL);
        regStage.initOwner(stage.getScene().getWindow());

        authPageController.init(authStage,
                mainScene, mainController,
                authPageScene, authPageController);

        registrationPageController.init(authStage,
                mainScene, mainController,
                regPageScene, registrationPageController);


        stage.show();

        stage.setOnCloseRequest(e-> {
                NoteStorage.getInstance().saveConf();
                Platform.exit();
                System.exit(0);
                DBManager.closeConnection();
            }
        );
    }

    public static void main(String[] args) {
        launch();
    }
}