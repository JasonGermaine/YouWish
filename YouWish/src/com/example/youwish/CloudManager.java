package com.example.youwish;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.http.StatusLine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceJsonTable;
import com.microsoft.windowsazure.mobileservices.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.TableJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableJsonQueryCallback;
import com.microsoft.windowsazure.mobileservices.UserAuthenticationCallback;

public class CloudManager
{
	private MobileServiceClient mClient;
	private MobileServiceJsonTable mUserTable, mWishTable, mBucketTable,
			mProductTable;
	private final String TAG = "ConnectivityManager";
	private MobileServiceAuthenticationProvider mProvider;
	private Context mContext;

	public CloudManager(Context context)
	{
		mContext = context;
		try
		{
			mClient = new MobileServiceClient(
					"https://youwish.azure-mobile.net/",
					"DLOtCZsychhFqEupVpZqWBQtcgFPnJ95", mContext);

			mUserTable = mClient.getTable("User");
			mWishTable = mClient.getTable("Wish");
			mBucketTable = mClient.getTable("BucketList");
			mProductTable = mClient.getTable("Product");
		}
		catch (MalformedURLException e)
		{
			Log.e(TAG,
					"There was an error creating the Mobile Service.  Verify the URL");
		}
	}

	public void setContext( Context context )
	{
		mClient.setContext(context);
	}

	public String getUserId()
	{
		return mClient.getCurrentUser().getUserId();
	}

	public static boolean verifyConnection( Context context )
	{
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null
				&& activeNetwork.isConnectedOrConnecting();
		return isConnected;
	}
}
