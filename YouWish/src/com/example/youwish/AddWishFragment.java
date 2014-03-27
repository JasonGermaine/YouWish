package com.example.youwish;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.youwish.AddWishActivity.GetAddressTask;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;

public class AddWishFragment extends Fragment implements LocationListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener
{
	// Variables to store request codes for handling intents
	static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int REQUEST_IMAGE_GALLERY = 2;

	// Directory path in which to store images
	private final String dir = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
			+ "/YouWish/";

	// ExifInterface is used to read/write JPEG properties
	private ExifInterface exif;

	// Variable to store Uri of image
	private Uri uri;

	// Variables for handling user input
	private String mAchieveBy, mDesc, mLoc, mTitle, mUrl, mImage;
	private int mEan, mRating;
	private boolean mExtraDetail, mIsLocation, mIsProduct;
	private double mPrice;

	// Variables for working with images
	private String mCurrentPath;
	private Bitmap mBitmap;
	private File mPhoto;

	// Variables to obtain current Location
	private Location mLocation;
	private LocationClient mLocationClient;

	// UI Components
	private EditText mAchieveByView, mDescView, mLocView, mPriceView,
			mTitleView, mUrlView, mEanView;
	private View mAchieveView, mExtraContent, mLocationView, mAddStatusView;
	private Button mAddDetail, mGalleryButton, mCameraButton;
	private ImageButton mLocButton;
	private ImageView mImageView;
	private RatingBar mRatingView;
	private TextView mTitleAchieve, mTitleEAN, mTitleLocation, mTitlePrice,
			mTitleURL;

	private Product p;
	private BucketList b;

	// Mandatory Constructor
	public AddWishFragment()
	{
	}

	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		// Create the status view
		mAddStatusView = getActivity().findViewById(R.id.add_status);

		// Create directory
		new File(dir).mkdirs();

		// Set initial handlers to appropriate value
		mExtraDetail = false;
		mIsProduct = true;
		mIsLocation = true;

		// Initializing UI components
		mDescView = ((EditText) getActivity().findViewById(R.id.add_desc));
		mImageView = ((ImageView) getActivity().findViewById(R.id.wish_img));
		mTitleView = ((EditText) getActivity().findViewById(R.id.add_title));

		mAddDetail = ((Button) getActivity().findViewById(
				R.id.add_extra_content));
		mExtraContent = getActivity().findViewById(R.id.extra_content);

		mTitlePrice = ((TextView) getActivity().findViewById(R.id.title_price));
		mPriceView = ((EditText) getActivity().findViewById(R.id.add_price));

		mTitleEAN = ((TextView) getActivity().findViewById(R.id.title_ean));
		mEanView = ((EditText) getActivity().findViewById(R.id.add_ean));

		mTitleURL = ((TextView) getActivity().findViewById(R.id.title_url));
		mUrlView = ((EditText) getActivity().findViewById(R.id.add_url));

		mGalleryButton = ((Button) getActivity().findViewById(
				R.id.image_gallery));
		mCameraButton = ((Button) getActivity().findViewById(R.id.image_camera));

		mRatingView = ((RatingBar) getActivity().findViewById(
				R.id.rating_priority));

		mTitleLocation = ((TextView) getActivity().findViewById(R.id.title_loc));
		mLocationView = getActivity().findViewById(R.id.wish_loc);
		mLocView = ((EditText) getActivity().findViewById(R.id.add_loc));
		mLocButton = ((ImageButton) getActivity().findViewById(R.id.pick_loc));

		mTitleAchieve = ((TextView) getActivity().findViewById(
				R.id.title_achieve));
		mAchieveView = getActivity().findViewById(R.id.wish_achieve);
		mAchieveByView = ((EditText) getActivity().findViewById(
				R.id.add_achieve));

		// Handle when location button is clicked
		mLocButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick( View v )
			{
				// calling the get address method
				getAddress();
			}
		});

		// Handle when gallery button is clicked
		mGalleryButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick( View v )
			{
				// Create intent for gallery and start activity
				Intent i = new Intent();
				i.setType("image/*");
				i.setAction("android.intent.action.GET_CONTENT");
				i.addCategory(Intent.CATEGORY_OPENABLE);
				startActivityForResult(i, REQUEST_IMAGE_GALLERY);
			}
		});

		// Handle when camera button is clicked
		mCameraButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick( View v )
			{
				// Call the get photo method
				takePhoto();
			}
		});

	}

	//
	// Methods to handle basic UI events
	//

	public void addExtraDetail( View view )
	{
		mExtraDetail = true;

		// Display added fields
		mAddDetail.setVisibility(View.GONE);
		mExtraContent.setVisibility(View.VISIBLE);
		mTitleURL.setVisibility(View.GONE);
		mUrlView.setVisibility(View.GONE);

		// Check if product of bucket list is selected
		// Display appropriate fields

		if (mIsProduct == true)
		{
			mTitleAchieve.setVisibility(View.GONE);
			mAchieveView.setVisibility(View.GONE);
		}
		else
		{
			mTitlePrice.setVisibility(View.GONE);
			mTitleEAN.setVisibility(View.GONE);
			mPriceView.setVisibility(View.GONE);
			mEanView.setVisibility(View.GONE);
		}

	}

	// Location/URL radio button handler
	public void onLocURLClicked( View view )
	{
		// check if button is checked
		boolean checked = ((RadioButton) view).isChecked();

		switch (view.getId())
		{

		// Check if location or url is clicked
		// Display appropriate fields

		case R.id.radio_location:
			if (checked)
			{
				mIsLocation = true;
				mTitleLocation.setVisibility(View.VISIBLE);
				mLocationView.setVisibility(View.VISIBLE);

				mTitleURL.setVisibility(View.GONE);
				mUrlView.setVisibility(View.GONE);
			}
			break;
		case R.id.radio_url:
			if (checked)
			{
				mIsLocation = false;
				mTitleURL.setVisibility(View.VISIBLE);
				mUrlView.setVisibility(View.VISIBLE);
				mTitleLocation.setVisibility(View.GONE);
				mLocationView.setVisibility(View.GONE);
			}
			break;
		}

	}

	// Product/BucketList radio button handler
	public void onWishTypeClicked( View view )
	{
		// check if button is checked
		boolean checked = ((RadioButton) view).isChecked();

		switch (view.getId())
		{
		// Check if location or url is clicked
		// Display appropriate fields

		case R.id.radio_bucket:
			if (checked)
			{
				mIsProduct = false;
				if (mExtraDetail == true)
				{
					mTitleAchieve.setVisibility(View.VISIBLE);
					mAchieveView.setVisibility(View.VISIBLE);

					mTitlePrice.setVisibility(View.GONE);
					mTitleEAN.setVisibility(View.GONE);
					mPriceView.setVisibility(View.GONE);
					mEanView.setVisibility(View.GONE);
				}
			}
			break;
		case R.id.radio_product:
			if (checked)
			{
				mIsProduct = true;
				if (mExtraDetail == true)
				{
					mTitlePrice.setVisibility(View.VISIBLE);
					mTitleEAN.setVisibility(View.VISIBLE);
					mPriceView.setVisibility(View.VISIBLE);
					mEanView.setVisibility(View.VISIBLE);

					mTitleAchieve.setVisibility(View.GONE);
					mAchieveView.setVisibility(View.GONE);
				}
			}
			break;
		}
	}

	public boolean onCreateOptionsMenu( Menu menu )
	{
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.add_wish, menu);
		return true;
	}

	public boolean onOptionsItemSelected( MenuItem item )
	{
		// Check if add button is pressed
		if (item == getActivity().findViewById(R.id.action_add))
		{
			// validate input if pressed
			validate();
		}
		return true;
	}

	// Get the appropriate date picker for this activity
	public void showDatePickerDialog( View paramView )
	{
		new DatePickerFragment(getActivity()).show(getActivity()
				.getSupportFragmentManager(), "datePicker");
	}

	//
	// Methods for obtaining gallery/camera and display image
	//

	public void takePhoto()
	{
		// Create a unique file path
		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String path = dir + timestamp + ".jpg";

		// Create new file using path
		mPhoto = new File(path);
		try
		{
			mPhoto.createNewFile();
			mCurrentPath = mPhoto.getAbsolutePath();
			exif = new ExifInterface(mCurrentPath);

			uri = Uri.fromFile(mPhoto);

			// Create intent for camera and start activity
			Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
			i.putExtra("output", uri);
			startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
			return;
		}
		catch (IOException localIOException)
		{

		}
	}

	// Capture result of activities
	public void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		// Check if the result came back ok
		if (resultCode == getActivity().RESULT_OK)
		{
			// Check if request is from the gallery
			if (requestCode == REQUEST_IMAGE_GALLERY)
			{
				uri = data.getData();
				mPhoto = new File(uri.toString());
				try
				{
					// We need to recyle unused bitmaps
					if (mBitmap != null)
					{
						mBitmap.recycle();
					}

					// Stream data into a bitmap
					InputStream stream = getActivity().getContentResolver()
							.openInputStream(data.getData());
					mBitmap = BitmapFactory.decodeStream(stream);
					stream.close();

					// Set ImageView to the bitmap created
					mImageView.setImageBitmap(mBitmap);
				}
				catch (FileNotFoundException e)
				{
					e.printStackTrace();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				super.onActivityResult(requestCode, resultCode, data);
			}

			// Check if result is from camera
			else if (requestCode == REQUEST_IMAGE_CAPTURE)
			{
				// Call methods to display and save
				setPic();
				galleryAddPic();
			}
		}
	}

	public void setPic()
	{
		// Create a bitmap for the image captured
		Bitmap b = BitmapFactory.decodeFile(mCurrentPath);
		int imageW = mImageView.getWidth();
		int imageH = mImageView.getHeight();

		// TODO: Fix photo orientation

		// Create a scaled version of image to display
		mBitmap = Bitmap.createScaledBitmap(b, imageW, imageH, true);

		// Display bitmap in ImageView
		mImageView.setImageBitmap(mBitmap);
	}

	// Add the captured to gallery folder
	public void galleryAddPic()
	{
		// Create and execute the intent to write to gallery
		Intent i = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		i.setData(Uri.fromFile(new File(mCurrentPath)));
		getActivity().sendBroadcast(i);
	}

	//
	// Methods for obtaining current location
	//
	public void getAddress()
	{
		// Create an instance of LocationClient
		mLocationClient = new LocationClient(MainActivity.CONTEXT, this, this);

		// connect to the client
		mLocationClient.connect();
	}

	public void onConnected( Bundle paramBundle )
	{
		// Check for Geocoder
		if (Geocoder.isPresent())
		{
			// Get last location and call asyn get address task
			mLocation = mLocationClient.getLastLocation();
			(new GetAddressTask(getActivity())).execute(mLocation);
		}
	}

	public void onConnectionFailed( ConnectionResult paramConnectionResult )
	{
	}

	public void onDisconnected()
	{
	}

	public void onLocationChanged( Location paramLocation )
	{
	}

	protected class GetAddressTask extends AsyncTask<Location, Void, String>
	{
		Context mContext;

		public GetAddressTask(Context context)
		{
			super();
			mContext = context;
		}

		/**
		 * Get a Geocoder instance, get the latitude and longitude look up the
		 * address, and return it
		 * 
		 * @params params One or more Location objects
		 * @return A string containing the address of the current location, or
		 *         an empty string if no address can be found, or an error
		 *         message
		 */
		@Override
		protected String doInBackground( Location... params )
		{
			Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

			// Get the current location from the input parameter list
			Location loc = params[0];

			// Create a list to contain the result address
			List<Address> addresses = null;
			try
			{
				/*
				 * Return 1 address.
				 */
				addresses = geocoder.getFromLocation(loc.getLatitude(),
						loc.getLongitude(), 1);
			}
			catch (IOException e1)
			{
				Log.e("LocationSampleActivity",
						"IO Exception in getFromLocation()");
				e1.printStackTrace();
				return ("IO Exception trying to get address");
			}
			catch (IllegalArgumentException e2)
			{
				// Error message to post in the log
				String errorString = "Illegal arguments "
						+ Double.toString(loc.getLatitude()) + " , "
						+ Double.toString(loc.getLongitude())
						+ " passed to address service";
				Log.e("LocationSampleActivity", errorString);
				e2.printStackTrace();
				return errorString;
			}
			// If the reverse geocode returned an address
			if (addresses != null && addresses.size() > 0)
			{
				// Get the first address
				Address address = addresses.get(0);
				/*
				 * Format the first line of address (if available), city, and
				 * country name.
				 */
				String addressText = String.format(
						"%s, %s, %s",
						// If there's a street address, add it
						address.getMaxAddressLineIndex() > 0 ? address
								.getAddressLine(0) : "",
						// Locality is usually a city
						address.getLocality(),
						// The country of the address
						address.getCountryName());
				// Return the text
				return addressText;
			}
			else
			{
				return "No address found";
			}
		}

		@Override
		protected void onPostExecute( String address )
		{
			// Set the location EditText to retrieved address
			mLocView.setText(address);
		}
	}

	public void validate()
	{
		// local boolean to flag errors
		boolean cancel = false;

		// focus view to set focus on error field
		View focusView = null;

		prepareImage();

		mTitleView.setError(null);
		mTitle = mTitleView.getText().toString();

		// Title field is required
		// Check if title field as empty
		if (TextUtils.isEmpty(mTitle))
		{
			mTitleView.setError(getString(R.string.error_field_required));
			focusView = mTitleView;
			cancel = true;
		}

		// if add content button was not pressed
		if (!mExtraDetail)
		{
			// Check if errors were found
			if (cancel == true)
			{
				// set focus on field
				focusView.requestFocus();
			}
			else
			{
				if (mIsProduct)
				{

					// TODO: Add Product (basic)
					p = new Product(mImage, mTitle);
					addProduct(p);
				}
				else
				{
					// TODO: Add BucketList (basic)
					b = new BucketList(mImage, mTitle);
					addBucket(b);
				}
			}
		}
		else
		{
			mDescView.setError(null);
			mDesc = mDescView.getText().toString();

			// TODO: Validate Appropriately
			/*
			 * if (mDesc.matches("\"")) {
			 * 
			 * 
			 * }
			 */

			// check for location/url radio button clicked
			if (!mIsLocation)
			{
				mUrlView.setError(null);
				mUrl = mUrlView.getText().toString();

				// TODO: Validate Appropriately
				/*
				 * if (mUrl.contains("\"")) { localEditText = mUrlView; i = 1; }
				 */
			}
			else
			{
				mLocView.setError(null);
				mLoc = mLocView.getText().toString();

				// TODO: Validate Appropriately
				/*
				 * if (mLoc.contains("\"")) {
				 * 
				 * 
				 * }
				 */
			}

			// check if error was fount
			if (cancel == true)
			{
				// set focus on error field
				focusView.requestFocus();
			}
			else
			{
				mRating = (int) mRatingView.getRating();
				if (mIsProduct)
				{
					if (TextUtils.isEmpty(mPriceView.getText().toString()))
					{
						mPrice = 0.0;
					}
					else
					{
						mPrice = Double.parseDouble(mPriceView.getText()
								.toString());
					}

					if (TextUtils.isEmpty(mEanView.getText().toString()))
					{
						mEan = 0;
					}
					else
					{
						mEan = Integer.parseInt(mEanView.getText().toString());
					}

					// TODO: Add Detailed Product
					p = new Product(mImage, mTitle, mDesc, mLoc, mUrl, mRating,
							mPrice, mEan);

					addProduct(p);
				}
				else
				{
					mAchieveBy = mAchieveByView.getText().toString();
					// TODO: Add Detailed Product
					// TODO: Add Detailed Product
					b = new BucketList(mImage, mTitle, mDesc, mLoc, mUrl,
							mRating, mAchieveBy);

					addBucket(b);
				}
			}
		}
	}

	private void prepareImage()
	{
		mImage = null;
		if (uri != null && !uri.equals(""))
		{
			try
			{
				Cursor cursor = getActivity().getContentResolver().query(uri,
						null, null, null, null);
				cursor.moveToFirst();

				int index = cursor
						.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
				String absoluteFilePath = cursor.getString(index);
				FileInputStream fis = new FileInputStream(absoluteFilePath);

				int bytesRead = 0;
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] b = new byte[1024];
				while ((bytesRead = fis.read(b)) != -1)
				{
					bos.write(b, 0, bytesRead);
				}
				byte[] bytes = bos.toByteArray();
				mImage = Base64.encodeToString(bytes, Base64.DEFAULT);

				fis.close();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	private void addProduct( Product p )
	{
		// Attempt to insert using a callback
		/*
		 * mProductTable.insert(p, new TableOperationCallback<Product>() {
		 * public void onCompleted( Product entity, Exception exception,
		 * ServiceFilterResponse response ) { showProgress(false); if (exception
		 * == null) {
		 * 
		 * // Finish this activity /*finish();
		 * 
		 * // Start the Main Activity Intent i = new
		 * Intent(getActivity().getApplicationContext(), MainActivity.class);
		 * startActivity(i); } else {
		 * 
		 * } } });
		 */
	}

	private void addBucket( BucketList b )
	{
		// Attempt to insert using a callback
		/*
		 * mBucketListTable.insert(b, new TableOperationCallback<BucketList>() {
		 * public void onCompleted( BucketList entity, Exception exception,
		 * ServiceFilterResponse response ) { showProgress(false); if (exception
		 * == null) { // Finish this activity finish();
		 * 
		 * // Start the Main Activity Intent i = new
		 * Intent(getActivity().getApplicationContext(), MainActivity.class);
		 * startActivity(i); } else {
		 * 
		 * } } });
		 */
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress( final boolean show )
	{
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
		{
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mAddStatusView.setVisibility(View.VISIBLE);
			mAddStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter()
					{
						@Override
						public void onAnimationEnd( Animator animation )
						{
							mAddStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});
		}
		else
		{
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mAddStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
		}
	}

	public void onDestroyView()
	{
		getActivity().invalidateOptionsMenu();
		super.onDestroyView();
	}

}
