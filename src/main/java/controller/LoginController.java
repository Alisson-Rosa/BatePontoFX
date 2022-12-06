package controller;

import dao.UsersDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entity.User;
import model.enumeration.Role;
import util.Alerts;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController  {
    @FXML
    TextField textLoginAdmin;
    @FXML
    TextField textPasswordAdmin;
    @FXML
    TextField textLoginEmployee;
    @FXML
    TextField textPasswordEmployee;

    @FXML
    Button btnLogin;

    @FXML
    Button btnCancel;

    @FXML
    Button btnLoginAdmin;

    @FXML
    Button btnLoginEmployee;
    public static User usuarioLogado;

    public void login(ActionEvent event){
        try {
            boolean isLoginEmploye = btnLoginEmployee.isDisable();
            boolean isLoginAdmin = btnLoginAdmin.isDisable();
            Role role = null;
            String login = "";
            String password = "";
            if (isLoginEmploye){
                role = Role.COLABORADOR;
                login = textLoginEmployee.getText().toString();
                password = textPasswordEmployee.getText().toString();
            }
            if (isLoginAdmin){
                role = Role.ADMIN;
                login = textLoginAdmin.getText().toString();
                password = textPasswordAdmin.getText().toString();
            }


            User user = UsersDAO.getByLoginAndPasswordAndRole(login, password, role);

            if (user != null) {
                usuarioLogado = user;
                Stage stage = new Stage();
                if(Role.ADMIN.equals(user.getRole())){
                    Parent root = FXMLLoader.load(getClass().getResource("/fxml/UserList.fxml"));
                    stage.setScene(new Scene(root));
                    stage.setTitle("Usuarios Modal");
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner(((Node) event.getSource()).getScene().getWindow());
                    stage.show();
                }

                if(Role.COLABORADOR.equals(user.getRole())){
                    Parent root = FXMLLoader.load(getClass().getResource("/fxml/TimeSheet.fxml"));
                    stage.setScene(new Scene(root));
                    stage.setTitle("Bate Ponto Modal");
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner(((Node) event.getSource()).getScene().getWindow());
                    stage.show();
                }
            } else {
                Alerts.showAlert("Erro Login", null, "Login ou Senha Incorreto!", Alert.AlertType.ERROR);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            Alerts.showAlert("Erro Login", "Erro ao realizar login", e.getCause().getMessage(), Alert.AlertType.ERROR);
            throw new RuntimeException(e);
        }
    }

    public void btnLoginAdmin(){
        btnLoginAdmin.setDisable(true);
        btnLoginEmployee.setDisable(false);
        textLoginEmployee.setDisable(true);
        textPasswordEmployee.setDisable(true);
        textLoginAdmin.setDisable(false);
        textPasswordAdmin.setDisable(false);
        btnLogin.setDisable(false);
    }

    public void btnLoginEmployee(){
        btnLoginEmployee.setDisable(true);
        btnLoginAdmin.setDisable(false);
        textLoginAdmin.setDisable(true);
        textPasswordAdmin.setDisable(true);
        textLoginEmployee.setDisable(false);
        textPasswordEmployee.setDisable(false);
        btnLogin.setDisable(false);
    }

    public void cancel(ActionEvent event){
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

}
