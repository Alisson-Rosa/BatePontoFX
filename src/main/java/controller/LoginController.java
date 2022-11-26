package controller;

import dao.UsersDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entity.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

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
            List<User> userList = UsersDAO.listAll();
            System.out.println("Total de usuários na lista: " + userList.size());
            //Validar login e carregar tela
            String login = textLoginAdmin.getText().toString();
            String password = textPasswordAdmin.getText().toString();
            boolean status = false;
            //percorrer a lista e achar o login
            for(User u : userList){
                if(login.equals(u.getLogin()) && password.equals(u.getPassword())){
                    status = true;
                    usuarioLogado = u;
                    Stage stage = new Stage();
                    Parent root = FXMLLoader.load(getClass().getResource("/fxml/Cadastro.fxml"));
                    stage.setScene(new Scene(root));
                    stage.setTitle("My modal window");
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner(((Node)event.getSource()).getScene().getWindow() );
                    stage.show();
                    break;
                }else{
                    status = false;
                }
            }
            if(!status){
                System.out.println("Login incorreto!");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnLoginAdmin(){
        btnLoginAdmin.setDisable(true);
        btnLoginEmployee.setDisable(false);
        textLoginEmployee.setDisable(true);
        textPasswordEmployee.setDisable(true);
        textLoginAdmin.setDisable(false);
        textPasswordAdmin.setDisable(false);
    }

    public void btnLoginEmployee(){
        btnLoginEmployee.setDisable(true);
        btnLoginAdmin.setDisable(false);
        textLoginAdmin.setDisable(true);
        textPasswordAdmin.setDisable(true);
        textLoginEmployee.setDisable(false);
        textPasswordEmployee.setDisable(false);
    }

    public void cancel(ActionEvent event){}

}
