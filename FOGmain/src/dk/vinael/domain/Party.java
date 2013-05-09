package dk.vinael.domain;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import dk.vinael.interfaces.FogActivityInterface;

public class Party implements Serializable {

	// Attributes
	private int id, owner_user_id, status_id; // id's
	private String name, description, address, zip, city, country, door_code; // party data
	private String start_date_time, end_date_time; // party time
	private int min_age, max_age; // part age
	private int show_photos, show_wall; // show
	private double lat, lon; // geolocation
	private int max_guests;

	
	// Getters / Setters
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOwnerId() {
		return owner_user_id;
	}

	public void setOwnerId(int owner_id) {
		this.owner_user_id = owner_id;
	}

	public int getStatusId() {
		return status_id;
	}

	public void setStatusId(int status_id) {
		this.status_id = status_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDoorCode() {
		return door_code;
	}

	public void setDoorCode(String door_code) {
		if (door_code.length()==0){
			this.door_code = "none";
		}
		else{
			this.door_code = door_code;
		}
	}

	public String getStartDateAndTime() {
		return start_date_time;
	}

	public void setStartDateAndTime(String start_date_time) {
		this.start_date_time = start_date_time;
	}

	public String getEndDateAndTime() {
		return end_date_time;
	}

	public void setEndDateAndTime(String end_date_time) {
		this.end_date_time = end_date_time;
	}
	
	public int getMinAge() {
		return min_age;
	}

	public void setMinAge(int min_age) {
		this.min_age = min_age;
	}

	public int getMaxAge() {
		return max_age;
	}

	public void setMaxAge(int max_age) {
		this.max_age = max_age;
	}

	public int getShowPhotos() {
		return show_photos;
	}

	public void setShowPhotos(int showPhotos) {
		this.show_photos = showPhotos;
	}

	public int getShowWall() {
		return show_wall;
	}

	public void setShowWall(int showWall) {
		this.show_wall = showWall;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}
	
	public int getMaxGuests() {
		return max_guests;
	}

	public void setMaxGuests(int max_guests) {
		this.max_guests = max_guests;
	}
	
	// Constructor(s)

	public Party() {

	}	
	
	
	// Methods


	public String getStartDate(){
		return DateAndTimeStringHandler.getDateFromDateAndTime(start_date_time);
	}
	
	public String getStartTime(){
		return DateAndTimeStringHandler.getTimeFromDateAndTime(start_date_time);
	}
	
	public String getEndDate(){
		return DateAndTimeStringHandler.getDateFromDateAndTime(end_date_time);
	}
	
	public String getEndTime(){
		return DateAndTimeStringHandler.getTimeFromDateAndTime(end_date_time);
	}
	
	public boolean doShowPhotos(){
		if (getShowPhotos()==1){
			return true;
		}
		return false;
	}
	
	public boolean doShowWall(){
		if (getShowWall()==1){
			return true;
		}
		return false;
	}
	
	public String toString() {
		return name;
	}
	
	public void reset(){
		setId(0);
		setOwnerId(0);
		setStatusId(0);
		setName("");
		setDescription("");
		setAddress("");
		setZip("");
		setCity("");
		setCountry("");
		setDoorCode("");
		setStartDateAndTime("");
		setEndDateAndTime("");
		setMinAge(0);
		setMaxAge(0);
		setShowPhotos(0);
		setShowWall(0);
		setLat(0);
		setLon(0);
		setMaxAge(0);
	}
	
	public void setPartyWithJSON(JSONObject obj) {
		try {
			setId(Integer.parseInt(obj.getString("id")));
			setOwnerId(Integer.parseInt(obj.getString("owner_user_id")));
			setStatusId(Integer.parseInt(obj.getString("status_id")));
			setName(obj.getString("name"));
			setDescription(obj.getString("description"));
			setAddress(obj.getString("address"));
			setZip(obj.getString("zip"));
			setCity(obj.getString("city"));
			setCountry(obj.getString("country"));
			setDoorCode(obj.getString("door_code"));
			setStartDateAndTime(obj.getString("start_time"));
			setEndDateAndTime(obj.getString("end_time"));
			setMinAge(Integer.parseInt(obj.getString("min_age")));
			setMaxAge(Integer.parseInt(obj.getString("max_age")));
			setShowPhotos(Integer.parseInt(obj.getString("show_photos")));
			setShowWall(Integer.parseInt(obj.getString("show_wall")));
			setLat(obj.getDouble("lat"));
			setLon(obj.getDouble("lon"));
			setMaxGuests(Integer.parseInt(obj.getString("max_guests")));
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	public void getPartiesUserAttended(FogActivityInterface activity, String identifier, int month, int year) {
		SqlWrapper.selectPartiesUserAttended(activity, identifier, month, year);
	}
	public void setPartyWithAttributes(
			int id, int owner_user_id, int status_id, 
			String name, String description, String address, String zip, String city, String country, String door_code, 
			String start_time, String end_time, int min_age, int max_age,
			int show_photos, int show_wall, double lat, double lon, int max_guests){
		
		setId(id);
		setOwnerId(owner_user_id);
		setStatusId(status_id);
		setName(name);
		setDescription(description);
		setAddress(address);
		setZip(zip);
		setCity(city);
		setCountry(country);
		setDoorCode(door_code);
		setStartDateAndTime(start_time);
		setEndDateAndTime(end_time);
		setMinAge(min_age);
		setMaxAge(max_age);
		setShowPhotos(show_photos);
		setShowWall(show_wall);
		setLat(lat);
		setLon(lon);
		setMaxGuests(max_guests);
	}
	public void selectParties(FogActivityInterface activity, String identifier, double lat, double lon, double radius, 
			int min_age, int max_age, int userID) {
		SqlWrapper.selectParties(activity, identifier, lat, lon, radius, min_age, max_age, userID);
	}
	
	// Statements
	public void getPartyById(FogActivityInterface activity, String identifier,int party_id){
		SqlWrapper.selectParty(activity, identifier, party_id);
	}
	
	public void create(FogActivityInterface activity, String identifier){
		SqlWrapper.createParty(activity, identifier, this);
	}

	public void edit(FogActivityInterface activity, String identifier){
		SqlWrapper.editParty(activity, identifier, this);
	}
	
	public void selectUserInParty(FogActivityInterface activity, String identifier, User u){
		SqlWrapper.selectUserInParty(activity, identifier, this, u);
	}
	
	public void userRequestParty(FogActivityInterface activity, String identifier, User u){
		SqlWrapper.userRequestParty(activity, identifier, this, u);
	}
	
	public void userCancelRequestParty(FogActivityInterface activity, String identifier, User u){
		SqlWrapper.userCancelRequestParty(activity, identifier, this, u);
	}
	
	public void userAcceptedToParty(FogActivityInterface activity, String identifier, User u){
		SqlWrapper.userAcceptedToParty(activity, identifier, this, u);
	}
	
	public void userDeniedToParty(FogActivityInterface activity, String identifier, User u){
		SqlWrapper.userDeniedToParty(activity, identifier, this, u);
	}
	
	public void getPartyRequesters(FogActivityInterface activity, String identifier){
		SqlWrapper.getPartyRequesters(activity, identifier, this);
	}
	
	public void getPartyDenied(FogActivityInterface activity, String identifier){
		SqlWrapper.getPartyDenied(activity, identifier, this);
	}
	
	public void getPartyAttendees(FogActivityInterface activity, String identifier){
		SqlWrapper.getPartyAttendees(activity, identifier, this);
	}
	
}
