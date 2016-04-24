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
import android.widget.ListView;
import android.widget.Toast;

public class QuestionListActivity extends ListActivity {

	Response response;
	VideoApplication appInstance;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list); 
        //getActionBar().setDisplayHomeAsUpEnabled(true);
    	appInstance = ((VideoApplication)getApplicationContext());
        String videoId = getIntent().getStringExtra("videoId");
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			VideoClient client=appInstance.getClient();
			if(client.isConnected()){
				try { 
					response = (Response) new SendQuestionListRequest(appInstance).execute(videoId).get();
					ArrayList<String> questionList= ((QuestionListResponse) response).getQuestionTextList();
					setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,questionList));	
					if(questionList.size()==0){
			        	Toast.makeText(this, "There are no questions posted", Toast.LENGTH_SHORT).show();
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
				Toast.makeText(this, "You have been logged out, Please login again", Toast.LENGTH_SHORT).show();			}
		}
		else
	        Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_question_list, menu);
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
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
        switch(resultCode){
        	case RESULT_OK:
        		Intent result = getIntent();
        		String seekTime = data.getStringExtra("seektime");
            	result.putExtra("seektime", seekTime);
            	setResult(RESULT_OK, result);
            	finish();         
            	break;  
        }
       
    }
    
    /** Called when the user clicks the Question list item */
    public void onListItemClick(ListView parent, View v, int position, long id) {
    	String questionSelected=(String) getListView().getItemAtPosition(position);
    	Intent intent = new Intent(this, AnswerListActivity.class);
    	intent.putExtra("questionSelected",questionSelected);
    	intent.putExtra("questionListResponse",response);
    	startActivityForResult(intent,100);
    } 
}
