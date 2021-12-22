package org.tavian.scc.soa.messagequeues;

import org.tavian.scc.soa.models.Proposal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import jakarta.ws.rs.core.MediaType;

import java.nio.charset.StandardCharsets;

public class Publisher {
	private static enum EXCHANGE_TYPE {DIRECT, FANOUT, TOPIC, HEADERS};

	private final static String OFFERS_EXCHANGE_NAME = "TRAVEL_OFFERS";
	private final static String INTENT_EXCHANGE_NAME = "TRAVEL_INTENT";
	private ConnectionFactory factory;
	private String topic = "";
	//Connection connection;
	//Channel channel;
	ObjectMapper mapper;
	
	public Publisher() {
		factory = new ConnectionFactory();
        factory.setHost("152.71.155.95"); // Add here the IP provided by your tutor
        factory.setUsername("student"); // Add here the username provided by your tutor
        factory.setPassword("COMP30231"); // Add here the password provided by your tutor
        mapper = new ObjectMapper();
	}
	
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	public void publishProposal(Proposal proposal) {
		try(Connection connection = factory.newConnection();
			Channel channel = connection.createChannel()) {
			String proposalString = mapper.writeValueAsString(proposal);
			
			//channel.exchangeDelete(OFFERS_EXCHANGE_NAME);
			
			channel.exchangeDeclare(OFFERS_EXCHANGE_NAME, EXCHANGE_TYPE.TOPIC.toString().toLowerCase());
			channel.basicPublish(OFFERS_EXCHANGE_NAME,
					topic,
					new AMQP.BasicProperties.Builder()
					.contentType(MediaType.APPLICATION_JSON)
					.deliveryMode(2)
					.priority(1)
					.userId("student")
					.build(),
				proposalString.getBytes(StandardCharsets.UTF_8));
			System.out.println(" [x] Sent '" + topic + ":" + proposalString + "'");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
