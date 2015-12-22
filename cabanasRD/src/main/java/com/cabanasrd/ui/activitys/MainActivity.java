package com.cabanasrd.ui.activitys;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map.Entry;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cabanasrd.R;
import com.cabanasrd.config.CabannasrdApp;
import com.cabanasrd.config.CabannasrdApp.TrackerName;
import com.cabanasrd.data.HandlerMotelDataAcces;
import com.cabanasrd.data.HandlerMotelDataAccesRestFul;
import com.cabanasrd.data.MotelHandler;
import com.cabanasrd.data.entities.Motel;
import com.cabanasrd.data.entities.Rating;
import com.cabanasrd.ui.fragments.FragmentSuggestMotel;
import com.cabanasrd.ui.google.maps.Route;
import com.cabanasrd.ui.listeners.MotelsListener;
import com.cabanasrd.ui.tools.UtilUI;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends ActionBarActivity implements  LocationListener,OnQueryTextListener {

	final Context context = this;
	private boolean isMenuOpened  =false;
	private String[] opcionesMenu;
	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private CharSequence tituloSeleccion;
	private CharSequence tituloApp;
	private ActionBarDrawerToggle drawerToogle;
	private int positionFragment;
    private LocationRequest   mLocationRequest = null;
	private ListView lstListado;
	private AdaptadorMotels motelsAdapter;
	private MotelsListener listener;
    public volatile static HashMap<Marker,Motel> motelsMarkers = new HashMap<Marker,Motel>();
    private volatile ArrayList<Motel> motelWithFilters = new ArrayList<Motel>();
    private GoogleMap googleMap = null;
    private SupportMapFragment mMapFragment = null;
    private MotelHandler  motelHandler = null; 
    private Circle circleOptionsGoogleMap = null;
    private Route route = new Route();
    private LatLng userPosition = null;
    private SearchView searchView = null;
    private View detailsLayout = null;
    volatile Motel selectedMotel = null;
    private  boolean istheFirstTimeInThisActivity = true;
    int lastMotelIDSelected = 0;
    private View popupMapView = null;
    private Calendar dateOfLastMarketSelected = null;
    private WebView webViewAds = null;
    
    @Override
   	public boolean onQueryTextChange(String arg0) {
   		return false;
   		
   	}
    
    @Override
    public boolean  onCreateOptionsMenu(Menu menu) {

    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.activity_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_searchItem);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        this.searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        
        if (searchView != null) {
        	searchView.setQueryHint(getText(R.string.search));
        	searchView.setOnQueryTextListener(this);
        	searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(true);
       
        }
        
        
    	return super.onCreateOptionsMenu(menu);
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

    	 if (drawerToogle.onOptionsItemSelected(item)) {
             return true;
    	 }
         
    	switch (item.getItemId()) {
           case R.id.action_share_app:
        		Intent sendIntent = new Intent();
             	sendIntent.setAction(Intent.ACTION_SEND);
             	sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.cabanasrd");
             	sendIntent.setType("text/plain");
             	startActivity(sendIntent);
               return true;

           case R.id.action_create_new_motel:
       			Intent createNewMotel = new Intent(this,FragmentSuggestMotel.class);
       			
       			startActivity(createNewMotel);
              return true;

               
           default:
        	   
               return super.onOptionsItemSelected(item);
           }
    	  


    }
    
    @Override
	public boolean onQueryTextSubmit(String arg0) {
		 InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		searchOnList(arg0);
		return true;
	}
    
    private void changeMapLocation(LatLng latLng, int zoom) {

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        
    }
    
    @Override
    protected void onResume() {
    	
    	super.onResume();
    	
//    	 if (mGoogleApiClient.isConnected() & mLocationRequest !=null && !mRequestingLocationUpdates ) {
//    	        startLocationUpdates();
//    	 }
    	 setUpMap();
         
    }
    protected void createLocationRequest() {
        if(mLocationRequest ==null){ 
	    	mLocationRequest = new LocationRequest();
	        mLocationRequest.setInterval(60000*60);
	        mLocationRequest.setFastestInterval(60000);
	        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        }
    }
    
  
	
    public void initComponents(){
    	motelHandler = MotelHandler.getInstance();
   	 	detailsLayout = findViewById(R.id.detailsLayout);
   	 	       setUpMap();
       
   }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance();
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.map, mMapFragment);
            fragmentTransaction.commit();
            mMapFragment.setRetainInstance(true);
        }

    	popupMapView = getLayoutInflater().inflate(R.layout.custom_info_pop_map_motel, null);
    	 istheFirstTimeInThisActivity = true;

      

        
        
        // Get tracker.
        Tracker t = ((CabannasrdApp) getApplication()).getTracker(
            TrackerName.APP_TRACKER);

        // Set screen name.
        t.setScreenName(MainActivity.class.toString());

        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());
        
        
        
        opcionesMenu = getResources().getStringArray(R.array.nav_drawer_menu_left);
        
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerList = (ListView)findViewById(R.id.left_drawer);
        
        drawerList.setAdapter(new ArrayAdapter<String>(
        		getSupportActionBar().getThemedContext(), 
        		(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)?
        		android.R.layout.simple_list_item_activated_1 : android.R.layout.simple_list_item_checked, 
        		opcionesMenu));
        
        drawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				changeView(position);
				
			}
		});
        
        
        tituloSeleccion = getTitle();
        tituloApp = getTitle();
        
        drawerToogle = new ActionBarDrawerToggle(this, 
        		drawerLayout, 
        		R.string.drawer_open, 
        		R.string.drawer_close){
        	
	        	@Override
				public void onDrawerClosed(View view) {
					getSupportActionBar().setTitle(tituloSeleccion);
					ActivityCompat.invalidateOptionsMenu(MainActivity.this);
					
				}
	        	@Override
				public void onDrawerOpened(View drawerView) {
	        		getSupportActionBar().setTitle(tituloApp);
	        		ActivityCompat.invalidateOptionsMenu(MainActivity.this);
				}
        };
        UtilUI.validateGPSIsDisabledWithMessage(this);
        drawerLayout.setDrawerListener(drawerToogle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        initComponents();
        //Ads setUp
        setUpWebViewAds();
       
        if(!CabannasrdApp.THE_USER_SEE_THE_USER_SEE_NEW_FEATURE_HELP){
        	 LayoutInflater inflater = getLayoutInflater();
            View view =  inflater.inflate(R.layout.view_new_feature_pointers, null);
            
        	UtilUI.showAlertDialog(this, getString(R.string.newFeature),view ,R.string.iGotIt,null);
	    	   CabannasrdApp.set_THE_USER_SEE_THE_USER_SEE_NEW_FEATURE_HELP(true);
	       }

		LayoutInflater inflater = getLayoutInflater();
		View view =  inflater.inflate(R.layout.view_help_us, null);
		UtilUI.showAlertDialog(this, getString(R.string.weNeedYourHelpTitle),view ,R.string.iGotIt,null);
 
    }
    
    private void setUpMap() {
        // Do a null check to confirm that we have not already instantiated the map.

        if (googleMap == null) {

            googleMap = mMapFragment.getMap();

            // Check if we were successful in obtaining the map.
            if (googleMap != null) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setCompassEnabled(true);

                changeMapLocation(new LatLng(18.86471, -71.36719),7);
                googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {

                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        Log.e("mapactualUserPosition", location.getLatitude()+" "+ location.getLongitude());
                        CabannasrdApp.actualUserPosition = latLng;
                        if(userPosition !=null){
                            float []  distance = new float [4];
                            float  distanceBetweenUserAndActualPosition =0;

                            Location.distanceBetween(location.getLatitude(), location.getLongitude(), userPosition.latitude,  userPosition.longitude, distance);
                            distanceBetweenUserAndActualPosition = distance[0];

                            if(distanceBetweenUserAndActualPosition >=10){
                            	
                            	if(istheFirstTimeInThisActivity){
                            		changeMapLocation(latLng, 13);
                            		istheFirstTimeInThisActivity = false;
                            	}
                                drawCircle(latLng);
                            }

                        }else{
                        	if(istheFirstTimeInThisActivity){
                        		changeMapLocation(latLng, 13);
                        		istheFirstTimeInThisActivity = false;
                        	}
                            drawCircle(latLng);
                        }
                        userPosition =latLng;

                    }
                });
                googleMap.setOnMapClickListener(new OnMapClickListener() {
					
					@Override
					public void onMapClick(LatLng arg0) {
						
						if(lstListado!=null){
							detailsLayout.setVisibility(View.GONE);
							
						}
						UtilUI.hideSoftKeyBoard(MainActivity.this);
					}
				});
                googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(final Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(final Marker marker ) {
                    	
	                   	
	                   			
		                   		long howManyMillisSeconHavePassedToTheActivityStartToNow = 0;
	                            final Motel localSelectedMotel =motelsMarkers.get(marker); 
	                            final Marker markerShowingInfoWindow  = marker;
	                            int twoMinutes =  60000 *2;
	                            TextView txtNameMotel = (TextView) popupMapView.findViewById(R.id.txtNameMotel);
	                            TextView txtDescriptionMotel = (TextView) popupMapView.findViewById(R.id.txtDescriptionMotel);
	                            RatingBar   ratingBar = (RatingBar) popupMapView.findViewById(R.id.motel_rating_detail_map);
	                            selectedMotel = localSelectedMotel;
	                            
	                            if(localSelectedMotel.getRating() ==null){
	                            	ratingBar.setVisibility(View.GONE);
	                            }else{
	                            	 ratingBar.setVisibility(View.VISIBLE);
	                            	 ratingBar.setRating((float) localSelectedMotel.getRating().getValue());
	                            }

	                            if(dateOfLastMarketSelected !=null){
	                            	howManyMillisSeconHavePassedToTheActivityStartToNow = (dateOfLastMarketSelected.getTimeInMillis()-System.currentTimeMillis());
	                            }
	                             
	                    	    
	                    		if(  (dateOfLastMarketSelected ==null || (howManyMillisSeconHavePassedToTheActivityStartToNow > twoMinutes || lastMotelIDSelected !=localSelectedMotel.getId())) 
	                    				 && UtilUI.hasConnection(context) ){
	                    			 final HandlerMotelDataAcces dataAcces = new  HandlerMotelDataAccesRestFul();
	                    			 Log.e("popup", "getMotelRating");
	                                 dataAcces.getMotelRating(Integer.parseInt(localSelectedMotel.getId().toString())   , CabannasrdApp.android_id, new Callback<Rating>(){
	
	         							@Override
	         							public void failure(RetrofitError arg0) {
	         									
	         							}
	
	         							@Override
	         							public void success(final Rating rating, Response arg1) {
	         								new   Handler().post(new Runnable() { 
	         							             public void run() { 
	         							            	Log.e("popup", "llego getMotelRating");
	         							            	Log.e("popup", "getMotel");
	         							            	 dataAcces.getMotel(localSelectedMotel.getId().intValue(),  new Callback<Motel>() {
	
	         												@Override
	         												public void failure(RetrofitError arg0) {
	         											
	         												}
	
	         												@Override
	         												public  void success(Motel motelServer, Response arg1) {
	         													Log.e("popup", "llego getMotel");
	         													if(motelServer.getId().equals( localSelectedMotel.getId())  ){
	         												
	         														
	    		     														MotelHandler.getInstance().remplaceMotel(localSelectedMotel,motelServer,context);
	    		         													motelsMarkers.remove(localSelectedMotel);
	    		         													motelsMarkers.put(marker,motelServer );
	    		
	    		         													float []  distance = new float [4];
	    		         													float  distanceBetweenUserAndActualSelectedMotel =0;
	    		         													Location.distanceBetween(marker.getPosition().latitude, marker.getPosition().longitude, motelServer.getLatitude(),  motelServer.getLongitude(), distance);
	    		         						                            distanceBetweenUserAndActualSelectedMotel = distance[0];
	    		         						                            
	    		         						                            if(distanceBetweenUserAndActualSelectedMotel > 0){
	    		         						                            	LatLng newLatLng = new LatLng(motelServer.getLatitude(), motelServer.getLongitude());
	    		         						                            	marker.setPosition(newLatLng);
	    		         						                            	changeMapLocation(newLatLng, 17);
	    		         						                            }
	    		         						                    
	    		         						                            if(motelServer.getType() != localSelectedMotel.getType()){
	 	    		         						                           
	 		    		         						         	        	 switch (motelServer.getType()) {
	 		    		         						         				case CabannasrdApp.MOTEL_TYPE_CABANA:
	 		    		         						         					marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_google_map_marker));
	 		    		         						         					break;
	 		    		         						         					
	 		    		         						         				case CabannasrdApp.MOTEL_TYPE_HOTEL:
	 		    		         						         					marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_google_map_marker2));
	 		    		         						         					break;
	 		    		         						         					
	 		    		         						         				
	 		    		         						         				}
	 		    		         						         	        	if(!marker.isInfoWindowShown()){
	 		    		         						         	        		marker.showInfoWindow();
	 		    		         						         	        	}
	    		         						                           }

	    		         													
	    		         													motelServer.setRating(rating);
	    		         													lastMotelIDSelected = Integer.valueOf(""+motelServer.getId());
	    		         													dateOfLastMarketSelected =   Calendar.getInstance();                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
	    																
	    		     													if(popupMapView !=null){
	    		     														MainActivity.this.runOnUiThread(new Runnable() {
	    			     														
	    			     														@Override
	    			     														public void run() {
	    			     															if (markerShowingInfoWindow != null && markerShowingInfoWindow.isInfoWindowShown()) {
	    			     																Log.e("popup", " showInfoWindow");
	    			     																markerShowingInfoWindow.showInfoWindow();
	    			     																
	    			     											        		}
	    			     														}
	    			     													});
	    		     													}
	         													
	         												}else{
	         													Log.i("arturo", " fallo _>>> oldMotel "+localSelectedMotel.getId() +" newMotel " +motelServer.getId());
	         												}
	         												}
	         											} );
	         							            	
	         												
	         							             } 
	         							        });
	         								
	         							}});
	                    	    }
	                            
	                           
	                            float []  distance = new float [4];
	                            float  distanceBetweenUserAndActualSelectedMotel =0;
	                            String strdistanceBetweenUserAndActualSelectedMotelInKMORM ="";
	                            
	                            if(marker!=null && userPosition !=null ){
	                                Location.distanceBetween(marker.getPosition().latitude, marker.getPosition().longitude, userPosition.latitude,  userPosition.longitude, distance);
	                                distanceBetweenUserAndActualSelectedMotel = distance[0];
	                           
	                                if(distanceBetweenUserAndActualSelectedMotel > 1000){
	            						distanceBetweenUserAndActualSelectedMotel *=0.001;
	            						strdistanceBetweenUserAndActualSelectedMotelInKMORM =  String.format("%,.1f", distanceBetweenUserAndActualSelectedMotel) +" "+ context.getString(R.string.km) ;
	            					}else{
	            						strdistanceBetweenUserAndActualSelectedMotelInKMORM = String.format("%,.1f", distanceBetweenUserAndActualSelectedMotel)+" "+context.getString(R.string.meters);
	            					}
	                                strdistanceBetweenUserAndActualSelectedMotelInKMORM =  " ("+strdistanceBetweenUserAndActualSelectedMotelInKMORM+")";
	                            }
	
	                            txtNameMotel.setText(localSelectedMotel.getName() +strdistanceBetweenUserAndActualSelectedMotelInKMORM );
	                            txtDescriptionMotel.setText(localSelectedMotel.toString());

	                            if(  userPosition !=null && lastMotelIDSelected !=localSelectedMotel.getId()){
	                                ArrayList<LatLng> points  =  new ArrayList<LatLng>() ;
	                                points.add(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
	                                points.add(userPosition);
	                                route.drawRoute(googleMap,MainActivity.this,points,"",true);
	                            }
	                    	
                        return popupMapView;



                    }
                });
                
                setUpMotelMarkers();

                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                    @Override
                    public void onInfoWindowClick(Marker marker) {
                    	MotelDetailActivity.motel =  selectedMotel;
                    	Intent intent = new Intent(MainActivity.this,MotelDetailActivity.class);
                    	MainActivity.this.startActivity(intent);
                    }
                });

            }
        }
    }
    
    private void drawCircle(LatLng position){
        if(circleOptionsGoogleMap ==null) {
            double radiusInMeters = 2000.0;
            int strokeColor = 0xffff0000; //red outline
            int shadeColor = 0x44ff0000; //opaque red fill
            circleOptionsGoogleMap =   googleMap.addCircle(new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(4));
        }else{
            circleOptionsGoogleMap.setCenter(position);
        }


    }
    
    @Override
    protected void onStart() {
    	GoogleAnalytics.getInstance(this).reportActivityStart(this);
    	super.onStart();

    	
    	
    }
    @Override
    protected void onStop() {
//    	 if (mGoogleApiClient.isConnected() ) {
//    		 mGoogleApiClient.disconnect();
//    	 }
    	super.onStop();
    }
    @Override
    public void onBackPressed() {
        if(isMenuOpened ){
            drawerLayout.closeDrawers();
            isMenuOpened  =false;
        }else if(positionFragment != 0){
        	changeView(0);
        }
        else {
            super.onBackPressed();
        }
    }

      private void changeView(int position){
		switch (position) {
		case 0:

			break;	
		case 1:
			Intent intent1 = new Intent(MainActivity.this, AboutUsActivity.class);
			startActivity(intent1);
			positionFragment = 1;
			break;
		case 2:
			
			startActivity(new Intent(MainActivity.this, ContactUsActivity.class));
			positionFragment = 2;
			
			break;
	
		case 3:
			Intent intent;
			intent = new Intent(MainActivity.this, FragmentSuggestMotel.class);
			startActivity(intent);
			break;
			
			
		}

		if(position==0){
			drawerList.setItemChecked(position, true);
			tituloSeleccion = opcionesMenu[position];
			getSupportActionBar().setTitle(tituloSeleccion);
		}else{
			drawerList.setItemChecked(position, false);
		}
		
		drawerLayout.closeDrawer(drawerList);
    }
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		boolean menuAbierto = drawerLayout.isDrawerOpen(drawerList);
		 
		MenuItem  searchItem  = menu.findItem(R.id.action_searchItem);
		if(menuAbierto){ 
			isMenuOpened = true;
			getSupportActionBar().setTitle(R.string.drawer_open);
			if(searchItem!=null){
				searchItem.setVisible(false);
			}
			
		}else {
			isMenuOpened = false;
			if(searchItem!=null){
				searchItem.setVisible(true);
			}
		}
		return super.onPrepareOptionsMenu(menu);
		
	}
	
	@Override
	public void onPostCreate(Bundle savedInstanceState){
		super.onPostCreate(savedInstanceState);
		drawerToogle.syncState();
	}
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		drawerToogle.onConfigurationChanged(newConfig);
		
	}

	@Override
	public void onLocationChanged(Location location) {
		if(location!=null){
			LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
			CabannasrdApp.actualUserPosition =latLng;
			Log.e("LocationServices", location.getLatitude()+" "+ location.getLongitude());
		}
		Log.e("LocationServices", location.getLatitude()+" "+ location.getLongitude());
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	  public void addMotelsToMap(Motel ... motels){
	        for(Motel motel : motels ){
	        	MarkerOptions marker = new MarkerOptions().position(new LatLng(motel.getLatitude(), motel.getLongitude()));
	            googleMap.addMarker(marker);
	            
	        }
	    	
	    }
	    public void setUpMotelMarkers(){
	        for( Motel motel : motelHandler.getAllMotels()){
	        	MarkerOptions marker = new MarkerOptions().position(new LatLng(motel.getLatitude(), motel.getLongitude())).title(motel.getName())
	            .snippet(motel.toString());
	        	 Marker marker2  = googleMap.addMarker(marker);
	        	 //motel.getType()
	        	 marker2.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_google_map_marker));

				if(motel.isManagedByTheOwner()){
					switch (motel.getType()) {
						case CabannasrdApp.MOTEL_TYPE_CABANA:
							marker2.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_google_map_marker));
							break;

						case CabannasrdApp.MOTEL_TYPE_HOTEL:
							marker2.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_google_map_marker2));
							break;


					}
				}else{
					marker2.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_google_map_marker_no_color));
				}


	        	 motelsMarkers.put(marker2, motel);
	        }
	    }
	    
	   private Marker getSeletedMotelFromMarket( Motel motel){
		   Marker marker  = null;
		    for (Entry<Marker,Motel > entry : motelsMarkers.entrySet()) {
		        if (motel.equals(entry.getValue())) {
		        	marker = entry.getKey();
		        }
		    }
			return  marker;
	   }
	   
	   @Override
	protected void onPause() {
		   
		super.onPause();
	}
	   
	    //ListView Methods...
	    public void setCorreosListener(MotelsListener l) {
			this.listener = l;
		}

	

	



	 class AdaptadorMotels extends ArrayAdapter<Motel> {
		 
         Activity context;

         AdaptadorMotels(Activity context, ArrayList<Motel> moteles) {
             super(context, R.layout.listitem_motel, moteles);
             this.context = context;
         }

         @Override
			public View getView(int position, View convertView, ViewGroup parent) {
         	
         	View item = convertView;
             ViewHolder holder;
             Motel motel = motelWithFilters.get(position);  
             if(item == null){
             	LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		            item = inflater.inflate(R.layout.listitem_motel, null);
		            holder = new ViewHolder();
		            ImageView image_item_search = (ImageView)  item.findViewById(R.id.image_item_search);
		            
			       	 switch (motel.getType()) {
	    				case CabannasrdApp.MOTEL_TYPE_CABANA:
	    					image_item_search.setImageResource(R.drawable.ic_google_map_marker);
	    					
	    					break;
	    					
	    				case CabannasrdApp.MOTEL_TYPE_HOTEL:
	    					image_item_search.setImageResource(R.drawable.ic_google_map_marker2);
	    					
	    					break;
	    					
	    				
	    				}
		            holder.nombre = (TextView)item.findViewById(R.id.LblNombre);
		            holder.descripcion = (TextView)item.findViewById(R.id.LblDescripcion);
		            holder.image_item_search = image_item_search;
		            item.setTag(holder);
             }
             else
             {
                 holder = (ViewHolder)item.getTag();
             }
           
             holder.nombre.setText(motel.getName());
             holder.descripcion.setText(motel.toString().replaceAll("\n", " || "));
             if(holder.image_item_search !=null){
            	 switch (motel.getType()) {
 				case CabannasrdApp.MOTEL_TYPE_CABANA:
 					holder.image_item_search.setImageResource(R.drawable.ic_google_map_marker);
 					
 					break;
 					
 				case CabannasrdApp.MOTEL_TYPE_HOTEL:
 					holder.image_item_search.setImageResource(R.drawable.ic_google_map_marker2);
 					
 					break;
 					
 				
 				}
             }
            
	 
	            return(item);
     }
     }
 
 static class ViewHolder {
     TextView nombre;
     TextView descripcion;
     ImageView image_item_search;
 }
 
 public void setUpListView(){
 	//ListView Init
     lstListado = (ListView) findViewById(R.id.LstListado);
     motelsAdapter = new AdaptadorMotels(this, motelWithFilters);
     lstListado.setAdapter(motelsAdapter);
     
     lstListado.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(listener != null){
					
					listener.onMotelSeleccionado((Motel)lstListado.getAdapter().getItem(position));
					 
                  
				}
				detailsLayout.setVisibility(View.GONE);
				Motel motel =(Motel)lstListado.getAdapter().getItem(position);
				Marker marker = getSeletedMotelFromMarket(motel);
				marker.showInfoWindow();
				changeMapLocation(new LatLng(motel.getLatitude(), motel.getLongitude()), 17);
				searchView.setIconified(true);
				searchView.setIconified(true);
			}
		});
     //En listView init
 	
 }
 public void searchOnList(String query){
 	//List Filter Implementation
 	if(motelsAdapter == null){
 		setUpListView();
 	}
 	
 	
 	motelsAdapter.clear();
     motelsAdapter.addAll(motelHandler.searchMotelOrHotelByPattern(query));
     if(!motelsAdapter.isEmpty()){
     	detailsLayout.setVisibility(View.VISIBLE);
     }else{
     	detailsLayout.setVisibility(View.GONE);
     	UtilUI.showAlertDialog(this, getString(R.string.information), getString(R.string.msErrorNoDataFound), R.string.ok, null);
     }
     ((BaseAdapter) lstListado.getAdapter()).notifyDataSetChanged();	
         
     
 	
 }

	public void setUpWebViewAds() {
		webViewAds = (WebView) findViewById(R.id.webviewAds);
		webViewAds.getSettings().setJavaScriptEnabled(true);
		webViewAds.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webViewAds.getSettings().setSupportMultipleWindows(true);
		String url = "file:///android_asset/ads.html";
		webViewAds.loadUrl(url);
		webViewAds.setWebChromeClient(new MyWebChromeclient());

	}
	private class MyWebChromeclient extends WebChromeClient {


		@Override
		public boolean onCreateWindow(WebView view, boolean isDialog,
									  boolean isUserGesture, Message resultMsg) {

			WebView newWebView = new WebView(MainActivity.this);
			view.addView(newWebView);
			WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
			transport.setWebView(newWebView);
			resultMsg.sendToTarget();

			newWebView.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					Intent browserIntent = new Intent(Intent.ACTION_VIEW);
					browserIntent.setData(Uri.parse(url));
					startActivity(browserIntent);
					return true;
				}
			});
			return true;
		}
	}
}
