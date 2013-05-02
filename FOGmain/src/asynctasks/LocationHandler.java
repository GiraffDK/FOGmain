package asynctasks;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import dk.vinael.interfaces.FogActivityInterface;

public abstract class LocationHandler {
	
	public static  void convertLocationToAddress(FogActivityInterface activity, Location location, String identifier) {
		new LocationToAddress(activity, location, identifier).execute(Type.LOCATIONTOADDRESS);
	}
	public static  void convertAddressToLocation(FogActivityInterface activity, String address, String identifier) {
		new LocationToAddress(activity, address, identifier).execute(Type.ADDRESSTOLOCATION);
		
	}
	private enum Type {
		LOCATIONTOADDRESS, ADDRESSTOLOCATION
	}
	private static class LocationToAddress extends AsyncTask<Type, Void, String> {
		private FogActivityInterface callingAcitivity;
		private Location location;
		private String identifier;
		 
		private List<Address> addresses;
		private String addressText;
		private Type type;
		
		public LocationToAddress(FogActivityInterface inContext, Location loc, String identifier) {
			super();
			this.location = loc;
			this.callingAcitivity = inContext;
			this.identifier = identifier;
		}
		public LocationToAddress(FogActivityInterface inContext, String Address, String identifier) {
			super();
			addressText = Address;
			this.callingAcitivity = inContext;
			this.identifier = identifier;
		}
		@Override
		protected String doInBackground(Type... params) {
			Geocoder geocoder = new Geocoder(((Activity)callingAcitivity).getBaseContext(), Locale.getDefault());
			addresses = new ArrayList<Address>();
			type = params[0];
			
			if (Geocoder.isPresent()) {
				try {
					if (type == Type.ADDRESSTOLOCATION) {
						addresses = geocoder.getFromLocationName(addressText, 5);
						return "AddressToLocation";
						
					} else if (type == Type.LOCATIONTOADDRESS) {
						addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
						if (addresses != null && addresses.size() > 0) {
							Address address = addresses.get(0);
							addressText = String.format("%s, %s, %s", address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "", address.getLocality(), address.getCountryName());
							return addressText;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (type == Type.LOCATIONTOADDRESS) {
				callingAcitivity.returningAddress(addressText, identifier);
			} else if (type == Type.ADDRESSTOLOCATION) {
				Location temp = new Location("");
				if (addresses.size() > 0) {
					Address location = addresses.get(0);
					temp.setLatitude(location.getLatitude());
					temp.setLongitude(location.getLongitude());
					callingAcitivity.returningLocation(temp, identifier);
				} else {
					callingAcitivity.returningLocation(null, identifier);
				}
				
			}
			super.onPostExecute(result);
		}
	}

}
