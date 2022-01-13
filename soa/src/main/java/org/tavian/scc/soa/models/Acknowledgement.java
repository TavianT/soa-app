package org.tavian.scc.soa.models;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "acknowledgement")
public class Acknowledgement {
	
	int intentUserId;
	String msgId;
	
	public int getIntentUserId() {
		return intentUserId;
	}
	public String getMsgId() {
		return msgId;
	}
	
	@XmlElement(name = "intentUserId", required = true)
	public void setIntentUserId(int intentUserId) {
		this.intentUserId = intentUserId;
	}
	@XmlElement(name = "msgId", required = true)
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
}
