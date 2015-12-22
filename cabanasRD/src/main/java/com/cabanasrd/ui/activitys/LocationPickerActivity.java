
package com.cabanasrd.ui.activitys;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.cabanasrd.R;
import com.cabanasrd.config.CabannasrdApp;
import com.cabanasrd.config.CabannasrdApp.TrackerName;
import com.cabanasrd.ui.tools.UtilUI;
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


public class LocationPickerActivity extends ActionBarActivity 	 {
	
	
	
	   private GoogleMap googleMap = null;
	   private SupportMapFragment mMapFragment = null;
	   private Marker selectedMarker = null;
	   private LatLng userPosition = null;
	   private TextView txtAddress = null;
	   private Circle circleOptionsGoogleMap = null;
	   private 	String address = null;
	   private boolean isTheFirstTime = false;
	
	 protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	     
	
	        setContentView(R.layout.activity_location_picker_map);
	        
	        if (mMapFragment == null) {
	            mMapFragment = SupportMapFragment.newInstance();
	            FragmentTransaction fragmentTransaction =
	                    getSupportFragmentManager().beginTransaction();
	            fragmentTransaction.add(R.id.map, mMapFragment);
	            fragmentTransaction.commit();
	            
	        }
	        txtAddress = (TextView )findViewById(R.id.txtAddress);
	        
	        Button btnSelectPosition  = (Button )findViewById(R.id.btnSelectPosition);
	        btnSelectPosition.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent result = new Intent();
					

					
					result.putExtra("address", txtAddress.getText());
					result.putExtra("userPosition", selectedMarker.getPosition());
					
					setResult(RESULT_OK, result);
					finish();
					
				}
			});
	        
	        setUpMap();
	        
	        // Get tracker.
	        Tracker t = ((CabannasrdApp)  getApplication()).getTracker(
	            TrackerName.APP_TRACKER);

	        // Set screen name.
	        t.setScreenName(LocationPickerActivity.class.toString());

	        // Send a screen view.
	        t.send(new HitBuilders.AppViewBuilder().build());
	        
	        
	      
	     
			
		}
	 
	 @Override
		public boolean onCreateOptionsMenu(Menu menu) {
			
			getMenuInflater().inflate(R.menu.activity_pick_location, menu);
			return super.onCreateOptionsMenu(menu);
		}
	 @Override
     public boolean onOptionsItemSelected(MenuItem item) {
             switch (item.getItemId()) {
             case android.R.id.home:
                 finish(); 
                 break; 
             case R.id.action_help_picky_location:
            	 UtilUI.showAlertDialog(this, getString(R.string.help),getString(R.string.selectPositionHelp) ,R.string.iGotIt,null);
              	
              	
              	
               break;
        }
             
             return true;
     }
	    private void changeMapLocation(LatLng latLng, int zoom) {

	        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
	        
	    }
	    @Override
	    public void onResume() {
	        super.onResume();
	        setUpMap();
	    }
	  private void setUpMap() {
	        // Do a null check to confirm that we have not already instantiated the map.

	        if (googleMap == null) {

	            googleMap = mMapFragment.getMap();

	            // Check if we were successful in obtaining the map.
	            if (googleMap != null) {
	            	LatLng dominicanRepublicLatLng = new LatLng(18.86471, -71.36719);
	                googleMap.setMyLocationEnabled(true);
	                googleMap.getUiSettings().setCompassEnabled(true);
	                MarkerOptions marker = new MarkerOptions().position(dominicanRepublicLatLng);
                    selectedMarker =  googleMap.addMarker(marker);
                    selectedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_google_map_marker));
	                changeMapLocation(dominicanRepublicLatLng,7);
	                googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
	                    @Override
	                    public void onMyLocationChange(Location location) {

	                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
	                        


	                        userPosition =latLng;
	                        

	                    }
	                });
	                googleMap.setOnMapClickListener(new OnMapClickListener() {
						
						@Override
						public void onMapClick(LatLng clickedLat) {
							
							if(selectedMarker !=null){
								selectedMarker.setPosition(clickedLat);
								changeMapLocation(clickedLat, 17);
								setAddressText();
							}
							
						}
					});
	              
	                googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
	                    @Override
	                    public void onMyLocationChange(Location location) {

	                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
	                        if(userPosition !=null){
	                            float []  distance = new float [4];
	                            float  distanceBetweenUserAndActualPosition =0;

	                            Location.distanceBetween(location.getLatitude(), location.getLongitude(), userPosition.latitude,  userPosition.longitude, distance);
	                            distanceBetweenUserAndActualPosition = distance[0];

	                            if(distanceBetweenUserAndActualPosition >=10){
	                                drawCircle(latLng);
	                            }

	                        }else{
	                            drawCircle(latLng);
	                            setAddressText();
	                            
	                        }
	                        if(!isTheFirstTime && selectedMarker !=null){
	                        	selectedMarker.setPosition(latLng);
								changeMapLocation(latLng, 17);
								setAddressText();
	                        
	                        	isTheFirstTime =true;
	                        }

	                        userPosition =latLng;

	                    }
	                });
	                
	                

	                

	            }
	        }
	    }
	  
	  
	  private void setAddressText(){
		  
		  final LatLng latLng = 	  selectedMarker.getPosition();
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					List<Address> addresses;
					Geocoder geocoder = new Geocoder(LocationPickerActivity.this, Locale.getDefault());
					
					
					try {
						addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
					
						address= 	addresses.get(0).getAddressLine(0) +" "+addresses.get(0).getAddressLine(1);
						
						
					} catch (IOException e) {
						e.printStackTrace();
					}

					LocationPickerActivity.this.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							txtAddress.setVisibility(View.VISIBLE);
							if(address!=null){
								txtAddress.setText(address);
							}else{
								txtAddress.setText("Lat "+selectedMarker.getPosition().latitude +"Lon "+selectedMarker.getPosition().longitude);
							}
							
						}
					});
				  
					
				}
			}).start();
		  
			
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
	   

	

	
	
}
