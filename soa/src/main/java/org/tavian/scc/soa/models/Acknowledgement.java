package org.tavian.scc.soa.models;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "acknowledgement")
public class Acknowledgement {
	
	int intentUserId;
	int userId;
	String msgId;
	
	public int getIntentUserId() {
		return intentUserId;
	}
	public String getMsgId() {
		return msgId;
	}
	
	public int getUserId() {
		return userId;
	}
	
	@XmlElement(name = "intentUserId", required = true)
	public void setIntentUserId(int intentUserId) {
		this.intentUserId = intentUserId;
	}
	
	@XmlElement(name = "userId", required = true)
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	@XmlElement(name = "msgId", required = true)
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
}
