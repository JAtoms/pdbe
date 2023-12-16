package database;//package database;

import database.multimedia.ImageProcessing;
import database.multimedia.MultimediaDatabase;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

public class DataHandler {

    private static final String oracleUrl = "jdbc:oracle:thin:@//gort.fit.vutbr.cz:1521/orclpdb";
    private static Connection connection = null;

    public static String connectToDB(String userName, String password) {
        try {
            connection = DriverManager.getConnection(oracleUrl, userName, password);
            ImageProcessing imageProcessing = new ImageProcessing(connection);
//            MultimediaDatabase.create(connection);
//            imageProcessing.saveReturnedImage(94);
            imageProcessing.pickImage();
//            imageProcessing.rotateImage(48, 94);
            return "Login successful"; // yTBDz7n2
        } catch (Exception ignored) {
            ignored.printStackTrace();
            return "Invalid login credentials";
        }

    }

}
