package myDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.DatabaseMetaData;

public class Twitter {
	public static void main(String[] args) {

		Connection con = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String url = "jdbc:mysql://127.0.0.1:3306/Twitter";
			String user = "root", passwd = "12345";
			con = DriverManager.getConnection(url, user, passwd);
			System.out.println(con);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			String select = "select * from Timeline";
			rs = stmt.executeQuery(select);
			while(rs.next()) {
				String UserID = rs.getString(1);
				if (rs.wasNull()) UserID = "null";
				String timestamp = rs.getString(2);
				if(rs.wasNull()) timestamp="null";
				String TweetID = rs.getString(3);
				if (rs.wasNull()) TweetID = "null";
				System.out.println("UserID : "+UserID + ", timestamp : " + timestamp+", TweetID : "+ TweetID);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			if (stmt != null && !stmt.isClosed())
				stmt.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

//		PreparedStatement pstmt = null;
//		try {
//			String psql = "insert into instructor value (?, ?, ?, ?)";
//			pstmt = con.prepareStatement(psql);
//			String id = "12345", name = "Neumann", dept_name = "Biology";
//			int salary = 98000;
//			pstmt.setString(1, id);
//			pstmt.setString(2, name);
//			pstmt.setString(3, dept_name);
//			pstmt.setInt(4, salary);
//			int count = pstmt.executeUpdate();
//			System.out.println(count);
//		} catch (SQLException e1) {
//			e1.printStackTrace();
//		}
//
//		try {
//			if (pstmt != null && !pstmt.isClosed())
//				pstmt.close();
//		} catch (SQLException e1) {
//			e1.printStackTrace();
//		}
	}
}
