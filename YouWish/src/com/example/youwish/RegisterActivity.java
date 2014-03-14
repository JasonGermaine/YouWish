package com.example.youwish;

import com.microsoft.windowsazure.mobileservices.*;

import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;

public class RegisterActivity extends FragmentActivity
{

	// Create Client
	private MobileServiceClient mClient;
	private MobileServiceTable<User> mUserTable;

	// Create User to register
	private User user;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;
	private String mFName;
	private String mLName;
	private String mDOB;

	// UI references.
	private TextView mLoginScreen;
	private EditText mFNameView, mLNameView, mEmailView, mPasswordView;
	private EditText mDOBView;
	private View mRegisterStatusView;
	private View mRegisterFormView;
	private TextView mRegisterStatusMessageView;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Connect client to azure
		try
		{
			mClient = new MobileServiceClient(
					"https://youwish.azure-mobile.net/",
					"DLOtCZsychhFqEupVpZqWBQtcgFPnJ95", this);
			mUserTable = mClient.getTable(User.class);
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Set View to register.xml
		setContentView(R.layout.register);

		// UI Elements
		mLoginScreen = (TextView) findViewById(R.id.link_to_login);
		mFNameView = (EditText) findViewById(R.id.reg_firstname);
		mLNameView = (EditText) findViewById(R.id.reg_lastname);
		mEmailView = (EditText) findViewById(R.id.email_field);
		mPasswordView = (EditText) findViewById(R.id.password_field);
		mDOBView = (EditText) findViewById(R.id.dob_field);

		mRegisterFormView = findViewById(R.id.register_form);

		// Create the status view
		mRegisterStatusView = findViewById(R.id.login_status);

		// Create the status message
		mRegisterStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		// Listening to Login Screen link
		mLoginScreen.setOnClickListener(new View.OnClickListener()
		{

			public void onClick(View arg0)
			{
				// Closing registration screen
				// Switching to Login Screen/closing register screen
				finish();
			}
		});

		// Add a click listener to perform action when button is clicked
		findViewById(R.id.btnRegister).setOnClickListener(
				new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						// Attempt to register
						attemptRegister();
					}
				});

	}

	// Method to show the date picker
	public void showDatePickerDialog(View v)
	{
		DatePickerFragment newFragment = new DatePickerFragment(this);
		newFragment.show(getSupportFragmentManager(), "datePicker");

	}

	/**
	 * Attempts register the account specified by the register form. If there
	 * are form errors (invalid email, missing fields, etc.), the errors are
	 * presented and no actual register attempt is made.
	 */
	public void attemptRegister()
	{

		// Patterns to detect invalid input or illegal characters
		Pattern namePattern = Pattern.compile("[^a-z]", Pattern.CASE_INSENSITIVE);
		Pattern passwordPattern = Pattern.compile("[^a-zA-Z0-9]");
		Pattern emailPattern = Pattern
				.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
						+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
		Matcher m;

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);
		mFNameView.setError(null);
		mLNameView.setError(null);
		mDOBView.setError(null);

		// Store values at the time of the register attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		mFName = mFNameView.getText().toString();
		mLName = mLNameView.getText().toString();
		mDOB = mDOBView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.

		m = passwordPattern.matcher(mPassword);

		if (TextUtils.isEmpty(mPassword))
		{
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}
		else if (mPassword.length() < 6)
		{
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}
		else if (m.find() == true)
		{
			mPasswordView.setError(getString(R.string.error_invalid_format));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.

		m = emailPattern.matcher(mEmail);

		if (TextUtils.isEmpty(mEmail))
		{
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		}
		else if (m.find() == false)
		{
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		// Check for a valid first name.

		m = namePattern.matcher(mFName);

		if (TextUtils.isEmpty(mFName))
		{
			mFNameView.setError(getString(R.string.error_field_required));
			focusView = mFNameView;
			cancel = true;
		}
		else if (m.find() == true)
		{
			mFNameView.setError(getString(R.string.error_invalid_format));
			focusView = mFNameView;
			cancel = true;
		}

		// Check for a valid last name.

		m = namePattern.matcher(mLName);

		if (TextUtils.isEmpty(mLName))
		{
			mLNameView.setError(getString(R.string.error_field_required));
			focusView = mLNameView;
			cancel = true;
		}
		else if (m.find() == true)
		{
			mLNameView.setError(getString(R.string.error_invalid_format));
			focusView = mLNameView;
			cancel = true;
		}
		// Check for a valid date.
		if (TextUtils.isEmpty(mDOB))
		{
			mDOBView.setError(getString(R.string.error_field_required));
			focusView = mDOBView;
			cancel = true;
		}

		if (cancel)
		{
			// There was an error; don't attempt registration and focus the
			// first
			// form field with an error.
			focusView.requestFocus();
		}
		else
		{
			// Create User to register
			user = new User(mEmail, mPassword, mFName, mLName, mDOB);
			
			// Show a progress spinner, and kick off a background task to
			// perform the user register attempt.
			mRegisterStatusMessageView
					.setText(R.string.login_progress_signing_in);
			showProgress(true);
			
			// Attempt to insert using a callback
			mUserTable.insert(user, new TableOperationCallback<User>()
			{
				public void onCompleted(User entity, Exception exception,
						ServiceFilterResponse response)
				{
					showProgress(false);
					if (exception == null)
					{
						// Finish the login activity
						LoginActivity.login_activity.finish();

						// Finish this activity
						finish();

						// Start the Main Activity
						Intent i = new Intent(getApplicationContext(),
								MainActivity.class);
						startActivity(i);
					}
					else
					{

					}
				}
			});

		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show)
	{
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
		{
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mRegisterStatusView.setVisibility(View.VISIBLE);
			mRegisterStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter()
					{
						@Override
						public void onAnimationEnd(Animator animation)
						{
							mRegisterStatusView
									.setVisibility(show ? View.VISIBLE
											: View.GONE);
						}
					});

			mRegisterFormView.setVisibility(View.VISIBLE);
			mRegisterFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter()
					{
						@Override
						public void onAnimationEnd(Animator animation)
						{
							mRegisterFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		}
		else
		{
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mRegisterStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
}