package com.cabanasrd.ui.menu;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cabanasrd.R;

import java.util.ArrayList;

public class DrawerMenuAdapter extends BaseAdapter {

	private static DrawerMenuAdapter instance;
	private static LayoutInflater inflater =null;
	private int selected;
	private Activity activity =  null;
	private ArrayList<MenuItem> items;
	public static synchronized DrawerMenuAdapter getInstance(Activity activity) {
        if (instance == null) {
            instance = new DrawerMenuAdapter(activity);
        }
        return instance;
    }


	
	
	public void setSelection(int position)
	{
		selected = position;
		notifyDataSetChanged();
	}


	
	public void selectedItem(int index) {
		setSelection( index);
	}

	private DrawerMenuAdapter(Activity activity) {
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);	
		this.activity = activity;
	}
	
	public void update(){
	
			notifyDataSetInvalidated();
			notifyDataSetChanged();
	
	}
	
	public ArrayList<MenuItem> getItems() {
		return items;
	}




	public void setItems(ArrayList<MenuItem> items) {
		this.items = items;
	}




	@Override
	public int getCount() {
		
		return items.size();
	}
	
	public void clear(){
		update();
	}

	@Override
	public MenuItem getItem(int arg0)
	{
		return items.get(arg0);
	}
	
	@Override
	public long getItemId(int position)
	{
		return position;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (convertView == null)
			convertView = LayoutInflater.from(activity).inflate(
					R.layout.left_nav_item, null);
		TextView lbl = (TextView) convertView;
		lbl.setText(getItem(position).getTitle1());

		if (position == selected)
		{
			lbl.setCompoundDrawablesWithIntrinsicBounds(getItem(position)
					.getImage2(), 0, 0, 0);
//			lbl.setBackgroundColor(activity.getResources().getColor(
//					R.color.main_color_green));
//
			lbl.setTextColor(Color.WHITE);
		}
		else
		{
			lbl.setCompoundDrawablesWithIntrinsicBounds(getItem(position)
					.getImage1(), 0, 0, 0);
			lbl.setBackgroundResource(0);
//			lbl.setTextColor(activity.getResources().getColor(
//					R.color.main_color_gray_dk));
		}
		return convertView;
	}



}
