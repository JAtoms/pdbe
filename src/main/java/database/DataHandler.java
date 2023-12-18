package database;//package database;

import com.atoms.refurbished.DatabaseController;
import com.atoms.refurbished.HelloApplication;
import database.multimedia.ImageProcessing;
import database.multimedia.MultimediaDatabase;
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
            DatabaseController controller = new DatabaseController();
            controller.setOwnerUsername(userName, connection);
            MultimediaDatabase.create(connection);
            showToast("Login successful!", ownerStage);
            navigateToNewPage(userName, event);
            return "Login successful"; // yTBDz7n2
        } catch (Exception ignored) {
            ignored.printStackTrace();
            showToast("Login credentials incorrect!", ownerStage);
            return "Login credentials incorrect!";

        }

    }


    private static void navigateToNewPage(String userName, ActionEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("database-type.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 800);
        stage.setScene(scene);
        stage.setTitle("Welcome " + userName + "!");
        stage.show();
        ((Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow()).close();
    }

}
