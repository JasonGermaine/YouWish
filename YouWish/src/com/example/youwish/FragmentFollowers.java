package com.example.youwish;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentFollowers extends Fragment
{
	private Button mB1, mB2, mB3;
	private ListView mList;
	
	@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) 
	    {
        	mB1 = (Button) getView().findViewById(R.id.Button01);
            mB2 = (Button) getView().findViewById(R.id.Button02);
            mB3 = (Button) getView().findViewById(R.id.Button03);
            
            mList = (ListView) getView().findViewById(R.id.listView1);
            
            // Handle when Wishes button is clicked
            mB1.setOnClickListener(new View.OnClickListener()
      		{
      			public void onClick( View v )
      			{
      				// Create new fragment and transaction
      				FragmentWish mWish = new FragmentWish();
      				FragmentTransaction transaction = getFragmentManager().beginTransaction();

      				// Replace whatever is in the fragment_container view with this fragment,
      				// and add the transaction to the back stack
      				transaction.replace(R.id.fragment_container, mWish);
      				transaction.addToBackStack(null);

      				// Commit the transaction
      				transaction.commit();
      			}
      		});
      		// Handle when Profile button is clicked
            mB2.setOnClickListener(new View.OnClickListener()
     		{
     			public void onClick( View v )
     			{
     				// Create new fragment and transaction
      				FragmentBio mBio = new FragmentBio();
      				FragmentTransaction transaction = getFragmentManager().beginTransaction();

      				// Replace whatever is in the fragment_container view with this fragment,
      				// and add the transaction to the back stack
      				transaction.replace(R.id.fragment_container, mBio);
      				transaction.addToBackStack(null);

      				// Commit the transaction
      				transaction.commit();				
     			}
     		});
     		
     		// Handle when Following button is clicked
            mB3.setOnClickListener(new View.OnClickListener()
     		{
     			public void onClick( View v )
     			{
     				// Create new fragment and transaction
      				FragmentFollowing mFollowing = new FragmentFollowing();
      				FragmentTransaction transaction = getFragmentManager().beginTransaction();

      				// Replace whatever is in the fragment_container view with this fragment,
      				// and add the transaction to the back stack
      				transaction.replace(R.id.fragment_container, mFollowing);
      				transaction.addToBackStack(null);

      				// Commit the transaction
      				transaction.commit();
     			}
     		});
    	
    	
    	// Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_followers, container, false);
    }	
}
