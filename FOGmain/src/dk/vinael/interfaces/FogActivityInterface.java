package dk.vinael.interfaces;

import org.json.JSONArray;

import android.location.Location;

public interface FogActivityInterface {
	public void jsonArrayHandler(JSONArray ja, String identifier);
	public void returningAddress(String Address, String identifier);
	public void returningLocation(Location location, String identifier);
}

