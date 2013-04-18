package dk.vinael.fogmain;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class MapsActivity extends FragmentActivity implements OnMapClickListener, LocationListener {

	private Location loc = new Location("Your Location");
	private GoogleMap map;
	
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
			addExisting(bundle.getStringArrayList("list"));
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
					// db2.execSQL("INSERT INTO locations (lan, lon) VALUES ('"
					// + house.getLatitude() + "', '" +house.getLongitude()+
					// "')");
					// Toast.makeText(MainActivity.this, "You finger was @ (" +
					// house.getLatitude() + ", " +house.getLongitude()+ ")" ,
					// Toast.LENGTH_SHORT).show();
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

	public void addExisting(ArrayList<String> items) {
		for (int i = 0; i < items.size(); i++) {
				String[] temp = items.get(i).split("-");
				double lan = Double.parseDouble(temp[0]);
				double lon = Double.parseDouble(temp[1]);
				LatLng latLng = new LatLng(lan, lon);
				Location temp2 = new Location("Point");
				temp2.setLatitude(lan);
				temp2.setLongitude(lon);
				map.addMarker(new MarkerOptions().position(latLng).title("new location"));
		}
	}

//	Don't use this method, doesn't work at the moment.
//	public void SQLtry(double d) {
//		// lan and lon is text - doesn't work
//		Cursor cur = db2.rawQuery("SELECT * FROM locations " + "WHERE lan > " + (loc.getLatitude() - d) + " AND lan < " + (loc.getLatitude() + d) + " " + "AND lon > " + (loc.getLongitude() - d)
//				+ " AND lon < " + (loc.getLongitude() + d) + ";", null);
//
//		Toast.makeText(this, cur.getCount() + "", Toast.LENGTH_LONG).show();
//		addExisting(cur, 1000);
//	}

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
}
