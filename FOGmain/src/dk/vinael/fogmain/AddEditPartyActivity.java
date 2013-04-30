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
	
	// GUI items
	
	private Spinner spin_party_status;
	private EditText et_party_name;
	private EditText et_party_description;
	private EditText et_address;
	private EditText et_zip;
	private EditText et_city;
	private EditText et_country;
	private EditText et_door_code;
	private TextView tv_start_time;
	private TextView tv_end_time;
	private EditText et_min_age;
	private EditText et_max_age;
	private CheckBox cb_show_photos;
	private CheckBox cb_show_wall;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addeditparty);
		
		// Set Gui items
		spin_party_status 		= (Spinner) findViewById(R.id.spin_party_status);
		et_party_name 			= (EditText) findViewById(R.id.et_party_name);
		et_party_description	= (EditText) findViewById(R.id.et_party_description);
		et_address				= (EditText) findViewById(R.id.et_address);
		et_zip					= (EditText) findViewById(R.id.et_zip);
		et_city					= (EditText) findViewById(R.id.et_city);
		et_country				= (EditText) findViewById(R.id.et_country);
		et_door_code			= (EditText) findViewById(R.id.et_door_code);
		tv_start_time			= (TextView) findViewById(R.id.tv_start_time);
		tv_end_time				= (TextView) findViewById(R.id.tv_end_time);
		et_min_age				= (EditText) findViewById(R.id.et_min_age);
		et_max_age				= (EditText) findViewById(R.id.et_max_age);
		cb_show_photos			= (CheckBox) findViewById(R.id.cb_show_photos);
		cb_show_wall			= (CheckBox) findViewById(R.id.cb_show_wall);
		
		user = ((FOGmain)getApplicationContext()).user;
		list = new ArrayList<String>();
		
		
		list.add("Cancel");
		list.add("Active");
		
		// Set spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_party_status.setAdapter(dataAdapter);
		spin_party_status.setSelection(1);
		
		// Edit party
		bundle = getIntent().getExtras();
		if (bundle!=null){
			party = (Party) bundle.getSerializable("party");
			if (party!=null){
				putDataInItems(party);
			}
		}
	}
	
	public void putDataInItems(Party p){
		spin_party_status.setSelection(p.getStatusId());
		et_party_name.setText(p.getName());
		et_party_description.setText(p.getDescription());
		et_address.setText(p.getAddress());
		et_zip.setText(p.getZip());
		et_city.setText(p.getCity());
		et_country.setText(p.getCountry());
		et_door_code.setText(p.getDoorCode());
		//tv_start_time.setText(p.getStartTime());
		//tv_end_time.setText(p.getEndTime());
		et_min_age.setText(String.valueOf(p.getMinAge()));
		et_max_age.setText(String.valueOf(p.getMaxAge()));
		cb_show_photos.setChecked(p.doShowPhotos());
		cb_show_wall.setChecked(p.doShowWall());
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
		int max_age = 60;
		try{
			min_age = Integer.parseInt(((EditText) findViewById(R.id.et_min_age)).getText().toString());
			max_age = Integer.parseInt(((EditText) findViewById(R.id.et_max_age)).getText().toString());
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
		
		double lat = 55.55;
		double lon = 12.12;
		
		party = new Party();
		party.setPartyWithAttributes(0, owner_user_id, status_id, 
				name, description, address, zip, city, country, door_code, 
				start_time, end_time, min_age, max_age, 
				show_photos, show_wall, lat, lon
				);
		
		
		party.create(this, "partyCreated");
		
		//Toast.makeText(this.getBaseContext(), query, Toast.LENGTH_LONG).show();
		//new WebserviceCaller(this, "partyCreated").execute("insert", query);
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
			try {
				JSONObject jo = ja.getJSONObject(0);
				int createdPartyId = jo.getInt("inserted_id");
				
				//Toast.makeText(this, ""+createdPartyId, Toast.LENGTH_LONG).show();
				// Go to ViewPartyActivity
				party = new Party();
				party.getPartyById(this, "getParty", createdPartyId);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		else if(identifier.equals("latestParty")){
			Party partyToView = new Party();
			try {
				partyToView.setPartyWithJSON(ja.getJSONObject(0));
				if (partyToView!=null){
					// Toast.makeText(this.getBaseContext(), partyToView.getName(), Toast.LENGTH_LONG).show();
					Intent intent = new Intent(this, ViewPartyActivity.class);
					intent.putExtra("party", partyToView);
					this.startActivity(intent);
				}
				
			} catch (JSONException e) {
				Toast.makeText(this.getBaseContext(), "Party could not be loaded..", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}
		else if(identifier.equals("getParty")){
			try {
				party.reset();
				party.setPartyWithJSON(ja.getJSONObject(0));
				//Intent intent = new Intent(this, ViewPartyActivity.class);
				Intent intent = new Intent(this, ViewPartyActivity.class);
				intent.putExtra("party", party);
				this.finish();
				this.startActivity(intent);
			} catch (JSONException e) {
				Toast.makeText(this.getBaseContext(), "Party could not be loaded..", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			
		}
	}

}
