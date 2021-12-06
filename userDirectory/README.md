# [UserDirectory Application]

This is a Maven Spring Boot project. It runs with Apache Tomcat Server.

This application stores data in MySQL database server. The database has two tables named User and Phone. Reference of User's primary key
i.e. User id is used in Phone table as Foreign Key.

------------------
## DataBase Schema user_directory

CREATE SCHEMA `user_directory` ;

Following is both tables' description

Table : User  
Columns     Data Types    Constraints  
userid      long		  Primary Key , Auto Incremental  
username    varchar(40)	  
emailid		varchar(40)  
password    varchar(40)  


Table : Phone  
Columns      		      Data Types      Constraints  
phoneid		 	 	      long		    Primary Key , Auto Incremental  
phonename			      varchar(40)  
phonemodel 	  	 	      varchar(40)  
preferredphonenumber	  TinyInt		NOT NULL  
phonenumber				  varchar(15)	NOT NULL,Unique  
userid					  int			NOT NULL, Foreign Key          CASCADE ON Delete  
 
CREATE TABLE `user_directory`.`user` (  
  `userid` VARCHAR(36) NOT NULL,  
  `username` VARCHAR(45) NOT NULL,  
  `password` VARCHAR(45) NULL,  
  `emailid` VARCHAR(45) NULL,  
  PRIMARY KEY (`userid`));  
   
  
  CREATE TABLE `user_directory`.`phone` (  
  `phoneid` VARCHAR(36) NOT NULL,  
  `phonename` VARCHAR(45) NULL,  
  `phonemodel` VARCHAR(45) NULL,  
  `preferredphonenumber` TINYINT NOT NULL DEFAULT 0,  
  `phonenumber` VARCHAR(15) NOT NULL,  
  `userid` VARCHAR(36) NOT NULL,  
  PRIMARY KEY (`phoneid`),  
  INDEX `userid_idx` (`userid` ASC) VISIBLE,  
  UNIQUE INDEX `phonenumber_UNIQUE` (`phonenumber` ASC) VISIBLE,  
  CONSTRAINT `userid`  
    FOREIGN KEY (`userid`)  
    REFERENCES `user_directory`.`user` (`userid`)  
    ON DELETE CASCADE  
    ON UPDATE NO ACTION);  
    

--------------------------
 
Application supports following tasks with their APIs and params  
## 1)  Add a user to the system      
API: [POST]    localhost:port/cisco/adduser  
name = John  
password = 12345  
email = john@xyz  
phonename = john's phone  
phonemodel = android  
phonenumber = +32 67754537  
preferredphonenumber = yes  

## 2)  Delete a user from the system
API: [DELETE] localhost:port/cisco/delete   
userId = 1

## 3)  List users in the system
API: [GET]  localhost:8090/cisco/user

## 4)  Add a phone to a user
API: [POST] localhost:port/cisco/addphone  
phonemodel=android   
phonename=Jame's phone  
phonenumber=+91 34556760  
userid=1  
preferredphonenumber=1  

## 5)  Delete a user's phone
API: [DELETE]  localhost:port/cisco/deletephone  
phonenumber=+91 34556760  

## 6)  List a user's phones
API: [GET] localhost:port/cisco/listphones  
userid=2  

## 7)  Update a user's preferred phone number   
API: [PUT] localhost:8090/cisco/updatepreferredphonenumber  
userid=2    
phonenumber=+9189612344    

 Unit Test cases are written in TestNG