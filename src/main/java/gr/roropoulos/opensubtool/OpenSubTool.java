package gr.roropoulos.opensubtool;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class OpenSubTool extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(OpenSubTool.class.getResource("/fxml/MainView.fxml"));
            AnchorPane rootAnchorPane = loader.load();
            Scene scene = new Scene(rootAnchorPane);
            stage.setScene(scene);
            stage.setTitle("OpenSubTool");
            stage.setMinWidth(810);
            stage.setMinHeight(590);
            stage.show();
            stage.setOnCloseRequest(e -> {
                Platform.exit();
                System.exit(0);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
