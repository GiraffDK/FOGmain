package dk.vinael.fogmain;

import org.json.JSONArray;

import dk.vinael.domain.FOGmain;
import dk.vinael.domain.User;
import dk.vinael.interfaces.FogActivityInterface;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MenuActivity extends Activity implements FogActivityInterface {

	private User user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		
		user = ((FOGmain)getApplicationContext()).user;
		((TextView)findViewById(R.id.tv_username_menu)).setText("Logged in as: " + user.getFirstName() + " " + user.getLastName());

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
