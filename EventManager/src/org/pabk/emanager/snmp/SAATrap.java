package org.pabk.emanager.snmp;

import org.pabk.ber.BERInteger;
import org.pabk.ber.BEROctetString;
import org.pabk.ber.BEROid;
import org.pabk.ber.BERSequence;
import org.pabk.ber.Tagged;
import org.pabk.rfc.rfc1157.RFC1157;

public class SAATrap {
	
	public static final String SAA_INSTANCE="saaInstance";
	public static final String SAA_DATE="saaDate";
	public static final String SAA_TIME="saaTime";
	public static final String SAA_PLUGIN="saaPlugin";
	public static final String SAA_EVENT_NUMBER="saaEventNumber";
	public static final String SAA_EVENT_SEVERITY="saaInstance";
	public static final String SAA_EVENT_CLASS="saaEventClass";
	public static final String SAA_EVENT_NAME="saaEventName";
	public static final String SAA_EVENT_DESCRIPTION="saaEventDescription";
	public static final String VALUE="value";
	
	
	public static final BEROid SWIFT	= new BEROid("swift",new int[]{1,3,6,1,4,1,18494});
	public static final BEROid SAA		= new BEROid("saa",new int[]{1,3,6,1,4,1,18494,2});
	public static final BEROid SAATRAP	= new BEROid("saatrap",new int[]{1,3,6,1,4,1,18494,2,1});
	
	public static final BEROid SAA_INSTANCE_ID = new BEROid("saaInstanceId",new int[]{1,3,6,1,4,1,18494,2,1,1});
	public static final BEROid SAA_DATE_ID = new BEROid("saaDateId",new int[]{1,3,6,1,4,1,18494,2,1,2});
	public static final BEROid SAA_TIME_ID = new BEROid("saaTimeId",new int[]{1,3,6,1,4,1,18494,2,1,3});
	public static final BEROid SAA_PLUGIN_ID = new BEROid("saaPluginId",new int[]{1,3,6,1,4,1,18494,2,1,4});
	public static final BEROid SAA_EVENT_NUMBER_ID = new BEROid("saaEventNumberId",new int[]{1,3,6,1,4,1,18494,2,1,5});
	public static final BEROid SAA_EVENT_SEVERITY_ID = new BEROid("saaEventSeverityId",new int[]{1,3,6,1,4,1,18494,2,1,6});
	public static final BEROid SAA_EVENT_CLASS_ID = new BEROid("saaEventClassId",new int[]{1,3,6,1,4,1,18494,2,1,7});
	public static final BEROid SAA_EVENT_NAME_ID = new BEROid("saaEventNameId",new int[]{1,3,6,1,4,1,18494,2,1,8});
	public static final BEROid SAA_EVENT_DESCRIPTION_ID = new BEROid("saaEventDescriptionId",new int[]{1,3,6,1,4,1,18494,2,1,9});
		
	private final BERSequence saaInstance = new BERSequence(SAA_INSTANCE,new Object[]{SAA_INSTANCE_ID,new BEROctetString(VALUE)});
	private final BERSequence saaDate = new BERSequence(SAA_DATE,new Object[]{SAA_DATE_ID,new BEROctetString(VALUE)});
	private final BERSequence saaTime = new BERSequence(SAA_TIME,new Object[]{SAA_TIME_ID,new BEROctetString(VALUE)});
	private final BERSequence saaPlugin = new BERSequence(SAA_PLUGIN,new Object[]{SAA_PLUGIN_ID,new BEROctetString(VALUE)});
	private final BERSequence saaEventNumber = new BERSequence(SAA_EVENT_NUMBER,new Object[]{SAA_EVENT_NUMBER_ID,new BERInteger(VALUE)});
	private final BERSequence saaEventSeverity = new BERSequence(SAA_EVENT_SEVERITY,new Object[]{SAA_EVENT_SEVERITY_ID,new BEROctetString(VALUE)});
	private final BERSequence saaEventClass = new BERSequence(SAA_EVENT_CLASS,new Object[]{SAA_EVENT_CLASS_ID,new BEROctetString(VALUE)});
	private final BERSequence saaEventName = new BERSequence(SAA_EVENT_NAME,new Object[]{SAA_EVENT_NAME_ID,new BEROctetString(VALUE)});
	private final BERSequence saaEventDescription = new BERSequence(SAA_EVENT_DESCRIPTION,new Object[]{SAA_EVENT_DESCRIPTION_ID,new BEROctetString(VALUE)});
	
	private final Object[] SAA_VARIABLES_SEQUENCE = {
		saaInstance,
		saaDate,
		saaTime,
		saaPlugin,
		saaEventNumber,
		saaEventSeverity,
		saaEventClass,
		saaEventName,
		saaEventDescription
	};
	
	private final BERSequence SAA_VARIABLES = new BERSequence("snmpSAAEvent",SAA_VARIABLES_SEQUENCE);
	
	private BERSequence message;
	
	public SAATrap(String name) {
		this.message=RFC1157.getMessage(name);
		Object ber=this.message.forName(RFC1157.TRAP_PDU_NAME);
		if(ber instanceof Tagged) {
			Object[] obj=(Object[]) ((Tagged) ber).getValue();
			obj[0]=SAA;
			obj[obj.length-1]=SAA_VARIABLES;
			((Tagged) ber).setValue(obj);
		}
	}
	
	public BERSequence getMessage() {return message;}
}
