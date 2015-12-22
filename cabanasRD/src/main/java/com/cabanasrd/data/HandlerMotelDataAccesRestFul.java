
package com.cabanasrd.data;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

import com.cabanasrd.config.CabannasrdApp;
import com.cabanasrd.data.entities.AppConfigData;
import com.cabanasrd.data.entities.Availability;
import com.cabanasrd.data.entities.Motel;
import com.cabanasrd.data.entities.MotelLog;
import com.cabanasrd.data.entities.Question;
import com.cabanasrd.data.entities.Rating;
import com.cabanasrd.data.entities.User;
import com.cabanasrd.data.entities.UserAnswer;
import com.cabanasrd.data.entities.UserVote;

public class HandlerMotelDataAccesRestFul implements HandlerMotelDataAcces {



	@Override
	public void getAllMotelsAsyn(Callback<ArrayList<Motel>> callback) {
		RestAdapter restAdapter = new RestAdapter.Builder()
	    .setEndpoint(CabannasrdApp.serverURL).setLogLevel(RestAdapter.LogLevel.NONE)
	    .build();
//TODO: BORRAR INFORMACION LOG setLogLevel
		HandlerMotelDataAcces service = restAdapter.create(HandlerMotelDataAcces.class);
		service.getAllMotelsAsyn(callback);
	}

	@Override
	public void getMotelRating(@Path("id") int motelId,@Path("device") String device, Callback<Rating> callback) {
		RestAdapter restAdapter = new RestAdapter.Builder()
	    .setEndpoint(CabannasrdApp.serverURL).setLogLevel(RestAdapter.LogLevel.NONE)
	    .build();
//TODO: BORRAR INFORMACION LOG setLogLevel
		HandlerMotelDataAcces service = restAdapter.create(HandlerMotelDataAcces.class);
		service.getMotelRating(motelId, device, callback);
	}

	@Override
	public void createRating(@Body Rating rating, Callback<Rating> cb) {
		RestAdapter restAdapter = new RestAdapter.Builder()
	    .setEndpoint(CabannasrdApp.serverURL).setLogLevel(RestAdapter.LogLevel.NONE)
	    .build();
		//TODO: BORRAR INFORMACION LOG setLogLevel
		HandlerMotelDataAcces service = restAdapter.create(HandlerMotelDataAcces.class);
		service.createRating(rating, cb);
	}

	@Override
	public void createDevice(@Body User device, Callback<User> cb) {
		RestAdapter restAdapter = new RestAdapter.Builder()
	    .setEndpoint(CabannasrdApp.serverURL).setLogLevel(RestAdapter.LogLevel.NONE)
	    .build();
		//TODO: BORRAR INFORMACION LOG setLogLevel
		HandlerMotelDataAcces service = restAdapter.create(HandlerMotelDataAcces.class);
		service.createDevice(device, cb);
		
	}

	@Override
	@GET("/api/Diff/{id}")
	public 	void getMotelDiff(@Path("id") int motelId, Callback<ArrayList<Motel>> callback) {
		RestAdapter restAdapter = new RestAdapter.Builder()
	    .setEndpoint(CabannasrdApp.serverURL).setLogLevel(RestAdapter.LogLevel.NONE)
	    .build();
		//TODO: BORRAR INFORMACION LOG setLogLevel
		HandlerMotelDataAcces service = restAdapter.create(HandlerMotelDataAcces.class);
		service.getMotelDiff(motelId, callback);
		
	}

	@Override
	public 	void getMotel(@Path("id") int motelId, Callback<Motel> callback) {
		RestAdapter restAdapter = new RestAdapter.Builder()
	    .setEndpoint(CabannasrdApp.serverURL).setLogLevel(RestAdapter.LogLevel.NONE)
	    .build();
		//TODO: BORRAR INFORMACION LOG setLogLevel	
		HandlerMotelDataAcces service = restAdapter.create(HandlerMotelDataAcces.class);
		service.getMotel(motelId, callback);
		
	}

	@Override
	public 	void getAppConfig(Callback<AppConfigData> callback) {
		RestAdapter restAdapter = new RestAdapter.Builder()
	    .setEndpoint(CabannasrdApp.serverURL).setLogLevel(RestAdapter.LogLevel.NONE)
	    .build();
		//TODO: BORRAR INFORMACION LOG setLogLevel	
		HandlerMotelDataAcces service = restAdapter.create(HandlerMotelDataAcces.class);
		service.getAppConfig(callback);
		
	}

	@Override
	
	public 	void getAvailabilityOfMotel( int motelId,
	
			
		 Callback<ArrayList<Availability>>  callback) {
		
		
		RestAdapter restAdapter = new RestAdapter.Builder()
	    .setEndpoint(CabannasrdApp.serverURL).setLogLevel(RestAdapter.LogLevel.NONE)
	    .build();
		HandlerMotelDataAcces service = restAdapter.create(HandlerMotelDataAcces.class);
		service.getAvailabilityOfMotel(motelId,callback);
		
	}

	@Override
	
	public 	void setAvailabilityOfMotel( UserVote  userVote,  Callback<Object> callback) {
		RestAdapter restAdapter = new RestAdapter.Builder()
	    .setEndpoint(CabannasrdApp.serverURL).setLogLevel(RestAdapter.LogLevel.NONE)
	    .build();
		HandlerMotelDataAcces service = restAdapter.create(HandlerMotelDataAcces.class);
		service.setAvailabilityOfMotel(userVote,callback);
		
	}

	@Override
	public 	void addToLog(MotelLog log,Callback<Object> callback) {
		RestAdapter restAdapter = new RestAdapter.Builder()
	    .setEndpoint(CabannasrdApp.serverURL).setLogLevel(RestAdapter.LogLevel.NONE)
	    .build();
		HandlerMotelDataAcces service = restAdapter.create(HandlerMotelDataAcces.class);
		service.addToLog(log, callback);
		
	}

	@Override
	public void getPendingQuestions(String deviceID, Callback<ArrayList<Question>> callback) {
		RestAdapter restAdapter = new RestAdapter.Builder()
				.setEndpoint("http://desarrollo-cabanasrdapi.azurewebsites.net/").setLogLevel(RestAdapter.LogLevel.NONE)
				.build();
		HandlerMotelDataAcces service = restAdapter.create(HandlerMotelDataAcces.class);
		service.getPendingQuestions(deviceID,callback);
	}

	@Override
	public void saveQuestions(UserAnswer userAnswer, Callback<Object> callback) {
		RestAdapter restAdapter = new RestAdapter.Builder()
				.setEndpoint("http://desarrollo-cabanasrdapi.azurewebsites.net/").setLogLevel(RestAdapter.LogLevel.FULL)
				.build();
		HandlerMotelDataAcces service = restAdapter.create(HandlerMotelDataAcces.class);
		service.saveQuestions(userAnswer, callback);
	}


}

