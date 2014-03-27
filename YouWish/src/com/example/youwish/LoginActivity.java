package com.example.youwish;

import com.microsoft.windowsazure.mobileservices.*;

import java.net.MalformedURLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user
 */
public class LoginActivity extends FragmentActivity
{

	// Create Client
	private MobileServiceClient mClient;
	private MobileServiceTable<User> mUserTable;

	public static Activity login_activity;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private TextView mRegisterScreen;

	// Session Manager Class
	private SessionManager session;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{

		// Session class instance
		session = SessionManager.getSessionManager(getApplicationContext());

		// Connect client to azure
		try
		{
			mClient = new MobileServiceClient(
					"https://youwish.azure-mobile.net/",
					"DLOtCZsychhFqEupVpZqWBQtcgFPnJ95", this);

			// Get the Mobile Service Table instance to use
			mUserTable = mClient.getTable(User.class);
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onCreate(savedInstanceState);

		// Set the view from the login.xml layout
		setContentView(R.layout.login);

		// Creating instance of this activity to destroy from RegisterActivity
		login_activity = this;

		// Set up the login form.

		// Create the text field in which the email is to be entered.
		mEmailView = (EditText) findViewById(R.id.email_field);

		// Create the text field in which the email is to be entered.
		mPasswordView = (EditText) findViewById(R.id.password_field);

		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener()
				{
					@Override
					public boolean onEditorAction( TextView textView, int id,
							KeyEvent keyEvent )
					{
						if (id == R.id.login || id == EditorInfo.IME_NULL)
						{
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		// Create the form
		mLoginFormView = findViewById(R.id.login_form);

		// Create the status view
		mLoginStatusView = findViewById(R.id.login_status);

		// Create the status message
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		// Add a click listener to perform action when button is clicked
		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener()
				{
					@Override
					public void onClick( View view )
					{
						if (CloudManager.verifyConnection(getApplicationContext()) == true)
						{

							attemptLogin();
						}
						else
						{
							Toast.makeText(getApplicationContext(),
									"No Internet Connectivty", 1).show();
						}
					}
				});

		// Creating the link to the register screen
		mRegisterScreen = (TextView) findViewById(R.id.link_to_register);

		// Listening to register new account link
		mRegisterScreen.setOnClickListener(new View.OnClickListener()
		{
			public void onClick( View v )
			{
				// Switching to Register screen
				Intent i = new Intent(getApplicationContext(),
						RegisterActivity.class);
				startActivity(i);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Attempts to sign in the account specified by the login form. If there are
	 * form errors (invalid email, missing fields, etc.), the errors are
	 * presented and no actual login attempt is made.
	 */
	public void attemptLogin()
	{

		// Patterns to detect invalid input or illegal characters
		Pattern emailPattern = Pattern
				.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
						+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
		Pattern passwordPattern = Pattern.compile("[^a-zA-Z0-9]");
		Matcher m;

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

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

		if (cancel)
		{
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		}
		else
		{
			// Encrypt Password
			mPassword = encrypt(mPassword);
			
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);

			// Attempt to query using the email input using a callback
			mUserTable.where().field("email").eq(mEmail)
					.execute(new TableQueryCallback<User>()
					{

						public void onCompleted( List<User> result, int count,
								Exception exception,
								ServiceFilterResponse response )
						{
							if (exception == null)
							{

								for (User u : result)
								{
									showProgress(false);
									// Compare retrieved password against
									// password input
									if (u.getPassword().equals(mPassword))
									{

										session.createLoginSession(mEmail);
										// Finish this activity
										finish();

										// Start the main activity
										Intent i = new Intent(
												getApplicationContext(),
												MainActivity.class);
										startActivity(i);
									}
									else
									{
										mPasswordView
												.setError(getString(R.string.error_incorrect_password));
										mPasswordView.requestFocus();
									}
								}
							}
							else
							{
								showProgress(false);
							}
						}
					});
		}
	}

	private String encrypt( String password )
	{
		MessageDigest md;
		try
		{
			md = MessageDigest.getInstance("SHA-256");
	        md.update(password.getBytes());
	        
	        byte byteData[] = md.digest();
	 
	        //convert the byte to hex format method 1
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < byteData.length; i++) {
	         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	        }
	        password = sb.toString();
		}
		catch (NoSuchAlgorithmException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return password;
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress( final boolean show )
	{
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
		{
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter()
					{
						@Override
						public void onAnimationEnd( Animator animation )
						{
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter()
					{
						@Override
						public void onAnimationEnd( Animator animation )
						{
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		}
		else
		{
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

}
