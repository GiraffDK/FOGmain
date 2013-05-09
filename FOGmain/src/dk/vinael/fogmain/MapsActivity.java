package dk.vinael.fogmain;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import dk.vinael.domain.FOGmain;
import dk.vinael.domain.Party;

public class MapsActivity extends FragmentActivity implements OnMapClickListener, LocationListener, OnInfoWindowClickListener {

	private Location loc;
	private GoogleMap map;
	private ArrayList<Party> parties;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.framelayout);

		Bundle bundle = getIntent().getExtras(); 		// Getting Data From Bundle
		loc = new Location("Your Location");
		loc.set((Location) bundle.get("Location"));

		// Standard Google Maps Setup.
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(loc.getLatitude(), loc.getLongitude()));
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
		map.moveCamera(center);
		map.animateCamera(zoom);
		map.setOnInfoWindowClickListener(this); 		// Adding listener to the marker info window.
		
				
		checkingCaller(bundle); 	// Checking how we want to use this map.
		addingNewInfoWindow(); 		// Adding new Layout/View for the marker info.
		
		ActionBar bar = getActionBar();
		bar.setIcon(R.drawable.maps_direction);
		bar.setTitle("Google Maps");
		bar.setHomeButtonEnabled(true);

	}
	/**
	 * Checks if the maps should be used for placing parties as markers or
	 * if you want to use the map to set your a location by clicking on the map.
	 * @param bundle
	 */
	@SuppressWarnings("unchecked")
	public void checkingCaller(Bundle bundle) {
		map.clear();
		if (bundle.size() > 1) 
		{
			parties = (ArrayList<Party>) bundle.get("List"); 		// Contains a list over parties that should be added to the map.
			addExisting(); 		// Calls the methods to add the list of parties.
		} 
		else 
		{
			map.setOnMapClickListener(this); 		// Doesn't contain a list and therefore the map is used for finding a location etc.
		}
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
	/**
	 * This method is used to change the info window of a marker when you click it.
	 * It inflates the default window with a title and button.
	 */
	public void addingNewInfoWindow() {
		map.setInfoWindowAdapter(new InfoWindowAdapter() {
			@Override
			public View getInfoWindow(Marker marker) {
				return null;
			}
			@Override
			public View getInfoContents(Marker marker) {	
				View v = getLayoutInflater().inflate(R.layout.infowindow_view, null);	
				TextView tvTitle = (TextView) v.findViewById(R.id.tv_party_title);
				tvTitle.setText(marker.getTitle());
				return v;
			}
		});
	}

	/**
	 * This method is only used if you want to set a location on the map rather than writing the address.
	 * The listener is only added to the map in method "checkingCaller()"
	 */
	@Override
	public void onMapClick(LatLng point) {
		final LatLng location = point;
		final Marker mark = map.addMarker(new MarkerOptions().position(location).title("New location"));
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					Location house = new Location("Selected Location");
					house.setLatitude(location.latitude);
					house.setLongitude(location.longitude);
					Intent i = new Intent();
					i.putExtra("Choosen Location", house);
					setResult(RESULT_OK, i);
					finish();
					MapsActivity.this.finish();
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					mark.remove();
					break;
				}
			}
		};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Want to search from the location?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
	}

	/**
	 * Simple loop that takes each party in the ps list and adds its location as a marker on the map.
	 */
	public void addExisting() {
		
		for (Party  p : parties) {
			map.addMarker(new MarkerOptions().position(new LatLng(p.getLat(), p.getLon())).title(p.getName()));
		}
	}

	/**
	 * If the provider (Could be the network) detects a new position if will update the loc (Location).
	 */
	@Override
	public void onLocationChanged(Location location) {
		loc.set(location);
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
	/**
	 * implementation of the "OnInfoWindowClick"
	 * If you click the info window this method will ask you if you want to see the party in the "Party view".
	 * Yes = you will go to the party.
	 * No = DialogBox closes.
	 */
	@Override
	public void onInfoWindowClick(final Marker marker) {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					for (Party p : parties) {
						if (p.getName().equals(marker.getTitle())) {
							Intent i = new Intent(MapsActivity.this, ViewPartyActivity.class);
							i.putExtra("party", p);
							MapsActivity.this.startActivity(i);
						}
					}
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					break;
				}
			}
		};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Go to this party?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
	}
}
