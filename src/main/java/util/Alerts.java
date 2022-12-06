package util;

import javafx.scene.control.Alert;

public class Alerts {

    public static void showAlert(String title, String header, String content, javafx.scene.control.Alert.AlertType type){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }
}
