package com.example.youwish.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.youwish.R;
import com.example.youwish.fragment.SearchUserFragment;
import com.example.youwish.model.User;
import com.example.youwish.util.YouWishApplication;

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
	
	private final SearchUserFragment fragment;

	private User u;
	
	public UserAdapter(Context context, int resource, SearchUserFragment frag)
	{
		super(context, resource);

		mContext = context;
		mLayoutResourceId = resource;
		fragment = frag;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{

		View row = convertView;
		final User currentUser = getItem(position);

		
		u = ((YouWishApplication) mContext
				.getApplicationContext()).getUser();
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
		final ImageView mProfilePic = (ImageView) row.findViewById(R.id.list_image);
		final ImageView forward = (ImageView) row.findViewById(R.id.action_forward);
		
		
		mName.setText(currentUser.getFName() + " "+ currentUser.getLName());
		mName.setEnabled(true);
		mEmail.setText(currentUser.getEmail());
		mEmail.setEnabled(true);
		
		if(currentUser.getProfilePic() != null)
		{
			mProfilePic.setImageBitmap(decodeBitmap(currentUser.getProfilePic(), mProfilePic.getWidth(), mProfilePic.getHeight()));
		}
		else
		{
			Drawable drawable = mContext.getResources().getDrawable(
					mContext.getResources().getIdentifier("image_preview", "drawable",
							mContext.getPackageName()));
			mProfilePic.setImageDrawable(drawable);

		}
		mProfilePic.setEnabled(true);
		
		
		forward.setOnClickListener( new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				((YouWishApplication) mContext
						.getApplicationContext())
						.setGuest(currentUser);
				
				if(currentUser.getEmail().equals(u.getEmail()))
				{
					fragment.toProfile(true);
				}
				else
				{
					fragment.toProfile(false);
				}
				
			}
		});
		return row;
	}
	
	
	public Bitmap decodeBitmap(String image, int width, int height)
	{
		byte[] decodedByte = Base64.decode(image, 0);
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = calculateSize(options, width, height);
		Bitmap bmp = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length, options);
		return bmp;
	}

	public int calculateSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
	{

		final int height = options.outHeight;
		final int width = options.outWidth;
		int size = 1;

		if (height > reqHeight || width > reqWidth)
		{
			if (width > height)
			{
				size = Math.round((float) height / (float) reqHeight);
			}
			else
			{
				size = Math.round((float) width / (float) reqWidth);
			}
		}
		return size;
	}

}
