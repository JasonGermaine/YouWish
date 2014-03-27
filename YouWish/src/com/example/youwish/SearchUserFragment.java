package com.example.youwish;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

public class SearchUserFragment extends ListFragment
{
	// Create Client
	private MobileServiceClient mClient;
	private MobileServiceTable<User> mUserTable;

	private ListView userList;
	private UserAdapter mAdapter;
	private String mQuery, mInput; // holds the current query
	private EditText mSearchView;

	public SearchUserFragment()
	{
		// Empty constructor required for fragment subclasses
	}

	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState )
	{

		// Connect client to azure
		try
		{
			mClient = new MobileServiceClient(
					"https://youwish.azure-mobile.net/",
					"DLOtCZsychhFqEupVpZqWBQtcgFPnJ95", getActivity());

			// Get the Mobile Service Table instance to use
			mUserTable = mClient.getTable(User.class);
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		View rootView = inflater.inflate(R.layout.fragment_search_user,
				container, false);

		mSearchView = (EditText) rootView.findViewById(R.id.search_box);

		// Create an adapter to bind the items with the view
		mAdapter = new UserAdapter(getActivity(), R.layout.list_user);
		userList = (ListView) rootView.findViewById(android.R.id.list);
		userList.setAdapter(mAdapter);
		queryCheck();

		return rootView;
	}

	private void queryCheck()
	{
		mSearchView
				.setOnEditorActionListener(new TextView.OnEditorActionListener()
				{

					@Override
					public boolean onEditorAction( TextView v, int actionId,
							KeyEvent event )
					{
						if (actionId == EditorInfo.IME_ACTION_DONE)
						{
							mQuery = mSearchView.getText().toString();
							Toast.makeText(
									getActivity().getApplicationContext(),
									mQuery, Toast.LENGTH_SHORT).show();

							InputMethodManager imm = (InputMethodManager) getActivity()
									.getSystemService(
											Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(getActivity()
									.getWindow().getCurrentFocus()
									.getWindowToken(), 0);
							mSearchView.clearFocus();
							Toast.makeText(
									getActivity().getApplicationContext(),
									"Query", Toast.LENGTH_SHORT).show();

							// Specify your database function here.
							search();
							return true;
						}
						return false;
					}
				});
	}

	private void search()
	{
		Toast.makeText(getActivity().getApplicationContext(), "Search",
				Toast.LENGTH_SHORT).show();
		// Attempt to query using the email input using a callback
		mUserTable.where().field("email").eq(mQuery)
				.execute(new TableQueryCallback<User>()
				{

					public void onCompleted( List<User> result, int count,
							Exception exception, ServiceFilterResponse response )
					{
						if (exception == null)
						{

							for (User u : result)
							{
								mAdapter.add(u);
							}

						}
						else
						{
						}
					}
				});

	}
}
