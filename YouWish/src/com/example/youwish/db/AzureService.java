package com.example.youwish.db;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Pair;
import android.widget.Toast;

import com.example.youwish.model.BucketList;
import com.example.youwish.model.Product;
import com.example.youwish.model.User;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.QueryOrder;
import com.microsoft.windowsazure.mobileservices.TableDeleteCallback;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

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


	/*
	 * Direct lookup of user using email
	 */
	public void lookup(String mEmail, TableOperationCallback<User> callback)
	{
		mUserTable.lookUp(mEmail, callback);
	}

	/*
	 * Update a user password
	 */
	public void updatePassword(User u, TableOperationCallback<User> callback)
	{
		queryParams.add(new Pair<String, String>("update", "password"));

		mUserTable.update(u, queryParams, callback);
	}

	/*
	 * Update a user bio
	 */
	public void updateBio(User u, TableOperationCallback<User> callback)
	{
		//queryParams.add(new Pair<String, String>("update", "bio"));

		mUserTable.update(u, queryParams, callback);
	}

	/*
	 * Insert a new user
	 */
	public void addUser(User u, TableOperationCallback<User> callback)
	{
		mUserTable.insert(u, callback);
	}

	/*
	 * Insert new Bucket List
	 */
	public void addBucketList(BucketList b, TableOperationCallback<BucketList> callback)
	{
		mBucketListTable.insert(b, callback);
	}

	
	/*
	 * Insert new Product
	 */
	public void addProduct(Product p, TableOperationCallback<Product> callback)
	{
		mProductTable.insert(p, callback);
	}

	/*
	 * Retrieve Products for the wish stream
	 */
	public void streamProducts(int count, TableQueryCallback<Product> callback)
	{
		if (count == 0)
		{
			mProductTable.top(2).orderBy("time_stamp", QueryOrder.Descending).execute(callback);
		}
		else
		{
			mProductTable.top(2).skip(count).orderBy("time_stamp", QueryOrder.Descending)
					.execute(callback);
		}
	}

	
	/*
	 * Retrieve Bucket Lists for the wish stream
	 */
	public void streamBucketList(int count, TableQueryCallback<BucketList> callback)
	{
		if (count == 0)
		{
			mBucketListTable.top(2).orderBy("time_stamp", QueryOrder.Descending).execute(callback);
		}
		else
		{
			mBucketListTable.top(2).skip(count).orderBy("time_stamp", QueryOrder.Descending)
					.execute(callback);
		}
	}

	/*
	 * Retrieve Products for a specific user
	 */
	public void streamUserProducts(int count, String email, TableQueryCallback<Product> callback)
	{
		if (count == 0)
		{
			mProductTable.where().field("userid").eq(email).top(5).orderBy("time_stamp", QueryOrder.Descending).execute(callback);
		}
		else
		{
			mProductTable.where().field("userid").eq(email).top(5).skip(count).orderBy("time_stamp", QueryOrder.Descending)
					.execute(callback);
		}
	}

	/*
	 * Retrieve BucketList for a specific user
	 */
	public void streamUserBucketList(int count,String email, TableQueryCallback<BucketList> callback)
	{
		if (count == 0)
		{
			mBucketListTable.where().field("userid").eq(email).top(5).orderBy("time_stamp", QueryOrder.Descending).execute(callback);
		}
		else
		{
			mBucketListTable.where().field("userid").eq(email).top(5).skip(count).orderBy("time_stamp", QueryOrder.Descending)
					.execute(callback);
		}
	}
	
	/*
	 * Query user using first name
	 */
	public void searchFirstName(String mFNameOnly, TableQueryCallback<User> callback)
	{
		mUserTable.where().toUpper("fname").eq(mFNameOnly).execute(callback);
	}

	/*
	 * Query user using full name
	 */
	public void searchFullName(String mFName, String mLName, TableQueryCallback<User> callback)
	{
		mUserTable.where().toUpper("fname").eq(mFName).and().toUpper("lname").eq(mLName)
				.execute(callback);
	}
	
	/*
	 * Update user following
	 */
	public void updateFollowing(final User u, final TableOperationCallback<User> callback)
	{
		queryParams.add(new Pair<String, String>("update", "following"));
		mUserTable.update(u, queryParams, callback);			
	}

	/*
	 * Update user profile picture
	 */
	public void updateProfilePic(User user, TableOperationCallback<User> callback)
	{
		queryParams.add(new Pair<String, String>("update", "picture"));
		mUserTable.update(user, queryParams, callback);
		
	}

	
	/*
	 * Delete Bucket List
	 */
	public void deleteBucket(String id, TableDeleteCallback callback)
	{
		mBucketListTable.delete(id, callback);
	}
	
	/*
	 * Delete Product
	 */
	public void deleteProduct(String id, TableDeleteCallback callback)
	{
		mProductTable.delete(id, callback);
	}
}
