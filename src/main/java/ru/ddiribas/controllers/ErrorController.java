package ru.ddiribas.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ErrorController {

    private MainController parent;
    private Stage thisStage;

    @FXML
    public Button okButton;
    @FXML
    public Label warningLabel;
    String label = "";

    void setParent(MainController parent, Stage thisStage) {
        this.parent = parent;
        this.thisStage = thisStage;
    }
    void init() {
        okButton.setLayoutX(thisStage.getWidth()/2 - 30);
        okButton.setDefaultButton(true);
    }

    void setLabel(String s) {
        warningLabel.setText(s);
        warningLabel.setAlignment(Pos.BASELINE_CENTER);
        thisStage.setWidth(warningLabel.getText().length() * 7);
        warningLabel.setPrefWidth(warningLabel.getText().length() * 7);
    }

    public void closeAction(ActionEvent actionEvent) {
        thisStage.close();
    }
}
