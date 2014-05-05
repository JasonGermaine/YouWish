package com.example.youwish.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.youwish.R;
import com.example.youwish.util.YouWishApplication;

public class StreamFragment extends Fragment
{

	private FragmentTabHost mTabHost;

	// Mandatory Constructor
	public StreamFragment()
	{
	}

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	/*
	 * Sets up the tabs for the wish stream
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView;
		if (((YouWishApplication) getActivity().getApplication()).verifyConnection(getActivity()) == false)
		{
			rootView = inflater.inflate(R.layout.connection_failure, container, false);

			RelativeLayout layout = (RelativeLayout) rootView.findViewById(R.id.connection_error);
			layout.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					refresh();
				}
			});
		}
		else
		{

			rootView = inflater.inflate(R.layout.tabhost_stream, container, false);

			mTabHost = (FragmentTabHost) rootView.findViewById(android.R.id.tabhost);
			mTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);

			Bundle wishes = new Bundle();
			wishes.putString("option", "wishes");
			mTabHost.addTab(mTabHost.newTabSpec("wishes").setIndicator("Wishes"),
					WishStreamFragment.class, wishes);

			Bundle prods = new Bundle();
			prods.putString("option", "products");
			mTabHost.addTab(mTabHost.newTabSpec("products").setIndicator("Products"),
					WishStreamFragment.class, prods);

			Bundle bucks = new Bundle();
			bucks.putString("option", "buckets");
			mTabHost.addTab(mTabHost.newTabSpec("buckets").setIndicator("Bucket Lists"),
					WishStreamFragment.class, bucks);

		}
		
		setHasOptionsMenu(true);
		return rootView;
	}
	
	
	private void refresh()
	{
		// update the main content by replacing fragments
		Fragment fragment = new StreamFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);

		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
	}
	
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	       // handle item selection
	       switch (item.getItemId()) {
	          case R.id.action_refresh:
	        	  refresh();
	             return true;
	          default:
	             return super.onOptionsItemSelected(item);
	       }
	    }   

}
