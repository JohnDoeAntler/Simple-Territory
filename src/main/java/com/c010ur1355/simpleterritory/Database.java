package com.c010ur1355.simpleterritory;

import com.mysql.jdbc.Connection;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static Connection connection;

    public static void setup(String host, int port, String database, String username, String password){
        try{
            if(getConnection() != null && !getConnection().isClosed()){
                return;
            }

            Class.forName("com.mysql.jdbc.Driver");
            setConnection((Connection)DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    private static void setConnection(Connection _connection) {
        connection = _connection;
    }
}
