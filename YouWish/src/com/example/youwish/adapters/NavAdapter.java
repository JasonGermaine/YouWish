package com.example.youwish.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.youwish.R;

public class NavAdapter extends ArrayAdapter<String>
{
	Context context;
	private String[] TextValue;

	public NavAdapter(Context context, String[] TextValue)
	{
		super(context, R.layout.row_drawer, TextValue);
		this.context = context;
		this.TextValue = TextValue;

	}

	/*
	 * Sets the view of the drawer list items
	 */
	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.row_drawer, parent,
				false);

		TextView text = (TextView) rowView.findViewById(R.id.text1);
		ImageView image = (ImageView) rowView.findViewById(R.id.image1);

		text.setText(TextValue[position]);

		if (position == 0)
		{
			image.setImageResource(R.drawable.ic_action_profile);
		}
		else if (position == 1)
		{
			image.setImageResource(R.drawable.ic_action_stream);
		}
		else if (position == 2)
		{
			image.setImageResource(R.drawable.ic_action_search);
		}
		else
		{
			image.setImageResource(R.drawable.ic_action_logout);
		}

		return rowView;

	}

}