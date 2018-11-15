package ru.ddiribas.controllers;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.*;
import javafx.event.ActionEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import ru.ddiribas.Encryption.*;
import ru.ddiribas.MainApp;

public class MainController {

    private MainApp mainApp;
    private Parent modalScene;
    private FXMLLoader fxmlLoader;
    private WarningController warningController;

    private File src, dst, keyFile;
    boolean integrityControl = true, deleteOriginal = true, fingerPrint = true, encryptName = true, passwordAuth = true;
    byte[] passwordHash;

    @FXML
    TextField pathField;
    @FXML
    TextField keyFileField;
    @FXML
    TextArea infConsole;
    @FXML
    Button encryptButton;
    @FXML
    Button decryptButton;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void init() {
        encryptButton.defaultButtonProperty().bind(encryptButton.focusedProperty());
        decryptButton.defaultButtonProperty().bind(decryptButton.focusedProperty());
        pathField.setText("C:\\Share\\Диплом\\test");
        keyFileField.setText("C:\\Share\\Диплом\\KeyFile.dkey");
    }

    public WarningController getWarningController() {
        return warningController;
    }

    public void showErrorWindow(String label) {
        try {
            fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/fxml/errorWindow.fxml"));
            modalScene = fxmlLoader.load();
            Stage modalStage = new Stage();
            ErrorController errorController = fxmlLoader.getController();
            errorController.setParent(this, modalStage);
            errorController.setLabel(label);
            errorController.init();

            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(mainApp.getMainWindow());
            modalStage.setTitle("Error");
            modalStage.setScene(new Scene(modalScene));
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
            modalScene = fxmlLoader.load();
            Stage modalStage = new Stage();
            PasswordController passwordController = fxmlLoader.getController();
            passwordController.setParent(this, modalStage);

            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(mainApp.getMainWindow());
            modalStage.setTitle("Enter the password");
            modalStage.setScene(new Scene(modalScene));
            modalStage.setResizable(false);
            passwordController.init();
            modalStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return passwordHash;
    }

    public void showWarningWindow (String label) {
        try {
            fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/fxml/warningWindow.fxml"));
            modalScene = fxmlLoader.load();
            Stage modalStage = new Stage();
            warningController = fxmlLoader.getController();
            warningController.setParent(this, modalStage);
            warningController.setLabel(label);
            warningController.continueExecution = true;

            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(mainApp.getMainWindow());
            modalStage.setTitle("Warning");
            modalStage.setScene(new Scene(modalScene));
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
            FileEncryptor encryptor = FileEncryptor.getEncryptor(src, dst, keyFile, deleteOriginal, integrityControl, fingerPrint, encryptName, passwordAuth);
            EncryptionPerformer.prepareForEncryption(encryptor);
            infConsole.appendText(EncryptionPerformer.performEncryption(encryptor) + "\n");
        } catch (FileNotFoundException e) {
            showErrorWindow(e.getLocalizedMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (StopOperationException ignored) {
        }
    }

    public void decrypt(ActionEvent actionEvent) {
        src = new File(pathField.getText());
        dst = src; //пока что
        keyFile = new File(keyFileField.getText());
        try {
            FileDecryptor decryptor = FileDecryptor.getDecryptor(src, dst, keyFile, deleteOriginal, integrityControl, fingerPrint, encryptName, passwordAuth);
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
            modalScene = fxmlLoader.load();
            Stage modalStage = new Stage();
            SettingsController settingsController = fxmlLoader.getController();
            settingsController.setParent(this, modalStage);
            settingsController.setInitial(deleteOriginal, integrityControl, fingerPrint, encryptName, passwordAuth);

            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(mainApp.getMainWindow());
            modalStage.setTitle("Settings");
            modalStage.setScene(new Scene(modalScene));
            modalStage.setResizable(false);
            modalStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
