package com.example.youwish;


public class User
{
	
	@com.google.gson.annotations.SerializedName("email")
	private String mEmail;
	public String getEmail() { return mEmail; }
	public void setEmail(String email) { this.mEmail = email; }

	
	@com.google.gson.annotations.SerializedName("password")
	private String mPassword;
	public String getPassword() { return mPassword; }
	public void setPassword(String password) { this.mPassword = password; }
	
	@com.google.gson.annotations.SerializedName("fname")
	private String mFName;
	public String getFName() { return mFName; }
	public void setFName(String fName) { this.mFName = fName; }
	
	@com.google.gson.annotations.SerializedName("lname")
	private String mLName;
	public String getLName() { return mLName; }
	public void setLName(String lName) { this.mLName = lName; }
	
	@com.google.gson.annotations.SerializedName("dob")
	private String mDOB;
	public String getDOB() { return mDOB; }
	public void setDOB(String DOB) { this.mDOB = DOB; }

	
	public User(String email, String password, String fName, String lName, String DOB)
	{
		this.mEmail = email;
		this.mPassword = password;
		this.mFName = fName;
		this.mLName = lName;
		this.mDOB = DOB;

	}
	
	public User()
	{
		
	}
	
	public User (String email, String password) 
	{
		this.mEmail = email;
		this.mPassword = password;
	}
	


	@com.google.gson.annotations.SerializedName("id")
	private String mId;
	public String getId() { return mId;}
	public void setId(String id){this.mId = id;}
	
	
	@Override
	public boolean equals(Object o) {
		return o instanceof User && ((User) o).mId == mId;
	}
	
}

