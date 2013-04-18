package dk.vinael.domain;

import java.sql.Date;

public class Party {
	
	private int id;
	private int owner_id;
	private int status_id;
	private String name, description, address, zip, city, country, door_code;
	private Date start;
	private Date end;
	private int min_age;
	private int max_age;
	private boolean photos;
	private boolean wall;
	
	public Party(int id, int own, int status, String name, String desc, String add, String code, Date s, Date e, int agel, int agem, boolean pho, boolean wal) {
		this.id = id; 
		this.owner_id = own;
		this.status_id = status;
		this.description = desc;
		this.address = add;
		this.door_code = code;
		this.start = s;
		this.end = e;
		this.min_age = agel;
		this.max_age = agem;
		this.photos = pho;
		this.wall = wal;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOwner_id() {
		return owner_id;
	}
	public void setOwner_id(int owner_id) {
		this.owner_id = owner_id;
	}
	public int getStatus_id() {
		return status_id;
	}
	public void setStatus_id(int status_id) {
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
	public String getDoor_code() {
		return door_code;
	}
	public void setDoor_code(String door_code) {
		this.door_code = door_code;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	public int getAge_low() {
		return min_age;
	}
	public void setAge_low(int age_low) {
		this.min_age = age_low;
	}
	public int getAge_max() {
		return max_age;
	}
	public void setAge_max(int age_max) {
		this.max_age = age_max;
	}
	public boolean isPhotos() {
		return photos;
	}
	public void setPhotos(boolean photos) {
		this.photos = photos;
	}
	public boolean isWall() {
		return wall;
	}
	public void setWall(boolean wall) {
		this.wall = wall;
	}
	public String toString() {
		return name;
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
}
