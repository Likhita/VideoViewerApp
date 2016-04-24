package com.example.videoapp;

import java.io.IOException;

import android.os.AsyncTask;

public class SendAnswerListRequest extends AsyncTask<String,Void,Object> {
	
	VideoApplication appInstance;
	
	SendAnswerListRequest(VideoApplication clientInstance){
		appInstance=clientInstance;	
	}

	protected Object doInBackground(String... params){
		VideoClient client=appInstance.getClient();
		Response response = new Response("");
		Message message= new AnswerList(params[0]);
		try {
			client.send(message.getMessage());
			response= new AnswerListResponse(client.readServerResponse());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
	}
