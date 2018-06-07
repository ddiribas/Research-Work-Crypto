package ru.ddiribas.controllers;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.*;
import javafx.event.ActionEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import ru.ddiribas.Encryption.EncryptionPerformer;
import ru.ddiribas.Encryption.FileDecryptor;
import ru.ddiribas.Encryption.FileEncryptor;
import ru.ddiribas.Encryption.IntegrityException;
import ru.ddiribas.MainApp;

public class MainController {

    MainApp mainApp;
    Parent parentModal;
    FXMLLoader fxmlLoader;
    WarningController warningController;
    SettingsController settingsController;
    Stage modalStage;

    File src, dst, keyFile;
    boolean integrityControl = true, deleteOriginal = true;

    @FXML
    TextField pathField;
    @FXML
    TextField keyFileField;
    @FXML
    TextArea infConsole;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void showWarningWindow(String label) {
        try {
            fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/fxml/warningWindow.fxml"));
            parentModal = fxmlLoader.load();
            warningController = fxmlLoader.getController();
            warningController.setParent(this);
            warningController.setLabel(label);

            modalStage = new Stage();
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(mainApp.getMainWindow());
            modalStage.setTitle("Error");
            modalStage.setScene(new Scene(parentModal));
            modalStage.setResizable(false);
            modalStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void encrypt(ActionEvent actionEvent) {
        src = new File(pathField.getText());
        dst = src;
        keyFile = new File(keyFileField.getText());
        try {
            FileEncryptor encryptor = FileEncryptor.getEncryptor(src, dst, keyFile, deleteOriginal, integrityControl);
            infConsole.appendText(EncryptionPerformer.performEncryption(encryptor) + "\n");
        } catch (FileNotFoundException e) {
            showWarningWindow(e.getLocalizedMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void decrypt(ActionEvent actionEvent) {
        src = new File(pathField.getText());
        dst = src;
        keyFile = new File(keyFileField.getText());
        try {
            FileDecryptor decryptor = FileDecryptor.getDecryptor(src, dst, keyFile, deleteOriginal, integrityControl);
            infConsole.appendText(EncryptionPerformer.performDecryption(decryptor) + "\n");
        } catch (FileNotFoundException e) {
            showWarningWindow(e.getLocalizedMessage());
        } catch (IntegrityException e) {
            infConsole.appendText(e.getLocalizedMessage());
        } catch (IOException e) {
            e.printStackTrace();
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

    public void settingsMenuItem(ActionEvent actionEvent) {
        try {
            fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/fxml/settingsWindow.fxml"));
            parentModal = fxmlLoader.load();
            settingsController = fxmlLoader.getController();
            settingsController.setParent(this);
            settingsController.setInitial(deleteOriginal, integrityControl);

            modalStage = new Stage();
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(mainApp.getMainWindow());
            modalStage.setTitle("Settings");
            modalStage.setScene(new Scene(parentModal));
            modalStage.setResizable(false);
            modalStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
