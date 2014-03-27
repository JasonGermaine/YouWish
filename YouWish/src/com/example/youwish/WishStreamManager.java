package com.example.youwish;

import java.net.MalformedURLException;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

public class WishStreamManager extends ListFragment
{
	public WishStreamManager()
	{
	}
	
	

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState )
	{	
		View rootView = inflater.inflate(R.layout.wish_stream_search,
				container, false);
		return rootView;
	}


}
