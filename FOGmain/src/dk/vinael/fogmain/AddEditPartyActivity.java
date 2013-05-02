package dk.vinael.fogmain;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import asynctasks.LocationHandler;
import dk.vinael.domain.DateAndTimeStringHandler;
import dk.vinael.domain.FOGmain;
import dk.vinael.domain.Party;
import dk.vinael.domain.SqlWrapper;
import dk.vinael.domain.User;
import dk.vinael.interfaces.FogActivityInterface;
import fragments.DatePickerFragment;
import fragments.TimePickerFragment;

public class AddEditPartyActivity extends FragmentActivity implements FogActivityInterface, LocationListener {

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
	private Button btn_set_start_date;
	private Button btn_set_start_time;
	private Button btn_set_end_date;
	private Button btn_set_end_time;
	private EditText et_min_age;
	private EditText et_max_age;
	private CheckBox cb_show_photos;
	private CheckBox cb_show_wall;
	
	private double lat;
	private double lon;
	public String addressToLookup;
	
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
		btn_set_start_date		= (Button) findViewById(R.id.btn_set_start_date);
		btn_set_start_time		= (Button) findViewById(R.id.btn_set_start_time);
		btn_set_end_date		= (Button) findViewById(R.id.btn_set_end_date);
		btn_set_end_time		= (Button) findViewById(R.id.btn_set_end_time);
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
		
		// Set date and time
		btn_set_start_date.setText(DateAndTimeStringHandler.getCurrentDate());
		btn_set_start_time.setText(DateAndTimeStringHandler.getCurrentTime());
		btn_set_end_date.setText(DateAndTimeStringHandler.getCurrentDate());
		btn_set_end_time.setText(DateAndTimeStringHandler.getCurrentTime());
		
		((Button)findViewById(R.id.btn_editParty)).setVisibility(View.GONE);
		
		// Edit party
		bundle = getIntent().getExtras();
		if (bundle!=null){
			party = (Party) bundle.getSerializable("party");
			if (party!=null){
				putDataInItems(party);
				((Button)findViewById(R.id.btn_createParty)).setVisibility(View.GONE);
				((Button)findViewById(R.id.btn_editParty)).setVisibility(View.VISIBLE);
			}
		}
		
		
		OnFocusChangeListener ofl = new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				addressToLookup = et_address.getText().toString() + ", " + et_zip.getText().toString() + ", " + et_country.getText().toString();
				if (et_address.getText().toString().length()>0 && et_zip.getText().toString().length()>0 && et_country.getText().toString().length()>0){
					getAddressGeoCode();
				}
			}
		};
		
		et_address.setOnFocusChangeListener(ofl);
		et_zip.setOnFocusChangeListener(ofl);
		et_city.setOnFocusChangeListener(ofl);
		et_country.setOnFocusChangeListener(ofl);

	}
	
	public void getAddressGeoCode(){
		Location loc = new Location(addressToLookup);
		LocationHandler.convertAddressToLocation(this, addressToLookup, "");
		//LocationHandler.convertLocationToAddress(this, loc, "addressLookup");
		//new LocationToAddress(loc, this, "addressLookup").execute(loc);
		Toast.makeText(this.getBaseContext(), "looking up", Toast.LENGTH_LONG).show();
	}
	
	public void setLocation(List<Address> addresses){
		if (addresses.size()>0){
			Toast.makeText(this.getBaseContext(), addresses.get(0).getLatitude()+" : "+addresses.get(0).getLongitude(), Toast.LENGTH_LONG).show();
			//Toast.makeText(this.getBaseContext(), ""+addresses.size(), Toast.LENGTH_LONG).show();
			this.lat = (addresses.get(0).getLatitude());
			this.lon = (addresses.get(0).getLongitude());
		}
	}
	
	public void selectTime(View v){
		DialogFragment newFragment = new TimePickerFragment();
		if (v == findViewById(R.id.btn_set_start_time)){
			newFragment.show(getSupportFragmentManager(), "party_start_time_picker");
		}
		else if(v == findViewById(R.id.btn_set_end_time)){
			newFragment.show(getSupportFragmentManager(), "party_end_time_picker");
		}
	    //Toast.makeText(this.getBaseContext(), newFragment.getTag(), Toast.LENGTH_LONG).show();
	}
	
	public void selectDate(View v){
		DialogFragment newFragment = new DatePickerFragment();
		if (v == findViewById(R.id.btn_set_start_date)){
			newFragment.show(getSupportFragmentManager(), "party_start_date_picker");
		}
		else if(v == findViewById(R.id.btn_set_end_date)){
			newFragment.show(getSupportFragmentManager(), "party_end_date_picker");
		}
	    //Toast.makeText(this.getBaseContext(), newFragment.getTag(), Toast.LENGTH_LONG).show();
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
		btn_set_start_date.setText(p.getStartDate());
		btn_set_start_time.setText(p.getStartTime());
		btn_set_end_date.setText(p.getEndDate());
		btn_set_end_time.setText(p.getEndTime());
		et_min_age.setText(String.valueOf(p.getMinAge()));
		et_max_age.setText(String.valueOf(p.getMaxAge()));
		cb_show_photos.setChecked(p.doShowPhotos());
		cb_show_wall.setChecked(p.doShowWall());
	}

	public void createEditParty(View view){
		int party_id = 0;
		
		if (party!=null){
			if (party.getId()!=0){
				party_id = party.getId();
			}
		}
		
		int owner_user_id 	= user.getUserId();
		int status_id 		= ((Spinner) findViewById(R.id.spin_party_status)).getSelectedItemPosition();
		String name			= ((EditText) findViewById(R.id.et_party_name)).getText().toString();
		String description 	= ((EditText) findViewById(R.id.et_party_description)).getText().toString();
		String address 		= ((EditText) findViewById(R.id.et_address)).getText().toString();
		String zip 			= ((EditText) findViewById(R.id.et_zip)).getText().toString();
		String city 		= ((EditText) findViewById(R.id.et_city)).getText().toString();
		String country 		= ((EditText) findViewById(R.id.et_country)).getText().toString();
		String door_code 	= ((EditText) findViewById(R.id.et_door_code)).getText().toString();
		
		String start_date_time 	= DateAndTimeStringHandler.setDateAndTime(btn_set_start_date.getText().toString(), btn_set_start_time.getText().toString());
		String end_date_time 	= DateAndTimeStringHandler.setDateAndTime(btn_set_end_date.getText().toString(), btn_set_end_time.getText().toString());
		
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
		
		
		/*
		double lat = 55.55;
		double lon = 12.12;
		*/
		
		double lat = this.lat;
		double lon = this.lon;
		
		
		party = new Party();
		
		party.setPartyWithAttributes(party_id, owner_user_id, status_id, 
				name, description, address, zip, city, country, door_code, 
				start_date_time, end_date_time, min_age, max_age, 
				show_photos, show_wall, lat, lon
				);
		
		
		if (view==findViewById(R.id.btn_editParty)){
			party.edit(this, "partyEdited");
		}
		else if(view==findViewById(R.id.btn_createParty)){
			party.create(this, "partyCreated");
			//Toast.makeText(this.getBaseContext(), party.getName(), Toast.LENGTH_LONG).show();
		}

		//Toast.makeText(this.getBaseContext(), query, Toast.LENGTH_LONG).show();
		//new WebserviceCaller(this, "partyCreated").execute("insert", query);
	}
	
	public void viewParty(View view){
		SqlWrapper.selectParty(this, "latestParty", 13);
		//String query = "SELECT * FROM party ORDER BY id DESC LIMIT 1;";
		//new WebserviceCaller(this, "latestParty").execute("select", query);
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
		else if(identifier.equals("partyEdited")){
			Intent intent = new Intent(this, ViewPartyActivity.class);
			intent.putExtra("party", party);
			this.finish();
			this.startActivity(intent);
			//Toast.makeText(this, ""+ja.toString(), Toast.LENGTH_LONG).show();
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

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void returningAddress(String Address, String identifier) {
		
		
	}

	@Override
	public void returningLocation(Location location, String identifier) {
		Toast.makeText(this, location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_LONG).show();	
	}

}
