package com.example.youwish;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
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
	public View getView(int position, View convertView, ViewGroup parent)
	{

		View row = convertView;
		final Wish currentWish = getItem(position);

		if (row == null)
		{
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(mLayoutResourceId, parent, false);
		}
			
		
		row.setTag(currentWish);

		TextView title = (TextView) row.findViewById(R.id.wish_title); // title
		TextView desc = (TextView) row.findViewById(R.id.wish_desc); // artist name
		ImageView image = (ImageView) row.findViewById(R.id.list_image); // thumb image
		TextView upload = (TextView) row.findViewById(R.id.wish_upload);
		
		ImageView forward = (ImageView) row.findViewById(R.id.action_forward);
		
		forward.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				createDialog(currentWish);
			}
		});

		title.setText(currentWish.getTitle().toUpperCase());
		title.setTypeface(null, Typeface.BOLD);
		title.setEnabled(true);

		if (currentWish.getDesc() != null)
		{
			desc.setText(currentWish.getDesc());
		}
		else
		{
			desc.setText("No Description Available");
		}
		desc.setTypeface(null, Typeface.ITALIC);
		desc.setEnabled(true);

		if (currentWish.getImage() != null)
		{
			image.setImageBitmap(decodeBitmap(currentWish.getImage(), image.getWidth(), image.getHeight()));
		}
		else
		{
			Drawable drawable = mContext.getResources().getDrawable(
					mContext.getResources().getIdentifier("image_preview", "drawable",
							mContext.getPackageName()));
			image.setImageDrawable(drawable);

		}

		image.setEnabled(true);

		upload.setText(currentWish.getUploadedTime());
		return row;
	}

	public Bitmap decodeBitmap(String image, int reqWidth, int reqHeight)
	{
		byte[] decodedByte = Base64.decode(image, 0);
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length, options);

		options.inSampleSize = calculateSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
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
	
	
	protected void createDialog(Wish w)
	{
		
		// custom dialog
		final Dialog dialog = new Dialog((Activity) mContext);
		View layout = ((Activity) mContext).findViewById(R.layout.wish_dialog);
		dialog.setContentView(R.layout.wish_dialog);
		dialog.setTitle("Wish");
		
		ImageView image = (ImageView) dialog.findViewById(R.id.image_display); 
		TextView title = (TextView)  dialog.findViewById(R.id.title_display);
		TextView desc = (TextView) dialog.findViewById(R.id.desc_display);
		TextView loc = (TextView)  dialog.findViewById(R.id.loc_display);
		TextView url = (TextView)  dialog.findViewById(R.id.url_display);
		RatingBar rating = (RatingBar)  dialog.findViewById(R.id.priority_display);
		TextView price = (TextView)  dialog.findViewById(R.id.price_display);
		TextView barcode = (TextView) dialog.findViewById(R.id.ean_display);
		TextView date = (TextView)  dialog.findViewById(R.id.date_display);
		Button button = (Button) dialog.findViewById(R.id.button_display);
		
		title.setText(w.getTitle().toUpperCase());
		title.setTypeface(null, Typeface.BOLD);
		
		if (w.getImage() != null)
		{
			image.setImageBitmap(decodeBitmap(w.getImage(), image.getWidth(), image.getHeight()));
		}
		else
		{
			Drawable drawable = mContext.getResources().getDrawable(
					mContext.getResources().getIdentifier("image_preview", "drawable",
							mContext.getPackageName()));
			image.setImageDrawable(drawable);

		}

		if (w.getDesc() != null)
		{
			desc.setVisibility(View.VISIBLE);
			desc.setText(w.getDesc());
		}
		
		if (w.getLocation() != null)
		{
			loc.setVisibility(View.VISIBLE);
			loc.setText(w.getLocation());
		}
		else if (w.getUrl() != null)
		{
			url.setVisibility(View.VISIBLE);
			url.setText(w.getUrl());
		}
		
		if(w.getPriority() != 0)
		{
			rating.setVisibility(View.VISIBLE);
			rating.setRating(w.getPriority() - 1);
		}
		
		dialog.show();
		Log.i("Click List", "Show");
		
		
		button.setOnClickListener(new View.OnClickListener() 
		{

			@Override
			public void onClick(View v)
			{
				dialog.dismiss();
				
			}
		});
	}

}
