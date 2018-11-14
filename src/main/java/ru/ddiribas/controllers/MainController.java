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

    private MainApp mainApp;
    private Parent parentModal;
    private FXMLLoader fxmlLoader;
    private ErrorController errorController;
    private WarningController warningController;
    private SettingsController settingsController;
    private PasswordController passwordController;
    Stage modalStage;

    private File src, dst, keyFile;
    boolean integrityControl = true, deleteOriginal = true, fingerPrint = true, encryptName = true;

    @FXML
    TextField pathField;
    @FXML
    TextField keyFileField;
    @FXML
    TextArea infConsole;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public WarningController getWarningController() {
        return warningController;
    }

    private void showErrorWindow(String label) {
        try {
            fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/fxml/errorWindow.fxml"));
            parentModal = fxmlLoader.load();
            errorController = fxmlLoader.getController();
            errorController.setParent(this);
            errorController.setLabel(label);

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

    public byte[] requestPassword() {
        try {
            fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/fxml/passwordRequest.fxml"));
            parentModal = fxmlLoader.load();
            passwordController = fxmlLoader.getController();
            passwordController.setParent(this);

            modalStage = new Stage();
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(mainApp.getMainWindow());
            modalStage.setTitle("Enter the password");
            modalStage.setScene(new Scene(parentModal));
            modalStage.setResizable(false);
            modalStage.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showWarningWindow (String label) {
        try {
            fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/fxml/warningWindow.fxml"));
            parentModal = fxmlLoader.load();
            warningController = fxmlLoader.getController();
            warningController.setParent(this);
            warningController.setLabel(label);
            warningController.continueExecution = true;

            modalStage = new Stage();
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(mainApp.getMainWindow());
            modalStage.setTitle("Warning");
            modalStage.setScene(new Scene(parentModal));
            modalStage.setResizable(false);
            modalStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*TODO: Разобраться с нестандартными ситуациями:
    Изменение файла между зашифрованием и расшифрованием
    В частности нестандартная длина зашифрованного файла (падает, когда часть, отвечающая за данные самого файла, некратна длине блока)
     */
    public void encrypt(ActionEvent actionEvent) {
        src = new File(pathField.getText());
        dst = src;
        keyFile = new File(keyFileField.getText());
        try {
            FileEncryptor encryptor = FileEncryptor.getEncryptor(src, dst, keyFile, deleteOriginal, integrityControl, fingerPrint, encryptName);
            EncryptionPerformer.prepareForEncryption(encryptor);
            infConsole.appendText(EncryptionPerformer.performEncryption(encryptor) + "\n");
        } catch (FileNotFoundException e) {
            showErrorWindow(e.getLocalizedMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void decrypt(ActionEvent actionEvent) {
        src = new File(pathField.getText());
        dst = src; //пока что
        keyFile = new File(keyFileField.getText());
        try {
            FileDecryptor decryptor = FileDecryptor.getDecryptor(src, dst, keyFile, deleteOriginal, integrityControl, fingerPrint, encryptName);
            EncryptionPerformer.prepareForDecryption(decryptor);
            infConsole.appendText(EncryptionPerformer.performDecryption(decryptor) + "\n");
        } catch (FileNotFoundException e) {
            showErrorWindow(e.getLocalizedMessage());
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
            settingsController.setInitial(deleteOriginal, integrityControl, fingerPrint, encryptName);

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
