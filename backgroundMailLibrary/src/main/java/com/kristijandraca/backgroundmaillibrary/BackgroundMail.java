package com.kristijandraca.backgroundmaillibrary;

import java.util.ArrayList;

import com.kristijandraca.backgroundmaillibrary.mail.GmailSender;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class BackgroundMail {
	String TAG = "Bacground Mail Library";
	String username, password, mailto, subject, body, sendingMessage,
			sendingMessageSuccess;
	boolean processVisibility = true;
	ArrayList<String> attachments = new ArrayList<String>();
	Context mContext;

	public BackgroundMail(Context context) {
		this.mContext = context;
	}

	public void setGmailUserName(String string) {
		this.username = string;
	}

	public void setGmailPassword(String string) {
		this.password = string;
	}

	public void setProcessVisibility(boolean state) {
		this.processVisibility = state;
	}

	public void setMailTo(String string) {
		this.mailto = string;
	}

	public void setFormSubject(String string) {
		this.subject = string;
	}

	public void setFormBody(String string) {
		this.body = string;
	}

	public void setSendingMessage(String string) {
		this.sendingMessage = string;
	}

	public void setSendingMessageSuccess(String string) {
		this.sendingMessageSuccess = string;

	}
	
	public void setAttachment(String attachments) {
		this.attachments.add(attachments);

	}

	public void send() {
		boolean valid = true;
		if (username == null && username.trim().length() > 0) {
			Log.e(TAG, "You didn't set a Gmail username!");
			valid = false;
		}
		if (password == null && password.trim().length() > 0) {
			Log.e(TAG, "You didn't set a Gmail password!");
			valid = false;
		}
		if (mailto == null && mailto.trim().length() > 0) {
			Log.e(TAG, "You didn't set an email recipient!");
			valid = false;
		}
		if (Utils.isNetworkAvailable(mContext) == false) {
			Log.e(TAG, "User doesn't have a working internet connection!");
			valid = false;
		}
		if (valid == true) {
			new startSendingEmail().execute();
		}
	}

	public class startSendingEmail extends AsyncTask<String, Void, String> {
		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			if (processVisibility != false &&  sendingMessage!=null) {
				pd = new ProgressDialog(mContext);

				pd.setMessage(sendingMessage);

				pd.setCancelable(false);
				pd.show();
			}
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... arg0) {
			try {
				GmailSender sender = new GmailSender(username, password);
				if(!attachments.isEmpty()){
					for (int i = 0; i < attachments.size(); i++) {
							if(attachments.get(i).trim().length() > 0){
								sender.addAttachment(attachments.get(i));
							}
					}
				}
				sender.sendMail(subject, body, username, mailto);
			} catch (Exception e) {
				e.printStackTrace();
				if(e.getMessage() != null)
					Log.e(TAG, e.getMessage().toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
		try{
			if (processVisibility != false && sendingMessageSuccess!=null) {
				if(pd!=null){
					pd.dismiss();
				}
				Toast.makeText(mContext, sendingMessageSuccess,
							Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(e.getMessage() != null)
				Log.e(TAG, e.getMessage().toString());
		}
			super.onPostExecute(result);
		}
	}

}
