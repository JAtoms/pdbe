package components;

import com.atoms.refurbished.HelloApplication;
import javafx.animation.FadeTransition;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;

public class Toast {

    public static void showToast(String message, Stage ownerStage){
        Stage toastStage = new Stage();
        toastStage.initOwner(ownerStage);
        toastStage.setOpacity(0.8);
        toastStage.setResizable(false);
        toastStage.initStyle(javafx.stage.StageStyle.UNDECORATED);
        Label label = new Label(message);
        label.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("toast.css"))
                .toExternalForm());
        label.getStyleClass().add("toast-label");

        StackPane root = new StackPane();
        root.getChildren().add(label);

        Scene scene = new Scene(root);
        scene.setFill(null);
        toastStage.setScene(scene);

        toastStage.setX(ownerStage.getX() + ownerStage.getWidth() / 2 - scene.getWidth() / 2);
        toastStage.setY(ownerStage.getY() + ownerStage.getHeight() / 2 - scene.getHeight() / 2);

        toastStage.show();

        // Close the toast after a short delay
        Duration duration = Duration.millis(2000); // Adjust the duration as needed
        FadeTransition fade = new FadeTransition(duration, root);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);

        fade.setOnFinished(event -> toastStage.close());
        fade.play();
    }
}
