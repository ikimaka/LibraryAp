package main;

import database.DatabaseHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.LibraryAssistantUtil;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/login/login.fxml"));
        primaryStage.setTitle("Library Assistant Login");

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();
        LibraryAssistantUtil.setStageIcon(primaryStage);

        new Thread(DatabaseHandler::getInstance).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
