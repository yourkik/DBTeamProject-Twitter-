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

CREATE TABLE Tweet (
  TweetID varchar(20),
  WriterID varchar(20),
  Content varchar(255),
  Timestamp varchar(25),
  PRIMARY KEY (TweetID),
  FOREIGN KEY (WriterID) REFERENCES User(UserID)#이 문장이 Tweet table contraint error 발생 수정 필요
)DEFAULT CHARACTER SET UTF8;


CREATE TABLE Follower (
   UserID varchar(20),
   followingID varchar(20),
   Timestamp varchar(25),
   Primary key (UserID,FollowingID),
   Foreign key (followingID) references User(UserID),
   Foreign key(UserID) references USer(USerID)
)DEFAULT CHARACTER SET UTF8;

CREATE TABLE Following (
  UserID varchar(20),
  FollowerID varchar(20),
  Timestamp varchar(25),
  Primary key (UserID, FollowerID),
  Foreign key (followerID) references User(UserID),
  Foreign Key(UserID) references User(UserID)
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

/*Tweet 추가*/
insert into Tweet values('0','202235040','Test','2023.11.21');
select *from Tweet;

select *from following;
select *from follower;