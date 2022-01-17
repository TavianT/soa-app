package org.tavian.scc.soa.messagequeues;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMResult;

import org.tavian.scc.soa.ServiceUtils;
import org.tavian.scc.soa.models.Acknowledgement;
import org.tavian.scc.soa.models.ErrorMessage;
import org.tavian.scc.soa.models.Intent;
import org.tavian.scc.soa.models.Proposal;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

public class Subscriber {
	private static enum EXCHANGE_TYPE {DIRECT, FANOUT, TOPIC, HEADERS};
	
	private final static String OFFERS_EXCHANGE_NAME = "TRAVEL_OFFERS";
	private final static String INTENT_EXCHANGE_NAME = "TRAVEL_INTENT";
	private ConnectionFactory factory;
	private String topic = "";
	ObjectMapper mapper;
	
	public Subscriber() {
		factory = new ConnectionFactory();
        factory.setHost("152.71.155.95"); // Add here the IP provided by your tutor
        factory.setUsername("student"); // Add here the username provided by your tutor
        factory.setPassword("COMP30231"); // Add here the password provided by your tutor
        mapper = new ObjectMapper();
	}
	
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	public void declareQueue(int userId) {
		try {
			Connection connection = factory.newConnection();
	        Channel channel = connection.createChannel();
	        channel.queueDeclare(String.valueOf(userId), true, false, false, null);
	        channel.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void consumeProposals(int userId) {
		String queueName = String.valueOf(userId);
		try {
			Connection connection = factory.newConnection();
	        Channel channel = connection.createChannel();
	        
	        channel.exchangeDeclare(OFFERS_EXCHANGE_NAME, EXCHANGE_TYPE.TOPIC.toString().toLowerCase());
	        //String queueName = channel.queueDeclare().getQueue();
	        channel.queueDeclare(queueName, true, false, false, null);
	        
	        channel.queueBind(queueName, OFFERS_EXCHANGE_NAME, topic);
	        
	        System.out.println("Messages in queue: " + channel.messageCount(queueName));
	       
	        
	        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
	            String message = new String(delivery.getBody(), "UTF-8");
	            System.out.println(" [x] Received '" + message + "'");
	        	Proposal proposal = mapper.readValue(message, Proposal.class);
	        	ServiceUtils.addProposal(userId, proposal); //add proposal to users proposal file
	        };
	        
	        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void consumeIntents(int userId) {
		String queueName = String.valueOf(userId);
		try {
			Connection connection = factory.newConnection();
	        Channel channel = connection.createChannel();
	        
	        channel.exchangeDeclare(INTENT_EXCHANGE_NAME, EXCHANGE_TYPE.TOPIC.toString().toLowerCase());
	        channel.queueDeclare(queueName, true, false, false, null);
	        
	        channel.queueBind(queueName, INTENT_EXCHANGE_NAME, topic);
	        
	        System.out.println("Messages in queue: " + channel.messageCount(queueName));
	       
	        
	        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
	            String message = new String(delivery.getBody(), "UTF-8");
	            System.out.println(" [x] Received '" + message + "'");
	        	Intent intent = mapper.readValue(message, Intent.class);
	        	ServiceUtils.addIntent(userId, intent);
	        };
	        
	        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void consumeAcknowledgements(int userId) {
		String queueName = String.valueOf(userId);
		try {
			Connection connection = factory.newConnection();
	        Channel channel = connection.createChannel();
	        JAXBContext jc = JAXBContext.newInstance(Acknowledgement.class);
	        
	        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
	        
	        channel.exchangeDeclare(INTENT_EXCHANGE_NAME, EXCHANGE_TYPE.TOPIC.toString().toLowerCase());
	        channel.queueDeclare(queueName, true, false, false, null);
	        
	        channel.queueBind(queueName, INTENT_EXCHANGE_NAME, topic);
	       
	        
	        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
	            String message = new String(delivery.getBody(), "UTF-8");
	            System.out.println(" [x] Received '" + message + "'");
	            try {
	            	Document document = documentBuilder.parse(new InputSource(new StringReader(message)));
		            Unmarshaller unmarshaller = jc.createUnmarshaller();
		            Acknowledgement ack = (Acknowledgement) unmarshaller.unmarshal(document);
		            ServiceUtils.addAcknowledgement(userId, ack);
	            } catch (Exception e) {
	            	e.printStackTrace();
	            }
	            
	        };
	        
	        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
