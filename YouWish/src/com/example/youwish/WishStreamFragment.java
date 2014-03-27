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

    //Mandatory Constructor
    public WishStreamFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

    	
        View rootView = inflater.inflate(R.layout.fragment_wish_stream,container, false);

        mTabHost = (FragmentTabHost)rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("locality").setIndicator("Locality"),
                WishStreamManager.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("popular").setIndicator("Popular"),
        		WishStreamManager.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("recent").setIndicator("Recent"),
        		WishStreamManager.class, null);


        return rootView;
    }
	
}