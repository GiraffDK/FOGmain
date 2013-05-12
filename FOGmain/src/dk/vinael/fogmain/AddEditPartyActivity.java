package dk.vinael.fogmain;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
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

	private EditText et_max_guests_addeditparty;

	private Button btn_createParty;
	private Button btn_editParty;

	private double lat;
	private double lon;
	public String addressToLookup;
	private View buttonClicked;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addeditparty);

		// Set Gui items
		spin_party_status = (Spinner) findViewById(R.id.spin_party_status);
		et_party_name = (EditText) findViewById(R.id.et_party_name);
		et_party_description = (EditText) findViewById(R.id.et_party_description);
		et_address = (EditText) findViewById(R.id.et_address);
		et_zip = (EditText) findViewById(R.id.et_zip);
		et_city = (EditText) findViewById(R.id.et_city);
		et_country = (EditText) findViewById(R.id.et_country);
		et_door_code = (EditText) findViewById(R.id.et_door_code);
		btn_set_start_date = (Button) findViewById(R.id.btn_set_start_date);
		btn_set_start_time = (Button) findViewById(R.id.btn_set_start_time);
		btn_set_end_date = (Button) findViewById(R.id.btn_set_end_date);
		btn_set_end_time = (Button) findViewById(R.id.btn_set_end_time);
		et_min_age = (EditText) findViewById(R.id.et_min_age);
		et_max_age = (EditText) findViewById(R.id.et_max_age);
		cb_show_photos = (CheckBox) findViewById(R.id.cb_show_photos);
		cb_show_wall = (CheckBox) findViewById(R.id.cb_show_wall);

		et_max_guests_addeditparty = (EditText) findViewById(R.id.et_max_guests_addeditparty);

		user = ((FOGmain) getApplicationContext()).user;

		list = new ArrayList<String>();
		list.add("Cancel");
		list.add("Active");

		// Set spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_party_status.setAdapter(dataAdapter);
		spin_party_status.setSelection(1);

		// Set date and time
		btn_set_start_date.setText(DateAndTimeStringHandler.getCurrentDate());
		btn_set_start_time.setText(DateAndTimeStringHandler.getCurrentTime());
		btn_set_end_date.setText(DateAndTimeStringHandler.getCurrentDate());
		btn_set_end_time.setText(DateAndTimeStringHandler.getCurrentTime());
		btn_createParty = (Button) findViewById(R.id.btn_createParty);
		btn_editParty = (Button) findViewById(R.id.btn_editParty);

		btn_editParty.setVisibility(View.GONE);

		// Edit party
		ActionBar bar = getActionBar();
		bundle = getIntent().getExtras();
		if (bundle != null) {
			party = (Party) bundle.getSerializable("party");
			if (party != null) {
				putDataInItems(party);
				btn_createParty.setVisibility(View.GONE);
				btn_editParty.setVisibility(View.VISIBLE);
				bar.setTitle("Edit Party");
			} else {
				bar.setTitle("Create New Party");
			}
		}

		OnFocusChangeListener ofl = new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				addressToLookup = et_address.getText().toString() + ", " + et_zip.getText().toString() + ", " + et_country.getText().toString();
				if (et_address.getText().toString().length() > 0 && et_zip.getText().toString().length() > 0 && et_country.getText().toString().length() > 0) {
					getAddressGeoCode();
				}
			}
		};

		et_address.setOnFocusChangeListener(ofl);
		et_zip.setOnFocusChangeListener(ofl);
		et_city.setOnFocusChangeListener(ofl);
		et_country.setOnFocusChangeListener(ofl);

		bar.setIcon(R.drawable.ic_a_stiff_drink);
		bar.setHomeButtonEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_to_main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		((FOGmain) getApplicationContext()).onOptionsItemSelected(item, this);
		return true;
	}

	public void getAddressGeoCode() {
		LocationHandler.convertAddressToLocation(this, addressToLookup, "");
		Toast.makeText(this.getBaseContext(), "looking up", Toast.LENGTH_LONG).show();
	}

	public void selectTime(View v) {
		DialogFragment newFragment = new TimePickerFragment();
		if (v == btn_set_start_time) {
			newFragment.show(getSupportFragmentManager(), "party_start_time_picker");
		} else if (v == btn_set_end_time) {
			newFragment.show(getSupportFragmentManager(), "party_end_time_picker");
		}
		// Toast.makeText(this.getBaseContext(), newFragment.getTag(),
		// Toast.LENGTH_LONG).show();
	}

	public void selectDate(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		if (v == btn_set_start_date) {
			newFragment.show(getSupportFragmentManager(), "party_start_date_picker");
		} else if (v == btn_set_end_date) {
			newFragment.show(getSupportFragmentManager(), "party_end_date_picker");
		}
		// Toast.makeText(this.getBaseContext(), newFragment.getTag(),
		// Toast.LENGTH_LONG).show();
	}

	public void putDataInItems(Party p) {
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
		et_max_guests_addeditparty.setText(String.valueOf(p.getMaxGuests()));
	}

	public void createEditParty(View view) {
		int party_id = 0;
		buttonClicked = view;
		if (party != null) {
			if (party.getId() != 0) {
				party_id = party.getId();
			}
		}

		int owner_user_id = user.getUserId();
		int status_id = spin_party_status.getSelectedItemPosition();
		String name = et_party_name.getText().toString();
		String description = et_party_description.getText().toString();
		String address = et_address.getText().toString();
		String zip = et_zip.getText().toString();
		String city = et_city.getText().toString();
		String country = et_country.getText().toString();
		String door_code = et_door_code.getText().toString();

		String start_date_time = DateAndTimeStringHandler.setDateAndTime(btn_set_start_date.getText().toString(), btn_set_start_time.getText().toString());
		String end_date_time = DateAndTimeStringHandler.setDateAndTime(btn_set_end_date.getText().toString(), btn_set_end_time.getText().toString());

		int min_age = 18;
		int max_age = 60;
		int max_guests = 0;
		try {
			min_age = Integer.parseInt(et_min_age.getText().toString());
			max_age = Integer.parseInt(et_max_age.getText().toString());
			max_guests = Integer.parseInt(et_max_guests_addeditparty.getText().toString());
		} catch (Exception e) {

		}

		int show_photos = 0;
		int show_wall = 0;
		if (cb_show_photos.isChecked()) {
			show_photos = 1;
		}
		if (cb_show_wall.isChecked()) {
			show_wall = 1;
		}

		/*
		 * double lat = 55.55; double lon = 12.12;
		 */

		double lat = this.lat;
		double lon = this.lon;

		if (validateData()) {
			party = new Party();

			party.setPartyWithAttributes(party_id, owner_user_id, status_id, name, description, address, zip, city, country, door_code, start_date_time, end_date_time, min_age, max_age, show_photos,
					show_wall, lat, lon, max_guests);
			LocationHandler.convertAddressToLocation(this, et_address.getText().toString() + ", " + et_city.getText().toString() + ", " + et_country.getText().toString(), "checkAddress");
		}

		// Toast.makeText(this.getBaseContext(), query,
		// Toast.LENGTH_LONG).show();
		// new WebserviceCaller(this, "partyCreated").execute("insert", query);
	}

	public void makeToast(EditText eText, String msg) {
		Toast.makeText(this, eText.getHint() + " Error: " + msg, Toast.LENGTH_LONG).show();
	}

	public boolean checkLength(EditText eText, String msg) {
		if (eText.getText().length() < 1) {
			makeToast(eText, msg);
			return true;
		}
		return false;
	}

	public boolean validateData() {
		boolean valid = true;
		String[] st_data = btn_set_start_date.getText().toString().split("-");
		String[] end_data = btn_set_end_date.getText().toString().split("-");
		String[] st_data_time = btn_set_start_time.getText().toString().split(":");
		String[] end_data_time = btn_set_end_time.getText().toString().split(":");
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();

		start.set(Integer.parseInt(st_data[0]), Integer.parseInt(st_data[1]), Integer.parseInt(st_data[2]), Integer.parseInt(st_data_time[0]), Integer.parseInt(st_data_time[1]),
				Integer.parseInt(st_data_time[2]));
		start.add(Calendar.MINUTE, 59);
		end.set(Integer.parseInt(end_data[0]), Integer.parseInt(end_data[1]), Integer.parseInt(end_data[2]), Integer.parseInt(end_data_time[0]), Integer.parseInt(end_data_time[1]),
				Integer.parseInt(end_data_time[2]));
		int min = Integer.parseInt(et_min_age.getText().toString());
		int max = Integer.parseInt(et_max_age.getText().toString());

		if (checkLength(et_party_name, "To short party name")) {
			valid = false;
		} else if (checkLength(et_address, "To short street address")) {
			valid = false;
		} else if (checkLength(et_zip, "To short zip code")) {
			valid = false;
		} else if (checkLength(et_country, "To short country name")) {
			valid = false;
		} else if (et_min_age.getText().toString().length() != 2 || et_max_age.getText().toString().length() != 2 ||
				min > max || min < 18 || min > 60) {
			Toast.makeText(this, "Min/max age Error: Should be between 18 and 60 ", Toast.LENGTH_LONG).show();
			valid = false;
		} else if (start.after(end)) {
			valid = false;
			Toast.makeText(this, "Start date and time needs to be atleast 1 hour before end", Toast.LENGTH_LONG).show();
		} else {
			start.add(Calendar.DATE, 7);
			if (end.after(start)) {
				valid = false;
				Toast.makeText(this, "Max length for a party is 7 days", Toast.LENGTH_LONG).show();
			}
		}
		return valid;
	}

	public void viewParty(View view) {
		SqlWrapper.selectParty(this, "latestParty", 13);
		// String query = "SELECT * FROM party ORDER BY id DESC LIMIT 1;";
		// new WebserviceCaller(this, "latestParty").execute("select", query);
	}

	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {
		if (identifier.equals("partyCreated")) {
			try {
				JSONObject jo = ja.getJSONObject(0);
				int createdPartyId = jo.getInt("inserted_id");

				// Toast.makeText(this, ""+createdPartyId,
				// Toast.LENGTH_LONG).show();
				// Go to ViewPartyActivity
				party = new Party();
				party.getPartyById(this, "getParty", createdPartyId);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (identifier.equals("partyEdited")) {
			Intent intent = new Intent(this, ViewPartyActivity.class);
			intent.putExtra("party", party);
			this.finish();
			this.startActivity(intent);
			// Toast.makeText(this, ""+ja.toString(), Toast.LENGTH_LONG).show();
		} else if (identifier.equals("latestParty")) {
			Party partyToView = new Party();
			try {
				partyToView.setPartyWithJSON(ja.getJSONObject(0));
				if (partyToView != null) {
					// Toast.makeText(this.getBaseContext(),
					// partyToView.getName(), Toast.LENGTH_LONG).show();
					Intent intent = new Intent(this, ViewPartyActivity.class);
					intent.putExtra("party", partyToView);
					this.startActivity(intent);
				}

			} catch (JSONException e) {
				Toast.makeText(this.getBaseContext(), "Party could not be loaded..", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		} else if (identifier.equals("getParty")) {
			try {
				party.reset();
				party.setPartyWithJSON(ja.getJSONObject(0));
				// Intent intent = new Intent(this, ViewPartyActivity.class);
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
		if (identifier.equals("checkAddress")) {
			Toast.makeText(this, identifier, Toast.LENGTH_LONG).show();
			if (location != null) {
				if (this.buttonClicked == findViewById(R.id.btn_editParty)) {
					party.edit(this, "partyEdited");
				} else if (this.buttonClicked == findViewById(R.id.btn_createParty)) {
					party.create(this, "partyCreated");
				}
			} else {
				Toast.makeText(this, "Seems to be an error in you Address", Toast.LENGTH_LONG).show();
			}
		}
		if (location != null) {
			this.lat = (location.getLatitude());
			this.lon = (location.getLongitude());
		} 
			
		
	}

}
