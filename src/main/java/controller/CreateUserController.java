package controller;

import dao.UsersDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.entity.User;
import model.enumeration.Role;
import util.Alerts;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CreateUserController implements Initializable {
    @FXML
    TextField textLogin;

    @FXML
    PasswordField textPassword;

    @FXML
    TextField textName;

    @FXML
    ComboBox<Role> comboBoxRole;
    ObservableList<Role> obsListRole;

    @FXML
    TextField textId;

    @FXML
    Button btnSave;

    @FXML
    Button btnCancel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String usuarioLogadoName = LoginController.usuarioLogado.getName();
        int registrationCode = LoginController.usuarioLogado.getRegistrationCode();
        System.out.println("user: " + usuarioLogadoName + " id: " + registrationCode);

        List<Role> roleList = new ArrayList<>();
        roleList.add(Role.COLABORADOR);
        roleList.add(Role.ADMIN);
        obsListRole = FXCollections.observableArrayList(roleList);
        comboBoxRole.setItems(obsListRole);
    }

    @FXML
    public void save(ActionEvent event) {
        String login = textLogin.getText();
        String name = textName.getText();
        String password = textPassword.getText();
        Role role = comboBoxRole.getSelectionModel().getSelectedItem();
        String idStr = textId.getText();

        if(login.isBlank() || name.isBlank() || password.isBlank() || role == null || idStr.isBlank()){
            Alerts.showAlert("Campos Não Preenchidos", null, "Campos Obrigatório Não Preenchidos!", Alert.AlertType.ERROR);
            return;
        }

        if(!idStr.matches("[0-9]*")){
            Alerts.showAlert("Código de Cadastro", null, "O código de cadastro precisa ser um número inteiro!", Alert.AlertType.ERROR);
            return;
        }

        int id = Integer.parseInt(idStr);

        try {
            User user = UsersDAO.getByIdOrLogin(id, login);
            if(user != null){
                String loginUser = user.getLogin();
                int registrationCode = user.getRegistrationCode();
                if(loginUser.equals(login)){
                    Alerts.showAlert("Usuario já Cadastrado", null, "Login já cadastrado, tente outro!", Alert.AlertType.ERROR);
                    return;
                }
                if(registrationCode == id){
                    Alerts.showAlert("Usuario já Cadastrado", null, "Código de Registro já cadastrado, tente outro!", Alert.AlertType.ERROR);
                    return;
                }
                return;
            }
            user = new User();
            user.setRegistrationCode(id);
            user.setName(name);
            user.setLogin(login);
            user.setPassword(password);
            user.setRole(role);

            UsersDAO.create(user);
            Alerts.showAlert("Usuario salvo", null, "Novo usuario salvo com sucesso!", Alert.AlertType.CONFIRMATION);
            UserListController.instantiation.updateTableView();
        } catch (SQLException throwables) {
            Alerts.showAlert("Erro inesperado", "Erro ao tentar salvar usuario", throwables.getCause().getMessage(), Alert.AlertType.ERROR);
            throwables.printStackTrace();
            return;
        } catch (Exception e){
            Alerts.showAlert("Erro inesperado", "Erro ao tentar salvar usuario", e.getCause().getMessage(), Alert.AlertType.ERROR);
            throw e;
        }
    }

    @FXML
    public void cancel(){
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
