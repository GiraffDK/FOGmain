package dk.vinael.fogmain;

import org.json.JSONArray;

import dk.vinael.domain.User;
import dk.vinael.interfaces.FogActivityInterface;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

public class NoInternetActivity extends Activity implements FogActivityInterface {

	private User user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nointernet);
		
		
	}
	
	public void goToLogin(View v){
		Intent intent = new Intent(this, MainActivity.class);
		this.finish();
		this.startActivity(intent);
	}

	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {
		// TODO Auto-generated method stub
		
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
