package com.example.youwish;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

public class TopProductFragment extends ListFragment
{
	private Button mButton;
	private WishAdapter mAdapter;
	private ListView wishList;

	private ProgressDialog mProcess;
	private ProgressBar mProgress;

	public TopProductFragment()
	{

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.list_wish, container, false);

		// user = ((YouWishApplication) getActivity().getApplication()).getUser();

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
				mButton.setVisibility(View.GONE);

			}
		});

		// Create an adapter to bind the items with the view
		mAdapter = new WishAdapter(getActivity(), R.layout.wish_row);
		wishList = (ListView) rootView.findViewById(android.R.id.list);
		wishList.setAdapter(mAdapter);

		new GetContent().execute();
		return rootView;
	}

	private class GetContent extends AsyncTask<String, Void, Wish>
	{

		@Override
		protected Wish doInBackground(String... arg)
		{

			String title;
			String desc;
			String image;
			Document doc;
			try
			{
				doc = Jsoup.connect("http://www.amazon.com/Best-Sellers/zgbs").get();

				Elements products = doc.getElementsByClass("zg_item zg_homeWidgetItem");

				for (Element element : products)
				{
					Element images = element.getElementsByClass("zg_image").first();
					String url = images.attr("src");
					title = images.attr("title");

					Bitmap bitmap = null;
					InputStream in = null;
					in = OpenHttpConnection(url);
					bitmap = BitmapFactory.decodeStream(in);
					in.close();
					
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
					byte[] byteArray = byteArrayOutputStream .toByteArray();
					image = Base64.encodeToString(byteArray, Base64.DEFAULT);
					
					return new Wish(image, title, null);

				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			return null;
		}
		

		private InputStream OpenHttpConnection(String urlString)
		{
			try
			{
				InputStream in = null;
				int response = -1;

				URL url = new URL(urlString);
				URLConnection conn = url.openConnection();

				if (!(conn instanceof HttpURLConnection))
					throw new IOException("Not an HTTP connection");

				HttpURLConnection httpConn = (HttpURLConnection) conn;
				httpConn.setAllowUserInteraction(false);
				httpConn.setInstanceFollowRedirects(true);
				httpConn.setRequestMethod("GET");
				httpConn.connect();
				response = httpConn.getResponseCode();
				if (response == HttpURLConnection.HTTP_OK)
				{
					in = httpConn.getInputStream();
				}
				return in;
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
			return null;
		}

		 protected void onPostExecute(Wish result)
		 {
			 mAdapter.add(result);
	     }
	}

}
