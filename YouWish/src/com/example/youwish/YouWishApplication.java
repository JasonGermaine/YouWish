package com.example.youwish;

import android.app.Activity;
import android.app.Application;

public class YouWishApplication extends Application
{
	private AzureService mAzureService;
	private Activity mCurrentActivity;
	private User user;
	
	public YouWishApplication()
	{
		
	}
	
	public User getUser()
	{
		return user;
	}
	
	public void setUser(User u)
	{
		this.user = user;
	}

	public AzureService getAuthService()
	{
		if (mAzureService == null)
		{
			mAzureService = new AzureService(this);
		}
		return mAzureService;
	}

	public void setCurrentActivity(Activity activity)
	{
		mCurrentActivity = activity;
	}

	public Activity getCurrentActivity()
	{
		return mCurrentActivity;
	}
}