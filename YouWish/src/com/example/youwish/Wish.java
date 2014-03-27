package com.example.youwish;

public abstract class Wish
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
	
	
	public Wish(String image, String title)
	{
		this.mImage = image;
		this.mTitle = title;
		this.mDesc = null;
		this.mLocation = null;
		this.mUrl = null;
		this.mPriority = 0;
	}
	
	public Wish(String image, String title, String desc, String location, String url, int priority)
	{
		this.mImage = image;
		this.mTitle = title;
		this.mDesc = desc;
		this.mLocation = location;
		this.mUrl = url;
		this.mPriority = priority;
	}
	

}
