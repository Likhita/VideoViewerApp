package com.example.videoapp;

import java.io.IOException;

import android.os.AsyncTask;

public class SendVideoListRequest extends AsyncTask<String,Void,Object> {
	
	VideoApplication appInstance;
	
	SendVideoListRequest(VideoApplication clientInstance){
		appInstance=clientInstance;	
	}
	
	protected Object doInBackground(String... params){
		VideoClient client=appInstance.getClient();
		Response response = new Response("");
		Message message= new VideoList(params[0]);
		try {
			client.send(message.getMessage());
			response= new VideoListResponse(client.readServerResponse());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
}

