package dk.vinael.fogmain;

import org.json.JSONArray;

import dk.vinael.interfaces.FogActivityInterface;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MenuActivity extends Activity implements FogActivityInterface {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		
	}
	
	public void gotoAddParty(View view){
		Intent intent = new Intent(this, AddEditPartyActivity.class);
		this.startActivity(intent);
	}
	
	public void gotoSearch(View view){
		//Intent intent = new Intent(this, SearchPartyActivity.class);
		//this.startActivity(intent);
	}
	
	public void gotoParties(View view){
		//Intent intent = new Intent(this, PartiesActivity.class);
		//this.startActivity(intent);
	}
	
	public void gotoProfile(View view){
		//Intent intent = new Intent(this, ProfileActivity.class);
		//this.startActivity(intent);
	}
	
	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {
		
	}

}
