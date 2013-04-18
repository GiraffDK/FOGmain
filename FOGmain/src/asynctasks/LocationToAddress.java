package asynctasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dk.vinael.fogmain.SearchForPartyActivity;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;

public class LocationToAddress extends AsyncTask<Location, Void, String> {
	private Activity mContext;
	private List<Address> addresses;
	private Location loc;
	private String addressText;
	private String caller;
	
	public LocationToAddress(Location loc, Activity inContext, String caller) {
		super();
		this.loc = loc;
		this.mContext = inContext;
		this.caller = caller;
	}
	

	@Override
	protected String doInBackground(Location... params) {
		Geocoder geocoder = new Geocoder(mContext.getBaseContext(), Locale.getDefault());
		addresses = new ArrayList<Address>();
		if (Geocoder.isPresent()) {
			try {
				// Call the synchronous getFromLocation() method by passing in
				// the
				// lat/long values.
				addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
			} catch (IOException e) {
				e.printStackTrace();
				// Update UI field with the exception.

			}

			if (addresses != null && addresses.size() > 0) {
				Address address = addresses.get(0);
				// Format the first line of address (if available), city, and
				// country name.
				setAddressText(String.format("%s, %s, %s", address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "", address.getLocality(), address.getCountryName()));
				return addressText;
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		if (caller.equals("PartyView")) {
			//((PartyView) mContext).setAddress(getAddressText());	
		} else if (caller.equals("SearchParty")) {
			((SearchForPartyActivity) mContext).setLocationText(getAddressText());
		}	
		super.onPostExecute(result);		
	}
	public String getAddressText() {
		return addressText;
	}


	public void setAddressText(String addressText) {
		this.addressText = addressText;
	}
}
