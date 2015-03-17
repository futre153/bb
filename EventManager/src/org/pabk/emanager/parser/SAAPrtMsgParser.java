package org.pabk.emanager.parser;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.pabk.emanager.DBConnector;
import org.pabk.emanager.routing.Distribution;
import org.pabk.emanager.util.Base64Coder;
import org.pabk.emanager.util.Sys;

public class SAAPrtMsgParser extends ArrayList<Object> {

	private static final long serialVersionUID = 1L;

	private static final int MAX_READED_BYTES = 2048;
	private static final byte[] BUFFER=new byte[MAX_READED_BYTES];
	
	private static final byte HT = 0x09;
	private static final byte LF = 0x0A;
	private static final byte CR = 0x0D;
	private static final byte FF = 0x0C; 
	
	private static final byte[] LINE_TERMINATORS = new byte[]{LF,CR};
	
	private static final byte[] MS_NEWLINE=new byte[]{CR,LF};
	private static final byte[] UNIX_NEWLINE=new byte[]{LF};
	
	private static final byte[][] SUPPORTED_NEWLINES = {
		MS_NEWLINE,
		UNIX_NEWLINE
	};

	private static final String MESSAGE_SEPARATOR = new String(new byte[]{HT,HT});
	private static final String MESSAGE_SEPARATOR2 = new String(new byte[]{HT,FF});
		
	private static int pointer=0;
	private static int length=0;
	private static boolean eof=false;
	private static final SAAPrtMsgParser messages=new SAAPrtMsgParser();
	//private static final SAAPrtMsgParser statements=new SAAPrtMsgParser();
	
	//private static final String[][] FIN_103_I
	
	
	private static final String MESSAGE_HEADER_MASK = "_+";

	private static final String[] INSTANCE_TYPE_AND_TRANSMISSION = {
		"\t-+ +Instance Type and Transmission +-+\t",
		"\t-+ +Message Header +-+\t"
	};

	private static final String[] MESSAGE_HEADER = {
		"\t-+ +Message Header +-+\t",
		"\t-+ +Message Text +-+\t"
	};

	private static final String[] MESSAGE_TEXT = {
		"\t-+ +Message Text +-+\t",
		"\t-+ +Message Trailer +-+\t"
	};

	private static final String[] MESSAGE_TRAILER = {
		"\t-+ +Message Trailer +-+\t",
		"\t-+ +Interventions +-+\t"
	};

	private static final String[] INTERVENTIONS = {
		"\t-+ +Interventions +-+\t",
		null
	};
	
	private static final String ALLM="all";
	private static final String INST="0";
	private static final String MSGH="1";
	
	private static final String IO="IO";
	private static final String MT="MT";
	private static final String MI="MessageInputReference";
	private static final String DS="DeliveryStatus";
	private static final String RF="Reference";
	private static final String VL="InterbankSettledAmount";
	private static final String TI="SendingTime";
	private static final String MO="MessageOutputReference";
	private static final String MU="MessageUserReference";
	private static final String TO="ReceptionTime";
	private static final String SE="Sender";
	private static final String RE="Receiver";
	private static final String AC="AbortCode";
	private static final String VC="VASCode";
	private static final String AN="AttName";
	private static final String SM="NumberMT";
	private static final String MS="MessageStatus";
	private static final String AI="AccountIdentification";
	private static final String SS="StatementSequence";
	private static final String OB="OpeningBalanceDate";
	private static final String SC="ServiceCode";
	private static final String SA="SWIFTAddress";
	private static final String IS="InformationForSender";
	private static final String CB="ClosingBalanceDate";
	private static final String NS="NrOfSequences";
	private static final String SN="StatementNr";
	private static final String AL="All";
	private static final String NO="No";
	private static final String YE="Yes";
	private static final String AD="ActualDate";
	private static final String LI="List";
		
	public static final String ET = "EventType";
	public static final String EVENT_TYPE_MSG = "msg";
	
	private static final String[] FIELD20=new String[]{null, null,	"20",	null,	".{1,16}",	RF};
	private static final String[] mi=new String[]{INST,	"4",	"Message Input Reference   : ",	null,	"\\d{4} \\d{6}[A-Z]{7}[A-Z0-9][A-Z][A-Z0-9]{3}\\d{10}",	MI};
	private static final String[] ds=new String[]{INST,	"2",	"Delivery Status      : ",		null,	".+",				DS};
	
	private static final String[][] MSG_ALL = {
		new String[]{MSGH,	"1",	"Swift ",	" ",	"(Output)|(Input)",	IO},
		new String[]{MSGH,	"1",	": ",		null,	".+",						MT},
		new String[]{MSGH,	"2",	": ",		" ",	"[A-Z]{6}[0-9A-Z]{2}([0-9A-Z]{3})?",	SE},
		new String[]{MSGH,	null,	"Receiver : ",System.getProperty("line.separator")+"| ",	"[A-Z]{6}[0-9A-Z]{2}([0-9A-Z]{3})?",	RE}
	};
	
	private static final String[][] MSG_010_O = {
		new String[]{null,	null,	"106",	null,	null,	MI},
		new String[]{null,	null,	"108",	null,	null,	MU},
		new String[]{null,	null,	"431",	null,	null,	MS},
	};
	
	private static final String[][] MSG_011_O = {
		new String[]{null,	null,	"175",	null,	null,	TI},
		new String[]{null,	null,	"106",	null,	null,	MI},
		new String[]{null,	null,	"108",	null,	null,	MU},
		new String[]{null,	null,	"175,2",null,	null,	TO},
		new String[]{null,	null,	"107",	null,	null,	MO}
	};
	
	private static final String[][] MSG_012_O = {
		new String[]{null,	null,	"106",	null,	null,	MI},
		new String[]{null,	null,	"108",	null,	null,	MU},
		new String[]{null,	null,	"102",	null,	null,	SA},
		new String[]{null,	null,	"103",	null,	null,	SC},
		new String[]{null,	null,	"114",	null,	null,	IS}
	};
	
	
	private static final String[][] MSG_019_O = {
		new String[]{null,	null,	"175",	null,	null,	TI},
		new String[]{null,	null,	"106",	null,	null,	MI},
		new String[]{null,	null,	"108",	null,	null,	MU},
		new String[]{null,	null,	"432",	null,	null,	AC},
		new String[]{null,	null,	"619",	null,	null,	VC}
	};
		
	private static final String[][] MSG_103_I = {
		mi,
		ds,
		FIELD20,
		new String[]{null,	null,	"32A",	null,	null,		"@"+VL}
	};
	
	private static final String[][] MSG_202_I = MSG_103_I;
	
	private static final String[][] MSG_544_O = {
		FIELD20
	};
	
	private static final String[][] MSG_535_O = MSG_544_O;
	
	private static final String[][] MSG_599_O = {
		FIELD20
	};
	
	private static final String[][] MSG_940_O = {
		FIELD20,
		new String[]{null,	null,	"25",	null,	".{1,35}",		AI},
		new String[]{null,	null,	"28C",	null,	"(\\d{1,5}(\\/\\d{1,5}))|(\\d{1,5})",	SS},
		new String[]{null,	null,	"60F",	null,	null,			OB},
		new String[]{null,  null,   "62F",  null,   null,			CB}
	};
	
	private static final String[][] MSG_999_I = {
		mi,
		ds,
		FIELD20,
	};
	
	private static final String[][] MSG_950_O = MSG_940_O;
		
	public static final String ID="ID";
	private static final String SOURCE="SOURCE";
	public static final String STATUS="STATUS";
	public static final String MSG_TEXT="MSG_TEXT";
	public static final String INSERT_TIME="INSERT_TIME";
	private static final String SAA_SERVER="SAA_SERVER";
		
	private static final String GENERIC_COLUMN_NAME = "FIELD";
	
	private static final String[] PRINT_MSG_COLUMN= {
		SOURCE,STATUS,MSG_TEXT,INSERT_TIME,SAA_SERVER
	};

	public static final String NOT_EXECUTED = "0";
	public static final String EXECUTED = "1";
	public static final String STATEMENT = "2";
	
	public static final String PRT_MSG_RECEIVED = "Output";
	public static final String PRT_MSG_SENT = "Input";

	private static final String THIS_CLASSNAME = "org.pabk.emanager.parser.SAAPrtMsgParser";

	private static final String FIELD_DEFINITION_PREFIX = "MSG_";

	private static final String FIELD_DEFINITION_SEPARATOR = "_";

	private static final String ATT_NAME_PREFIX = "MT";

	private static final String RTF_EXTENSION = ".rtf";

	private static final String END_MARK_DELIMITER = "\\|";

	private static final String UNKNOWN_PRINT_FORMAT = "Unknown print format";

	private static final String HEADER_SPLIT = "\\t";

	private static final String HEADER_DATETIME_MASK = "\\d\\d\\/\\d\\d/\\d\\d\\-\\d\\d:\\d\\d:\\d\\d";
	private static final String HEADER_NETWORK_PARTNER_MASK = "[0-9A-Za-z]+\\-\\d{4}\\-\\d{6}";
	private static final String HEADER_PAGES_MASK = "\\d+";
	
	

	//private static String[] header;

	private SAAPrtMsgParser(){
		Arrays.sort(LINE_TERMINATORS);
	}
	
	public static synchronized void loadMessages(Logger l, InputStream in) throws IOException {
		messages.addMessages(l, in, true);
	}
	
	private void checkPrintFormat(ArrayList<String> tmp) throws IOException {
		if(tmp.size()==0) {
			throw new IOException(UNKNOWN_PRINT_FORMAT+" zero header");
		}
		int i=0;
		for(;i<(tmp.size()-1);i++) {
			if(tmp.get(i).length()!=0) {
				throw new IOException(UNKNOWN_PRINT_FORMAT+" first lines must be emty");
			}
		}
		if(i!=(tmp.size()-1)){
			throw new IOException(UNKNOWN_PRINT_FORMAT+ " only end line is not empty");
		}
		String[] a=tmp.get(i).split(HEADER_SPLIT);
		if(a.length!=3){
			throw new IOException(UNKNOWN_PRINT_FORMAT+" check failed");
		}
		if(!a[0].trim().matches(HEADER_DATETIME_MASK)){
			throw new IOException(UNKNOWN_PRINT_FORMAT+" datetime check failed");
		}
		if(!a[1].trim().matches(HEADER_NETWORK_PARTNER_MASK)){
			throw new IOException(UNKNOWN_PRINT_FORMAT+" MP check failed");
		}
		if(!a[2].trim().matches(HEADER_PAGES_MASK)){
			throw new IOException(UNKNOWN_PRINT_FORMAT+" page number check failed");
		}
	}
	
	private synchronized void addMessages(Logger l, InputStream in, boolean check) throws IOException {
		//FileInputStream in = new FileInputStream(f);
		String line=null;
		ArrayList<String> tmp=new ArrayList<String>();
		pointer=0;
		length=0;
		eof=false;
		if(check) {
			boolean checked=false;
			while((line=readLine(in))!=null) {
				//System.out.println("'"+line+"'");
				if(line.matches(MESSAGE_HEADER_MASK)) {
					checkPrintFormat(tmp);
					checked=true;
					tmp.clear();
					break;
				}
				else {
					tmp.add(line);
				}
			}
			//System.exit(1);
			if(!checked) throw new IOException(UNKNOWN_PRINT_FORMAT);
		}
		while((line=readLine(in))!=null) {
		//	System.out.println("'"+line+"'");
			if(line.equals(MESSAGE_SEPARATOR) || line.equals(MESSAGE_SEPARATOR2)) {
				add(loadMessage(tmp));
			}
			else {
				tmp.add(line);
			}
		}
		if(tmp.size()>0)add(loadMessage(tmp));
		in.close();
	}
			
	private static String[] loadMessage(ArrayList<String> tmp) {
		String[] array=new String[tmp.size()];
		tmp.toArray(array);
		tmp.clear();
		return array;
	}

	private static String readLine(InputStream in) throws IOException {
		String line=null;
		loadBuffer(in);
		if(pointer!=length) {
			int index=pointer;
			int i=-1;
			while((i=findBytes(LINE_TERMINATORS,index))>=0) {
				int len=checkNewLine(SUPPORTED_NEWLINES,i);
				if(len>0) {
					line=new String(BUFFER,pointer,i-pointer);
					pointer=i+len;
					return line;
				}
				index=i+1;
				if(index>length) break;
			}
			line=new String(BUFFER,pointer,length-pointer);
			pointer=length;
			String tmp=readLine(in);
			line+=(tmp==null?"":tmp);
		}
		return line;
	}

	private static int checkNewLine(byte[][] nl, int index) {
		for(int i=0;i<nl.length;i++) {
			int j=0;
			for(;j<nl[i].length;j++) {
				if(BUFFER[index+j]!=nl[i][j])break;
			}
			if(j==nl[i].length)return nl[i].length;
		}
		return 0;
	}

	private static int findBytes(byte[] bs, int index) {
		for(int i=index;i<length;i++) {
			if(Arrays.binarySearch(bs, BUFFER[i])>0)return i;
		}
		return -1;
	}

	private static void loadBuffer(InputStream in) throws IOException {
		if(eof)return;
		if(length==MAX_READED_BYTES && pointer>0)truncateBuffer(in);
		int i=in.read(BUFFER, length, MAX_READED_BYTES-length);
		if(i<0)eof=true;else length+=i;
	}

	private static void truncateBuffer(InputStream in) {
		System.arraycopy(BUFFER, pointer, BUFFER, 0, length-pointer);
		length-=pointer;
		pointer=0;
	}
	
	public static Object next() {
		return messages.remove(0);
	}
	public static boolean hasMoreElements(){return messages.size()>0;}
	
	private static String[] parseAdditionalFields(String[][] def, String[] msg, int[][] block, ArrayList<PrtBodyItem> body) {
		String[] value=new String[def.length];
		for(int i=0;i<def.length;i++) {
			value[i]="";
			byte[] b=null;
			String val=null;
			
			String[] tmp=null;
			
			if(def[i][0]!=null) {
				if(def[i][0].equals(ALLM)) {
					b=Sys.concatenate(msg, System.getProperty("line.separator")).getBytes();
				}
				else {
					if(def[i][1]==null) {
						b=getBlockText(msg,block,Integer.parseInt(def[i][0])).getBytes();
					}
					else {
						b=msg[block[Integer.parseInt(def[i][0])][0]+Integer.parseInt(def[i][1])-1].getBytes();
					}
				}
				int s=SAAEventParser.findStart(def[i][2],b);
				if(s<0) continue;
				if(def[i][3]==null) {
					int e=SAAEventParser.findEnd(def[i][3],s,b);
					if(e<0) continue;
					tmp=new String[]{new String(b,s,e-s)};
				}
				else {
					String[] endMask=def[i][3].split(END_MARK_DELIMITER);
					tmp=new String[endMask.length];
					for(int j=0;j<endMask.length;j++) {
						int e=SAAEventParser.findEnd(endMask[j],s,b);
						if(e<0)continue;
						tmp[j]=new String(b,s,e-s);
					}
				}
			}
			else {
				tmp=new String[]{PrtBodyItem.getValue(body,def[i][2])};
			}
			//System.out.println("'"+tmp+"',"+def[i][4]+":"+(tmp.matches(def[i][4])));
			for(int j=0;j<tmp.length;j++) {
				if(tmp[j]==null)continue;
				if(def[i][4]!=null) if(!tmp[j].matches(def[i][4]))continue;
				val=tmp[j];
				break;
			}
			if(val==null)continue;
			value[i]=def[i][5].charAt(0)=='@'?Base64Coder.encodeString(val):val;
		}
		return value;
	}
	
	
	private static String getBlockText(String[] msg, int[][] block, int index) {
		String[] array=new String[block[index][1]-block[index][0]+1];
		System.arraycopy(msg, block[index][0], array, 0, array.length);
		return Sys.concatenate(array, System.getProperty("line.separator"));
	}

	public static String[][] parsePrintMessage(String[] msg, String srcDir, String server) throws IOException {
		
		int[][] block={
			getBlok(msg, INSTANCE_TYPE_AND_TRANSMISSION),
			getBlok(msg, MESSAGE_HEADER),
			getBlok(msg, MESSAGE_TEXT),
			getBlok(msg, MESSAGE_TRAILER),
			getBlok(msg, INTERVENTIONS)
		};
		String[] bd=new String[block[2][1]-block[2][0]+1];
		System.arraycopy(msg, block[2][0], bd, 0, bd.length);
		ArrayList<PrtBodyItem> body=PrtBodyItem.parse(bd);
		
		String[] value=new String[SAAPrtMsgParser.PRINT_MSG_COLUMN.length];
		value[0]=srcDir;
		value[1]=NOT_EXECUTED;
		value[2]=Sys.createTmpFile(Sys.concatenate(msg, System.getProperty("line.separator")).getBytes());
		value[3]=new Timestamp(Calendar.getInstance().getTimeInMillis()).toString();
		value[4]=server;
		String[] addValue=parseAdditionalFields(MSG_ALL, msg, block, body);
		String[] addValue2=null;
		try {
			int i=addValue[0].equals(PRT_MSG_RECEIVED)?1000:0;
			switch(i+Integer.parseInt(Sys.getShortMT(addValue[1]))) {
			case 103:addValue2=parseAdditionalFields(MSG_103_I, msg, block, body);break;
			case 202:addValue2=parseAdditionalFields(MSG_202_I, msg, block, body);break;
			case 999:addValue2=parseAdditionalFields(MSG_999_I, msg, block, body);break;
			case 1010:addValue2=parseAdditionalFields(MSG_010_O, msg, block, body);break;
			case 1011:addValue2=parseAdditionalFields(MSG_011_O, msg, block, body);break;
			case 1012:addValue2=parseAdditionalFields(MSG_012_O, msg, block, body);break;
			case 1019:addValue2=parseAdditionalFields(MSG_019_O, msg, block, body);break;
			case 1544:addValue2=parseAdditionalFields(MSG_544_O, msg, block, body);break;
			case 1535:addValue2=parseAdditionalFields(MSG_535_O, msg, block, body);break;
			case 1599:addValue2=parseAdditionalFields(MSG_599_O, msg, block, body);break;
			case 1940:value[1]=STATEMENT;addValue2=parseAdditionalFields(MSG_940_O, msg, block, body);break;
			case 1950:value[1]=STATEMENT;addValue2=parseAdditionalFields(MSG_950_O, msg, block, body);break;
			default: addValue2=new String[]{};
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new IOException (e.getMessage());
		}
		addValue=Sys.join(addValue, addValue2);
		String[] header=new String[addValue.length];
		for(int i=0;i<addValue.length;i++) {
			header[i]=SAAPrtMsgParser.GENERIC_COLUMN_NAME+(i+1);
		}
		return new String[][]{Sys.join(value, addValue),Sys.join(PRINT_MSG_COLUMN, header)};
	}

	private static int[] getBlok(String[] msg,String[] a) {
		int i=0;
		int[] tmp=new int[2];
		if(a[0]!=null) {
			for(;i<msg.length;i++) {
				if(msg[i].matches(a[0]))break;
			}
			tmp[0]=i+1;
		}
		else {
			tmp[0]=i;
		}
		tmp[1]=tmp[0]-1;
		if(a[1]!=null) {
			for(;i<msg.length;i++) {
				if(msg[i].matches(a[1]))break;
			}
			tmp[1]=i-1;
		}
		else {
			tmp[1]=msg.length-1;
		}
		return tmp;
	}

	public static ArrayList<Hashtable<String, Object>> getMsgFromDB(String server, String tableName, Object locker, String reqStatus) {
		
		String[] cols=getMsgFieldNames();
		ArrayList<Hashtable<String, Object>> obj=new ArrayList<Hashtable<String,Object>>();
		try {
			DBConnector db=DBConnector.getDb(true, locker);
			db.select(
				tableName,
				null,
				"where "+SAAPrtMsgParser.SAA_SERVER+" = '"+server+"' and "+SAAPrtMsgParser.STATUS+" ='"+reqStatus+"'");
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
			
		}
		return obj;
	}
		
	private static Hashtable<String, Object> getFields(Object[] res) {
		Hashtable<String, Object> tab=new Hashtable<String, Object>();
		int i=0;
		tab.put(ID, res[i]);i++;
		for(int j=0;j<SAAPrtMsgParser.PRINT_MSG_COLUMN.length;j++) {
			tab.put(PRINT_MSG_COLUMN[j], res[i]);i++;
		}
		
		String[][] flDef=MSG_ALL;
		for(int j=0;j<flDef.length;j++) {
			//System.out.println(flDef[j][5]+"="+res[i]);
			tab.put(flDef[j][5], res[i]);i++;
		}
		
		try {
			Field fl=Class.forName(THIS_CLASSNAME).getDeclaredField(FIELD_DEFINITION_PREFIX+Sys.getShortMT((String) tab.get(SAAPrtMsgParser.MT))+FIELD_DEFINITION_SEPARATOR+((String)tab.get(SAAPrtMsgParser.IO)).substring(0,1));
			flDef=(String[][]) fl.get(null);
			for(int j=0;j<flDef.length;j++) {
				//System.out.println(flDef[j][5]+"="+res[i]);
				tab.put(flDef[j][5], res[i]);i++;
			}
		}
		catch (NoSuchFieldException | SecurityException | ClassNotFoundException | IllegalArgumentException | IllegalAccessException e) {
			//e.printStackTrace();
		}
		String attName=ATT_NAME_PREFIX+Sys.getShortMT((String) tab.get(MT))+FIELD_DEFINITION_SEPARATOR;
		attName+=(Sys.getShortDirection((String) tab.get(IO))+FIELD_DEFINITION_SEPARATOR);
		attName+=((tab.get(IO).equals(PRT_MSG_RECEIVED)?(tab.get(SE)):tab.get(RE))+FIELD_DEFINITION_SEPARATOR);
		attName+=(tab.get(RF)==null?"":(RF.substring(0,3).toLowerCase()+FIELD_DEFINITION_SEPARATOR+Sys.ignoreInvalidFilenameChars((String) tab.get(RF),FIELD_DEFINITION_SEPARATOR.toCharArray()[0])));
		attName+=RTF_EXTENSION;
		tab.put(AN, attName);
		//System.out.println(tab);
		return tab;
	}

	private static String[] getMsgFieldNames() {
		return Sys.join(Sys.join(new String[]{ID},SAAPrtMsgParser.PRINT_MSG_COLUMN), new String[]{
			GENERIC_COLUMN_NAME+"1",
			GENERIC_COLUMN_NAME+"2",
			GENERIC_COLUMN_NAME+"3",
			GENERIC_COLUMN_NAME+"4",
			GENERIC_COLUMN_NAME+"5",
			GENERIC_COLUMN_NAME+"6",
			GENERIC_COLUMN_NAME+"7",
			GENERIC_COLUMN_NAME+"8",
			GENERIC_COLUMN_NAME+"9"
		});

	}
	
	private static String getFullMIR (Hashtable<String, Object> tab) {
		switch(Sys.getNrMT((String) tab.get(MT))) {
		case -1:return null;
		case 10 : return (String) tab.get(MI);
		case 11 : return (String) tab.get(MI);
		case 12 : return (String) tab.get(MI);
		case 19 : return (String) tab.get(MI);
		default:
			String mi=(String) tab.get(MI);
			if(mi==null)return mi;
			return ((String) tab.get(MI)).split(" ")[1].trim();
		}
	}
	
	public static Hashtable<String, Object> findTwinsMessage(ArrayList<Hashtable<String, Object>> list,
			Hashtable<String, Object> tab) {
		String mask=getFullMIR(tab);
		int mt=Sys.getNrMT((String) tab.get(MT));
		if(mt==10||mt==11||mt==12||mt==19) {
			for(int i=0;i<list.size();i++) {
				if(list.get(i).equals(tab))continue;
				try {
					if(mask.equals(getFullMIR(list.get(i)))) return list.get(i);
				}
				catch(NullPointerException e) {
					//System.out.println(list.size());
					//System.out.println(list.get(i));
					//System.out.println(mask);
					//System.out.println(tab);
					//System.exit(1);
				}
			}
		}
		else {
			return null;
		}
		return new Hashtable<String,Object>();
	}




	public static int length() {return messages.size();}





	public static void setShortMT(Hashtable<String, Object> tab) {
		tab.put(SM, Integer.toString(Sys.getNrMT((String) tab.get(MT))));
		
	}
	
	static final String TRN="TRN";
	static final String REL_TRN="REL_TRN";
	static final String ACC_ID="ACC_ID";
	static final String ST_NR="ST_NR";
	static final String SQ_NR="SQ_NR";
	static final String FREE_TXT="FREE_TXT";
	static final String OB_DC_MARK="OB_DC_MARK";
	static final String OB_DATE="OB_DATE";
	static final String OB_CURRENCY="OB_CURRENCY";
	static final String OB_AMOUNT="OB_AMOUNT";
	static final String SL_VALUE_DATE = "SL_VALUE_DATE";
	static final String SL_ENTRY_DATE = "SL_ENTRY_DATE";
	static final String SL_DC_MARK = "SL_DC_MARK";
	static final String SL_FUNDS_CODE = "SL_FUNDS_CODE";
	static final String SL_AMOUNT = "SL_AMOUNT";
	static final String SL_IC = "SL_IC";
	static final String SL_TT = "SL_TT";
	static final String SL_REF_AO = "SL_REF_AO";
	static final String SL_REF_ASI = "SL_REF_ASI";
	static final String SL_DETAILS = "SL_DETAILS";
	static final String CB_DC_MARK="CB_DC_MARK";
	static final String CB_DATE="CB_DATE";
	static final String CB_CURRENCY="CB_CURRENCY";
	static final String CB_AMOUNT="CB_AMOUNT";
	static final String CAB_DC_MARK="CAB_DC_MARK";
	static final String CAB_DATE="CAB_DATE";
	static final String CAB_CURRENCY="CAB_CURRENCY";
	static final String CAB_AMOUNT="CAB_AMOUNT";
	static final String FAB_DC_MARK="FAB_DC_MARK";
	static final String FAB_DATE="FAB_DATE";
	static final String FAB_CURRENCY="FAB_CURRENCY";
	static final String FAB_AMOUNT="FAB_AMOUNT";

	private static final Object STMTDF = "StmtDF";

	private static final String STATEMENT_ZIP_FILENAME = "stmt_%s.zip";
	
	static Hashtable<String, Object> getSimpleField(ArrayList<PrtBodyItem> body, String id) {
		return getField(body, id, null);
	}
	
	static Hashtable<String, Object> getField(ArrayList<PrtBodyItem> body, String id, String function) {
		int index=PrtBodyItem.getIndex(body,id);
		if(index<0) return null;
		PrtBodyItem item=body.get(index);
		return getField(item,id,function);		
	}
	
	private static Hashtable<String, Object> getField(PrtBodyItem item, String id, String function) {
		String value=item.getText();
		//System.out.println("value1="+value);
		if(function != null) {
			Class<?>[] param={value.getClass()};
			Object[] args={value};
			try {
				Class<?> cl=item.getClass();
				Method f=cl.getDeclaredMethod(function,param);
				value=(String) f.invoke(item, args);
			}
			catch (Exception e) {
				e.printStackTrace();
				value=null;
			}
		}
		//System.out.println("value2="+value);
		Hashtable<String, Object> ht=FINBodyParser.getValues(value, id);
		//System.out.println(id+"=\r\n"+ht);
		return ht;
	}

	public static ArrayList<Hashtable<String, Object>> joinStatements(ArrayList<Hashtable<String, Object>> list, String tableName) {
		//ArrayList<Hashtable<String, Object>> newList=new  ArrayList<Hashtable<String, Object>>();
		for(int i=0;i<list.size();i++) {
			String ob=(String) list.get(i).get("OpeningBalanceDate");
			if(ob.length()>0) {
				String file=null;
				ArrayList<Hashtable<String, Object>> serie=findStatementSeries(list,i);
				DBConnector db=null;
				Hashtable<String, Object> table=null;
				if(serie.size()>0) {
					try {
						table=serie.get(0);
						db=DBConnector.getDb(false, null);
						table.put(CB, serie.get(serie.size()-1).get(CB));
						ByteArrayOutputStream out=new ByteArrayOutputStream();
						for(int j=0;j<serie.size();j++) {
							byte[] b=null;
							b=Sys.getTempFile((String) serie.get(j).get(MSG_TEXT));
							if(j>0) {
							out.write(MESSAGE_SEPARATOR2.getBytes());
							}
							out.write(b);
						}
						file=Sys.createTmpFile(out.toByteArray());
					}
					catch(IOException | SQLException e) {
						file=null;
					}
					if(file!=null) {
						for(int j=0;j<serie.size();j++) {
							try {
								db.update(
										tableName,
										new String[]{STATUS},
										new String[]{NOT_EXECUTED},
										"WHERE "+ID+" = '"+serie.get(j).get(ID)+"'");
							}
							catch (SQLException e) {
								e.printStackTrace();
							}
						}
						table.put(SN, Integer.toString(PrtBodyItem.parseSequenceStatement((String) table.get(SS))[0]));
						table.put(NS,Integer.toString(serie.size()));
						table.put(AL, NO);
						table.put(MSG_TEXT, file);
						table.put(Distribution.MESSAGE_ID_KEY, STMTDF);
						Distribution.createMessage(table);
						serie.clear();
					}
				}
			}
			//System.out.println("'"+list.get(i).get("OpeningBalanceDate")+"'");
		}
		return list;
	}
	
	public static ArrayList<Hashtable<String, Object>> joinStatementsAll(ArrayList<Hashtable<String, Object>> list, String tableName) {
		//ArrayList<Hashtable<String, Object>> newList=new  ArrayList<Hashtable<String, Object>>();
		ArrayList<Hashtable<String, Object>> all=new ArrayList<Hashtable<String, Object>>();
		Hashtable<String, Object> table;
		for(int i=0;i<list.size();i++) {
			String ob=(String) list.get(i).get("OpeningBalanceDate");
			if(ob.length()>0) {
				String file=null;
				ArrayList<Hashtable<String, Object>> serie=findStatementSeries(list,i);
				DBConnector db=null;
				table=null;
				if(serie.size()>0) {
					try {
						table=serie.get(0);
						db=DBConnector.getDb(false, null);
						table.put(CB, serie.get(serie.size()-1).get(CB));
						ByteArrayOutputStream out=new ByteArrayOutputStream();
						for(int j=0;j<serie.size();j++) {
							byte[] b=null;
							b=Sys.getTempFile((String) serie.get(j).get(MSG_TEXT));
							if(j>0) {
							out.write(MESSAGE_SEPARATOR2.getBytes());
							}
							out.write(b);
						}
						file=Sys.createTmpFile(out.toByteArray());
					}
					catch(IOException | SQLException e) {
						file=null;
					}
					if(file!=null) {
						for(int j=0;j<serie.size();j++) {
							try {
								db.update(
										tableName,
										new String[]{STATUS},
										new String[]{NOT_EXECUTED},
										"WHERE "+ID+" = '"+serie.get(j).get(ID)+"'");
							}
							catch (SQLException e) {
								e.printStackTrace();
							}
						}
						table.put(SN, Integer.toString(PrtBodyItem.parseSequenceStatement((String) table.get(SS))[0]));
						table.put(NS,Integer.toString(serie.size()));
						//table.put(AN, value)
						table.put(MSG_TEXT, file);
						table.put(Distribution.MESSAGE_ID_KEY, STMTDF);
						//Distribution.createMessage(table);
						serie.clear();
						all.add(table);
					}
				}
			}
			//System.out.println("'"+list.get(i).get("OpeningBalanceDate")+"'");
		}
		if(all.size()>0) {
			try {
				Hashtable<String, Object> a=new Hashtable<String, Object>();
				a.put(AL, YE);
				a.put(SN, Integer.toString(all.size()));
				a.put(AD, Sys.getActualDate());
				String zipFile=Sys.createTmpFile(new byte[]{});
				ZipOutputStream out=new ZipOutputStream(new FileOutputStream(zipFile));
				StringBuffer buf=new StringBuffer();
				for(int i=0;i<all.size();i++) {
					table=all.get(i);
					buf.append(String.format("%s\t%s\t%s\t%s\t%s\t%s%s",
							table.get(SE),
							table.get(RE),
							table.get(SN),
							table.get(SN),
							Sys.getFullDateSK((String) table.get(OB)),
							Sys.getFullDateSK((String) table.get(CB)),
							System.getProperty("line.separator")));
					String file=(String) table.get(MSG_TEXT);
					out.putNextEntry(new ZipEntry((String) table.get(AN)));
					out.write(Sys.getTempFile(file));
					out.closeEntry();
					Sys.deleteTmpFile(file);
				}
				out.close();
				a.put(AN, String.format(STATEMENT_ZIP_FILENAME,Sys.getISOTime()));
				a.put(MSG_TEXT,out);
				a.put(LI, Base64Coder.encodeString(buf.toString()));
				a.put(Distribution.MESSAGE_ID_KEY, STMTDF);
				Distribution.createMessage(a);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return list;
	}
	
	
	
	private static ArrayList<Hashtable<String, Object>> findStatementSeries(
			ArrayList<Hashtable<String, Object>> list, int index) {
		ArrayList<Hashtable<String, Object>> serie=new ArrayList<Hashtable<String, Object>>();
		Hashtable<String, Object> table=list.get(index);
		Object[] item=new Object[] {table.get(MT),table.get(SE),table.get(RE)};
		int[] sequence=PrtBodyItem.parseSequenceStatement((String) table.get(SS));
		if(sequence==null) return serie;
		serie.add(table);
		list.remove(index);
		for(int i=0;i<list.size();i++) {
			table=list.get(i);
			if(table.get(MT).equals(item[0]) && table.get(SE).equals(item[1]) && table.get(RE).equals(item[2])) {
				int[] seq=PrtBodyItem.parseSequenceStatement((String) table.get(SS));
				if(seq!=null) {
					if(sequence[0]==seq[0]) {
						if(seq[1]==0) {
							serie.add(table);
						}
						else {
							boolean b=true;
							for(int j=0;j<serie.size();j++) {
								int s=PrtBodyItem.parseSequenceStatement((String) serie.get(j).get(SS))[1];
								if(s==0 || seq[1]<s) {
									serie.add(j,table);
									b=false;
									break;
								}
							}
							if(b) serie.add(table);
						}
					}
				}
			}
		}
		serie=SAAPrtMsgParser.orderStatementSerie(serie);
		return serie;
	}

	private static ArrayList<Hashtable<String, Object>> orderStatementSerie(
			ArrayList<Hashtable<String, Object>> serie) {
		for(int i=0;i<serie.size();i++) {
			Hashtable<String, Object> table = serie.get(i);
			if(PrtBodyItem.parseSequenceStatement((String) table.get(SS))[1]==0) {
				if(((String)table.get(OB)).length()!=0) {
					serie.add(0, serie.remove(i));
					break;
				}
			}
		}
		for(int i=0;i<serie.size();i++) {
			Hashtable<String, Object> table = serie.get(i);
			if(PrtBodyItem.parseSequenceStatement((String) table.get(SS))[1]==0) {
				if(((String)table.get(CB)).length()!=0) {
					serie.add(serie.size()-1, serie.remove(i));
					break;
				}
			}
		}
		if(!(((String)serie.get(0).get(OB)).length()!=0 && ((String)serie.get(serie.size()-1).get(CB)).length()!=0)) { 
			serie.clear();
		}
		return serie;
	}
	 
	
	/*
	private static final String MT_STAT="MT";
	private static final String SE_STAT="SENDER";
	private static final String RE_STAT="RECEIVER";
	
	private static String[] STATEMENT_SIMPLE_FIELDS = {"20","21","25","28C"};
	private static String[] STATEMENT_FORMATTED_FIELDS={
		"60F",PrtBodyItem.BALANCE,
		"60M",PrtBodyItem.BALANCE,
		"62F",PrtBodyItem.BALANCE,
		"62M",PrtBodyItem.BALANCE,
		"64",PrtBodyItem.BALANCE
	};
	private static String[][] STATEMENT_NON_FIELD_ITEMS = {
		{MT, MT_STAT, Sys.GET_MT},
		{SE, SE_STAT, null},
		{RE, RE_STAT, null}
	};
	
	public static void addStatement(Logger l, Hashtable<String, Object> tab) throws IOException {
		@SuppressWarnings("unused")
		Hashtable<String, Object> stat=new Hashtable<String, Object>();
		ByteArrayInputStream txt=new ByteArrayInputStream(Sys.getTempFile((String) tab.get(MSG_TEXT)));
		statements.addMessages(l, txt);
		String[] msg = (String[]) statements.remove(0);
		int[][] block={
				getBlok(msg, INSTANCE_TYPE_AND_TRANSMISSION),
				getBlok(msg, MESSAGE_HEADER),
				getBlok(msg, MESSAGE_TEXT),
				getBlok(msg, MESSAGE_TRAILER),
				getBlok(msg, INTERVENTIONS)
			};
		String[] bd=new String[block[2][1]-block[2][0]+1];
		System.arraycopy(msg, block[2][0], bd, 0, bd.length);
		ArrayList<PrtBodyItem> body=PrtBodyItem.parse(bd);
		
		Hashtable<String, Object> fix=new Hashtable<String, Object>();
		
		for(int i=0;i<STATEMENT_SIMPLE_FIELDS.length;i++) {
			Hashtable<String, Object> ht=getSimpleField(body,STATEMENT_SIMPLE_FIELDS[i]);
			//System.out.println(STATEMENT_SIMPLE_FIELDS[i]+"=\r\n"+ht);
			if(ht!=null) fix=Sys.join(ht, fix);
		}
		
		for(int i=0;i<STATEMENT_FORMATTED_FIELDS.length;i+=2) {
			Hashtable<String, Object> ht=getField(body,STATEMENT_FORMATTED_FIELDS[i],STATEMENT_FORMATTED_FIELDS[i+1]);
			//System.out.println(STATEMENT_FORMATTED_FIELDS[i]+"=\r\n"+ht);
			if(ht!=null) fix=Sys.join(ht, fix);
		}
		
		for(int i=0;i<STATEMENT_NON_FIELD_ITEMS.length;i++) {
			getNonField(tab,fix,STATEMENT_NON_FIELD_ITEMS[i]);
		}
				
		ArrayList<PrtBodyItem[]> list=PrtBodyItem.getArrayValue(body, new String[]{"28C"}, new String[]{"62F","62M"}, new String[]{"61","86"});
		ArrayList<PrtBodyItem[]> list2=PrtBodyItem.getArrayValue(body, new String[]{"62F","62M"}, null, new String[]{"65"});
		
		System.out.println(body);
		System.out.println("list.size="+list.size());
		System.out.println("list2.size="+list2.size());
		
		int index=PrtBodyItem.findFirstOccurenceOf(body, new String[]{"62F","62M"});
		if(index>0)index++;
		Hashtable<String, Object> field86 =null;
		if(index<body.size()) field86 = getField(body.get(index),"86",null);
		if(field86!=null)fix=Sys.join(fix, field86);
		//System.out.println(field86);
		
		
		
		
		
		for(int i=0;i<list.size();i++) {
			//System.out.println(list.get(i)[0]);
			//System.out.println(list.get(i)[1]);
			//System.out.println();
		}
		
		@SuppressWarnings("unused")
		ArrayList<Hashtable<String, Object>> sl=null;
		if(fix.get(MT_STAT).equals("950"))sl=getStatementLines950(list);
		else if(fix.get(MT_STAT).equals("940"))sl=getStatementLines940(list);
		
		ArrayList<Hashtable<String, Object>> fab=new ArrayList<Hashtable<String, Object>>();
		if(fix.get(MT_STAT).equals("940"))setForwardAvailableBalance(fab, list2);
		
		
		//System.out.println("fix=\r\n"+fix);
		//System.out.println("sl=\r\n"+sl);
		//System.out.println("fab=\r\n"+fab);
		
		
		System.exit(0);
		
	}
		
	
	private static void setForwardAvailableBalance(ArrayList<Hashtable<String, Object>> fab,ArrayList<PrtBodyItem[]> list) {
		
		
	}

	private static ArrayList<Hashtable<String, Object>> getStatementLines940(ArrayList<PrtBodyItem[]> list) throws IOException {
		ArrayList<Hashtable<String, Object>> sl=new ArrayList<Hashtable<String, Object>>();
		for(int i=0;i<sl.size();i++) {
			Hashtable<String, Object> ht=new Hashtable<String, Object>();
			PrtBodyItem[] line=list.get(i);
			if(line[0]!=null) {
				String text=PrtBodyItem.getMT950StatementLine(line[0].getText())[0];
				Hashtable<String, Object> tmp=FINBodyParser.getValues(text, "61");
				if(tmp!=null)ht=Sys.join(ht, tmp);
			}
			if(line[0]!=null) {
				Hashtable<String, Object> tmp=FINBodyParser.getValues(line[0].getText(), "86");
				if(tmp!=null)ht=Sys.join(ht, tmp);
			}
			if(ht.size()>0)sl.add(ht);
		}
		return sl;
	}
		
	private static ArrayList<Hashtable<String, Object>> getStatementLines950(ArrayList<PrtBodyItem[]> list) throws IOException {
		ArrayList<Hashtable<String, Object>> sl=new ArrayList<Hashtable<String, Object>>();
		if(list.size()==1) {
			PrtBodyItem[] line=list.get(0);
			if(line[0]!=null) {
				String[] lines=PrtBodyItem.getMT950StatementLine(line[0].getText());
				for(int i=0;i<lines.length;i++) {
					sl.add(FINBodyParser.getValues(lines[i], "61"));
				}
			}
		}
		return sl;
	}
	
	
	
	
	private static void getNonField(Hashtable<String, Object> tab,Hashtable<String, Object> fix, String[] s) {
		String value=(String) tab.get(s[0]);
		if(s[2]!=null) {
			try {
				Class<?> cl=Class.forName(Sys.THIS);
				Method m=cl.getDeclaredMethod(s[2], new Class<?>[]{value.getClass()});
				value=(String) m.invoke(null, new Object[]{value});
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				value=null;
			}
		}
		if(value!=null) {
			fix.put(s[1], value);
		}
	}
	*/
}
