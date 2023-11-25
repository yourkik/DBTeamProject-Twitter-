import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class SimpleTwitterApp extends JFrame {
    private Connection connection;
    private JTextField useridField, usernameField, useremailField, passwordField, followField, tweetField;
    private JTextArea feedArea;
    private boolean isLoggedIn = false;

    public SimpleTwitterApp() {
        //Initialize GUI components
        useridField = new JTextField(10);
        usernameField = new JTextField(10);
        useremailField = new JTextField(20);
        passwordField = new JPasswordField(10);
        followField = new JTextField(10);
        tweetField = new JTextField(30);
        feedArea = new JTextArea(10, 30);

        JButton createUserButton = new JButton("Create User");
        JButton followButton = new JButton("Follow");
        JButton postButton = new JButton("Post");

        //Connect to the database
        connectToDatabase();

        //Set up the GUI layout
        setLayout(new BorderLayout());

        JPanel userPanel = new JPanel();
        userPanel.setBackground(new Color(126, 210, 255)); //배경 색상
        if (!isLoggedIn) {
            //로그인 필드 및 버튼
            JTextField loginIdField = new JTextField(10);
            JPasswordField loginPasswordField = new JPasswordField(10);
            JButton loginButton = new JButton("Login");
            loginButton.setBackground(new Color(255, 255, 255)); //버튼 색상
            loginButton.setForeground(Color.BLUE); //텍스트 색상

            userPanel.add(new JLabel("UserID:"));
            userPanel.add(loginIdField);
            userPanel.add(new JLabel("Password:"));
            userPanel.add(loginPasswordField);
            userPanel.add(loginButton);

            //로그인 버튼 액션 처리
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //로그인 로직 수행
                    String inputId = loginIdField.getText();
                    String inputPassword = new String(loginPasswordField.getPassword());

                    if (checkLogin(inputId, inputPassword)) {
                        isLoggedIn = true;
                        JOptionPane.showMessageDialog(SimpleTwitterApp.this, "로그인 되었습니다.", "로그인 성공", JOptionPane.INFORMATION_MESSAGE);
                        loginButton.setText("Logout");
                        //로그인 사용자의 타임라인 표시
                        displayUserFeed(inputId);
                    } else {
                        JOptionPane.showMessageDialog(SimpleTwitterApp.this, "아이디 또는 비밀번호가 일치하지 않습니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        } else {
            //로그인된 상태에서는 로그아웃 버튼 표시
            JButton logoutButton = new JButton("Logout");
            userPanel.add(logoutButton);

            //로그아웃 버튼 액션 처리
            logoutButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    isLoggedIn = false;
                    JOptionPane.showMessageDialog(SimpleTwitterApp.this, "로그아웃 되었습니다.", "로그아웃", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
        userPanel.add(createUserButton);
        createUserButton.setBackground(new Color(255, 255, 255)); //버튼 색상
        createUserButton.setForeground(Color.BLUE); //텍스트 색상

        JPanel followPanel = new JPanel();
        followPanel.add(new JLabel("Follow User:"));
        followPanel.add(followField);
        followPanel.add(followButton);

        JPanel tweetPanel = new JPanel();
        tweetPanel.add(new JLabel("Tweet:"));
        tweetPanel.add(tweetField);
        tweetPanel.add(postButton);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(new JScrollPane(feedArea), BorderLayout.SOUTH);
        mainPanel.add(userPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        //회원가입 다이얼로그 생성
        createUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //다이얼로그 생성
                JDialog createUserDialog = new JDialog(SimpleTwitterApp.this, "Create User", true);
                createUserDialog.setLayout(new BorderLayout());

                //입력 필드 및 레이블 추가
                JTextField userIdField = new JTextField(10);
                JTextField userNameField = new JTextField(10);
                JTextField userEmailField = new JTextField(20);
                JPasswordField userPasswordField = new JPasswordField(10);

                JPanel inputPanel = new JPanel(new GridLayout(4, 2));
                inputPanel.add(new JLabel("UserID:"));
                inputPanel.add(userIdField);
                inputPanel.add(new JLabel("Username:"));
                inputPanel.add(userNameField);
                inputPanel.add(new JLabel("User Email:"));
                inputPanel.add(userEmailField);
                inputPanel.add(new JLabel("Password:"));
                inputPanel.add(userPasswordField);

                createUserDialog.add(inputPanel, BorderLayout.CENTER);

                //확인 버튼 추가
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //입력된 정보를 사용하여 사용자 생성
                        String userId = userIdField.getText();
                        String userName = userNameField.getText();
                        String userEmail = userEmailField.getText();
                        String userPassword = new String(userPasswordField.getPassword());

                        //사용자 생성 메서드 호출
                        createUser(userId, userName, userEmail, userPassword);

                        //다이얼로그 종료
                        createUserDialog.dispose();
                    }
                });

                //취소 버튼 추가
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //다이얼로그 종료
                        createUserDialog.dispose();
                    }
                });

                JPanel buttonPanel = new JPanel();
                buttonPanel.add(okButton);
                buttonPanel.add(cancelButton);
                createUserDialog.add(buttonPanel, BorderLayout.SOUTH);

                //다이얼로그 크기와 위치 설정
                createUserDialog.setSize(300, 150);
                createUserDialog.setLocationRelativeTo(SimpleTwitterApp.this);
                createUserDialog.setVisible(true);
            }
        });

        followButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String followingUsername = followField.getText();
                followUser(followingUsername);
            }
        });

        postButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tweetContent = tweetField.getText();
                postTweet(tweetContent);
            }
        });
    }

    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/twitter?serverTimezone=UTC&useSSL=false", "root", "1234");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to the database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean checkLogin(String inputId, String inputPassword) {
        try {
            String query = "SELECT * FROM User WHERE UserID = ? AND Password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
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

    private void createUser(String userID, String name, String email, String password) {
        try {
            String query = "INSERT INTO User (UserID, Name, Email, Password) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, userID);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, email);
                preparedStatement.setString(4, password);
                preparedStatement.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "User created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating user.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void followUser(String followingUsername) {
        try {
            String currentUser = usernameField.getText();
            if (!isFollowing(currentUser, followingUsername)) {
                String query = "INSERT INTO Follower (UserID, followingID) VALUES (?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, currentUser);
                    preparedStatement.setString(2, followingUsername);
                    preparedStatement.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "You are now following " + followingUsername, "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "You are already following " + followingUsername, "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error following user.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isFollowing(String followerId, String followingId) throws SQLException {
        String query = "SELECT * FROM Follower WHERE UserID = ? AND followingID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, followerId);
            preparedStatement.setString(2, followingId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private void postTweet(String tweetContent) {
        try {
            String currentUser = usernameField.getText();
            String tweetId = generateTweetId();
            String timestamp = new java.text.SimpleDateFormat("yyyy.MM.dd.HH:mm").format(new java.util.Date());
            String query = "INSERT INTO Tweet (TweetID, WriterID, Content, Timestamp) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, tweetId);
                preparedStatement.setString(2, currentUser);
                preparedStatement.setString(3, tweetContent);
                preparedStatement.setString(4, timestamp);
                preparedStatement.executeUpdate();
            }

            query = "INSERT INTO Timeline (UserID, timestamp, TweetID) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, currentUser);
                preparedStatement.setString(2, timestamp);
                preparedStatement.setString(3, tweetId);
                preparedStatement.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Tweet posted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

            displayUserFeed(currentUser);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error posting tweet.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generateTweetId() {
        return String.valueOf(System.currentTimeMillis());
    }

    private void displayUserFeed(String username) {
        try {
            String queryUserId = "SELECT UserID FROM User WHERE UserID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(queryUserId)) {
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        JOptionPane.showMessageDialog(this, "Current user not found.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            String query = "SELECT t.Content, t.Timestamp FROM Tweet t " +
                    "JOIN Timeline tl ON t.TweetID = tl.TweetID " +
                    "WHERE tl.UserID = ? OR tl.UserID IN (SELECT followingID FROM Follower WHERE UserID = ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    StringBuilder feed = new StringBuilder();
                    while (resultSet.next()) {
                        String content = resultSet.getString("Content");
                        String timestamp = resultSet.getString("Timestamp");
                        feed.append("[").append(timestamp).append("] ").append(content).append("\n");
                    }
                    feedArea.setText(feed.toString());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error displaying feed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimpleTwitterApp app = new SimpleTwitterApp();
            app.setSize(500, 400);
            app.setTitle("Twitter");
            app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            app.setVisible(true);
        });
    }
}
