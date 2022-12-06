package controller;

import dao.TimeSheetDAO;
import dao.UsersDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.entity.TimeSheet;
import model.entity.User;
import model.entity.UserFilter;
import model.enumeration.Role;
import util.Alerts;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ResourceBundle;

public class TimeSheetController implements Initializable {

    @FXML
    TextField textDateInit;
    @FXML
    TextField textDateFinish;

    @FXML
    public Button btnSearch;

    @FXML
    public Button btnStart;

    @FXML
    public Button btnStartLunch;

    @FXML
    public Button btnEndLunch;

    @FXML
    public Button btnEnd;

    @FXML
    TableView<TimeSheet> tableViewTimeSheet;
    @FXML
    TableColumn<TimeSheet, String> columnData;
    @FXML
    TableColumn<TimeSheet, String> columnStart;
    @FXML
    TableColumn<TimeSheet, String> columnStartLunch;
    @FXML
    TableColumn<TimeSheet, String> columnEndLunch;
    @FXML
    TableColumn<TimeSheet, String> columnEnd;

    public static TimeSheetController instantiation;
    public static User userLogado;
    public static LocalDate date;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userLogado = LoginController.usuarioLogado;
        String usuarioLogadoName = userLogado.getName();
        int registrationCode = userLogado.getRegistrationCode();
        System.out.println("user: " + usuarioLogadoName + " id: " + registrationCode);
        date = LocalDate.now();
        try {
            initializeNodes();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    private void initializeNodes() throws SQLException {
        columnData.setCellValueFactory(new PropertyValueFactory<>("dateStr"));
        columnStart.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        columnStartLunch.setCellValueFactory(new PropertyValueFactory<>("startTimeLunch"));
        columnEndLunch.setCellValueFactory(new PropertyValueFactory<>("endTimeLunch"));
        columnEnd.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        updateTableView();
        updateButtons();
    }

    private void updateButtons() {
        int id = userLogado.getRegistrationCode();
        Role role = userLogado.getRole();
        if(Role.ADMIN.equals(role)){
            btnStart.setDisable(true);
            btnStartLunch.setDisable(true);
            btnEndLunch.setDisable(true);
            btnEnd.setDisable(true);
            return;
        }
        try {
            TimeSheet timeSheet = TimeSheetDAO.getByUserIdAndDate(id, date);
            if(timeSheet != null && timeSheet.getStartTime() != null){
                btnStart.setDisable(true);
            } else {
                btnStart.setDisable(false);
                btnStartLunch.setDisable(true);
                btnEndLunch.setDisable(true);
                btnEnd.setDisable(true);
                return;
            }

            LocalTime startTimeLunch = timeSheet.getStartTimeLunch();
            if(startTimeLunch != null){
                btnStartLunch.setDisable(true);
            } else {
                btnStartLunch.setDisable(false);
                btnEndLunch.setDisable(true);
                btnEnd.setDisable(true);
                return;
            }

            LocalTime endTimeLunch = timeSheet.getEndTimeLunch();
            if(endTimeLunch != null){
                btnEndLunch.setDisable(true);
            } else {
                btnEndLunch.setDisable(false);
                btnEnd.setDisable(true);
                return;
            }

            LocalTime endTime = timeSheet.getEndTime();
            btnEnd.setDisable(endTime != null);
        } catch (SQLException e) {
            e.printStackTrace();
            Alerts.showAlert("Ponto", "Erro ao carregar ponto", e.getCause().getMessage(), Alert.AlertType.ERROR);
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onSearch(ActionEvent event) throws SQLException {
        Role role = userLogado.getRole();
        Integer userId = null;
        if(Role.COLABORADOR.equals(role)){
            userId = userLogado.getRegistrationCode();
        }
        if(Role.ADMIN.equals(role)){
            userId = UserListController.userSelect.getRegistrationCode();
        }
        String dateInitText = textDateInit.getText();
        String dateFinishText = textDateFinish.getText();
        try {
            LocalDate dateInit = !dateInitText.isBlank() ? LocalDate.parse(dateInitText, DateTimeFormatter.ofPattern("dd-MM-yyyy")) : null;
            LocalDate dateFinish = !dateFinishText.isBlank() ? LocalDate.parse(dateFinishText, DateTimeFormatter.ofPattern("dd-MM-yyyy")) : null;
            List<TimeSheet> timeSheetList = TimeSheetDAO.findByDateInitAndFinish(dateInit, dateFinish, userId);
            updateTableView(timeSheetList);
        } catch (DateTimeParseException e){
            e.printStackTrace();
            Alerts.showAlert("Pesquisa", null, "Data inserida em formato invalido!", Alert.AlertType.ERROR);
        }

    }

    @FXML
    public void onStart(){
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now();

        TimeSheet timeSheet = new TimeSheet();
        timeSheet.setDatePoint(localDate);
        timeSheet.setStartTime(localTime);
        timeSheet.setUser(userLogado);
        try {
            TimeSheetDAO.create(timeSheet);
            Alerts.showAlert("Ponto", null, "Entrada salva com sucesso!", Alert.AlertType.CONFIRMATION);
            updateTableView();
            updateButtons();
        } catch (SQLException e) {
            e.printStackTrace();
            Alerts.showAlert("Ponto", "Erro ao salvar entrada", e.getCause().getMessage(), Alert.AlertType.ERROR);
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onStartLunch(){
        int id = userLogado.getRegistrationCode();

        try {
            TimeSheet timeSheet = TimeSheetDAO.getByUserIdAndDate(id, date);
            timeSheet.setStartTimeLunch(LocalTime.now());
            TimeSheetDAO.update(timeSheet);
            Alerts.showAlert("Ponto", null, "Inicio de almoco salvo com sucesso!", Alert.AlertType.CONFIRMATION);
            updateTableView();
            updateButtons();
        } catch (SQLException e) {
            e.printStackTrace();
            Alerts.showAlert("Ponto", "Erro ao salvar inicio de almoco", e.getCause().getMessage(), Alert.AlertType.ERROR);
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onEndLunch(){
        int id = userLogado.getRegistrationCode();

        try {
            TimeSheet timeSheet = TimeSheetDAO.getByUserIdAndDate(id, date);
            timeSheet.setEndTimeLunch(LocalTime.now());
            TimeSheetDAO.update(timeSheet);
            Alerts.showAlert("Ponto", null, "Fim de almoco salvo com sucesso!", Alert.AlertType.CONFIRMATION);
            updateTableView();
            updateButtons();
        } catch (SQLException e) {
            e.printStackTrace();
            Alerts.showAlert("Ponto", "Erro ao salvar fim de almoco", e.getCause().getMessage(), Alert.AlertType.ERROR);
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onEnd(){
        int id = userLogado.getRegistrationCode();

        try {
            TimeSheet timeSheet = TimeSheetDAO.getByUserIdAndDate(id, date);
            timeSheet.setEndTime(LocalTime.now());
            TimeSheetDAO.update(timeSheet);
            Alerts.showAlert("Ponto", null, "Saida salva com sucesso!", Alert.AlertType.CONFIRMATION);
            updateTableView();
            updateButtons();
        } catch (SQLException e) {
            e.printStackTrace();
            Alerts.showAlert("Ponto", "Erro ao salvar saida", e.getCause().getMessage(), Alert.AlertType.ERROR);
            throw new RuntimeException(e);
        }
    }

    public void updateTableView() throws SQLException {
        updateTableView(null);
    }

    public void updateTableView(List<TimeSheet> timeSheetList) throws SQLException {
        Role role = userLogado.getRole();
        if(timeSheetList == null){

            int userId = 0;
            if(Role.COLABORADOR.equals(role)){
                userId = userLogado.getRegistrationCode();
            }
            if(Role.ADMIN.equals(role)){
                userId = UserListController.userSelect.getRegistrationCode();
            }
            timeSheetList = TimeSheetDAO.findByUserId(userId);
        }
        ObservableList<TimeSheet> timeSheetObs = FXCollections.observableArrayList(timeSheetList);
        tableViewTimeSheet.setItems(timeSheetObs);
    }
}
