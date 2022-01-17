package org.tavian.scc.soa;

import org.tavian.scc.soa.messagequeues.Subscriber;

public class MqThread implements Runnable {
	int id;
	
	public MqThread(int id) {
		this.id = id;
	}
	public void run() {
		Subscriber subscriber = new Subscriber();
		subscriber.setTopic("proposals");
		subscriber.consumeProposals(id);
		subscriber.setTopic("intents_" + String.valueOf(id));
		subscriber.consumeIntents(id);
		subscriber.setTopic("acknowledgements_" + String.valueOf(id));
		subscriber.consumeAcknowledgements(id);
	}
}
