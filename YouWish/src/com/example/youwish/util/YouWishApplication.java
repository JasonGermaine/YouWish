package com.example.youwish.util;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.youwish.db.AzureService;
import com.example.youwish.model.User;

public class YouWishApplication extends Application
{
	private AzureService mAzureService;
	private User mUser;
	private User mGuest;
	
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

	
	public boolean verifyConnection( Context context )
	{
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null
				&& activeNetwork.isConnectedOrConnecting();
		return isConnected;
	}
	
	public User getGuest()
	{
		return mGuest;
	}
	
	public void setGuest(User u)
	{
		mGuest = u;
	}
	
	public void eraseGuest()
	{
		mGuest = null;
	}
}