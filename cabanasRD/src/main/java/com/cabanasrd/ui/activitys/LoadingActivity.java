package com.cabanasrd.ui.activitys;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import com.cabanasrd.R;
import com.cabanasrd.config.CabannasrdApp;
import com.cabanasrd.config.CabannasrdApp.TrackerName;
import com.cabanasrd.data.HandlerMotelDataAcces;
import com.cabanasrd.data.HandlerMotelDataAccesRestFul;
import com.cabanasrd.data.MotelHandler;
import com.cabanasrd.data.entities.AppConfigData;
import com.cabanasrd.data.entities.Motel;
import com.cabanasrd.data.entities.Question;
import com.cabanasrd.data.entities.User;
import com.cabanasrd.ui.tools.UtilUI;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoadingActivity extends Activity {
	private TextView sloganTv;
	private Calendar dateOfActivityStarted = null;
	
	final Context context = this;
	private String reason;
	private User user;
	Runnable finalizeAppRunnable = null;
    

	private void init(){
		dateOfActivityStarted =   Calendar.getInstance();
		CabannasrdApp.currencyTypeDescripctionShort = getResources().getStringArray(R.array.motels_services_currency_type_short);
		CabannasrdApp.creditCardsType = getResources().getStringArray(R.array.credit_cards_type);
		CabannasrdApp.deleteOldFileOfMotels(this);
		

	   
	     
		final ArrayList<Motel> motelsFromDisk = readFromInternalStorage();
		
		//Checking if the user have the data on his cellphone
		if(motelsFromDisk ==null || (motelsFromDisk !=null && motelsFromDisk.isEmpty()) ){
			getAllMotels();
		}else{
			
			// the user have the data
			if(UtilUI.hasConnection(this) ){
				// Checking if are new Motels in the server
						Motel lastMotel = motelsFromDisk.get(motelsFromDisk.size() - 1);
						if(lastMotel!=null){
							int MotelLastId = lastMotel.getId().intValue();
							MotelHandler.getInstance().getMotelDiff(MotelLastId, new Callback<ArrayList<Motel>>() {
								
								@Override
								public void success(ArrayList<Motel> motels, Response arg1) {
									
									motelsFromDisk.addAll(motels);
									MotelHandler.getInstance().setMotels(motelsFromDisk);
									saveToInternalStorage();
									
									VerifyDeviceAndStartMainActivity();
								}
								
								@Override
								public void failure(RetrofitError arg0) {
									
									MotelHandler.getInstance().setMotels(motelsFromDisk);
									VerifyDeviceAndStartMainActivity();
								}
							});
						}
						
					}else{
						MotelHandler.getInstance().setMotels(motelsFromDisk);
						VerifyDeviceAndStartMainActivity();
					
					}
			
			
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		finalizeAppRunnable = new Runnable() {
			
			@Override
			public void run() {
				finish();
				
			}
		};
		
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Typeface typeface = Typeface.createFromAsset(getAssets(), "Existence-Light.ttf");
		setContentView(R.layout.activity_loading);
		sloganTv = (TextView)findViewById(R.id.txtSlogan);
		sloganTv.setTypeface(typeface);
	
		
		
		  // Get tracker.
        Tracker t = ((CabannasrdApp) getApplication()).getTracker(
            TrackerName.APP_TRACKER);

        // Set screen name.
        t.setScreenName(LoadingActivity.class.toString());

        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());
        
        
	}
	private void getAllMotels(){
		
		
		if(UtilUI.validateInternetConnetion(context, finalizeAppRunnable) ){
			
			MotelHandler.getInstance().LoaddDataAsyn(new Callback<Object>() {
				
				@Override
				public void success(Object arg0, Response arg1) {
					
					
					
					saveToInternalStorage();
					VerifyDeviceAndStartMainActivity();
					
					
				}
				
				@Override
				public void failure(RetrofitError arg0) {
					
					UtilUI.showAlertDialog(LoadingActivity.this,  
							LoadingActivity.this.getString(R.string.information), 
							LoadingActivity.this.getString(R.string.msErrorConnection), 
							R.string.iGotIt, finalizeAppRunnable);
					
					
				}
			});
		}
		
	}
	private void starIntentMainActivity(){

	    long howManyMillisSeconHavePassedToTheActivityStartToNow = (dateOfActivityStarted.getTimeInMillis()-System.currentTimeMillis());
		int delayToStarActivity = 0;
		
	    if(howManyMillisSeconHavePassedToTheActivityStartToNow < 3000){
	    	delayToStarActivity = 3000;
	    }
	    
	    new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				//Iniciar intento
				Intent mainIntent = new Intent(LoadingActivity.this, MainActivity.class);
				mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(mainIntent);
				LoadingActivity.this.finish();
				
			}
		}, delayToStarActivity);
		
	}
	public void VerifyDeviceAndStartMainActivity() {
		//Verificar si el dispositivo está registrado


		HandlerMotelDataAcces dataAcces = new  HandlerMotelDataAccesRestFul();
		if(CabannasrdApp.DEVICE == ""){

			user = new User();
			user.setCode("devsCabanas");
			user.setDevice(Secure.getString(context.getContentResolver(), Secure.ANDROID_ID));
			
			dataAcces.createDevice(user, new Callback<User>() {
				
				@Override
				public void success(User arg0, Response arg1) {
					CabannasrdApp.setDEVICE(user.getDevice());
					starIntentMainActivity();
				}
				
				@Override
				public void failure(RetrofitError arg0) {

					reason = arg0.getKind().name();
					if(reason.equalsIgnoreCase("NETWORK")){
						UtilUI.showAlertDialog(context, 
								getString(R.string.no_conn_error),
								getString(R.string.no_conn_message),
								R.string.ok,finalizeAppRunnable);
					}else{
						UtilUI.showAlertDialog(context,
								getString(R.string.server_error),
								getString(R.string.server_message),
								R.string.ok,finalizeAppRunnable);
					}
					
				}
			});
			
		}else{

			starIntentMainActivity();
		}
	}

	
	 public void saveToInternalStorage() {
	            MotelHandler.getInstance().saveToInternalStorage(CabannasrdApp.MOTEL_PATH, this);
	}
	 @Override
	protected void onResume() {
		 
		 

			 //Validating Version app
			 
			 MotelHandler.getInstance().getAppConfig(new Callback<AppConfigData>() {
		
							
							
		
							@Override
							public void success(AppConfigData appInfo, Response arg1) {
								
								PackageInfo pInfo;
								try {
									pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
								
								int versionApp = pInfo.versionCode;
								
								if(  appInfo.getAppServerVersion() <= versionApp){
									
									init();
									
								}else{
									
									UtilUI.showAlertDialog(LoadingActivity.this,LoadingActivity.this.getString( R.string.ms_app_outdated), 
											LoadingActivity.this.getString( R.string.ms_update_app), R.string.ok, new Runnable() {
												
												@Override
												public void run() {
													final String appPackageName = getPackageName(); 
										        	try {
										        	    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
										        	} catch (android.content.ActivityNotFoundException anfe) {
										        	    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
										        	}
										        	finish(); 
													
												}
											});
								}
									
								} catch (NameNotFoundException e) {
									
									e.printStackTrace();
								}
									
								
								
								
							}
							
							@Override
							public void failure(RetrofitError arg0) {
								// if some failure happende validating the app 
								// let the user use the app
									init();
								
							}
						});
		  
		 
		 
		 
		
		super.onResume();
	}
	public ArrayList<Motel> readFromInternalStorage() {
	    ArrayList<Motel> toReturn = null;
	    FileInputStream fis;
	    try {
	        fis = openFileInput(CabannasrdApp.MOTEL_PATH);
	        ObjectInputStream oi = new ObjectInputStream(fis);
	        
	        toReturn = (ArrayList<Motel>) oi.readObject();
	        oi.close();
	    } catch (FileNotFoundException e) {
	    	String err = (e.getMessage()==null)?"SD Card failed":e.getMessage();
	        Log.e("InternalStorage", err);
	    } catch (IOException e) {
	    	String err = (e.getMessage()==null)?"SD Card failed":e.getMessage();
	        Log.e("InternalStorage", err);
	    
	    } catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		}
	    return toReturn;
	} 
	
}
