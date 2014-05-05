package com.example.youwish.model;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;

public class Wish
{
	@com.google.gson.annotations.SerializedName("image")
	private String mImage;
	public String getImage() { return mImage; }
	public void setImage(String image) { this.mImage = image; }
	
	@com.google.gson.annotations.SerializedName("title")
	private String mTitle;
	public String getTitle() { return mTitle; }
	public void setTitle(String title) { this.mTitle = title; }
	
	@com.google.gson.annotations.SerializedName("desc")
	private String mDesc;
	public String getDesc() { return mDesc; }
	public void setDesc(String desc) { this.mDesc = desc; }

	@com.google.gson.annotations.SerializedName("location")
	private String mLocation;
	public String getLocation() { return mLocation; }
	public void setLocation(String location) { this.mLocation = location;}

	@com.google.gson.annotations.SerializedName("url")
	private String mUrl;
	public String getUrl() { return mUrl; }
	public void setUrl(String url) { this.mUrl = url;}
	
	@com.google.gson.annotations.SerializedName("priority")
	private int mPriority;
	public int getPriority() { return mPriority; }
	public void setPriority(int priority) { this.mPriority = priority;}
	
	
	public Wish(String image, String title, String userId)
	{
		this.mUserId = userId;
		this.mImage = image;
		this.mTitle = title;
		this.mDesc = null;
		this.mLocation = null;
		this.mUrl = null;
		this.mPriority = 0;
	}
	
	public Wish(String image, String title, String desc, String location, String url, int priority , Date time, String userId)
	{
		this.mUserId = userId;
		this.mImage = image;
		this.mTitle = title;
		this.mDesc = desc;
		this.mLocation = location;
		this.mUrl = url;
		this.mPriority = priority;
		this.mTimeStamp = time;
	}
	
	@com.google.gson.annotations.SerializedName("userid")
	private String mUserId;

	public String getUserId()
	{
		return mUserId;
	}

	public void setUserId( String id )
	{
		this.mUserId = id;
	}
	

	@com.google.gson.annotations.SerializedName("time_stamp")
	private Date mTimeStamp;

	public Date getTimeStamp()
	{
		return mTimeStamp;
	}

	public void setTimeStamp( )
	{
		this.mTimeStamp = new Date();
	}
	
	public DateTime getComparableTime()
	{
		return new DateTime(mTimeStamp);
	}

	public String getUploadedTime()
	{
		
		DateTime todayDate = new DateTime( new Date());
		DateTime uploadDate = new DateTime (mTimeStamp);
		
		int days = Days.daysBetween(uploadDate, todayDate).getDays();
		String message; 
		
		if(days != 0)
		{
			message = days + " days ago";
		}
		else
		{
			int hours = Hours.hoursBetween(uploadDate, todayDate).getHours() % 24;
			if(hours != 0 )
			{
				message = hours + " hours ago";
			}
			else
			{
				int mins = Minutes.minutesBetween(uploadDate, todayDate).getMinutes() % 60;
				
				if(mins  == 0)
				{
					message = "just now";
				}
				else
				{
					message = mins + " minutes ago";
				}
			}
			
		}
		
		return message + " by " + this.mUserId;
	}
}
