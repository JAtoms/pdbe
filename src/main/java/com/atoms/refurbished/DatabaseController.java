package com.atoms.refurbished;

import database.DataHandler;
import database.multimedia.ImageProcessing;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;


public class DatabaseController {

    @FXML
    private Label loginGreeting, logs;

    @FXML
    private Button insertImageBtn;

    @FXML
    private ImageView imageView;

    @FXML
    private TextField delImageID;

    private ActionEvent event;
    private static Connection connection;

    public void setOwnerUsername(String userName, Connection connection) {
        this.connection = connection;
        //        loginGreeting.setText("Welcome - " + userName);
    }

    @FXML
    protected void insertImageBtn() throws SQLException, IOException, ImageProcessing.DataBaseException {
        logs.setText("Inserting image... be patient");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(insertImageBtn.getScene().getWindow());

        if (selectedFile != null) {
            ImageProcessing imageProcessing = new ImageProcessing(connection);
            String insertionStatus = imageProcessing.insertImage(selectedFile);
            if (insertionStatus.equals("Image inserted successfully")) {
                logs.setText("Image inserted successfully");
                Image image = new Image(new File("src/main/resources/results/newImage.png").toURI().toString());
                imageView.setFitHeight(270);
                imageView.setFitWidth(270);
                imageView.setImage(image);

            } else {
                logs.setText("Image insertion failed");
            }
        }

    }

    @FXML
    protected void deleteImage() throws SQLException {
        if (!delImageID.getText().matches("\\d+")) {
            logs.setText("Please enter a valid image ID");
            return;
        }
        logs.setText("Deleting image... be patient");
        ImageProcessing imageProcessing = new ImageProcessing(connection);
        String deleteStatus = imageProcessing.deleteImage(Integer.parseInt(delImageID.getText()));
        logs.setText(deleteStatus);
    }

    @FXML
    protected void clearDB() {
//        logs.setText(DataHandler.connectDB("xnwokoj00", "yTBDz7n2"));

    }

    @FXML
    protected void update() {
        logs.setText(DataHandler.connectDB("xnwokoj00", "yTBDz7n2"));

    }

}