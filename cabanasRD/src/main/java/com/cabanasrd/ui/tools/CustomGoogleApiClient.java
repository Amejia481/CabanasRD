package com.cabanasrd.ui.tools;

import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;

public class CustomGoogleApiClient  implements ConnectionCallbacks, OnConnectionFailedListener {

	private  GoogleApiClient  apiClient = null;
	
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		
	}

}
