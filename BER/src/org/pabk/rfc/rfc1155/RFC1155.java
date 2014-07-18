package org.pabk.rfc.rfc1155;

import org.pabk.ber.BERChoice;
import org.pabk.ber.BERInteger;
import org.pabk.ber.BERNull;
import org.pabk.ber.BEROctetString;
import org.pabk.ber.BEROid;
import org.pabk.ber.Tagged;

public final class RFC1155 {
		
	public static final BEROid INTERNET		= new BEROid("internet",new int[]{1,3,6,1});
	public static final BEROid DIRECTORY	= new BEROid("directory",new int[]{1,3,6,1,1});
	public static final BEROid MGMT			= new BEROid("mgmt",new int[]{1,3,6,1,2});
	public static final BEROid EXPERIMENTAL	= new BEROid("experimental",new int[]{1,3,6,1,3});
	public static final BEROid PRIVATE		= new BEROid("private",new int[]{1,3,6,1,4});
	public static final BEROid ENTERPRISES	= new BEROid("enterprises",new int[]{1,3,6,1,4,1});
	
	private static final Object[] SIMPLE_SYNTAX_CHOICE = {
		new BERInteger("number"),
		new BEROctetString("string"),
		new BEROid("object"),
		new BERNull("empty")
	};
	private static final Object[] NETWORK_ADDRESS_CHOICE = {
		new Tagged(null, new BEROctetString(null), 0, true, true)
	};
	private static final Object[] APPLICATION_SYNTAX_CHOICE = {
		new BERChoice("address", NETWORK_ADDRESS_CHOICE, 0),
		new Tagged("counter", new BERInteger(null), 1, true, true),
		new Tagged("gauge", new BERInteger(null), 2, true, true),
		new Tagged("ticks", new BERInteger(null), 3, true, true),
		new Tagged("arbitrary", new BEROctetString(null), 4, true, true)
	};
	
	private static final BERChoice SIMPLE_SYNTAX 			= new BERChoice(null, SIMPLE_SYNTAX_CHOICE, -1);
	private static final BERChoice APPLICATION_SYNTAX 	= new BERChoice(null, APPLICATION_SYNTAX_CHOICE, -1);
	
	private static final Object[] OBJECT_SYNTAX_CHOICE = {
		SIMPLE_SYNTAX,
		APPLICATION_SYNTAX
	};
	
	private static final BEROid OBJECT_NAME			= new BEROid(null);
	private static final BERChoice OBJECT_SYNTAX	= new BERChoice(null, OBJECT_SYNTAX_CHOICE, -1);
	private static final Tagged TIME_TICKS			= new Tagged(null, new BERInteger(null), 3, true, true);
	private static final BERChoice NETWORK_ADDRESS	= new BERChoice(null, NETWORK_ADDRESS_CHOICE, 0);
	
	private RFC1155(){};
	
	
	
	public static BERChoice getObjectSyntax(String name) {return (BERChoice) OBJECT_SYNTAX.copy(name);
		/*try {
			return OBJECT_SYNTAX.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;*/
	}

	public static BEROid getObjectName(String name) {return (BEROid) OBJECT_NAME.copy(name);
	/*	try {
			return OBJECT_NAME.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;*/
	}
	
	public static Tagged getTimeTicks(String name) {return (Tagged) TIME_TICKS.copy(name);
		/*try {
			return TIME_TICKS.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;*/
	}

	public static BERChoice getNetworkAddress(String name) {return (BERChoice) NETWORK_ADDRESS.copy(name);
		/*try {
			return NETWORK_ADDRESS.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;*/
	}
}
