package com.example.videoapp;

import java.util.concurrent.ExecutionException;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

public class PlayVideoActivity extends Activity implements MediaPlayer.OnPreparedListener{

	private VideoView video;
	private EditText questionField;
	ProgressBar progressBar;
	String videoId;
	VideoApplication appInstance ;
	int currentVideoTime=0;
	   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video); 
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
       // getActionBar().setDisplayHomeAsUpEnabled(true);    
    	appInstance = ((VideoApplication)getApplicationContext());
        String videoName = getIntent().getStringExtra("videoSelected");
        VideoListResponse response=(VideoListResponse) getIntent().getSerializableExtra("videoResponse");
        video = (VideoView) findViewById(R.id.videoView);
        questionField = (EditText) findViewById(R.id.editText1);
        progressBar=(ProgressBar) findViewById(R.id.progressBar1);
        video.setOnPreparedListener(this);
        videoId=response.getVideoId(videoName);
        String url=response.getVideoUrl(videoName);
		video.setVideoPath(url);
        MediaController controls=new MediaController(this);
        video.setMediaController(controls);
        video.requestFocus();
        if(savedInstanceState != null){
        	currentVideoTime=savedInstanceState.getInt("video");
        }
    } 
    
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
    	super.onSaveInstanceState(savedInstanceState); 
    	savedInstanceState.putInt("video", currentVideoTime);    	  
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentVideoTime=savedInstanceState.getInt("video");
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_play_video, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
        	case R.id.menu_logout:
        		Toast.makeText(this, "Press the back button till you reach the login screen", Toast.LENGTH_SHORT).show();
        		return true;
    	}
        return super.onOptionsItemSelected(item);
    }

	public void onPrepared(MediaPlayer aPlayer) {
		progressBar.setVisibility(View.GONE);
		if(currentVideoTime !=0){
			if(currentVideoTime>0){
				aPlayer.seekTo(currentVideoTime);
			}else
				aPlayer.start();	
		}else{
			aPlayer.start();	
		}
	}
	
	
	String escapeSpecialCharacter(String aString){
    	String finalString ="";
    	String replacedString;
    	for(int i=0;i<aString.length();i++){
    		char c=aString.charAt(i);
    		replacedString = checkString(c);
    		finalString = finalString + replacedString;    		
    	}
    	return finalString;
    }

	String checkString(char c){
		String newString = "";
		if (c == ';' || c == '\\' || c==':')
			newString = "\\"+c;
		else 
			newString = ""+c;
		return newString;
	}

	public void questionList(View view) {
		currentVideoTime=video.getCurrentPosition();
		Intent intent = new Intent(this, QuestionListActivity.class);
    	intent.putExtra("videoId",videoId);
    	startActivityForResult(intent,100); // 100 is the request code for playing the video for a question
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		progressBar.setVisibility(View.VISIBLE);
        switch(resultCode){
        	case RESULT_OK:
        		String seekTime = data.getStringExtra("seektime");
        		int time=Integer.parseInt(seekTime);
        		currentVideoTime=time-5000;     //seeks the time to 5 secs before the question was submitted
            	break;  
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
	
	//when post Question button is clicked, the text in edit text is escaped for special characters and sent to the server 
	 public void postQuestion(View view) {
		 String question= questionField.getText().toString();
		 if(question.equals("")|| question.equals(" ")){
			 Toast.makeText(this, "Question field should not be empty", Toast.LENGTH_SHORT).show();
		 }else{
			 String escapedQuestion=escapeSpecialCharacter(question);
			 int time= video.getCurrentPosition();			 
			 String result = "";
			 ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			 NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			 if (networkInfo != null && networkInfo.isConnected()) {
				VideoClient client=appInstance.getClient();
				if(client.isConnected()){
					try {
						 result = new SendAddQuestionRequest(appInstance).execute(videoId,escapedQuestion,String.valueOf(time)).get();
					 } catch (InterruptedException e) {
						e.printStackTrace();
					 } catch (ExecutionException e) {
						e.printStackTrace();
					 }
				}
				else{
					Intent intent = new Intent(this,MainActivity.class); 
					startActivity(intent);
					Toast.makeText(this, "You have been logged out, Please login again", Toast.LENGTH_SHORT).show();				
					}
			 }
			 else
				 Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
			 
			 if (result.substring(0, result.indexOf(':')).equals("ok")){
				Toast.makeText(this, "Question posted", Toast.LENGTH_SHORT).show();
			    questionField.setText("");
			}
		 }
	 }
}
