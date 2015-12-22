package com.cabanasrd.ui.fragments;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.cabanasrd.R;
import com.cabanasrd.config.CabannasrdApp;
import com.cabanasrd.config.CabannasrdApp.TrackerName;
import com.cabanasrd.data.entities.Image;
import com.cabanasrd.data.entities.MotelService;
import com.cabanasrd.ui.activitys.LocationPickerActivity;
import com.cabanasrd.ui.activitys.MotelDetailActivity;
import com.cabanasrd.ui.tools.UtilUI;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.model.LatLng;



public class FragmentSuggestMotel extends ActionBarActivity	 {
	
	final Context context = this;
    private ArrayList<MotelService> motelServices = null;
    private ArrayList<String> telephones = null;
    private ArrayList<String> creditCards = null;
    private ArrayList<Image> imagesList = null;
    private LatLng selectMotelLatLng = null;
    private EditText  txtName  = null;
    LinearLayout cardViewServices = null;
    LinearLayout cardViewServicesTelephons = null;
    LinearLayout cardViewCreditCards = null;
    LinearLayout cardViewImages = null;
    LayoutInflater inflaterItem = null;
    LinearLayout cardViewLocation = null;
    
    
    
    private Typeface custom_font2 = null;
    
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.activity_suggest_place, menu);
		return super.onCreateOptionsMenu(menu);
	}
    @Override
    protected void onResume() {
    	super.onResume();
    }
	 @Override
     public boolean onOptionsItemSelected(MenuItem item) {
             switch (item.getItemId()) {
             case android.R.id.home:
                    finish(); 
                    break;
                    
             case R.id.action_help_suggest_place:
            	 UtilUI.showAlertDialog(this, getString(R.string.help),getString(R.string.helpSuggestPlace) ,R.string.iGotIt,null);
                break;
        }
             
             return true;
     }
    
	 protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.fragment_suggested_motel);
	        
	        telephones = new ArrayList<String>();
	    	creditCards = new ArrayList<String>();
	    	imagesList = new ArrayList<Image>();
	        initComponents();

	        	motelServices = new ArrayList<MotelService>();
	        	
	        	inflaterItem = (LayoutInflater) FragmentSuggestMotel.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        
	         cardViewServices =  (LinearLayout)findViewById(R.id.cardViewServicesPrices);
			 cardViewServicesTelephons =  (LinearLayout) findViewById(R.id.cardViewServicesTelephons);
			cardViewCreditCards =  (LinearLayout)findViewById(R.id.cardViewCreditCards);
			cardViewImages =  (LinearLayout)findViewById(R.id.cardViewImages);
			cardViewLocation =  (LinearLayout)findViewById(R.id.cardViewLocation);
		     custom_font2 = Typeface.createFromAsset(FragmentSuggestMotel.this.getAssets(), "Roboto-Thin.ttf");
		     
		     	txtName =  (EditText) findViewById(R.id.txtName);  
		     	Button btnHelpImageService = (Button)findViewById(R.id.btnHelpImageService);  
				Button btnSuggestedTelephons = (Button)findViewById(R.id.btnSuggestedTelephons);
				Button btnHelpImagePhones = (Button)findViewById(R.id.btnHelpImagePhones);
				Button btnHelpCreditcars = (Button)findViewById(R.id.btnHelpCreditcars);
				final Button btnSend = (Button)findViewById(R.id.btnSend);
				Button btnHelpLocation = (Button)findViewById(R.id.btnHelpLocation);
				Button btnHelpImage = (Button)findViewById(R.id.btnHelpImage);
				
				btnHelpImage.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						UtilUI.showAlertDialog(FragmentSuggestMotel.this, getString(R.string.ImageSuggested),getString(R.string.btnHelpImages) ,R.string.iGotIt,null);
					}
				});
				
				btnHelpLocation.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						UtilUI.showAlertDialog(FragmentSuggestMotel.this, getString(R.string.location_info),getString(R.string.btnHelpLocation) ,R.string.iGotIt,null);
					}
				});
				
				btnSend.setOnClickListener(new OnClickListener() {
		  			@Override
				public void onClick(View v) {
					
					
						
		  				String LatLngCoord = "";
	  					if(selectMotelLatLng != null){
	  						LatLngCoord = selectMotelLatLng.toString();
	  					}
	  					
	  					if(LatLngCoord.isEmpty()){
	  						UtilUI.showAlertDialog(FragmentSuggestMotel.this, getString(R.string.location_info),getString(R.string.select_location_info) ,R.string.iGotIt,null);
	  						((TextView) FragmentSuggestMotel.this.findViewById(R.id.lblLocation)).requestFocus();;  
	  						ScrollView sv = (ScrollView) FragmentSuggestMotel.this.findViewById(R.id.ScrollViewSujectMotel);
	  						sv.scrollTo(0, sv.getTop());
	  						return ;
	  					}
		  				if(!UtilUI.validateEmptyFields(FragmentSuggestMotel.this,txtName)  && UtilUI.validateInternetConnetion(FragmentSuggestMotel.this)){

		  					ArrayList<String> motelServicesArray = new ArrayList<String>();
		  					
		  					for(MotelService motel: motelServices){
		  						motelServicesArray.add("{name: '"+ motel.getService() + "', price: '" + motel.getPrice() +"', type: '" + motel.getType() +"', currency: '" + motel.getCurrencyType() + "'}");
		  					}

		  					UtilUI.sendMailBackground(FragmentSuggestMotel.this,
									CabannasrdApp.mailTo, "Sugerencia de nuevo sitio",
									"{MotelName:"+ txtName.getText().toString() +", Services: ["+TextUtils.join(", ", motelServicesArray.toArray())+"], Tels: ["+TextUtils.join(", ", telephones.toArray())+"], CreditCards: ["+TextUtils.join(", ", creditCards.toArray())+
									"], LatLng:'"+ LatLngCoord +"'} DEVICE "+CabannasrdApp.DEVICE
									, false, null, null, null) ;
		  					
		  					for (int i = 0; i < imagesList.size(); i++) {
		  						UtilUI.sendMailBackground(FragmentSuggestMotel.this,
		  								CabannasrdApp.mailTo, "Imagenes Adjuntas de " +txtName.getText().toString() + " {Description: '"+ imagesList.get(i).getDescription() +"'} DEVICE "+CabannasrdApp.DEVICE,
		  								"Ver imagen adjunta"
		  								, false, null, null, imagesList.get(i).getUrl()) ;
							}
		  					
		  					UtilUI.showAlertDialog(FragmentSuggestMotel.this, FragmentSuggestMotel.this.getString(R.string.thanksContibution),FragmentSuggestMotel.this.getString(R.string.thanksForSubmitInfo), R.string.iGotIt, new Runnable() {
								
								@Override
								public void run() {
									FragmentSuggestMotel.this.finish();
									
								}
							});
		  					
		  					
		  				}
					
				}
			});
				
				btnHelpCreditcars.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						UtilUI.showAlertDialog(FragmentSuggestMotel.this, getString(R.string.CreditCardSuggested),getString(R.string.btnHelpCreditcars) ,R.string.iGotIt,null);
					}
				});
				
				btnHelpImagePhones.setOnClickListener(new OnClickListener() {
		  			@Override
		  			public void onClick(View v) {
					
					
						
						UtilUI.showAlertDialog(FragmentSuggestMotel.this, getString(R.string.TelephoneSuggested),getString(R.string.helpImagePhones) ,R.string.iGotIt,null);
				}
			});
				
			btnHelpImageService.setOnClickListener(new OnClickListener() {
		  			@Override
		  			public void onClick(View v) {
					
					
						UtilUI.showAlertDialog(FragmentSuggestMotel.this, getString(R.string.SeciceSuggested),getString(R.string.helpImageService) ,R.string.iGotIt,null);
					
				}
			});
	        
			btnSuggestedTelephons.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					  
						UtilUI.getTelephoneDialog(FragmentSuggestMotel.this, new Runnable() {
							  
							@Override
							public void run() {
								
								UtilUI.hideSoftKeyBoard(FragmentSuggestMotel.this);
								String phone = UtilUI.telephone;
								telephones.add(phone);
								View viewPhones = inflaterItem.inflate(R.layout.item_layout_edit, null, true);
								TextView txtPhone = (TextView) viewPhones.findViewById(R.id.lblTimeOftheLastVote);
								TextView txtValue = (TextView) viewPhones.findViewById(R.id.service_value);
								ImageView iconImage  = (ImageView) viewPhones.findViewById(R.id.iconService);
								iconImage.setImageResource(R.drawable.icons_07);
								
								
								txtPhone.setText(phone);
								txtPhone.setTypeface(custom_font2);
												
						
									
										
								txtValue.setOnClickListener(new OnClickListener() {
											
											@Override
											public void onClick(View v) {
												RelativeLayout r = (RelativeLayout)v.getParent();
												int index  = cardViewServicesTelephons.indexOfChild(r);
												cardViewServicesTelephons.removeViewAt(index);
												telephones.remove(index);
											}
										});
								
				
								cardViewServicesTelephons.addView(viewPhones);
								
								
								UtilUI.telephone = null;
								
							}
						});
				}
						
				});
			// Get tracker.
	        Tracker t = ((CabannasrdApp) getApplication()).getTracker(
	            TrackerName.APP_TRACKER);

	        // Set screen name.
	        t.setScreenName(FragmentSuggestMotel.class.toString());

	        // Send a screen view.
	        t.send(new HitBuilders.AppViewBuilder().build());
	        
	       if(!CabannasrdApp.THE_USER_SEE_HELP_ADD_MESSAGE_ON_NEW_PLACES){
	    	   UtilUI.showAlertDialog(this, getString(R.string.help),getString(R.string.helpSuggestPlace) ,R.string.iGotIt,null);
	    	   CabannasrdApp.set_THE_USER_SEE_HELP_ADD_MESSAGE_ON_NEW_PLACES(true);
	       } 
	 }
    
	 /*
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        
    	telephones = new ArrayList<String>();
    	creditCards = new ArrayList<String>();
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_suggested_motel, container, false);
            Typeface custom_font = Typeface.createFromAsset(FragmentSuggestMotel.this.getAssets(),
        	        "Existence-Light.ttf");
        	        
        	TextView txtAbout = (TextView)rootView.findViewById(R.id.editText1);
//        	listMotelService = (ListView)rootView.findViewById(R.id.listMotelService);
        	motelServices = new ArrayList<MotelService>();
        	adaptadorMotelSevices = new AdaptadorMotelSevices(this, motelServices);
        	inflaterItem = (LayoutInflater) FragmentSuggestMotel.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        }
         cardViewServices =  (LinearLayout) rootView.findViewById(R.id.cardViewServicesPrices);
		 cardViewServicesTelephons =  (LinearLayout) rootView.findViewById(R.id.cardViewServicesTelephons);
		cardViewCreditCards =  (LinearLayout) rootView.findViewById(R.id.cardViewCreditCards);
		 
		 custom_font = Typeface.createFromAsset(FragmentSuggestMotel.this.getAssets(),  "Existence-Light.ttf");
	     custom_font2 = Typeface.createFromAsset(FragmentSuggestMotel.this.getAssets(), "Roboto-Thin.ttf");
	     
		
	     	Button btnHelpImageService = (Button) rootView.findViewById(R.id.btnHelpImageService);  
			Button btnSuggestedTelephons = (Button) rootView.findViewById(R.id.btnSuggestedTelephons);
			Button btnHelpImagePhones = (Button) rootView.findViewById(R.id.btnHelpImagePhones);
			Button btnSuggestCreditCards = (Button) rootView.findViewById(R.id.btnSuggestCreditCards);
			Button btnHelpCreditcars = (Button) rootView.findViewById(R.id.btnHelpCreditcars);
		
		
		btnHelpImageService.setOnClickListener(new OnClickListener() {
	  			@Override
			public void onClick(View v) {
				
				
					UtilUI.showAlertDialog(FragmentSuggestMotel.this, getString(R.string.SeciceSuggested),getString(R.string.helpImageService) ,R.string.iGotIt,null);
				
			}
		});
        
		btnSuggestedTelephons.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				  
					UtilUI.getTelephoneDialog(FragmentSuggestMotel.this, new Runnable() {
						  
						@Override
						public void run() {
							
						
							String phone = UtilUI.telephone;
							telephones.add(phone);
							View viewPhones = inflaterItem.inflate(R.layout.item_layout_edit, null, true);
							TextView txtPhone = (TextView) viewPhones.findViewById(R.id.service_name);
							TextView txtValue = (TextView) viewPhones.findViewById(R.id.service_value);
							ImageView iconImage  = (ImageView) viewPhones.findViewById(R.id.iconService);
							iconImage.setImageResource(R.drawable.icons_07);
							
							
							txtPhone.setText(phone);
							txtPhone.setTypeface(custom_font2);
											
					
								
									
							txtValue.setOnClickListener(new OnClickListener() {
										
										@Override
										public void onClick(View v) {
											RelativeLayout r = (RelativeLayout)v.getParent();
											int index  = cardViewServicesTelephons.indexOfChild(r);
											cardViewServicesTelephons.removeViewAt(index);
											telephones.remove(index);
										}
									});
							
			
							cardViewServicesTelephons.addView(viewPhones);
							
							
							UtilUI.telephone = null;
							
						}
					});
			}
					
			});
      
        // Get tracker.
        Tracker t = ((CabannasrdApp) FragmentSuggestMotel.this. getApplication()).getTracker(
            TrackerName.APP_TRACKER);

        // Set screen name.
        t.setScreenName(FragmentSuggestMotel.class.toString());

        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());
        return rootView;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //super.onActivityCreated(savedInstanceState);
        initComponents();
    }
*/

 
    public void initComponents(){
    	
    	Spinner spinner = (Spinner) findViewById(R.id.cb_motels_type);
    	
    	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(FragmentSuggestMotel.this,
    	        R.array.motels_types, android.R.layout.simple_spinner_item);

    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	
    	spinner.setAdapter(adapter);
    	
    	((Button)findViewById(R.id.btnPickLocation)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				v.requestFocus();
				if(UtilUI.validateInternetConnetion(FragmentSuggestMotel.this)){
				
					Intent intent =  new Intent( FragmentSuggestMotel.this,LocationPickerActivity.class);
		    		startActivityForResult(intent, 11);
				}
	    		
			}
		});
    	
    	 ((Button)findViewById(R.id.btnAddNewServicieMotel)).setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				v.requestFocus();
				UtilUI.getMotelServiceDialog(FragmentSuggestMotel.this, new Runnable() {
						  
						@Override
						public void run() {
							
							MotelService motelService =UtilUI.motelService;
							motelServices.add(motelService);
							
							View viewPrices = inflaterItem.inflate(R.layout.item_layout_edit, null, true);
							TextView serviceName = (TextView)viewPrices.findViewById(R.id.lblTimeOftheLastVote);
							ImageView iconImage  = (ImageView) viewPrices.findViewById(R.id.iconService);
						    final TextView servicePrice =(TextView)viewPrices.findViewById(R.id.service_value);
	
						    switch (motelService.getType()) {
							case CabannasrdApp.MOTEL_TYPE_HOUSING:
								
								iconImage.setImageResource(R.drawable.bed33);
								break;
							
							case CabannasrdApp.MOTEL_TYPE_ENTERTAINMENT:
								iconImage.setImageResource(R.drawable.tv31);
								break;
							case CabannasrdApp.MOTEL_TYPE_FOOD_OR_DRINKS:
								iconImage.setImageResource(R.drawable.restaurant50);
								break;
							case CabannasrdApp.MOTEL_TYPE_OTHER:
								iconImage.setImageResource(R.drawable.businessman263);
								break;
	
							default:
								iconImage.setImageResource(R.drawable.bed33);
								break;
						}
						
							serviceName.setText(motelService.getService());
							String currency = CabannasrdApp.currencyTypeDescripctionShort[motelService.getCurrencyType()];
							if(currency ==null){
								currency = "RD";
							}
							
							servicePrice.setText(String.format(currency+"$%,3.2f",motelService.getPrice()));
							serviceName.setTypeface(custom_font2);
							servicePrice.setTypeface(custom_font2);
							
							cardViewServices.addView(viewPrices);
							
							
							servicePrice.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									RelativeLayout r = (RelativeLayout)v.getParent();
									int index  = cardViewServices.indexOfChild(r);
									cardViewServices.removeViewAt(index);
									motelServices.remove(index);
								}
							});
							UtilUI.motelService = null;
							UtilUI.hideSoftKeyBoard(FragmentSuggestMotel.this);
						}
					});
			
			
		 
		    	  
//         	   motelServices.add(
//         	   new MotelService(txtMotelServiceName.getText().toString(),  Double.valueOf(txtMotelServicePrice.getText().toString()),
//         			   motels_services_types.getSelectedItemPosition(), motels_services_currency_type.getSelectedItemPosition(), ""));
		    

		    	
		    	
		        

					UtilUI.hideSoftKeyBoard(FragmentSuggestMotel.this);
			    
			}
		});
    	 
    	 ((Button)findViewById(R.id.btnSuggestCreditCards)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				v.requestFocus();
				final ArrayList<String> creditCard = new ArrayList<String>();
				UtilUI.getCreditCardsDialog(FragmentSuggestMotel.this, creditCard, new Runnable() {
					
					@Override
					public void run() {
						for(String creditCardValue: creditCard){
							if(creditCards.indexOf(creditCardValue) == -1){
								creditCards.add(creditCardValue);
								View viewCreditCards = inflaterItem.inflate(R.layout.item_layout_edit, null, true);
								TextView txtCreditCard = (TextView) viewCreditCards.findViewById(R.id.lblTimeOftheLastVote);
								TextView txtValue = (TextView) viewCreditCards.findViewById(R.id.service_value);
								ImageView iconImage  = (ImageView) viewCreditCards.findViewById(R.id.iconService);
								iconImage.setImageResource(R.drawable.icons_10);
								
								
								txtCreditCard.setText(creditCardValue);
								txtCreditCard.setTypeface(custom_font2);
												
						
									
										
								txtValue.setOnClickListener(new OnClickListener() {
											
											@Override
											public void onClick(View v) {
												RelativeLayout r = (RelativeLayout)v.getParent();
												int index  = cardViewCreditCards.indexOfChild(r);
												cardViewCreditCards.removeViewAt(index);
												creditCards.remove(index);
											}
										});
								
				
								cardViewCreditCards.addView(viewCreditCards);
							}
							
							
						}
						
						
						
						
						
					}
				});
				
			}
		});
    	 
    	 ((Button)findViewById(R.id.btnAddImage)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					v.requestFocus();
					Intent intent;

					  if (Build.VERSION.SDK_INT < 19){
					       intent = new Intent();
					       intent.setAction(Intent.ACTION_GET_CONTENT);
					  } 
					  else 
					  {
					       intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
					       intent.addCategory(Intent.CATEGORY_OPENABLE);
					  }
					
					
			        intent.setType("image/*");//image/jpeg
			        //intent.setAction(Intent.ACTION_GET_CONTENT);
			        //intent.addCategory(Intent.CATEGORY_OPENABLE);
			        if (android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.HONEYCOMB) {
			        	  //for API Level 11+
			        	intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
			        	}else{
			        		intent.putExtra("android.intent.extra.LOCAL_ONLY", true);
			        	}
			        
			        startActivityForResult(intent, 10);
				

			}
		});
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  
    	
    	//data.getExtras().containsKey("userPosition")
    	
    	
    	if(requestCode == 10  && resultCode == RESULT_OK) {
			  
			  
			 if(data!=null && data.getData() !=null){ 
				
				showInputDialogAndAddElement(data);
					
				
			  }
		  } 
    	
	    	if(requestCode == 11  && resultCode == RESULT_OK){
	    		 if(data!=null && data.getExtras().containsKey("userPosition")){ 
	    			   
	    		
				
						cardViewLocation.removeAllViews();
						selectMotelLatLng = null;
						
	    			   LatLng selectMotelPosition = (LatLng) data.getExtras().get("userPosition");
	    			   selectMotelLatLng = selectMotelPosition;
	    			   	
	    			   	View viewPrices = inflaterItem.inflate(R.layout.item_layout_edit, null, true);
						TextView serviceName = (TextView)viewPrices.findViewById(R.id.lblTimeOftheLastVote);
						ImageView iconImage  = (ImageView) viewPrices.findViewById(R.id.iconService);
						String 	address = (String) data.getExtras().get("address");	
					    final TextView servicePrice =(TextView)viewPrices.findViewById(R.id.service_value);
					    servicePrice.setVisibility(View.GONE);
					    
							iconImage.setImageResource(R.drawable.icons_03);
											
						
						serviceName.setTypeface(custom_font2);
						serviceName.setText((address!=null && address.length() != 0) ? address : "Lat "+selectMotelLatLng.latitude +" Lon "+selectMotelLatLng.longitude);
						cardViewLocation.addView(viewPrices);
						
						
				

						
					}
				}
		
	    		 }
	    	
		
    

 
    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    @SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
    
    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
            String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    
    


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
    
    
    protected void showInputDialogAndAddElement(final Intent data) {

		// get prompts.xml view
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setView(promptView);

		final TextView editText = (TextView) promptView.findViewById(R.id.editTextDescription);
		// setup a dialog window
		alertDialogBuilder.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						//resultText.setText("Hello, " + editText.getText());

						try {
							String urlImage = getPath(context,data.getData());
							View viewImages = inflaterItem.inflate(R.layout.item_layout_edit_images, null, true);
							ImageView imageItem = (ImageView) viewImages.findViewById(R.id.imageItem);
							TextView txtValue = (TextView) viewImages.findViewById(R.id.service_value);
							final TextView txtDescription = (TextView) viewImages.findViewById(R.id.imageDescription);
							txtDescription.setText(editText.getText().toString());
							txtValue.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									RelativeLayout r = (RelativeLayout)v.getParent();
									int index  = cardViewImages.indexOfChild(r);
									cardViewImages.removeViewAt(index);
									imagesList.remove(index);
								}
							});
							
							
							cardViewImages.addView(viewImages);
			               
							InputStream stream = getContentResolver().openInputStream(
			                        data.getData());
							final Bitmap bitmap = BitmapFactory.decodeStream(stream);
			                stream.close();
			                //imageItem.setImageBitmap(BitmapFactory.decodeFile(picturePath));
			                imageItem.setImageBitmap(bitmap);
			                Image imageObj = new Image();
			                imageObj.setUrl(urlImage);
			                imageObj.setDescription(editText.getText().toString());
			                imagesList.add(imageObj);
			            } catch (FileNotFoundException e) {
			            	Log.i("photo", e.toString());
			            } catch (IOException e) {
			            	Log.i("photo", e.toString());
			            } catch (Exception e) {
			            	Log.i("photo", e.toString());
						}
				
					}
				})/*
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						})*/;

		// create an alert dialog
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}
    
}

