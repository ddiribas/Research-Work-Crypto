package ru.ddiribas.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.Formatter;

public class WarningController {

    private MainController parent;
    private Stage thisStage;
    public boolean continueExecution;

    void setParent(MainController parent, Stage thisStage) {
        this.parent = parent;
        this.thisStage = thisStage;
    }

    String label = "";
    @FXML
    public Label warningLabel;
    void setLabel(String s) {
        warningLabel.setText(s);
        warningLabel.setAlignment(Pos.BASELINE_CENTER);
    }

    @FXML
    public Button okButton;

    public void okAction(ActionEvent actionEvent) { thisStage.close(); }

    public void cancelAction(ActionEvent actionEvent) {
        continueExecution = false;
        thisStage.close();
    }
}
