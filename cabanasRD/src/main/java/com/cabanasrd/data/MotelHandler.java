

package com.cabanasrd.data;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Path;
import android.content.Context;
import android.util.Log;

import com.cabanasrd.config.CabannasrdApp;
import com.cabanasrd.data.entities.AppConfigData;
import com.cabanasrd.data.entities.Availability;
import com.cabanasrd.data.entities.Motel;
import com.cabanasrd.data.entities.MotelLog;
import com.cabanasrd.data.entities.UserVote;

public class MotelHandler {

    
	private volatile ArrayList<Motel> motels = null;
	private static  MotelHandler  instance ;
	private HandlerMotelDataAcces dataAcces = null;
	
	
	public 	void addToLog(MotelLog log,Callback<Object> callbacks) {
		dataAcces.addToLog(log, callbacks);
	}
	public 	void getAppConfig(Callback<AppConfigData> callback) {
		dataAcces.getAppConfig(callback);
	}
	
	public 	void getAvailabilityOfMotel( int motelId,
			  Callback<ArrayList<Availability>> callback) {
		dataAcces.getAvailabilityOfMotel(motelId,  callback);
	}
	public 	void setAvailabilityOfMotel( int motelId,
			String device,int idAvailability,  Callback<Object> callback) {
		dataAcces.setAvailabilityOfMotel(new UserVote(""+idAvailability,""+ motelId, device), callback);
		
	}
	
	 public void saveToInternalStorage(String fileName,Context context) {
	        try {
	        	FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
	            
	            
	            ObjectOutputStream of = new ObjectOutputStream(fos);
	      
	            of.writeObject(getAllMotels());
	            of.flush();
	            of.close();
	            fos.close();
	        }
	        catch (Exception e) {
	            Log.e("InternalStorage", e.getMessage());
	        }
	}
	public void deleteOldData(String fileName, Context context) {
		context.deleteFile(fileName);
	}
	public synchronized void  remplaceMotel(Motel oldMotel,Motel newMotel,Context context){
		Log.i("arturo", "oldMotel "+oldMotel.getId() +" newMotel " +newMotel.getId());
		
		motels.set(motels.indexOf(oldMotel), newMotel);
		
		saveToInternalStorage(CabannasrdApp.MOTEL_PATH, context);
		
	}
	
	public Motel findMotelById(int id) {
		Motel motelFound = null;
		for(Motel motel: motels){
			if(motel.getId() == id){
				motelFound = motel;
			}
		}
		return motelFound;
	}
	public void LoaddDataAsyn(final Callback<Object> callback){
		
		dataAcces.getAllMotelsAsyn(new Callback<ArrayList<Motel>>() {
			
			@Override
			public void success(ArrayList<Motel> Motels, Response arg1) {
				 MotelHandler.this.motels = Motels;
				 
				
					
				 callback.success(null, null);
				 
			}
			
			@Override
			public void failure(RetrofitError arg0) {
				callback.failure(arg0);
				
			}
		});
	}
	public 	void getMotelDiff(@Path("id") int motelId, Callback<ArrayList<Motel>> callback) {
		dataAcces.getMotelDiff(motelId, callback);
	}
	private MotelHandler (){
		motels = new ArrayList<Motel>();
		dataAcces = new  HandlerMotelDataAccesRestFul();

	}
	
	public static MotelHandler getInstance(){
		if(instance == null){
			instance = new MotelHandler();
		}
		return  instance;
	}
	
	public ArrayList<Motel> getMotels() {
		return motels;
	}

	public void setMotels(ArrayList<Motel> motels) {
		this.motels = motels;
	}

	public ArrayList<Motel>  getAllMotels(){
		return motels;
	} 
	
	public 	void getMotel( int motelId, Callback<Motel> callback) {
		dataAcces.getMotel(motelId, callback);
	}
	
	

    public ArrayList<Motel> searchMotelOrHotelByPattern(String patterToSearch){
        ArrayList<Motel> motelsFound = new ArrayList<Motel>();
        patterToSearch =   patterToSearch.toLowerCase(Locale.US);
        for(Motel motel: motels){
        	
        	 
        	
        	
			String name  = (Normalizer.normalize(motel.getName().toLowerCase(Locale.US), Normalizer.Form.NFD)).replaceAll("[^\\p{ASCII}]", "") ;
			String state = motel.getState().getName().toLowerCase(Locale.US);
        	
            if( ( name.contains(patterToSearch)) ||  (state !=null && state.contains(patterToSearch)) ){
            	motelsFound.add(motel);
            }
        }
        return motelsFound;
    }
    
   
}
