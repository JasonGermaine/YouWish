package com.example.youwish;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;



public class ProfileActivity extends FragmentActivity 
{
    
	FragmentProfile mProfile;
	FragmentBio mBio;
	FragmentWish mWish;
	FragmentFollowers mFollowers;
	FragmentFollowing mFollowing;
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.top_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            mProfile = new FragmentProfile();
            
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            mProfile.setArguments(getIntent().getExtras());
            
            // Add the fragment to the 'top_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.top_container, mProfile).commit();
        }

        
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            mBio = new FragmentBio();
            
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            mBio.setArguments(getIntent().getExtras());
            
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, mBio).commit();
        }
        
    }

}