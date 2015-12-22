package com.cabanasrd.config;

import java.io.File;
import java.util.HashMap;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings.Secure;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.model.LatLng;

public class CabannasrdApp extends Application {
	
	 private static final String PROPERTY_ID = "UA-57906569-1";
	 public static final String MOTEL_PATH = "Motels_2_1_6.data";
	 public static  int appServerVersion =0;
	 
	
	 public static  String serverURL ="http://desarrollo-cabanasrdapi.azurewebsites.net/";
	 public static  String mailService ="a@b.com";
	 public static  String mailTo ="a@b.com";
	 public static  String[] currencyTypeDescripctionShort = null;
	 public static  String[] creditCardsType = null;
	 
	 public static  String passMailService ="mailSenderPass";
	 public final static  int MOTEL_TYPE_HOUSING = 1;
	 public final static  int MOTEL_TYPE_ENTERTAINMENT = 2;
	 public final static  int MOTEL_TYPE_FOOD_OR_DRINKS = 3;
	 public final static  int MOTEL_TYPE_OTHER = 4;
	 public final static  int MOTEL_TYPE_CABANA = 1;
	 public final static  int MOTEL_TYPE_HOTEL = 2;
	 public final static  int MOTEL_TYPE_MOTEL = 3;
	 public static  String android_id ="";
	 public final static  int MOTEL_AVAILABILITY_EMPTY = 0;
	 public final static  int MOTEL_AVAILABILITY_MEDIUM = 1;
	 public final static  int MOTEL_AVAILABILITY_FULL = 2;
	 public static SharedPreferences prefs;
	 public static   String DEVICE ="";
	 public static LatLng actualUserPosition = null;
	 public static   boolean THE_USER_SEE_HELP_ADD_MESSAGE_ON_NEW_PLACES =false;
	 public static   boolean THE_USER_SEE_THE_USER_SEE_HELP_AVAILABILITY=false;
	 public static   boolean THE_USER_SEE_THE_USER_SEE_NEW_FEATURE_HELP=false;
	 public static   boolean OLD_FILE_MOTEL_DELETE = false;
	 public static   String LAST_DAY_USER_ANSWER_A_QUESTION = null;
	 public static Context context;

	    @Override public void onCreate() {
	        super.onCreate();
	        context = getApplicationContext();
			loadSharedPreferences(context);
	    }
	   
	    public static boolean   deleteOldFileOfMotels(Context context){
		    
	    	if(!OLD_FILE_MOTEL_DELETE){
	    		String dir = context.getFilesDir().getAbsolutePath();
			    File f0 = new File(dir, "Motels_2_1_3.data");
			    OLD_FILE_MOTEL_DELETE  = f0.delete();
			    if(OLD_FILE_MOTEL_DELETE){
				    SharedPreferences.Editor editor = prefs.edit();
					editor.putBoolean("OLD_FILE_MOTEL_DELETE1",OLD_FILE_MOTEL_DELETE);
					editor.commit();
					
			    }
			    Log.i("log_file", "File deleted: " + dir + " " + OLD_FILE_MOTEL_DELETE);
	    	}
	    	 
		    return OLD_FILE_MOTEL_DELETE  ; 
	    }
	 
	    public static boolean   get_OLD_FILE_MOTEL_DELETE(){
			return  prefs.getBoolean("OLD_FILE_MOTEL_DELETE1", false);
		}
		public static String   get_LAST_DAY_USER_ANSWER_A_QUESTION(){
			return  prefs.getString("LAST_DAY_USER_ANSWER_A_QUESTION", null);
		}


	    
	    public static boolean   get_THE_USER_SEE_THE_USER_SEE_NEW_FEATURE_HELP(){
			return  prefs.getBoolean("THE_USER_SEE_THE_USER_SEE_NEW_FEATURE_HELP1", false);
		}
	 
	 public static boolean   get_THE_USER_SEE_THE_USER_SEE_HELP_AVAILABILITY(){
			return  prefs.getBoolean("THE_USER_SEE_THE_USER_SEE_HELP_AVAILABILITY", false);
		}
	public enum TrackerName {
	    APP_TRACKER, // Tracker used only in this app.
	    GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
	    ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
	  }


	public static void loadSharedPreferences(Context context){
		prefs = context.getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
		DEVICE =  getSharedPreferencesString("device", "");
		THE_USER_SEE_HELP_ADD_MESSAGE_ON_NEW_PLACES =  get_THE_USER_SEE_HELP_ADD_MESSAGE_ON_NEW_PLACES();
		THE_USER_SEE_THE_USER_SEE_HELP_AVAILABILITY = get_THE_USER_SEE_THE_USER_SEE_HELP_AVAILABILITY();
		THE_USER_SEE_THE_USER_SEE_NEW_FEATURE_HELP = get_THE_USER_SEE_THE_USER_SEE_NEW_FEATURE_HELP();
		LAST_DAY_USER_ANSWER_A_QUESTION = get_LAST_DAY_USER_ANSWER_A_QUESTION();
		OLD_FILE_MOTEL_DELETE =  get_OLD_FILE_MOTEL_DELETE();
		CabannasrdApp.android_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		
	}
	public static String getSharedPreferencesString(String key,String ifNullValue){
		
		return prefs.getString(key, ifNullValue);
		
	}


	public static void  set_LAST_DAY_USER_ANSWER_A_QUESTION(String value){
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("LAST_DAY_USER_ANSWER_A_QUESTION", value);
		editor.commit();
		LAST_DAY_USER_ANSWER_A_QUESTION = value;
	}
	public static void  set_THE_USER_SEE_HELP_ADD_MESSAGE_ON_NEW_PLACES(boolean value){
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("THE_USER_SEE_HELP_ADD_MESSAGE_ON_NEW_PLACES",value);
		editor.commit();
		THE_USER_SEE_HELP_ADD_MESSAGE_ON_NEW_PLACES = value;
		
		
		
	}
	public static void  set_THE_USER_SEE_THE_USER_SEE_NEW_FEATURE_HELP(boolean value){
		
		if(prefs!=null){
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean("THE_USER_SEE_THE_USER_SEE_NEW_FEATURE_HELP1",value);
			editor.commit();
			THE_USER_SEE_THE_USER_SEE_NEW_FEATURE_HELP = value;
		}
		
		
	}
	public static void  set_THE_USER_SEE_THE_USER_SEE_HELP_AVAILABILITY(boolean value){
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("THE_USER_SEE_THE_USER_SEE_HELP_AVAILABILITY",value);
		editor.commit();
		THE_USER_SEE_THE_USER_SEE_HELP_AVAILABILITY = value;
		
		
		
	}
	public static boolean   get_THE_USER_SEE_HELP_ADD_MESSAGE_ON_NEW_PLACES(){
		return  prefs.getBoolean("THE_USER_SEE_HELP_ADD_MESSAGE_ON_NEW_PLACES", false);
	}
	public static void  setDEVICE(String key){
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("device",key);
		editor.commit();
		DEVICE = key;
		
		
	}
	

	public  HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
	

	
	public synchronized Tracker getTracker(TrackerName trackerId) {
	    if (!mTrackers.containsKey(trackerId)) {
	    	
	      GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
	      Tracker t = analytics.newTracker(PROPERTY_ID);
	          
	      mTrackers.put(trackerId, t);

	    }
	    return mTrackers.get(trackerId);
	  }
}
