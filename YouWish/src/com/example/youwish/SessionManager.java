package com.example.youwish;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/*
 * 
 *  Session Manager is implemented as a Singleton class
 * 
 */

public class SessionManager
{
	private static SessionManager sm;

	// Shared Preferences
	private SharedPreferences pref;

	// Editor for Shared preferences
	private Editor editor;

	// Context
	private Context _context;

	// Shared pref mode
	private int PRIVATE_MODE = 0;

	// Sharedpref file name
	private static final String PREF_NAME = "AndroidYouWishPref";

	// All Shared Preferences Keys
	private static final String IS_LOGIN = "IsLoggedIn";

	// Email address (make variable public to access from outside)
	public static final String KEY_EMAIL = "email";

	// Constructor
	private SessionManager(Context context)
	{
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}
	
	// Method to enforce singleton pattern
	public static synchronized SessionManager getSessionManager(Context context)
	{
		if(sm == null)
		{
			sm = new SessionManager(context);
		}
		
		return sm;
	}
	
	

	/**
	 * Create login session
	 * */
	public void createLoginSession( String email )
	{
		// Storing login value as TRUE
		editor.putBoolean(IS_LOGIN, true);

		// Storing email in pref
		editor.putString(KEY_EMAIL, email);

		// commit changes
		editor.commit();
	}

	/**
	 * Check login method wil check user login status If false it will redirect
	 * user to login page Else won't do anything
	 * */
	public boolean checkLogin()
	{
		// Check login status
		if (!this.isLoggedIn())
		{
			// user is not logged in redirect him to Login Activity
			return false;
		}

		return true;
	}

	/**
	 * Get stored session data
	 * */
	public HashMap<String, String> getUserDetails()
	{
		HashMap<String, String> user = new HashMap<String, String>();

		// user email id
		user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

		// return user
		return user;
	}

	/**
	 * Clear session details
	 * */
	public void logoutUser()
	{
		// Clearing all data from Shared Preferences
		editor.clear();
		editor.commit();

	}

	/**
	 * Quick check for login
	 * **/
	// Get Login State
	public boolean isLoggedIn()
	{
		return pref.getBoolean(IS_LOGIN, false);
	}
}
