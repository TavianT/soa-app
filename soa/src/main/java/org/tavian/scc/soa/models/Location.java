package org.tavian.scc.soa.models;

public class Location {
	String name;
	String longitude;
	String latitude;
	
	//getters
	public String getName() {
		return name;
	}
	public String getLongitude() {
		return longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	//setters
	public void setName(String name) {
		this.name = name;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
}
