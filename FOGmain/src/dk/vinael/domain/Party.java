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
		this.door_code = door_code;
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
	}
	
	public void setPartyWithJSON(JSONObject obj) {
		try {
			setId(obj.getInt("id"));
			setOwnerId(obj.getInt("owner_user_id"));
			setStatusId(obj.getInt("status_id"));
			setName(obj.getString("name"));
			setDescription(obj.getString("description"));
			setAddress(obj.getString("address"));
			setZip(obj.getString("zip"));
			setCity(obj.getString("city"));
			setCountry(obj.getString("country"));
			setDoorCode(obj.getString("door_code"));
			setStartDateAndTime(obj.getString("start_time"));
			setEndDateAndTime(obj.getString("end_time"));
			setMinAge(obj.getInt("min_age"));
			setMaxAge(obj.getInt("max_age"));
			setShowPhotos(obj.getInt("show_photos"));
			setShowWall(obj.getInt("show_wall"));
			setLat(obj.getInt("lat"));
			setLon(obj.getInt("lon"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
	public void setPartyWithAttributes(
			int id, int owner_user_id, int status_id, 
			String name, String description, String address, String zip, String city, String country, String door_code, 
			String start_time, String end_time, int min_age, int max_age,
			int show_photos, int show_wall, double lat, double lon){
		
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
}
