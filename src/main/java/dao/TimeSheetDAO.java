package dao;

import database.DbConnection;
import model.entity.TimeSheet;
import model.entity.User;
import model.enumeration.Role;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TimeSheetDAO {

    public static void create(TimeSheet timeSheet) throws SQLException {
        Connection connection;
        PreparedStatement stmt;
        connection = DbConnection.getConnectionSqlite();
        String save = " INSERT INTO time_sheet (datePoint, startTime, startTimeLunch, endTimeLunch, endTime, users_id) " +
                      " VALUES ( ?, ?, ?, ?, ?, ?) ";
        stmt = connection.prepareStatement(save);

        LocalDate datePoint = timeSheet.getDatePoint();
        String datePointStr = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(datePoint);
        stmt.setString(1, datePointStr);
        LocalTime startTime = timeSheet.getStartTime();
        String startTimeStr = startTime != null ? DateTimeFormatter.ofPattern("HH:mm:ss").format(startTime) : "";
        stmt.setString(2, startTimeStr);
        LocalTime startTimeLunch = timeSheet.getStartTimeLunch();
        String startTimeLunchStr = startTimeLunch != null ? DateTimeFormatter.ofPattern("HH:mm:ss").format(startTimeLunch) : "";
        stmt.setString(3, startTimeLunchStr);
        LocalTime endTimeLunch = timeSheet.getEndTimeLunch();
        String endTimeLunchStr = endTimeLunch != null ? DateTimeFormatter.ofPattern("HH:mm:ss").format(endTimeLunch) : "";
        stmt.setString(4, endTimeLunchStr);
        LocalTime endTime = timeSheet.getEndTime();
        String endTimeStr = endTime != null ? DateTimeFormatter.ofPattern("HH:mm:ss").format(endTime) : "";
        stmt.setString(5, endTimeStr);
        stmt.setInt(6, timeSheet.getUser().getRegistrationCode());

        stmt.execute();
        connection.close();
    }

    public static void update(TimeSheet timeSheet) throws SQLException {
        Connection connection;
        connection = DbConnection.getConnectionSqlite();
        PreparedStatement stmt;
        if (connection != null) {
            String update = "update time_sheet set startTime = ?, startTimeLunch = ?, endTimeLunch = ?, endTime = ? where id = ?";
            stmt = connection.prepareStatement(update);

            LocalTime startTime = timeSheet.getStartTime();
            String startTimeStr = startTime != null ? DateTimeFormatter.ofPattern("HH:mm:ss").format(startTime) : "";
            stmt.setString(1, startTimeStr);
            LocalTime startTimeLunch = timeSheet.getStartTimeLunch();
            String startTimeLunchStr = startTimeLunch != null ? DateTimeFormatter.ofPattern("HH:mm:ss").format(startTimeLunch) : "";
            stmt.setString(2, startTimeLunchStr);
            LocalTime endTimeLunch = timeSheet.getEndTimeLunch();
            String endTimeLunchStr = endTimeLunch != null ? DateTimeFormatter.ofPattern("HH:mm:ss").format(endTimeLunch) : "";
            stmt.setString(3, endTimeLunchStr);
            LocalTime endTime = timeSheet.getEndTime();
            String endTimeStr = endTime != null ? DateTimeFormatter.ofPattern("HH:mm:ss").format(endTime) : "";
            stmt.setString(4, endTimeStr);
            stmt.setInt(5, timeSheet.getId());
            stmt.execute();

            stmt.close();
            connection.close();
        }
    }

    public static void deleteById(Integer id) throws SQLException {
        Connection connection;
        PreparedStatement stmt;
        connection = DbConnection.getConnectionSqlite();
        String delete = " delete from users where id = ?";
        stmt = connection.prepareStatement(delete);
        //Receber dados do parâmetro
        stmt.setInt(1, id);

        //Executar a instrução
        stmt.execute();
        connection.close();
    }

    public static TimeSheet getById(int id) throws SQLException {
        Connection connection;
        PreparedStatement stmt;
        ResultSet rst;
        connection = DbConnection.getConnectionSqlite();
        String search = "select * from users where id = ?";
        stmt = connection.prepareStatement(search);
        stmt.setInt(1, id);
        //Receber resultado da consulta
        rst = stmt.executeQuery();
        TimeSheet timeSheet = null;
        while (rst.next()) {
            timeSheet = instantiateTimeSheet(rst);
        }
        rst.close();
        connection.close();
        return timeSheet;
    }

    public static TimeSheet getByUserIdAndDate(int userId, LocalDate datePoint) throws SQLException {
        Connection connection;
        PreparedStatement stmt;
        ResultSet rst;
        connection = DbConnection.getConnectionSqlite();
        String search = "select * from time_sheet where users_id = ? and datePoint = ?";
        stmt = connection.prepareStatement(search);
        stmt.setInt(1, userId);
        String datePointStr = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(datePoint);
        stmt.setString(2, datePointStr);
        //Receber resultado da consulta
        rst = stmt.executeQuery();
        TimeSheet timeSheet = null;
        while (rst.next()) {
            timeSheet = instantiateTimeSheet(rst);
        }
        rst.close();
        connection.close();
        return timeSheet;
    }

    public static TimeSheet getByLoginAndNotId(String login, int id) throws SQLException {
        Connection connection;
        PreparedStatement stmt;
        ResultSet rst;
        connection = DbConnection.getConnectionSqlite();
        String search = "select * from users where id != ? and login = ?";
        stmt = connection.prepareStatement(search);
        stmt.setInt(1, id);
        stmt.setString(2, login);
        //Receber resultado da consulta
        rst = stmt.executeQuery();
        TimeSheet timeSheet = null;
        while (rst.next()) {
            timeSheet = instantiateTimeSheet(rst);
        }
        rst.close();
        connection.close();
        return timeSheet;
    }

    public static TimeSheet getByLoginAndPasswordAndRole(String login, String password, Role role) throws SQLException {
        Connection connection;
        connection = DbConnection.getConnectionSqlite();
        ResultSet rst;
        PreparedStatement pst = null;
        TimeSheet timeSheet = null;
        if (connection != null) {
            pst = connection.prepareStatement("select * from users where login = ? and password = ? and role = ?"); //TODO Adicionar where para role
            pst.setString(1, login);
            pst.setString(2, password);
            pst.setString(3, role.toString());
            rst = pst.executeQuery();
            //Pegar os itens do resultset e inserir na lista
            if (rst.next()) {
                timeSheet = instantiateTimeSheet(rst);
            }
            rst.close();
            connection.close();
        }
        return timeSheet;
    }

    private static TimeSheet instantiateTimeSheet(ResultSet rst) throws SQLException {
        TimeSheet timeSheet = new TimeSheet();
        try {
            timeSheet.setId(rst.getInt("id"));
            String datePointStr = rst.getString("datePoint");
            LocalDate localDatePoint = LocalDate.parse(datePointStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            timeSheet.setDatePoint(localDatePoint);
            String startTimeStr = rst.getString("startTime");
            if(startTimeStr != null && !startTimeStr.equals("")){
                LocalTime localStartTime = LocalTime.parse(startTimeStr, DateTimeFormatter.ofPattern("HH:mm:ss"));
                timeSheet.setStartTime(localStartTime);
            }
            String startTimeLunchStr = rst.getString("startTimeLunch");
            if(startTimeLunchStr != null && !startTimeLunchStr.equals("")){
                LocalTime localStartTimeLunch = LocalTime.parse(startTimeLunchStr, DateTimeFormatter.ofPattern("HH:mm:ss"));
                timeSheet.setStartTimeLunch(localStartTimeLunch);
            }
            String endTimeLunchStr = rst.getString("endTimeLunch");
            if(endTimeLunchStr != null && !endTimeLunchStr.equals("")){
                LocalTime localEndTimeLunch = LocalTime.parse(endTimeLunchStr, DateTimeFormatter.ofPattern("HH:mm:ss"));
                timeSheet.setEndTimeLunch(localEndTimeLunch);
            }
            String endTimeStr = rst.getString("endTime");
            if(endTimeStr != null && !endTimeStr.equals("")){
                LocalTime localEndTime = LocalTime.parse(endTimeStr, DateTimeFormatter.ofPattern("HH:mm:ss"));
                timeSheet.setEndTime(localEndTime);
            }
            int userId = rst.getInt("users_id");
            User user = UsersDAO.getById(userId);
            if (user != null) {
                timeSheet.setUser(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return timeSheet;
    }

    public static List<TimeSheet> listAll() throws SQLException {
        Connection connection;
        connection = DbConnection.getConnectionSqlite();
        Statement stmt;
        ResultSet rst;
        stmt = connection.createStatement();
        rst = stmt.executeQuery("select * from time_sheet");
        ArrayList<TimeSheet> timeSheetList = new ArrayList<>();
        while (rst.next()) {
            TimeSheet timeSheet = instantiateTimeSheet(rst);
            timeSheetList.add(timeSheet);
        }
        rst.close();
        connection.close();
        return timeSheetList;
    }

    public static List<TimeSheet> findByDateInitAndFinish(LocalDate dateInit, LocalDate dateFinish, Integer userId) throws SQLException {
        Connection connection;
        connection = DbConnection.getConnectionSqlite();
        PreparedStatement stmt;
        ResultSet rst;
        StringBuilder sql = new StringBuilder();
        sql.append(" select * from time_sheet where 1=1 ");
        List<Object> params = new ArrayList<Object>();

        if(dateInit != null && dateFinish != null){
            sql.append(" and datePoint between ? and ? ");
            String dateInitStr = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(dateInit);
            String dateFinishStr = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(dateFinish);
            params.add(dateInitStr);
            params.add(dateFinishStr);
        } else if(dateInit != null){
            String dateInitStr = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(dateInit);
            sql.append(" and datePoint >= ? ");
            params.add(dateInitStr);
        } else if(dateFinish != null){
            String dateFinishStr = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(dateFinish);
            sql.append(" and datePoint <= ? ");
            params.add(dateFinishStr);
        }

        if(userId != null){
            sql.append(" and users_id = ? ");
            params.add(userId);
        }

        stmt = connection.prepareStatement(sql.toString());
        for (int i = 0; i < params.size(); i++) {
            Object object = params.get(i);
            if(object instanceof String){
                stmt.setString(i+1, (String) object);
            }
            if(object instanceof Integer){
                stmt.setInt(i+1, (Integer) object);
            }
        }

        rst = stmt.executeQuery();
        ArrayList<TimeSheet> timeSheetList = new ArrayList<>();
        //Pegar os itens do resultset e inserir na lista
        while(rst.next()){
            TimeSheet timeSheet = instantiateTimeSheet(rst);
            timeSheetList.add(timeSheet);
        }
        rst.close();
        connection.close();
        return timeSheetList;
    }

    public static List<TimeSheet> findByUserId(int userId) throws SQLException {
        Connection connection;
        connection = DbConnection.getConnectionSqlite();
        PreparedStatement stmt;
        ResultSet rst;
        StringBuilder sql = new StringBuilder();
        sql.append(" select * from time_sheet where users_id = ? order by id desc");

        stmt = connection.prepareStatement(sql.toString());

        stmt.setInt(1, userId);

        rst = stmt.executeQuery();
        ArrayList<TimeSheet> timeSheetList = new ArrayList<>();
        //Pegar os itens do resultset e inserir na lista
        while (rst.next()) {
            TimeSheet timeSheet = instantiateTimeSheet(rst);
            timeSheetList.add(timeSheet);
        }
        rst.close();
        connection.close();
        return timeSheetList;
    }
}
