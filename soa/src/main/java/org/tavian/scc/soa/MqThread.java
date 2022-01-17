package org.tavian.scc.soa;

import org.tavian.scc.soa.messagequeues.Subscriber;

public class MqThread implements Runnable {
	int id;
	
	public MqThread(int id) {
		this.id = id;
	}
	public void run() {
		Subscriber proposalSubscriber = new Subscriber();
		proposalSubscriber.setTopic("proposals");
		proposalSubscriber.consumeProposals(id);
		Subscriber intentSubscriber = new Subscriber();
		intentSubscriber.setTopic("intents_" + String.valueOf(id));
		intentSubscriber.consumeIntents(id);
		Subscriber ackSubscriber = new Subscriber();
		ackSubscriber.setTopic("acknowledgements_" + String.valueOf(id));
		ackSubscriber.consumeAcknowledgements(id);
	}
}
