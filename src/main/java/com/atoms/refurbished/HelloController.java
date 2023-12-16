package com.atoms.refurbished;

import database.DataHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class HelloController {

    @FXML
    private Label loginErrorMsg;

    @FXML
    private TextField userName;

    @FXML
    private PasswordField password;

    private static Stage ownerStage;

    public static void setOwnerStage(Stage ownerStage) {
        HelloController.ownerStage = ownerStage;
    }

    @FXML
    protected void setLoginAction(ActionEvent event) {

        if (userName.getText().isBlank() || password.getText().isBlank()) {
            loginErrorMsg("Login credentials incorrect!");
        } else {
            loginErrorMsg(DataHandler.connectToDB("xnwokoj00", "yTBDz7n2", event, ownerStage));
//            loginErrorMsg(DataHandler.connectToDB(userName.getText(), password.getText(), event, ownerStage));
        }

    }

    protected void loginErrorMsg(String errorMessage) {
        loginErrorMsg.setText(errorMessage);
    }
}