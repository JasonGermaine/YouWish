package com.example.youwish;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingActivity extends PreferenceActivity
{
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new SettingFragment()).commit();
	}

	public static class SettingFragment extends PreferenceFragment
	{
		private User user;
		private EditTextPreference mEmailPref, mPasswordPref;
		private String mEmail, mPassword;

		private Matcher m;
		private Pattern emailPattern, passwordPattern;
		private AzureService mAzureService;

		@Override
		public void onCreate(final Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);

			user = ((YouWishApplication) getActivity().getApplication()).getUser();
			mAzureService = ((YouWishApplication) getActivity().getApplication()).getService();
			mAzureService.setClient(getActivity().getApplicationContext());

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.settings);

			mEmailPref = (EditTextPreference) findPreference("email_preference");
			mEmailPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
			{
				@Override
				public boolean onPreferenceChange(Preference preference, Object value)
				{
					// Patterns to detect invalid input or illegal characters
					emailPattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
							+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

					mEmail = value.toString();

					m = emailPattern.matcher(mEmail);

					if (TextUtils.isEmpty(mEmail))
					{
						createAndShowDialog("The email can't be empty.", "Invalid Email");
						return false;
					}
					else if (m.find() == false)
					{
						createAndShowDialog("The email entered is invalid.", "Invalid Email");
						return false;
					}
					else
					{
						if (mEmail != user.getEmail())
						{
							updateEmail();
						}
						return true;
					}

					
				}
			});

			mPasswordPref = (EditTextPreference) findPreference("password_preference");
			mPasswordPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
			{
				@Override
				public boolean onPreferenceChange(Preference preference, Object value)
				{
					passwordPattern = Pattern.compile("[^a-zA-Z0-9]");
					mPassword = value.toString();

					m = passwordPattern.matcher(mPassword);

					if (TextUtils.isEmpty(mPassword))
					{
						createAndShowDialog("The password can't be empty.", "Invalid Password");
						return false;
					}
					else if (mPassword.length() < 6)
					{
						createAndShowDialog("Password must be at least 6 charachters",
								"Invalid Password");
						return false;
					}
					else if (m.find() == true)
					{
						createAndShowDialog("Password can only contain numbers and/or letters",
								"Invalid Password");
						return false;
					}
					else
					{
						updatePassword();
						return true;
					}

				}
			});
		}

		private void updatePassword()
		{
			// Update the user in the database for the new password
			mAzureService.updatePassword(user, new TableOperationCallback<User>()
			{
				public void onCompleted(User entity, Exception exception,
						ServiceFilterResponse response)
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

		private void updateEmail()
		{
			// Update the user in the database for the new password
			mAzureService.updateEmail(user, new TableOperationCallback<User>()
			{
				public void onCompleted(User entity, Exception exception,
						ServiceFilterResponse response)
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
	}
}
