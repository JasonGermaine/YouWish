package com.example.youwish;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class User
{

	@com.google.gson.annotations.SerializedName("id")
	private String mEmail;

	public String getEmail()
	{
		return mEmail;
	}

	public void setEmail(String email)
	{
		this.mEmail = email;
	}

	@com.google.gson.annotations.SerializedName("gender")
	private String mGender;

	public String getGender()
	{
		return mGender;
	}

	public void setGender(String gender)
	{
		this.mGender = gender;
	}

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
			// Choose the SHA 256 Hash
			md = MessageDigest.getInstance("SHA-256");
			md.update(password.getBytes());

			byte byteData[] = md.digest();

			// convert the byte to hex format method 1
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++)
			{
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}

			this.mPassword = sb.toString();
		} catch (NoSuchAlgorithmException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@com.google.gson.annotations.SerializedName("fname")
	private String mFName;

	public String getFName()
	{
		return mFName;
	}

	public void setFName(String fName)
	{
		this.mFName = fName;
	}

	@com.google.gson.annotations.SerializedName("lname")
	private String mLName;

	public String getLName()
	{
		return mLName;
	}

	public void setLName(String lName)
	{
		this.mLName = lName;
	}

	@com.google.gson.annotations.SerializedName("dob")
	private String mDOB;

	public String getDOB()
	{
		return mDOB;
	}

	public void setDOB(String DOB)
	{
		this.mDOB = DOB;
	}

	public User(String email, String password, String fName, String lName, String gender, String DOB)
	{
		this.mEmail = email;
		this.mGender = gender;
		setPassword(password);
		this.mFName = fName;
		this.mLName = lName;
		this.mDOB = DOB;

	}
	
	public User(String email, String password, String fName, String lName, String gender, String DOB, String bio)
	{
		this.mEmail = email;
		this.mGender = gender;
		setPassword(password);
		this.mFName = fName;
		this.mLName = lName;
		this.mDOB = DOB;
		this.mBio = bio;

	}

	public User()
	{

	}

	public User(String email, String password)
	{
		this.mEmail = email;
		setPassword(password);
	}

	public void generateRecovery()
	{
		// String containing valid password characters
		String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random rnd = new Random();

		// Generate number between 6 - 10 for new password length
		int len = rnd.nextInt(10-6) + 6;

		// Build new string with the random length
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
		{
			// Select character at random index of characters
			// Place it at string index
			sb.append(characters.charAt(rnd.nextInt(characters.length())));
		}
		this.mPassword = sb.toString();
	}

	
	@com.google.gson.annotations.SerializedName("bio")
	private String mBio;

	public String getBio()
	{
		return mBio;
	}

	public void setBio(String bio)
	{
		this.mBio = bio;
	}

	

	@Override
	public boolean equals(Object o)
	{
		return o instanceof User && ((User) o).mEmail == mEmail;
	}

}
