package com.hcl.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
@Entity
public class Users {

	@Id
	private int userId;
	@Column(nullable=false)
	private String userName;
	@Column(nullable=false)
	private String passWord;
	
	public Users()
	{
		
	}

	public Users(int userId, String userName, String passWord) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.passWord = passWord;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	@Override
	public String toString() {
		return "Users [userId=" + userId + ", userName=" + userName + ", passWord=" + passWord + "]";
	}
	



}
