package com.example.youwish;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.QueryOrder;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;

public class WishStreamManager extends ListFragment
{
	// Create Client
	private MobileServiceClient mClient;
	private MobileServiceTable<BucketList> mBucketTable;
	private MobileServiceTable<Product> mProductTable;

	private ArrayList<Product> mProds;
	private ArrayList<BucketList> mBucks;

	private Button mButton;
	private WishAdapter mAdapter;
	private ListView wishList;

	private ProgressDialog mProcess;
	private ProgressBar mProgress;
	
	private boolean mGotProds, mGotBucks, mProdEmpty, mBuckEmpty;

	private AzureService mAzureService;

	private int mProdCounter, mBuckCounter;

	private User user;

	public WishStreamManager()
	{
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mBuckCounter = 0;
		mProdCounter = 0;
		mProdEmpty = false;
		mBuckEmpty = false;

		View rootView = inflater.inflate(R.layout.list_wish, container, false);

		mAzureService = ((YouWishApplication) getActivity().getApplication()).getService();
		mAzureService.setClient(getActivity().getApplicationContext());

		user = ((YouWishApplication) getActivity().getApplication()).getUser();

		mProcess = new ProgressDialog(getActivity());

		mProcess.setMessage("Loading Wishes");
		mProcess.setCancelable(false);
		mProcess.show();

		mProgress = (ProgressBar) rootView.findViewById(R.id.progress_wish);
		
		mButton = (Button) rootView.findViewById(R.id.load_wishes);
		mButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				getContent();
				mButton.setVisibility(View.GONE);
				
			}
		});

		// Create an adapter to bind the items with the view
		mAdapter = new WishAdapter(getActivity(), R.layout.wish_row);
		wishList = (ListView) rootView.findViewById(android.R.id.list);
		wishList.setAdapter(mAdapter);

		mGotProds = false;
		mGotBucks = false;

		// sortWishes();
		getContent();

		return rootView;
	}

	private void getContent()
	{
		mButton.setVisibility(View.GONE);
		mProgress.setVisibility(View.VISIBLE);
		if (!mProdEmpty && !mBuckEmpty)
		{
			Log.i("STREAM", "GETTING PRODUCTS AND BUCKETS");
			mGotProds = false;
			mGotBucks = false;

			getProducts();
			getBuckets();

		}
		else if (!mProdEmpty)
		{
			Log.i("STREAM", "GETTING PRODUCTS");
			getProducts();
		}
		else if (!mBuckEmpty)
		{
			Log.i("STREAM", "GETTING BUCKETS");
			getBuckets();
		}
		else
		{
			Log.i("STREAM", "EMPTY");
			mProgress.setVisibility(View.GONE);
		}
	}

	private void getProducts()
	{
		mAzureService.streamProducts(mProdCounter, new TableQueryCallback<Product>()
		{
			public void onCompleted(List<Product> result, int count, Exception exception,
					ServiceFilterResponse response)
			{
				if (exception == null)
				{
					mProds = (ArrayList<Product>) result;

					if (mProds.isEmpty())
					{
						Log.i("AZURE", "EMPTY PRDDUCTS " + result.size());
						mProdEmpty = true;
					}
					else
					{
						Log.i("AZURE", "PRDDUCTS " + result.size());
						mProdCounter += result.size();

					}
					mGotProds = true;
					sortWishes();

				}
				else
				{
					createAndShowDialog(exception, "Error");
				}
			}
		});
	}

	private void getBuckets()
	{
		mAzureService.streamBucketList(mBuckCounter, new TableQueryCallback<BucketList>()
		{
			public void onCompleted(List<BucketList> result, int count, Exception exception,
					ServiceFilterResponse response)
			{
				if (exception == null)
				{
					mBucks = (ArrayList<BucketList>) result;
					if (mBucks.isEmpty())
					{
						Log.i("AZURE", "EMPTY BUCKETS " + result.size());
						mBuckEmpty = true;
					}
					else
					{
						Log.i("AZURE", "BUCKETS " + result.size());
						mBuckCounter += result.size();
					}

					mGotBucks = true;
					sortWishes();
				}
				else
				{
					createAndShowDialog(exception, "Error");
				}
			}
		});

	}

	private void sortWishes()
	{

		if (mGotProds && mGotBucks)
		{
			if (mProds.isEmpty() && mBucks.isEmpty())
			{
				mProcess.dismiss();
				createAndShowDialog("There are no more wishes to load!",
						"No Results Found");
				mProgress.setVisibility(View.GONE);
			}
			else
			{
				if (mProds.isEmpty())
				{
					for (Wish w : mBucks)
					{
						mAdapter.add(w);
					}
				}
				else if (mBucks.isEmpty())
				{
					for (Wish w : mProds)
					{
						mAdapter.add(w);
					}
				}
				else
				{
					Wish w;
					int i, j, m, n;
					i = 0;
					j = 0;
					m = mProds.size();
					n = mBucks.size();
					while (i < m && j < n)
					{
						if (mProds.get(i).getComparableTime()
								.isAfter(mBucks.get(j).getComparableTime()))
						{
							w = mProds.get(i);
							mAdapter.add(w);
							i++;
						}
						else
						{
							w = mBucks.get(j);
							mAdapter.add(w);
							j++;
						}
					}
					if (i < m)
					{
						for (int p = i; p < m; p++)
						{
							w = mProds.get(p);
							mAdapter.add(w);
						}
					}
					else
					{
						for (int p = j; p < n; p++)
						{
							w = mBucks.get(p);
							mAdapter.add(w);
						}
					}
				}
				mProcess.dismiss();
				mProgress.setVisibility(View.GONE);
				mButton.setVisibility(View.VISIBLE);
			}

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
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setMessage(message);
		builder.setTitle(title);
		builder.setPositiveButton("OK", null);
		builder.create().show();
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
