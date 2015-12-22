package com.cabanasrd.ui.activitys;

import java.util.Locale;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.cabanasrd.R;
import com.cabanasrd.config.CabannasrdApp;
import com.cabanasrd.config.CabannasrdApp.TrackerName;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;



public class AboutUsActivity extends ActionBarActivity  {
	
    

    protected void onCreate(Bundle savedInstanceState) {
    	 super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_about_us);

    	WebView webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        String url = "";
        if(Locale.getDefault().getLanguage().equalsIgnoreCase("en")){
        	url = "file:///android_asset/team-en.html";
        }else{
        	url = "file:///android_asset/team.html";
        }
        
        
        webView.loadUrl(url);
    	
    	// Get tracker.
        Tracker t = ((CabannasrdApp)  getApplication()).getTracker(
            TrackerName.APP_TRACKER);

        // Set screen name.
        t.setScreenName(AboutUsActivity.class.toString());

        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());
       
        
    }
    


 
   
}

