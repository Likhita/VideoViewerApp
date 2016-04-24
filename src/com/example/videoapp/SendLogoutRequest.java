package com.example.videoapp;

import java.io.IOException;
import java.net.UnknownHostException;
import android.os.AsyncTask;

public class SendLogoutRequest extends AsyncTask<Void,Void,Void> {

	VideoApplication appInstance;
	
	SendLogoutRequest(VideoApplication clientInstance){
		appInstance=clientInstance;	
	}
	
	protected Void doInBackground(Void... params){
		VideoClient client=appInstance.getClient();		
		try {
			client.closeSocket();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
