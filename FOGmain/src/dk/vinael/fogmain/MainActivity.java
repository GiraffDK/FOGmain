package dk.vinael.fogmain;

import org.json.JSONArray;
import org.json.JSONException;

import dk.vinael.domain.FOGmain;
import dk.vinael.domain.User;
import dk.vinael.interfaces.FogActivityInterface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import asynctasks.WebserviceCaller;

public class MainActivity extends Activity implements FogActivityInterface {

	private User user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// ## Set user
		// ## Set user to global variable
		// ## Handle resume, pause, etc.
		
		user = ((FOGmain)getApplicationContext()).user;
		if (user==null){
			user = new User();
			((FOGmain)getApplicationContext()).user = user;
		}
		
		user.selectUserByToken(MainActivity.this, "checkAndSetUser");
		TextView tv_please_wait = (TextView) findViewById(R.id.tv_pleasewait);
		tv_please_wait.setText("Trying to login.. Hold on!");
		
		/*
		Handler handler = new Handler();
		handler.postDelayed(new Runnable(){
			@Override
			public void run() {
				user = ((FOGmain)getApplicationContext()).user;
				if (user==null){
					user = new User();
					((FOGmain)getApplicationContext()).user = user;
				}
				user.setToken("kevin98f13708210194c475687be6106a3b84");
				user.selectUserByToken(MainActivity.this, "checkAndSetUser");
				//new WebserviceCaller(MainActivity.this, "checkAndSetUser").execute("select", "SELECT * FROM user WHERE token='kevin98f13708210194c475687be6106a3b84';");
			}
		}, 1000);
		*/
	}


	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {
		// Print out username from all users
		//Toast.makeText(this.getBaseContext(), ja.length(), Toast.LENGTH_LONG).show();
		if (identifier.equals("checkAndSetUser")){
			try {
				user.setUserByJson(ja.getJSONObject(0));
				Intent intent = new Intent(this, MenuActivity.class);
				this.finish();
				this.startActivity(intent);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
	}

	@Override
	public void returningAddress(String Address, String identifier) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void returningLocation(Location location, String identifier) {
		// TODO Auto-generated method stub
		
	}

}
