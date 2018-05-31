package ru.ddiribas.controllers;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.TextField;
import javafx.stage.*;
import javafx.event.ActionEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import ru.ddiribas.Encryption.EncryptionPerformer;
import ru.ddiribas.MainApp;

public class MainController {

    MainApp mainApp;
    Parent parentWarning;
    FXMLLoader fxmlLoader;
    WarningController warningController;
    Stage warningStage;

    File src, dst, keyFile;

    @FXML
    TextField pathField;
    @FXML
    TextField keyFileField;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void showWarningWindow(String label) {
        try {
            fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/fxml/warningWindow.fxml"));
            parentWarning = fxmlLoader.load();
            warningController = fxmlLoader.getController();
            warningController.setParent(this);
            warningController.setLabel(label);

            warningStage = new Stage();
            warningStage.initModality(Modality.WINDOW_MODAL);
            warningStage.initOwner(mainApp.getMainWindow());
            warningStage.setTitle("Error");
            warningStage.setScene(new Scene(parentWarning));
            warningStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void encrypt(ActionEvent actionEvent) {
        src = new File(pathField.getText());
        dst = src;
        keyFile = new File(keyFileField.getText());
        try {
            EncryptionPerformer.performEncryption(src, src, keyFile);
        } catch (FileNotFoundException e) {
            showWarningWindow(e.getLocalizedMessage());
        }
    }

    public void decrypt(ActionEvent actionEvent) {
        src = new File(pathField.getText());
        dst = src;
        keyFile = new File(keyFileField.getText());
        try {
            EncryptionPerformer.performDecryption(src, src, keyFile);
        } catch (FileNotFoundException e) {
            showWarningWindow(e.getLocalizedMessage());
        }
    }

    public void viewPath(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Path");
        pathField.setText(directoryChooser.showDialog(((Node)actionEvent.getSource()).getScene().getWindow()).getAbsolutePath());
    }

    public void viewKeyFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Key File");
        keyFileField.setText(fileChooser.showOpenDialog(((Node)actionEvent.getSource()).getScene().getWindow()).getAbsolutePath());
    }



}
