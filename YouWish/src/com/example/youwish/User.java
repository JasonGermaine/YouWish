package com.example.youwish;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class User
{
	
	@com.google.gson.annotations.SerializedName("email")
	private String mEmail;
	public String getEmail() { return mEmail; }
	public void setEmail(String email) { this.mEmail = email; }

	
	@com.google.gson.annotations.SerializedName("password")
	private String mPassword;
	public String getPassword()
	{ 
		
		return mPassword; 
	}
	
	public void setPassword(String password)
	{ 
		MessageDigest md;
		try
		{
			md = MessageDigest.getInstance("SHA-256");
	        md.update(password.getBytes());
	        
	        byte byteData[] = md.digest();
	 
	        //convert the byte to hex format method 1
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < byteData.length; i++) {
	         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	        }
	 
	        this.mPassword = sb.toString();
		}
		catch (NoSuchAlgorithmException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
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
		setPassword(password);
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
		setPassword(password);
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

