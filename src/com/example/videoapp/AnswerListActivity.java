package com.example.videoapp;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AnswerListActivity extends ListActivity {

	Response response;
	EditText editAnswer; 
	String questionId;
	VideoApplication appInstance;
	QuestionListResponse questionListResponse;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_list);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
    	appInstance = ((VideoApplication)getApplicationContext());
        TextView question= (TextView) findViewById(R.id.textView2);
        editAnswer = (EditText) findViewById(R.id.editText1);
        String questionText = getIntent().getStringExtra("questionSelected");
        question.setText(questionText);
        questionListResponse=(QuestionListResponse) getIntent().getSerializableExtra("questionListResponse");
        questionId=questionListResponse.getQuestionId(questionText);
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			VideoClient client=appInstance.getClient();
			if(client.isConnected()){
				try {
					response = (Response) new SendAnswerListRequest(appInstance).execute(questionId).get();
					ArrayList<String> answerList= ((AnswerListResponse) response).getAnswerTextList();
					setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,answerList));
					if(answerList.size()==0){
			        	Toast.makeText(this, "There are no answers posted", Toast.LENGTH_SHORT).show();
					}
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_answer_list, menu);
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
    
    public void postAnswer(View view) {
		 String answer= editAnswer.getText().toString();
		 if(answer.equals("")|| answer.equals(" ")){
			 Toast.makeText(this, "Answer field should not be empty", Toast.LENGTH_SHORT).show();
		 }else{
			 String escapedAnswer=escapeSpecialCharacter(answer);
			 String result = "";
			 ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		     NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			 if (networkInfo != null && networkInfo.isConnected()) {
					VideoClient client=appInstance.getClient();
					if(client.isConnected()){
						try {
							 result = new SendAddAnswerRequest(appInstance).execute(questionId,escapedAnswer).get();
							 response = (Response) new SendAnswerListRequest(appInstance).execute(questionId).get();
							 ArrayList<String> answerList= ((AnswerListResponse) response).getAnswerTextList();
							 setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,answerList));
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
				Toast.makeText(this, "Answer posted", Toast.LENGTH_SHORT).show();
			    editAnswer.setText("");
			}
		 }
    }
    
    public void playVideo(View view) {
    	String seekTime=questionListResponse.getVideoTime(questionId);
    	Intent result = getIntent();
    	result.putExtra("seektime", seekTime);
    	setResult(RESULT_OK, result);
    	finish();
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
}
