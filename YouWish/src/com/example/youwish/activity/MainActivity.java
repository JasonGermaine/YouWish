package com.example.youwish.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.youwish.R;
import com.example.youwish.adapters.NavAdapter;
import com.example.youwish.fragment.ProfileFragment;
import com.example.youwish.fragment.SearchUserFragment;
import com.example.youwish.fragment.StreamFragment;
import com.example.youwish.util.SessionManager;
import com.example.youwish.util.YouWishApplication;

public class MainActivity extends FragmentActivity
{

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	public static Context CONTEXT;

	private CharSequence mDrawerTitle;
	private String[] mListTitles;

	// Session Manager Class
	private SessionManager session;

	/*
	 * Set up UI  
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		CONTEXT = this;

		// Session class instance
		session = SessionManager.getSessionManager(getApplicationContext());

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mListTitles = getResources().getStringArray(R.array.list_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener

		mDrawerList.setAdapter(new NavAdapter(this, mListTitles));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(false);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		)
		{
			@Override
			public void onDrawerClosed(View view)
			{
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView)
			{
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null)
		{
			selectItem(0);
		}

	}

	/*
	 * Create the option menu for when user presses android menu button 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_addwish).setVisible(!drawerOpen);
		menu.findItem(R.id.action_refresh).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/*
	 * Handles the on click events for menu options
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item))
		{
			return true;
		}

		// Handle action buttons
		switch (item.getItemId())
		{
		case R.id.action_addwish:
			Intent i = new Intent(getApplicationContext(), AddWishActivity.class);
			startActivity(i);
			return true;
		case R.id.action_logout:
			logout();

			return true;

		case R.id.action_settings:
			Intent j = new Intent(getApplicationContext(), SettingActivity.class);
			startActivity(j);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	
	/*
	 * Logs the user out and clears session data
	 */
	private void logout()
	{
		// Clear the session data
		// This will clear all session data and
		// redirect user to LoginActivity
		session.logoutUser();
		((YouWishApplication) getApplication()).eraseUser();

		Intent j = new Intent(getApplicationContext(), LoginActivity.class);
		startActivity(j);
		finish();
	}

	/* The click listener for ListView in the navigation drawer */
	private class DrawerItemClickListener implements ListView.OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			selectItem(position);
		}
	}

	/*
	 * Handles the drawer list navigation item selection events
	 */
	private void selectItem(int position)
	{
		if (position == 0)
		{
			// update the main content by replacing fragments
			Fragment fragment = new ProfileFragment();
			Bundle args = new Bundle();
			args.putBoolean("localuser", true);
			fragment.setArguments(args);

			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

		}
		else if (position == 1)
		{
			// update the main content by replacing fragments
			Fragment fragment = new StreamFragment();
			Bundle args = new Bundle();
			fragment.setArguments(args);

			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
		}
		else if (position == 2)
		{

			// update the main content by replacing fragments
			Fragment fragment = new SearchUserFragment();
			Bundle args = new Bundle();
			fragment.setArguments(args);

			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

		}
		else
		{
			logout();
		}
		mDrawerList.setItemChecked(position, true);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	/* 
	 * Sets the drawer selector to profile when the user selects another user's profile
	 */
	public void setProfileSelector()
	{
		mDrawerList.setItemChecked(0, true);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during onPostCreate() and
	 * onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
}