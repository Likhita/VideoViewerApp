package com.example.videoapp;

import java.util.concurrent.ExecutionException;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;	
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	Response response;
	ProgressBar progressBar;
	VideoApplication appInstance ;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    	appInstance = ((VideoApplication)getApplicationContext());
        appInstance.setClient(new VideoClient());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    // Called when the user clicks the Login button 
    public void sendLoginMessage(View view) throws InterruptedException, ExecutionException {
    	EditText editUserName = (EditText) findViewById(R.id.editText1);
    	EditText editPassword = (EditText) findViewById(R.id.editText2);
    	TextView statusMessage= (TextView) findViewById(R.id.textView3);
    	progressBar=(ProgressBar) findViewById(R.id.progressBar1);
    	String userName = editUserName.getText().toString();
    	String password = editPassword.getText().toString();
    	ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        Response loginResponse= new Response("");
		if(userName.equals("")|| password.equals(""))
      	  statusMessage.setText("Please fill the text fields, username or password cannot be blank");
        else if (networkInfo != null && networkInfo.isConnected()) {
        	progressBar.setVisibility(View.VISIBLE);
        	loginResponse= (Response) new SendLoginRequest(appInstance).execute(userName,password).get(); 
		 	progressBar.setVisibility(View.GONE);
        }
        else
        	statusMessage.setText("No network connection available.");
		String status=((SafeLoginResponse) loginResponse).getStatus();
		if(status.equals("success")){
			Intent intent = new Intent(this,CourseListActivity.class); 
			startActivity(intent);
		}
    } 
}
