package dk.vinael.domain;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.entity.SerializableEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.widget.Toast;

import dk.vinael.interfaces.FogActivityInterface;

public class User implements Serializable {
	
	// User attributes
	private int userId;
	private String email;
	private String firstName;
	private String lastName;
	private String description;
	private String fbUserId;
	private String token;
	private String password;
	private String birthdate;
	private String address;
	private String zip;
	private String city;
	private String country;
	private String phoneNr;
	
	// Getters and setters

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFbUserId() {
		return fbUserId;
	}

	public void setFbUserId(String fbUserId) {
		this.fbUserId = fbUserId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
		
		/* use HASH! */
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(password.getBytes());
			byte byteData[] = md.digest();
			
			StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < byteData.length; i++) {
	        	sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	        }
	        
	        this.password = sb.toString();
	        
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	public String getPhoneNr() {
		return phoneNr;
	}

	public void setPhoneNr(String phoneNr) {
		this.phoneNr = phoneNr;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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
	
	// Constructor
	public void reset(){
		userId=0;
		email=null;
		firstName=null;
		lastName=null;
		description=null;
		fbUserId=null;
		token=null;
		password=null;
		birthdate=null;
		address=null;
		zip=null;
		city=null;
		country=null;		
	}
	
	// Methods
	
	public String toString(){
		/*
		return "user_id: " + getUserId() +
				"\nemail: " + getEmail() +
				"\nfirstname: " + getFirstName() +
				"\nlastname: " + getLastName() +
				"\ndescription: " + getDescription() +
				"\nfb_user_id: " + getFbUserId() +
				"\ntoken: " + getToken() +
				"\npassword: " + getPassword() +
				"\nbirthdate: " + getBirthdate() +
				"\naddress: " + getAddress() +
				"\nzip: " + getZip() +
				"\ncity: " + getCity() +
				"\ncountry: " + getCountry() +
				"";
		*/
		
		return getFirstName() + " " + getLastName();
	}
	
	public void setUserByJson(JSONObject jo){
		// set attributes by jo
		try {
			setUserId(Integer.parseInt(jo.getString("user_id")));
			setEmail(jo.getString("email"));
			setFirstName(jo.getString("firstname"));
			setLastName(jo.getString("lastname"));
			setDescription(jo.getString("description"));
			setFbUserId(jo.getString("fb_user_id"));
			setToken(jo.getString("token"));
			setPassword("");
			setBirthdate(jo.getString("birthdate"));
			setAddress(jo.getString("address"));
			setZip(jo.getString("zip"));
			setCity(jo.getString("city"));
			setCountry(jo.getString("country"));
			setPhoneNr(jo.getString("phone_nr"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void selectUserByToken(FogActivityInterface a, String i){
		SqlWrapper.selectUserByToken(a, i, this);
	}
	
	public void selectUserByEmailAndPassword(FogActivityInterface a, String i){
		SqlWrapper.selectUserByEmailAndPassword(a, i, this);
	}
	public void selectUserByUserID(FogActivityInterface a, String i){
		SqlWrapper.selectUserById(a, i, this);
	}
	
	public void selectAllPartiesByOwnerUserId(FogActivityInterface activity, String identifier){
		SqlWrapper.selectAllPartiesByOwnerUserId(activity, identifier, this);
	}
	
	public void selectAllPartiesImAttending(FogActivityInterface activity, String identifier) {
		SqlWrapper.selectAllPartiesImAttending(activity, identifier, this);
	}
	
	public void selectAllPartiesIveRequested(FogActivityInterface activity, String identifier) {
		SqlWrapper.selectAllPartiesIveRequested(activity, identifier, this);
	}
	
	public void getUserInPartyStatusByParty(FogActivityInterface activity, String identifier, Party party){
		SqlWrapper.getUserInPartyStatusByParty(activity, identifier, party, this);
	}
	
	public void resetUserToken(FogActivityInterface activity, String identifier){
		SqlWrapper.resetUserToken(activity, identifier, this);
	}
}
