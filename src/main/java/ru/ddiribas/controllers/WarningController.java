package ru.ddiribas.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Formatter;
import java.util.ResourceBundle;

public class WarningController implements Initializable {

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
        thisStage.setWidth(warningLabel.getText().length() * 7);
        warningLabel.setPrefWidth(warningLabel.getText().length() * 7);
    }

    @FXML
    public Button okButton;

    public void okAction(ActionEvent actionEvent) { thisStage.close(); }

    public void cancelAction(ActionEvent actionEvent) {
        continueExecution = false;
        thisStage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        okButton.setLayoutX(thisStage.getWidth()/2 - 30);
        okButton.setDefaultButton(true);
    }
}
