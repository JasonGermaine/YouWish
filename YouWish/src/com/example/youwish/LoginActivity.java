package com.example.youwish;

import com.microsoft.windowsazure.mobileservices.*;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
	private AzureService mAzureService;

	// private AzureService mAzureService;
	private ConnectionManager mConnection;

	// Create Client
	private MobileServiceClient mClient;
	private MobileServiceTable<User> mUserTable;
	List<Pair<String, String>> queryParams = new ArrayList<Pair<String, String>>();

	public static Activity login_activity;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	private User recoveryUser;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private TextView mRegisterScreen;

	// Session Manager Class
	private SessionManager session;

	private ProgressDialog mProcess;

	/*
	 * Defines the operations to take place when the Activity is created. This will initalize UI
	 * components and add appropriate listeners
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		mAzureService = ((YouWishApplication) getApplication()).getService();
		mAzureService.setClient(getApplicationContext());

		mProcess = new ProgressDialog(this);

		// Session class instance
		session = SessionManager.getSessionManager(getApplicationContext());

		mConnection = ConnectionManager.getConnectionManager();

		super.onCreate(savedInstanceState);

		// Set the view from the login.xml layout
		setContentView(R.layout.login);

		// Creating instance of this activity to destroy from RegisterActivity
		login_activity = this;

		// Set up the login form.

		// Create the text field in which the email is to be entered.
		mEmailView = (EditText) findViewById(R.id.email_field);
		mEmailView.setFilters(new InputFilter[]
		{ new InputFilter.LengthFilter(50) });

		// Create the text field in which the email is to be entered.
		mPasswordView = (EditText) findViewById(R.id.password_field);
		mPasswordView.setFilters(new InputFilter[]
		{ new InputFilter.LengthFilter(50) });

		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
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
		findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener()
		{

			// Check for internet connectivity
			@Override
			public void onClick(View view)
			{
				if (mConnection.verifyConnection(getApplicationContext()) == true)
				{
					// Internet access - attempt login
					attemptLogin();
				}
				else
				{
					// No connectivity - display error message
					Toast.makeText(getApplicationContext(), "No Internet Connectivty",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		// Creating the link to the register screen
		mRegisterScreen = (TextView) findViewById(R.id.link_to_register);

		// Listening to register new account link
		mRegisterScreen.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				// Switching to Register screen
				Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
				startActivity(i);
			}
		});

	}

	/*
	 * Creates the menu buttons
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/*
	 * Defines actions to perform when menu items are selected
	 */
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Check if add button is pressed
		if (item.getItemId() == R.id.action_forgot_password)
		{
			if (mConnection.verifyConnection(getApplicationContext()) == true)
			{
				// Internet access - attempt recovery
				recoveryEmail();
			}
			else
			{
				// No connectivity - display error message
				Toast.makeText(getApplicationContext(), "No Internet Connectivty",
						Toast.LENGTH_SHORT).show();
			}
		}

		return true;
	}

	/*
	 * Creates and shows an Alert Dialog view. Allows user to input email and passes email to
	 * passwordRecovery() method
	 */
	private void recoveryEmail()
	{

		LayoutInflater li = LayoutInflater.from(this);

		// Get the view for password recovery
		View promptsView = li.inflate(R.layout.password_recovery, null);

		// Build alert dialog and set the view to the password recovery view
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(promptsView);

		// Initialize EditText to capture user input
		final EditText userInput = (EditText) promptsView.findViewById(R.id.email_recovery);

		// Set buttons for the alert dialog
		alertDialogBuilder.setCancelable(false)
				.setPositiveButton("Reset Password", new DialogInterface.OnClickListener()
				{
					// If reset password is clicked
					public void onClick(DialogInterface dialog, int id)
					{
						mProcess.setMessage("Verifying User");
						mProcess.show();
						// recover password for inputted email
						recoverPassword(userInput.getText().toString());
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
				{
					// If cancel is clicked
					public void onClick(DialogInterface dialog, int id)
					{
						// Cancel dialog
						dialog.cancel();
					}
				});

		// Create and show dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();

	}

	/*
	 * Attempts to sign in the account specified by the login form. If there are form errors
	 * (invalid email, missing fields, etc.), the errors are presented and no actual login attempt
	 * is made.
	 */
	public void attemptLogin()
	{

		// Patterns to detect invalid input or illegal characters
		Pattern emailPattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
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
			mPasswordView.setText("");
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
			Toast.makeText(getApplicationContext(), mPassword, Toast.LENGTH_SHORT).show();

			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);

			// Attempt to query using the email input using a callback

			mAzureService.lookup(mEmail, new TableOperationCallback<User>()
			{
				@Override
				public void onCompleted(User entity, Exception exception,
						ServiceFilterResponse response)
				{
					showProgress(false);
					if (exception == null)
					{

						if (entity == null)
						{
							createAndShowDialog(
									"The is no user currently registered with that email!",
									"Invalid Email");
							mPasswordView.setText("");
						}
						else
						{
							// Compare retrieved password against
							// password input
							if (entity.getPassword().equals(mPassword))
							{
								((YouWishApplication) getApplication()).setUser(entity);
								
								session.createLoginSession(mEmail);
								// Finish this activity
								finish();

								// Start the main activity
								Intent i = new Intent(getApplicationContext(), MainActivity.class);
								startActivity(i);
							}
							else
							{
								mPasswordView
										.setError(getString(R.string.error_incorrect_password));
								mPasswordView.requestFocus();
								mPasswordView.setText("");
							}
						}
					}
					else
					{

						createAndShowDialog(
								"The is no user currently registered with that email!",
								"Invalid Email");
						mPasswordView.setText("");
					}

				}
			});
		}
	}

	/*
	 * Encrypt the input password using SHA 256 to be compared to User's password
	 */
	private String encrypt(String password)
	{
		MessageDigest md;
		try
		{
			md = MessageDigest.getInstance("SHA-256");
			md.update(password.getBytes());

			byte byteData[] = md.digest();

			// convert the byte to hex format method 1
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++)
			{
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			password = sb.toString();
		} catch (NoSuchAlgorithmException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return password;
	}

	/*
	 * Thread to start password recovery
	 */
	Runnable r = new Runnable()
	{

		@Override
		public void run()
		{
			try
			{
				// Generate new password
				recoveryUser.generateRecovery();

				// Send recovery email
				// Update User to contain new hashed password
				recoveryUser = RecoveryManager.sendMail(recoveryUser);
				updateUser();
			} catch (AddressException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MessagingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	private void updateUser()
	{
		// Update the user in the database for the new password
		mAzureService.updatePassword(recoveryUser, new TableOperationCallback<User>()
		{
			public void onCompleted(User entity, Exception exception, ServiceFilterResponse response)
			{
				if (exception == null)
				{
					createAndShowDialog("A new password has been sent to your email",
							"Recovery Success");
				}
				else
				{
					createAndShowDialog(exception, "Error");

				}
			}
		});

	}

	/*
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
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter()
					{
						@Override
						public void onAnimationEnd(Animator animation)
						{
							mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter()
					{
						@Override
						public void onAnimationEnd(Animator animation)
						{
							mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(message);
		builder.setTitle(title);
		builder.setPositiveButton("OK", null);
		builder.create().show();
	}

	/*
	 * Attempts to retrieve a User that maps to inputted email
	 */
	private void recoverPassword(String email)
	{
		// Attempt to query using the email input using a callback
		mAzureService.lookup(email, new TableOperationCallback<User>()
		{

			@Override
			public void onCompleted(User entity, Exception exception, ServiceFilterResponse response)
			{

				if (exception == null)
				{

					if (entity == null)
					{
						mProcess.dismiss();
						createAndShowDialog("The is no user currently registered with that email!",
								"Invalid Email");
						mPasswordView.setText("");
					}
					else
					{

						mProcess.dismiss();
						recoveryUser = entity;
						Thread t = new Thread(r);
						t.start();
					}
				}
				else
				{

					createAndShowDialog(
							"The is no user currently registered with that email!",
							"Invalid Email");
					mPasswordView.setText("");
					mProcess.dismiss();

				}

			}
		});
	}
}
