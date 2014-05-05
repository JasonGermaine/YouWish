package com.example.youwish.fragment;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youwish.R;
import com.example.youwish.adapters.UserAdapter;
import com.example.youwish.db.AzureService;
import com.example.youwish.model.User;
import com.example.youwish.util.YouWishApplication;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

public class SearchUserFragment extends ListFragment
{

	private AzureService mAzureService;
	// UI Components
	private ListView userList;
	private UserAdapter mAdapter;
	private EditText mSearchView;

	
	private boolean mLocalUser;
	
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

		mAzureService = ((YouWishApplication) getActivity().getApplication()).getService();
		mAzureService.setClient(getActivity().getApplicationContext());

		// Inflate view for this fragment
		View rootView = inflater.inflate(R.layout.fragment_search_user, container, false);

		// EditText for user to input search criteria
		mSearchView = (EditText) rootView.findViewById(R.id.search_box);
		mSearchView.setFilters(new InputFilter[]
		{ new InputFilter.LengthFilter(100) });

		// Create an adapter to bind the items with the view
		mAdapter = new UserAdapter(getActivity(), R.layout.row_user, SearchUserFragment.this);
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
					if (((YouWishApplication) getActivity().getApplication())
							.verifyConnection(getActivity()) == false)
					{
						Toast.makeText(getActivity(), "No Internet Connectivty", Toast.LENGTH_SHORT)
								.show();
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
									Log.i("LNAME", mLName);
									Log.i("FNAME", mFName);
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

									searchFullName();
								}
							}
							else
							{
								// One word query, at first assume email
								mEmail = mQuery.toLowerCase();

								// Test for valid Email
								m = emailPattern.matcher(mEmail);
								if (m.find() == false)
								{
									mFNameOnly = mQuery;
									m = namePattern.matcher(mFNameOnly);
									if (m.find() == true)
									{
										mSearchView
												.setError(getString(R.string.error_invalid_search));
										focusView = mSearchView;
										focusView.requestFocus();
										mProcess.dismiss();
										return false;
									}
									else
									{
										searchFirstName();
									}

								}
								else
								{
									searchEmail();
								}

							}
						}

						InputMethodManager imm = (InputMethodManager) getActivity()
								.getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus()
								.getWindowToken(), 0);
						mSearchView.clearFocus();
						return true;
					}
				}
				mProcess.dismiss();
				return false;
			}

			private void searchFirstName()
			{
				mAzureService.searchFirstName(mFNameOnly, new TableQueryCallback<User>()
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
									final User user = u;
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

			private void searchFullName()
			{
				mAzureService.searchFullName(mFName, mLName, new TableQueryCallback<User>()
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

			private void searchEmail()
			{
				// Attempt to query using the email input using a callback
				mAzureService.lookup(mEmail, new TableOperationCallback<User>()
				{

					@Override
					public void onCompleted(User entity, Exception exception,
							ServiceFilterResponse response)
					{

						if (exception == null)
						{

							if (entity == null)
							{
								mProcess.dismiss();
								createAndShowDialog(
										"The is no user currently registered with that email!",
										"Invalid Email");
							}
							else
							{
								mProcess.dismiss();
								mAdapter.add(entity);
							}
						}
						else
						{

							createAndShowDialog(
									"The is no user currently registered with that email!",
									"Invalid Email");
							mProcess.dismiss();

						}

					}
				});

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

	/*
	 * Making a transition to a user's page
	 */
	public void toProfile(boolean local)
	{
		mLocalUser = local;
		Log.i("LIST", "CLICKED");
		// update the main content by replacing fragments
		Fragment fragment = new ProfileFragment();
		Bundle args = new Bundle();
		args.putBoolean("localuser", mLocalUser);
		fragment.setArguments(args);

		final FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.content_frame, fragment);
		ft.commit();

	}

}
