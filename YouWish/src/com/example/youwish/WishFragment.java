/*package com.example.youwish;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class WishFragment extends Fragment 
{
	static final String wish = "wish";
	static final String title = "title";
	static final String desc = "desc";
	static final String thumb_img = "thumb_img";
	
	ListView list;
    ListAdapter adapter;
    
    //private Wish w1, w2, w3, w4, w5;
	
	public WishFragment()
	{
		
		
	}
	
	public View onCreateView( LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState )
	{	
		View rootView = inflater.inflate(R.layout.fragment_wish,
				container, false);
		
		//w1 = new Wish("image", "T-Shirt", "White round-neck t-shirt", "location", "url", 1);
		ArrayList<HashMap<String, String>> wishList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put(title, "T-Shirt");
        map.put(desc, "White round-neck t-shirt");
		wishList.add(map);
		
		map.put(title, "Runners");
        map.put(desc, "Black Free Runs");
		wishList.add(map);
		
		list = (ListView) getActivity().findViewById(R.id.list);
		
		return rootView;
	}
	
	
	 
    // Getting adapter by passing xml data ArrayList
    adapter = new ListAdapter(this, wishList);
    list.setAdapter(adapter);
}
*/