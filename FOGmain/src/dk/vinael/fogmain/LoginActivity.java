package dk.vinael.fogmain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dk.vinael.domain.FOGmain;
import dk.vinael.domain.User;
import dk.vinael.interfaces.FogActivityInterface;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import asynctasks.WebserviceCaller;

public class LoginActivity extends Activity implements FogActivityInterface {

	private User user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		((FOGmain)getApplicationContext()).user = new User();
		this.user = ((FOGmain)getApplicationContext()).user;
	}

	public void login(View view){
		EditText txt_email = (EditText) findViewById(R.id.txt_email);
		EditText txt_password = (EditText) findViewById(R.id.txt_password);
		user.setEmail(txt_email.getText().toString());
		user.setPassword(txt_password.getText().toString());
		//Log.i(user.getPassword(), "PASS:");
		user.selectUserByEmailAndPassword(this, "getUserToken");
	}
	
	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {
		if (identifier.equals("getUserToken")){
			try {
				JSONObject jo = ja.getJSONObject(0);
				if (jo.has("access")){
					if (jo.getString("access")!="granted" && jo.getString("access")!="denied"){
						user.setToken(jo.getString("access"));
						user.selectUserByToken(this, "setUser");
					}
				}
			} catch (JSONException e) {
				user = null;
				//e.printStackTrace();
			}
		}
		else if (identifier.equals("setUser")){
			try {
				JSONObject jo = ja.getJSONObject(0);
				if (jo.has("firstname")){
					user.setUserByJson(jo);
					Intent intent = new Intent(this, MenuActivity.class);
					this.finish();
					this.startActivity(intent);
					overridePendingTransition(R.anim.activity_slide_in_left, R.anim.activity_slide_out_left);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
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
