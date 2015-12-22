

package com.cabanasrd.ui.activitys;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.ocpsoft.prettytime.PrettyTime;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cabanasrd.R;
import com.cabanasrd.config.CabannasrdApp;
import com.cabanasrd.config.CabannasrdApp.TrackerName;
import com.cabanasrd.data.HandlerMotelDataAcces;
import com.cabanasrd.data.HandlerMotelDataAccesRestFul;
import com.cabanasrd.data.entities.Availability;
import com.cabanasrd.data.entities.Motel;
import com.cabanasrd.data.entities.UserVote;
import com.cabanasrd.ui.tools.UtilUI;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;



public class MotelShowAvailabilityActivity extends ActionBarActivity  implements ConnectionCallbacks, OnConnectionFailedListener	 {
	
	final Context context = this;
	public  static Motel motel = null;
	RatingBar ratingBar;
	private HandlerMotelDataAcces dataAcces = null;
	private ProgressDialog progress = null;
	private String reason = "";
    private LinearLayout linearAvailabilities = null;
    private PrettyTime prettyTime =  null;
    LayoutInflater inflaterItem = null;
    Builder optionsBuilder;
	DisplayImageOptions options;
	View lasView = null;
	private GoogleApiClient  mGoogleApiClient  = null;
	// Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

	 @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        if (requestCode == REQUEST_RESOLVE_ERROR) {
	            mResolvingError = false;
	            if (resultCode == RESULT_OK) {
	                // Make sure the app is not already connected or attempting to connect
	                if (!mGoogleApiClient.isConnecting() &&
	                        !mGoogleApiClient.isConnected()) {
	                    mGoogleApiClient.connect();
	                }
	            }
	        }
	    }
	 /* Called from ErrorDialogFragment when the dialog is dismissed. */
	   public void onDialogDismissed() {
	       mResolvingError = false;
	   }

	   /* A fragment to display an error dialog */
	   public static class ErrorDialogFragment extends DialogFragment {
	       public ErrorDialogFragment() { }

	       @Override
	       public Dialog onCreateDialog(Bundle savedInstanceState) {
	           // Get the error code and retrieve the appropriate dialog
	           int errorCode = this.getArguments().getInt(DIALOG_ERROR);
	           return GooglePlayServicesUtil.getErrorDialog(errorCode,
	                   this.getActivity(), REQUEST_RESOLVE_ERROR);
	       }

	       @Override
	       public void onDismiss(DialogInterface dialog) {
	           ((MotelShowAvailabilityActivity)getActivity()).onDialogDismissed();
	       }
	   }
	   


		@Override
		public void onConnectionSuspended(int arg0) {
		}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.show_availability_location, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		 if (mResolvingError) {
	            // Already attempting to resolve an error.
	            return;
	        } else if (result.hasResolution()) {
	            try {
	                mResolvingError = true;
	                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
	            } catch (SendIntentException e) {
	                // There was an error with the resolution intent. Try again.
	                mGoogleApiClient.connect();
	            }
	        } else {
	            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
	            showErrorDialog(result.getErrorCode());
	            mResolvingError = true;
	        }
		
	}
	@Override
	public void onConnected(Bundle arg0) {
	 
		 
		requestLastPosition();
		
	}
private void requestLastPosition(){
	if(mGoogleApiClient.isConnected()){
		Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
	    if(location !=null){
			LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
	       CabannasrdApp.actualUserPosition = latLng;
	    }
	       
	}
	   
}

	 protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	     
	      

	        mGoogleApiClient = new GoogleApiClient.Builder(this)
	        .addConnectionCallbacks(this)
	        .addOnConnectionFailedListener(this)
	        .addApi(LocationServices.API)
	        .build();
	        mGoogleApiClient.connect();
	        setContentView(R.layout.activity_show_motel_avilability);
	        linearAvailabilities =  (LinearLayout)findViewById(R.id.linearAvailabilities);
	         inflaterItem = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	       
	        dataAcces = new  HandlerMotelDataAccesRestFul();
	         
	        
//	        custom_font2 = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");
	        
	        progress = ProgressDialog.show(this, getString(R.string.searching_info),
	        		getString(R.string.searching_message), true);
	

		     
		    if(Locale.getDefault().getLanguage().equalsIgnoreCase("en")){
		    	prettyTime = new PrettyTime(new Locale("en"));
	        }else{
	        	prettyTime = new PrettyTime(new Locale("es"));
	        }
		    
	     getAvailability();
	       
	        
	   

	    
	        // Get tracker.
	        Tracker t = ((CabannasrdApp) getApplication()).getTracker(
	            TrackerName.APP_TRACKER);

	        // Set screen name.
	        t.setScreenName(MotelShowAvailabilityActivity.class.toString());

	        // Send a screen view.
	        t.send(new HitBuilders.AppViewBuilder().build());
	        
	        if(!CabannasrdApp.THE_USER_SEE_THE_USER_SEE_HELP_AVAILABILITY){
		    	   UtilUI.showAlertDialog(this, getString(R.string.help),getString(R.string.helpAvailability) ,R.string.iGotIt,null);
		    	   CabannasrdApp.set_THE_USER_SEE_THE_USER_SEE_HELP_AVAILABILITY(true);
		       }   
	
	        
	    }
	 
	
	 public static void setMargins (View v, int l, int t, int r, int b) {
		    if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
		        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
		        p.setMargins(l, t, r, b);
		        v.requestLayout();
		    }
		}
	 public void getAvailability(){
		   dataAcces.getAvailabilityOfMotel(motel.getId().intValue(),   new Callback<ArrayList<Availability>> () {
				int position = 1;
				@Override
				public void success(final ArrayList<Availability>  availabilities, Response arg1) {
				
					
					MotelShowAvailabilityActivity.this.runOnUiThread(new Runnable() {
						
						boolean isAvailabilityAnyCreated = true;
						private void createAvailabilityView(Availability availability,boolean createEmpty,boolean isTheFist){
							
							View item_availability_cardview = inflaterItem.inflate(R.layout.item_availability_cardview, null, true);
							TextView lblAvailability = (TextView)item_availability_cardview.findViewById(R.id.lblAvailability);
							TextView lblNumberOfUsersThatVoted = (TextView)item_availability_cardview.findViewById(R.id.lblNumberOfUsersThatVoted);
							TextView lblTimeOftheLastVote = (TextView)item_availability_cardview.findViewById(R.id.lblTimeOftheLastVote);
							TextView btnHelpCountOfUserThatVoted = (TextView)item_availability_cardview.findViewById(R.id.btnHelpCountOfUserThatVoted);
							TextView btnHelpTimeOftheLastVote = (TextView)item_availability_cardview.findViewById(R.id.btnHelpTimeOftheLastVote);
							
							if(!createEmpty){
							   switch (availability.getVoteType()) {
										
									    case CabannasrdApp.MOTEL_AVAILABILITY_FULL:
									    	lblAvailability.setText(position+"- "+context.getString(R.string.availability_full));
									    	lblAvailability.setBackgroundColor(context.getResources().getColor(R.color.aviavility_full));
									    	isAvailabilityAnyCreated = false;
									    	break;
										
										case CabannasrdApp.MOTEL_AVAILABILITY_MEDIUM:
											lblAvailability.setText(position+"- "+context.getString(R.string.availability_medium));
											lblAvailability.setBackgroundColor(context.getResources().getColor(R.color.aviavility_regular));
											isAvailabilityAnyCreated = false;
											break;
										case CabannasrdApp.MOTEL_AVAILABILITY_EMPTY:
											lblAvailability.setText(position+"- "+context.getString(R.string.availability_empty));
											lblAvailability.setBackgroundColor(context.getResources().getColor(R.color.aviavility_empty));
											isAvailabilityAnyCreated = false;
											break;
										
								}
						    
					        
							   
					        	lblNumberOfUsersThatVoted.setText((String.format("%,d",availability.getCountVote())+" "+context.getString(R.string.vote)));
						    	DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a",Locale.getDefault());
						    	
						    	try {
									java.util.Date date = (java.util.Date)formatter.parse(availability.getLastDateUserVote());
									lblTimeOftheLastVote.setText(prettyTime.format(date));
						    	} catch (ParseException e) {

									e.printStackTrace();
								}
					        }else{
					        	lblAvailability.setText(context.getString(R.string.availability_any));
						    	lblAvailability.setBackgroundColor(context.getResources().getColor(R.color.aviavility_any));
						    	
					        	lblNumberOfUsersThatVoted.setText(context.getString(R.string.beTheFirst));
					        	lblTimeOftheLastVote.setText(context.getString(R.string.notAvailable));
//					        	ImageView  iconTime =  (ImageView)item_availability_cardview.findViewById(R.id.iconTime);
					        	//iconTime.setImageResource(R.drawable.not5);
					        	
					        }
					    	
					    	
					    	
					    
					    	
					    	btnHelpTimeOftheLastVote.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									UtilUI.showAlertDialog(context, getString(R.string.help),getString(R.string.helpTimeOftheLastVote) ,R.string.iGotIt,null);
									
								}
							});
					    	
						    btnHelpCountOfUserThatVoted.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									UtilUI.showAlertDialog(context, getString(R.string.help),getString(R.string.helpAviavilityCountOfUserThatVoted) ,R.string.iGotIt,null);
									
								}
							}); 
						    
						    linearAvailabilities.addView(item_availability_cardview);
							
						    
						    if(isTheFist){
						    	
								Button btnSuggestAvailability = (Button)item_availability_cardview.findViewById(R.id.btnSuggestAvailability);
								Button btnHelpSuggestAvailability = (Button)item_availability_cardview.findViewById(R.id.btnHelpSuggestAvailability);
								
								
								
								btnHelpSuggestAvailability.setOnClickListener(new OnClickListener() {
						    		
									@Override
									public void onClick(View v) {
										UtilUI.showAlertDialog(context, getString(R.string.help),getString( R.string.helpSuggestAvailabilityFull) ,R.string.iGotIt,null);
										
									}
								});
						    	
						    	
								
								
								btnSuggestAvailability.setOnClickListener(new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										   float []  distance = new float [4];
				                            float  distanceBetweenUserAndActualPosition =0;
				                            requestLastPosition();
				                            
				                            if(CabannasrdApp.actualUserPosition!=null){
				                            	Location.distanceBetween(motel.getLatitude(), motel.getLongitude(), CabannasrdApp.actualUserPosition.latitude,  CabannasrdApp.actualUserPosition.longitude, distance);
				                            	distanceBetweenUserAndActualPosition = distance[0];
				                            }
				                            

										if(CabannasrdApp.actualUserPosition!=null && distanceBetweenUserAndActualPosition <=140){
											AlertDialog.Builder builder = new AlertDialog.Builder(context);
										    builder.setTitle(R.string.ReportAvailability)
										           .setItems(R.array.availability, new DialogInterface.OnClickListener() {
										               public void onClick(DialogInterface dialog, int which) {

										            
										            	   
										            	   setAvailability(which+1);
										           }
										    });
										    builder.create().show()                               ;
			                            }else{
			                            	UtilUI.showAlertDialog(context, getString(R.string.help),getString(R.string.youCantReportAvailability) ,R.string.iGotIt,null);
			                            }
			                            

										
									}
								});
								
								
								setMargins(item_availability_cardview, 0,20, 0, 100);
								
							}else{
								item_availability_cardview.findViewById(R.id.suggestAvailabilityButtoms).setVisibility(View.GONE);
								setMargins(item_availability_cardview, 15, 20, 15, 20);
							}

						}
						
						@Override
						public void run() {
							
							
							boolean isFirst = true;
							linearAvailabilities.removeAllViews();
							for(Availability availability : availabilities){
								
								if(availability.getCountVote() != 0){
									createAvailabilityView( availability,false,isFirst);
									if(isFirst){
										MotelDetailActivity.actualAvailability = availability;
									}
									isFirst = false;
									position= 1;
								}
								++position;
								
							}
							if( isAvailabilityAnyCreated){
								MotelDetailActivity.actualAvailability = null;
								createAvailabilityView(null,true,true);
								position= 1;
							}
							MotelShowAvailabilityActivity.this.runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									if(progress.isShowing()){
										
										
										
										    	progress.dismiss();;
										 
										
									}
									
								}
							});
							
							
							
						}
					});
					
				}
				
				@Override
				public void failure(final RetrofitError error) {
					MotelShowAvailabilityActivity.this.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							error.printStackTrace();
								MotelShowAvailabilityActivity.this.runOnUiThread(new Runnable() {
									
									@Override
									public void run() {
										UtilUI.showAlertDialog(context, getString(R.string.message),getString(R.string.msErrorConnection) ,R.string.ok,new Runnable() {
											
											@Override
											public void run() {
												progress.dismiss();
												MotelShowAvailabilityActivity.this.finish();
												
											}
										});
										
										
									}
								});
							
						}
					});
					
				}
			});
	 }
	 
	 public void validateRequestError(Handler handler){
		 handler.postDelayed(new Runnable() { 
             public void run() { 
            	 	
					progress.dismiss();
					if(reason.equalsIgnoreCase("NETWORK")){
						ratingBar.setIsIndicator(true);
					}
					UtilUI.showAlertDialog(context, getString(R.string.no_conn_error),getString(R.string.no_conn_message) ,R.string.ok,null);
					
             } 
        }, 2000);
	 }
	 @Override
	public void onBackPressed() {
		super.onBackPressed();
	}



	 public void setAvailability(int idAvailability){
	        progress = ProgressDialog.show(this, getString(R.string.searching_info),
	        		getString(R.string.searching_message), true);

		 
		 dataAcces.setAvailabilityOfMotel(new UserVote(""+idAvailability, ""+motel.getId().intValue(), CabannasrdApp.android_id), new Callback<Object>() {
			
			@Override
			public void success(Object arg0, Response arg1) {
				MotelShowAvailabilityActivity.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						progress.dismiss();
						getAvailability();
					}
				});
				
			}
			
			@Override
			public void failure(RetrofitError arg0) {
				UtilUI.showAlertDialog(context, getString(R.string.no_conn_error),getString(R.string.msErrorConnection) ,R.string.ok,null);
				
			}
		});
	 }
	 
	 

	 @Override
     public boolean onOptionsItemSelected(MenuItem item) {
             switch (item.getItemId()) {
             case android.R.id.home:
                    finish(); 
                    break;
              
                    
             case R.id.menu_help_availability:
            	 UtilUI.showAlertDialog(this, getString(R.string.help),getString(R.string.helpAvailability) ,R.string.iGotIt,null);
                break;
                
             case R.id.menu_refresh_availability:
            	 progress.show();
            	 getAvailability();
                break;
        }
             
             return true;
     }
}
