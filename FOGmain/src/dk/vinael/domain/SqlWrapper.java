package dk.vinael.domain;

import dk.vinael.interfaces.FogActivityInterface;
import asynctasks.WebserviceCaller;

public class SqlWrapper {

	public static void selectParties(FogActivityInterface activity, String identifier, double lat, double lon, double radius, 
			int min_age, int max_age){
		
		String sqlString = "SELECT * FROM party WHERE lat > '"+(lat - radius)+"' AND " +
					"lat < '"+(lat + radius)+"' AND " +
					"lon > '"+(lon - radius)+"' AND " +
					"lon < '"+(lon + radius)+"' AND " +
					"min_age>="+min_age+" AND " +
					"max_age<="+max_age+" AND " +
					"end_time >= NOW() AND " +
					"status = 1;";
		
		new WebserviceCaller(activity, identifier).execute("select", sqlString);
		//new WebserviceCaller(a, i, t, sqlString);
	}
	
	public static void selectParty(FogActivityInterface activity, String identifier, int party_id){
		String sqlString = "SELECT * from party WHERE id = "+ party_id + ";";
		new WebserviceCaller(activity, identifier).execute("select", sqlString);
	}
	
}
