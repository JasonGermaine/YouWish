package com.example.youwish.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youwish.R;
import com.example.youwish.db.AzureService;
import com.example.youwish.model.BucketList;
import com.example.youwish.model.ListWish;
import com.example.youwish.model.Product;
import com.example.youwish.model.User;
import com.example.youwish.model.Wish;
import com.example.youwish.util.YouWishApplication;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableDeleteCallback;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;

public class NewWishAdapter extends ArrayAdapter<ListWish>
{

	/**
	 * Adapter context
	 */
	private final Context mContext;

	private final User user;

	private final AzureService mAzureService;

	/**
	 * Adapter View layout
	 */
	private int mLayoutResourceId;

	public NewWishAdapter(Context context, int resource)
	{
		super(context, resource);

		mContext = context;
		mLayoutResourceId = resource;
		user = ((YouWishApplication) mContext.getApplicationContext()).getUser();
		mAzureService = ((YouWishApplication) mContext.getApplicationContext()).getService();
		mAzureService.setClient(mContext);

	}

	/*
	 * Sets up the UI for current Wish item and item click events 
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{

		View row = convertView;

		final ListWish lw = getItem(position);
		final Wish currentWish;

		if (lw.getBucketList() != null)
		{
			currentWish = lw.getBucketList();
			Log.i("BUCKET LIST", "" + lw.getBucketList().getUserId());
			currentWish.setUserId(lw.getBucketList().getUserId());
		}
		else
		{
			currentWish = lw.getProduct();
			Log.i("PRODUCT", "" + lw.getProduct().getUserId());
			currentWish.setUserId(lw.getProduct().getUserId());
		}

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
				createDialog(currentWish, lw);
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
			image.setImageBitmap(decodeBitmap(currentWish.getImage(), image.getWidth(),
					image.getHeight()));
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

	/*
	 * Loads the image into a bitmap
	 */
	public Bitmap decodeBitmap(String image, int width, int height)
	{
		byte[] decodedByte = Base64.decode(image, 0);
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = calculateSize(options, width, height);
		Bitmap bmp = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length, options);
		return bmp;
	}

	/*
	 * Scales the bitmap to appropriate size
	 */
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

	protected void createDialog(Wish w, final ListWish lw)
	{
		// custom dialog
		final Dialog dialog = new Dialog((Activity) mContext);
		dialog.setContentView(R.layout.dialog_wish);
		dialog.setTitle("Wish");

		ImageView image = (ImageView) dialog.findViewById(R.id.image_display);
		TextView title = (TextView) dialog.findViewById(R.id.title_display);
		TextView desc = (TextView) dialog.findViewById(R.id.desc_display);
		TextView loc = (TextView) dialog.findViewById(R.id.loc_display);
		TextView url = (TextView) dialog.findViewById(R.id.url_display);
		TextView date = (TextView) dialog.findViewById(R.id.date_display);
		TextView price = (TextView) dialog.findViewById(R.id.price_display);
		TextView ean = (TextView) dialog.findViewById(R.id.ean_display);
		RatingBar rating = (RatingBar) dialog.findViewById(R.id.priority_display);
		Button button = (Button) dialog.findViewById(R.id.button_display);
		Button addButton = (Button) dialog.findViewById(R.id.button_add);
		Button deleteButton = (Button) dialog.findViewById(R.id.button_delete);

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

		if (w.getPriority() != 0)
		{
			rating.setVisibility(View.VISIBLE);
			rating.setRating(w.getPriority());
		}
		if (lw.getBucketList() != null)
		{
			if (lw.getBucketList().getAchieveBy() != null)
			{
				date.setVisibility(View.VISIBLE);
				date.setText("To achieve by: " + lw.getBucketList().getAchieveBy());
			}
		}
		else
		{
			if (lw.getProduct().getEan() != 0)
			{
				ean.setVisibility(View.VISIBLE);
				ean.setText("Product Code: " + Integer.toString(lw.getProduct().getEan()));
			}
			if (lw.getProduct().getPrice() != 0.0)
			{
				price.setVisibility(View.VISIBLE);
				price.setText("€" + lw.getProduct().getPrice());
			}
		}

		dialog.show();

		button.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				dialog.dismiss();

			}
		});

		Log.i("WISH", "" + w.getUserId());
		Log.i("USER", "" + user.getEmail());

		if (w.getUserId().equals(user.getEmail()))
		{
			addButton.setVisibility(View.GONE);
			deleteButton.setVisibility(View.VISIBLE);

			deleteButton.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					dialog.dismiss();
					confirmDeletion(lw);

				}
			});
		}
		else
		{
			addButton.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					if (lw.getBucketList() != null)
					{
						BucketList b = lw.getBucketList();
						b.setId(null);
						b.setUserId(user.getEmail());
						b.setTimeStamp();

						mAzureService.addBucketList(b, new TableOperationCallback<BucketList>()
						{
							@Override
							public void onCompleted(BucketList entity, Exception exception,
									ServiceFilterResponse response)
							{
								dialog.dismiss();
								if (exception == null)
								{
									Toast.makeText(mContext, "Wish Added", Toast.LENGTH_SHORT)
											.show();
								}
								else
								{
									createAndShowDialog(exception, "Error");
								}
							}
						});

					}
					else
					{
						Product p = lw.getProduct();
						p.setId(null);
						p.setUserId(user.getEmail());
						p.setTimeStamp();

						mAzureService.addProduct(p, new TableOperationCallback<Product>()
						{
							@Override
							public void onCompleted(Product entity, Exception exception,
									ServiceFilterResponse response)
							{
								dialog.dismiss();
								if (exception == null)
								{
									Toast.makeText(mContext, "Wish Added", Toast.LENGTH_SHORT)
											.show();
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

	private void createAndShowDialog(Exception exception, String title)
	{
		createAndShowDialog(exception.getCause().getMessage(), title);
	}

	/*
	 * Method to build and display Alert Dialog
	 */
	private void createAndShowDialog(String message, String title)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

		builder.setMessage(message);
		builder.setTitle(title);
		builder.setPositiveButton("OK", null);
		builder.create().show();
	}

	/*
	 * Dialog to show conformation of deletion
	 */
	private void confirmDeletion(final ListWish lw)
	{
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				switch (which)
				{
				case DialogInterface.BUTTON_POSITIVE:
					if (lw.getBucketList() != null)
					{
						mAzureService.deleteBucket(lw.getBucketList().getId(),
								new TableDeleteCallback()
								{
									public void onCompleted(Exception exception,
											ServiceFilterResponse response)
									{
										if (exception == null)
										{
											Toast.makeText(mContext, "Wish Deleted",
													Toast.LENGTH_SHORT).show();
										}
										else
										{
											createAndShowDialog(exception, "Error");
										}
									}
								});
					}
					else
					{
						mAzureService.deleteProduct(lw.getProduct().getId(),
								new TableDeleteCallback()
								{
									public void onCompleted(Exception exception,
											ServiceFilterResponse response)
									{
										if (exception == null)
										{
											Toast.makeText(mContext, "Wish Deleted",
													Toast.LENGTH_SHORT).show();
										}
										else
										{
											createAndShowDialog("Wish is already deleted", "Error");
										}
									}
								});
					}
					
					
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
				.setNegativeButton("No", dialogClickListener).show();
	}

}
