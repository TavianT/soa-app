package org.tavian.scc.soa.models;

public class Offer {
	Proposal proposal;
	Weather weather;
	
	//getters
	public Proposal getProposal() {
		return proposal;
	}
	public Weather getWeather() {
		return weather;
	}
	//setters
	public void setProposal(Proposal proposal) {
		this.proposal = proposal;
	}
	public void setWeather(Weather weather) {
		this.weather = weather;
	}
}
