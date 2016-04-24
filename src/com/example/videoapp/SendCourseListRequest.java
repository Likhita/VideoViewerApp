package com.example.videoapp;

import java.io.IOException;
import java.net.UnknownHostException;

import android.os.AsyncTask;

public class SendCourseListRequest extends AsyncTask<Void,Void,Object> {
	
	VideoApplication appInstance;
	
	SendCourseListRequest(VideoApplication clientInstance){
		appInstance=clientInstance;	
	}
	
	protected Object doInBackground(Void... params){
		VideoClient client=appInstance.getClient();
		Response response = new Response("");
		try {
			Message message= new CourseList();
			client.send(message.getMessage());
			String courseList=client.readServerResponse();
			response= new CourseListResponse(courseList); 
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
}