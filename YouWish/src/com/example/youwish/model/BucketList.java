package com.example.youwish.model;

import java.util.Date;

public class BucketList extends Wish
{
	@com.google.gson.annotations.SerializedName("achieve")
	private String mAchieveBy;

	public String getAchieveBy()
	{
		return mAchieveBy;
	}

	public void setAchieveBy( String achieveBy )
	{
		this.mAchieveBy = achieveBy;
	}

	public BucketList(String image, String title, String userId)
	{
		super(image, title, userId);
		this.mAchieveBy = null;
	}

	public BucketList(String image, String title, String desc, String location,
			String url, int priority, Date time, String date, String userId)
	{
		super(image, title, desc, location, url, priority, time, userId);
		this.mAchieveBy = date;
	}



	@com.google.gson.annotations.SerializedName("id")
	private String mId;

	public String getId()
	{
		return mId;
	}

	public void setId( String id )
	{
		this.mId = id;
	}



	@Override
	public boolean equals( Object o )
	{
		return o instanceof BucketList && ((BucketList) o).mId == mId;
	}
	
	
	
}
