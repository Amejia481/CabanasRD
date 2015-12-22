
package com.cabanasrd.data;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

import com.cabanasrd.data.entities.AppConfigData;
import com.cabanasrd.data.entities.Availability;
import com.cabanasrd.data.entities.Motel;
import com.cabanasrd.data.entities.MotelLog;
import com.cabanasrd.data.entities.Question;
import com.cabanasrd.data.entities.Rating;
import com.cabanasrd.data.entities.User;
import com.cabanasrd.data.entities.UserAnswer;
import com.cabanasrd.data.entities.UserVote;

public interface HandlerMotelDataAcces {
	 
	@GET("/api/Cabanias")
	public void  getAllMotelsAsyn(Callback<ArrayList<Motel>> callback);

	@GET("/api/Cabanias/{id}")
	void getMotel(@Path("id") int motelId, Callback<Motel> callback);
	
	@GET("/api/Ratings/{id}")
	void getMotelRating(@Path("id") int motelId,@Query("device") String device, Callback<Rating> callback);
	
	@POST("/api/Ratings")
	void createRating(@Body Rating rating, Callback<Rating> cb);
	
	@POST("/api/Devices")
	void createDevice(@Body User device, Callback<User> cb);
	
	@GET("/api/Cabanias/Diff/{id}")
	void getMotelDiff(@Path("id") int motelId, Callback<ArrayList<Motel>> callback);
	
	@GET("/Home/getappversion")
	void getAppConfig( Callback<AppConfigData> callback);
	
	@GET("/api/Availability/{id}/")
	void  getAvailabilityOfMotel(@Path("id") int motelId, Callback<ArrayList<Availability>> callback);
	
	
	@POST("/api/Availability/")
	void setAvailabilityOfMotel(@Body  UserVote userVote, Callback<Object> callback);
	
	@POST("/api/Log/")
	void addToLog(@Body MotelLog log,Callback<Object> callback);

	@GET("/api/Survey/PendingQuestions")
	void getPendingQuestions(@Query("idDevice") String deviceID, Callback<ArrayList<Question>> callback);


	@POST("/api/Survey/SendAnswer")
	void saveQuestions(@Body UserAnswer userAnswer, Callback<Object> callback);
	
}
