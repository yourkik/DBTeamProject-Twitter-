package Twiiter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.TreeMap;
import java.util.ArrayList;

public class SimpleTwitterApp extends JFrame {

	private JTextField useridField, usernameField, useremailField, passwordField, followField, tweetField;
	private JTextArea feedArea;
	private boolean isLoggedIn = false;
	private JComboBox<String> userComboBox;
	private String loginUserId;
	private String Password;

	Twitter twitter = new Twitter();

	public SimpleTwitterApp() {
		// Initialize GUI components
		useridField = new JTextField(10);
		usernameField = new JTextField(10);
		useremailField = new JTextField(20);
		passwordField = new JPasswordField(10);
		followField = new JTextField(10);
		tweetField = new JTextField(30);
		feedArea = new JTextArea(10, 30);
		userComboBox = new JComboBox<>();

		JButton createUserButton = new JButton("Create User");
		JButton followButton = new JButton("Follow");
		JButton postButton = new JButton("Post");

		// Connect to the database
		Twitter.Connection();

		// Set up the GUI layout
		setLayout(new BorderLayout());

		JPanel userPanel = new JPanel();
		userPanel.setBackground(new Color(126, 210, 255)); // 배경 색상

		JButton loginButton = new JButton("Login");
		loginButton.setBackground(new Color(255, 255, 255)); // 버튼 색상
		loginButton.setForeground(Color.BLUE); // 텍스트 색상

		if (!isLoggedIn) {
			// 로그인 필드 및 버튼
			JTextField loginIdField = new JTextField(10);
			JPasswordField loginPasswordField = new JPasswordField(10);

			userPanel.add(new JLabel("UserID:"));
			userPanel.add(loginIdField);
			userPanel.add(new JLabel("Password:"));
			userPanel.add(loginPasswordField);
			userPanel.add(loginButton);

			// 로그인 버튼 액션 처리
			loginButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// 로그인 로직 수행
					String inputId = loginIdField.getText();
					String inputPassword = new String(loginPasswordField.getPassword());

					if (inputId.isEmpty() || inputPassword.isEmpty()) {
						// 아이디 또는 비밀번호가 비어있으면 오류 메시지 표시
						JOptionPane.showMessageDialog(SimpleTwitterApp.this, "아이디와 비밀번호를 모두 입력하세요.", "입력 오류",
								JOptionPane.ERROR_MESSAGE);
						return; // 로그인 로직 수행하지 않고 종료
					}
					if (Twitter.checkLogin(inputId, inputPassword)) {
						isLoggedIn = true;
						loginUserId = inputId;
						JOptionPane.showMessageDialog(SimpleTwitterApp.this, "로그인 되었습니다.", "로그인 성공",
								JOptionPane.INFORMATION_MESSAGE);

						// 로그인 상태에 따라 컴포넌트를 숨기거나 보이게 설정
						loginIdField.setVisible(false);
						loginPasswordField.setVisible(false);
						loginButton.setVisible(false);

						// 로그아웃 버튼 추가
						JButton logoutButton = new JButton("Logout");
						logoutButton.setBackground(new Color(255, 255, 255)); // 버튼 색상
						logoutButton.setForeground(Color.BLUE); // 텍스트 색상
						userPanel.add(logoutButton);

						// 로그아웃 버튼 액션 처리
						logoutButton.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								isLoggedIn = false;
								loginUserId = null;
								JOptionPane.showMessageDialog(SimpleTwitterApp.this, "로그아웃 되었습니다.", "로그아웃",
										JOptionPane.INFORMATION_MESSAGE);
								// 로그아웃 시 피드 영역을 비우기
								feedArea.setText("");
								// 로그인 상태에 따라 컴포넌트를 숨기거나 보이게 설정
								loginIdField.setVisible(true);
								loginPasswordField.setVisible(true);
								loginButton.setVisible(true);

								// 로그아웃 버튼 제거
								userPanel.remove(logoutButton);

								// 변경 사항을 반영하기 위해 userPanel 다시 그리기
								userPanel.revalidate();
								userPanel.repaint();
							}
						});

						// 로그인 성공 후 사용자 피드 표시
						displayUserFeed(inputId);
						// 로그인 성공 후 필드 비우기
						loginIdField.setText("");
						loginPasswordField.setText("");
					} else {
						JOptionPane.showMessageDialog(SimpleTwitterApp.this, "아이디 또는 비밀번호가 일치하지 않습니다.", "로그인 실패",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			});
		} else {
			// 로그인된 경우에는 로그아웃 버튼 표시
			JButton logoutButton = new JButton("Logout");
			userPanel.add(logoutButton);

			// 로그아웃 버튼 액션 처리
			logoutButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					isLoggedIn = false;
					JOptionPane.showMessageDialog(SimpleTwitterApp.this, "로그아웃 되었습니다.", "로그아웃",
							JOptionPane.INFORMATION_MESSAGE);
					// 로그아웃 시 피드 영역을 비우기
					feedArea.setText("");
					// 로그인 버튼 텍스트를 "Login"으로 변경
					loginButton.setText("Login");
				}
			});
		}
		userPanel.add(createUserButton);
		createUserButton.setBackground(new Color(255, 255, 255));
		createUserButton.setForeground(Color.BLUE);

		// 피드 영역을 JScrollPane로 감싸서 스크롤 가능하게
		JScrollPane feedScrollPane = new JScrollPane(feedArea);
		feedScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		// 피드 영역 왼쪽에 새로운 영역을 추가할 패널을 생성
		JPanel feedLeftPanel = new JPanel();
		feedLeftPanel.setLayout(new BoxLayout(feedLeftPanel, BoxLayout.Y_AXIS));
		feedLeftPanel.setBackground(new Color(255, 255, 255));

		// 팔로잉 목록 버튼
		JButton followingButton = new JButton("Following");

		setButtonSize(followingButton);
		followingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isLoggedIn) {
					JOptionPane.showMessageDialog(null, "현재 로그인돼있지 않습니다.", "error", JOptionPane.WARNING_MESSAGE);
					return;
				}
				// 팔로잉 목록 보기 로직 추가
				displayFollowingList();
				// 팔로우할 수 있는 user검색 후 팔로우 기능
				displayFollowableUsers();
			}
		});
		feedLeftPanel.add(followingButton);
		followingButton.setBackground(new Color(255, 255, 255));
		followingButton.setForeground(Color.BLUE);

		// 팔로워 목록 버튼
		JButton followersButton = new JButton("Followers");
		setButtonSize(followersButton);
		followersButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 팔로워 목록 보기 로직 추가
				displayFollowersList();
			}
		});
		feedLeftPanel.add(followersButton);
		followersButton.setBackground(new Color(255, 255, 255));
		followersButton.setForeground(Color.BLUE);

		// 비밀번호 변경 버튼
		JButton changePasswordButton = new JButton("<html>Change<br>Password</html>");
		setButtonSize(changePasswordButton);
		changePasswordButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 비밀번호 변경 창 열기 로직 추가
				openChangePasswordDialog();
			}
		});
		feedLeftPanel.add(changePasswordButton);
		changePasswordButton.setBackground(new Color(255, 255, 255));
		changePasswordButton.setForeground(Color.BLUE);

		// 트윗 버튼 추가
		JButton tweetActionButton = new JButton("Tweet");
		setButtonSize(tweetActionButton);
		tweetActionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 트윗을 작성하고 게시하기 위한 대화 상자 열기
				openTweetDialog();
			}
		});
		// 트윗 버튼을 userPanel에 추가
		feedLeftPanel.add(tweetActionButton);
		tweetActionButton.setBackground(new Color(255, 255, 255));
		tweetActionButton.setForeground(Color.BLUE);

		// comment 버튼 추가
		JButton commentButton = new JButton("Comment");
		setButtonSize(commentButton);
		commentButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// comment 다이얼로그를 열고 댓글을 처리하는 로직 수행
				openCommentDialog();
			}
		});
		
		feedLeftPanel.add(commentButton);
		commentButton.setBackground(new Color(255, 255, 255));
		commentButton.setForeground(Color.BLUE);
		
		
		// Board 버튼 추가
        JButton boardButton = new JButton("Board");
        setButtonSize(boardButton);
        boardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 게시판 보기 로직 추가
                openBoardDialog();
            }
        });
        feedLeftPanel.add(boardButton);
        boardButton.setBackground(new Color(255, 255, 255));
        boardButton.setForeground(Color.BLUE);

		// 전체 레이아웃 구성
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(feedScrollPane, BorderLayout.CENTER);
		mainPanel.add(userPanel, BorderLayout.NORTH);
		// 피드 영역의 왼쪽에 패널 추가
		mainPanel.add(feedLeftPanel, BorderLayout.WEST);
		// 왼쪽 패널 크기 조정
		feedLeftPanel.setPreferredSize(new Dimension(200, feedLeftPanel.getHeight()));
		add(mainPanel, BorderLayout.CENTER);

		// 회원가입 다이얼로그 생성
		createUserButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 다이얼로그 생성
				JDialog createUserDialog = new JDialog(SimpleTwitterApp.this, "Create User", true);
				createUserDialog.setLayout(new BorderLayout());

				// 입력 필드 및 레이블 추가
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

				// 확인 버튼 추가
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// 입력된 정보를 사용하여 사용자 생성
						String userId = userIdField.getText();
						String userName = userNameField.getText();
						String userEmail = userEmailField.getText();
						String userPassword = new String(userPasswordField.getPassword());

						// 회원가입 메서드 호출
						twitter.signUp(userId, userName, userEmail, userPassword);

						// 다이얼로그 종료
						createUserDialog.dispose();
					}
				});

				// 취소 버튼 추가
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// 다이얼로그 종료
						createUserDialog.dispose();
					}
				});

				JPanel buttonPanel = new JPanel();
				buttonPanel.add(okButton);
				buttonPanel.add(cancelButton);
				createUserDialog.add(buttonPanel, BorderLayout.SOUTH);

				// 다이얼로그 크기와 위치 설정
				createUserDialog.setSize(300, 150);
				createUserDialog.setLocationRelativeTo(SimpleTwitterApp.this);
				createUserDialog.setVisible(true);
			}
		});

		followButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Get the selected or entered username from the userComboBox
				String followingUsername = userComboBox.getSelectedItem().toString();
				if (followingUsername.isEmpty()) {
					JOptionPane.showMessageDialog(SimpleTwitterApp.this, "Please enter a username to follow.", "Info",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				// Call the followUser method
				followUser(followingUsername);
			}
		});
		postButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String tweetContent = tweetField.getText();
				//postTweet(tweetContent);
			}
		});
	}

	// 새로 열리는 창 설정
	private void openInputDialog(String title, String okButtonText, ActionListener okAction, String[] labels,
			JComponent[] components) {
		JDialog dialog = new JDialog(this, title, true);
		dialog.setLayout(new BorderLayout());

		JPanel inputPanel = new JPanel(new GridLayout(labels.length, 2));
		for (int i = 0; i < labels.length; i++) {
			inputPanel.add(new JLabel(labels[i]));
			inputPanel.add(components[i]);
		}

		dialog.add(inputPanel, BorderLayout.CENTER);

		JButton okButton = new JButton(okButtonText);
		okButton.addActionListener(okAction);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> dialog.dispose());

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		dialog.add(buttonPanel, BorderLayout.SOUTH);

		dialog.setSize(300, 150);
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}
	
	// Tweet창 열기
	private void openTweetDialog() {
		
		if (!isLoggedIn) {
			JOptionPane.showMessageDialog(null, "현재 로그인돼있지 않습니다.", "error", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		JTextField tweetContentField = new JTextField(30);
		JLabel label = new JLabel("write the comment for tweet: ");

		openInputDialog("Write Tweet", "Tweet", e -> {
			String tweetContent = tweetContentField.getText().trim();
			if (tweetContent.isEmpty()) {
				JOptionPane.showMessageDialog(null, "Tweet comment is empty.", "error", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			Twitter.tweet(loginUserId, tweetContent);
			JOptionPane.showMessageDialog(this, "트윗이 게시되었습니다.", "Success", JOptionPane.INFORMATION_MESSAGE);
			displayUserFeed(loginUserId);
		}, new String[] { "", "Write for Tweet:" }, new JComponent[] { new JPanel(), tweetContentField });
	}

	// 2023.11.29 정은섭 비밀번호 변경 수정
	// Change Passaword창 열기
	private void openChangePasswordDialog() {
		if (!isLoggedIn) {
			JOptionPane.showMessageDialog(null, "현재 로그인돼있지 않습니다.", "error", JOptionPane.WARNING_MESSAGE);
			return;
		}
		JPasswordField currentPasswordField = new JPasswordField(10);
		JPasswordField newPasswordField = new JPasswordField(10);
		JPasswordField confirmPasswordField = new JPasswordField(10);

		openInputDialog("Change Password", "Change Password", e -> {
			String currentPassword = new String(currentPasswordField.getPassword());
			String newPassword = new String(newPasswordField.getPassword());
			String confirmPassword = new String(confirmPasswordField.getPassword());
			if (!twitter.checkLogin(loginUserId, currentPassword)) {
				JOptionPane.showMessageDialog(null, "현재 비밀번호가 일치하지 않습니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
				return;
			}
			if (!newPassword.equals(confirmPassword)) {
				JOptionPane.showMessageDialog(null, "새 비밀번호와 확인 비밀번호가 일치하지 않습니다.", "입력 오류",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			if (!twitter.updatePassword(loginUserId, newPassword)) {
				JOptionPane.showMessageDialog(this, "비밀번호 변경 중 오류가 발생했습니다.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			JOptionPane.showMessageDialog(this, "비밀번호가 성공적으로 변경되었습니다.", "Success", JOptionPane.INFORMATION_MESSAGE);
		}, new String[] { "Current Password:", "New Password:", "Confirm New Password:" },
				new JComponent[] { currentPasswordField, newPasswordField, confirmPasswordField });
	}
	private void openBoardDialog() {
        // 다이얼로그 생성
        JDialog boardDialog = new JDialog(SimpleTwitterApp.this, "Board", true);
        boardDialog.setLayout(new BorderLayout());

        // 입력 필드 및 레이블 추가
        JTextField userIdField = new JTextField(10);
        JPanel inputPanel = new JPanel(new GridLayout(1, 2));
        inputPanel.add(new JLabel("UserID:"));
        inputPanel.add(userIdField);

        boardDialog.add(inputPanel, BorderLayout.CENTER);

        // 확인 버튼 추가
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 입력된 정보를 사용하여 게시판 보기
                String userId = userIdField.getText();
                displayUserFeed(userId);

                // 다이얼로그 종료
                boardDialog.dispose();
            }
        });

        // 취소 버튼 추가
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 다이얼로그 종료
                boardDialog.dispose();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        boardDialog.add(buttonPanel, BorderLayout.SOUTH);

        // 다이얼로그 크기와 위치 설정
        boardDialog.setSize(300, 150);
        boardDialog.setLocationRelativeTo(SimpleTwitterApp.this);
        boardDialog.setVisible(true);
    }

	//feedArea 초기화 함수
	private void clearFeedArea() {
	    feedArea.setText(""); // feedArea에 있는 모든 텍스트를 지웁니다.
	}
	
	// 2023.11.29 박건우 추가
	// UserFeed에 유저와 follow 중인 사람의 트윗을 순서대로 출력하는 메서드
	private void displayUserFeed(String userId) {
		// Userid가 유효한 값인지 확인하는 함수 필요
		clearFeedArea();
		TreeMap<String, String> tweetsMap = new TreeMap<>();
		TreeMap<String, String> commentsMap = new TreeMap<>();

		tweetsMap = Twitter.displayUserAndFollowingTweets(userId);

		// error가 발생했을 경우 처리 구문
		if (tweetsMap.containsKey("0")) {
			JOptionPane.showMessageDialog(this, tweetsMap.get("0"), "Error", JOptionPane.ERROR_MESSAGE);
		} else if (tweetsMap.containsKey("-1")) {
			JOptionPane.showMessageDialog(this, tweetsMap.get("-1"), "Error", JOptionPane.ERROR_MESSAGE);
		}
		feedArea.append(userId+"'s Board\n");
		// Tweet 내용을 최신순으로 출력
		for (String tweet : tweetsMap.descendingMap().values()) {
			feedArea.append(tweet + "\n");
			commentsMap = Twitter.displayCommentsForTweet(Twitter.getTweetIDFromTweetString(tweet));
			if (!commentsMap.isEmpty()) {
				for (String comment : commentsMap.descendingMap().values()) {
					feedArea.append("      ->" + comment + "\n");
				}
			}
		}
	}

	//comment 작성 다이얼로그 생성
	private void openCommentDialog() {
		
		if (!isLoggedIn) {
			JOptionPane.showMessageDialog(null, "현재 로그인돼있지 않습니다.", "error", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
    	//다이얼로그 생성 및 설정
    	JDialog commentDialog = new JDialog(this, "Comment", true);
    	commentDialog.setLayout(new GridLayout(4, 2));

    	//사용자 선택을 위한 JComboBox 생성 및 설정
    	JComboBox<String> userSelector = new JComboBox<>();

    	for (String user: Twitter.populateUserSelector()) {
        	userSelector.addItem(user);
    	}
    	
    	//트윗 선택을 위한 JComboBox 생성 및 설정
    	JComboBox<String> tweetSelector = new JComboBox<>();

    	//사용자 선택에 따라 트윗 목록을 가져오기 위한 ActionListener 추가
    	userSelector.addActionListener(new ActionListener() {
        		@Override
        		public void actionPerformed(ActionEvent e) {
            		tweetSelector.removeAllItems(); // 이전에 추가된 아이템 제거
            		for(String tweet : Twitter.populateTweetSelector(userSelector.getSelectedItem().toString())){
            			tweetSelector.addItem(tweet);
            		}
        		}
    	});

    	//comment 입력을 위한 컴포넌트 추가
    	JTextField commentField = new JTextField(30);

    	//comment 게시 버튼 추가
    	JButton postCommentButton = new JButton("Comment");
    	postCommentButton.addActionListener(new ActionListener() {
        		@Override
        		public void actionPerformed(ActionEvent e) {
            		//comment 작성 로직
            		String selectedUserID = userSelector.getSelectedItem().toString();
            		String selectedTweetID = tweetSelector.getSelectedItem().toString();
            		String commentContent = commentField.getText();

            		//comment를 데이터베이스에 저장하고 유효성 검사
            		if (!selectedUserID.isEmpty() && !selectedTweetID.isEmpty() && !commentContent.isEmpty()) {
                		Twitter.comment(selectedTweetID, loginUserId, commentContent);
                		commentDialog.dispose();
            		} else {
                		JOptionPane.showMessageDialog(commentDialog, "Comment를 작성하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            		}
        		}
    	});

    	//다이얼로그에 컴포넌트 추가
    	commentDialog.add(new JLabel("User ID:"));
    	commentDialog.add(userSelector);
    	commentDialog.add(new JLabel("Tweet:"));
    	commentDialog.add(tweetSelector);
    	commentDialog.add(new JLabel("Comment:"));
    	commentDialog.add(commentField);
    	commentDialog.add(new JLabel(""));
    	commentDialog.add(postCommentButton);

    	//다이얼로그 속성 설정 및 표시
    	commentDialog.setSize(400, 200);
    	commentDialog.setLocationRelativeTo(this);
    	commentDialog.setVisible(true);
    	
    	displayUserFeed(loginUserId);
	}

	// 팔로잉 목록 보기
	private void displayFollowingList() {
		ArrayList<String> List = twitter.FollowingList(loginUserId);

		StringBuilder followingList = new StringBuilder("Following List:\n");
		if (List.size() != 0) {
			for (int i = 0; i < List.size(); i++) {
				followingList.append(List.get(i)).append("\n");
			}
		} else {
			JOptionPane.showMessageDialog(this, "Your following list does not exist.", "Info",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		JTextArea followingTextArea = new JTextArea(followingList.toString());
		followingTextArea.setEditable(false);
		JOptionPane.showMessageDialog(this, new JScrollPane(followingTextArea), "Following List",
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	
	// 팔로우 할 수 있는 user검색 후 팔로우 기능
	private void displayFollowableUsers() {
		
		ArrayList<String> List = twitter.AllUserList(loginUserId);

		JComboBox<String> followableUsersComboBox = new JComboBox<>();
		followableUsersComboBox.setEditable(true);
		if (List.size() != 0) {
			for (int i = 0; i < List.size(); i++) {
				followableUsersComboBox.addItem(List.get(i));
			}
		}
		JOptionPane.showMessageDialog(this, followableUsersComboBox, "Follow User", JOptionPane.INFORMATION_MESSAGE);
		String selectedUser = followableUsersComboBox.getSelectedItem().toString();
		followUser(selectedUser);

	}

	// 팔로워 목록 보기
	private void displayFollowersList() {

		ArrayList<String> List = twitter.FollowerList(loginUserId);

		StringBuilder followerList = new StringBuilder("Follower List:\n");
		if (List.size() != 0) {
			for (int i = 0; i < List.size(); i++) {
				followerList.append(List.get(i)).append("\n");
			}
		} else {
			JOptionPane.showMessageDialog(this, "Your follower list does not exist.", "Info",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		JTextArea followerTextArea = new JTextArea(followerList.toString());
		followerTextArea.setEditable(false);
		JOptionPane.showMessageDialog(this, new JScrollPane(followerTextArea), "Following List",
				JOptionPane.INFORMATION_MESSAGE);
	}

	// 팔로우 유저
	private void followUser(String followerUser) {
		if (!twitter.isFollowing(loginUserId, followerUser)) {
			if (twitter.isUserIDExists(followerUser)) {
				twitter.Follow(loginUserId, followerUser);
				JOptionPane.showMessageDialog(this, "You are now following " + followerUser, "Success",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			} else {
				JOptionPane.showMessageDialog(this, "User " + followerUser + " does not exist.", "Info",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		} else {
			JOptionPane.showMessageDialog(this, "You are already following " + followerUser, "Info",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
	}

	// 버튼 크기 설정
	private void setButtonSize(JButton button) {
		Dimension buttonSize = new Dimension(200, 80); // 원하는 크기로 조절
		button.setPreferredSize(buttonSize);
		button.setMaximumSize(buttonSize);
		button.setMinimumSize(buttonSize);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			SimpleTwitterApp app = new SimpleTwitterApp();
			app.setSize(600, 475);
			app.setTitle("Twitter");
			app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			app.setVisible(true);
		});
	}
}
