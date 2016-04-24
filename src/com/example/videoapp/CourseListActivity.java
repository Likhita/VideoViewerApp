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

//displays the list of courses enrolled 
public class CourseListActivity extends ListActivity {

	Response response; 
	VideoApplication appInstance;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);
        //getActionBar().setDisplayHomeAsUpEnabled(true); 
        appInstance = ((VideoApplication)getApplicationContext());
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			VideoClient client=appInstance.getClient();
			if(client.isConnected()){
				try {
					response = (Response) new SendCourseListRequest(appInstance).execute().get();
					ArrayList<String> courseList= ((CourseListResponse) response).getCourseNameList();
					setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,courseList));
					if(courseList.size()==0){
			        	Toast.makeText(this, "There are no courses listed", Toast.LENGTH_SHORT).show();
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
        getMenuInflater().inflate(R.menu.activity_course_list, menu);
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
    public void finish(){
    	new SendLogoutRequest(appInstance).execute();
    	Toast.makeText(this, "Logging out..", Toast.LENGTH_SHORT).show();
    	Toast.makeText(this, "You have been logged out", Toast.LENGTH_SHORT).show();
    	super.finish();
    }
    
    /** Called when the user clicks the course List item */
    public void onListItemClick(ListView parent, View v, int position, long id) {
    	String courseSelected=(String) getListView().getItemAtPosition(position);
    	Intent intent = new Intent(this, VideoListActivity.class);
    	intent.putExtra("courseSelected",courseSelected);
    	intent.putExtra("courseResponse",response);
    	startActivity(intent);
    } 
}
