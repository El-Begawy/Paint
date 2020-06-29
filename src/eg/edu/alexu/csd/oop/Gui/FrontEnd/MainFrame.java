package eg.edu.alexu.csd.oop.Gui.FrontEnd;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFrame extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainFrame.fxml"));
        Parent root = (Parent) loader.load();
        Controller controller = (Controller) loader.getController();
        controller.setStage(primaryStage);
        primaryStage.setTitle("Paint");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
