package com.example.youwish.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.youwish.R;
import com.example.youwish.db.AzureService;
import com.example.youwish.model.User;
import com.example.youwish.util.YouWishApplication;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;

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
		private EditTextPreference mPasswordPref;
		private String mPassword;

		private Matcher m;
		private Pattern passwordPattern;
		private AzureService mAzureService;
		private Preference twitterPref;

		@Override
		public void onCreate(final Bundle savedInstanceState)
		{

			super.onCreate(savedInstanceState);

			user = ((YouWishApplication) getActivity().getApplication()).getUser();
			mAzureService = ((YouWishApplication) getActivity().getApplication()).getService();
			mAzureService.setClient(getActivity().getApplicationContext());

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.settings);

			mPasswordPref = (EditTextPreference) findPreference("password_preference");
			EditText mPasswordEdit = ((EditTextPreference) findPreference("password_preference"))
					.getEditText();
			mPasswordEdit.setFilters(new InputFilter[]
			{ new InputFilter.LengthFilter(50) });

			mPasswordEdit.setText("");

			twitterPref = (Preference) findPreference("twitter_contact");
			twitterPref.setOnPreferenceClickListener(new OnPreferenceClickListener()
			{
				public boolean onPreferenceClick(Preference preference)
				{
					Intent intent = null;
					try {
					    // get the Twitter app if possible
					    getActivity().getPackageManager().getPackageInfo("com.twitter.android", 0);
					    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=2459820728"));
					    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					} catch (Exception e) {
					    // no Twitter app, revert to browser
					    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/2459820728"));
					}
					getActivity().startActivity(intent);
					return true;
				}
			});

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
					else if (((YouWishApplication) getActivity().getApplication())
							.verifyConnection(getActivity()) == false)
					{
						createAndShowDialog("No Internet Connection Available", "Connection Error");
						return false;
					}
					else
					{
						User u = user;
						u.setPassword(mPassword);
						updatePassword(u);
						return true;
					}

				}
			});
		}

		/*
		 * Handles UI and on click listeners
		 */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState)
		{
			View view = super.onCreateView(inflater, container, savedInstanceState);

			view.setBackgroundColor(getResources().getColor(android.R.color.background_light));

			return view;
		}

		/*
		 * Updates user password through Azure
		 */
		private void updatePassword(User u)
		{
			// Update the user in the database for the new password
			mAzureService.updatePassword(u, new TableOperationCallback<User>()
			{
				@Override
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
