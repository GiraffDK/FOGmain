package dk.vinael.domain;

import dk.vinael.interfaces.FogActivityInterface;
import android.app.Activity;
import android.widget.Toast;
import asynctasks.WebserviceCaller;

public class SqlWrapper {
	
	/* Select Parties (SearchResultPartyActivity) */
	public static void selectParties(FogActivityInterface activity, String identifier, double lat, double lon, double radius, 
			int min_age, int max_age){
		
		String sqlString = "SELECT * FROM party WHERE lat > '"+(lat - radius)+"' AND " +
					"lat < '"+(lat + radius)+"' AND " +
					"lon > '"+(lon - radius)+"' AND " +
					"lon < '"+(lon + radius)+"' AND " +
					"min_age >= "+min_age+" AND " +
					"max_age <= "+max_age+" AND " +
					"end_time >= NOW() AND " +
					"status_id = 1;";
		
		new WebserviceCaller(activity, identifier).execute("select", sqlString);
	}
	
	/*  */
	public static void selectParty(FogActivityInterface activity, String identifier, int party_id){
		String sqlString = "SELECT * from party WHERE id = "+ party_id + ";";
		new WebserviceCaller(activity, identifier).execute("select", sqlString);
	}
	
	public static void createParty(FogActivityInterface activity, String identifier, Party p){
		String sqlString = "INSERT INTO party " +
				"(" +
					"id, "+
					"owner_user_id, " +
					"status_id, " +
					"name, " +
					"description, " +
					"address, " +
					"zip, " +
					"city, " +
					"country, " +
					"door_code, " +
					"start_time, " +
					"end_time, " +
					"min_age, " +
					"max_age, " +
					"show_photos, " +
					"show_wall, " + 
					"lat, " +
					"lon" +
				") VALUES " +
				"(" +
					0+", "+
					""+p.getOwnerId()+", " +
					""+p.getStatusId()+", " +
					"'"+p.getName()+"', " +
					"'"+p.getDescription()+"', " +
					"'"+p.getAddress()+"', " +
					"'"+p.getZip()+"', " +
					"'"+p.getCity()+"', " +
					"'"+p.getCountry()+"', " +
					"'"+p.getDoorCode()+"', " +
					"'"+p.getStartTime()+"', " +
					"'"+p.getEndTime()+"', " +
					""+p.getMinAge()+", " +
					""+p.getMaxAge()+", " +
					""+p.getShowPhotos()+", " +
					""+p.getShowWall()+", " +
					"'"+p.getLat()+"', " +
					"'"+p.getLon()+"'" +
							");";
		//Toast.makeText((Activity) activity, sqlString, Toast.LENGTH_LONG).show();
		new WebserviceCaller(activity, identifier).execute("insert", sqlString);
	}
	
	public static void selectUserByToken(FogActivityInterface activity, String identifier, User u){
		String sqlString ="SELECT * FROM user WHERE token = '"+u.getToken()+"';";
		new WebserviceCaller(activity, identifier).execute("select", sqlString);
	}
}