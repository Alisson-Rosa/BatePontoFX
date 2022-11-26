import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {



    @Override
    public void start(Stage primaryStage) throws Exception{
        Stage stage = primaryStage;
        Parent fxmlMain = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
        Scene mainScene = new Scene(fxmlMain);
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Login do Sistema");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) throws ClassNotFoundException {
        launch(args);
    }

}
