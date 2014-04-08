package com.example.youwish;

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

public class WishAdapter extends ArrayAdapter<Wish>
{

	/**
	 * Adapter context
	 */
	private final Context mContext;

	/**
	 * Adapter View layout
	 */
	private int mLayoutResourceId;

	public WishAdapter(Context context, int resource)
	{
		super(context, resource);

		mContext = context;
		mLayoutResourceId = resource;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{

		View row = convertView;
		final Wish currentWish = getItem(position);

		if (row == null)
		{
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(mLayoutResourceId, parent, false);
		}

		row.setTag(currentWish);
		

        TextView title = (TextView)row.findViewById(R.id.wish_title); // title
        TextView desc = (TextView)row.findViewById(R.id.wish_desc); // artist name
        ImageView image = (ImageView)row.findViewById(R.id.list_image); // thumb image
		
		title.setText(currentWish.getTitle());
		title.setEnabled(true);
		
		if(currentWish.getDesc() != null)
		{
			desc.setText(currentWish.getDesc());
		}
		else
		{
			desc.setText("No Description Available");
		}
		desc.setEnabled(true);
		
		if(currentWish.getImage() != null)
		{
			 byte[] decodedByte = Base64.decode(currentWish.getImage(), 0);
			 Bitmap b = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
			image.setImageBitmap(b);
		}
		else
		{
			Drawable drawable = mContext.getResources().getDrawable(mContext.getResources()
	                .getIdentifier("image_preview", "drawable", mContext.getPackageName()));
			image.setImageDrawable(drawable);

		}
		
		image.setEnabled(true);
		return row;
	}

}
