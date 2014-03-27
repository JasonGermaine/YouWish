package com.example.youwish;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class UserAdapter extends ArrayAdapter<User>
{

	/**
	 * Adapter context
	 */
	private final Context mContext;

	/**
	 * Adapter View layout
	 */
	private int mLayoutResourceId;

	public UserAdapter(Context context, int resource)
	{
		super(context, resource);

		mContext = context;
		mLayoutResourceId = resource;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{

		View row = convertView;
		final User currentUser = getItem(position);

		if (row == null)
		{
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(mLayoutResourceId, parent, false);
		}

		row.setTag(currentUser);
		final TextView mName = (TextView) row
				.findViewById(R.id.list_user_name);
		final TextView mEmail = (TextView) row
				.findViewById(R.id.list_user_email);
		mName.setText(currentUser.getFName() + " "+ currentUser.getLName());
		mName.setEnabled(true);
		mEmail.setText(currentUser.getEmail());
		mEmail.setEnabled(true);
		return row;
	}

}
