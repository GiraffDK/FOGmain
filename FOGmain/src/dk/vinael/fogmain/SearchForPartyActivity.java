package dk.vinael.fogmain;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import asynctasks.LocationToAddress;

public class SearchForPartyActivity extends Activity implements LocationListener {
	
	private LocationManager locationManager;
	private Location loc;
	private NumberPicker np_radius; 
	private NumberPicker np_age_low;
	private NumberPicker np_age_max;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_party);
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		onLocationChanged(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
		
		// Setting Pickers:
		np_radius = (NumberPicker) findViewById(R.id.np_radius);
		np_radius.setMaxValue(2000);
		np_radius.setMinValue(1);
		np_age_low = (NumberPicker) findViewById(R.id.np_age_low);
		np_age_low.setMaxValue(60);
		np_age_low.setMinValue(18);
		np_age_max = (NumberPicker) findViewById(R.id.np_age_max);
		np_age_max.setMaxValue(60);
		np_age_max.setMinValue(18);	
	}
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		new LocationToAddress(loc ,this, "SearchParty").execute(loc);
	}

	public void setLocationText(String text) {
		EditText et = (EditText) findViewById(R.id.et_location);
		et.setText(text);	
	}
	public void onClick(View view) {
		Intent in = new Intent(this, SearchResultActivity.class);
		in.putExtra("Location", loc);
		in.putExtra("Radius", np_radius.getValue());
		in.putExtra("Min_age", np_age_low.getValue());
		in.putExtra("Max_age", np_age_low.getValue());
		startActivity(in);
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
			loc.set((Location)data.getExtras().get("newLocation"));
			new LocationToAddress(loc ,this, "SearchParty").execute(loc);	
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
}
