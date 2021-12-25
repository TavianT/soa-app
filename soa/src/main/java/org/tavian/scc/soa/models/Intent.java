package org.tavian.scc.soa.models;

public class Intent {
	int userId;
	int proposalUserId;
	String msgId;
	
	//getters
	public int getUserId() {
		return userId;
	}
	public int getProposalUserId() {
		return proposalUserId;
	}
	public String getMsgId() {
		return msgId;
	}
	//setters
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public void setProposalUserId(int proposalUserId) {
		this.proposalUserId = proposalUserId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
}
