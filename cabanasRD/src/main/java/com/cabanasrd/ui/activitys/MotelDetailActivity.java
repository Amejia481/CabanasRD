

package com.cabanasrd.ui.activitys;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.cabanasrd.R;
import com.cabanasrd.config.CabannasrdApp;
import com.cabanasrd.config.CabannasrdApp.TrackerName;
import com.cabanasrd.data.HandlerMotelDataAcces;
import com.cabanasrd.data.HandlerMotelDataAccesRestFul;
import com.cabanasrd.data.entities.Availability;
import com.cabanasrd.data.entities.CreditCard;
import com.cabanasrd.data.entities.Image;
import com.cabanasrd.data.entities.Motel;
import com.cabanasrd.data.entities.MotelLog;
import com.cabanasrd.data.entities.MotelService;
import com.cabanasrd.data.entities.Question;
import com.cabanasrd.data.entities.Rating;
import com.cabanasrd.data.entities.UserAnswer;
import com.cabanasrd.ui.tools.UtilUI;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.viewpagerindicator.CirclePageIndicator;

import org.joda.time.DateTime;
import org.joda.time.Days;


public class MotelDetailActivity extends ActionBarActivity	 {
	
	final Context context = this;
	public  static Motel motel = null;
	RatingBar ratingBar;
	private HandlerMotelDataAcces dataAcces = null;
	private ProgressDialog progress = null;
	private Rating newRating = null;
	private String android_id = null;
	private boolean rated = false;
	private String reason = "";
	TextView motel_score;
	TextView motel_name = null;
    TextView motel_location = null;
    TextView txtPriceMotelFast = null;
    TextView txtPriceMotelNormal = null;
    TextView txtPriceMotelVIP = null;
    private Typeface custom_font = null;
    private Typeface custom_font2 = null;
    TextView lblTotalVotes = null;
    private TextView txtMotelImageDescription = null;
	private WebView webViewAds = null;
     private Button btnShowAvailability= null;
     public static Availability  actualAvailability = null;  
			
    Builder optionsBuilder;
	DisplayImageOptions options;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.activity_motel_detail, menu);
		return super.onCreateOptionsMenu(menu);
	}

		@Override
		protected void onRestart() {

			if(actualAvailability!=null && actualAvailability.getCountVote() > 0 ){ 
				   switch (actualAvailability.getVoteType()) {
					
				    case CabannasrdApp.MOTEL_AVAILABILITY_FULL:
				    	btnShowAvailability.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_point_full, 0, 0, 0);
				    	break;
					
					case CabannasrdApp.MOTEL_AVAILABILITY_MEDIUM:
						btnShowAvailability.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_point_regular, 0, 0, 0);
						break;
					case CabannasrdApp.MOTEL_AVAILABILITY_EMPTY:
						btnShowAvailability.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_point_empty, 0, 0, 0);
						break;
					
						default:
							btnShowAvailability.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_default, 0, 0, 0);
				   }
			}else{
				btnShowAvailability.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_default, 0, 0, 0);
			}
			super.onRestart();
		}
	 protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        optionsBuilder = new DisplayImageOptions.Builder()
	        //.showImageOnLoading(R.drawable.)
			.showImageForEmptyUri(R.drawable.no_image)
			.showImageOnFail(R.drawable.no_image)
			.resetViewBeforeLoading(true)
			.cacheOnDisk(true)
			.imageScaleType(ImageScaleType.EXACTLY)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.considerExifParams(true)
			.displayer(new FadeInBitmapDisplayer(300));

		 dataAcces = new  HandlerMotelDataAccesRestFul();

		 //Loading Questiongs




		 if(CabannasrdApp.DEVICE !="") {
			 boolean canISearchForAnotherQuestion = false;
			 if(CabannasrdApp.LAST_DAY_USER_ANSWER_A_QUESTION !=null){
				 SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				 Date lastDate = new Date();
				 try {
					 lastDate = dateFormat.parse(CabannasrdApp.LAST_DAY_USER_ANSWER_A_QUESTION );
					 Date today = new Date();
					 int days = Days.daysBetween(new DateTime(lastDate).toLocalDate(), new DateTime(today).toLocalDate()).getDays();
					 canISearchForAnotherQuestion = (days >= 1);
				 } catch (ParseException e) {
					 e.printStackTrace();
				 }
			 }

			 if(canISearchForAnotherQuestion){
				 dataAcces.getPendingQuestions(CabannasrdApp.DEVICE, new Callback<ArrayList<Question>>() {
					 @Override
					 public void success(ArrayList<Question> questions, Response response) {
						 if(!questions.isEmpty())	{
							 final Question question = questions.get(0);
							 UtilUI.getQuestionDialog(MotelDetailActivity.this,question, new Runnable() {
								 @Override
								 public void run() {
									 dataAcces.saveQuestions(new UserAnswer(question.getId(), question.getUserAnswerID(), CabannasrdApp.DEVICE), new Callback<Object>() {
										 @Override
										 public void success(Object o, Response response) {
											 saveUserLastQuestion();
										 }

										 @Override
										 public void failure(RetrofitError error) {
											 saveUserLastQuestion();
										 }
									 });
								 }
							 }, new Runnable() {
								 @Override
								 public void run() {
									 saveUserLastQuestion();
								 }
							 });
						 }

					 }

					 @Override
					 public void failure(RetrofitError error) {
						 saveUserLastQuestion();
					 }
				 });
			 }

		 }

			//Putting Loading image....
	        if(Locale.getDefault().getLanguage().equalsIgnoreCase("en")){
	        	optionsBuilder.showImageOnLoading(R.drawable.loading);
	        }else{
	        	optionsBuilder.showImageOnLoading(R.drawable.cargando);
	        }

	    	options = optionsBuilder.build();
	    	ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
			.threadPriority(Thread.NORM_PRIORITY - 2)
			.denyCacheImageMultipleSizesInMemory()
			.diskCacheFileNameGenerator(new Md5FileNameGenerator())
			.diskCacheSize(50 * 1024 * 1024) // 50 Mb
			.tasksProcessingOrder(QueueProcessingType.LIFO)
			.writeDebugLogs() // Remove for release app
			.build();

	    	ImageLoader.getInstance().init(config);
	
	        setContentView(R.layout.activity_motel_detail_activity);
	    
	        //Getting the Device ID
	        android_id = CabannasrdApp.android_id;
	        //android_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
	        //Loading Fonts and colors...
	       
	       
	       final  ViewPager ViewImageMotel  = (ViewPager) findViewById(R.id.ViewImageMotel);
	        
	        final CirclePageIndicator titleIndicator = (CirclePageIndicator) findViewById(R.id.paginatorImagesMotes);
//	        titleIndicator.setViewPager(ViewImageMotel);
	        
	         motel_name = (TextView)findViewById(R.id.motel_name_detail);
	         txtMotelImageDescription = (TextView)findViewById(R.id.txtMotelImageDescription);
	         motel_location = (TextView)findViewById(R.id.motel_location_detail);
	         motel_score = (TextView)findViewById(R.id.motel_score_detail);
	         btnShowAvailability =  (Button)findViewById(R.id.btnShowAvailability);
	         custom_font = Typeface.createFromAsset(getAssets(),  "Existence-Light.ttf");
	         custom_font2 = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");
	         TextView lblMenu = (TextView) findViewById(R.id.lblMenu);
	        			lblMenu.setTypeface(custom_font2);
	        motel_name.setTypeface(custom_font);
	        motel_location.setTypeface(custom_font2);
	        motel_score.setTypeface(custom_font);    
	        txtMotelImageDescription.setTypeface(custom_font2);

	        Button button_route = (Button)findViewById(R.id.motel_route_detail);
	           //Setting fonts to titles.

			
			TextView lblTels = (TextView) findViewById(R.id.lblTels);
			TextView lblAditional = (TextView) findViewById(R.id.lblAditionalInfo_);
			TextView lblRegister = (TextView) findViewById(R.id.lblRegister);

		 	if(!motel.isManagedByTheOwner()) {
				lblRegister.setVisibility(View.VISIBLE);
			}
		 	lblTotalVotes = (TextView) findViewById(R.id.total_votes);
			TextView lblServices = (TextView) findViewById(R.id.lblServices);
			
			lblTels.setTypeface(custom_font2);
			lblAditional.setTypeface(custom_font2);
			lblTotalVotes.setTypeface(custom_font2);
			lblServices.setTypeface(custom_font2);

		 lblRegister.setOnClickListener(new OnClickListener() {
			 @Override
			 public void onClick(View v) {
				 LayoutInflater inflater = getLayoutInflater();
				 View view =  inflater.inflate(R.layout.view_help_us, null);
				 UtilUI.showAlertDialog(MotelDetailActivity.this, getString(R.string.weNeedYourHelpTitle),view ,R.string.iGotIt,null);

			 }
		 });
	        button_route.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					try{
					  Uri gmmIntentUri = Uri.parse("google.navigation:q="+motel.getLatitude()+","+motel.getLongitude());
				        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
				        mapIntent.setPackage("com.google.android.apps.maps");
				        startActivity(mapIntent);
					} catch(ActivityNotFoundException e){
						UtilUI.showAlertDialog(context, getString(R.string.help),getString(R.string.needGoogleMapApp) ,R.string.iGotIt, new Runnable() {
							public void run() {
								try {
					        	    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps" )));
					        	} catch (android.content.ActivityNotFoundException anfe) {
					        	    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps" )));
					        	}
							}
						});
					}
					
				}
			});
	        
	        setInfoDetail();

	        SetServicesList(motel);
	        setUpWebViewAds();
	        // Get tracker.
	        Tracker t = ((CabannasrdApp) getApplication()).getTracker(
	            TrackerName.APP_TRACKER);

	        // Set screen name.
	        t.setScreenName(MotelDetailActivity.class.toString());

	        // Send a screen view.
	        t.send(new HitBuilders.AppViewBuilder().build());
	        
	        
//	        rate = 0;
	        
	        //Show a Progress Dialog while data is coming
	        
	        progress = ProgressDialog.show(this, getString(R.string.searching_info),
	        		getString(R.string.searching_message), true);
	        
	        final Handler handler = new Handler(); 
	        ratingBar=(RatingBar)findViewById(R.id.motel_rating_detail);
	     //Getting data from API

	        
	        dataAcces.addToLog(new MotelLog(motel.getId().intValue(), "01/01/2012", CabannasrdApp.android_id), new Callback<Object>() {

				@Override
				public void failure(RetrofitError arg0) {
					
					System.out.println("");
				}

				@Override
				public void success(Object arg0, Response arg1) {
					
					System.out.println("");	
				}
			});
	        
			   dataAcces.getAvailabilityOfMotel(motel.getId().intValue(),   new Callback<ArrayList<Availability>> () {
					
					@Override
					public void success(final ArrayList<Availability>  availabilities, Response arg1) {
						
						

						Availability availability = availabilities.get(0);
						
						if(availability.getCountVote() > 0){ 
							   switch (availability.getVoteType()) {
								
							    case CabannasrdApp.MOTEL_AVAILABILITY_FULL:
							    	btnShowAvailability.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_point_full, 0, 0, 0);
							    	break;
								
								case CabannasrdApp.MOTEL_AVAILABILITY_MEDIUM:
									btnShowAvailability.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_point_regular, 0, 0, 0);
									break;
								case CabannasrdApp.MOTEL_AVAILABILITY_EMPTY:
									btnShowAvailability.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_point_empty, 0, 0, 0);
									break;
								
								default:
									btnShowAvailability.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_default, 0, 0, 0);
							   }
						}
					}
					
					@Override
					public void failure(final RetrofitError error) {
						
						
					}
				});
	        dataAcces.getMotelRating(Integer.parseInt(motel.getId().toString()), android_id,new Callback<Rating>() {

				@Override
				public void failure(RetrofitError arg0) {

					System.out.println("BAD");
					reason = arg0.getKind().name();
					
			        validateRequestError(handler);
					
				}

				@Override
				public void success(Rating arg0, Response arg1) {
					System.out.println("OK");

					
					MotelDetailActivity.this.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							progress.dismiss();
						}
					});
			        
			        if(arg0.getDevice() != null){
			        	if (arg0.getDevice().equalsIgnoreCase("registered")){
			        		rated = true;
			        	}
			        }
					ratingBar.setRating((float) arg0.getValue());
					motel_score.setText((float) round(arg0.getValue(), 1) +"/5.0");
				}
	        	
			});
	        
	        btnShowAvailability.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					MotelShowAvailabilityActivity.motel = motel;
					Intent intent = new Intent(context,MotelShowAvailabilityActivity.class);
					context.startActivity(intent);
					
				}
			});
	        
	        
	        //RatingBar options
			ViewImageMotel.setAdapter(new ImageAdapter());
			titleIndicator.setViewPager(ViewImageMotel);

	        setRatingBarOptions(ratingBar);
	        
	    }

	private void saveUserLastQuestion(){
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		String formatDate = df.format(c.getTime());
		CabannasrdApp.set_LAST_DAY_USER_ANSWER_A_QUESTION(formatDate);
		UtilUI.showToaststMessage(this.getString(R.string.thanksContibution),this);

	}
	 public void validateRequestError(Handler handler){
		 handler.postDelayed(new Runnable() { 
             public void run() { 
            	 	
					progress.dismiss();
					if(reason.equalsIgnoreCase("NETWORK")){
						ratingBar.setIsIndicator(true);
						UtilUI.showAlertDialog(context, getString(R.string.no_conn_error),getString(R.string.no_conn_message) ,R.string.ok,null);
					}else{
						UtilUI.showAlertDialog(context, getString(R.string.message),getString(R.string.msErrorConnection) ,R.string.ok,null);
					}
					 
					
					
             } 
        }, 2000);
	 }
	 @Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	 public void setInfoDetail(){
		 if(motel_name!=null && motel !=null ){
			 motel_name.setText(motel.getName());
		 }
		 if(motel_location!=null &&  motel !=null &&  motel.getState() !=null){
			 motel_location.setText(motel.getState().getName());
		 }
	    
	    
	     
	 }

	 public static double round(double value, int places) {
		    if (places < 0) throw new IllegalArgumentException();

		    BigDecimal bd = new BigDecimal(value);
		    bd = bd.setScale(places, RoundingMode.HALF_UP);
		    return bd.doubleValue();
}

	 public void setRatingBarOptions(RatingBar ratingBarControl){
		// Set ChangeListener to Rating Bar
	        
		 ratingBarControl.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
				
				@Override
				public void onRatingChanged(final RatingBar ratingBar, float rating,
						boolean fromUser) {
					if(fromUser){
						ratingBar.setIsIndicator(true);
						newRating = new Rating();
						newRating.setValue(rating);
						newRating.setGuestHouse(Integer.parseInt(motel.getId().toString()));
						newRating.setDevice(android_id);
						
						dataAcces.createRating(newRating, new Callback<Rating>() {

							@Override
							public void failure(RetrofitError arg0) {
								System.out.println("BAD");
								
							}

							@Override
							public void success(Rating rakingServer, Response arg1) {
								System.out.println("OK");
								
								if(rated){
									Toast.makeText(getApplicationContext(),getString(R.string.vote_updated)+" "  + String.valueOf((int) rakingServer.getValue()),Toast.LENGTH_LONG).show();
								}else{
									Toast.makeText(getApplicationContext(),getString(R.string.vote_done),Toast.LENGTH_LONG).show();
//									lblTotalVotes.setText(newRating.get);
									

								}
								motel_score.setText((float) rakingServer.getValue() +"/5.0");
    							//TODO: ARREGRAR API PARA QUE DEVUELVA EL ULTIMO RAKING GENERADO	ratingBar.setRating((float) rakingServer.getValue());
								//TODO: ACTUALIZAR API PARA QUE DEVUELVA LA CANTIDA TOTAL DE GENTE QUE HA VOTADO motel.setRating(rakingServer);
							}
						});
					}
					
				}
			});
	 }
	 @Override
     public boolean onOptionsItemSelected(MenuItem item) {
             switch (item.getItemId()) {
             case android.R.id.home:
                    finish(); 
                    break;
                    
             case R.id.action_share_app:
          		Intent sendIntent = new Intent();
               	sendIntent.setAction(Intent.ACTION_SEND);
               	
               	
               	
               	sendIntent.putExtra(Intent.EXTRA_TEXT, ""+motel.getName()+
               			" http://maps.google.com/maps?q=" +motel.getLatitude() +","+ 
                       	motel.getLongitude() +		
               			"\n \n Cabañas RD App: \n \n https://play.google.com/store/apps/details?id=com.cabanasrd");
               	sendIntent.setType("text/plain");
               	startActivity(sendIntent);
                break;
        }
             
             return true;
     }
	 
		private class ImageAdapter extends PagerAdapter  {

			private LayoutInflater inflater;
			

			ImageAdapter() {
				inflater = LayoutInflater.from(MotelDetailActivity.this);
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				container.removeView((View) object);
			}

			@Override
			public int getCount() {
				int count = (motel.getImages2() == null) ? motel.getImages().size() : motel.getImages2().size();
				if( count== 0){
					count =1; 
					motel.getImages().add("http://cabanasrd.com.do/assets/no-image.jpg");
				}
				return count;
			}

			@Override
			public Object instantiateItem(ViewGroup view, int position) {
				View imageLayout = inflater.inflate(R.layout.item_pager_image_motel, view, false);
				
				assert imageLayout != null;
				ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image_motel);
				final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading_image_motel);
			
				
				
				String urlImage = null;
				if(motel.getImages2() != null && !motel.getImages2().isEmpty()){
					Image image =  motel.getImages2().get(position);   
					urlImage =  image.getUrl();
					
					if(image.getDescription() !=null && !image.getDescription().isEmpty()){
						txtMotelImageDescription.setVisibility(View.VISIBLE);
						txtMotelImageDescription.setText(image.getDescription());
					}
					
				}else{
					urlImage =   motel.getImages().get(position);
				}
				
				ImageLoader.getInstance().displayImage(urlImage, imageView, options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						//TODO:ARREGLAR PARA QUE ESTE VISIBLE
						spinner.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						String message = null;
						switch (failReason.getType()) {
							case IO_ERROR:
								message = getString(R.string.IO_ERROR);
								break;
							case DECODING_ERROR:
								message = getString(R.string.DECODING_ERROR);
								break;
							case NETWORK_DENIED:
								message = getString(R.string.NETWORK_DENIED);
								break;
							case OUT_OF_MEMORY:
								message = getString(R.string.OUT_OF_MEMORY);
								break;
							case UNKNOWN:
								message = getString(R.string.UNKNOWN);
								break;
						}
						Toast.makeText(MotelDetailActivity.this, message, Toast.LENGTH_SHORT).show();

						spinner.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						spinner.setVisibility(View.GONE);
					}
				});

				view.addView(imageLayout, 0);
				return imageLayout;
			}

			@Override
			public boolean isViewFromObject(View view, Object object) {
				return view.equals(object);
			}

			@Override
			public void restoreState(Parcelable state, ClassLoader loader) {
			}

			@Override
			public Parcelable saveState() {
				return null;
			}

			
		}
		//Function to add row
		public void SetServicesList(final Motel motelDetail) {
			
			
			
			LinearLayout cardViewServices =  (LinearLayout) findViewById(R.id.cardViewServicesPrices);
			LinearLayout cardViewServicesTelephons =  (LinearLayout) findViewById(R.id.cardViewServicesTelephons);
			LinearLayout cardViewCreditCards =  (LinearLayout) findViewById(R.id.cardViewCreditCards);
			
			LayoutInflater inflaterItem = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			
			Button btnSuggestedServices = (Button) MotelDetailActivity.this.findViewById(R.id.btnSuggestedServices);
			Button btnSuggestedTelephons = (Button) MotelDetailActivity.this.findViewById(R.id.btnSuggestedTelephons);
			Button btnSuggestCreditCards = (Button) MotelDetailActivity.this.findViewById(R.id.btnSuggestCreditCards);
			Button btnSuggestImage = (Button) MotelDetailActivity.this.findViewById(R.id.btnSuggestImage);
			Button btnHelpImageService = (Button) MotelDetailActivity.this.findViewById(R.id.btnHelpImageService);
			Button btnHelpImagePhones = (Button) MotelDetailActivity.this.findViewById(R.id.btnHelpImagePhones);
			Button btnHelpCreditcars = (Button) MotelDetailActivity.this.findViewById(R.id.btnHelpCreditcars);
			
			LinearLayout cardViewServicesMenu =  (LinearLayout) findViewById(R.id.cardViewServicesMenu);
			Button btnSuggestedMenu = (Button) MotelDetailActivity.this.findViewById(R.id.btnSuggestedMenu);
			Button btnHelpMenu = (Button) MotelDetailActivity.this.findViewById(R.id.btnHelpImageMenu);
			
			
			
			btnHelpImageService.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					
						UtilUI.showAlertDialog(MotelDetailActivity.this, getString(R.string.SeciceSuggested),getString(R.string.helpImageService) ,R.string.iGotIt,null);
					
				}
			});
			
			btnHelpMenu.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					UtilUI.showAlertDialog(MotelDetailActivity.this, getString(R.string.MenuSuggested),getString(R.string.btnHelpMenu) ,R.string.iGotIt,null);
				}
			});
			
			
				btnSuggestedMenu.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
							if(UtilUI.validateInternetConnetion(MotelDetailActivity.this)){  
								UtilUI.getMotelServiceMenuDialog(MotelDetailActivity.this, new Runnable() {
									  
									@Override
									public void run() {
										MotelService motelService =UtilUI.motelService;
										UtilUI.sendMailBackground(MotelDetailActivity.this,
												CabannasrdApp.mailTo, "Sugerencia de Menú",
												"{MotelID:"+motelDetail.getId()+",ServiceName:"+motelService.getService()+
												",ServiceType:"+motelService.getType()+
												",ServicePrice:"+motelService.getPrice()+
												",ServiceCurrencyType:"+motelService.getCurrencyType()+"} DEVICE" +CabannasrdApp.DEVICE
												, true, MotelDetailActivity.this.getString(R.string.sending), MotelDetailActivity.this.getString(R.string.thanksContibution), null) ;
										
										UtilUI.motelService = null;
										
									}
								});
						}
						}
					});
			
			btnHelpImageService.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					
						UtilUI.showAlertDialog(MotelDetailActivity.this, getString(R.string.SeciceSuggested),getString(R.string.helpImageService) ,R.string.iGotIt,null);
					
				}
			});
			
			btnHelpImagePhones.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					UtilUI.showAlertDialog(MotelDetailActivity.this, getString(R.string.TelephoneSuggested),getString(R.string.helpImagePhones) ,R.string.iGotIt,null);
				}
			});
			
			btnHelpCreditcars.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					UtilUI.showAlertDialog(MotelDetailActivity.this, getString(R.string.CreditCardSuggested),getString(R.string.btnHelpCreditcars) ,R.string.iGotIt,null);
				}
			});
			btnSuggestImage.setOnClickListener(new OnClickListener() {
				
					@Override
					public void onClick(View v) {
						
							UtilUI.showAlertDialog(MotelDetailActivity.this, getString(R.string.ImageSuggested),getString(R.string.btnHelpImages) ,R.string.iGotIt,new Runnable() {
								
								@Override
								public void run() {
										if(UtilUI.validateInternetConnetion(MotelDetailActivity.this)){			
											Intent fileIntent = new Intent(Intent.ACTION_PICK,
											        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
													fileIntent.setType("image/*");
													MotelDetailActivity.this.startActivityForResult(fileIntent, 10);
										}
								}
							});
					
	
				}
			});
			
			
			btnSuggestedServices.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
							if(UtilUI.validateInternetConnetion(MotelDetailActivity.this)){  
								UtilUI.getMotelServiceDialog(MotelDetailActivity.this, new Runnable() {
									  
									@Override
									public void run() {
										MotelService motelService =UtilUI.motelService;
										UtilUI.sendMailBackground(MotelDetailActivity.this,
												CabannasrdApp.mailTo, "Sugerencia de servicio",
												"{MotelID:"+motelDetail.getId()+",ServiceName:"+motelService.getService()+
												",ServiceType:"+motelService.getType()+
												",ServicePrice:"+motelService.getPrice()+
												",ServiceCurrencyType:"+motelService.getCurrencyType()+"}DEVICE" +CabannasrdApp.DEVICE
												, true, MotelDetailActivity.this.getString(R.string.sending), MotelDetailActivity.this.getString(R.string.thanksContibution), null) ;
										
										UtilUI.motelService = null;
										
									}
								});
						}
						}
					});
			
			btnSuggestedTelephons.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(UtilUI.validateInternetConnetion(MotelDetailActivity.this)){  
							UtilUI.getTelephoneDialog(MotelDetailActivity.this, new Runnable() {
								  
								@Override
								public void run() {
									
									UtilUI.sendMailBackground(MotelDetailActivity.this,
											CabannasrdApp.mailTo, "Sugerencia de servicio",
											"{MotelID:"+motelDetail.getId()+",Phone:"+UtilUI.telephone+
											"}DEVICE" +CabannasrdApp.DEVICE
											, true, MotelDetailActivity.this.getString(R.string.sending), MotelDetailActivity.this.getString(R.string.thanksContibution), null) ;
									
									UtilUI.telephone = null;
									
								}
							});
					}
							}
					});
					
			btnSuggestCreditCards.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final ArrayList<String> creditCard = new ArrayList<String>();
							if(UtilUI.validateInternetConnetion(MotelDetailActivity.this)){	
								  UtilUI.getCreditCardsDialog(MotelDetailActivity.this,creditCard, new Runnable() {
									  
									@Override
									public void run() {
										
										UtilUI.sendMailBackground(MotelDetailActivity.this,
												CabannasrdApp.mailTo, "Sugerencia de CreditCards",
												"{MotelID:"+motelDetail.getId()+",CreditCards:"+TextUtils.join(", ", creditCard.toArray())+
												"}DEVICE" +CabannasrdApp.DEVICE
												, true, MotelDetailActivity.this.getString(R.string.sending), MotelDetailActivity.this.getString(R.string.thanksContibution), null) ;
										
										
										
									}
								});
						}
						}
		});
			
			TextView totalVotes = (TextView)this.findViewById(R.id.total_votes);
			if(motelDetail.getRanking() > 0){
				totalVotes.setText( ""+motelDetail.getRanking() );
			}else{
				totalVotes.setText( getText(R.string.beTheFirst) );
				
			}
			

			int countServicesTypesFoodOrDrink  = 0;
			if(motelDetail.getMotelServices()==null  || motelDetail.getMotelServices().isEmpty()){
				View viewPrices = inflaterItem.inflate(R.layout.item_layout, null, true);
				TextView serviceName = (TextView)viewPrices.findViewById(R.id.lblTimeOftheLastVote);
				ImageView iconImage  = (ImageView) viewPrices.findViewById(R.id.iconService);
				iconImage.setImageResource(R.drawable.icons_03);
				serviceName.setText(R.string.notAvailable);
				serviceName.setTypeface(custom_font2);
				cardViewServices.addView(viewPrices);
				//Clearing data
				viewPrices = null;
				serviceName = null;
				iconImage =null;
			}else{
				
					for (MotelService motelService : motelDetail.getMotelServices()) {
							View viewPrices = inflaterItem.inflate(R.layout.item_layout, null, true);
							TextView serviceName = (TextView)viewPrices.findViewById(R.id.lblTimeOftheLastVote);
							ImageView iconImage  = (ImageView) viewPrices.findViewById(R.id.iconService);
							boolean isMenuFoodOrDrink = false;
						    final TextView servicePrice =(TextView)viewPrices.findViewById(R.id.service_value);
	
						    switch (motelService.getType()) {
								case CabannasrdApp.MOTEL_TYPE_HOUSING:
									
									iconImage.setImageResource(R.drawable.bed33);
									break;
								
								case CabannasrdApp.MOTEL_TYPE_ENTERTAINMENT:
									iconImage.setImageResource(R.drawable.tv31);
									break;
								case CabannasrdApp.MOTEL_TYPE_FOOD_OR_DRINKS:
									isMenuFoodOrDrink = true;
									countServicesTypesFoodOrDrink++;
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
							String currency = CabannasrdApp.currencyTypeDescripctionShort[(motelService.getCurrencyType() -1)];
							if(currency ==null){
								currency = "RD";
							}
							
							servicePrice.setText(String.format(currency+"$%,3.2f",motelService.getPrice()));
							serviceName.setTypeface(custom_font2);
							servicePrice.setTypeface(custom_font2);
							
							final String  motelServiceDescriptionDetail = motelService.getDescriptionDetail();
							if(motelServiceDescriptionDetail!=null){
								
								OnClickListener clickEvent = new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										if(motelServiceDescriptionDetail !=null){
											UtilUI.showAlertDialog(MotelDetailActivity.this, getString(R.string.aditional_service_info),motelServiceDescriptionDetail ,R.string.ok,null);
										}
									}
								};
								
								
								viewPrices.setOnClickListener(clickEvent);
								serviceName.setOnClickListener(clickEvent);
								servicePrice.setOnClickListener(clickEvent);
								
							}
							
							if(isMenuFoodOrDrink){
								cardViewServicesMenu.addView(viewPrices);
							}else{
								cardViewServices.addView(viewPrices);
							}

						
								//Clearing data
								viewPrices = null;
								serviceName = null;
								
							
							
					
				}
					
					
			}
			if(countServicesTypesFoodOrDrink ==0){
				View viewPrices = inflaterItem.inflate(R.layout.item_layout, null, true);
				TextView serviceName = (TextView)viewPrices.findViewById(R.id.lblTimeOftheLastVote);
				ImageView iconImage  = (ImageView) viewPrices.findViewById(R.id.iconService);
				iconImage.setImageResource(R.drawable.restaurant50);
				serviceName.setText(R.string.notAvailable);
				serviceName.setTypeface(custom_font2);
				cardViewServicesMenu.addView(viewPrices);
				//Clearing data
				
				viewPrices = null;
				serviceName = null;
				iconImage =null;
				
			}
			
			
			if(motelDetail.getPhones().isEmpty()){
				View viewPrices = inflaterItem.inflate(R.layout.item_layout, null, true);
				TextView serviceName = (TextView)viewPrices.findViewById(R.id.lblTimeOftheLastVote);
				ImageView iconImage  = (ImageView) viewPrices.findViewById(R.id.iconService);
				iconImage.setImageResource(R.drawable.icons_07);
				serviceName.setText(R.string.notAvailable);
				serviceName.setTypeface(custom_font2);
				cardViewServicesTelephons.addView(viewPrices);
			}else{
				for(String phone :motelDetail.getPhones()){
					
					View viewPhones = inflaterItem.inflate(R.layout.item_layout_with_buttons, null, true);
					Button txtPhone = (Button) viewPhones.findViewById(R.id.service_name_button);
					Button txtValue = (Button) viewPhones.findViewById(R.id.service_value_button);
//					ImageView iconImage  = (ImageView) viewPhones.findViewById(R.id.iconService);
					
					txtValue.setVisibility(View.GONE);
					txtPhone.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icons_07, 0, 0, 0);
					txtPhone.setText(phone);
					txtPhone.setTypeface(custom_font2);
									
					final String phone_ =  phone;

					if(phone_ !=null){
						OnClickListener clickEvent = new OnClickListener() {
							
							@Override
							public void onClick(View v) {
									 Intent intent = new Intent(Intent.ACTION_DIAL);
									 intent.setData(Uri.parse( "tel:" + phone_.trim()));
									 startActivity(intent);
							}
						};
							viewPhones.setOnClickListener(clickEvent);
							txtPhone.setOnClickListener(clickEvent);
					}
	
					cardViewServicesTelephons.addView(viewPhones);
					
	
				}
			}
			
			//Credict Cards
			
			if(motelDetail.getCreditCards() !=null  && !motelDetail.getCreditCards().isEmpty()){
				for(CreditCard creditCard :motelDetail.getCreditCards()){
					
					View viewTakeCredictCards = inflaterItem.inflate(R.layout.item_layout, null, true);
					TextView serviceName = (TextView)  viewTakeCredictCards.findViewById(R.id.lblTimeOftheLastVote);
					ImageView iconImage  = (ImageView) viewTakeCredictCards.findViewById(R.id.iconService);
					
						
					iconImage.setImageResource(R.drawable.icons_10);
					serviceName.setText(CabannasrdApp.creditCardsType[(creditCard.getId()-1)]);
					serviceName.setTypeface(custom_font2);
					cardViewCreditCards.addView(viewTakeCredictCards);
				}
			}else{
				View viewTakeCredictCards = inflaterItem.inflate(R.layout.item_layout, null, true);
				TextView serviceName = (TextView)  viewTakeCredictCards.findViewById(R.id.lblTimeOftheLastVote);
				TextView service_value =(TextView)  viewTakeCredictCards.findViewById(R.id.service_value);
				ImageView iconImage  = (ImageView) viewTakeCredictCards.findViewById(R.id.iconService);
				
				iconImage.setImageResource(R.drawable.icons_10);
				serviceName.setText(this.getString(R.string.creditCards));
				service_value.setText((motelDetail.isTakeCredictCards()) ? this.getString(R.string.yes) : this.getString(R.string.no));
				serviceName.setTypeface(custom_font2);
				service_value.setTypeface(custom_font2);
				cardViewCreditCards.addView(viewTakeCredictCards);
			}
			
			

			
				

			
		}








	public void setUpWebViewAds() {
		webViewAds = (WebView) findViewById(R.id.webviewAdsDetail);
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

			WebView newWebView = new WebView(MotelDetailActivity.this);
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
		




		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			  if(requestCode == 10  && resultCode == RESULT_OK) {
				  
				 if(data!=null && data.getData() !=null){ 
					 //TODO: Request for a image description
					showInputDialogAndSendMessage(data);
					
				  }
			  }
			}
		
		protected void showInputDialogAndSendMessage(final Intent dataIntent) {

			// get prompts.xml view
			LayoutInflater layoutInflater = LayoutInflater.from(MotelDetailActivity.this);
			View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MotelDetailActivity.this);
			alertDialogBuilder.setView(promptView);

			final EditText editText = (EditText) promptView.findViewById(R.id.editTextDescription);
			// setup a dialog window
			alertDialogBuilder.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							//resultText.setText("Hello, " + editText.getText());
							String dataToSend = " {MotelID:"+motel.getId()+" Description: '"+ editText.getText() +"'}";
							UtilUI.sendMailBackground(MotelDetailActivity.this,
									CabannasrdApp.mailTo, "Sugerencia de Imagen DEVICE" +CabannasrdApp.DEVICE +motel.getName() + dataToSend,
									dataToSend
									, true, null, getString(R.string.thanksContibution), UtilUI.getRealPathFromUri(context,dataIntent.getData())) ;
							UtilUI.showToaststMessage(getString(R.string.sending), context);
						}
					})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
								}
							});

			// create an alert dialog
			AlertDialog alert = alertDialogBuilder.create();
			alert.show();
		}

		
		
}
