package com.atoms.refurbished;

import database.multimedia.ImageProcessing;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public class DatabaseController {

    @FXML
    private Label loginGreeting, imageProperties, logs, images;

    @FXML
    private Button insertImageBtn, updateImageBtn;

    @FXML
    private ImageView imageView;

    @FXML
    private TextField delImageID, updateImgID, updateImgTitle;

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
            List<String> insertionStatus = imageProcessing.insertImage(selectedFile, 101, null);
            if (insertionStatus.getFirst().equals("Image inserted successfully")) {
                logs.setText("Image inserted successfully");
                imageProperties.setText(insertionStatus.get(1));
                imageViewer();
                showImages(imageProcessing);
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
        showImages(imageProcessing);
    }

    @FXML
    protected void clearDB() {

        ImageProcessing imageProcessing = new ImageProcessing(connection);
        String clearDB = imageProcessing.clearDB();
        logs.setText(clearDB);
        if (clearDB.equals("Database cleared")) {
            images.setText("No data in database");
        }

    }

    @FXML
    protected void updateImageBtn() throws SQLException, IOException, ImageProcessing.DataBaseException {

        if (!updateImgID.getText().matches("\\d+")) {
            logs.setText("Please enter a valid image ID");
            return;
        }

        logs.setText("Updating image... be patient");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(updateImageBtn.getScene().getWindow());

        if (selectedFile != null) {
            ImageProcessing imageProcessing = new ImageProcessing(connection);
            List<String> insertionStatus = imageProcessing.insertImage(selectedFile, Integer.parseInt(updateImgID.getText()), updateImgTitle.getText());
            if (insertionStatus.getFirst().equals("Image inserted successfully")) {
                logs.setText("Image updated successfully");
                imageProperties.setText(insertionStatus.get(1));
                imageViewer();
                showImages(imageProcessing);
            } else {
                logs.setText("Image updating failed");
            }
        }
    }


    private void showImages(ImageProcessing imageProcessing) throws SQLException {
        images.setText("All images in the database: \n\n" + imageProcessing.getAlLImages());
    }

    private void imageViewer() {
        Image image = new Image(new File("src/main/resources/results/newImage.png")
                .toURI().toString());
        imageView.setFitHeight(270);
        imageView.setFitWidth(270);
        imageView.setImage(image);
    }

}