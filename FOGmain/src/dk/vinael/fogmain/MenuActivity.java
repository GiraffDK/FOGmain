package dk.vinael.fogmain;

import org.json.JSONArray;

import dk.vinael.domain.FOGmain;
import dk.vinael.domain.User;
import dk.vinael.interfaces.FogActivityInterface;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import asynctasks.NotificationService;

public class MenuActivity extends Activity implements FogActivityInterface {

	private User user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		
		user = ((FOGmain)getApplicationContext()).user;
		((TextView)findViewById(R.id.tv_username_menu)).setText("Logged in as: " + user.getFirstName() + " " + user.getLastName());

		// Service start
		Intent serviceintent = new Intent(this, NotificationService.class);
		serviceintent.putExtra("user", ((FOGmain)getApplicationContext()).user); 
		startService(serviceintent);
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}
	public void onLogOut(MenuItem item) {
		if (item.getItemId() == R.id.menu_log_out) {
			((FOGmain)getApplicationContext()).user.resetUserToken(this, "logout");
			((FOGmain)getApplicationContext()).user = new User();
			Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
		}
	}
	
	public void gotoAddParty(View view){
		Intent intent = new Intent(this, AddEditPartyActivity.class);
		this.startActivity(intent);
	}
	
	public void gotoSearch(View view){
		Intent intent = new Intent(this, SearchForPartyActivity.class);
		this.startActivity(intent);
	}
	
	public void gotoParties(View view){
		Intent intent = new Intent(this, PartiesActivity.class);
		this.startActivity(intent);
	}
	
	public void gotoProfile(View view){
		Intent intent = new Intent(this, ViewProfilActivity.class);
		intent.putExtra("user", ((FOGmain)getApplicationContext()).user);
		this.startActivity(intent);
	}
	
	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {
		
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
