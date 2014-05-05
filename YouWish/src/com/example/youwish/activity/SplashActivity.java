package com.example.youwish.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.youwish.R;
import com.example.youwish.db.AzureService;
import com.example.youwish.model.User;
import com.example.youwish.util.SessionManager;
import com.example.youwish.util.YouWishApplication;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;

public class SplashActivity extends Activity
{

	AzureService mAzureService;

	// Splash screen timer
	private static int SPLASH_TIME_OUT = 2000;

	// Session Manager Class
	private SessionManager session;

	private String mEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		mAzureService = ((YouWishApplication) getApplication()).getService();
		mAzureService.setClient(getApplicationContext());

		// Session class instance
		session = SessionManager.getSessionManager(getApplicationContext());
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		if (((YouWishApplication) getApplication()).verifyConnection(this) == true && session.checkLogin() == true)
		{						
			mEmail = session.getUserDetails();
			mAzureService.lookup(mEmail, new TableOperationCallback<User>()
			{

				@Override
				public void onCompleted(User entity, Exception exception,
						ServiceFilterResponse response)
				{
					((YouWishApplication) getApplication()).setUser(entity);
					Intent i = new Intent(getApplicationContext(), MainActivity.class);
					startActivity(i);
					finish();

				}
			});
		}
		else
		{
			if(session.checkLogin() == true)
			{		
				session.logoutUser();
				((YouWishApplication) getApplication()).eraseUser();
			}
			new Handler().postDelayed(new Runnable()
			{

				/*
				 * Showing splash screen with a timer.
				 */

				@Override
				public void run()
				{
					// This method will be executed once the timer is over

					Intent i = new Intent(getApplicationContext(), LoginActivity.class);
					startActivity(i);
					finish();
				}
			}, SPLASH_TIME_OUT);
		}
	}
}
