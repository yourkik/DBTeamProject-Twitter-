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

	public static Boolean login(String inputUserID, String inputPassword) {
		String selectUserQuery = "SELECT UserID, Password, name FROM User WHERE UserID=?";
		try {
			PreparedStatement preparedStatement = con.prepareStatement(selectUserQuery);
			preparedStatement.setString(1, inputUserID);
			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				String password = resultSet.getString("Password");
				if (password.equals(inputPassword)) {
					System.out.println("로그인 성공");//로그인 성공과 아래 문장을 밖으로 빼야 비밀번호 변경이 더 수월해 질 것 같아요 후에 return 값을 name으로 변경하면 해결 가능 or check 함수를 만들어서 password가 같은지 확인하는 방법도 가능
					System.out.println("반갑습니다 " + resultSet.getString("name")+"님");
					return true;
				} else {
					System.out.println("비밀번호가 일치하지 않습니다.");
					return false;
				}
			} else {
				System.out.println("해당 사용자가 존재하지 않습니다.");
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("로그인 중 오류가 발생했습니다.");
		}
		return false;
	}

	public static void changePassword(String userID, String nowPassword, String newPassword) {
		String updatePasswordQuery = "UPDATE User SET Password=? WHERE UserID=?";
		if (login(userID, nowPassword)) {
			try {
				PreparedStatement preparedStatement = con.prepareStatement(updatePasswordQuery);
				preparedStatement.setString(1, newPassword);
				preparedStatement.setString(2, userID);
				int rowsAffected = preparedStatement.executeUpdate();

				if (rowsAffected > 0) {//같은 UserID와 Password를 가진 사람은 없기 때문에 >0으로 설정했습니다.
					System.out.println("비밀번호 변경이 완료되었습니다.");
				} else {
					System.out.println("해당 사용자가 존재하지 않습니다.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				System.err.println("비밀번호 변경 중 오류가 발생했습니다.");
			}
		} else {
			System.out.println("현재 비밀번호가 맞지 않습니다 다시 확인해주세요");
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
		//signUp("202235041", "박건우2", "yourkik@gachon.ac.kr", "12345");
		
		//login
		login("202235040","12345");

		//Change Password
		changePassword("202235041","12345","123456");
		login("202235041","12345");
		login("202235041","123456");
		
		// Selection
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			String select = "select * from Timeline";
			rs = stmt.executeQuery(select);
			while (rs.next()) {
				String UserID = rs.getString(1);
				if (rs.wasNull())
					UserID = "null";
				String timestamp = rs.getString(2);
				if (rs.wasNull())
					timestamp = "null";
				String TweetID = rs.getString(3);
				if (rs.wasNull())
					TweetID = "null";
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