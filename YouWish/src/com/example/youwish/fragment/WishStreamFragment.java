package com.example.youwish.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.youwish.R;
import com.example.youwish.adapters.NewWishAdapter;
import com.example.youwish.db.AzureService;
import com.example.youwish.model.BucketList;
import com.example.youwish.model.ListWish;
import com.example.youwish.model.Product;
import com.example.youwish.model.User;
import com.example.youwish.util.YouWishApplication;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

public class WishStreamFragment extends ListFragment
{

	private ArrayList<Product> mProds;
	private ArrayList<BucketList> mBucks;

	private Button mButton;
	private NewWishAdapter mAdapter;
	private ListView wishList;

	private ProgressBar mProgress;

	private boolean mGotProds, mGotBucks, mProdEmpty, mBuckEmpty;

	private AzureService mAzureService;

	private int mProdCounter, mBuckCounter;

	private User user;

	private String mStream;

	public WishStreamFragment()
	{
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView;
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

			mStream = getArguments().getString("option");
			Log.i("OPTION", "" + mStream);

			if (mStream.equals("buckets"))
			{
				mProds = null;
				mProdEmpty = true;
				mBuckEmpty = false;
				mGotProds = true;
				mGotBucks = false;
			}
			else if (mStream.equals("products"))
			{
				mBucks = null;
				mProdEmpty = false;
				mBuckEmpty = true;
				mGotProds = false;
				mGotBucks = true;
			}
			else
			{
				mProdEmpty = false;
				mBuckEmpty = false;
				mGotProds = false;
				mGotBucks = false;
			}
			mBuckCounter = 0;
			mProdCounter = 0;

			rootView = inflater.inflate(R.layout.fragment_stream, container, false);

			mAzureService = ((YouWishApplication) getActivity().getApplication()).getService();
			mAzureService.setClient(getActivity().getApplicationContext());

			user = ((YouWishApplication) getActivity().getApplication()).getUser();

			mProgress = (ProgressBar) rootView.findViewById(R.id.progress_wish);

			mButton = (Button) rootView.findViewById(R.id.load_wishes);
			mButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
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
						getContent();
						mButton.setVisibility(View.GONE);
					}

				}
			});

			// Create an adapter to bind the items with the view
			mAdapter = new NewWishAdapter(getActivity(), R.layout.row_wish);
			wishList = (ListView) rootView.findViewById(android.R.id.list);
			wishList.setAdapter(mAdapter);

			getContent();
		}

		setHasOptionsMenu(true);
		return rootView;
	}

	private void refresh()
	{
		// update the main content by replacing fragments
		Fragment fragment = new StreamFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);

		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// handle item selection
		switch (item.getItemId())
		{
		case R.id.action_refresh:
			refresh();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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
			if ((mProds == null || mProds.isEmpty()) && (mBucks == null || mBucks.isEmpty()))
			{
				createAndShowDialog("There are no more wishes to load!", "No Results Found");
				mProgress.setVisibility(View.GONE);
			}
			else
			{
				if (mProds == null || mProds.isEmpty())
				{
					for (int i = 0; i < mBucks.size(); i++)
					{
						ListWish lw = new ListWish();
						lw.setBucketList(mBucks.get(i));
						mAdapter.add(lw);
					}
				}
				else if (mBucks == null || mBucks.isEmpty())
				{
					for (int i = 0; i < mProds.size(); i++)
					{
						ListWish lw = new ListWish();
						lw.setProduct(mProds.get(i));
						mAdapter.add(lw);
					}
				}
				else
				{
					int i, j, m, n;
					i = 0;
					j = 0;
					m = mProds.size();
					n = mBucks.size();
					while (i < m && j < n)
					{
						ListWish lw = new ListWish();
						if (mProds.get(i).getComparableTime()
								.isAfter(mBucks.get(j).getComparableTime()))
						{
							lw.setProduct(mProds.get(i));
							mAdapter.add(lw);
							i++;
						}
						else
						{
							lw.setBucketList(mBucks.get(j));
							mAdapter.add(lw);
							j++;
						}
					}
					if (i < m)
					{
						for (int p = i; p < m; p++)
						{
							ListWish lw = new ListWish();
							lw.setProduct(mProds.get(p));
							mAdapter.add(lw);
						}
					}
					else
					{
						for (int p = j; p < n; p++)
						{
							ListWish lw = new ListWish();
							lw.setBucketList(mBucks.get(p));
							mAdapter.add(lw);
						}
					}
				}
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
}
