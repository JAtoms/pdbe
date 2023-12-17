package database;//package database;

import com.atoms.refurbished.DatabaseController;
import com.atoms.refurbished.HelloApplication;
import database.multimedia.ImageProcessing;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

import static components.Toast.showToast;

public class DataHandler {

    private static final String oracleUrl = "jdbc:oracle:thin:@//gort.fit.vutbr.cz:1521/orclpdb";
    private static Connection connection = null;

    public static String connectToDB(String userName, String password, ActionEvent event, Stage ownerStage) {
        try {
            connection = DriverManager.getConnection(oracleUrl, userName, password);
            ImageProcessing imageProcessing = new ImageProcessing(connection);
//            MultimediaDatabase.create(connection);
//            imageProcessing.saveReturnedImage(94);
//            imageProcessing.rotateImage(48, 94);
            showToast("Login successful!", ownerStage);
            navigateToNewPage(userName, event);
            return "Login successful"; // yTBDz7n2
        } catch (Exception ignored) {
            ignored.printStackTrace();
            showToast("Login credentials incorrect!", ownerStage);
            return "Login credentials incorrect!";

        }

    }

    public static String connectDB(String userName, String password) {
        try {
            connection = DriverManager.getConnection(oracleUrl, userName, password);
            DatabaseController controller = new DatabaseController();
            controller.setOwnerUsername(userName, connection);
            System.out.println("Connected to Oracle database server");
            return "Connected to Oracle database server";
        } catch (Exception ignored) {
            ignored.printStackTrace();
            System.out.println("Connection to Oracle database server failed");
            return "Connection to Oracle database server failed";
        }

    }

    private static void navigateToNewPage(String userName, ActionEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("database-type.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        stage.setScene(scene);
        stage.setTitle("Welcome " + userName + "!");
        stage.show();
        ((Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow()).close();
    }

}
