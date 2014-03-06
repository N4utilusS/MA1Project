package com.example.myfirstapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void sendMessage(View view){
    	Intent intent = new Intent(this, DisplayMessageActivity.class);
    	EditText t = (EditText) findViewById(R.id.edit_message);
    	String message = t.getText().toString();
    	intent.putExtra(EXTRA_MESSAGE, message);
    	startActivity(intent);
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	// Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
            	Intent intent = new Intent(this, DisplayMessageActivity.class);
            	String message = "Search not found !";
            	intent.putExtra(EXTRA_MESSAGE, message);
            	startActivity(intent);
                return true;
            case R.id.action_settings:
            	Intent intent1 = new Intent(this, DisplayMessageActivity.class);
            	String message1 = "Please activate settings in the settings";
            	intent1.putExtra(EXTRA_MESSAGE, message1);
            	startActivity(intent1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
	}
    
    public void actionBarHide(View view){
    	if(this.getActionBar().isShowing())
    		this.getActionBar().hide();
    	else
    		this.getActionBar().show();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
    }
    
}
