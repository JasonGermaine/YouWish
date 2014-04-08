package com.example.youwish;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

public class AzureService
{

	private MobileServiceClient mClient;
	private MobileServiceTable<Product> mProductTable;
	private MobileServiceTable<BucketList> mBucketListTable;
	private MobileServiceTable<User> mUserTable;
	
	private Context mContext;
	private boolean mShouldRetryAuth;
	private boolean mIsCustomAuthProvider = false;

	public AzureService(Context context) {
		mContext = context;
		try
		{
			mClient = new MobileServiceClient("https://youwish.azure-mobile.net/",
					"DLOtCZsychhFqEupVpZqWBQtcgFPnJ95", mContext);
			mProductTable = mClient.getTable(Product.class);
			mBucketListTable = mClient.getTable(BucketList.class);
		} catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			Toast.makeText(mContext, "Client Problem", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	public void login(String email, TableQueryCallback<User> callback) 
	{
		mUserTable.where().field("email").eq(email).execute(callback);			
	}
	
	
}
