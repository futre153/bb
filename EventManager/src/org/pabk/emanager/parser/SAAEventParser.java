package org.pabk.emanager.parser;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;

import org.pabk.ber.BERChoice;
import org.pabk.ber.BERInteger;
import org.pabk.ber.BEROctetString;
import org.pabk.ber.BERSequence;
import org.pabk.emanager.DBConnector;
import org.pabk.emanager.snmp.SAATrap;
import org.pabk.emanager.util.Base64Coder;
import org.pabk.emanager.util.Sys;

public class SAAEventParser {

	public static final String NOT_EXECUTED = "0";
	public static final String EXECUTED = "1";
	public static final String MARKED_FOR_DELETION = "2";
	
	private static final String GENERIC_COLUMN_NAME = "FIELD";
	public static final String[] SAA_TRAPS_COLUMN= {
		"SAA_IP_ADDRESS","STATUS","SAA_INSTANCE","SAA_DATE","SAA_TIME","SAA_PLUGIN","SAA_EVENT_NUMBER",
		"SAA_EVENT_SEVERITY","SAA_EVENT_CLASS","SAA_EVENT_NAME","SAA_EVENT_DESCRIPTION"
	};
	
	public static final int SVRI = 0;
	public static final int TSTI = 1;
	public static final int INSI = 2;
	public static final int DATI = 3;
	public static final int TIMI = 4;
	public static final int PLGI = 5;
	public static final int ENRI = 6;
	public static final int ESVI = 7;
	public static final int ECLI = 8;
	public static final int ENMI = 9;
	public static final int EDEI = 10;
	
	public static final int START_SESSION=10018;
	public static final int SESSION_CLOSED=10023;
	public static final int VALIDATION_ERROR_FROM=10117;
	public static final int NACKED = 8005;
	public static final int QUEUE_OVERFLOW = 2010;
	public static final int POSSIBLE_DUPLICATE_MESSAGE = 8026;
	public static final int POSSIBLE_DUPLICATE_EMISSION = 8135;
	public static final int DISPOSE_ERROR = 10050;
	public static final int SNF_FILE_RECEIVED = 28116;
	
	public static final String CODER_PREFIX="@";
	
	public static final String ID = "ID";
	public static final String MP = "MessagePartner";
	public static final String SE = "Session";
	public static final String FN = "Filename";
	public static final String PR = "Printer";
	public static final String IA = "InputMessagesAccepted";
	public static final String IR = "InputMessagesRejected";
	public static final String OA = "OutputMessagesAccepted";
	public static final String OR = "OutputMessagesRejected";
	public static final String MB = "MessagesBypassed";
	public static final String QE = "Queue";
	public static final String UM = "UMID"; 
	public static final String SU = "Suffix";
	public static final String RC = "ReasonCode";
	public static final String NA = "Nack";
	public static final String SN = "SequenceNumber";
	public static final String UU = "UUMID";
	public static final String TR = "TargetRoutingPoint";
	public static final String OM = "OffsetInMessage";
	public static final String MI = "MessageInfo";
	public static final String RE = "Reason";
	public static final String SD = "SessionDirection";
	public static final String ET = "EventType";
	public static final String QU = "Queue";
	public static final String SV = "Service";
	public static final String RD = "RequestorDN";
	public static final String OD = "ResponderDN";
	public static final String LN = "LogicalName";
	public static final String SZ = "Size";
	private static final String EVENT_TYPE_TRAP = "trap";
	
	private static final String FIELD_DEFINITION_PREFIX = "FIELD_DEFINITION_";
	
	/*
	 * 0 - line number 1 - start, 2 - end, 3-mask, 4 - name
	 */
	
	/* Event 10018
	 *  Message Partner %s, Session %u - Request to start session
			Session mode       : %s
			Session origin     : %s
			Session direction  : %s
			Session type       : %s
			Session parameters : %s
			Start sequence number: %u
			Operator           : %s
			Hostname           : %s
			Transfer PKI Signatures: %s
			Always transfer MAC/PAC: %s
			 
	 * 0 - Message partner
	 * 1 - Session
	 * 2 - Filename
	 */
	
	private static final String[][] FIELD_DEFINITION_10018= {
		new String[] {"1",	"Message Partner ",		",",	null,		MP},
		new String[] {"1",	"Session ",				" ",	"\\d{1,4}",	SE},
		new String[] {null,	"Filename : ",			"\n",	null,		FN},
		new String[] {null, "Printer :",			"\n",	null,		PR}
	};
	/* Event 28116
	 * Queue %s, Session %s - Store-and-forward file received.
	   Sequence number: %s
	   Service: %s
	   Requestor DN: %s
	   Responder DN: %s
	   %sUUMID: %s
	   Suffix: %s
	   Logical Name: %s
	   Size: %s 
	 *
	 * 0 - Queue
	 * 1 - Service
	 * 2 - Requestor DN
	 * 3 - Responder DN
	 * 4 - UUMID
	 * 5 - Suffix
	 * 6 - Logical Name
	 * 7 - Size
	 */
	
	private static final String[][] FIELD_DEFINITION_28116 = {
		new String[] {"1",	"Queue ",				",",	null,		QU},
		new String[] {"4",	"Service: ",			null,	null,		SV},
		new String[] {"5",	"Requestor DN: ",		null,	null,		CODER_PREFIX+RD},
		new String[] {"6",	"Responder DN: ",		null,	null,		CODER_PREFIX+OD},
		new String[] {"9",	"UUMID: ",				null,	"[IO][A-Z0-9]{11}[0-9]{3}.{1,30}",UU},
		new String[] {"10",	"Suffix: ",				null,	"\\d{6}\\d{0,5}",	SU},
		new String[] {"11",	"Logical Name: ",		null,	null,		LN},
		new String[] {"12", "Size: ",				null,	"\\d+",		SZ}
	};
	
	
	/* Event 10023
	 * Message Partner %s, Session %u - Session closed
           Input messages  : accepted %06u - rejected %06u
           Output messages : accepted %06u - rejected %06u
                                           - bypassed %06u
	 *
	 * 0 - Message partner
	 * 1 - Session
	 * 2 - Input messages accepted
	 * 3 - Input messages rejected
	 * 4 - Output messages accepted
	 * 5 - Output messages rejected
	 * 6 - Bypassed messages
	 */
	
	private static final String[][] FIELD_DEFINITION_10023= {
		new String[] {"1",	"Message Partner ",		",",	null,		MP},
		new String[] {"1",	"Session ",				" ",	"\\d{1,4}",	SE},
		new String[] {"2",	"accepted ",			" ",	"\\d{6}",	IA},
		new String[] {"2",	"rejected ",			null,	"\\d{6}",	IR},
		new String[] {"3",	"accepted ",			" ",	"\\d{6}",	OA},
		new String[] {"3",	"rejected ",			null,	"\\d{6}",	OR},
		new String[] {"4",	"bypassed ",			null,	"\\d{6}",	MB}
	};
		
	/* Event 2010
	 * 
	 * 	The queue %s is in overflow. (%d message instance(s), threshold is %d).
		Empty this queue as soon as possible.
	 *
	 * 0 - queue
	 */
	
	private static final String[][] FIELD_DEFINITION_2010= {
		new String[] {"1",	"queue ",				" ",	null,		QE},
	};
	
	/* Event 8005
	 * 
	 * Message UMID %s, Suffix %s: nacked by SWIFT. Session %d, ISN %d, Reason code %s
	   Nack received: %s
	 *
	 * 0 - UMID
	 * 1 - Suffix
	 * 2 - Session
	 * 3 - Reason code
	 * 4 - Nack
	 */
	
	private static final String[][] FIELD_DEFINITION_8005= {
		new String[] {"1",	"UMID ",				",",	null,UM},
		new String[] {"1",	"Suffix ",				":",	"\\d{6}\\d{0,5}",	SU},
		new String[] {"1",	"Session ",				",",	"\\d{1,4}",	SE},
		new String[] {"1",	"Reason code ",			null,	null,		RC},
		new String[] {null,	"Nack received: ",		null,	null,		NA}
	};
	
	/*
	 * Event 8026
	 * 
	 * Message UMID %s, Suffix %s: PDM received (Possible Duplicate Message). This is caused by an abort of a previous session.
	 * 
	 * 0 - UMID
	 * 1 - Suffix
	 */
	
	private static final String[][] FIELD_DEFINITION_8026= {
		new String[] {"1",	"UMID ",				",",	"[IO][A-Z0-9]{11}[0-9]{3}.{1,16}",UM},
		new String[] {"1",	"Suffix ",				":",	"\\d{6}\\d{0,5}",	SU}
	};
	
	/*
	 * Event 8135
	 * 
	 * Message UMID %s, Suffix %s: PDE received (Possible Duplicate Emission).
	 * 
	 * 0 - UMID
	 * 1 - Suffix
	 */
	
	private static final String[][] FIELD_DEFINITION_8135= {
		new String[] {"1",	"UMID ",				",",	"[IO][A-Z0-9]{11}[0-9]{3}.{1,16}",UM},
		new String[] {"1",	"Suffix ",				":",	"\\d{6}\\d{0,5}",	SU}
	};
	
	/*
	 * Event 10117
	 * 
	 * Message Partner %s, Session %u - Text validation error (From)
	   Sequence number      : %u
	   UUMID                : %s
	   Suffix               : %s
	   Target routing point : %s
	   Offset in message    : %d
	   Message info         : %s
	 * 
	 * 0 - Message Partner
	 * 1 - Session
	 * 2 - Sequence
	 * 3 - UUMID
	 * 4 - Suffix
	 * 5 - Target
	 * 6 - Offset
	 * 7 - Message Info
	 */
	
	private static final String[][] FIELD_DEFINITION_10117= {
		new String[] {"1",	"Message Partner ",		",",	null,		MP},
		new String[] {"1",	"Session ",				" ",	"\\d{1,4}",	SE},
		new String[] {"2",	": ",					null,	"\\d+",		SN},
		new String[] {"3",	": ",					null,	"[IO][A-Z0-9]{11}[0-9]{3}.{1,30}",UU},
		new String[] {"4",	": ",					null,	"\\d{6}\\d{0,5}",	SU},
		new String[] {"5",	": ",					null,	null,		TR},
		new String[] {"6",	": ",					null,	null,		OM},
		new String[] {null,	"Message info         : ",null,	null,		CODER_PREFIX+MI},
	};
	
	/*
	 * Event 10050
	 * 
	 * Message Partner %s, Session %u - Dispose error (From)
		Sequence number      : %u
		UUMID                : %s
		Suffix               : %s
		Reason               : %s
		Target routing point : %s
	 * 
	 * 0 - Message Partner
	 * 1 - Session
	 * 2 - Sequence
	 * 3 - UUMID
	 * 4 - Suffix
	 * 5 - Reason
	 * 6 - Target
	 * 
	 */
	
	private static final String[][] FIELD_DEFINITION_10050= {
		new String[] {"1",	"Message Partner ",		",",	null,		MP},
		new String[] {"1",	"Session ",				" ",	"\\d{1,4}",	SE},
		new String[] {"2",	": ",					null,	"\\d+",		SN},
		new String[] {"3",	": ",					null,	"[IO][A-Z0-9]{11}[0-9]{3}.{1,30}",UU},
		new String[] {"4",	": ",					null,	"\\d{6}\\d{0,5}",	SU},
		new String[] {"5",	": ",					null,	null,		TR},
		new String[] {"6",	": ",					null,	null,		RE},
	};
	
	/*
	 * Event 10098
	 * 
	 * Message Partner %s, Session %u - Session failed
		Session direction : %s
		Reason            : %s
       Try again.
	 * 
	 * 0 - Message Partner
	 * 1 - Session
	 * 2 - Session Direction
	 * 3 - Reason
	 */
	
	private static final String[][] FIELD_DEFINITION_10098= {
		new String[] {"1",	"Message Partner ",		",",	null,		MP},
		new String[] {"1",	"Session ",				" ",	"\\d{1,4}",	SE},
		new String[] {"2",	": ",					null,	null,		SD},
		new String[] {"3",	": ",					null,	null,		RE},
	};
	private static final String THIS_CLASSNAME = "org.pabk.emanager.parser.SAAEventParser";
	private static final String STATUS_1023_KEY = "Result";
	private static final String STATUS_1023_OK = "OK";
	private static final String STATUS_1023_FAIL = "!! CHYBA !! Niektoré správy neboli odoslané alebo boli preskoèené";

	
	private static String[] parse(int nr, String text) {
		
		switch (nr) {
		case 2010:return parseAdditionalFields(FIELD_DEFINITION_2010,text);
		case 8005:return parseAdditionalFields(FIELD_DEFINITION_8005,text);
		case 8026:return parseAdditionalFields(FIELD_DEFINITION_8026,text);
		case 8135:return parseAdditionalFields(FIELD_DEFINITION_8135,text);
		case 10018:return parseAdditionalFields(FIELD_DEFINITION_10018,text);
		case 10023:return parseAdditionalFields(FIELD_DEFINITION_10023,text);
		case 10050:return parseAdditionalFields(FIELD_DEFINITION_10050,text);
		case 10098:return parseAdditionalFields(FIELD_DEFINITION_10098,text);
		case 10117:return parseAdditionalFields(FIELD_DEFINITION_10117,text);
		case 28116:return parseAdditionalFields(FIELD_DEFINITION_28116,text);
		default: return new String[]{}; 
		}
		
		
	}

	private static String[] parseAdditionalFields(String[][] def, String text) {
		String[] value=new String[def.length];
		for(int i=0;i<def.length;i++) {
			value[i]="";
			byte[] b=def[i][0]==null?text.getBytes():findLine(text,def[i][0]);
			int s=findStart(def[i][1],b);
			if(s<0) continue;
			int e=findEnd(def[i][2],s,b);
			if(e<0) continue;
			String tmp=new String(b,s,e-s);
			if(def[i][3]!=null)if(!tmp.matches(def[i][3]))continue;
			value[i]=def[i][4].charAt(0)=='@'?Base64Coder.encodeString(tmp):tmp;
		}
		return value;
	}

	static int findEnd(String mask, int s, byte[] b) {
		if(mask==null)return b.length;
		byte[] a=mask.getBytes();
		for(int i=s;i<(b.length-a.length+1);i++) {
			int j=i;
			for(;j<(i+a.length);j++) {
				if(a[j-i]!=b[j])
					break;
			}
			if(j==(i+a.length))return j-a.length;
		}
		return -1;
	}

	static int findStart(String mask, byte[] b) {
		if(mask==null)return 0;
		byte[] a=mask.getBytes();
		for(int i=0;i<(b.length-a.length);i++) {
			int j=i;
			for(;j<(i+a.length);j++) {
				if(a[j-i]!=b[j])break;
			}
			if(j==(i+a.length))return i+a.length;
		}
		return -1;
	}

	static byte[] findLine(String text, String index) {
		try {
			return text.split("\\n")[Integer.parseInt(index)-1].getBytes();
		}
		catch (Exception e) {
			return text.getBytes();
		}
	}

	public static String getISODate(String saaDate) {
		try {
			String[] tmp=saaDate.split("/");
			return tmp[2]+'-'+tmp[1]+'-'+tmp[0];
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String[][] parse(BERSequence msg) {
		int eNumber=SAAEventParser.getEventNumber(msg);
		String eDesc=SAAEventParser.getEventDescription(msg);
		String [] value={
			SAAEventParser.getAgentAddress(msg),
			SAAEventParser.NOT_EXECUTED,
			SAAEventParser.getInstance(msg),
			SAAEventParser.getDate(msg),
			SAAEventParser.getTime(msg),
			SAAEventParser.getPlugin(msg),
			Integer.toString(eNumber),
			SAAEventParser.getEventSeverity(msg),
			SAAEventParser.getEventClass(msg),
			Base64Coder.encodeString(SAAEventParser.getEventName(msg)),
			Base64Coder.encodeString(eDesc)
		};
		String[] fields=SAAEventParser.parse(eNumber, eDesc);
		String[] header=new String[fields.length];
		for(int i=0;i<header.length;i++) {
			header[i]=SAAEventParser.GENERIC_COLUMN_NAME+(i+1);
		}
		value=Sys.join(value, fields);
		header=Sys.join(SAA_TRAPS_COLUMN, header);
		return new String[][]{header,value};
	}
	
	static String getAgentAddress(BERSequence msg) {
		byte[] b=(byte[])(((BERChoice)msg.forName("agent-addr")).getValue());
		return (b[0]&0xFF)+"."+(b[1]&0xFF)+"."+(b[2]&0xFF)+"."+(b[3]&0xFF);
	}
	
	static String getInstance(BERSequence msg) {
		return new String(getOctetStringValue(getSequence(msg,SAATrap.SAA_INSTANCE),SAATrap.VALUE));
	}
	static String getDate(BERSequence msg) {
		return getISODate(new String(getOctetStringValue(getSequence(msg,SAATrap.SAA_DATE),SAATrap.VALUE)));
	}
	static String getTime(BERSequence msg) {
		return new String(getOctetStringValue(getSequence(msg,SAATrap.SAA_TIME),SAATrap.VALUE));
	}
	static String getPlugin(BERSequence msg) {
		return new String(getOctetStringValue(getSequence(msg,SAATrap.SAA_INSTANCE),SAATrap.VALUE));
	}
	static int getEventNumber(BERSequence msg) {
		return (int)(getIntegerValue(getSequence(msg,SAATrap.SAA_EVENT_NUMBER),SAATrap.VALUE));
	}
	static String getEventSeverity(BERSequence msg) {
		return new String(getOctetStringValue(getSequence(msg,SAATrap.SAA_EVENT_SEVERITY),SAATrap.VALUE));
	}
	static String getEventClass(BERSequence msg) {
		return new String(getOctetStringValue(getSequence(msg,SAATrap.SAA_EVENT_CLASS),SAATrap.VALUE));
	}
	static String getEventName(BERSequence msg) {
		return new String(getOctetStringValue(getSequence(msg,SAATrap.SAA_EVENT_NAME),SAATrap.VALUE));
	}
	static String getEventDescription(BERSequence msg) {
		return new String(getOctetStringValue(getSequence(msg,SAATrap.SAA_EVENT_DESCRIPTION),SAATrap.VALUE));
	}
	
	
	
	private static byte[] getOctetStringValue(BERSequence ber, String name) {
		return (byte[])getOctetString(ber,name).getValue();
	}
	
	private static long getIntegerValue(BERSequence ber, String name) {
		return (long)getInteger(ber,name).getValue();
	}
	
	private static BERSequence getSequence(BERSequence ber, String name) {
		return (BERSequence) ber.forName(name);
	}
	
	private static BEROctetString getOctetString (BERSequence ber, String name) {
		return (BEROctetString) ber.forName(name);
	}
	
	private static BERInteger getInteger (BERSequence ber, String name) {
		return (BERInteger) ber.forName(name);
	}

	private static String[] getEventFieldNames() {
		return Sys.join(Sys.join(new String[]{ID},SAA_TRAPS_COLUMN), new String[]{
			GENERIC_COLUMN_NAME+"1",
			GENERIC_COLUMN_NAME+"2",
			GENERIC_COLUMN_NAME+"3",
			GENERIC_COLUMN_NAME+"4",
			GENERIC_COLUMN_NAME+"5",
			GENERIC_COLUMN_NAME+"6",
			GENERIC_COLUMN_NAME+"7",
			GENERIC_COLUMN_NAME+"8",
		});
		
	}
	
	public static ArrayList<Hashtable<String, Object>> getTrapsFromDB(String tableName, Object locker, String where) {
		String[] cols=getEventFieldNames();
		ArrayList<Hashtable<String, Object>> obj=new ArrayList<Hashtable<String,Object>>();
		DBConnector db=null;
		try {
			db=DBConnector.getDb(true, locker);
			db.select(tableName, null, where);
			while(true) {
				try {
					Object[] res=null;
					try {
						db.next();
						res=db.getRow(cols);
					}
					catch(Exception e) {break;}
					obj.add(getFields(res));
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			db.close();
		}
		catch (SQLException e) {
			if(db!=null)db.close();
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return obj;
	}
	
	public static ArrayList<Hashtable<String, Object>> getTrapsFromDB(String server, String tableName, Object locker) {
		/*String[] cols=getEventFieldNames();
		ArrayList<Hashtable<String, Object>> obj=new ArrayList<Hashtable<String,Object>>();
		try {
			DBConnector db=DBConnector.getDb(true, locker);
			db.select(
				tableName,
				null,
				"where "+SAA_TRAPS_COLUMN[SVRI]+" = '"+server+"' and "+SAA_TRAPS_COLUMN[TSTI]+" ='"+NOT_EXECUTED+"'");
			while(true) {
				try {
					Object[] res=null;
					try {
						db.next();
						res=db.getRow(cols);
					}
					catch(Exception e) {break;}
					obj.add(getFields(res));
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			} 
			db.close();
		}
		catch(Exception e) {
			//e.printStackTrace();
		}
		return obj;
		*/
		return SAAEventParser.getTrapsFromDB(tableName, locker, "where "+SAA_TRAPS_COLUMN[SVRI]+" = '"+server+"' and "+SAA_TRAPS_COLUMN[TSTI]+" ='"+NOT_EXECUTED+"'");
	}
	
	public static final String getFieldName(int index) {
		return SAAEventParser.SAA_TRAPS_COLUMN[index];
	}
	
	private static Hashtable<String, Object> getFields(Object[] res) {
		Hashtable<String, Object> tab=new Hashtable<String, Object>();
		tab.put(ID, res[0]);
		tab.put(ET, EVENT_TYPE_TRAP);
		for(int i=0;i<SAAEventParser.SAA_TRAPS_COLUMN.length;i++) {
			tab.put(SAA_TRAPS_COLUMN[i], res[i+1]);
		}
		try {
			Field fl=Class.forName(THIS_CLASSNAME).getDeclaredField(FIELD_DEFINITION_PREFIX+res[ENRI+1]);
			String[][] flDef=(String[][]) fl.get(null);
			for(int i=0;i<flDef.length;i++) {
				//System.out.println(flDef[i][4]+"="+res[i+SAA_TRAPS_COLUMN.length+1]);
				tab.put(flDef[i][4], res[i+SAA_TRAPS_COLUMN.length+1]);
			}
		}
		catch (NoSuchFieldException | SecurityException | ClassNotFoundException | IllegalArgumentException | IllegalAccessException e) {
			//e.printStackTrace();
		}
		return tab;
	}

	public static void set1023Status(Hashtable<String, Object> join) {
		int in=Integer.parseInt((String) join.get(IR));
		int mb=Integer.parseInt((String) join.get(MB));
		join.put(STATUS_1023_KEY, (in==0 && mb==0)?STATUS_1023_OK:STATUS_1023_FAIL);			
	}

	public static long getDateFromISODate(String date) {
		GregorianCalendar c = new GregorianCalendar();
		String[] s=date.split("-");
		c.set(Integer.parseInt(s[0]), Integer.parseInt(s[1])-1, Integer.parseInt(s[2]),0,0,0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTimeInMillis();
	}

	

}
