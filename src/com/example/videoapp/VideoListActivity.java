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
import android.widget.ProgressBar; 
import android.widget.Toast;

//Displays the list of videos in the course
public class VideoListActivity extends ListActivity {

	Response response;
	VideoApplication appInstance;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
    	appInstance = ((VideoApplication)getApplicationContext());
        String courseName = getIntent().getStringExtra("courseSelected");
        CourseListResponse courseListResponse=(CourseListResponse) getIntent().getSerializableExtra("courseResponse");
        String courseId=courseListResponse.getCourseId(courseName);
        ProgressBar progressBar=(ProgressBar) findViewById(R.id.progressBar1);
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			VideoClient client=appInstance.getClient();
			if(client.isConnected()){
				try {
					progressBar.setVisibility(View.VISIBLE);
					response = (Response) new SendVideoListRequest(appInstance).execute(courseId).get();
					progressBar.setVisibility(View.GONE);
					ArrayList<String> videoList= ((VideoListResponse) response).getVideoNameList();
					setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,videoList));	
					if(videoList.size()==0){
			        	Toast.makeText(this, "There are no videos listed", Toast.LENGTH_SHORT).show();
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
        getMenuInflater().inflate(R.menu.activity_video_list, menu);
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

    /** Called when the user clicks the video List item */
    public void onListItemClick(ListView parent, View v, int position, long id) {
    	String videoSelected=(String) getListView().getItemAtPosition(position);
    	Intent intent = new Intent(this, PlayVideoActivity.class);
    	intent.putExtra("videoSelected",videoSelected);
    	intent.putExtra("videoResponse",response);
    	startActivity(intent);
    } 
}
