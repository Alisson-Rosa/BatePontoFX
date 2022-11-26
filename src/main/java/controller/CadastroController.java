package controller;

import dao.UsersDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.entity.User;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CadastroController implements Initializable {

    @FXML
    TextField textID;
    @FXML
    TextField textName;
    @FXML
    TextField textLogin;
    @FXML
    TextField textPassword;
    @FXML
    TableView<User> usersTable;
    @FXML
    TableColumn<User, String> columnName;
    @FXML
    TableColumn<User, String> columnID;

    public void save(ActionEvent event){}
    public void retrieve(ActionEvent event) throws SQLException {
        if(!textID.getText().isEmpty()){
            int id = Integer.parseInt(textID.getText().toString());
            User user = UsersDAO.retrive(id);
            textName.setText(user.getName());
        }else{
            System.out.println("Error");
        }

    }
    public void cancel(ActionEvent event){
        //clear all fields
        textName.setText("");
        textID.setText("");
        textLogin.setText("");
        textPassword.setText("");
    }
    public void delete(ActionEvent event){}


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Usuário logado: ");
        System.out.println(LoginController.usuarioLogado.getName());
        System.out.println(LoginController.usuarioLogado.getRegistrationCode());
        try {
            usersTable.setItems(usersList());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    private ObservableList<User> usersList() throws SQLException {
        return FXCollections.observableArrayList(UsersDAO.listAll());
    }
}
