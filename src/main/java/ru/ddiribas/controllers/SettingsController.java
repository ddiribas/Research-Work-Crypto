package ru.ddiribas.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

public class SettingsController {

    private MainController parent;

    @FXML
    public CheckBox deleteOriginalBox;
    @FXML
    public CheckBox integrityControlBox;
    @FXML
    public CheckBox fingerPrintBox;
    @FXML
    public CheckBox encryptNameBox;

    void setParent(MainController parent) {
        this.parent = parent;
    }

    void setInitial(boolean deleteOriginal, boolean integrityControl, boolean fingerPrint, boolean encryptName) {
        deleteOriginalBox.setSelected(deleteOriginal);
        integrityControlBox.setSelected(integrityControl);
        fingerPrintBox.setSelected(fingerPrint);
        encryptNameBox.setSelected(encryptName);
    }

    public void okButton(ActionEvent actionEvent) {
        parent.deleteOriginal = deleteOriginalBox.isSelected();
        parent.integrityControl = integrityControlBox.isSelected();
        parent.fingerPrint = fingerPrintBox.isSelected();
        parent.encryptName = encryptNameBox.isSelected();
        parent.modalStage.close();
    }
}
