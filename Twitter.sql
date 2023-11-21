DROP DATABASE Twitter;
CREATE DATABASE Twitter;
USE Twitter;

CREATE TABLE User (
  UserID varchar(20),
  Name varchar(12),
  Email varchar(20),
  Password varchar(23),
  PRIMARY KEY (UserID)
);

CREATE TABLE Tweet (
  TweetID varchar(20),
  WriterID varchar(20),
  Content varchar(255),
  Timestamp varchar(25),
  PRIMARY KEY (TweetID),
  Foreign key (writerID) references User(UserID)
);

CREATE TABLE Timeline (
  UserID varchar(20),
  timestamp varchar(25),
  TweetID varchar(20),
  Primary key (UserID),
  Foreign key(TweetID) references Tweet(TweetID)
);

CREATE TABLE BlackList (
  FollowID varchar(20),
  Timestamp varchar(25),
  PRIMARY KEY (FollowID)
);

CREATE TABLE Follower (
	UserID varchar(20),
    followingID varchar(20),
    Primary key (UserID,FollowingID),
    Foreign key (followingID) references User(UserID),
    Foreign key(UserID) references USer(USerID)
);

CREATE TABLE Comment (
  CommentID varchar(20),
  TweetID varchar(20),
  WriterID varchar(20),
  Content varchar(50),
  PRIMARY KEY (CommentID),
  foreign key(TweetID) references Tweet(TweetID),
  foreign key(WriterID) references Timeline(UserID)
);

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