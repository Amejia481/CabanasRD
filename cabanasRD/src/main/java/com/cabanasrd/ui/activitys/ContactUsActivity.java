package com.cabanasrd.ui.activitys;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cabanasrd.R;
import com.cabanasrd.config.CabannasrdApp;
import com.cabanasrd.config.CabannasrdApp.TrackerName;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;



public class ContactUsActivity  extends ActionBarActivity  {
	
    

	protected void onCreate(Bundle savedInstanceState) {
	   	 super.onCreate(savedInstanceState);
	   	 setContentView(R.layout.activity_contact_us);
	   	 Typeface custom_font = Typeface.createFromAsset(getAssets(),
		        "Existence-Light.ttf");
	   	 TextView txtAbout = (TextView)findViewById(R.id.editText1);
	   	 txtAbout.setTypeface(custom_font);

        // Get tracker.
        Tracker t = ((CabannasrdApp)  getApplication()).getTracker(
            TrackerName.APP_TRACKER);

        // Set screen name.
        t.setScreenName(ContactUsActivity.class.toString());

        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());
        initComponents();
	}
	        
        

  @Override
public void onResume() {
	EditText txtName  = (EditText)findViewById(R.id.txtName);
  	EditText txtTitle  = (EditText)findViewById(R.id.txtTitle);
  	EditText txtMessage  = (EditText)findViewById(R.id.txtMessage);
	txtName.setText("");
	txtTitle.setText("");
	txtMessage.setText("");
	super.onResume();
}
 
    public void initComponents(){
    	 ((Button)findViewById(R.id.btnSend)).setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText txtName  = (EditText)findViewById(R.id.txtName);
				EditText txtTitle  = (EditText)findViewById(R.id.txtTitle);
				EditText txtMessage  = (EditText)findViewById(R.id.txtMessage);
				
				Intent emailIntent = new Intent(Intent.ACTION_SEND);
				emailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{"devscabanasrd@gmail.com"});
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, txtTitle.getText());
				emailIntent.setType("message/rfc822");
				emailIntent.putExtra(Intent.EXTRA_TEXT,"De :"+txtName.getText() +"\n\n "+ txtMessage.getText());

				
				
				try {
					startActivity(emailIntent);
				} catch (android.content.ActivityNotFoundException ex) {
				   ex.printStackTrace();
				}
			}
		});
    }
}

