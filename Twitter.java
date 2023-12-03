package Twiiter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.DatabaseMetaData;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

import javax.swing.JOptionPane;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;


public class Twitter {
	private static Connection con;
	
	//중복된 ID인지 확인하는 함수
	public static boolean isUserIDExists(String userID) {
	    String checkUserQuery = "SELECT UserID FROM User WHERE UserID=?";
	    try {
	        PreparedStatement preparedStatement = con.prepareStatement(checkUserQuery);
	        preparedStatement.setString(1, userID);
	        ResultSet resultSet = preparedStatement.executeQuery();
	        return resultSet.next(); // true면 중복된 ID가 존재함
	    } catch (SQLException e) {
	        e.printStackTrace();
	        System.err.println("사용자 ID 확인 중 오류가 발생했습니다.");
	        return false;
	    }
	}
	
	public static void signUp(String userID, String name, String email, String password) {
		if (isUserIDExists(userID)) {
	        System.out.println("이미 존재하는 사용자 ID입니다. 다른 사용자 ID를 선택해주세요.");
	        return;
	    }
		String insertUserQuery = "INSERT INTO User (UserID, Name, Email, Password) VALUES (?, ?, ?, ?)";
		try {
			PreparedStatement preparedStatement = con.prepareStatement(insertUserQuery);
			preparedStatement.setString(1, userID);
			preparedStatement.setString(2, name);
			preparedStatement.setString(3, email);
			preparedStatement.setString(4, password);
			preparedStatement.executeUpdate();
			System.out.println("회원가입이 완료되었습니다.");
			
			LocalDate now = LocalDate.now();
			 // 포맷 정의        
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
			String formatedNow = now.format(formatter);
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

	public static boolean checkLogin(String inputId, String inputPassword) {
		try {
			String query = "SELECT * FROM User WHERE UserID = ? AND Password = ?";
			try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
				preparedStatement.setString(1, inputId);
				preparedStatement.setString(2, inputPassword);
				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					return resultSet.next();
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	// 비밀번호 업데이트 메서드
	public static boolean updatePassword(String userId, String newPassword) {
		try {
			String query = "UPDATE User SET Password = ? WHERE UserID = ?";
			try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
				preparedStatement.setString(1, newPassword);
				preparedStatement.setString(2, userId);
				preparedStatement.executeUpdate();
			}
			System.out.println("비밀번호가 성공적으로 변경되었습니다.");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("비밀번호 변경 중 오류가 발생했습니다.");
			return false;
		}
	}

	public static void tweet(String WriterID, String content) {
    		String insertTweetQuery = "INSERT INTO Tweet (TweetID, WriterID, Content, Timestamp) VALUES (?, ?, ?, ?)";
    		try {
    		  	PreparedStatement preparedStatement = con.prepareStatement(insertTweetQuery);
			String tweetID = UUID.randomUUID().toString(); // Generate a unique tweetID
			tweetID = tweetID.replaceAll("-", "").substring(0, 20);
       			String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()); // Get the current timestamp
       	 		preparedStatement.setString(1, tweetID);
        		preparedStatement.setString(2, WriterID);
        		preparedStatement.setString(3, content);
        		preparedStatement.setString(4, timestamp);
        		preparedStatement.executeUpdate();
        		System.out.println("게시완료.");
    		} catch (SQLException e) {
        		e.printStackTrace();
        		System.err.println("게시중 오류가 발생하였습니다.");
    		}
	}

	public static void comment(String tweetID, String WriterID, String content) {
    		String insertCommentQuery = "INSERT INTO Comment (CommentID, TweetID, WriterID, Content, Timestamp) VALUES (?, ?, ?, ?, ?)";
    		try {
        		PreparedStatement preparedStatement = con.prepareStatement(insertCommentQuery);
        		String commentID = UUID.randomUUID().toString(); // Generate a unique commentID
        		commentID = commentID.replaceAll("-", "").substring(0, 20);
        		String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()); // Get the current timestamp
		        preparedStatement.setString(1, commentID);
		        preparedStatement.setString(2, tweetID);
		        preparedStatement.setString(3, WriterID);
		        preparedStatement.setString(4, content);
		        preparedStatement.setString(5,timestamp);
		        preparedStatement.executeUpdate();
		        System.out.println("게시완료.");
		} catch (SQLException e) {
		        e.printStackTrace();
		        System.err.println("comment 게시중 오류가 발생하였습니다.");
		}
	}

	
	public static void Follow(String userID, String followID) {									  // 유저1이 유저2를 팔로우하는 상황 가정
		String insertFollowingQuery = "INSERT INTO Following (UserID, FollowerID) VALUES (?, ?)"; // 유저1의 팔로잉 목록에 유저2를 업데이트
		String insertFollowerQuery = "INSERT INTO Follower (UserID, FollowingID) VALUES (?, ?)";  // 유저2의 팔로워 목록에 유저1을 업데이트
		try {
			PreparedStatement preparedStatement = con.prepareStatement(insertFollowingQuery);
			preparedStatement.setString(1, userID);
			preparedStatement.setString(2, followID);
			PreparedStatement preparedStatement2 = con.prepareStatement(insertFollowerQuery);
			preparedStatement2.setString(1, followID);
			preparedStatement2.setString(2, userID);
			preparedStatement.executeUpdate();
			preparedStatement2.executeUpdate();
			System.out.println(userID + "(이)가 " + followID +"(을)를 팔로우했습니다.");
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("팔로우에 실패했습니다.");
		}
	}
	
	public static ArrayList AllUserList(String userID) {
		String query = "SELECT UserID FROM User WHERE UserID != ?";
		ArrayList<String> UserList = new ArrayList<>();
		try {
			PreparedStatement preparedStatement = con.prepareStatement(query);
			preparedStatement.setString(1, userID);
			ResultSet resultSet = preparedStatement.executeQuery();
			
			while (resultSet.next()) {
				String userId = resultSet.getString("UserID");
				UserList.add(userId);
			}
			return UserList;
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("오류가 발생했습니다.");
			return UserList;
		}
	}
	
	public static boolean isFollowing(String followingUser, String followerUser) {
		String query = "SELECT * FROM Following WHERE UserID = ? AND followerID = ?";
		try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
			preparedStatement.setString(1, followingUser);
			preparedStatement.setString(2, followerUser);
			ResultSet resultSet = preparedStatement.executeQuery();
			return resultSet.next();
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("오류가 발생했습니다.");
			return false;
		}
	}
	
	public static ArrayList FollowingList(String UserID) { // 유저의 팔로잉 목록 확인
		String selectFollowingQuery = "SELECT FollowerID FROM Following WHERE UserID=?";
		ArrayList<String> followingList = new ArrayList<>();
		try {
			PreparedStatement preparedStatement = con.prepareStatement(selectFollowingQuery);
			preparedStatement.setString(1, UserID);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				String FollowerID = resultSet.getString("FollowerID");
				followingList.add(FollowerID);
			}
			return followingList;
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("팔로잉 목록 오류가 발생했습니다.");
			return followingList;
		}
	}
	
	public static ArrayList FollowerList(String UserID) { // 유저의 팔로워 목록 확인
		String selectFollowerQuery = "SELECT FollowingID FROM Follower WHERE UserID=?";
		ArrayList<String> followerList = new ArrayList<>();
		try {
			PreparedStatement preparedStatement = con.prepareStatement(selectFollowerQuery);
			preparedStatement.setString(1, UserID);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				String FollowingID = resultSet.getString("FollowingID");
				followerList.add(FollowingID);
			}
			return followerList;
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("팔로워 목록 오류가 발생했습니다.");			
			return followerList;
		}
	}
	
	public static String getTweetIDFromTweetString(String tweet) {
	    String[] parts = tweet.split(", "); // 예시: "TweetID: 123, Content: ..."
	    for (String part : parts) {
	        if (part.startsWith("TweetID:")) {
	            return part.split(": ")[1]; // "123"
	        }
	    }
	    return null; // 적절한 부분을 찾지 못한 경우
	}
	
	public static TreeMap<String, String> displayCommentsForTweet(String tweetID) {
	    TreeMap<String, String> commentsMap = new TreeMap<>(); // TreeMap을 사용하여 댓글을 최신순으로 정렬

	    String selectCommentsQuery = "SELECT * FROM Comment WHERE TweetID=?";
	    try {
	        PreparedStatement preparedStatement = con.prepareStatement(selectCommentsQuery);
	        preparedStatement.setString(1, tweetID);
	        ResultSet resultSet = preparedStatement.executeQuery();

	        while (resultSet.next()) {
	            String commentID = resultSet.getString("CommentID");
	            String content = resultSet.getString("Content");
	            String userID = resultSet.getString("WriterID");
	            String timestamp = resultSet.getString("Timestamp");
	            commentsMap.put(timestamp, "CommentID: " + commentID + ", TweetID: " + tweetID + ", WriterID: " + userID + ", Content: " + content + ", Timestamp: " + timestamp);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        System.err.println("댓글을 가져오는 중 오류가 발생했습니다.");
	        commentsMap.put("-1", "댓글을 가져오는 중 오류가 발생했습니다.");
	        return commentsMap;
	    }

	    // 최신순으로 출력
	    if (!commentsMap.isEmpty()) {
	    	System.out.println("트윗(" + tweetID + ")에 대한 최신순 댓글:");
	        for (String comment : commentsMap.descendingMap().values()) {
	            System.out.println("-> " + comment);
	        }
	    }
	    
	    return commentsMap;
	}

	
	public static TreeMap<String, String> displayUserAndFollowingTweets(String userID) {
	    TreeMap<String, String> tweetsMap = new TreeMap<>(); // TreeMap을 사용하여 트윗을 최신순으로 정렬

	    // 자신의 트윗 가져오기
	    String selectUserTweetsQuery = "SELECT * FROM Tweet WHERE WriterID=?";
	    try {
	        PreparedStatement preparedStatement = con.prepareStatement(selectUserTweetsQuery);
	        preparedStatement.setString(1, userID);
	        ResultSet resultSet = preparedStatement.executeQuery();

	        while (resultSet.next()) {
	            String tweetID = resultSet.getString("TweetID");
	            String content = resultSet.getString("Content");
	            String WriterID = resultSet.getString("WriterID");
	            String timestamp = resultSet.getString("Timestamp");
	            tweetsMap.put(timestamp, "TweetID: " + tweetID + ", WriterID: "+WriterID + ", Content: " + content + ", Timestamp: " + timestamp);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        System.err.println("자신의 트윗을 가져오는 중 오류가 발생했습니다.");
	        tweetsMap.put("-1", "자신의 트윗을 가져오는 중 오류가 발생했습니다.");
	        return tweetsMap;
	    }

	    // 팔로우한 사용자들의 트윗 가져오기
	    String selectFollowingTweetsQuery = "SELECT * FROM Tweet " +
	            "JOIN Following ON Tweet.WriterID = Following.FollowerID " +
	            "WHERE Following.UserID = ?";
	    try {
	        PreparedStatement preparedStatement = con.prepareStatement(selectFollowingTweetsQuery);
	        preparedStatement.setString(1, userID);
	        ResultSet resultSet = preparedStatement.executeQuery();

	        while (resultSet.next()) {
	            String tweetID = resultSet.getString("TweetID");
	            String content = resultSet.getString("Content");
	            String WriterID = resultSet.getString("WriterID");
	            String timestamp = resultSet.getString("Timestamp");
	            tweetsMap.put(timestamp, "TweetID: " + tweetID +", WriterID: "+WriterID+", Content: " + content + ", Timestamp: " + timestamp);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        System.err.println("팔로우한 사용자들의 트윗을 가져오는 중 오류가 발생했습니다.");
	        tweetsMap.put("0", "팔로우한 사용자들의 트윗을 가져오는 중 오류가 발생했습니다.");
	        return tweetsMap;
	    }

	    // 최신순으로 출력
	    System.out.println(userID + "님의 트윗과 팔로우한 사용자들의 트윗 (최신순):");
	    for (String tweet : tweetsMap.descendingMap().values()) {
	        System.out.println(tweet);
	        displayCommentsForTweet(getTweetIDFromTweetString(tweet));
	    }	
	    return tweetsMap;
	}
	
	
	public static void Connection() {
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
	}
	
	public static void main(String[] args) {
		Connection();
//		// SignUP
//		signUp("202235041", "박건우2", "yourkik@gachon.ac.kr", "12345");
//		signUp("202235042", "ex3", "ex3@gachon.ac.kr", "12345");
//		signUp("202235043", "ex4", "ex4@gachon.ac.kr", "12345");
//		
//		//login
//		login("202235040","12345");
//
//		//Change Password
//		changePassword("202235041","12345","123456");
//		login("202235041","12345");
//		login("202235041","123456");
//		
//		//Follow
//		Follow("202235040","202235041");
//		Follow("202235040","202235042");
//		Follow("202235040","202235043");
//		Follow("202235043","202235041");
//		
//		//FollowingList
		System.out.println(FollowingList("202235040"));
//		//FollowerList
		FollowerList("202235041");
//		
		comment("0", "202235040", "hello!");
//		//tweet("202235040","Hello");
		displayUserAndFollowingTweets("202235040");
//		
//		//tweet("202235041","Hello 202235040!");
//		displayUserAndFollowingTweets("202235040");
		
	}
}
