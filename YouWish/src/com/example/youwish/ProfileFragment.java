package com.example.youwish;

import java.net.MalformedURLException;
import java.util.List;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class ProfileFragment extends Fragment 
{
	
	private FragmentTabHost mTabHost;
	
	private TextView mName;
	private ImageView mProfilePic, mFollow, mFollowing; 
	private ProgressDialog mProcess;
	
	private User user;
	private MobileServiceClient mClient;
	private MobileServiceTable<User> mUserTable;
	private String mFName;
	private String mLName;
	private String text;
	
	
	public ProfileFragment()
	{
	}
	
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

		user = ((YouWishApplication) getActivity().getApplication()).getUser();
		
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        getActivity().setTitle("Profile");
 
        
        mTabHost = (FragmentTabHost)rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);
        
     // Connect client to azure
		try
		{
			mClient = new MobileServiceClient(
					"https://youwish.azure-mobile.net/",
					"DLOtCZsychhFqEupVpZqWBQtcgFPnJ95", getActivity());
			mUserTable = mClient.getTable(User.class);
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mName = (TextView) rootView.findViewById(R.id.profile_name);
		mFollow = (ImageView) rootView.findViewById(R.id.follow);
		mFollowing = (ImageView) rootView.findViewById(R.id.following);
		
		mName.setText(user.getFName() + " " + user.getLName());

		
		mFollow.setOnClickListener(new View.OnClickListener()
		{
			public void onClick( View v )
			{
				mFollow.setVisibility(View.GONE);
				mFollowing.setVisibility(View.VISIBLE);
			}
		});
		mFollowing.setOnClickListener(new View.OnClickListener()
		{
			public void onClick( View v )
			{
				mFollowing.setVisibility(View.GONE);
				mFollow.setVisibility(View.VISIBLE);
			}
		});	
		
		

        mTabHost.addTab(mTabHost.newTabSpec("bio").setIndicator("Bio"),
                BioFragment.class, null);
        TextView c = (TextView) mTabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        c.setTextSize(8);
        mTabHost.addTab(mTabHost.newTabSpec("wishes").setIndicator("Wishes"),
        		WishFragment.class, null);
        TextView d = (TextView) mTabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
        d.setTextSize(8);
        mTabHost.addTab(mTabHost.newTabSpec("followers").setIndicator("Followers"),
        		FollowFragment.class, null);
        TextView a = (TextView) mTabHost.getTabWidget().getChildAt(2).findViewById(android.R.id.title);
        a.setTextSize(8);
        mTabHost.addTab(mTabHost.newTabSpec("following").setIndicator("Following"),
        		FollowFragment.class, null);
        TextView b = (TextView) mTabHost.getTabWidget().getChildAt(3).findViewById(android.R.id.title);
        b.setTextSize(8);
        
        
        
        
        //mName.setText(text);

        return rootView;
    }
	
	
}
