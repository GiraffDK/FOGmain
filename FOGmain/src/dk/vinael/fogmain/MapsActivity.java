package dk.vinael.fogmain;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import dk.vinael.domain.Party;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class MapsActivity extends FragmentActivity implements OnMapClickListener, LocationListener, OnMarkerClickListener {

	private Location loc = new Location("Your Location");
	private GoogleMap map;
	private ArrayList<Party> parties;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.framelayout);
		
		// Getting Data From Bundle
		Bundle bundle = getIntent().getExtras();
		loc.set((Location) bundle.get("Location"));
		
		// Standard Google Maps Setup.
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(loc.getLatitude(), loc.getLongitude()));
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
		map.moveCamera(center);
		map.animateCamera(zoom);

		
		if (bundle.size() > 1) {
			map.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())).title("new location"));
			parties = (ArrayList<Party>) bundle.get("list");
			addExisting();
		} else {
			map.setOnMapClickListener(this);
		}
	}

	@Override
	public void onMapClick(LatLng point) {
		final LatLng location = point;
		final Marker mark = map.addMarker(new MarkerOptions().position(location).title("new location"));
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					Location house = new Location("Selected Location");
					house.setLatitude(location.latitude);
					house.setLongitude(location.longitude);
					Intent i = new Intent();
					i.putExtra("newLocation", house);
					setResult(RESULT_OK, i);
					finish();
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					mark.remove();
					break;
				}
			}
		};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Want to set location here?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
	}

	public void addExisting() {
		for (int i = 0; i < parties.size(); i++) {
				double lat = parties.get(i).getLat();
				double lon = parties.get(i).getLon();
				LatLng latLng = new LatLng(lat, lon);
				Location temp2 = new Location("Point");
				temp2.setLatitude(lat);
				temp2.setLongitude(lon);
				map.addMarker(new MarkerOptions().position(latLng).title(parties.get(i).getName()));
		}
	}
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

	@Override
	public boolean onMarkerClick(final Marker marker) {
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
		return false;
	}
}
