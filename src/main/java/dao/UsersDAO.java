package dao;

import database.DbConnection;
import model.entity.User;
import model.entity.UserFilter;
import model.enumeration.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UsersDAO {

    public static void create(User user) throws SQLException {
        Connection connection;
        PreparedStatement stmt;
        connection = DbConnection.getConnectionSqlite();
        String save = "insert into users (id, name, login, password, role) values (?, ?, ?, ?, ?)";
        stmt = connection.prepareStatement(save);
        //Receber dados do parâmetro
        stmt.setInt(1, user.getRegistrationCode());
        stmt.setString(2, user.getName());
        stmt.setString(3, user.getLogin());
        stmt.setString(4, user.getPassword());
        stmt.setString(5, user.getRole().toString());
        //Executar a instrução
        stmt.execute();
        connection.close();
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

    public static User getById(int id) throws SQLException {
        Connection connection;
        PreparedStatement stmt;
        ResultSet rst;
        connection = DbConnection.getConnectionSqlite();
        String search = "select * from users where id = ?";
        stmt = connection.prepareStatement(search);
        stmt.setInt(1, id);
        //Receber resultado da consulta
        rst = stmt.executeQuery();
        User user = null;
        while(rst.next()){
            user = instantiateUser(rst);
        }
        rst.close();
        connection.close();
        return user;
    }

    public static User getByIdOrLogin(int id, String login) throws SQLException {
        Connection connection;
        PreparedStatement stmt;
        ResultSet rst;
        connection = DbConnection.getConnectionSqlite();
        String search = "select * from users where id = ? or login = ?";
        stmt = connection.prepareStatement(search);
        stmt.setInt(1, id);
        stmt.setString(2, login);
        //Receber resultado da consulta
        rst = stmt.executeQuery();
        User user = null;
        while(rst.next()){
            user = instantiateUser(rst);
        }
        rst.close();
        connection.close();
        return user;
    }

    public static User getByLoginAndNotId(String login, int id) throws SQLException {
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
        User user = null;
        while(rst.next()){
            user = instantiateUser(rst);
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
            String update = "update users set name = ?, login = ?, password = ?, role = ? where id = ?";
            stmt = connection.prepareStatement(update);
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole().toString());
            stmt.setInt(5, user.getRegistrationCode());
            stmt.execute();
            stmt.close();
            connection.close();
        }
    }

    public static User getByLoginAndPasswordAndRole(String login, String password, Role role) throws SQLException {
        Connection connection;
        connection = DbConnection.getConnectionSqlite();
        ResultSet rst;
        PreparedStatement pst = null;
        User user = null;
        if(connection != null){
            pst = connection.prepareStatement("select * from users where login = ? and password = ? and role = ?"); //TODO Adicionar where para role
            pst.setString(1, login);
            pst.setString(2, password);
            pst.setString(3, role.toString());
            rst = pst.executeQuery();
            //Pegar os itens do resultset e inserir na lista
            if(rst.next()){
                user = instantiateUser(rst);
            }
            rst.close();
            connection.close();
        }
        return user;
    }

    private static User instantiateUser(ResultSet rst) throws SQLException {
        User user = new User();
        user.setRegistrationCode(rst.getInt("id"));
        user.setName(rst.getString("name"));
        user.setLogin(rst.getString("login"));
        user.setPassword(rst.getString("password"));
        String role = rst.getString("role");
        user.setRole(Role.valueOf(role));
        return user;
    }

    public static List<User> listAll() throws SQLException {
        Connection connection;
        connection = DbConnection.getConnectionSqlite();
        Statement stmt;
        ResultSet rst;
        stmt = connection.createStatement();
        rst = stmt.executeQuery("select * from users");
        ArrayList<User> listUsers = new ArrayList<User>();
        while(rst.next()){
            User user = instantiateUser(rst);
            listUsers.add(user);
        }
        rst.close();
        connection.close();
        return listUsers;
    }

    public static List<User> findByFilter(UserFilter filter) throws SQLException {
        Connection connection;
        connection = DbConnection.getConnectionSqlite();
        PreparedStatement stmt;
        ResultSet rst;
        StringBuilder sql = new StringBuilder();
        sql.append(" select * from users where 1=1 ");
        String login = filter.getLogin();
        String name = filter.getName();
        String role = filter.getRole();
        Integer registrationCode = filter.getRegistrationCode();
        List<Object> params = new ArrayList<Object>();

        if(!login.isBlank()){
            sql.append(" and login = ? ");
            params.add(login);
        }

        if(!name.isBlank()){
            sql.append(" and lower(name) like ? ");
            params.add("%" + name + "%");
        }

        if(!role.isBlank()){
            sql.append(" and role = ? ");
            params.add(role);
        }

        if(registrationCode != null){
            sql.append(" and id = ? ");
            params.add(registrationCode);
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
        ArrayList<User> listUsers = new ArrayList<User>();
        //Pegar os itens do resultset e inserir na lista
        while(rst.next()){
            User user = instantiateUser(rst);
            listUsers.add(user);
        }
        rst.close();
        connection.close();
        return listUsers;
    }
}
