package org.tavian.scc.soa.messagequeues;

import org.tavian.scc.soa.models.Acknowledgement;
import org.tavian.scc.soa.models.ErrorMessage;
import org.tavian.scc.soa.models.Intent;
import org.tavian.scc.soa.models.Proposal;
import org.w3c.dom.Document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Publisher {
	private static enum EXCHANGE_TYPE {DIRECT, FANOUT, TOPIC, HEADERS};

	private final static String OFFERS_EXCHANGE_NAME = "TRAVEL_OFFERS";
	private final static String INTENT_EXCHANGE_NAME = "TRAVEL_INTENT";
	private ConnectionFactory factory;
	private String topic = "";
	ObjectMapper mapper;
	TransformerFactory transformerFactory;
	
	public Publisher() {
		factory = new ConnectionFactory();
        factory.setHost("152.71.155.95"); // Add here the IP provided by your tutor
        factory.setUsername("student"); // Add here the username provided by your tutor
        factory.setPassword("COMP30231"); // Add here the password provided by your tutor
        mapper = new ObjectMapper();
        transformerFactory = TransformerFactory.newInstance();
	}
	
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	public void publishProposal(Proposal proposal) {
		try(Connection connection = factory.newConnection();
			Channel channel = connection.createChannel()) {
			String proposalString = mapper.writeValueAsString(proposal);
			
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
			ErrorMessage errorMessage = new ErrorMessage("Unable to submit Proposal. Please Try again later.", 500);
			Response response = Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(errorMessage)
					.build();
			throw new WebApplicationException(response);
		}
	}
	
	public void publishIntent(Intent intent) {
		try(Connection connection = factory.newConnection();
				Channel channel = connection.createChannel()) {
			String intentString = mapper.writeValueAsString(intent);
			channel.exchangeDeclare(INTENT_EXCHANGE_NAME, EXCHANGE_TYPE.TOPIC.toString().toLowerCase());
			channel.basicPublish(INTENT_EXCHANGE_NAME,
					topic,
					new AMQP.BasicProperties.Builder()
					.contentType(MediaType.APPLICATION_JSON)
					.deliveryMode(2)
					.priority(1)
					.userId("student")
					.build(),
					intentString.getBytes(StandardCharsets.UTF_8));
			System.out.println(" [x] Sent '" + topic + ":" + intentString + "'");
		} catch (Exception e) {
			e.printStackTrace();
			ErrorMessage errorMessage = new ErrorMessage("Unable to submit intent. Please Try again later.", 500);
			Response response = Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(errorMessage)
					.build();
			throw new WebApplicationException(response);
		}
	}
	
	public void publishAcknowledgement(Acknowledgement ack) {
		try(Connection connection = factory.newConnection();
				Channel channel = connection.createChannel()) {
			JAXBContext jc = JAXBContext.newInstance(Acknowledgement.class);
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		    Document document = documentBuilder.newDocument();
		    Marshaller marshaller = jc.createMarshaller();
		    marshaller.marshal(ack, document);
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StringWriter stringWriter = new StringWriter();
			StreamResult result = new StreamResult(stringWriter);
			transformer.transform(source, result);
			String ackString = stringWriter.getBuffer().toString();
			
			channel.exchangeDeclare(INTENT_EXCHANGE_NAME, EXCHANGE_TYPE.TOPIC.toString().toLowerCase());
			channel.basicPublish(INTENT_EXCHANGE_NAME,
					topic,
					new AMQP.BasicProperties.Builder()
					.contentType(MediaType.APPLICATION_JSON)
					.deliveryMode(2)
					.priority(1)
					.userId("student")
					.build(),
					ackString.getBytes(StandardCharsets.UTF_8));
			System.out.println(" [x] Sent '" + topic + ":" + ackString + "'");
		} catch (Exception e) {
			e.printStackTrace();
			ErrorMessage errorMessage = new ErrorMessage("Unable to submit acknowledgement. Please Try again later.", 500);
			Response response = Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(errorMessage)
					.build();
			throw new WebApplicationException(response);
		}
	}
	
}
