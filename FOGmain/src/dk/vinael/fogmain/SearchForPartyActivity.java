package dk.vinael.fogmain;

import org.json.JSONArray;

import dk.vinael.domain.FOGmain;
import dk.vinael.interfaces.FogActivityInterface;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.Toast;
import asynctasks.LocationHandler;

public class SearchForPartyActivity extends Activity implements LocationListener, FogActivityInterface {

	private LocationManager locationManager;
	private Location loc;
	private NumberPicker np_radius;
	private NumberPicker np_age_low;
	private NumberPicker np_age_max;
	private Button btn_search;

	@Override
	protected void onResume() {
		btn_search.setClickable(true);
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_party);
		btn_search = (Button) findViewById(R.id.btn_search_parties);
		
		Criteria criteria = new Criteria();
	    
		
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		String provider = locationManager.getBestProvider(criteria, true);
		locationManager.requestLocationUpdates(provider, 0, 0, this);
		onLocationChanged(locationManager.getLastKnownLocation(provider));

		//Toast.makeText(this, provider, Toast.LENGTH_LONG).show();
		// Setting Pickers:
		np_radius = (NumberPicker) findViewById(R.id.np_radius);
		np_radius.setMaxValue(50);
		np_radius.setMinValue(1);
		np_age_low = (NumberPicker) findViewById(R.id.np_age_low);
		np_age_low.setMaxValue(59);
		np_age_low.setMinValue(18);
		np_age_low.setOnValueChangedListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				if (np_age_low.getValue() > np_age_max.getValue()) {
					np_age_max.setValue(np_age_low.getValue()+1);
				}
				
			}
		});
		np_age_max = (NumberPicker) findViewById(R.id.np_age_max);
		np_age_max.setMaxValue(60);
		np_age_max.setMinValue(19);
		np_age_max.setOnValueChangedListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				if (np_age_low.getValue() >= np_age_max.getValue()) {
					np_age_max.setValue(np_age_low.getValue()+1);
				}
				
			}
		});
		
		//ActionBar bar = getActionBar();
		//bar.setIcon(R.drawable.ic_search);
		//bar.setTitle("Search For Party");

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_to_main_menu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		((FOGmain) getApplicationContext()).onOptionsItemSelected(item,this);
		return true;
	}
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		LocationHandler.convertLocationToAddress(this, loc, "SetAddressText");

	}

	public void onClick(View view) {
		btn_search.setClickable(false);
		LocationHandler.convertAddressToLocation(this, ((EditText) findViewById(R.id.et_location)).getText().toString(), "CheckAddress");
	}

	public void selectOnMap(View view) {
		Intent in = new Intent(this, MapsActivity.class);
		in.putExtra("Location", loc);
		startActivityForResult(in, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_CANCELED) {
			loc.set((Location) data.getExtras().get("Choosen Location"));
			LocationHandler.convertLocationToAddress(this, loc, "SetAddressText");
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void jsonArrayHandler(JSONArray ja, String identifier) {
		// TODO Auto-generated method stub

	}

	@Override
	public void returningAddress(String Address, String identifier) {
		if (identifier.equals("SetAddressText")) {
			((EditText) findViewById(R.id.et_location)).setText(Address);
			(findViewById(R.id.linlay_searchparties)).requestFocus();
			
		} 
	}

	@Override
	public void returningLocation(Location location, String identifier) {
		if (identifier.equals("CheckAddress")) {
			if (location != null) {
				loc = location;
				// Starting new view.
				Intent in = new Intent(this, SearchResultActivity.class);
				in.putExtra("Location", loc);
				in.putExtra("Radius", np_radius.getValue());
				in.putExtra("Min_age", np_age_low.getValue());
				in.putExtra("Max_age", np_age_max.getValue());
				startActivity(in);
			} else {
				AlertDialog.Builder adb = new AlertDialog.Builder(this);
				adb.setTitle("Google Map");
				adb.setMessage("Please Provide the Proper Place");
				adb.setPositiveButton("Close", null);
				adb.show();
			}
		}
	}
}
