package com.example.videoapp;

import java.io.IOException;

import android.os.AsyncTask;

public class SendAddQuestionRequest extends AsyncTask<String,Void,String> {
		
	VideoApplication appInstance;
	
	SendAddQuestionRequest(VideoApplication clientInstance){
		appInstance=clientInstance;	
	}
	
	protected String doInBackground(String... params){
		VideoClient client=appInstance.getClient();
		Message message=new QuestionAdd(params[0],params[1],params[2]);
		Response response = null;
		try {
			client.send(message.getMessage());
			response = new QuestionAddResponse(client.readServerResponse());	
		} catch (IOException e) {
			e.printStackTrace();
		}  
		return response.getResponse();
	}
}