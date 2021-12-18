package org.tavian.scc.soa.models;

public class Proposal {
	int userId;
	String msgId;
	String tripProposalDate;
	Location location;
	
	//getters
	public int getUserId() {
		return userId;
	}
	public String getMsgId() {
		return msgId;
	}
	public String getTripProposalDate() {
		return tripProposalDate;
	}
	public Location getLocation() {
		return location;
	}
	
	//setters
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public void setTripProposalDate(String tripProposalDate) {
		this.tripProposalDate = tripProposalDate;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	
	
}
