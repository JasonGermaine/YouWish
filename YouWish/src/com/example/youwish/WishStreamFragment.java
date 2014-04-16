package com.example.youwish;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WishStreamFragment extends Fragment
{

	private FragmentTabHost mTabHost;

	// Mandatory Constructor
	public WishStreamFragment()
	{
	}

	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

	}

	public View onCreateView( LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState )
	{

		View rootView = inflater.inflate(R.layout.fragment_wish_stream,
				container, false);

		mTabHost = (FragmentTabHost) rootView
				.findViewById(android.R.id.tabhost);
		mTabHost.setup(getActivity(), getChildFragmentManager(),
				android.R.id.tabcontent);


		mTabHost.addTab(mTabHost.newTabSpec("wishes").setIndicator("Wishes"),
				WishStreamManager.class, null);
		
		mTabHost.addTab(mTabHost.newTabSpec("products")
				.setIndicator("Product"), WishStreamManager.class, null);


		mTabHost.addTab(mTabHost.newTabSpec("buckets").setIndicator("Bucket Lists"),
				WishStreamManager.class, null);


		return rootView;
	}

}
