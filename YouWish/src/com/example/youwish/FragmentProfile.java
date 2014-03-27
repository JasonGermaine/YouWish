package com.example.youwish;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentProfile extends Fragment
{
    // Create UI Components
	private ImageView profile_pic;
	private TextView name;
	private User user;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		user = new User("example@a.com", "pword", "Shane", "Galvin", "11-01-93");
		// Intialise UI Components
		name.setText(user.getFName() + " " + user.getLName());
		
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }	
}
