package com.example.youwish.fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.youwish.R;
import com.example.youwish.activity.MainActivity;
import com.example.youwish.db.AzureService;
import com.example.youwish.model.User;
import com.example.youwish.util.SessionManager;
import com.example.youwish.util.YouWishApplication;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;

public class ProfileFragment extends Fragment
{
	private FragmentTabHost mTabHost;

	private TextView mName;
	private ImageView mProfilePic, mFollow, mUnfollow;
	private ProgressDialog mProcess;

	private AzureService mAzureService;
	private SessionManager session;
	private MobileServiceClient mClient;
	private MobileServiceTable<User> mUserTable;
	private String mFName;
	private String mLName;
	private String text;
	private String mEmail;
	private boolean mPickImage;
	private boolean mLocalUser;
	public static ProfileFragment frag;

	// Variables to store request codes for handling intents
	static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int REQUEST_IMAGE_GALLERY = 2;

	// Directory path in which to store images
	private final String dir = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/YouWish/";

	// ExifInterface is used to read/write JPEG properties
	private ExifInterface exif;

	// Variable to store Uri of image
	private Uri uri;

	// Variables for working with images
	private String mCurrentPath;
	private Bitmap mBitmap;
	private File mPhoto;

	private User user;
	private User guest;

	private View rootView;

	public ProfileFragment()
	{

	}

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		session = SessionManager.getSessionManager(getActivity().getApplicationContext());

	}

	/*
	 * Takes in an argument to determine if the user is the local logged in user or 
	 * if the local logged in user if viewing another user's page.
	 * It sets up tabs and passes the same argument to the tab fragments.
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		frag = this;
		mLocalUser = getArguments().getBoolean("localuser");
		if (((YouWishApplication) getActivity().getApplication()).verifyConnection(getActivity()) == false)
		{
			rootView = inflater.inflate(R.layout.connection_failure, container, false);

			RelativeLayout layout = (RelativeLayout) rootView.findViewById(R.id.connection_error);
			layout.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					refresh(mLocalUser);
				}
			});
		}
		else
		{
			rootView = inflater.inflate(R.layout.fragment_profile, container, false);

			user = ((YouWishApplication) getActivity().getApplication()).getUser();

			Activity a = getActivity();
			if (a instanceof MainActivity)
			{
				((MainActivity) a).setProfileSelector();
			}

			mProfilePic = (ImageView) rootView.findViewById(R.id.profile_pic);
			mName = (TextView) rootView.findViewById(R.id.profile_name);
			mFollow = (ImageView) rootView.findViewById(R.id.follow);
			mUnfollow = (ImageView) rootView.findViewById(R.id.following);

			mTabHost = (FragmentTabHost) rootView.findViewById(android.R.id.tabhost);
			mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);
			
			mAzureService = ((YouWishApplication) getActivity().getApplication()).getService();
			mAzureService.setClient(getActivity().getApplicationContext());
			mProcess = new ProgressDialog(getActivity());
			mProcess.setMessage("Loading Profile");
			mProcess.setCancelable(false);
			mProcess.show();

			// ((YouWishApplication) getActivity().getApplication()).eraseGuest();
			if (mLocalUser == false)
			{
				guest = ((YouWishApplication) getActivity().getApplication()).getGuest();
				mEmail = guest.getEmail();

				if (guest.getProfilePic() != null)

				{
					mProfilePic.setImageBitmap(decodeBitmap(guest.getProfilePic(),
							mProfilePic.getWidth(), mProfilePic.getHeight()));
				}

				mName.setText(guest.getFName() + " " + guest.getLName());

				if (user.getFollowing() != null)
				{
					for (int i = 0; i < user.getFollowing().size(); i++)
					{
						if (user.getFollowing().get(i).equals(mEmail))
						{
							mFollow.setVisibility(View.GONE);
							mUnfollow.setVisibility(View.VISIBLE);
						}
					}
				}
				mFollow.setOnClickListener(new View.OnClickListener()
				{
					public void onClick(View v)
					{

						ArrayList<String> list;
						if (user.getFollowing() == null)
						{
							list = new ArrayList<String>();
						}
						else
						{
							list = user.getFollowing();
						}
						list.add(mEmail);
						user.setFollowing(list);
						mAzureService.updateFollowing(user, new TableOperationCallback<User>()
						{
							public void onCompleted(User entity, Exception exception,
									ServiceFilterResponse response)
							{
								if (exception == null)
								{
									mFollow.setVisibility(View.GONE);
									mUnfollow.setVisibility(View.VISIBLE);
								}
								else
								{
									createAndShowDialog(exception, "Error");

								}
							}
						});

					}
				});

				mUnfollow.setOnClickListener(new View.OnClickListener()
				{
					public void onClick(View v)
					{

						ArrayList<String> list = user.getFollowing();
						for (int i = 0; i < list.size(); i++)
						{
							if (list.get(i).equals(mEmail))
							{
								list.remove(i);
								user.setFollowing(list);
								mAzureService.updateFollowing(user,
										new TableOperationCallback<User>()
										{
											public void onCompleted(User entity,
													Exception exception,
													ServiceFilterResponse response)
											{
												if (exception == null)
												{
													mUnfollow.setVisibility(View.GONE);
													mFollow.setVisibility(View.VISIBLE);
												}
												else
												{
													createAndShowDialog(exception, "Error");

												}
											}
										});
							}
						}
					}
				});
				mProcess.dismiss();
			}
			else
			{
				mEmail = user.getEmail();
				if (user.getProfilePic() != null)
				{

					mProfilePic.setImageBitmap(decodeBitmap(user.getProfilePic(),
							mProfilePic.getWidth(), mProfilePic.getHeight()));
				}

				mName.setText(user.getFName() + " " + user.getLName());
				mProcess.dismiss();
				mLocalUser = true;
				mFollow.setVisibility(View.GONE);
				mUnfollow.setVisibility(View.GONE);

				mProfilePic.setOnClickListener(new View.OnClickListener()
				{
					public void onClick(View v)
					{
						createDialog();
					}
				});
				mProcess.dismiss();
			}

			mPickImage = false;

			
			Bundle args = new Bundle();
			args.putBoolean("LocalUser", mLocalUser);
			mTabHost.addTab(mTabHost.newTabSpec("bio").setIndicator("Bio"), BioFragment.class, args);

			mTabHost.addTab(mTabHost.newTabSpec("wishes").setIndicator("Wishes"),
					WishFragment.class, args);

			mTabHost.addTab(mTabHost.newTabSpec("following").setIndicator("Following"),
					FollowFragment.class, args);
		}

		setHasOptionsMenu(true);
		return rootView;
	}

	/*
	 * Creates a method to refresh the profile page.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// handle item selection
		switch (item.getItemId())
		{
		case R.id.action_refresh:
			refresh(mLocalUser);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void createDialog()
	{
		// custom dialog
		final Dialog dialog = new Dialog(getActivity());
		View layout = (getActivity()).findViewById(R.layout.dialog_picture);
		dialog.setContentView(R.layout.dialog_picture);
		dialog.setTitle("Change Profile Picture");

		ImageView mGalleryButton = ((ImageView) dialog.findViewById(R.id.image_gallery));
		ImageView mCameraButton = ((ImageView) dialog.findViewById(R.id.image_camera));

		Button button = (Button) dialog.findViewById(R.id.close_button);

		dialog.show();

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
		mCameraButton.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// Call the get photo method
				takePhoto();
			}
		});

		button.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				dialog.dismiss();

			}
		});
	}

	/*
	 * Loads bitmap to profile image
	 */
	public Bitmap decodeBitmap(String image, int reqWidth, int reqHeight)
	{
		byte[] decodedByte = Base64.decode(image, 0);
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = calculateSize(options, reqWidth, reqHeight);
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

	/*
	 * Determines if camera photo was taken or photo was chosen from gallery
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data)
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
					// We need to recycle unused bitmaps
					if (mBitmap != null)
					{
						mBitmap.recycle();
					}

					// Stream data into a bitmap
					InputStream stream = getActivity().getContentResolver().openInputStream(
							data.getData());
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
		Cursor cursor = getActivity().getContentResolver()
				.query(contentURI, null, null, null, null);
		if (cursor == null)
		{ // Source is Dropbox or other similar local file path
			result = contentURI.getPath();
		}
		else
		{
			cursor.moveToFirst();
			int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
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

			// get the base 64 string
			String mImage = Base64.encodeToString(getBytesFromBitmap(mBitmap), Base64.NO_WRAP);
			user.setProfilePic(mImage);
			mAzureService.updateProfilePic(user, new TableOperationCallback<User>()
			{
				@Override
				public void onCompleted(User entity, Exception exception,
						ServiceFilterResponse response)
				{
					if (exception == null)
					{
						mProfilePic.setImageBitmap(mBitmap);
					}
					else
					{
						createAndShowDialog(exception, "Error");

					}
				}
			});

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
		getActivity().sendBroadcast(i);
	}

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

	private void createAndShowDialog(Exception exception, String title)
	{
		createAndShowDialog(exception.getCause().getMessage(), title);
	}

	private void createAndShowDialog(String message, String title)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setMessage(message);
		builder.setTitle(title);
		builder.setPositiveButton("OK", null);
		builder.create().show();
	}

	/*
	 * Method to refresh page if internet connection goes
	 */
	public void refresh(boolean local)
	{
		mLocalUser = local;
		
		// update the main content by replacing fragments
		Fragment fragment = new ProfileFragment();
		Bundle args = new Bundle();
		args.putBoolean("localuser", mLocalUser);
		fragment.setArguments(args);

		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
	}
}
