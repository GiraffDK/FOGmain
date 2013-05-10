package dk.vinael.domain;

import dk.vinael.interfaces.FogActivityInterface;
import android.app.Activity;
import android.widget.Toast;
import asynctasks.WebserviceCaller;

public class SqlWrapper {
	
	/* Select Parties (SearchResultPartyActivity) */
	public static void selectParties(FogActivityInterface activity, String identifier, double lat, double lon, double radius, 
			int min_age, int max_age, int userID){
		
		String sqlString = "SELECT * FROM party WHERE lat > '"+(lat - radius)+"' AND " +
					"lat < '"+(lat + radius)+"' AND " +
					"lon > '"+(lon - radius)+"' AND " +
					"lon < '"+(lon + radius)+"' AND " +
					"min_age >= "+min_age+" AND " +
					"max_age <= "+max_age+" AND " +
					"end_time >= NOW() AND " +
					"owner_user_id != " +userID + " AND " +
					"status_id = 1;";
		
		new WebserviceCaller(activity, identifier).execute("select", sqlString);
	}
	public static void selectPartiesUserAttended(FogActivityInterface activity, String identifier, int month, int year) {
		String sqlString = "SELECT party.* FROM user_in_party " +
				"INNER JOIN party ON user_in_party.party_id = party.id WHERE user_in_party.user_id = 3 AND " +
				"(SELECT RIGHT(LEFT(DATE_FORMAT(party.end_time, '%Y-%m-%d'), 7), 2)) = " + month + " AND " +
				"(SELECT LEFT(DATE_FORMAT(party.end_time, '%Y-%m-%d'), 4)) = " + year + " " +
				"AND NOW() > party.start_time;";
		new WebserviceCaller(activity, identifier).execute("select", sqlString);
	}
	
	/*  */
	public static void selectParty(FogActivityInterface activity, String identifier, int party_id){
		String sqlString = "SELECT * from party WHERE id = "+ party_id + ";";
		new WebserviceCaller(activity, identifier).execute("select", sqlString);
	}
	public static void selectAllPartiesByOwnerUserId(FogActivityInterface activity, String identifier, User u) {
		String sqlString = "SELECT * FROM party WHERE owner_user_id = " + u.getUserId() + ";";
		new WebserviceCaller(activity, identifier).execute("select", sqlString);
	}
	public static void selectAllPartiesImAttending(FogActivityInterface activity, String identifier, User u) {
		String sqlString = "SELECT party.* FROM user_in_party " +
				"INNER JOIN party ON user_in_party.party_id = party.id " +
				"WHERE user_in_party.user_id = " + u.getUserId() +" " +
				"AND user_in_party.attending_status_id = 2;";
		new WebserviceCaller(activity, identifier).execute("select", sqlString);
	}
	public static void selectAllPartiesIveRequested(FogActivityInterface activity, String identifier, User u) {
		String sqlString = "SELECT party.* FROM user_in_party " +
				"INNER JOIN party ON user_in_party.party_id = party.id " +
				"WHERE (user_in_party.attending_status_id = 1 OR user_in_party.attending_status_id = 3) " +
				"AND user_in_party.user_id = " + u.getUserId() +"";
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
					"lon, " +
					"max_guests" +
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
					"'"+p.getStartDateAndTime()+"', " +
					"'"+p.getEndDateAndTime()+"', " +
					""+p.getMinAge()+", " +
					""+p.getMaxAge()+", " +
					""+p.getShowPhotos()+", " +
					""+p.getShowWall()+", " +
					"'"+p.getLat()+"', " +
					"'"+p.getLon()+"', " +
					""+p.getMaxGuests()+"" +
							");";
		//Toast.makeText((Activity) activity, sqlString, Toast.LENGTH_LONG).show();
		new WebserviceCaller(activity, identifier).execute("insert", sqlString);
	}
	
	public static void editParty(FogActivityInterface activity, String identifier, Party p){
		String sqlString = "UPDATE party SET " +
					"status_id = " + p.getStatusId() + ", "+
					"name = '" + p.getName() + "', "+
					"description = '" + p.getDescription() + "', "+
					"address = '" + p.getAddress() + "', "+
					"zip = '" + p.getZip() + "', "+
					"city = '" + p.getCity() + "', "+
					"country = '" + p.getCountry() + "', "+
					"door_code = '" + p.getDoorCode() + "', "+
					"start_time = '" + p.getStartDateAndTime() + "', "+
					"end_time = '" + p.getEndDateAndTime() + "', "+
					"min_age = " + p.getMinAge() + ", "+
					"max_age = " + p.getMaxAge() + ", "+
					"show_photos = " + p.getShowPhotos() + ", "+
					"show_wall = " + p.getShowWall() + ", "+
					"lat = '" + p.getLat() + "', "+
					"lon = '" + p.getLon() + "',"+
					"max_guests = " + p.getMaxGuests() + ""+
				" WHERE id = "+p.getId()+";";
		//Toast.makeText((Activity) activity, sqlString, Toast.LENGTH_LONG).show();
		new WebserviceCaller(activity, identifier).execute("update", sqlString);
	}
	
	public static void selectUserByToken(FogActivityInterface activity, String identifier, User u){
		String sqlString = "SELECT * FROM user WHERE token = '"+u.getToken()+"';";
		new WebserviceCaller(activity, identifier).execute("select", sqlString);
	}
	public static void selectUserById(FogActivityInterface activity, String identifier, User u){
		String sqlString = "SELECT * FROM user WHERE user_id = '"+u.getUserId()+"';";
		new WebserviceCaller(activity, identifier).execute("select", sqlString);
	}
	
	public static void selectUserByEmailAndPassword(FogActivityInterface activity, String identifier, User u){
		String sqlString = "SELECT * FROM user WHERE email='"+u.getEmail()+"' AND password='"+u.getPassword()+"';";
		new WebserviceCaller(activity, identifier).execute("select", sqlString);
	}
	
	public static void updateUser(FogActivityInterface activity, String identifier, User u) {
		String sqlString ="UPDATE user " +
				"SET email = '" + u.getEmail()+ "', firstname = '"+u.getFirstName()+"', " +
				"lastname = '"+u.getLastName() +"', description = '" + u.getDescription() + "' , birthdate = '" + u.getBirthdate() + "', " +
				"address = '" +u.getAddress() + "', zip = '"+u.getZip() +"', city = '" + u.getCity() +"', country = '" +u.getCountry() +"' " +
				"WHERE user.user_id = "+u.getUserId()+";";
		new WebserviceCaller(activity, identifier).execute("update", sqlString);
	}
	
	public static void selectUserInParty(FogActivityInterface activity, String identifier, Party p, User u){
		String sqlString = "SELECT * FROM user_in_party WHERE party_id="+p.getId()+" AND user_id="+u.getUserId()+";";
		new WebserviceCaller(activity, identifier).execute("select", sqlString);
	}
	
	public static void userRequestParty(FogActivityInterface activity, String identifier, Party p, User u){
		String sqlString = "INSERT INTO user_in_party VALUES (0, "+u.getUserId()+", "+p.getId()+", 1, 0);";
		new WebserviceCaller(activity, identifier).execute("insert", sqlString);
	}
	
	public static void userAcceptedToParty(FogActivityInterface activity, String identifier, Party p, User u){
		String sqlString = "UPDATE user_in_party SET attending_status_id=2 WHERE party_id="+p.getId()+" AND user_id="+u.getUserId()+";";
		new WebserviceCaller(activity, identifier).execute("update", sqlString);
	}
	
	public static void userDeniedToParty(FogActivityInterface activity, String identifier, Party p, User u){
		String sqlString = "UPDATE user_in_party SET attending_status_id=3 WHERE party_id="+p.getId()+" AND user_id="+u.getUserId()+";";
		new WebserviceCaller(activity, identifier).execute("update", sqlString);
	}
	
	public static void userCancelRequestParty(FogActivityInterface activity, String identifier, Party p, User u){
		String sqlString = "DELETE FROM user_in_party WHERE party_id="+p.getId()+" AND user_id="+u.getUserId()+";";
		new WebserviceCaller(activity, identifier).execute("delete", sqlString);
	}
	
	
	public static void getPartyRequesters(FogActivityInterface activity, String identifier, Party p){
		//String sqlString = "SELECT * FROM user_in_party WHERE party_id="+p.getId()+" AND attending_status_id=1";
		String sqlString = "SELECT user.* FROM user INNER JOIN user_in_party ON user_in_party.user_id = user.user_id WHERE user_in_party.party_id="+p.getId()+" AND user_in_party.attending_status_id=1;";
		new WebserviceCaller(activity, identifier).execute("select", sqlString);
	}
	
	public static void getPartyDenied(FogActivityInterface activity, String identifier, Party p){
		//String sqlString = "SELECT * FROM user_in_party WHERE party_id="+p.getId()+" AND attending_status_id=1";
		String sqlString = "SELECT user.* FROM user INNER JOIN user_in_party ON user_in_party.user_id = user.user_id WHERE user_in_party.party_id="+p.getId()+" AND user_in_party.attending_status_id=3;";
		new WebserviceCaller(activity, identifier).execute("select", sqlString);
	}
	
	public static void getPartyAttendees(FogActivityInterface activity, String identifier, Party p){
		//String sqlString = "SELECT * FROM user_in_party WHERE party_id="+p.getId()+" AND attending_status_id=1";
		String sqlString = "SELECT user.* FROM user INNER JOIN user_in_party ON user_in_party.user_id = user.user_id WHERE user_in_party.party_id="+p.getId()+" AND user_in_party.attending_status_id=2;";
		new WebserviceCaller(activity, identifier).execute("select", sqlString);
	}
	
	public static void getUserInPartyStatusByParty(FogActivityInterface activity, String identifier, Party p, User u){
		String sqlString = "SELECT user_in_party.attending_status_id, user_in_party.party_id FROM user_in_party WHERE user_in_party.party_id="+p.getId()+" AND user_in_party.user_id="+u.getUserId()+";";
		new WebserviceCaller(activity, identifier).execute("select", sqlString);
	}
	
	public static void resetUserToken(FogActivityInterface activity, String identifier, User user){
		String sqlString = "UPDATE user SET token=NULL WHERE user.user_id ="+user.getUserId()+";";
		new WebserviceCaller(activity, identifier).execute("update", sqlString);
	}
	
}
