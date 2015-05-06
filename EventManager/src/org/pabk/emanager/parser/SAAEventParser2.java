package org.pabk.emanager.parser;

import java.io.IOException;

import org.pabk.emanager.util.Base64Coder;
import org.pabk.emanager.util.Sys;


public class SAAEventParser2 {
	
	
	private static final String GENERIC_COLUMN_NAME = "FIELD";	
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
	
	@SuppressWarnings("unused")
	private static final String[][] FIELD_DEFINITION_10018= {
		new String[] {"1",	"Message Partner ",		",",	null,		SAAEventParser.MP},
		new String[] {"1",	"Session ",				" ",	"\\d{1,4}",	SAAEventParser.SE},
		new String[] {null,	"Filename : ",			"\n",	null,		SAAEventParser.FN},
		new String[] {null, "Printer :",			"\n",	null,		SAAEventParser.PR}
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
		/*
		 * 			  line	startSequence	  endSequ 	   reqExpPatt variableName		 							   trim replace 
		 */
		new String[] {"1",	"Queue ",		  ",",		   null,	  SAAEventParser.QU,							   "N", "\\s+", "_"},
		new String[] {null,	"Service:",		  "Requestor", null,	  SAAEventParser.SV,							   "Y", "\\s+", "!"},
		new String[] {null,	"Requestor DN: ", "Responder", null,	  SAAEventParser.CODER_PREFIX + SAAEventParser.RD, "Y", "\\s+",  "="},
		new String[] {null,	"Responder DN: ", "Transfer" , null,	  SAAEventParser.CODER_PREFIX + SAAEventParser.OD, "Y", "\\s+",  "="},
		new String[] {null,	"UUMID:",		  "Suffix",	   null,	  SAAEventParser.UU,							   "Y", "\\s+",  " "},
		new String[] {null,	"Suffix:",		  "Logical",   "\\d{6,}", SAAEventParser.SU,							   "Y", "\\s+",  " "},
		new String[] {null,	"Logical Name:",  "Size",	   null,	  SAAEventParser.LN,							   "Y", null,	null},
		new String[] {null, "Size:",		  null,		   "\\d+",	  SAAEventParser.SZ,							   "Y",	null,	null}
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
	
	@SuppressWarnings("unused")
	private static final String[][] FIELD_DEFINITION_10023= {
		new String[] {"1",	"Message Partner ",		",",	null,		SAAEventParser.MP},
		new String[] {"1",	"Session ",				" ",	"\\d{1,4}",	SAAEventParser.SE},
		new String[] {"2",	"accepted ",			" ",	"\\d{6}",	SAAEventParser.IA},
		new String[] {"2",	"rejected ",			null,	"\\d{6}",	SAAEventParser.IR},
		new String[] {"3",	"accepted ",			" ",	"\\d{6}",	SAAEventParser.OA},
		new String[] {"3",	"rejected ",			null,	"\\d{6}",	SAAEventParser.OR},
		new String[] {"4",	"bypassed ",			null,	"\\d{6}",	SAAEventParser.MB}
	};
		
	/* Event 2010
	 * 
	 * 	The queue %s is in overflow. (%d message instance(s), threshold is %d).
		Empty this queue as soon as possible.
	 *
	 * 0 - queue
	 */
	
	@SuppressWarnings("unused")
	private static final String[][] FIELD_DEFINITION_2010= {
		new String[] {"1",	"queue ",				" ",	null,		SAAEventParser.QE},
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
		/*
		 * 			  line	startSequence	  endSequ reqExpPatte variableName		 trim replace 
		 */
		new String[] {null,	"UMID",			  ",",	  null,		  SAAEventParser.UM, "Y", "\\s+", " "},
		new String[] {null,	"Suffix",		  ":",	  "\\d{6,}",  SAAEventParser.SU, "Y", null,	  null},
		new String[] {null,	"Session",		  ",",	  "\\d{1,4}", SAAEventParser.SE, "Y", null,	  null},
		new String[] {null,	"Reason code",	  "Nack", null,		  SAAEventParser.RC, "Y", null,	  null},
		new String[] {null,	"Nack received:", null,	  null,		  SAAEventParser.NA, "N", null,	  null}
	};
	
	/*
	 * Event 8026
	 * 
	 * Message UMID %s, Suffix %s: PDM received (Possible Duplicate Message). This is caused by an abort of a previous session.
	 * 
	 * 0 - UMID
	 * 1 - Suffix
	 */
	
	@SuppressWarnings("unused")
	private static final String[][] FIELD_DEFINITION_8026= {
		new String[] {"1",	"UMID ",				",",	"[IO][A-Z0-9]{11}[0-9]{3}.{1,16}",SAAEventParser.UM},
		new String[] {"1",	"Suffix ",				":",	"\\d{6}\\d{0,5}",	SAAEventParser.SU}
	};
	
	/*
	 * Event 8135
	 * 
	 * Message UMID %s, Suffix %s: PDE received (Possible Duplicate Emission).
	 * 
	 * 0 - UMID
	 * 1 - Suffix
	 */
	
	@SuppressWarnings("unused")
	private static final String[][] FIELD_DEFINITION_8135= {
		new String[] {"1",	"UMID ",				",",	"[IO][A-Z0-9]{11}[0-9]{3}.{1,16}",SAAEventParser.UM},
		new String[] {"1",	"Suffix ",				":",	"\\d{6}\\d{0,5}",	SAAEventParser.SU}
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
		/*
		 * 			  line	startSequence	  		  endSequenc reqExpPatte variableName									trim   replace 
		 */
		new String[] {null,	"Message Partner ",		  ",",		 null,		 SAAEventParser.MP,								  "Y", null,   null},
		new String[] {null,	"Session ",				  "-",		 "\\d{1,4}", SAAEventParser.SE,								  "Y", null,   null},
		new String[] {null,	"Sequence number      :", "UUMID",	 "\\d+",     SAAEventParser.SN,								  "Y", null,   null},
		new String[] {null,	"UUMID                :", "Suffix",	 null,	     SAAEventParser.UU,								  "Y", "\\s+", " "},
		new String[] {null,	"Suffix               :", "Target",	 "\\d{6,}",	 SAAEventParser.SU,								  "Y", null,   null},
		new String[] {null,	"Target routing point :", "Offset",	 null,	     SAAEventParser.TR,								  "Y", null,   null},
		new String[] {null,	"Offset in message    :", "Message", null,	     SAAEventParser.OM,								  "Y", null,   null},
		new String[] {null,	"Message info         :", null,		 null,	     SAAEventParser.CODER_PREFIX + SAAEventParser.MI, "N", null,   null},
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
		/*
		 * 			  line	startSequence	  		  endSequen reqExpPatte variableName	   trim replace 
		 */
		new String[] {null,	"Message Partner",		  ",",		null,		SAAEventParser.MP, "Y", null,	null},
		new String[] {null,	"Session",				  "-",		"\\d{1,4}",	SAAEventParser.SE, "Y", null,	null},
		new String[] {null,	"Sequence number      :", "UUMID",	"\\d+",		SAAEventParser.SN, "Y", null, 	null},
		new String[] {null,	"UUMID                :", "Suffix",	null,		SAAEventParser.UU, "Y", "\\s+", " "},
		new String[] {null,	"Suffix               :", "Reason",	"\\d{6,}",	SAAEventParser.SU, "Y", null,	null},
		new String[] {null,	"Target routing point :", null,		null,		SAAEventParser.TR, "Y", null,	null},
		new String[] {null,	"Reason               :", "Target", null,		SAAEventParser.RE, "Y", "\\s+", " "},
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
	
	@SuppressWarnings("unused")
	private static final String[][] FIELD_DEFINITION_10098= {
		new String[] {"1",	"Message Partner ",		",",	null,		SAAEventParser.MP},
		new String[] {"1",	"Session ",				" ",	"\\d{1,4}",	SAAEventParser.SE},
		new String[] {"2",	": ",					null,	null,		SAAEventParser.SD},
		new String[] {"3",	": ",					null,	null,		SAAEventParser.RE},
	};
	
	private static final String SEPARATOR = "\\:";
	@SuppressWarnings("unused")
	private static final Object CRLF = "\r\n";
	private static final String DATE_TIME_SEPARATOR = " ";
	private static final String DATE_SEAPARATOR = "\\/";
	private static final String FULL_DATE_PREFIX = "20";
	private static final String NEW_DATE_SAPARATOR = "-";

	public static String[][] parse(String AgentIPAdress, String AccessInstance, String[] msg) throws IOException {
		try {
			int eNumber=SAAEventParser2.getEventNumber(msg[0]);
			String eDesc=SAAEventParser2.getEventDescription(msg);
			String [] value={
				AgentIPAdress,
				SAAEventParser.NOT_EXECUTED,
				AccessInstance,
				SAAEventParser2.getDate(msg[5]),
				SAAEventParser2.getTime(msg[5]),
				SAAEventParser2.getPlugin(msg[2]),
				Integer.toString(eNumber),
				SAAEventParser2.getEventSeverity(msg[3]),
				SAAEventParser2.getEventClass(msg[4]),
				Base64Coder.encodeString(SAAEventParser2.getEventName(msg[1])),
				Base64Coder.encodeString(eDesc)
			};
			String[] fields=SAAEventParser2.parse(eNumber, eDesc);
			String[] header=new String[fields.length];
			for(int i=0;i<header.length;i++) {
				header[i]=SAAEventParser2.GENERIC_COLUMN_NAME+(i+1);
			}
			value=Sys.join(value, fields);
			header=Sys.join(SAAEventParser.SAA_TRAPS_COLUMN, header);
			return new String[][]{header,value};
		}
		catch (Exception e) {
			throw new IOException (e);
		}
	}
	
	private static String[] parse(int nr, String text) throws IOException {
		switch (nr) {
		//case 2010:return parseAdditionalFields(FIELD_DEFINITION_2010, text);
		case 8005:return parseAdditionalFields(FIELD_DEFINITION_8005, text);
		//case 8026:return parseAdditionalFields(FIELD_DEFINITION_8026, text);
		//case 8135:return parseAdditionalFields(FIELD_DEFINITION_8135, text);
		//case 10018:return parseAdditionalFields(FIELD_DEFINITION_10018, text);
		//case 10023:return parseAdditionalFields(FIELD_DEFINITION_10023, text);
		case 10050:return parseAdditionalFields(FIELD_DEFINITION_10050, text);
		//case 10098:return parseAdditionalFields(FIELD_DEFINITION_10098, text);
		case 10117:return parseAdditionalFields(FIELD_DEFINITION_10117, text);
		case 28116:return parseAdditionalFields(FIELD_DEFINITION_28116, text);
		default: return new String[]{}; 
		}
	}
	
	private static String[] parseAdditionalFields(String[][] def, String text) throws IOException {
		try {
			String[] value=new String[def.length];
			for(int i=0;i<def.length;i++) {
				value[i]="";
				byte[] b=def[i][0]==null?text.getBytes():SAAEventParser.findLine(text,def[i][0]);
				int s=SAAEventParser.findStart(def[i][1],b);
				if(s<0) continue;
				int e=SAAEventParser.findEnd(def[i][2],s,b);
				if(e<0) continue;
				String tmp=new String(b,s,e-s);
				if(def[i][5].equals("Y")) {
					tmp = tmp.trim();
				}
				if(def[i][6] != null) {
					tmp = tmp.replaceAll(def[i][6], def[i][7]);
				}
				if(def[i][3]!=null)if(!tmp.matches(def[i][3]))continue;
				value[i]=def[i][4].charAt(0)=='@'?Base64Coder.encodeString(tmp):tmp;
			}
			return value;
		}
		catch (Exception e) {
			throw new IOException(e);
		}
	}

	private static String getEventName(String line) throws IOException {
		return getString(line);
	}

	private static String getEventClass(String line) throws IOException {
		return getString(line);
	}

	private static String getEventSeverity(String line) throws IOException {
		return getString(line);
	}

	private static String getPlugin(String line) throws IOException {
		return getString(line);
	}

	private static String getString(String line) throws IOException {
		try {
			return line.split(SEPARATOR)[1].trim();
		}
		catch (Exception e) {
			throw new IOException (e);
		}
	}

	private static String getTime(String line) throws IOException {
		try {
			return line.split(SEPARATOR, 2)[1].trim().split(DATE_TIME_SEPARATOR)[1].trim();
		}
		catch (Exception e) {
			throw new IOException (e);
		}
	}

	private static String getDate(String line) throws IOException {
		try {
			String[] date = line.split(SEPARATOR)[1].trim().split(DATE_TIME_SEPARATOR)[0].trim().split(DATE_SEAPARATOR);
			return FULL_DATE_PREFIX + date[2] + NEW_DATE_SAPARATOR + date[1] + NEW_DATE_SAPARATOR + date[0];
		}
		catch (Exception e) {
			throw new IOException (e);
		}
	}

	private static String getEventDescription(String[] msg) {
		StringBuffer sb = new StringBuffer();
		for(int i = 8; i < msg.length; i++) {
			if(i > 8) {
				sb.append('\n');
			}
			sb.append(msg[i]);
		}
		return sb.toString();
	}

	private static int getEventNumber(String line) throws IOException {
		try {
			return Integer.parseInt(line.split(SEPARATOR)[1].trim());
		}
		catch (Exception e) {
			throw new IOException (e);
		}
	}
	
}
