package com.atoms.refurbished;

import database.DataHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.SQLException;


public class HelloController {

    @FXML
    private Label loginErrorMsg;

    @FXML
    private TextField userName;

    @FXML
    private PasswordField password;

    @FXML
    private Button loginBtn;

    @FXML
    protected void setLoginAction(ActionEvent e) throws SQLException {
        pickImage();
        if (userName.getText().isBlank() || password.getText().isBlank()) {
            loginErrorMsg("Login credentials incorrect!");
        } else {
            loginErrorMsg("");
            System.out.println(userName.getText());
            System.out.println(password.getText());
            loginErrorMsg(DataHandler.connectToDB(userName.getText(), password.getText()));
        }

    }

    private void pickImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(loginBtn.getScene().getWindow());
        if (selectedFile != null) {
           System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            // Load and display the selected image
//            Image selectedImage = new Image(selectedFile.toURI().toString());
//            imageView.setImage(selectedImage);
        }
    }

    protected void loginErrorMsg(String errorMessage) {
        loginErrorMsg.setText(errorMessage);
    }
}