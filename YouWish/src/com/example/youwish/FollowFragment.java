package com.example.youwish;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FollowFragment extends Fragment 
{
	public FollowFragment()
	{
		
	}
	
	
	public View onCreateView( LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState )
	{	
		View rootView = inflater.inflate(R.layout.fragment_follow,
				container, false);
		
		return rootView;
	}
}
