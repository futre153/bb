package org.pabk.rfc.rfc1157;

import org.pabk.ber.BERChoice;
import org.pabk.ber.BERInteger;
import org.pabk.ber.BEROctetString;
import org.pabk.ber.BEROid;
import org.pabk.ber.BERSequence;
import org.pabk.ber.BERSequenceOf;
import org.pabk.ber.Tagged;
import org.pabk.rfc.rfc1155.RFC1155;

public final class RFC1157 {
	
	public static final String TRAP_PDU_NAME 			= "trap";
	public static final String ENTERPRISE_NAME 			= "enterprise";
	public static final String VARIABLE_BINDINGS_NAME	= "trap";
	
	private static final Object[] VAR_BIND_SEQUENCE = {
		RFC1155.getObjectName("name"),
		RFC1155.getObjectSyntax("value")
	};

	private static final Object[] VAR_BIND_LIST_SEQUENCEOF = {
		new BERSequence(null, VAR_BIND_SEQUENCE)
	};

	private static final Object[] PDU_SEQUENCE = {
		new BERInteger("request-id"),
		new BERInteger("error-status"),
		new BERInteger("error-index"),
		new BERSequenceOf("variable-bindings", VAR_BIND_LIST_SEQUENCEOF)
	};

	private static final Object[] TRAP_PDU_SEQUENCE = {
		new BEROid("enterprise"),
		RFC1155.getNetworkAddress("agent-addr"),
		new BERInteger("generic-trap"),
		new BERInteger("specific-trap"),
		RFC1155.getTimeTicks("time-stamp"),
		new BERSequenceOf("variable-bindings", VAR_BIND_LIST_SEQUENCEOF)
	};

	private static final Object[] DATA_CHOICE = {
		new Tagged("get-request", new BERSequence(null, PDU_SEQUENCE), 0, true, false),
		new Tagged("get-next-request", new BERSequence(null, PDU_SEQUENCE), 1, true, false),
		new Tagged("get-response", new BERSequence(null, PDU_SEQUENCE), 2, true, false),
		new Tagged("set-request", new BERSequence(null, PDU_SEQUENCE), 3, true, false),
		new Tagged(TRAP_PDU_NAME, new BERSequence(null, TRAP_PDU_SEQUENCE), 4, true, false)
	};
	
	private static final Object[] MESSAGE_SEQUENCE = {
		new BERInteger("version"),
		new BEROctetString("community"),
		new BERChoice("data", DATA_CHOICE, -1)
	};
	
	private static final BERSequence MESSAGE = new BERSequence("message",MESSAGE_SEQUENCE); 
	
	public static final BERSequence getMessage(String name) {return (BERSequence) MESSAGE.copy(name);}
		
}
