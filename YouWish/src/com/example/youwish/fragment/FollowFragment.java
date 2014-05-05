package com.example.youwish.fragment;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.youwish.R;
import com.example.youwish.db.AzureService;
import com.example.youwish.model.User;
import com.example.youwish.util.YouWishApplication;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;

public class FollowFragment extends Fragment
{
	public FollowFragment()
	{

	}

	private ListView userList;
	private User user;
	private ArrayList<String> users;
	private boolean mLocalUser;
	private User guest;
	private AzureService mAzureService;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView;

		// If there is no connection
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
			rootView = inflater.inflate(R.layout.fragment_follow, container, false);

			mAzureService = ((YouWishApplication) getActivity().getApplication()).getService();
			mAzureService.setClient(getActivity().getApplicationContext());

			mLocalUser = getArguments().getBoolean("LocalUser");

			if (mLocalUser)
			{
				user = ((YouWishApplication) getActivity().getApplication()).getUser();
			}
			else
			{
				user = ((YouWishApplication) getActivity().getApplication()).getGuest();
			}

			userList = (ListView) rootView.findViewById(R.id.list);
			users = user.getFollowing();

			if (users != null)
			{

				ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
						android.R.layout.simple_list_item_1, users);

				userList.setAdapter(arrayAdapter);
				userList.setClickable(true);
				userList.setOnItemClickListener(new AdapterView.OnItemClickListener()
				{

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
					{
						if (((YouWishApplication) getActivity().getApplication())
								.verifyConnection(getActivity()) == false)
						{
							// No connectivity - display error message
							Toast.makeText(getActivity().getApplicationContext(),
									"No Internet Connectivty", Toast.LENGTH_SHORT).show();
						}
						else
						{

							final String mEmail = (String) userList.getItemAtPosition(position);

							final ProgressDialog ringProgressDialog = ProgressDialog.show(
									getActivity(), "Please wait ...", "Profile Loading ...", true);

							ringProgressDialog.setCancelable(true);

							mAzureService.lookup(mEmail, new TableOperationCallback<User>()
							{
								@Override
								public void onCompleted(User entity, Exception exception,
										ServiceFilterResponse response)
								{
									ringProgressDialog.dismiss();
									if (exception == null)
									{
										guest = entity;
										((YouWishApplication) getActivity().getApplication())
												.setGuest(guest);
										ProfileFragment.frag.refresh(false);
									}
									else
									{
										createAndShowDialog(exception, "Error");
									}

								}
							});
						}

					}
				});
			}
		}

		return rootView;
	}

	/*
	 * Calls method from Profile to refresh page
	 */
	private void refresh()
	{
		ProfileFragment.frag.refresh(mLocalUser);
	}

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
