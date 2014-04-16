package com.example.youwish;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

public class YouWishApplication extends Application
{
	private AzureService mAzureService;
	private Context mContext;
	private User mUser;
	
	public YouWishApplication()
	{
		mAzureService = new AzureService();
	}
	
	public User getUser()
	{
		return mUser;
	}
	
	public void setUser(User u)
	{
		mUser = u;
	}
	
	public void eraseUser()
	{
		mUser = null;
	}

	public AzureService getService()
	{
		return mAzureService;
	}

}