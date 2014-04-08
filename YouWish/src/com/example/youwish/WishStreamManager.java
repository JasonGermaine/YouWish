package com.example.youwish;




import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.QueryOrder;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TabHost.OnTabChangeListener;


public class WishStreamManager extends ListFragment
{
	// Create Client
	private MobileServiceClient mClient;
	private MobileServiceTable<BucketList> mBucketTable;
	private MobileServiceTable<Product> mProductTable;
	
	private ArrayList<Product> mProds;
	private ArrayList<BucketList> mBucks;
	private ArrayList<Wish> mWishes;
	
	private WishAdapter mAdapter;
	private ListView wishList;
	
	public WishStreamManager()
	{
	}
	
	

	
	
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState )
	{	
		View rootView = inflater.inflate(R.layout.list_wish,
				container, false);
		
		try
		{
			mClient = new MobileServiceClient("https://youwish.azure-mobile.net/",
					"DLOtCZsychhFqEupVpZqWBQtcgFPnJ95", getActivity());

			// Get the Mobile Service Table instance to use
			mProductTable = mClient.getTable(Product.class);
			mBucketTable = mClient.getTable(BucketList.class);
		} catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// Create an adapter to bind the items with the view
		mAdapter = new WishAdapter(getActivity(), R.layout.wish_row);
		wishList = (ListView) rootView.findViewById(android.R.id.list);
		wishList.setAdapter(mAdapter);
		
		getContent();
		
		return rootView;
	}



	private void getContent()
	{
		mProductTable.top(10).orderBy("time_stamp", QueryOrder.Ascending)
	    .execute(new TableQueryCallback<Product>()
				{
					public void onCompleted(List<Product> result, int count, Exception exception,
							ServiceFilterResponse response)
					{
						if (exception == null)
						{
							if (result.isEmpty())
							{
								createAndShowDialog("There are no results matching your search!",
										"No Results Found");
							}
							else
							{
								mProds = (ArrayList<Product>) result;
								
								for(Wish w : mProds)
								{
									mAdapter.add(w);
								}
							}
						}
						else
						{
							createAndShowDialog(exception, "Error");
						}
					}
				});
		
		mBucketTable.top(10).orderBy("time_stamp", QueryOrder.Ascending)
	    .execute(new TableQueryCallback<BucketList>()
				{
					public void onCompleted(List<BucketList> result, int count, Exception exception,
							ServiceFilterResponse response)
					{
						if (exception == null)
						{
							if (result.isEmpty())
							{
								createAndShowDialog("There are no results matching your search!",
										"No Results Found");
							}
							else
							{
								mBucks = (ArrayList<BucketList>) result;
							}
						}
						else
						{
							createAndShowDialog(exception, "Error");
						}
					}
				});
		
		
		
		
	}



	private void sortWishes()
	{
		for(int i = 0; i < 10; i++)
		{
			mWishes.add(mProds.get(i));
			mWishes.add(mBucks.get(i));
		}
		
		for ( Wish w : mWishes)
		{
			mAdapter.add(w);
		}
	}


	private void createAndShowDialog(Exception exception, String title)
	{
		createAndShowDialog(exception.getCause().getMessage(), title);
	}

	/*
	 * Method to build and display Alert Dialog
	 */
	private void createAndShowDialog(String message, String title)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setMessage(message);
		builder.setTitle(title);
		builder.setPositiveButton("OK", null);
		builder.create().show();
	}

}
