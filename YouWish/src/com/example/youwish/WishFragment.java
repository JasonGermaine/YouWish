package com.example.youwish;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.QueryOrder;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class WishFragment extends Fragment
{
	static final String wish = "wish";
	static final String title = "title";
	static final String desc = "desc";
	static final String thumb_img = "thumb_img";

	ListView list;
	ListAdapter adapter;

	// Create Client

	private MobileServiceClient mClient;
	private MobileServiceTable<BucketList> mBucketTable;
	private MobileServiceTable<Product> mProductTable;
	private ArrayList<Product> mProds;
	private ArrayList<BucketList> mBucks;
	private ArrayList<Wish> mWishes;
	private WishAdapter mAdapter;
	private ListView wishList;
	private ProgressDialog mProcess; 

	public WishFragment()
	{

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_wish, container,
				false);
		
		mProcess = new ProgressDialog(getActivity());
		mProcess.setMessage("Loading Wishes");
		mProcess.setCancelable(false);
		mProcess.show();
		
		// Connect client to azure
		try
		{
			mClient = new MobileServiceClient(
					"https://youwish.azure-mobile.net/",
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
		wishList = (ListView) rootView.findViewById(R.id.list);
		getContent();
		wishList.setAdapter(mAdapter);
		return rootView;
	}

	/*
	 * // Click event for single list row list.setOnItemClickListener(new
	 * OnItemClickListener() {
	 * 
	 * @Override public void onItemClick(AdapterView&lt;?&gt; parent, View view,
	 * int position, long id) {
	 * 
	 * } });
	 */

	private void getContent()

	{
		mProductTable.top(10).orderBy("time_stamp", QueryOrder.Ascending)
				.execute(new TableQueryCallback<Product>()
				{
					public void onCompleted(List<Product> result, int count,
							Exception exception, ServiceFilterResponse response)
					{
						if (exception == null)
						{
							if (result.isEmpty())
							{
								createAndShowDialog(
										"There are no results matching your search!",
										"No Results Found");
							} else
							{
								mProds = (ArrayList<Product>) result;
								for (Wish w : mProds)
								{
									mAdapter.add(w);
								}
								mProcess.dismiss();
							}
						} else
						{
							createAndShowDialog(exception, "Error");
						}
					}
				});
		mBucketTable.top(10).orderBy("time_stamp", QueryOrder.Ascending)
				.execute(new TableQueryCallback<BucketList>()
				{
					public void onCompleted(List<BucketList> result, int count,
							Exception exception, ServiceFilterResponse response)
					{
						if (exception == null)
						{
							if (result.isEmpty())
							{
								
							} else
							{
								mBucks = (ArrayList<BucketList>) result;
							}
						} else
						{
							createAndShowDialog(exception, "Error");
						}
					}
				});
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
