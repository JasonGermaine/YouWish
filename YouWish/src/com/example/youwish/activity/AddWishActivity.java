package com.example.youwish.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.FragmentActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youwish.R;
import com.example.youwish.db.AzureService;
import com.example.youwish.fragment.DatePickerFragment;
import com.example.youwish.model.BucketList;
import com.example.youwish.model.Product;
import com.example.youwish.model.User;
import com.example.youwish.util.YouWishApplication;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;

public class AddWishActivity extends FragmentActivity implements LocationListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener
{
	// Variables to store request codes for handling intents
	static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int REQUEST_IMAGE_GALLERY = 2;

	// Directory path in which to store images
	private final String dir = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/YouWish/";



	// Variable to store Uri of image
	private Uri uri;

	// Variables for handling user input
	private String mAchieveBy, mDesc, mLoc, mTitle, mUrl, mImage;
	private int mEan, mRating;
	private boolean mExtraDetail, mIsLocation, mIsProduct, mPickImage;
	private double mPrice;

	// Variables for working with images
	private String mCurrentPath;
	private Bitmap mBitmap;
	private File mPhoto;

	// Variables to obtain current Location
	private Location mLocation;
	private LocationClient mLocationClient;

	// UI Components
	private EditText mAchieveByView, mDescView, mLocView, mPriceView, mTitleView, mUrlView,
			mEanView;
	private View mAchieveView, mExtraContent, mLocationView;
	private Button mAddDetail, mGalleryButton, mCameraButton;
	private ImageButton mLocButton;
	private ImageView mImageView;
	private RatingBar mRatingView;
	private TextView mTitleAchieve, mTitleEAN, mTitleLocation, mTitlePrice, mTitleURL;


	List<Pair<String, String>> queryParams = new ArrayList<Pair<String, String>>();

	private AzureService mAzureService;

	private User user;
	private Product p;
	private BucketList b;
	private ProgressDialog mProcess;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Set the current layout
		super.onCreate(savedInstanceState);

		if (((YouWishApplication) getApplication()).verifyConnection(this) == false)
		{
			setContentView(R.layout.connection_failure);

			RelativeLayout layout = (RelativeLayout) findViewById(R.id.connection_error);
			layout.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					finish();
					startActivity(getIntent());
				}
			});
		}
		else
		{

			user = ((YouWishApplication) getApplication()).getUser();

			mAzureService = ((YouWishApplication) getApplication()).getService();
			mAzureService.setClient(getApplicationContext());


			mProcess = new ProgressDialog(this);

			setContentView(R.layout.activity_add_wish);

			mPickImage = false;


			// Create directory
			new File(dir).mkdirs();

			// Set initial handlers to appropriate value
			mExtraDetail = false;
			mIsProduct = true;
			mIsLocation = true;

			// Initializing UI components
			mDescView = ((EditText) findViewById(R.id.add_desc));
			mDescView.setFilters(new InputFilter[]
					{ new InputFilter.LengthFilter(200) });
			
			mImageView = ((ImageView) findViewById(R.id.wish_img));
			
			mTitleView = ((EditText) findViewById(R.id.add_title));
			mTitleView.setFilters(new InputFilter[]
					{ new InputFilter.LengthFilter(100) });
			
			mAddDetail = ((Button) findViewById(R.id.add_extra_content));
			mExtraContent = findViewById(R.id.extra_content);

			mTitlePrice = ((TextView) findViewById(R.id.title_price));
			mPriceView = ((EditText) findViewById(R.id.add_price));
			mPriceView.setFilters(new InputFilter[]
					{ new InputFilter.LengthFilter(20) });

			mTitleEAN = ((TextView) findViewById(R.id.title_ean));
			mEanView = ((EditText) findViewById(R.id.add_ean));
			mEanView.setFilters(new InputFilter[]
					{ new InputFilter.LengthFilter(20) });
			
			
			mTitleURL = ((TextView) findViewById(R.id.title_url));
			mUrlView = ((EditText) findViewById(R.id.add_url));
			mUrlView.setFilters(new InputFilter[]
					{ new InputFilter.LengthFilter(200) });
			
			mGalleryButton = ((Button) findViewById(R.id.image_gallery));
			mCameraButton = ((Button) findViewById(R.id.image_camera));

			mRatingView = ((RatingBar) findViewById(R.id.rating_priority));

			mTitleLocation = ((TextView) findViewById(R.id.title_loc));
			mLocationView = findViewById(R.id.wish_loc);
			
			mLocView = ((EditText) findViewById(R.id.add_loc));
			mLocView.setFilters(new InputFilter[]
					{ new InputFilter.LengthFilter(200) });
			
			mLocButton = ((ImageButton) findViewById(R.id.pick_loc));

			mTitleAchieve = ((TextView) findViewById(R.id.title_achieve));
			mAchieveView = findViewById(R.id.wish_achieve);
			mAchieveByView = ((EditText) findViewById(R.id.add_achieve));

			// Handle when location button is clicked
			mLocButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (((YouWishApplication) getApplication())
							.verifyConnection(AddWishActivity.this) == true)
					{
						// calling the get address method
						getAddress();
					}
					else
					{
						// No connectivity - display error message
						Toast.makeText(getApplicationContext(), "No Internet Connectivty",
								Toast.LENGTH_SHORT).show();
					}

				}
			});

			// Handle when gallery button is clicked
			mGalleryButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
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
				@Override
				public void onClick(View v)
				{
					// Call the get photo method
					takePhoto();
				}
			});

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		if (((YouWishApplication) getApplication()).verifyConnection(this) == true)
		{
			
			inflater.inflate(R.menu.add_wish, menu);
		}
		else
		{
			inflater.inflate(R.menu.no_access, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * Handle UI Components Displays appropriate views for buttons clicked
	 */
	public void addExtraDetail(View view)
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

	/*
	 * Location/URL radio button handler
	 */
	public void onLocURLClicked(View view)
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

	/*
	 * Product/BucketList radio button handler
	 */
	public void onWishTypeClicked(View view)
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

	/*
	 * Handle events for menu buttons pressed
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Check if add button is pressed
		if (item.getItemId() == R.id.action_add)
		{
			if (((YouWishApplication) getApplication()).verifyConnection(this) == true)
			{
				// Internet access - attempt validation
				validate();
			}
			else
			{
				// No connectivity - display error message
				Toast.makeText(getApplicationContext(), "No Internet Connectivty",
						Toast.LENGTH_SHORT).show();
			}
		}
		else if(item.getItemId() == R.id.action_refresh)
		{
			finish();
			startActivity(getIntent());
		}
		return true;
	}

	/*
	 * Get the appropriate date picker for this activity
	 */
	public void showDatePickerDialog(View paramView)
	{
		new DatePickerFragment(this).show(getSupportFragmentManager(), "datePicker");
	}

	/*
	 * Create a new file and send intent to take an image
	 */
	public void takePhoto()
	{
		// Create a unique file path
		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String path = dir + timestamp + ".jpg";

		// Create new file using path
		mPhoto = new File(path);
		try
		{
			mPhoto.createNewFile();
			mCurrentPath = mPhoto.getAbsolutePath();

			uri = Uri.fromFile(mPhoto);

			// Create intent for camera and start activity
			Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
			i.putExtra("output", uri);
			i.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION,
					ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
			return;
		} catch (IOException localIOException)
		{

		}
	}

	// Capture result of activities
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// Check if the result came back ok
		if (resultCode == RESULT_OK)
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
					InputStream stream = getContentResolver().openInputStream(data.getData());
					mBitmap = BitmapFactory.decodeStream(stream);
					try
					{
						stream.close();
					} catch (IOException e)
					{
						e.printStackTrace();
					}

					uri = data.getData();
					mPhoto = new File(getRealPathFromURI(uri));
					mCurrentPath = mPhoto.getAbsolutePath();

					// Set ImageView to the bitmap created
					// mImageView.setImageBitmap(mBitmap);
					setPic();

				} catch (FileNotFoundException e)
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

			mPickImage = true;
		}
	}

	private String getRealPathFromURI(Uri contentURI)
	{
		String result;
		Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
		if (cursor == null)
		{ // Source is Dropbox or other similar local file path
			result = contentURI.getPath();
		}
		else
		{
			cursor.moveToFirst();
			int idx = cursor.getColumnIndex(MediaColumns.DATA);
			result = cursor.getString(idx);
			cursor.close();
		}
		return result;
	}

	/*
	 * Once a photo is taken, format it and set the image view with image taken
	 */
	private void setPic()
	{
		try
		{
			File f = new File(mCurrentPath);
			ExifInterface exif = new ExifInterface(f.getPath());
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			int angle = 0;

			if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
			{
				angle = 90;
			}
			else if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
			{
				angle = 180;
			}
			else if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
			{
				angle = 270;
			}

			Matrix mat = new Matrix();
			mat.postRotate(angle);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;

			Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
			mBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);
			ByteArrayOutputStream outstudentstreamOutputStream = new ByteArrayOutputStream();
			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, outstudentstreamOutputStream);
			mImageView.setImageBitmap(mBitmap);

		} catch (IOException e)
		{
			Log.w("TAG", "-- Error in setting image");
		} catch (OutOfMemoryError oom)
		{
			Log.w("TAG", "-- OOM Error in setting image");
		}
	}

	/*
	 * Write the image taken to the phone's storage
	 */
	public void galleryAddPic()
	{
		// Create and execute the intent to write to gallery
		Intent i = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		i.setData(Uri.fromFile(new File(mCurrentPath)));
		sendBroadcast(i);
	}

	/*
	 * Initialize Location Client and make Connection
	 */
	public void getAddress()
	{
		mProcess.setMessage("Obtaining Location");
		mProcess.show();

		// Create an instance of LocationClient
		mLocationClient = new LocationClient(this, this, this);

		// connect to the client
		mLocationClient.connect();
	}

	/*
	 * On connection, get location
	 */
	@Override
	public void onConnected(Bundle paramBundle)
	{
		// Check for Geocoder
		if (Geocoder.isPresent())
		{
			// Get last location and call asyn get address task
			mLocation = mLocationClient.getLastLocation();
			(new GetAddressTask(this)).execute(mLocation);
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult paramConnectionResult)
	{
	}

	@Override
	public void onDisconnected()
	{
	}

	@Override
	public void onLocationChanged(Location paramLocation)
	{
	}

	/*
	 * Obtains and formats an address from Longitude and Latitude values. Displays the results in
	 * the appropriate Edit Text area
	 */
	protected class GetAddressTask extends AsyncTask<Location, Void, String>
	{
		Context mContext;

		public GetAddressTask(Context context)
		{
			super();
			mContext = context;
		}

		/*
		 * Get a Geocoder instance, get the latitude and longitude look up the address, and return
		 * it
		 * 
		 * @params params One or more Location objects
		 * 
		 * @return A string containing the address of the current location, or an empty string if no
		 * address can be found, or an error message
		 */
		@Override
		protected String doInBackground(Location... params)
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
				addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
			} catch (IOException e1)
			{
				Log.e("LocationSampleActivity", "IO Exception in getFromLocation()");
				e1.printStackTrace();
				return ("IO Exception trying to get address");
			} catch (IllegalArgumentException e2)
			{
				// Error message to post in the log
				String errorString = "Illegal arguments " + Double.toString(loc.getLatitude())
						+ " , " + Double.toString(loc.getLongitude())
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
				 * Format the first line of address (if available), city, and country name.
				 */
				String addressText = String.format("%s, %s, %s",
				// If there's a street address, add it
						address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
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
		protected void onPostExecute(String address)
		{
			// Set the location EditText to retrieved address
			mLocView.setText(address);
			mProcess.dismiss();
		}
	}

	/*
	 * Provide detailed validation for appropriate fields that are displayed
	 */
	public void validate()
	{
		String userId = user.getEmail();
		Date timeStamp = new Date();
		mProcess.setMessage("Adding Wish");
		mProcess.show();

		// Initialize the patterns to validate fields
		Pattern regex = Pattern.compile("[$&+:;=?@#|]");

		Matcher m;

		// local boolean to flag errors
		boolean cancel = false;

		// focus view to set focus on error field
		View focusView = null;

		mImage = null;

		// If an image has been takend or selected
		if (mPickImage)
		{
			// get the base 64 string
			mImage = Base64.encodeToString(getBytesFromBitmap(mBitmap), Base64.NO_WRAP);
		}

		mTitleView.setError(null);
		mTitle = mTitleView.getText().toString();
		mTitle = mTitle.replace("\"", "");

		// Title field is required
		// Test if title field as empty
		if (TextUtils.isEmpty(mTitle))
		{
			mTitleView.setError(getString(R.string.error_field_required));
			focusView = mTitleView;
			cancel = true;
		}

		// Test is title contains illegal characters
		m = regex.matcher(mTitle);
		if (m.find())
		{
			mTitleView.setError(getString(R.string.error_invalid_format));
			focusView = mTitleView;
			cancel = true;
		}

		// if add content button was not pressed
		if (!mExtraDetail)
		{
			// Check if errors were found
			if (cancel == true)
			{
				mProcess.dismiss();
				// set focus on field
				focusView.requestFocus();
			}
			else
			{
				// Check if Wish is a Product
				if (mIsProduct)
				{

					p = new Product(mImage, mTitle, userId);
					addProduct(p);
				}
				else
				{
					b = new BucketList(mImage, mTitle, userId);
					addBucket(b);
				}
			}
		}
		else
		{
			// Validate extra fields
			mDescView.setError(null);
			mDesc = mDescView.getText().toString();
			mDesc = mDesc.replace("\"", "");
			m = regex.matcher(mDesc);

			// Test if field contains invalid characters
			if (m.find())
			{
				mDescView.setError(getString(R.string.error_invalid_format));
				focusView = mDescView;
				cancel = true;
			}
			// check for location/url radio button clicked
			if (!mIsLocation)
			{
				mUrlView.setError(null);
				mUrl = mUrlView.getText().toString();

				if (!TextUtils.isEmpty(mUrl))
				{
					if(!android.util.Patterns.WEB_URL.matcher(mUrl).matches())
					{
						mUrlView.setError(getString(R.string.error_invalid_url));
						focusView = mUrlView;
						cancel = true;
					}
				}
			}
			else
			{
				mLocView.setError(null);
				mLoc = mLocView.getText().toString();
				mLoc = mLoc.replace("\"", "");
				m = regex.matcher(mLoc);

				// Test if field contains invalid characters
				if (m.find())
				{
					mLocView.setError(getString(R.string.error_invalid_format));
					focusView = mLocView;
					cancel = true;
				}

			}

			// check if error was fount
			if (cancel == true)
			{
				mProcess.dismiss();
				// set focus on error field
				focusView.requestFocus();
			}
			else
			{
				mRating = (int) mRatingView.getRating();

				// If Wish is a Product
				if (mIsProduct)
				{
					// If no price entered
					if (TextUtils.isEmpty(mPriceView.getText().toString()))
					{
						mPrice = 0.0;
					}
					else
					{
						// Set price
						mPrice = Double.parseDouble(mPriceView.getText().toString());
					}

					// If no barcode entered
					if (TextUtils.isEmpty(mEanView.getText().toString()))
					{
						mEan = 0;
					}
					else
					{
						// Set barcode
						mEan = Integer.parseInt(mEanView.getText().toString());
					}

					p = new Product(mImage, mTitle, mDesc, mLoc, mUrl, mRating, timeStamp, mPrice,
							mEan, userId);

					addProduct(p);
				}
				else
				{
					// Set achieve by date
					mAchieveBy = mAchieveByView.getText().toString();

					b = new BucketList(mImage, mTitle, mDesc, mLoc, mUrl, mRating, timeStamp,
							mAchieveBy, userId);

					addBucket(b);
				}
			}
		}
	}

	/*
	 * Send Product to the server to be validated and inserted
	 */
	private void addProduct(Product p)
	{
		p.setUserId(user.getEmail());
		p.setTimeStamp();
		// Attempt to insert using a callback
		mAzureService.addProduct(p, new TableOperationCallback<Product>()
		{
			@Override
			public void onCompleted(Product entity, Exception exception,
					ServiceFilterResponse response)
			{
				if (exception == null)
				{
					mProcess.dismiss();
					// Finish this activity
					finish();

					// Start the Main Activity
					Intent i = new Intent(getApplicationContext(), MainActivity.class);
					startActivity(i);
				}
				else
				{
					mProcess.dismiss();
					createAndShowDialog(exception, "Error");
				}
			}
		});
	}

	/*
	 * Send BucketList to the server to be validated and inserted
	 */
	private void addBucket(BucketList b)
	{

		b.setUserId(user.getEmail());
		b.setTimeStamp();
		// Attempt to insert using a callback
		mAzureService.addBucketList(b, new TableOperationCallback<BucketList>()
		{
			@Override
			public void onCompleted(BucketList entity, Exception exception,
					ServiceFilterResponse response)
			{
				if (exception == null)
				{
					mProcess.dismiss();
					// Finish this activity
					finish();

					// Start the Main Activity
					Intent i = new Intent(getApplicationContext(), MainActivity.class);
					startActivity(i);
				}
				else
				{
					mProcess.dismiss();
					createAndShowDialog(exception, "Error");
				}
			}
		});
	}

	// convert from bitmap to byte array
	/*
	 * Method to convert Bitmpap to bytes to be encoded to a String
	 */
	public byte[] getBytesFromBitmap(Bitmap bitmap)
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 70, stream);
		return stream.toByteArray();
	}

	/*
	 * Method to display errors from server
	 */
	private void createAndShowDialog(Exception exception, String title)
	{
		createAndShowDialog(exception.getCause().getMessage(), title);
	}

	/*
	 * Method to display errors
	 */
	private void createAndShowDialog(String message, String title)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(message);
		builder.setTitle(title);
		builder.setPositiveButton("OK", null);
		builder.create().show();
	}
}
