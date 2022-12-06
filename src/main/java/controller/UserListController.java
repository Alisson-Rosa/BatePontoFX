package controller;

import dao.UsersDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entity.User;
import model.entity.UserFilter;
import model.enumeration.Role;
import util.Alerts;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class UserListController implements Initializable {

    @FXML
    TextField textID;
    @FXML
    TextField textName;
    @FXML
    TextField textLogin;
    @FXML
    TextField textRole;
    @FXML
    TableView<User> tableViewUser;
    @FXML
    TableColumn<User, String> columnName;
    @FXML
    TableColumn<User, String> columnLogin;
    @FXML
    TableColumn<User, Role> columnRole;
    @FXML
    TableColumn<User, Integer> columnID;

    public static UserListController instantiation;
    public static User userSelect;

    @FXML
    public void onSearch(ActionEvent event) throws SQLException {
        String idStr = textID.getText();
        String role = textRole.getText();
        String login = textLogin.getText();
        String name = textName.getText();

        if(idStr.isBlank()){
          idStr = null;
        }
        if(idStr != null && (!idStr.matches("[0-9]*"))){
            Alerts.showAlert("Código de Cadastro", null, "O código de cadastro precisa ser um número inteiro!", Alert.AlertType.ERROR);
            return;
        }
        Integer id = idStr == null ? null : Integer.parseInt(idStr);

        UserFilter filter = new UserFilter(id, name, login, role);
        List<User> users = UsersDAO.findByFilter(filter);
        updateTableView(users);
    }

    @FXML
    public void onCreate(ActionEvent event){
        try {
            instantiation = this;
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/FormCreateUser.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Novo Usuario");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onDelete(ActionEvent event){
        try {
            User userSelect = tableViewUser.getSelectionModel().getSelectedItem();
            if(userSelect == null){
                Alerts.showAlert("Exclusão Usuario", "Falha ao excluir usuario", "Nenhum usuario selecionado", Alert.AlertType.ERROR);
                return;
            }

            int id = userSelect.getRegistrationCode();
            UsersDAO.deleteById(id);
            Alerts.showAlert("Exclusão Usuario", null, "Usuario excluido com sucesso!", Alert.AlertType.CONFIRMATION);
            updateTableView();
        } catch (SQLException e) {
            e.printStackTrace();
            Alerts.showAlert("Exclusão Usuario", "Falha ao excluir usuario", e.getCause().getMessage(), Alert.AlertType.ERROR);
            throw new RuntimeException(e);
        }

    }

    @FXML
    public void onUpdate(ActionEvent event){
        userSelect = tableViewUser.getSelectionModel().getSelectedItem();
        if(userSelect == null){
            return;
        }
        try {
            instantiation = this;
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/FormUpdateUser.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Atualizar Usuario");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onTimeSheet(ActionEvent event){
        User userSelectTable = tableViewUser.getSelectionModel().getSelectedItem();
        if(userSelectTable == null){
            Alerts.showAlert("Ponto", "Falha ao extrair o ponto", "Nenhum usuario selecionado", Alert.AlertType.ERROR);
            return;
        }

        Role role = userSelectTable.getRole();
        if(Role.ADMIN.equals(role)){
            Alerts.showAlert("Ponto", "Falha ao extrair o ponto", "O usuario precisa ser um Colaborador", Alert.AlertType.ERROR);
            return;
        }

        userSelect = userSelectTable;
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/TimeSheet.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Bate Ponto Modal");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alerts.showAlert("Ponto", "Falha ao extrair o ponto", e.getCause().getMessage(), Alert.AlertType.ERROR);
            throw new RuntimeException(e);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String usuarioLogadoName = LoginController.usuarioLogado.getName();
        int registrationCode = LoginController.usuarioLogado.getRegistrationCode();
        System.out.println("user: " + usuarioLogadoName + " id: " + registrationCode);
        try {
            initializeNodes();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void initializeNodes() throws SQLException {
        columnID.setCellValueFactory(new PropertyValueFactory<>("registrationCode"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        columnRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        updateTableView();
    }

    public void updateTableView() throws SQLException {
        updateTableView(null);
    }

    public void updateTableView(List<User> userList) throws SQLException {
        if(userList == null){
            userList = UsersDAO.listAll();
        }
        ObservableList<User> usersObs = FXCollections.observableArrayList(userList);
        tableViewUser.setItems(usersObs);
    }
}
