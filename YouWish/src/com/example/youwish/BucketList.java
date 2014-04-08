package com.example.youwish;

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

	public BucketList(String image, String title)
	{
		super(image, title);
		this.mAchieveBy = null;
	}

	public BucketList(String image, String title, String desc, String location,
			String url, int priority, String date)
	{
		super(image, title, desc, location, url, priority);
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
