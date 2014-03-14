package com.example.youwish;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;

public class DatePickerFragment extends DialogFragment implements
		DatePickerDialog.OnDateSetListener
{	
	private EditText mFieldToSet;
	private Activity activity;

	public DatePickerFragment(){}
	
	public DatePickerFragment(Activity a)
	{
		activity = a;
	    if ((activity instanceof RegisterActivity))
	    {
	      this.mFieldToSet = ((EditText) activity.findViewById(R.id.dob_field));
	    }
	    else
	    {
	    	 this.mFieldToSet = ((EditText) activity.findViewById(R.id.add_achieve));
	    }
	    
	}



	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		
		if(activity instanceof RegisterActivity)
		{
			c.add(Calendar.YEAR, -13);
			long l = c.getTimeInMillis();	
			// Create a new instance of DatePickerDialog and return it
			DatePickerDialog dialog =  new DatePickerDialog(getActivity(), this, year, month, day);
			dialog.getDatePicker().setMaxDate(l);
		    return dialog;
		}
		else
		{	
			// Create a new instance of DatePickerDialog and return it
			DatePickerDialog dialog =  new DatePickerDialog(getActivity(), this, year, month, day);
			dialog.getDatePicker().setMinDate(c.getTimeInMillis());
		    return dialog;
		}
		



	}

	public void onDateSet(DatePicker view, int year, int month, int day)
	{
		mFieldToSet.setText(day + "-" + (month+1) + "-" + year);
	}
}
