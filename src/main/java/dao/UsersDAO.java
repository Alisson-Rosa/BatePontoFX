package dao;

import database.DbConnection;
import model.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsersDAO {

    public static void create(User user) throws SQLException {
        Connection connection;
        PreparedStatement stmt;
        connection = DbConnection.getConnectionSqlite();
        String save = "insert into users (name, login, password) values (?, ?, ?)";
        stmt = connection.prepareStatement(save);
        //Receber dados do parâmetro
        stmt.setString(1, user.getName());
        stmt.setString(2, user.getLogin());
        stmt.setString(3, user.getPassword());
        //Executar a instrução
        stmt.execute();
        connection.close();
    }

    public static User retrive(int id) throws SQLException {
        Connection connection;
        PreparedStatement stmt;
        ResultSet rst;
        connection = DbConnection.getConnectionSqlite();
        String search = "select * from users where id = ?";
        stmt = connection.prepareStatement(search);
        stmt.setInt(1, id);
        //Receber resultado da consulta
        rst = stmt.executeQuery();
        User user = new User();
        while(rst.next()){
            user.setRegistrationCode(rst.getInt("id"));
            user.setName(rst.getString("name"));
            user.setPassword(rst.getString("password"));
            user.setLogin(rst.getString("login"));
        }
        rst.close();
        connection.close();
        return user;
    }

    public static void update(User user) throws SQLException {
        Connection connection;
        connection = DbConnection.getConnectionSqlite();
        PreparedStatement stmt;
        if(connection != null){
            String update = "update users set name = ?, login = ?, password = ? where id = ?";
            stmt = connection.prepareStatement(update);
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getPassword());
            stmt.setInt(4, user.getRegistrationCode());
            stmt.close();
        }
    }

    public static List<User> listAll() throws SQLException {
        Connection connection;
        connection = DbConnection.getConnectionSqlite();
        Statement stmt;
        ResultSet rst;
        stmt = connection.createStatement();
        rst = stmt.executeQuery("select * from users");
        ArrayList<User> listUsers = new ArrayList<User>();
        //Pegar os itens do resultset e inserir na lista
        while(rst.next()){
            User u = new User();
            u.setRegistrationCode(rst.getInt("id"));
            u.setName(rst.getString("name"));
            u.setLogin(rst.getString("login"));
            u.setPassword(rst.getString("password"));
            listUsers.add(u);
        }
        rst.close();
        connection.close();
        return listUsers;
    }
    public static List<User> findByLoginAndPasswordAndRole(String login, String password) throws SQLException {
        Connection connection;
        connection = DbConnection.getConnectionSqlite();
        Statement stmt;
        ResultSet rst;
        stmt = connection.createStatement();
        rst = stmt.executeQuery("select * from users");
        ArrayList<User> listUsers = new ArrayList<User>();
        //Pegar os itens do resultset e inserir na lista
        while(rst.next()){
            User u = new User();
            u.setRegistrationCode(rst.getInt("id"));
            u.setName(rst.getString("name"));
            u.setLogin(rst.getString("login"));
            u.setPassword(rst.getString("password"));
            listUsers.add(u);
        }
        rst.close();
        connection.close();
        return listUsers;
    }
}
