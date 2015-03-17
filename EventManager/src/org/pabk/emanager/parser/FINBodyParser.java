package org.pabk.emanager.parser;

import java.io.IOException;
import java.util.Hashtable;

public class FINBodyParser {
	
	private static final String THIS = "org.pabk.emanager.parser.FINBodyParser";
	
	//Character sets
	private static final String SET_x="[0-9A-Za-z\\/\\-\\?\\:\\(\\)\\.\\,\\'\\+\\{\\}\\ \\\r\\\n]";
	private static final String SET_X="[0-9A-Za-z\\/\\-\\?\\:\\(\\)\\.\\,\\'\\+\\{\\}\\ ]";
	private static final String SET_Y="[0-9A-Z\\.\\,\\-\\(\\)\\/\\=\\'\\+\\:\\?\\!\\\"\\%\\&\\-\\<\\>\\;\\ ]";
	private static final String SET_z="[0-9A-Za-z\\.\\,\\-\\(\\)\\/\\=\\'\\+\\:\\?\\!\\\"\\%\\&\\*\\<\\>\\;\\{\\@\\#\\_\\\r\\\n]";
	private static final String SET_Z="[0-9A-Za-z\\.\\,\\-\\(\\)\\/\\=\\'\\+\\:\\?\\!\\\"\\%\\&\\*\\<\\>\\;\\{\\@\\#\\_]";
	private static final String SET_N="[0-9]";
	private static final String SET_A="[A-Z]";
	private static final String SET_C="[0-9A-Z]";
	private static final String SET_H="[0-9A-F]";
	private static final String SET_E="[ ]";
	private static final String SET_D="[\\d\\,]";
	
	private static final String FIELD_DEFINITION_PREFIX = "F";
	//Fields definition
	static final String[] F20=	{"M__16___X.:"+SAAPrtMsgParser.TRN};
	static final String[] F21=	{"M__16___X.:"+SAAPrtMsgParser.REL_TRN};
	static final String[] F25=	{"M__35___X__"+SAAPrtMsgParser.ACC_ID};
	static final String[] F28C=	{
		"M__05___N__"+SAAPrtMsgParser.ST_NR,
		"M/_05___N__"+SAAPrtMsgParser.SQ_NR};
	static final String[] F60F=	{
		"M__01!__A__"+SAAPrtMsgParser.OB_DC_MARK,
		"M__06!__N__"+SAAPrtMsgParser.OB_DATE,
		"M__03!__A__"+SAAPrtMsgParser.OB_CURRENCY,
		"M__15___D__"+SAAPrtMsgParser.OB_AMOUNT,
	};
	static final String[] F60M=	F60F;
	static final String[] F61=	{
		"M__06!__N__"+SAAPrtMsgParser.SL_VALUE_DATE,
		"___04!__N__"+SAAPrtMsgParser.SL_ENTRY_DATE,
		"M__02___A__"+SAAPrtMsgParser.SL_DC_MARK,
		"___01!__A__"+SAAPrtMsgParser.SL_FUNDS_CODE,
		"M__15___D__"+SAAPrtMsgParser.SL_AMOUNT,
		"M__01!__A__"+SAAPrtMsgParser.SL_TT,
		"M__03!__C__"+SAAPrtMsgParser.SL_IC,
		"M__16___X.:"+SAAPrtMsgParser.SL_REF_AO,
		"_//16___X.:"+SAAPrtMsgParser.SL_REF_ASI,
		"_\r\n35___X.:"+SAAPrtMsgParser.SL_DETAILS
	};
	static final String[] F62F=	{
		"M__01!__A__"+SAAPrtMsgParser.CB_DC_MARK,
		"M__06!__N__"+SAAPrtMsgParser.CB_DATE,
		"M__03!__A__"+SAAPrtMsgParser.CB_CURRENCY,
		"M__15___D__"+SAAPrtMsgParser.CB_AMOUNT,
	};
	static final String[] F62M=	F62F;
	static final String[] F64=	{
		"M__01!__A__"+SAAPrtMsgParser.CAB_DC_MARK,
		"M__06!__N__"+SAAPrtMsgParser.CAB_DATE,
		"M__03!__A__"+SAAPrtMsgParser.CAB_CURRENCY,
		"M__15___D__"+SAAPrtMsgParser.CAB_AMOUNT,
	};
	static final String[] F65=	{
		"M__01!__A__"+SAAPrtMsgParser.FAB_DC_MARK,
		"M__06!__N__"+SAAPrtMsgParser.FAB_DATE,
		"M__03!__A__"+SAAPrtMsgParser.FAB_CURRENCY,
		"M__15___D__"+SAAPrtMsgParser.FAB_AMOUNT,
	};
	static final String[] F79=	{"__M50*35X"+SAAPrtMsgParser.FREE_TXT};
	
	public static Hashtable<String,Object> getValues(String text, String id) {
		try {
			if(text == null || id == null) throw new IOException("Content of field or field ID is null");
			String[] def=(String[]) Class.forName(THIS).getDeclaredField(FIELD_DEFINITION_PREFIX+id).get(null);
			int index=0;
			Hashtable<String, Object> table=new Hashtable<String, Object>();
			for(int i=0;i<def.length;i++) {
				String value=checkPartial(text,def[i],index);
				if(value==null) throw new IOException("Multiformat error");
				if(value.length()>0) {
					String key=def[i].substring(11);
					index+=value.length();
					if(def[i].charAt(1)!='_')index++;
					if(def[i].charAt(2)!='_')index++;
					table.put(key, value);
				}
			}
			return table;
		}
		catch (IOException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
			//System.exit(1);
			return null;
		}
	}
		
	public static boolean check(String text, String id) {
		try {
			if(text == null || id == null) throw new IOException("Content of field or field ID is null");
			String[] def=(String[]) Class.forName(THIS).getDeclaredField(FIELD_DEFINITION_PREFIX+id).get(null);
			int index=0;
			for(int i=0;i<def.length;i++) {
				index+=checkPartial(text,def[i],index).length();
				if(def[i].charAt(1)!='_')index++;
				if(def[i].charAt(2)!='_')index++;
			}
			return true;
		} catch (IOException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ClassNotFoundException e) {
			return false;
		}
	}
	
	static private String checkPartial(String text, String def, int index) throws IOException {
		
		boolean mandatory = (def.charAt(0)=='M');
		int minLength=Integer.parseInt(def.substring(3,5));
		int maxLength=0;
		boolean fixedLength=false;
		int lines=0;
		char x=def.charAt(5);
		if(x=='!') {
			fixedLength=true;
			maxLength=minLength;
		}
		else if(x=='-') {
			maxLength=Integer.parseInt(def.substring(6,8));
		}
		else if(x=='*') {
			lines=minLength;
			maxLength=Integer.parseInt(def.substring(6,8));
			minLength=1;
		}
		else {
			maxLength=minLength;
			minLength=1;
		}
		int e=index;
		boolean forbidenStartSlash=(def.charAt(9)=='.');
		boolean forbidenTwoSlash=(def.charAt(10)==':');
		boolean[] frbs={forbidenStartSlash,forbidenTwoSlash};
		if(def.charAt(1)=='/' || def.charAt(1)=='\r') {
			if(e<text.length()) {
				if(!(text.charAt(e)==def.charAt(1))) {
					if(mandatory)throw new IOException ("Character '"+(def.charAt(0)=='/'?"/":"carriage return")+"' is expected as "+e);
				}
				else {
					e++;
				}
			}
			else {
				if(mandatory)throw new IOException ("Character '"+(def.charAt(0)=='/'?"/":"carriage return")+"' is expected as "+e);
			}
		}
		if(def.charAt(2)=='/' || def.charAt(2)=='\n') {
			if(e<text.length()) {
				if(!(text.charAt(e)==def.charAt(2))) {
					if(mandatory)throw new IOException ("Character '"+(def.charAt(0)=='/'?"/":"line feed")+"' is expected as "+e);
				}
				else {
					e++;
				}
			}
			else {
				if(mandatory)throw new IOException ("Character '"+(def.charAt(0)=='/'?"/":"line feed")+"' is expected as "+e);
			}
		}
		String mask=FINBodyParser.prepareMask(def.charAt(8),fixedLength,maxLength);
		String ret=null;
		if(lines>0) {
			ret=checkLines(lines,text,e,maxLength,mask,frbs);
		}
		else {
			ret=(fixedLength?checkFixedLength(text,e,maxLength,mask,frbs):checkLength(text,e,minLength, maxLength,mask,frbs));
		}
		ret=(mandatory?ret:(ret==null?"":ret));
		if(ret==null) throw new IOException("Character set exception. "+mask+" is expected.");
		return ret;
	}

	private static String checkLines(int lines, String text, int e, int length, String mask, boolean[] frbs) throws IOException {
		String ret=checkLength(text, e, 1, Integer.MAX_VALUE, mask, frbs);
		if(ret!=null) {
			if(ret.split("\r\n").length>lines) ret=null;
		}
		return ret;
	}

	static private String checkLength(String text,int e,int min, int max, String mask, boolean[] frbs) throws IOException {
		int i=min;
		if(text.substring(e,e+min).matches(prepareCharSet(mask,true,min))) {
			for(;i<max;i++) {
				if((i+e)>=text.length())break;
				if(!text.substring(e+i,e+i+1).matches(mask))break;
			}
			if(i>0) {
				String ret=checkForbiddens(text.substring(e,e+i),frbs);
				if(ret.length()>=min)return ret;
			}
		}
		return null;
	}

	static private String checkFixedLength(String text,int e,int len,String mask, boolean[] frbs) throws IOException {
		if(e+len>text.length())return null;
		String ret=text.substring(e,e+len);
		if(checkForbiddens(ret,frbs).length()==ret.length())if(ret.matches(mask))return ret;
		return null;
	}

	private static String checkForbiddens(String ret, boolean[] frbs) throws IOException {
		if(frbs[0])if(ret.charAt(0)=='/')throw new IOException ("Value cannot be start with slash");
		if(frbs[1])ret=ret.split("//")[0];
		return ret;
	}

	private static String prepareMask(char set, boolean fixed, int len) throws IOException {
		if(set=='X') return prepareCharSet(SET_X, fixed, len);
		if(set=='x') return prepareCharSet(SET_x, fixed, len);
		if(set=='Y') return prepareCharSet(SET_Y, fixed, len);
		if(set=='Z') return prepareCharSet(SET_Z, fixed, len);
		if(set=='z') return prepareCharSet(SET_z, fixed, len);
		if(set=='N') return prepareCharSet(SET_N, fixed, len);
		if(set=='A') return prepareCharSet(SET_A, fixed, len);
		if(set=='C') return prepareCharSet(SET_C, fixed, len);
		if(set=='H') return prepareCharSet(SET_H, fixed, len);
		if(set=='E') return prepareCharSet(SET_E, fixed, len);
		if(set=='D') return prepareCharSet(SET_D, fixed, len);
		throw new IOException ("Unknown character set "+set);
	}

	private static String prepareCharSet(String set, boolean fixed, int len) {
		if(fixed) return set+"{"+len+"}";
		return set;
	}
	
}
