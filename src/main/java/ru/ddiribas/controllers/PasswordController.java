package ru.ddiribas.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class PasswordController {
	private MainController parent;


	void setParent(MainController parent) {
		this.parent = parent;
	}

	@FXML
	public Button okButton;
	@FXML
	public PasswordField passwordField;

	public void okAction(ActionEvent actionEvent) {
		parent.passwordHash = passwordField.getAccessibleText();
		parent.modalStage.close();

	}
}
