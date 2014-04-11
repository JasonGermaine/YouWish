package com.example.youwish;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.InputFilter;
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
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

public class SearchUserFragment extends ListFragment
{
	private ConnectionManager mConnection;

	// Create Client
	private MobileServiceClient mClient;
	private MobileServiceTable<User> mUserTable;

	// UI Components
	private ListView userList;
	private UserAdapter mAdapter;
	private EditText mSearchView;

	// Strings to manage query
	private String mQuery, mFName, mLName, mEmail, mFNameOnly;

	// Validation Components
	private Matcher m;
	private Pattern namePattern, emailPattern;

	private ProgressDialog mProcess;

	public SearchUserFragment()
	{
		// Empty constructor required for fragment subclasses
	}

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

	}

	/*
	 * Initialize UI components and set appropriate listeners
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mProcess = new ProgressDialog(getActivity());

		mConnection = ConnectionManager.getConnectionManager();

		// Connect client to azure
		try
		{
			mClient = new MobileServiceClient("https://youwish.azure-mobile.net/",
					"DLOtCZsychhFqEupVpZqWBQtcgFPnJ95", getActivity());

			// Get the Mobile Service Table instance to use
			mUserTable = mClient.getTable(User.class);
		} catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Inflate view for this fragment
		View rootView = inflater.inflate(R.layout.fragment_search_user, container, false);

		// EditText for user to input search criteria
		mSearchView = (EditText) rootView.findViewById(R.id.search_box);
		mSearchView.setFilters(new InputFilter[]
		{ new InputFilter.LengthFilter(100) });

		// Create an adapter to bind the items with the view
		mAdapter = new UserAdapter(getActivity(), R.layout.list_user);
		userList = (ListView) rootView.findViewById(android.R.id.list);
		userList.setAdapter(mAdapter);
		queryCheck();

		return rootView;
	}

	/*
	 * This method handles user input. It attempts to determine what search criteria the user has
	 * entered. It attempts to validate. Using the validation results, it determines if the user is
	 * searching by email, first name or full name
	 */
	private void queryCheck()
	{
		// Set listener to determine when user presses DONE
		mSearchView.setOnEditorActionListener(new TextView.OnEditorActionListener()
		{

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				if (actionId == EditorInfo.IME_ACTION_DONE)
				{
					if (mConnection.verifyConnection(getActivity()) == false)
					{
						Toast.makeText(getActivity(), "No Internet Connectivty", 1).show();
					}
					else
					{

						mProcess.setMessage("Searching");
						mProcess.show();
						// Patterns for validation
						emailPattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
								+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

						namePattern = Pattern.compile("[^a-z]", Pattern.CASE_INSENSITIVE);

						mSearchView.setError(null);
						View focusView = null;

						mAdapter.clear();

						// Get search query
						mQuery = mSearchView.getText().toString();

						// If search is empty
						if (TextUtils.isEmpty(mQuery))
						{
							mSearchView.setError(getString(R.string.error_field_required));
							focusView = mSearchView;
							focusView.requestFocus();
							mProcess.dismiss();
							return false;
						}
						else
						{
							// If the query contains a space we assume multiple words are involved
							if (mQuery.contains(" "))
							{
								// Split the string at the space
								String[] names = mQuery.split("\\s+");

								// If more than two words are present show error
								if (names.length != 2)
								{
									mSearchView.setError(getString(R.string.error_invalid_format));
									focusView = mSearchView;
									focusView.requestFocus();
									mProcess.dismiss();
									return false;
								}
								else
								{
									// Set the two words to first and last name
									mFName = names[0].toUpperCase();
									mLName = names[1].toUpperCase();

									// Validate names
									m = namePattern.matcher(mFName);
									if (m.find() == true)
									{
										mSearchView
												.setError(getString(R.string.error_invalid_search));
										focusView = mSearchView;
										focusView.requestFocus();
										mProcess.dismiss();
										return false;
									}
									m = namePattern.matcher(mLName);
									if (m.find() == true)
									{
										mSearchView
												.setError(getString(R.string.error_invalid_search));
										focusView = mSearchView;
										focusView.requestFocus();
										mProcess.dismiss();
										return false;
									}
								}
							}
							else
							{

								// One word query, at first assume email
								mEmail = mQuery.toLowerCase();

								// Test for valid Emial
								m = emailPattern.matcher(mEmail);
								if (m.find() == false)
								{

									mSearchView.setError(getString(R.string.error_invalid_search));
									focusView = mSearchView;
									focusView.requestFocus();
									mProcess.dismiss();
									return false;

								}
							}
						}

						InputMethodManager imm = (InputMethodManager) getActivity()
								.getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus()
								.getWindowToken(), 0);
						mSearchView.clearFocus();

						// Specify your database function here.
						search();
						return true;
					}
				}
				mProcess.dismiss();
				return false;
			}
		});
	}

	/*
	 * Search the DB using the validated search criteria
	 */
	private void search()
	{

		// Attempt to query using the email input using a callback
		mUserTable.where().toLower("email").eq(mEmail).or()
				.toUpper("fname").eq(mFName).and().toUpper("lname").eq(mLName)
				.execute(new TableQueryCallback<User>()
				{

					public void onCompleted(List<User> result, int count, Exception exception,
							ServiceFilterResponse response)
					{
						if (exception == null)
						{
							if (result.isEmpty())
							{
								mProcess.dismiss();
								createAndShowDialog("There are no results matching your search!",
										"No Results Found");
							}
							else
							{

								for (User u : result)
								{
									mAdapter.add(u);
									mProcess.dismiss();
								}

							}
						}
						else
						{
							mProcess.dismiss();
							createAndShowDialog(exception, "Error");
						}
					}
				});

	}

	/*
	 * Method to display exceptions caught by Server
	 */

	private void createAndShowDialog(Exception exception, String title)
	{
		createAndShowDialog(exception.getCause().getMessage(), title);
	}

	/*
	 * Method to build and display Alert Dialog
	 */
	private void createAndShowDialog(String message, String title)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setMessage(message);
		builder.setTitle(title);
		builder.setPositiveButton("OK", null);
		builder.create().show();
	}
}
