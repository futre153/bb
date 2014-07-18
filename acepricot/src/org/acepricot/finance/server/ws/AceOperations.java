package org.acepricot.finance.server.ws;

import java.util.HashMap;

import org.apache.axis2.AxisFault;

public class AceOperations {

	@SuppressWarnings({ "rawtypes", "unused" })
	private static HashMap map = new HashMap();
	
	public Message getMessage(Message message) throws AxisFault {
		if(message != null) {
			MessageProcessor mp = new MessageProcessor();
			mp.process(message);
		}
		else {
			message = new Message();
			message.setContent("baca");
			message.setReference("jano");
		}
		return message;
	}
	
}
