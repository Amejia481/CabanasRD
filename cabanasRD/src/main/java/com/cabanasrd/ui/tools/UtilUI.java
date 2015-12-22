package com.cabanasrd.ui.tools;


import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cabanasrd.R;
import com.cabanasrd.config.CabannasrdApp;
import com.cabanasrd.data.entities.MotelService;
import com.cabanasrd.data.entities.Question;
import com.kristijandraca.backgroundmaillibrary.BackgroundMail;
import com.kristijandraca.backgroundmaillibrary.Utils;

public class UtilUI {
	
	 public  static MotelService motelService = null;
	 public  static String telephone = null;
//	public static InputFilter filtroDigitoOLetra = new InputFilter() { 
//        @Override
//        public CharSequence filter(CharSequence source, int start, int end, 
//                        Spanned dest, int dstart, int dend) { 
//                for (int i = start; i < end; i++) { 
//                        if (!Character.isLetterOrDigit(source.charAt(i)) && !Character.isSpace(source.charAt(i)) &&  !Character.isWhitespace(source.charAt(i))) { 
//                                return ""; 
//                        } 
//                } 
//                return null;
//               
//        }
//       
//	};
//
//       
//
//	public static void hideViews(View actualView , int...idViews){
//		for(int idView : idViews){
//			View view = actualView.findViewById(idView);
//			view.setVisibility(View.GONE);
//		}
//	}

	

	 
	 
	 public  static void  sendMailBackground(final Context context,String to,String subject,String body,boolean processVisibility,String sendingMessage,String sendingMessageSuccess,
			 String attachmentPath){
		 
		 	BackgroundMail bm = new BackgroundMail(context);
			bm.setGmailUserName(CabannasrdApp.mailService);
			bm.setGmailPassword(Utils.decryptIt(CabannasrdApp.passMailService));
			bm.setMailTo(to);
			
			if(subject !=null){
				bm.setFormSubject(subject);
			}
			if(body !=null){
				bm.setFormBody(body);
			}
			if(sendingMessage !=null){
				bm.setSendingMessage(sendingMessage);
			}
			if(sendingMessageSuccess !=null){
				bm.setSendingMessageSuccess(sendingMessageSuccess);		
			}
		
		    bm.setProcessVisibility(processVisibility);
         	
		    if(attachmentPath !=null){
		    	bm.setAttachment(attachmentPath);
		    }
		    bm.send();
		 
	 }
	 
	 public static String getRealPathFromUri(Context context, Uri contentUri) {
		    Cursor cursor = null;
		    try {
		        String[] proj = { MediaStore.Images.Media.DATA };
		        cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
		        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		        cursor.moveToFirst();
		        return cursor.getString(column_index);
		    } finally {
		        if (cursor != null) {
		            cursor.close();
		        }
		    }
		}
public  static MotelService  getMotelServiceDialog(final Activity activity,final Runnable callbackPositiveAction ){
	AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	    // Get the layout inflater
	
	LayoutInflater inflater = activity.getLayoutInflater();
	    final View viewRoot =  inflater.inflate(R.layout.custom_dialog_motel_services, null);
	     View custom_title =  inflater.inflate(R.layout.custom_title, null);

	     
	     ((TextView)custom_title.findViewById(R.id.txtTitleDialog)).setText(R.string.SeciceSuggested);
	     
	    builder.setCustomTitle(custom_title);     
	    final   Spinner motels_services_types = (Spinner) viewRoot.findViewById(R.id.motels_services_types);
	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity,
		        R.array.motels_services_types, android.R.layout.simple_spinner_item);
	
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		motels_services_types.setAdapter(adapter);
		
		final Spinner motels_services_currency_type = (Spinner) viewRoot.findViewById(R.id.motels_services_currency_type);
	    ArrayAdapter<CharSequence> adapter_motels_services_currency_type = ArrayAdapter.createFromResource(activity,
		        R.array.motels_services_currency_type, android.R.layout.simple_spinner_item);
	
	    adapter_motels_services_currency_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		motels_services_currency_type.setAdapter(adapter_motels_services_currency_type);
		
		builder.setCancelable(false);
	
		   
	
	
		
		
		builder.setView(viewRoot) .setNegativeButton(R.string.cancel, null);   
		builder.setView(viewRoot) .setPositiveButton(R.string.lblAdd, null);   
	
		 final AlertDialog dialog = 			    builder.create();
		 dialog.show();
	     //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
	     dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
	           {            
	               @Override
	               public void onClick(View v)
	               {
	            	   EditText  txtMotelServiceName  =  (EditText) viewRoot.findViewById(R.id.txtMotelServiceName);
	            	   EditText  txtMotelServicePrice  =  (EditText) viewRoot.findViewById(R.id.txtMotelServicePrice);
	            	  
	            	   if(!UtilUI.validateEmptyFields(activity,txtMotelServiceName,txtMotelServicePrice)){
		            	   
		            	   
	            		   motelService = 	   new MotelService(txtMotelServiceName.getText().toString(),  Double.parseDouble(txtMotelServicePrice.getText().toString()),
		            			   motels_services_types.getSelectedItemPosition(), motels_services_currency_type.getSelectedItemPosition(), "");
	            		   dialog.dismiss();
	            		   callbackPositiveAction.run();
		            	   
	            	   }
	            	  
	               }
	           });
	
	    
	
	
	    
	
	
	return motelService;

}

public  static String  getTelephoneDialog(final Activity activity,final Runnable callbackPositiveAction ){
	AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	    // Get the layout inflater
	    LayoutInflater inflater = activity.getLayoutInflater();
	    final View viewRoot =  inflater.inflate(R.layout.custom_dialog_telephone, null);
	     View custom_title =  inflater.inflate(R.layout.custom_title, null);

	     
	     ((TextView)custom_title.findViewById(R.id.txtTitleDialog)).setText(R.string.TelephoneSuggested);
	     
		builder.setCustomTitle(custom_title).setCancelable(false)
			.setView(viewRoot) 
			.setNegativeButton(R.string.cancel, null)
			.setView(viewRoot) 
			.setPositiveButton(R.string.lblAdd, null);   
	
		 final AlertDialog dialog = 			    builder.create();
		 dialog.show();
	     //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
	     dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
	           {            
	               @Override
	               public void onClick(View v)
	               {
	            	   EditText  txtTelephone  =  (EditText) viewRoot.findViewById(R.id.txtTelephone);
	            	   
	            	  
	            	   if(!UtilUI.validateEmptyFields(activity,txtTelephone)){
		            	   
		            	   
	            		   telephone = 	   txtTelephone.getText().toString();
	            		   dialog.dismiss();
	            		   callbackPositiveAction.run();
		            	   
	            	   }
	            	  
	               }
	           });
	
	    
	
	
	    
	
	
	return telephone;

}

public static boolean validateMultipleChoiceWithMessage(final Activity activity,boolean ...choices){
	boolean isValid = validateMultipleChoice(choices);
	if( !isValid){
		showToaststMessage(activity.getString(R.string.msErrorMultipleChoiceInvalid), activity);
	}
	
	return isValid;
}

public static boolean validateMultipleChoice(boolean ...choices){
	boolean isValid = false;
	for( boolean  choice :choices){
		if(choice){
			isValid = true;
			break;
		}
	}
	
	return isValid;
}

public static void showAlertDialog(Context context, String title, View view,int stringButton,final Runnable callBackaPositiveButton){
	 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

			// set title
			alertDialogBuilder.setTitle(title);

			// set dialog message
			alertDialogBuilder
				.setView(view)
				.setCancelable(false)
				.setPositiveButton(stringButton,new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked
						if(callBackaPositiveButton!=null){
							
							callBackaPositiveButton.run();
						}
					}
				  });

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.setCanceledOnTouchOutside(false);
				alertDialog.show();
				alertDialogBuilder = null;
}
public static void showAlertDialog(Context context, String title, String message,int stringButton,final Runnable callBackaPositiveButton){
	 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

			// set title
			alertDialogBuilder.setTitle(title);

			// set dialog message
			alertDialogBuilder
				.setMessage(message)
				.setCancelable(false)
				.setPositiveButton(stringButton,new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked
						if(callBackaPositiveButton!=null){
							
							callBackaPositiveButton.run();
						}
					}
				  });

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.setCanceledOnTouchOutside(false);
				alertDialog.show();
				alertDialogBuilder = null;
}
	public  static void  getQuestionDialog(final Activity activity,final Question question,final Runnable callbackPositiveAction,final Runnable callbackNegativeAction ){
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		LayoutInflater inflater = activity.getLayoutInflater();
		View custom_title =  inflater.inflate(R.layout.custom_title, null);
		((TextView)custom_title.findViewById(R.id.txtTitleDialog)).setText(question.getText());


		String [] answers =  new String[question.getAnswers().size()];
		for(int i =0 ; i < question.getAnswers().size(); i++ ) {
			answers[i] = question.getAnswers().get(i).getText();
		}
		builder.setCustomTitle(custom_title)
				.setSingleChoiceItems(answers, -1, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						question.setUserAnswerID(question.getAnswers().get(which).getId());
						callbackPositiveAction.run();
						dialog.dismiss();
					}
				}).
				setCancelable(false)
				.setNegativeButton(R.string.later, null);

		final AlertDialog dialog = 			    builder.create();
		dialog.show();
		//Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
		dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{


					callbackNegativeAction.run();
					dialog.dismiss();




			}
		});









	}
public  static void  getCreditCardsDialog(final Activity activity,final ArrayList<String> creditCards,final Runnable callbackPositiveAction ){
	AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	LayoutInflater inflater = activity.getLayoutInflater();
	
	final String[] credit_cards_type_string =activity.getResources().getStringArray(R.array.credit_cards_type);
	final boolean []  credit_cards_selected = {true,true,false,false};
	creditCards.add(credit_cards_type_string[0]);
	creditCards.add(credit_cards_type_string[1]);
    View custom_title =  inflater.inflate(R.layout.custom_title, null);

    
    ((TextView)custom_title.findViewById(R.id.txtTitleDialog)).setText(R.string.CreditCardSuggested);
   	
	
	
	    builder.setCustomTitle(custom_title)
	    .setMultiChoiceItems(R.array.credit_cards_type,new boolean[]{true,true,false,false},new OnMultiChoiceClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
			
				if (isChecked) {
                    // If the user checked the item, add it to the selected items
					creditCards.add(credit_cards_type_string[which]);
					credit_cards_selected[which] = true;
                } else if (creditCards.contains(credit_cards_type_string[which])) {
                    // Else, if the item is already in the array, remove it 
                	
                	creditCards.remove(creditCards.indexOf(credit_cards_type_string[which]));
                	credit_cards_selected[which] = false;
                	
                }
				
			}
		});
	    
	
	    	
		builder.setCancelable(false);
	
		   
	
	
		
		
		builder.setNegativeButton(R.string.cancel, null).setPositiveButton(R.string.lblAdd, null);   
	
		 final AlertDialog dialog = 			    builder.create();
		 dialog.show();
	     //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
	     dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
	           {            
	               @Override
	               public void onClick(View v)
	               {
	            	  
	            	   
	            	   
	            	   if(UtilUI.validateMultipleChoiceWithMessage(activity,credit_cards_selected)){
	            		   
	            		   dialog.dismiss();
	            		   callbackPositiveAction.run();
		            	   
	            	   }else{
	            		   dialog.dismiss();
	            	   }
	            	  
	               }
	           });
	
	    
	
	
	    
	
	
	

}


	public static boolean isGPSDisabled(Context contextActual){
		 LocationManager  locationManager = (LocationManager) contextActual.getSystemService(Context.LOCATION_SERVICE);
		  boolean estaDesabilitadoGPS = false;
		  
		  int locationMode = 0;
		    String locationProviders;

		    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
		        try {
		            locationMode = Settings.Secure.getInt(contextActual.getContentResolver(), Settings.Secure.LOCATION_MODE);

		        } catch (SettingNotFoundException e) {
		            e.printStackTrace();
		        }

		        estaDesabilitadoGPS = locationMode == Settings.Secure.LOCATION_MODE_OFF;

		    }else{
		    	if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)&& ! locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			    	 estaDesabilitadoGPS = true;
			    }
		    }

		  
		  
		 return estaDesabilitadoGPS;
	 }

	 public static void  validateGPSIsDisabledWithMessage(Context contextActual){
		 if(isGPSDisabled(  contextActual)){
			 showMessageGPSDisabled(contextActual);
		 }
		 
	 }
	 public static void showMessageGPSDisabled(final Context contextActual){
	
	        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(contextActual);
	        alertDialogBuilder.setMessage(contextActual.getString(R.string.msGPSdisabled))
	        .setCancelable(false)
	        .setPositiveButton(contextActual.getString(R.string.ok),
	                new DialogInterface.OnClickListener(){
	            public void onClick(DialogInterface dialog, int id){
	                Intent callGPSSettingIntent = new Intent(
	                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	                contextActual.startActivity(callGPSSettingIntent);
	            }
	        });
	        
	        alertDialogBuilder.setNegativeButton(contextActual.getString(R.string.cancel),
	                new DialogInterface.OnClickListener(){
	            public void onClick(DialogInterface dialog, int id){
	                dialog.cancel();
	            }
	        });
	        AlertDialog alert = alertDialogBuilder.create();
	        alert.show();
	}
//	 public static void removerAnimacionError(Context context,EditText txt) {
//		 txt.setError(null);
//	 }
	
	 
	 public static boolean hasConnection(Context context) {
		    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
		        Context.CONNECTIVITY_SERVICE);
		    boolean isConnection = false;
		    NetworkInfo wifiNetwork =null;
		    try{
		     wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		    }catch(Exception e){
		    	System.out.println(e);
		    }
		    if (wifiNetwork != null && wifiNetwork.isConnected()) {
		    	isConnection = true;
		    }
		    NetworkInfo mobileNetwork = null;
		    try{
		     mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		    }catch(Exception e){
		    	System.out.println(e);
		    }
		    if (mobileNetwork != null && mobileNetwork.isConnected()) {
		    	isConnection = true;
		    }
		    NetworkInfo activeNetwork = null;
try{
		   activeNetwork = cm.getActiveNetworkInfo();
	 }catch(Exception e){
	    	System.out.println(e);
	    }
		    if (activeNetwork != null && activeNetwork.isConnected()) {
		    	isConnection = true;
		    }

		    return isConnection;
		  }
	 
	 public static boolean validateInternetConnetion(Context context, Runnable callBackConfirmButton){
		 boolean connection = false;
		 if( hasConnection(context)){
			 connection = true;
		}else{
			
			
			showAlertDialog(context, context.getString(R.string.message), context.getString(R.string.msInternetNoConnection), R.string.ok, callBackConfirmButton );
			 
			
			
		}
		return connection;
	 }
	 
	 public static boolean validateInternetConnetion(Context context) {
		boolean connection = false;
		 if( hasConnection(context)){
			 connection = true;
		}else{
			
			
			showAlertDialog(context, context.getString(R.string.message), context.getString(R.string.msInternetNoConnection), R.string.ok, null);
			 
			
			
		}
		return connection;
	 }
//	 public static boolean validarCamposCantidadMinideCaracteres(Context context,int cantidadRequerida,EditText... txts) {
//		 boolean isCampoCantidadMinideCaracteres = false;
//		 for(EditText itemTxt : txts){
//			 if(itemTxt.getText().toString().trim().length() <cantidadRequerida ){
//				 Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
//				 itemTxt.startAnimation(shake);
//				 itemTxt.setError(context.getString(R.string.msErrorCantidadMinimaDeCaracteres) +" "+cantidadRequerida +" "+ context.getString(R.string.lblCaracteres) );
//				 isCampoCantidadMinideCaracteres = true;
//			 }else{
//				 itemTxt.setError(null);
//			 }
//		 }
//		 return isCampoCantidadMinideCaracteres;
//		 
//	 }
//	 public static boolean validarCamposCantidadMinideCaracteres(Context context,int cantidadRequerida,EditText itemTxt) {
//		 boolean isCampoCantidadMinideCaracteres = false;
//			 if(itemTxt.getText().toString().trim().length() <cantidadRequerida ){
//				 isCampoCantidadMinideCaracteres = true;
//		 }
//		 return isCampoCantidadMinideCaracteres;
//		 
//	 }
//	 public static void limpiarCampos(EditText... txts) {
//		 for(EditText itemTxt : txts){
//			 	 itemTxt.setText("");
//		 }
//	 }
	 public static void showToaststMessage(String strTexto,Context context){
			String strNoInternet = strTexto;
			Toast.makeText(context,strNoInternet,Toast.LENGTH_LONG).show();

		}
//	 
	 public static boolean validateEmptyFields(Context context,EditText... txts) {
		 boolean isCampoVacio = false;
		 for(EditText itemTxt : txts){
			 if("".equals(itemTxt.getText().toString().trim())){
				 Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
				 itemTxt.startAnimation(shake);
				 itemTxt.setError(context.getString(R.string.msErrorEmptyField));
				 itemTxt.setText("");
				 isCampoVacio = true;
			 }else{
				 itemTxt.setError(null);
			 }
		 }
		 return isCampoVacio;
		 
	 }

	 public static void hideSoftKeyBoard(Activity activity) {
			InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm != null )
				imm.hideSoftInputFromWindow(activity.getWindow().getCurrentFocus().getWindowToken(), 0);
		}

	 
	 public  static MotelService  getMotelServiceMenuDialog(final Activity activity,final Runnable callbackPositiveAction ){
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			    // Get the layout inflater
			
			LayoutInflater inflater = activity.getLayoutInflater();
			    final View viewRoot =  inflater.inflate(R.layout.custom_dialog_motel_services, null);
			     View custom_title =  inflater.inflate(R.layout.custom_title, null);

			     
			     ((TextView)custom_title.findViewById(R.id.txtTitleDialog)).setText(R.string.menuSuggested);
			     
			    builder.setCustomTitle(custom_title);     
			    final   Spinner motels_services_types = (Spinner) viewRoot.findViewById(R.id.motels_services_types);
			    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity,
				        R.array.motels_services_types_menu, android.R.layout.simple_spinner_item);
			
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				
				motels_services_types.setAdapter(adapter);
				
				final Spinner motels_services_currency_type = (Spinner) viewRoot.findViewById(R.id.motels_services_currency_type);
			    ArrayAdapter<CharSequence> adapter_motels_services_currency_type = ArrayAdapter.createFromResource(activity,
				        R.array.motels_services_currency_type, android.R.layout.simple_spinner_item);
			
			    adapter_motels_services_currency_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				
				motels_services_currency_type.setAdapter(adapter_motels_services_currency_type);
				
				builder.setCancelable(false);
			
				   
			
			
				
				
				builder.setView(viewRoot) .setNegativeButton(R.string.cancel, null);   
				builder.setView(viewRoot) .setPositiveButton(R.string.lblAdd, null);   
			
				 final AlertDialog dialog = 			    builder.create();
				 dialog.show();
			     //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
			     dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
			           {            
			               @Override
			               public void onClick(View v)
			               {
			            	   EditText  txtMotelServiceName  =  (EditText) viewRoot.findViewById(R.id.txtMotelServiceName);
			            	   EditText  txtMotelServicePrice  =  (EditText) viewRoot.findViewById(R.id.txtMotelServicePrice);
			            	  
			            	   if(!UtilUI.validateEmptyFields(activity,txtMotelServiceName,txtMotelServicePrice)){
				            	   
				            	   
			            		   motelService = 	   new MotelService(txtMotelServiceName.getText().toString(),  Double.parseDouble(txtMotelServicePrice.getText().toString()),
				            			   motels_services_types.getSelectedItemPosition(), motels_services_currency_type.getSelectedItemPosition(), "");
			            		   dialog.dismiss();
			            		   callbackPositiveAction.run();
				            	   
			            	   }
			            	  
			               }
			           });
			
			    
			
			
			    
			
			
			return motelService;

		}
}
