package ru.ddiribas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import ru.ddiribas.controllers.MainController;

import java.io.IOException;
import java.security.Security;

public class MainApp extends Application {

    private Stage mainWindow;
    public Stage getMainWindow() {
        return mainWindow;
    }

    private static MainController mainController;
    public static MainController getMainController() {
        return mainController;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.mainWindow = primaryStage;
        mainWindow.setTitle("ddiribas file encryptor");
        showMainWindow();
        mainWindow.setMinWidth(mainWindow.getWidth());
        mainWindow.setMinHeight(mainWindow.getHeight());
    }

    public void showMainWindow () {
        try {
            //Load main window
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(MainApp.class.getResourceAsStream("/fxml/mainWindow.fxml"));
            //Set scene containing the main window
            mainWindow.setScene(new Scene(root));
            // Give the controller access to the main application
            mainController = loader.getController();
            mainController.setMainApp(this);

            mainWindow.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}