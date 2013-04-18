package dk.vinael.fogmain;

import org.json.JSONArray;
import org.json.JSONException;

import dk.vinael.domain.FOGmain;
import dk.vinael.domain.User;
import dk.vinael.interfaces.FogActivityInterface;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
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
				new WebserviceCaller(MainActivity.this, user, "checkAndSetUser").execute("select", "SELECT * FROM user WHERE token='kevin98f13708210194c475687be6106a3b84';");
			}
		}, 1000);
	}

	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {
		// Print out username from all users
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

}
