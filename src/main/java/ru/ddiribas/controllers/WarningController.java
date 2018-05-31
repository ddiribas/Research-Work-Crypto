package ru.ddiribas.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class WarningController {

    public MainController parent;


    public void setParent(MainController parent) {
        this.parent = parent;
    }

    String label = "";
    @FXML
    public Label warningLabel;
    public void setLabel (String s) {
        warningLabel.setText(s);
        warningLabel.setAlignment(Pos.BASELINE_CENTER);
    }

    @FXML
    public Button okButton;

    public void closeAction(ActionEvent actionEvent) {
        parent.warningStage.close();
    }
}
