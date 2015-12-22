package com.cabanasrd.ui.fragments;

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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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
import com.cabanasrd.ui.activitys.MotelDetailActivity;
import com.cabanasrd.ui.google.maps.Route;
import com.cabanasrd.ui.listeners.MotelsListener;
import com.cabanasrd.ui.tools.UtilUI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
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



public class FragmentFindMotelOrHotelMap extends Fragment implements LocationListener, OnQueryTextListener {
	
	private ListView lstListado;
	private AdaptadorMotels motelsAdapter;
	private MotelsListener listener;
    public volatile static HashMap<Marker,Motel> motelsMarkers = new HashMap<Marker,Motel>();
    private volatile ArrayList<Motel> motelWithFilters = new ArrayList<Motel>();
    private View rootView = null;
    private GoogleMap googleMap = null;
    private SupportMapFragment mMapFragment = null;
    private MotelHandler  motelHandler = null; 
    private Circle circleOptionsGoogleMap = null;
    private Route route = new Route();
    private LatLng userPosition = null;
    private SearchView searchView = null;
    private View detailsLayout = null;
    private AdView adView;
    volatile Motel selectedMotel = null;
    private  boolean istheFirstTimeInThisActivity = true;
    int lastMotelIDSelected = 0;
    private Activity activityHost = null;
    private LayoutInflater inflaterRoot = null;
    private View popupMapView = null;
    private boolean markerPresed = false;
    private Calendar dateOfLastMarketSelected = null;
    @Override
    public void onAttach(Activity activity) {
    	if(activityHost==null){
    		activityHost = activity;
    	}
    	Log.e("arturo2", "onAttach");
    	Log.e("arturo2", "isAdded " +FragmentFindMotelOrHotelMap.this.isAdded());
    	super.onAttach(activity);
    }
	
    
    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CabannasrdApp.actualUserPosition = latLng;
            
        }

    }

    @Override
	public boolean onQueryTextChange(String arg0) {
		return false;
		
	}
    
    @Override
	public boolean onQueryTextSubmit(String arg0) {
		 InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
		searchOnList(arg0);
		return true;
	}
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    		
    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void changeMapLocation(LatLng latLng, int zoom) {

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        
    }


    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    	 // Inflate the menu; this adds items to the action bar if it is present.
    	inflater.inflate(R.menu.activity_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_searchItem);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        this.searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        
        if (searchView != null) {
        	searchView.setQueryHint(getActivity().getText(R.string.search));
        	searchView.setOnQueryTextListener(this);
        	searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setIconifiedByDefault(true);
       
        }
        
        
    	super.onCreateOptionsMenu(menu, inflater);
    }
    


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

    	   switch (item.getItemId()) {
           case R.id.action_share_app:
        		Intent sendIntent = new Intent();
             	sendIntent.setAction(Intent.ACTION_SEND);
             	sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.cabanasrd");
             	sendIntent.setType("text/plain");
             	startActivity(sendIntent);
               return true;

           case R.id.action_create_new_motel:
       			Intent createNewMotel = new Intent(FragmentFindMotelOrHotelMap.this.getActivity(),FragmentSuggestMotel.class);
       			
       			startActivity(createNewMotel);
              return true;

               
           default:
        	   
               return super.onOptionsItemSelected(item);
           }

    }
   
  @Override
public void onCreate(Bundle savedInstanceState) {
      if (mMapFragment == null) {
          mMapFragment = SupportMapFragment.newInstance();
          FragmentTransaction fragmentTransaction =
                  getChildFragmentManager().beginTransaction();
          fragmentTransaction.add(R.id.map, mMapFragment);
          fragmentTransaction.commit();
          setHasOptionsMenu(true) ;
      }
	super.onCreate(savedInstanceState);
}
    @Override
    public View onCreateView(LayoutInflater inflater,
                              ViewGroup container,  Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	inflaterRoot = inflater;
    	popupMapView = inflater.inflate(R.layout.custom_info_pop_map_motel, null);
    	Log.e("arturo2", "onCreateView");
    	Log.e("arturo2", "isAdded " +FragmentFindMotelOrHotelMap.this.isAdded());
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_motels_map, container, false);
        }
        istheFirstTimeInThisActivity = true;
        
        
        //TODO: GPS VALIDATION
        
        
//       UtilUI.validateGPSIsDisabledWithMessage(((LocationManager) this.getActivity()
//    			.getSystemService(
//    					Activity.LOCATION_SERVICE)), this, this.getActivity());
//       
        
        // Get tracker.
        Tracker t = ((CabannasrdApp) getActivity(). getApplication()).getTracker(
            TrackerName.APP_TRACKER);

        // Set screen name.
        t.setScreenName(FragmentFindMotelOrHotelMap.class.toString());

        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());
        
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("arturo2", "onActivityCreated");
        Log.e("arturo2", "isAdded " +FragmentFindMotelOrHotelMap.this.isAdded());
        initComponents();
        
    }
    @Override
    public void onDestroyView() {
    	Log.e("arturo2", "onDestroyView");
    	Log.e("arturo2", "isAdded " +FragmentFindMotelOrHotelMap.this.isAdded());
    	super.onDestroyView();
    }
    
    
    
@Override
public void onDetach() {
	Log.e("arturo2", "onDestroyView");
	Log.e("arturo2", "isAdded " +FragmentFindMotelOrHotelMap.this.isAdded());
	
	
	super.onDetach();
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
						
						UtilUI.hideSoftKeyBoard(FragmentFindMotelOrHotelMap.this.getActivity());
					}
				});
                googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(final Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(final Marker marker ) {
                    	Log.e("arturo2", "isAdded " +FragmentFindMotelOrHotelMap.this.isAdded()+ " popupMapView "+ popupMapView);
                    	View view = null;
                    
                    	if(!FragmentFindMotelOrHotelMap.this.isAdded()){
                    		Log.e("arturo2", "executePendingTransactions  " );
                    		markerPresed =  true;
								
						}
                    	
                    	
                    	
                   	if(popupMapView!=null){
                    		
                        	//LayoutInflater inflater = (LayoutInflater) FragmentFindMotelOrHotelMap.this.getActivity().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                             view =popupMapView ; // inflater.inflate(R.layout.custom_info_pop_map_motel, null);
                            TextView txtNameMotel = (TextView) view.findViewById(R.id.txtNameMotel);
                            TextView txtDescriptionMotel = (TextView) view.findViewById(R.id.txtDescriptionMotel);
                            RatingBar   ratingBar = (RatingBar) view.findViewById(R.id.motel_rating_detail_map);
                            
                            selectedMotel = motelsMarkers.get(marker);
                            final Motel localSelectedMotel =selectedMotel; 
                            final Marker markerShowingInfoWindow  = marker;
                            
                            if(localSelectedMotel.getRating() ==null){
                            	ratingBar.setVisibility(View.GONE);
                            }else{
                            	 
                            	 ratingBar.setVisibility(View.VISIBLE);
                            	 ratingBar.setRating((float) localSelectedMotel.getRating().getValue());
                            }
                            
                            
                            
                            long howManyMillisSeconHavePassedToTheActivityStartToNow = 0;
                            if(dateOfLastMarketSelected !=null){
                            	howManyMillisSeconHavePassedToTheActivityStartToNow = (dateOfLastMarketSelected.getTimeInMillis()-System.currentTimeMillis());
                            }
                             
                    		
                    		int twoMinutes =  60000 *2;
                    	    
                    		if(  (dateOfLastMarketSelected ==null || (howManyMillisSeconHavePassedToTheActivityStartToNow > twoMinutes || lastMotelIDSelected !=localSelectedMotel.getId())) 
                    				 && UtilUI.hasConnection(CabannasrdApp.context) ){
                    			 final HandlerMotelDataAcces dataAcces = new  HandlerMotelDataAccesRestFul();
                    			 
                                 dataAcces.getMotelRating(Integer.parseInt(localSelectedMotel.getId().toString())   , CabannasrdApp.android_id, new Callback<Rating>(){

         							@Override
         							public void failure(RetrofitError arg0) {
         									
         							}

         							@Override
         							public void success(final Rating rating, Response arg1) {
         								new   Handler().post(new Runnable() { 
         							             public void run() { 
         							            	 
         							            	 dataAcces.getMotel(localSelectedMotel.getId().intValue(),  new Callback<Motel>() {

         												@Override
         												public void failure(RetrofitError arg0) {
         											
         												}

         												@Override
         												public  void success(Motel motelServer, Response arg1) {
         													if(motelServer.getId().equals( localSelectedMotel.getId())  ){
         												
    		     														MotelHandler.getInstance().remplaceMotel(localSelectedMotel,motelServer,CabannasrdApp.context);
    		         													motelsMarkers.remove(localSelectedMotel);
    		         													motelsMarkers.put(marker,motelServer );
    		
    		         													float []  distance = new float [4];
    		         													float  distanceBetweenUserAndActualSelectedMotel =0;
    		         													Location.distanceBetween(marker.getPosition().latitude, marker.getPosition().longitude, motelServer.getLatitude(),  motelServer.getLongitude(), distance);
    		         						                            distanceBetweenUserAndActualSelectedMotel = distance[0];
    		         						                            
    		         						                            if(distanceBetweenUserAndActualSelectedMotel > 0){
    		         						                            	marker.setPosition(new LatLng(motelServer.getLatitude(), motelServer.getLongitude()));
    		         						                            }
    		         						                            
    		         													
    		         													
    		         													motelServer.setRating(rating);
    		         													lastMotelIDSelected = Integer.valueOf(""+motelServer.getId());
    		         													dateOfLastMarketSelected =   Calendar.getInstance();                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
    																
    		     													if(popupMapView !=null){
    		     														popupMapView.post(new Runnable() {
    			     														
    			     														@Override
    			     														public void run() {
    			     															if (markerShowingInfoWindow != null && markerShowingInfoWindow.isInfoWindowShown()) {

    			     																
    			     																
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
            						strdistanceBetweenUserAndActualSelectedMotelInKMORM =  String.format("%,.1f", distanceBetweenUserAndActualSelectedMotel) +" "+ CabannasrdApp.context.getString(R.string.km) ;
            					}else{
            						strdistanceBetweenUserAndActualSelectedMotelInKMORM = String.format("%,.1f", distanceBetweenUserAndActualSelectedMotel)+" "+CabannasrdApp.context.getString(R.string.meters);
            					}
                                strdistanceBetweenUserAndActualSelectedMotelInKMORM =  " ("+strdistanceBetweenUserAndActualSelectedMotelInKMORM+")";
                            }
                            
                            
        					
        					
                            

                            txtNameMotel.setText(localSelectedMotel.getName() +strdistanceBetweenUserAndActualSelectedMotelInKMORM );
                            txtDescriptionMotel.setText(localSelectedMotel.toString());
                            
                           

                            
                            if(  userPosition !=null && lastMotelIDSelected !=localSelectedMotel.getId()){
                                ArrayList<LatLng> points  =  new ArrayList<LatLng>() ;
                                points.add(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
                                points.add(userPosition);

                                route.drawRoute(googleMap,FragmentFindMotelOrHotelMap.this.getActivity(),points,"",true);
                            }
                    	}
                    	
                    
                        return view;



                    }
                });
                
                setUpMotelMarkers();

                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                    @Override
                    public void onInfoWindowClick(Marker marker) {
                       
                    	
                    	
                    	MotelDetailActivity.motel =  selectedMotel;
                    	Intent intent = new Intent(FragmentFindMotelOrHotelMap.this.getActivity(),MotelDetailActivity.class);
                    	FragmentFindMotelOrHotelMap.this.getActivity().startActivity(intent);
//                        if(marker !=null){
//                            LatLng latLng = new LatLng(marker.getPosition().latitude, (marker.getPosition().longitude));
//                            changeMapLocation(latLng,17);
//                        }

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



            circleOptionsGoogleMap =   googleMap.addCircle(new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8));
        }else{

            circleOptionsGoogleMap.setCenter(position);
        }


    }
    
    @Override
    public void onResume() {
        super.onResume();
        setUpMap();
        adView.resume();

    }
    @Override
    public void onDestroy() {
    	 super.onDestroy();
    	adView.destroy();
       
    }
 
    public void addMotelsToMap(Motel ... motels){
        for(Motel motel :motels ){
        	MarkerOptions marker = new MarkerOptions().position(new LatLng(motel.getLatitude(), motel.getLongitude()));
            googleMap.addMarker(marker);
            
        }
    	
    }
    public void setUpMotelMarkers(){
        for( Motel motel : motelHandler.getAllMotels()){
        	MarkerOptions marker = new MarkerOptions().position(new LatLng(motel.getLatitude(), motel.getLongitude())).title(motel.getName())
            .snippet(motel.toString());
        	 Marker marker2  = googleMap.addMarker(marker);
        	 marker2.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_google_map_marker));
        	 motelsMarkers.put(marker2,motel);
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
 
    public void initComponents(){


    	 motelHandler = MotelHandler.getInstance();
    	 detailsLayout = getActivity().findViewById(R.id.detailsLayout);

        adView = (AdView)FragmentFindMotelOrHotelMap.this.getActivity().findViewById(R.id.adView);


        AdRequest adRequest = new AdRequest.Builder().build();

        adView.loadAd(adRequest);
        setUpMap();
        
    }
    @Override
    public void onPause() {
    	 super.onPause();
    	adView.pause();
       
    }


    //ListView Methods...
    public void setCorreosListener(MotelsListener l) {
		this.listener = l;
	}
    
    class AdaptadorMotels extends ArrayAdapter<Motel> {
 
            Activity context;
 
            AdaptadorMotels(Fragment context, ArrayList<Motel> moteles) {
                super(context.getActivity(), R.layout.listitem_motel, moteles);
                this.context = context.getActivity();
            }
 
            @Override
			public View getView(int position, View convertView, ViewGroup parent) {
            	
            	View item = convertView;
                ViewHolder holder;
	            
                if(item == null){
                	LayoutInflater inflater = (LayoutInflater) FragmentFindMotelOrHotelMap.this.getActivity().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		            item = inflater.inflate(R.layout.listitem_motel, null);
		            
		            holder = new ViewHolder();
		            holder.nombre = (TextView)item.findViewById(R.id.LblNombre);
		            holder.descripcion = (TextView)item.findViewById(R.id.LblDescripcion);
		            item.setTag(holder);
                }
                else
                {
                    holder = (ViewHolder)item.getTag();
                }
                Motel motel = motelWithFilters.get(position);
                
                holder.nombre.setText(motel.getName());
	 
	            holder.descripcion.setText(motel.toString().replaceAll("\n", " || "));
	 
	            return(item);
        }
        }
    
    static class ViewHolder {
        TextView nombre;
        TextView descripcion;
    }
    
    public void setUpListView(){
    	//ListView Init
        lstListado = (ListView)getView().findViewById(R.id.LstListado);
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
				
					if(!isAdded()){
						getFragmentManager().executePendingTransactions();
					}else{
						marker.showInfoWindow();
					}
				changeMapLocation(new LatLng(motel.getLatitude(), motel.getLongitude()), 17);
				searchView.setIconified(true);
				searchView.setIconified(true);
				if(markerPresed){
					Log.e("arturo2", "@isAdded " + isAdded());
					getFragmentManager().executePendingTransactions();
					marker.showInfoWindow();
					markerPresed =false;
				}			
			}
		});
        //En listView init
    	
    }
    
    
    //End - ListView Methods
    
//    toggleVisibityMitemShare();
    
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
        	UtilUI.showAlertDialog(this.getActivity(), getString(R.string.information), getString(R.string.msErrorNoDataFound), R.string.ok, null);
        }
        ((BaseAdapter) lstListado.getAdapter()).notifyDataSetChanged();	
            
        
    	
    }
} 
