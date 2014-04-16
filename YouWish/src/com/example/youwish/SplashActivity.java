package com.example.youwish;

import com.microsoft.windowsazure.mobileservices.*;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;

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

		if (session.checkLogin() == true)
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
