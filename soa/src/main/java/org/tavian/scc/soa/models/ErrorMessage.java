package org.tavian.scc.soa.models;

public class ErrorMessage {
	String message;
	int errorCode;
	
	public ErrorMessage(String message, int errorCode) {
		this.message = message;
		this.errorCode = errorCode;
	}
	
	//getters
	public String getMessage() {
		return message;
	}
	public int getErrorCode() {
		return errorCode;
	}
	//setters
	public void setMessage(String message) {
		this.message = message;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
}
