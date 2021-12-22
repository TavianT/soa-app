package org.tavian.scc.soa.models;

public class Weather {
	int temp;
	int windSpeed;
	String weatherDesc;
	
	//getters
	public int getTemp() {
		return temp;
	}
	public int getWindSpeed() {
		return windSpeed;
	}
	public String getWeatherDesc() {
		return weatherDesc;
	}
	//setters
	public void setTemp(int temp) {
		this.temp = temp;
	}
	public void setWindSpeed(int windSpeed) {
		this.windSpeed = windSpeed;
	}
	public void setWeatherDesc(String weatherDesc) {
		this.weatherDesc = weatherDesc;
	}
}
