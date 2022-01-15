package org.tavian.scc.soa.models;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

//Wrapper class for Acknowledgements
@XmlRootElement(name = "acknowledgements")
public class Acknowledgements {
	
	List<Acknowledgement> acknowledgements = null;
	
	public List<Acknowledgement> getAcknowledgements() {
		return acknowledgements;
	}
	
	@XmlElement(name = "acknowledgement", required=true)
	public void setAcknowledgements(List<Acknowledgement> acknowledgements) {
		this.acknowledgements = acknowledgements;
	}
	
}
