package com.example.youwish;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.QueryOrder;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

import android.app.Activity;
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
	private List<Pair<String, String>> queryParams = new ArrayList<Pair<String, String>>();

	private Context mContext;

	public AzureService()
	{

	}

	public void setClient(Context context)
	{
		// mContext = context;
		try
		{
			mClient = new MobileServiceClient("https://youwish.azure-mobile.net/",
					"DLOtCZsychhFqEupVpZqWBQtcgFPnJ95", context);
			mUserTable = mClient.getTable(User.class);
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

	public void lookup(String mEmail, TableOperationCallback<User> callback)
	{
		mUserTable.lookUp(mEmail, callback);
	}

	public void updatePassword(User u, TableOperationCallback<User> callback)
	{
		queryParams.add(new Pair<String, String>("update", "password"));

		mUserTable.update(u, queryParams, callback);
	}

	public void updateBio(User u, TableOperationCallback<User> callback)
	{
		queryParams.add(new Pair<String, String>("update", "bio"));

		mUserTable.update(u, queryParams, callback);
	}

	public void addUser(User u, TableOperationCallback<User> callback)
	{
		mUserTable.insert(u, callback);
	}

	public void addBucketList(BucketList b, TableOperationCallback<BucketList> callback)
	{
		mBucketListTable.insert(b, callback);
	}

	public void addProduct(Product p, TableOperationCallback<Product> callback)
	{
		mProductTable.insert(p, callback);
	}

	public void streamProducts(int count, TableQueryCallback<Product> callback)
	{
		if (count == 0)
		{
			mProductTable.top(5).orderBy("time_stamp", QueryOrder.Descending).execute(callback);
		}
		else
		{
			mProductTable.top(5).skip(count).orderBy("time_stamp", QueryOrder.Descending)
					.execute(callback);
		}
	}

	public void streamBucketList(int count, TableQueryCallback<BucketList> callback)
	{
		if (count == 0)
		{
			mBucketListTable.top(5).orderBy("time_stamp", QueryOrder.Descending).execute(callback);
		}
		else
		{
			mBucketListTable.top(5).skip(count).orderBy("time_stamp", QueryOrder.Descending)
					.execute(callback);
		}
	}

	public void updateEmail(User u, TableOperationCallback<User> callback)
	{
		queryParams.add(new Pair<String, String>("update", "email"));

		mUserTable.update(u, queryParams, callback);
	}

	public void searchFirstName(String mFNameOnly, TableQueryCallback<User> callback)
	{
		mUserTable.where().toUpper("fname").eq(mFNameOnly).execute(callback);
	}

	public void searchFullName(String mFName, String mLName, TableQueryCallback<User> callback)
	{
		mUserTable.where().toUpper("fname").eq(mFName).and().toUpper("lname").eq(mLName)
				.execute(callback);
	}
}
