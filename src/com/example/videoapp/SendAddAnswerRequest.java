package com.example.videoapp;

import java.io.IOException;

import android.os.AsyncTask;

public class SendAddAnswerRequest extends AsyncTask<String,Void,String> {
	
	VideoApplication appInstance;
	
	SendAddAnswerRequest(VideoApplication clientInstance){
		appInstance=clientInstance;	
	}

	protected String doInBackground(String... params){
		VideoClient client=appInstance.getClient();
		Message message=new AnswerAdd(params[0],params[1]);
		Response response = new Response("");
		try {
			client.send(message.getMessage());
			response = new AnswerAddResponse(client.readServerResponse());	
		} catch (IOException e) {
			e.printStackTrace();
		}  
		return response.getResponse();
	}
}
