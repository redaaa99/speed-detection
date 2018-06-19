package proj.sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.opencv.core.Core;

public class Main extends Application {
    private Controller controller;
    @Override
    public void start(Stage primaryStage) throws Exception{
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION); // infos
            alert.setTitle("Erreur");
            alert.getDialogPane().setPrefSize(300,300);
            alert.setHeaderText("OpenCV:");
            alert.setContentText("Vous avez besoin de specifier le chemin vers la librarie OpenCV");
            alert.showAndWait();
        }
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        primaryStage.setTitle("Real time speed detection");
        primaryStage.setScene(new Scene(root, 1283, 597));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public Controller getController() {
        return controller;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
