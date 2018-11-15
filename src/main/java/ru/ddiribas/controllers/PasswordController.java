package ru.ddiribas.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import ru.ddiribas.Encryption.StribogBouncy;

import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ResourceBundle;

public class PasswordController {
	private MainController parent;
	private Stage thisStage;

	@FXML
	public Button okButton;
	@FXML
	public PasswordField passwordField;

	void setParent(MainController parent, Stage thisStage) {
		this.parent = parent;
		this.thisStage = thisStage;
	}
	void init() {
		passwordField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					okButton.fire();
				}
			}
		});
		passwordField.requestFocus();
	}

	public void okAction(ActionEvent actionEvent) {
		parent.passwordHash = StribogBouncy.getByteHash256(passwordField.getText().getBytes());
		thisStage.close();
	}
}
