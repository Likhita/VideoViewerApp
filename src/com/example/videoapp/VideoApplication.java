package com.example.videoapp;

import android.app.Application;

//the Socket object is stored in this class and used in various activities
public class VideoApplication extends Application {
    
	private VideoClient client;
    
    public VideoClient getClient(){
        return client;
    }
    
    public void setClient(VideoClient client){
        this.client =  client;
    }
}