package dk.vinael.fogmain;

import org.json.JSONArray;
import org.json.JSONException;

import dk.vinael.domain.FOGmain;
import dk.vinael.domain.User;
import dk.vinael.interfaces.FogActivityInterface;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void login(View view){
		EditText txt_email = (EditText) findViewById(R.id.txt_email);
		EditText txt_password = (EditText) findViewById(R.id.txt_password);
		user.setEmail(txt_email.getText().toString());
		user.setPassword(txt_password.getText().toString());
		
		new WebserviceCaller(this, "getUser").execute("SELECT * FROM user WHERE email = '"+user.getEmail()+"' AND password ='"+user.getPassword()+"';");
	}
	
	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {
		if (identifier.equals("getUser")){
			try {
				user.setUserByJson(ja.getJSONObject(0));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (user.getToken()!=null){
				// Should go to MenuActivity
				Intent intent = new Intent(this, AddEditPartyActivity.class);
				this.finish();
				this.startActivity(intent);
				
			}
		}
	}

	
}
