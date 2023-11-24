import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FollowFunc extends JFrame {

    private static final String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/twitter";
    private static final String USERNAME = "your_username";
    private static final String PASSWORD = "your_password";

    private JComboBox<String> followingComboBox;
    private JComboBox<String> followerComboBox;
    private JTextField targetUserIDField;

    private static Connection con;

    private void connectToDatabase() {
        try {
            // Load JDBC driver and establish a connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/twitter?serverTimezone=UTC&useSSL=false", "root", "1234");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to the database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public FollowFunc() {
        // Connect to the database
        connectToDatabase();

        setTitle("FollowFunc");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        followingComboBox = new JComboBox<>();
        followerComboBox = new JComboBox<>();
        targetUserIDField = new JTextField(15);

        JButton followButton = new JButton("Follow");
        followButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String targetUserID = targetUserIDField.getText();
                if (!targetUserID.isEmpty()) {
                    Follow(loggedInUserID, targetUserID);
                    // Refresh the combo boxes after following
                    fetchUserIDs();
                } else {
                    JOptionPane.showMessageDialog(FollowFunc.this, "Enter a valid target user ID.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Target User ID:"));
        panel.add(targetUserIDField);
        panel.add(followButton);
        panel.add(new JLabel("Following:"));
        panel.add(followingComboBox);
        panel.add(new JLabel("Followers:"));
        panel.add(followerComboBox);

        add(panel);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        setLocationRelativeTo(null);
        setVisible(true);

        fetchUserIDs(); // Fetch user IDs when the GUI is created
    }

    private void fetchUserIDs() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String followingQuery = "SELECT FollowingID FROM Following WHERE UserID = ?";
            String followerQuery = "SELECT FollowerID FROM Follower WHERE UserID = ?";

            // Fetch following user IDs
            fetchUserIDsFromDatabase(connection, followingComboBox, followingQuery);

            // Fetch follower user IDs
            fetchUserIDsFromDatabase(connection, followerComboBox, followerQuery);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void fetchUserIDsFromDatabase(Connection connection, JComboBox<String> comboBox, String query) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, loggedInUserID);

            ResultSet resultSet = preparedStatement.executeQuery();

            comboBox.removeAllItems(); // Clear existing items

            while (resultSet.next()) {
                comboBox.addItem(resultSet.getString(1));
            }
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
	
	public static void FollowingList(String UserID) { // 유저의 팔로잉 목록 확인
		String selectFollowingQuery = "SELECT FollowerID FROM Following WHERE UserID=?";
		try {
			PreparedStatement preparedStatement = con.prepareStatement(selectFollowingQuery);
			preparedStatement.setString(1, UserID);
			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				System.out.println(UserID + "의 팔로잉 목록");
				do {
					String FollowerID = resultSet.getString("FollowerID");
					System.out.println(FollowerID);
				} while(resultSet.next());
			} else {
				System.out.println(UserID + "사용자의 팔로잉 목록이 존재하지 않습니다.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("팔로잉 목록 오류가 발생했습니다.");
		}
	}
	
	public static void FollowerList(String UserID) { // 유저의 팔로워 목록 확인
		String selectFollowerQuery = "SELECT FollowingID FROM Follower WHERE UserID=?";
		try {
			PreparedStatement preparedStatement = con.prepareStatement(selectFollowerQuery);
			preparedStatement.setString(1, UserID);
			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				System.out.println(UserID + "의 팔로워 목록");
				do {
					String FollowingID = resultSet.getString("FollowingID");
					System.out.println(FollowingID);
				} while(resultSet.next());
			} else {
				System.out.println(UserID + "사용자의 팔로워 목록이 존재하지 않습니다.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("팔로워 목록 오류가 발생했습니다.");
		}
	}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FollowFunc();
            }
        });
    }
}
