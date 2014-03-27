package com.example.youwish;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;

public class SplashActivity extends Activity
{

	// Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;
	
    // Session Manager Class
	private SessionManager session;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	 
    	// Session class instance
        session = SessionManager.getSessionManager(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
 
        new Handler().postDelayed(new Runnable() {
 
            /*
             * Showing splash screen with a timer.
             */
 
            @Override
            public void run() {
                // This method will be executed once the timer is over
            	
            	if(session.checkLogin() == true)
            	{
            		Intent i = new Intent(
							getApplicationContext(),
							MainActivity.class);
					startActivity(i);
            	}
            	else
            	{
            		Intent i = new Intent(
							getApplicationContext(),
							LoginActivity.class);
					startActivity(i);
            	}
            	finish();
            }
        }, SPLASH_TIME_OUT);
    }

}
