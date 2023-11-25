DROP DATABASE Twitter;
CREATE DATABASE Twitter;
USE Twitter;

CREATE TABLE User (
  UserID varchar(20),
  Name varchar(12),
  Email varchar(20),
  Password varchar(23),
  PRIMARY KEY (UserID)
) DEFAULT CHARACTER SET UTF8;

#Tweet하고 writer로 나눈것 하나와 마지막에 그냥 Tweet만 적어 놓으신게 있는데 뭐가 맞는지 모르겠네요
CREATE TABLE Writer (
  WriterID varchar(20) PRIMARY KEY,
  Name varchar(50),
  Email varchar(50)
);

CREATE TABLE Tweet (
  TweetID varchar(20),
  WriterID varchar(20),
  Content varchar(255),
  Timestamp varchar(25),
  PRIMARY KEY (TweetID),
  FOREIGN KEY (WriterID) REFERENCES Writer(WriterID)
);

CREATE TABLE Timeline (
  UserID varchar(20),
  timestamp varchar(25),
  TweetID varchar(20),
  Primary key (UserID),
  Foreign key(TweetID) references Tweet(TweetID)
) DEFAULT CHARACTER SET UTF8;

CREATE TABLE Follower (
   UserID varchar(20),
   followingID varchar(20),
   Timestamp varchar(25),
   Primary key (UserID,FollowingID),
   Foreign key (followingID) references User(UserID),
   Foreign key(UserID) references USer(USerID)
)default character	set UTF8;

CREATE TABLE Following (
  UserID varchar(20),
  FollowerID varchar(20),
  Timestamp varchar(25),
  Primary key (UserID, FollowerID),
  Foreign key (followerID) references User(UserID),
  Foreign Key(UserID) references User(UserID)
)default character set UTF8;

CREATE TABLE BlackList (
  BlockingUserID varchar(20),
  BlockedUserID varchar(20),
  Timestamp varchar(25),
  Primary Key (BlackingUserID, BlockedUserID),
  Foreign Key (BlockingUserID) references User(UserID),
  Foreign Key (BlockedUserID) references User(UserID)
)DEFAULT CHARACTER SET UTF8;

CREATE TABLE Comment (
  CommentID varchar(20) PRIMARY KEY,
  TweetID varchar(20),
  UserID varchar(20),
  Content varchar(50),
  Timestamp varchar(25),
  FOREIGN KEY (TweetID) REFERENCES Tweet(TweetID),
  FOREIGN KEY (UserID) REFERENCES User(UserID)
)DEFAULT CHARACTER SET UTF8;


/*User 추가*/
insert into User(UserID, Name, email, password) values('202235040', 'ParkGeonwoo','yourkik@gachon.ac.kr', '12345'); 
select *from User;

/*Timeline*/
insert into Timeline(UserID, timestamp) values('202235040', '2023.11.21.10:40');
select *from Timeline;
/*Tweet 추가*/
insert into Tweet values('0','202235040','Test','2023.11.21');
select *from Tweet;
/*update table Timeline add '202235040', */
update Timeline set TweetID = '0' where Timeline.UserID='202235040';
select *from Timeline;