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

public class UpdateUserController implements Initializable {
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
    Button btnSave;

    @FXML
    Button btnCancel;

    private User userUpdate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Role> roleList = new ArrayList<>();
        roleList.add(Role.COLABORADOR);
        roleList.add(Role.ADMIN);
        obsListRole = FXCollections.observableArrayList(roleList);
        comboBoxRole.setItems(obsListRole);

        userUpdate = UserListController.userSelect;
        String login = userUpdate.getLogin();
        String name = userUpdate.getName();
        String password = userUpdate.getPassword();
        Role role = userUpdate.getRole();
        textLogin.setText(login);
        textName.setText(name);
        textPassword.setText(password);
        comboBoxRole.setPromptText(role.name());
    }

    @FXML
    public void save(ActionEvent event) {
        String login = textLogin.getText();
        String name = textName.getText();
        String password = textPassword.getText();
        Role role = comboBoxRole.getSelectionModel().getSelectedItem();

        if(login.isBlank() || name.isBlank() || password.isBlank() || role == null){
            Alerts.showAlert("Campos Não Preenchidos", null, "Campos Obrigatório Não Preenchidos!", Alert.AlertType.ERROR);
            return;
        }

        try {
            int id = userUpdate.getRegistrationCode();
            User user = UsersDAO.getById(id);
            if(user == null){
                Alerts.showAlert("Usuario não encontrado", null, "Usuario não encontrado!", Alert.AlertType.ERROR);
                return;
            }

            User userLogin = UsersDAO.getByLoginAndNotId(login, id);
            if(userLogin != null){
                Alerts.showAlert("Usuario já Cadastrado", null, "Login já cadastrado, tente outro!", Alert.AlertType.ERROR);
                return;
            }

            user.setName(name);
            user.setLogin(login);
            user.setPassword(password);
            user.setRole(role);

            UsersDAO.update(user);
            Alerts.showAlert("Usuario atualizado", null, "Usuario atualizado com sucesso!", Alert.AlertType.CONFIRMATION);
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
