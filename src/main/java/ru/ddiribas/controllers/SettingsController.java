package ru.ddiribas.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

public class SettingsController {

    public MainController parent;

    @FXML
    public CheckBox deleteOriginalBox;
    @FXML
    public CheckBox integrityControlBox;

    public void setParent(MainController parent) {
        this.parent = parent;
    }

    public void setInitial(boolean deleteOriginal, boolean integrityControl) {
        deleteOriginalBox.setSelected(deleteOriginal);
        integrityControlBox.setSelected(integrityControl);
    }

    public void okButton(ActionEvent actionEvent) {
        parent.deleteOriginal = deleteOriginalBox.isSelected();
        parent.integrityControl = integrityControlBox.isSelected();
        parent.modalStage.close();
    }
}
