package dk.vinael.fogmain;

import org.json.JSONArray;
import org.json.JSONException;

import dk.vinael.classes.FOGmain;
import dk.vinael.classes.User;
import dk.vinael.classes.WebserviceCaller;
import dk.vinael.interfaces.FogActivityInterface;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class LoginActivity extends Activity implements FogActivityInterface {

	private User user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void login(View view){
		user = new User();
		user.setUsername("");
		user.setPassword("");
		new WebserviceCaller(this, user, "checkSelect").execute("SELECT * FROM party;");
	}
	
	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {
		
	}

	
}
