package org.tavian.scc.soa.messagequeues;

import java.util.ArrayList;
import java.util.List;

import org.tavian.scc.soa.models.Intent;
import org.tavian.scc.soa.models.Proposal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Subscriber {
	private static enum EXCHANGE_TYPE {DIRECT, FANOUT, TOPIC, HEADERS};
	
	private final static String OFFERS_EXCHANGE_NAME = "TRAVEL_OFFERS";
	private final static String INTENT_EXCHANGE_NAME = "TRAVEL_INTENT";
	private ConnectionFactory factory;
	private String topic = "";
	Connection connection;
	Channel channel;
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
	
	public List<Proposal> consumeProposals(int userId) {
		String queueName = String.valueOf(userId);
		List<Proposal> proposals = new ArrayList<Proposal>();
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
	        	proposals.add(proposal);
	        };
	        
	        while(channel.messageCount(queueName) > 0) {
	        	channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
	        }
	        channel.close();
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("proposals list size: " + proposals.size());
		return proposals;
	}
	
	public List<Intent> consumeIntents(int userId) {
		String queueName = String.valueOf(userId);
		List<Intent> intents = new ArrayList<Intent>();
		try {
			Connection connection = factory.newConnection();
	        Channel channel = connection.createChannel();
	        
	        channel.exchangeDeclare(INTENT_EXCHANGE_NAME, EXCHANGE_TYPE.TOPIC.toString().toLowerCase());
	        //String queueName = channel.queueDeclare().getQueue();
	        channel.queueDeclare(queueName, true, false, false, null);
	        
	        channel.queueBind(queueName, INTENT_EXCHANGE_NAME, topic);
	        
	        System.out.println("Messages in queue: " + channel.messageCount(queueName));
	       
	        
	        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
	            String message = new String(delivery.getBody(), "UTF-8");
	            System.out.println(" [x] Received '" + message + "'");
	        	Intent intent = mapper.readValue(message, Intent.class);
	        	intents.add(intent);
	        };
	        
	        while(channel.messageCount(queueName) > 0) {
	        	channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
	        }
	        channel.close();
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Intents list size: " + intents.size());
		return intents;
		
	}
}
