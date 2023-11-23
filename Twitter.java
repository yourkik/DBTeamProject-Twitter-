package myDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.DatabaseMetaData;

public class Twitter {
    private static Connection con;

    public static void signUp(String userID, String name, String email, String password) {
        String insertUserQuery = "INSERT INTO User (UserID, Name, Email, Password) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(insertUserQuery);
            preparedStatement.setString(1, userID);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, password);
            preparedStatement.executeUpdate();
            System.out.println("회원가입이 완료되었습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("회원가입 중 오류가 발생했습니다.");
        }
    }

    public static void main(String[] args) {
        con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://127.0.0.1:3306/Twitter";
            String user = "root", passwd = "12345";
            con = DriverManager.getConnection(url, user, passwd);
            System.out.println(con);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        // SignUP
        signUp("202235041", "박건우2", "yourkik@gachon.ac.kr", "12345");

        // Selection
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            String select = "select * from Timeline";
            rs = stmt.executeQuery(select);
            while (rs.next()) {
                String UserID = rs.getString(1);
                if (rs.wasNull()) UserID = "null";
                String timestamp = rs.getString(2);
                if (rs.wasNull()) timestamp = "null";
                String TweetID = rs.getString(3);
                if (rs.wasNull()) TweetID = "null";
                System.out.println("UserID : " + UserID + ", timestamp : " + timestamp + ", TweetID : " + TweetID);
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (stmt != null && !stmt.isClosed())
                    stmt.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }
}