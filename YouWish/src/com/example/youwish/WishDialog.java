package com.example.youwish;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class WishDialog extends Dialog
{

	public Activity c;
	public Dialog d;
	public Button yes, no;
	private Wish w;

	public WishDialog(Activity a, Wish w)
	{
		super(a);
		// TODO Auto-generated constructor stub
		this.c = a;
		this.w = w;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wish_dialog);

		ImageView image = (ImageView) findViewById(R.id.image_display);
		TextView title = (TextView) findViewById(R.id.title_display);
		TextView desc = (TextView) findViewById(R.id.desc_display);
		TextView loc = (TextView) findViewById(R.id.loc_display);
		TextView url = (TextView) findViewById(R.id.url_display);
		RatingBar rating = (RatingBar) findViewById(R.id.priority_display);
		TextView price = (TextView) findViewById(R.id.price_display);
		TextView barcode = (TextView) findViewById(R.id.ean_display);
		TextView date = (TextView) findViewById(R.id.date_display);
		Button button = (Button) findViewById(R.id.button_display);

		title.setText(w.getTitle().toUpperCase());
		title.setTypeface(null, Typeface.BOLD);

		if (w.getImage() != null)
		{
			image.setImageBitmap(decodeBitmap(w.getImage(), image.getWidth(), image.getHeight()));
		}
		else
		{
			Drawable drawable = c.getResources()
					.getDrawable(
							c.getResources().getIdentifier("image_preview", "drawable",
									c.getPackageName()));
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

		//if (w.getPriority() != 0)
		//{
		//	rating.setVisibility(View.VISIBLE);
		//	rating.setRating(w.getPriority());
		//}

		show();
		Log.i("Click List", "Show");

		button.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				dismiss();

			}
			// Perform button logic
		});
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
}