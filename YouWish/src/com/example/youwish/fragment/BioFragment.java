package com.example.youwish.fragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youwish.R;
import com.example.youwish.db.AzureService;
import com.example.youwish.model.User;
import com.example.youwish.util.YouWishApplication;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;

public class BioFragment extends Fragment
{
	private TextView mName, mBioTitle, mBio;
	private EditText mNameEdit, mBioEdit;
	private Button mEdit, mSave, mCancel;
	private ProgressDialog mProcess;

	private MobileServiceTable<User> mUserTable;

	private AzureService mAzureService;

	private String mFullName;
	private String mBioUpdate;
	private String mFName;
	private String mLName;
	private User user;
	private boolean mLocalUser;

	public BioFragment()
	{

	}

	/*
	 * Initialize all UI events and item click listeners.
	 * Retrieves an argument to determine if the user is the logged in user
	 * or if the logged on user is viewing another profile.
	 * Displays appropriate UI components based on profile type
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		View rootView;
		if (((YouWishApplication) getActivity().getApplication()).verifyConnection(getActivity()) == false)
		{
			rootView = inflater.inflate(R.layout.connection_failure, container, false);

			RelativeLayout layout = (RelativeLayout) rootView.findViewById(R.id.connection_error);
			layout.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					refresh();
				}
			});
		}
		else
		{
			mAzureService = ((YouWishApplication) getActivity().getApplication()).getService();
			mAzureService.setClient(getActivity().getApplicationContext());

			mLocalUser = getArguments().getBoolean("LocalUser");
			if (mLocalUser == true)
			{
				user = ((YouWishApplication) getActivity().getApplication()).getUser();
			}
			else
			{
				user = ((YouWishApplication) getActivity().getApplication()).getGuest();
			}

			rootView = inflater.inflate(R.layout.fragment_bio, container, false);

			mName = (TextView) rootView.findViewById(R.id.bio_name);

			mNameEdit = (EditText) rootView.findViewById(R.id.bio_name_edit);
			mNameEdit.setFilters(new InputFilter[]
			{ new InputFilter.LengthFilter(101) });

			mBioTitle = (TextView) rootView.findViewById(R.id.bio_title);
			mBio = (TextView) rootView.findViewById(R.id.bio);

			mBioEdit = (EditText) rootView.findViewById(R.id.bio_edit);
			mBioEdit.setFilters(new InputFilter[]
			{ new InputFilter.LengthFilter(200) });

			
			// Buttons
			mEdit = (Button) rootView.findViewById(R.id.image_edit);
			if(mLocalUser == false)
			{
				mEdit.setVisibility(View.GONE);
			}

			mSave = (Button) rootView.findViewById(R.id.Button02);

			mCancel = (Button) rootView.findViewById(R.id.Button03);

			mName.setText(user.getFName() + " " + user.getLName());
			mNameEdit.setText(user.getFName() + " " + user.getLName());

			if (user.getBio() != null)
			{
				mBio.setText(user.getBio());
				mBioEdit.setText(user.getBio());
			}

			mNameEdit.setOnTouchListener(new View.OnTouchListener()
			{

				@Override
				public boolean onTouch(View v, MotionEvent event)
				{
					// TODO Auto-generated method stub
					mNameEdit.requestFocusFromTouch();
					return false;
				}
			});

			mBioEdit.setOnTouchListener(new View.OnTouchListener()
			{

				@Override
				public boolean onTouch(View v, MotionEvent event)
				{
					mBioEdit.requestFocusFromTouch();
					return false;
				}
			});

			// Handle when edit button is clicked
			mEdit.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					// setFields();
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
				public void onClick(View v)
				{
					if (((YouWishApplication) getActivity().getApplication())
							.verifyConnection(getActivity()) == false)
					{

						// Set Visibilities
						mEdit.setVisibility(View.VISIBLE);
						mSave.setVisibility(View.GONE);
						mCancel.setVisibility(View.GONE);
						mName.setVisibility(View.GONE);
						mNameEdit.setVisibility(View.GONE);
						mBio.setVisibility(View.VISIBLE);
						mBioEdit.setVisibility(View.GONE);

						// No connectivity - display error message
						Toast.makeText(getActivity().getApplicationContext(),
								"No Internet Connectivty", Toast.LENGTH_SHORT).show();
					}
					else
					{
						boolean cancel = false;
						mNameEdit.setError(null);
						mBioEdit.setError(null);
						View focusView = null;

						Matcher m;
						// Initialize the patterns to validate fields
						Pattern regex = Pattern.compile("[$&+:;=?@#|]");
						Pattern namePattern = Pattern.compile("[^a-z]", Pattern.CASE_INSENSITIVE);

						mFullName = mNameEdit.getText().toString();
						if (TextUtils.isEmpty(mFullName))
						{
							mNameEdit.setError(getString(R.string.error_field_required));
							focusView = mNameEdit;
							focusView.requestFocus();
							cancel = true;
						}
						else
						{
							mFName = mFullName.substring(0, mFullName.indexOf(' '));
							mLName = mFullName.substring(mFullName.indexOf(' ') + 1);

							m = namePattern.matcher(mFName);
							if (TextUtils.isEmpty(mFName) || TextUtils.isEmpty(mLName))
							{
								mNameEdit.setError(getString(R.string.error_field_required));
								focusView = mNameEdit;
								focusView.requestFocus();
								cancel = true;
							}
							else if (m.find() == true)
							{
								mNameEdit.setError(getString(R.string.error_invalid_format));
								focusView = mNameEdit;
								focusView.requestFocus();
								cancel = true;
							}
							else
							{
								m = namePattern.matcher(mLName);
								if (m.find() == true)
								{
									mNameEdit.setError(getString(R.string.error_invalid_format));
									focusView = mNameEdit;
									focusView.requestFocus();
									cancel = true;
								}
							}

						}

						mBioUpdate = mBioEdit.getText().toString();
						mBioUpdate = mBioUpdate.replace("\"", "");
						m = regex.matcher(mBioUpdate);

						// Test if field contains invalid characters
						if (m.find())
						{
							mBioEdit.setError(getString(R.string.error_invalid_format));
							focusView = mBioEdit;
							cancel = true;
						}

						if (!cancel)
						{
							if (user.getBio() == null || !user.getBio().equals(mBioUpdate)
									|| !user.getFName().equals(mFName)
									|| !user.getLName().equals(mLName))
							{
								user.setBio(mBioUpdate);
								user.setFName(mFName);
								user.setLName(mLName);
								updateBio();
							}

							mBio.setText(user.getBio());

							// Set Visibilities
							mEdit.setVisibility(View.VISIBLE);
							mSave.setVisibility(View.GONE);
							mCancel.setVisibility(View.GONE);
							mName.setVisibility(View.GONE);
							mNameEdit.setVisibility(View.GONE);
							mBio.setVisibility(View.VISIBLE);
							mBioEdit.setVisibility(View.GONE);
						}
					}

				}
			});
			// Handle when Cancel button is clicked
			mCancel.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
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
		}

		return rootView;
	}

	/*
	 * Method to persist updated bio to DB
	 */
	private void updateBio()
	{
		mAzureService.updateBio(user, new TableOperationCallback<User>()
		{
			public void onCompleted(User entity, Exception exception, ServiceFilterResponse response)
			{
				if (exception == null)
				{
					((YouWishApplication) getActivity().getApplication()).setUser(entity);
				}
				else
				{
					createAndShowDialog(exception, "Error");
				}
			}
		});
	}

	private void createAndShowDialog(Exception exception, String title)
	{
		createAndShowDialog(exception.getCause().getMessage(), title);
	}

	private void createAndShowDialog(String message, String title)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setMessage(message);
		builder.setTitle(title);
		builder.setPositiveButton("OK", null);
		builder.create().show();
	}

	private void refresh()
	{
		ProfileFragment.frag.refresh(mLocalUser);
	}
}
