package com.example.youwish;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.method.KeyListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class FragmentBio extends Fragment
{
	// Create UI Components
	private Button mB1, mB2, mB3, mEdit, mSave, mCancel;
	private EditText mBio;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) 
	{
              
        mB1 = (Button) getView().findViewById(R.id.profile_button1);
        mB2 = (Button) getView().findViewById(R.id.profile_button2);
        mB3 = (Button) getView().findViewById(R.id.profile_button3);
        mBio = (EditText) getView().findViewById(R.id.bio_title);
        mBio.setTag(mBio.getKeyListener()); 
        mBio.setEnabled(false);
        mEdit = (Button) getView().findViewById(R.id.Button01);
        mSave = (Button) getView().findViewById(R.id.Button02);
        mSave.setVisibility(View.GONE);
        mCancel = (Button) getView().findViewById(R.id.Button03);
        mCancel.setVisibility(View.GONE);
        
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
  		// Handle when Followers button is clicked
        mB2.setOnClickListener(new View.OnClickListener()
 		{
 			public void onClick( View v )
 			{
 				// Create new fragment and transaction
  				FragmentFollowers mFollower = new FragmentFollowers();
  				FragmentTransaction transaction = getFragmentManager().beginTransaction();

  				// Replace whatever is in the fragment_container view with this fragment,
  				// and add the transaction to the back stack
  				transaction.replace(R.id.fragment_container, mFollower);
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
        		
        // Handle when Edit button is clicked
 		mEdit.setOnClickListener(new View.OnClickListener()
 		{
 			public void onClick( View v )
 			{
 				mEdit.setVisibility(View.GONE); 		  
 		        mSave.setVisibility(View.VISIBLE);
 		        mCancel.setVisibility(View.VISIBLE);
 		        
 		        // Make it editable again
 		        mBio.setKeyListener((KeyListener)mBio.getTag());
 		        mBio.setEnabled(true);
 				
 			}
 		});
 		// Handle when Save button is clicked
 		mSave.setOnClickListener(new View.OnClickListener()
 		{
 			public void onClick( View v )
 			{
 				mEdit.setVisibility(View.VISIBLE); 		  
 		        mSave.setVisibility(View.GONE);
 		        mCancel.setVisibility(View.GONE);
 		        
 		        // Make it editable again
 		        mBio.setTag(mBio.getKeyListener());
 		        mBio.setEnabled(false);
 				
 			}
 		});
 	 	// Handle when Cancel button is clicked
 		mCancel.setOnClickListener(new View.OnClickListener()
 		{
 			public void onClick( View v )
 			{
 				mEdit.setVisibility(View.VISIBLE); 		  
 		        mSave.setVisibility(View.GONE);
 		        mCancel.setVisibility(View.GONE);
 		        
 		        // Make it editable again
 		        mBio.setTag(mBio.getKeyListener());
 		        mBio.setEnabled(false);
 				
 			}
 		});
 		
 		// Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bio, container, false);
    }	
}
