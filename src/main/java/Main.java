import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        try {
            DBConnector dbConnector = new DBConnector();
            dbConnector.createFanTable();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        Parent root = FXMLLoader.load(getClass().getResource("FanficsTabPages.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
