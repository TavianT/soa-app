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

	private final static String EXCHANGE_NAME = "n0790170_exchange";
	private ConnectionFactory factory;
	private String topic = "";
	Connection connection;
	Channel channel;
	ObjectMapper mapper;
	
	public Publisher() {
		factory = new ConnectionFactory();
        factory.setHost("152.71.155.95"); // Add here the IP provided by your tutor
        factory.setUsername("student"); // Add here the username provided by your tutor
        factory.setPassword("COMP30231"); // Add here the password provided by your tutor
        mapper = new ObjectMapper();
	}
	
	public void publishProposal(Proposal proposal) {
		try {
			String proposalString = mapper.writeValueAsString(proposal);
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE.FANOUT.toString().toLowerCase());
			channel.basicPublish(EXCHANGE_NAME,
					topic,
					new AMQP.BasicProperties.Builder()
					.contentType(MediaType.APPLICATION_JSON)
					.deliveryMode(2)
					.priority(1)
					.userId("student")
					.build(),
				proposalString.getBytes(StandardCharsets.UTF_8));
			System.out.println(" [x] Sent '" + topic + ":" + proposalString + "'");
		} catch(Exception e) {}
	}
	
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
}
