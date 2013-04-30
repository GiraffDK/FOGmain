package asynctasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dk.vinael.fogmain.R;
import dk.vinael.fogmain.SearchForPartyActivity;
import dk.vinael.fogmain.AddEditPartyActivity;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

public class LocationToAddress extends AsyncTask<Location, Void, String> {
	private Activity mContext;
	private List<Address> addresses;
	private Location loc;
	private String addressText;
	private String caller;
	private TextView tv_address; 

	public LocationToAddress(Location loc, Activity inContext, String caller) {
		super();
		this.loc = loc;
		this.mContext = inContext;
		this.caller = caller;
		tv_address = (TextView) inContext.findViewById(R.id.et_location);
	}

	@Override
	protected String doInBackground(Location... params) {
		Geocoder geocoder = new Geocoder(mContext.getBaseContext(), Locale.getDefault());
		addresses = new ArrayList<Address>();

		if (Geocoder.isPresent()) {
			try {
				if (caller.equals("AddressToLocation")) {
					addresses = geocoder.getFromLocationName(tv_address.getText().toString(), 5);
					return "AddressToLocation";
				} else if (caller.equals("SearchParty")) {

					addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);

					if (addresses != null && addresses.size() > 0) {
						Address address = addresses.get(0);
						// Format the first line of address (if available),
						// city,
						// and
						// country name.
						setAddressText(String.format("%s, %s, %s", address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "", address.getLocality(), address.getCountryName()));
						return addressText;
					}
				}
				else if (caller.equals("addressLookup")){
					//addresses = geocoder.getFromLocationName(((AddEditPartyActivity) mContext).addressToLookup, 5);

					return "addressLookup";
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		if (caller.equals("PartyView")) {
			// ((PartyView) mContext).setAddress(getAddressText());
		} else if (caller.equals("SearchParty")) {
			((SearchForPartyActivity) mContext).setLocationText(getAddressText());
		} else if (caller.equals("AddressToLocation")) {
			((SearchForPartyActivity) mContext).checkAddress(addresses);
		} else if (caller.equals("addressLookup")) {
			Toast.makeText(mContext, "returning!", Toast.LENGTH_LONG).show();
			//((AddEditPartyActivity) mContext).setLocation(addresses);

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
