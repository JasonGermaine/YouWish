package com.example.youwish;

import java.net.MalformedURLException;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class BioFragment extends Fragment
{
	private TextView mName, mBioTitle, mBio;
	private EditText mNameEdit, mBioEdit;
	private Button mEdit, mSave, mCancel;
	private ProgressDialog mProcess;

	private MobileServiceClient mClient;
	private MobileServiceTable<User> mUserTable;
	
	private String mFullName;
	private String mBioUpdate;
	private String mFName;
	private String mLName;
	private User user;
	
	
	public BioFragment()
	{
	
	}
	
	public View onCreateView( LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState )
	{	
		View rootView = inflater.inflate(R.layout.fragment_bio,
				container, false);
		
		mProcess = new ProgressDialog(getActivity());
		mProcess.setMessage("Loading Bio");
		mProcess.setCancelable(false);
		mProcess.show();
		
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
		
		mUserTable.lookUp("B9BF3D09-6D41-485C-9292-F12EEB3BE843", new TableOperationCallback<User>() {
			 

			@Override
			public void onCompleted(User entity, Exception exception,
					ServiceFilterResponse response) 
			{
				if(exception == null)
				{
					if(entity.getFName() == null || entity.getFName() == "")
					{
						
					}
					else
					{
						user = new User(entity.getEmail(), entity.getPassword(), entity.getFName(), entity.getLName(), 
								entity.getDOB(), entity.getBio());
						user.setId(entity.getId());
						mName.setText(entity.getFName() + " " + entity.getLName());
						mNameEdit.setText(entity.getFName() + " " + entity.getLName());
						mBio.setText(entity.getBio());
						mBioEdit.setText(entity.getBio());
					}
					mProcess.dismiss();
					
				}
				else
				{
					mProcess.dismiss();
				}
				
			}
		});
		
		
				
		mName = (TextView) rootView.findViewById(R.id.bio_name);
		
		mNameEdit = (EditText) rootView.findViewById(R.id.bio_name_edit);
        
		mBioTitle = (TextView) rootView.findViewById(R.id.bio_title);
		mBio = (TextView) rootView.findViewById(R.id.bio);	

		mBioEdit = (EditText) rootView.findViewById(R.id.bio_edit);
        
        // Buttons
		mEdit = (Button) rootView.findViewById(R.id.Button01);
		
		mSave = (Button) rootView.findViewById(R.id.Button02);
		
		mCancel = (Button) rootView.findViewById(R.id.Button03);
		
		
		
		mNameEdit.setOnTouchListener(new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			mNameEdit.requestFocusFromTouch();
			return false;
		}
		});
		
		mBioEdit.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				mBioEdit.requestFocusFromTouch();
				return false;
			}
			});
		
		
		// Handle when edit button is clicked
		mEdit.setOnClickListener(new View.OnClickListener()
 		{
 			@Override
			public void onClick( View v )
 			{
 				//setFields();
 				mEdit.setVisibility(View.GONE); 		  
 		        mSave.setVisibility(View.VISIBLE);
 		        mCancel.setVisibility(View.VISIBLE);
 		        mName.setVisibility(View.VISIBLE);
 		        mNameEdit.setVisibility(View.VISIBLE);
 		        mBio.setVisibility(View.GONE);
 		        mBioEdit.setVisibility(View.VISIBLE);
 		        
 		        
 			}
 		});		
		
		// OnClick for Save and Cancel Buttons
		
 		// Handle when Save button is clicked
 		mSave.setOnClickListener(new View.OnClickListener()
 		{
 			@Override
 			public void onClick( View v )
 			{
 				mFullName = mNameEdit.getText().toString();
 				mFName = mFullName.substring(0, mFullName.indexOf(' '));
		        mLName = mFullName.substring(mFullName.indexOf(' '));
		        
		        mBioUpdate = mBioEdit.getText().toString();
		        
		        user.setBio(mBioUpdate);
		        user.setFName(mFName);
		        user.setLName(mLName);
		        mUserTable.update(user, new TableOperationCallback<User>() {
		            public void onCompleted(User entity, 
		                                    Exception exception, 
		                                    ServiceFilterResponse response) {
		                if (exception == null) {
		                	// Set Visibilities
		                	mEdit.setVisibility(View.VISIBLE); 		  
		    		        mSave.setVisibility(View.GONE);
		    		        mCancel.setVisibility(View.GONE);
		    		        mName.setVisibility(View.GONE);   
		    		        mNameEdit.setVisibility(View.GONE);		        
		    		        mBio.setVisibility(View.VISIBLE);
		    		        mBio.setText(user.getBio());
		    		        mBioEdit.setVisibility(View.GONE);
		                } 
		                else
		                {
		                	mBioTitle.setText(exception.getMessage());
		                	mEdit.setVisibility(View.VISIBLE); 		  
		    		        mSave.setVisibility(View.GONE);
		    		        mCancel.setVisibility(View.GONE);
		    		        mName.setVisibility(View.GONE);   
		    		        mNameEdit.setVisibility(View.GONE);		        
		    		        mBio.setVisibility(View.VISIBLE);
		    		        mBio.setText(user.getBio());
		    		        mBioEdit.setVisibility(View.GONE);
		                }
		            }
		        });
		        
 				
 			}
 		});
 	 	// Handle when Cancel button is clicked
 		mCancel.setOnClickListener(new View.OnClickListener()
 		{
 			@Override
 			public void onClick( View v )
 			{
 				// Set Visibilities
				mEdit.setVisibility(View.VISIBLE); 		  
		        mSave.setVisibility(View.GONE);
		        mCancel.setVisibility(View.GONE);		        
		        mName.setVisibility(View.GONE);		        
		        mNameEdit.setVisibility(View.GONE);		        
		        mBio.setVisibility(View.VISIBLE);		        
		        mBioEdit.setVisibility(View.GONE);
 				
 			}
 		});
		
		return rootView;
	}
}
