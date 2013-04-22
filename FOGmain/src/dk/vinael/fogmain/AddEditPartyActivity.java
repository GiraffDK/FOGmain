package dk.vinael.fogmain;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Toast;
import asynctasks.WebserviceCaller;

import dk.vinael.domain.FOGmain;
import dk.vinael.domain.Party;
import dk.vinael.domain.SqlWrapper;
import dk.vinael.domain.User;
import dk.vinael.interfaces.FogActivityInterface;

public class AddEditPartyActivity extends Activity implements FogActivityInterface {

	private User user;
	public ArrayList<String> list;
	
	private Party party;
	private Bundle bundle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addeditparty);
		
		user = ((FOGmain)getApplicationContext()).user;
		list = new ArrayList<String>();
		
		Spinner spinner = (Spinner) findViewById(R.id.spin_party_status);
		
		list.add("Cancel");
		list.add("Active");
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
		spinner.setSelection(1);
		
		// Edit party
		bundle = getIntent().getExtras();
		if (bundle!=null){
			party = (Party) bundle.getSerializable("party");
			if (party!=null){
			
			}
		}
	}
	
	public void putDataInItems(Party p){
		
	}

	public void createParty(View view){
		int owner_user_id 	= user.getUserId();
		int status_id 		= ((Spinner) findViewById(R.id.spin_party_status)).getSelectedItemPosition();
		String name			= ((EditText) findViewById(R.id.et_party_name)).getText().toString();
		String description 	= ((EditText) findViewById(R.id.et_party_description)).getText().toString();
		String address 		= ((EditText) findViewById(R.id.et_address)).getText().toString();
		String zip 			= ((EditText) findViewById(R.id.et_zip)).getText().toString();
		String city 		= ((EditText) findViewById(R.id.et_city)).getText().toString();
		String country 		= ((EditText) findViewById(R.id.et_country)).getText().toString();
		String door_code 	= ((EditText) findViewById(R.id.et_door_code)).getText().toString();
		String start_time 	= ((TextView) findViewById(R.id.tv_start_time)).getText().toString();
		String end_time 	= ((TextView) findViewById(R.id.tv_end_time)).getText().toString();
		
		int min_age = 18;
		int max_age = 100;
		try{
			min_age = Integer.parseInt(((EditText) findViewById(R.id.et_min_age)).getText().toString());
			min_age = Integer.parseInt(((EditText) findViewById(R.id.et_max_age)).getText().toString());
		}
		catch(Exception e){
			
		}
		
		int show_photos = 0;
		int show_wall = 0;
		if (((CheckBox) findViewById(R.id.cb_show_photos)).isChecked()){
			show_photos = 1;
		}
		if (((CheckBox) findViewById(R.id.cb_show_wall)).isChecked()){
			show_wall = 1;
		}
		
		
		String query = "INSERT INTO party " +
				"(" +
					"owner_user_id, " +
					"status_id, " +
					"name, " +
					"description, " +
					"address, " +
					"zip, " +
					"city, " +
					"country, " +
					"door_code, " +
					"start_time, " +
					"end_time, " +
					"min_age, " +
					"max_age, " +
					"show_photos, " +
					"show_wall" +
				") VALUES " +
				"(" +
					""+owner_user_id+", " +
					""+status_id+", " +
					"'"+name+"', " +
					"'"+description+"', " +
					"'"+address+"', " +
					"'"+zip+"', " +
					"'"+city+"', " +
					"'"+country+"', " +
					"'"+door_code+"', " +
					"'"+start_time+"', " +
					"'"+end_time+"', " +
					""+min_age+", " +
					""+max_age+", " +
					""+show_photos+", " +
					""+show_wall+"" +
							");";
		
		Toast.makeText(this.getBaseContext(), query, Toast.LENGTH_LONG).show();
		new WebserviceCaller(this, "partyCreated").execute("insert", query);
	}
	
	public void viewParty(View view){
		SqlWrapper.selectParty(this, "latestParty", 13);
		//String query = "SELECT * FROM party ORDER BY id DESC LIMIT 1;";
		//new WebserviceCaller(this, "latestParty").execute("select", query);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {
		if (identifier.equals("partyCreated")){
			String s = "created";
			try {
				JSONObject jo = ja.getJSONObject(0);
				s = Integer.toString(jo.getInt("affected_rows"));
				s += " : " + jo.getInt("inserted_id");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Toast.makeText(this.getBaseContext(), s, Toast.LENGTH_LONG).show();
			
			// Go to ViewPartyActivity
			//Intent intent = new Intent(this, ViewPartyActivity.class);
			//intent.putExtra(name, value);
			//this.finish();
			//this.startActivity(intent);
		}
		else if(identifier.equals("latestParty")){
			
			Party partyToView = new Party();
			try {
				partyToView.setPartyWithJSON(ja.getJSONObject(0));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (partyToView!=null){
				Toast.makeText(this.getBaseContext(), partyToView.getName(), Toast.LENGTH_LONG).show();
			}
			
			Intent intent = new Intent(this, ViewPartyActivity.class);
			intent.putExtra("party", partyToView);
			this.startActivity(intent);
		}
	}

}
