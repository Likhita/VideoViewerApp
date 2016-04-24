package com.example.videoapp;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;

import android.os.AsyncTask;

public class SendLoginRequest extends AsyncTask<String,Void,Object> {
	
	VideoApplication appInstance;
	
	SendLoginRequest(VideoApplication clientInstance){
		appInstance=clientInstance;	
	}
	
	protected Object doInBackground(String... params){
		VideoClient client=appInstance.getClient();
		Response response = new Response("");
		try {
			client.connect();
			Message message=new Nonce();
			client.send(message.getMessage());
			response= new NonceResponse(client.readServerResponse()); 
			message= new SafeLogin(params[0],params[1],((NonceResponse) response).getNounce());
			client.send(message.getMessage());
			String safeLogin=client.readServerResponse();
    		response= new SafeLoginResponse(safeLogin); 
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return response;
	}
}
