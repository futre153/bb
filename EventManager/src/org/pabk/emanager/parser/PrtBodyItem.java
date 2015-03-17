package org.pabk.emanager.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class PrtBodyItem {
	private static final String JANUARY = "January";
	private static final String FEBRUARY = "February";
	private static final String MARCH = "March";
	private static final String APRIL = "April";
	private static final String MAY = "May";
	private static final String JUNE = "June";
	private static final String JULY = "July";
	private static final String AUGUST = "August";
	private static final String SEPTEMBER = "September";
	private static final String OCTOBER = "October";
	private static final String NOVEMBER = "November";
	private static final String DECEMBER = "December";
	
	private static final String JANUARY_SK = "Január";
	private static final String FEBRUARY_SK = "Február";
	private static final String MARCH_SK = "Marec";
	private static final String APRIL_SK = "Apríl";
	private static final String MAY_SK = "Máj";
	private static final String JUNE_SK = "Jún";
	private static final String JULY_SK = "Júl";
	private static final String AUGUST_SK = "August";
	private static final String SEPTEMBER_SK = "September";
	private static final String OCTOBER_SK = "Október";
	private static final String NOVEMBER_SK = "November";
	private static final String DECEMBER_SK = "December";
	
	private static final String[] YEAR_NAMES_SK = {
		JANUARY_SK,
		FEBRUARY_SK,
		MARCH_SK,
		APRIL_SK,
		MAY_SK,
		JUNE_SK,
		JULY_SK,
		AUGUST_SK,
		SEPTEMBER_SK,
		OCTOBER_SK,
		NOVEMBER_SK,
		DECEMBER_SK
	};
	
	private static final String ACTUAL_CENTURY_PREFIX="20";
	private static final String ENTRY_DATE_MASK = "((0[1-9])|(1[0-2]))((0[1-9])|(1\\d)|(2\\d)|(3[0-1]))";
	private static final String VALUE_DATE_MASK = "\\d\\d((0[1-9])|(1[0-2]))((0[1-9])|(1\\d)|(2\\d)|(3[0-1]))";
	private static final String FUNDS_CODE_MASK = "[A-Z]";
	private static final String CODE_MASK = "(S\\d{3})|([NF][A-Z]{3})";
	private static final String REFERENCE_MASK = ".{1,16}";
	private static final String AMOUNT_MASK = "(\\d{1,14}[,\\.])|(\\d{1,15})|(\\d{1,13}[,\\.]\\d?)|(\\d{1,12}[,\\.]\\d{0,2})";
	private static final String DEBIT_CREDIT_MARK_MASK = "R?[DC]";
	private static final String DETAILS_MASK = ".{1,34}";
	public static final String BALANCE = "getBalance";
	
	private String number;
	private String name;
	private String text;
	PrtBodyItem(String nr, String nm, String tx) {
		this.setNumber(nr);
		this.setName(nm);
		this.setText(tx);
	}
	String getNumber() {return number;}
	private void setNumber(String number) {this.number = number;}
	
	String getName() {return name;}
	private void setName(String name) {this.name = name;}
	
	String getText() {return text;}
	private void setText(String text) {this.text = text;}
	
	String getValue() {
		switch(getNrValue()) {
		case 32: return getValueDate(getText());
		case 106: return getMessageReference(getText());
		case 107: return getMessageReference(getText());
		case 60: return getBalanceDate(getText());
		case 62: return getBalanceDate(getText());
		default:return getText();
		}
	}
	
	private String getBalanceDate(String tx) {
		tx=getBalance(tx);
		if(tx!=null)if(tx.length()>6)return tx.substring(1,7);
		return null;
	}
	String getBalance(String tx) {
		String value="";
		String[] a=tx.split(System.getProperty("line.separator"));
		if(a.length==4) {
			String dc=getDebitCredit(a[0].trim());
			String dt=getDate(a[1].trim());
			String cu=getCurrency(a[2].trim());
			String am=getAmount(a[3].trim());
			if(dc!=null && dt!=null && cu!=null & am!=null) {
				value=dc+dt+cu+am;
			}
		}
		return value;
	}
	private String getDebitCredit(String dc) {
		String value=null;
		String[]a =dc.split(":");
		if(a.length==2) {
			dc=a[1].trim();
			if(dc.length()>0)value=dc.substring(0,1);
		}
		return value;
	}
	private String getMessageReference(String tx) {
		String value="";
		String[] a=tx.split(System.getProperty("line.separator"));
		if(a.length==2) {
			String dt=getDate(a[0].trim());
			if(dt!=null && a[1]!=null) value=dt+a[1].trim();
		}
		return value;
	}
	private String getValueDate(String tx) {
		String value="";
		String[] a=tx.split(System.getProperty("line.separator"));
		if(a.length==3) {
			String dt=getDate(a[0].trim());
			String cu=getCurrency(a[1].trim());
			String am=getAmount(a[2].trim());
			if(dt!=null && cu!=null & am!=null) {
				value=dt+cu+am;
			}
		}
		return value;
	}
	private String getAmount(String am) {
		String value=null;
		String[]a =am.split(":");
		if(a.length==2) {
			am=a[1].trim();
			if(am.charAt(0)=='#' && am.charAt(am.length()-1)=='#' && am.matches("#\\d+[\\.\\,]\\d*#")) {
				value=am.substring(1,am.length()-1);
			}
		}
		return value;
	}
	private String getCurrency(String c) {
		String value=null;
		String[] a=c.split(":");
		if(a.length==2) {
			c=a[1].trim().split(" ")[0].trim();
			if(c.matches("[A-Z]{3}"))value=c;
		}
		return value;
	}
	private String getDate(String dt) {
		String value=null;
		String[] a =dt.split(":");
		if(a.length==2) {
			a=a[1].trim().split(" ");
			if(a.length==3) {
				String d=getDay(a[0].trim());
				String m=getMonth(a[1].trim());
				String r=getYear(a[2].trim());
				if(d!=null && m!=null && r!=null) {
					value=r+m+d;
				}
			}
		}
		return value;
	}
	private String getYear(String r) {
		String value=null;
		if(r.matches("\\d{4}")) {
			value=r.substring(2,r.length());
		}
		return value;
	}
	private String getDay(String d) {
		String value=null;
		if(d.matches("\\d{1,2}")) {
			value=d;
		}
		return value;
	}
	
	public static String getFullDate (String s) {
		try {
			int d=Integer.parseInt(s.substring(4,6));
			int m=Integer.parseInt(s.substring(2,4));
			int r=Integer.parseInt(s.substring(0,2));
			return (d<10?"0":"")+Integer.toString(d)+" "+YEAR_NAMES_SK[m-1]+" "+ACTUAL_CENTURY_PREFIX+(r<10?"0":"")+r;			
		}
		catch(Exception e) {
			return s;
		}
	}
	
	public static String getFullDate(int d, int m, int r) {
		try {
			return (d<10?"0":"")+Integer.toString(d)+" "+YEAR_NAMES_SK[m-1]+" "+ACTUAL_CENTURY_PREFIX+(r<10?"0":"")+r;
		}
		catch(Exception e) {
			return Integer.toString(r)+"/"+Integer.toString(m)+"/"+Integer.toString(d);
		}
	}
	
	private String getMonth(String m) {
		if(m.equalsIgnoreCase(PrtBodyItem.JANUARY)){return "01";}
		else if(m.equalsIgnoreCase(PrtBodyItem.FEBRUARY)){return "02";}
		else if(m.equalsIgnoreCase(PrtBodyItem.MARCH)){return "03";}
		else if(m.equalsIgnoreCase(PrtBodyItem.APRIL)){return "04";}
		else if(m.equalsIgnoreCase(PrtBodyItem.MAY)){return "05";}
		else if(m.equalsIgnoreCase(PrtBodyItem.JUNE)){return "06";}
		else if(m.equalsIgnoreCase(PrtBodyItem.JULY)){return "07";}
		else if(m.equalsIgnoreCase(PrtBodyItem.AUGUST)){return "08";}
		else if(m.equalsIgnoreCase(PrtBodyItem.SEPTEMBER)){return "09";}
		else if(m.equalsIgnoreCase(PrtBodyItem.OCTOBER)){return "10";}
		else if(m.equalsIgnoreCase(PrtBodyItem.NOVEMBER)){return "11";}
		else if(m.equalsIgnoreCase(PrtBodyItem.DECEMBER)){return "12";}
		else {return null;}
	}
	
	private int getNrValue() {
		if(getNumber().matches("\\d+")) return Integer.parseInt(getNumber());
		if(getNumber().matches("\\d+[A-Z]")) return Integer.parseInt(getNumber().substring(0,getNumber().length()-1));
		return 0;
	}
	
	static int[] parseSequenceStatement(String ss) {
		String[] a=ss.split("\\/");
		switch(a.length) {
		case 1:	try {return new int[]{Integer.parseInt(a[0]),0};}catch(Exception e) {return null;}
		case 2: try {return new int[]{Integer.parseInt(a[0]),Integer.parseInt(a[1])};}catch(Exception e) {return null;}
		default:return null;
		}
	}
	
	static ArrayList<PrtBodyItem> parse (String[] body) {
		ArrayList<PrtBodyItem> bd=new ArrayList<PrtBodyItem>();
		int p=-1;
		if(body.length>1) {
			p=getPrefix(body[body.length-1]);
			if(p>=0) {
				int i=0;
				String nr="";
				String nm="";
				String tx="";
				while(true) {
					if(i>=body.length)break;
					String n=body[i].substring(0,p).trim();
					String t=body[i].substring(p).trim();
					if(n.length()==0) {
						tx+=(tx.length()==0?t:(System.getProperty("line.separator")+t));
					}
					else {
						if(n.matches("\\d+[A-Z]?:")) {
							if(!(nr.length()==0||nm.length()==0||tx.length()==0))bd.add(new PrtBodyItem(nr,nm,tx));
							nr=n.substring(0,n.length()-1);
							nm=t;
							tx="";
						}
						else {
							tx+=(tx.length()==0?t:(System.getProperty("line.separator")+t));
						}
					}
					i++;
				}
				if(!(nr.length()==0||nm.length()==0||tx.length()==0))bd.add(new PrtBodyItem(nr,nm,tx));
			}
		}
		return bd;
	}
	
	private static int getPrefix(String s) {
		int p=0;
		while(s.charAt(p)==' ') {
			p++;
			if(p>=s.length())break;		}
		return p;
	}
	
	static int getIndex(ArrayList<PrtBodyItem> body, String nr) {
		String[] s=nr.split(",");
		nr=s[0];
		int x=1;
		if(s.length>1) {
			try {x=Integer.parseInt(s[1]);}catch(Exception e) {}
		}
		for(int i=0;i<body.size();i++) {
			if(nr.equals(body.get(i).getNumber())) {
				x--;
				if(x==0)return i;
			}
		}
		return -1;
	}
	
	static String getValue(ArrayList<PrtBodyItem> body, String nr) {
		String[] s=nr.split(",");
		nr=s[0];
		int x=1;
		if(s.length>1) {
			try {x=Integer.parseInt(s[1]);}catch(Exception e) {}
		}
		for(int i=0;i<body.size();i++) {
			if(nr.equals(body.get(i).getNumber())) {
				x--;
				if(x==0)return body.get(i).getValue();
			}
		}
		return null;
	}
	
	static int findFirstOccurenceOfFromTo(ArrayList<PrtBodyItem> body, String[] fields, int start, int end) {
		for(int i=start;(i<body.size())||(i<end);i++) {
			String nr=body.get(i).getNumber();
			for(int j=0;j<fields.length;j++) {
				if(nr.equals(fields[j]))return i;
			}
		}
		return -1;
	}
	
	static int findFirstOccurenceOfFrom(ArrayList<PrtBodyItem> body, String[] fields, int start) {
		return findFirstOccurenceOfFromTo(body,fields,start,body.size());
	}
	
	static int findFirstOccurenceOf(ArrayList<PrtBodyItem> body, String[] fields) {
		return findFirstOccurenceOfFromTo(body,fields,0,body.size());
	}
	/*
	private static int getFirstAccuranceFrom(ArrayList<PrtBodyItem> body,	String[] fields, int index) {
		if(index>=body.size())return -2;
		for(int j=0;j<fields.length;j++) {
			if(body.get(index).number.equals(fields[j]))return j;
		}
		return -1;
	}
	*/
	
	static String[] getMT950StatementLine(String text) throws IOException {
		ArrayList<String> ret=new ArrayList<String>();
		String[] line=text.split(System.getProperty("line.separator"));
		//System.out.println("line.lenght="+line.length);
		int i=0;
		String line1 = null;
		while(true) {
			i++;
			if(i>=line.length) {
				if(line1!=null)ret.add(line1);
				break;
			}
			String line2=null,line3=null;
			if(line1==null) {
				line1=PrtBodyItem.getFirstStatementLine(line[i]);
				i++;
			}
			try {line2=PrtBodyItem.getFirstStatementLine(line[i]);}
			catch(IOException e) {}
			if(line2!=null) {
				ret.add(line1);
				line1=line2;
				continue;
			}
			line2=PrtBodyItem.getSecondStatementLine(line[i]);
			if(line2!=null) {
				line1+=("//"+line2);
				i++;
				try {line3=PrtBodyItem.getFirstStatementLine(line[i]);}
				catch(IOException e) {}
				if(line3!=null) {
					ret.add(line1);
					line1=line3;
					continue;
				}
			}
			line3=PrtBodyItem.getThirdStatementLine(line[i]);
			if(line3!=null) {
				line1+=(System.getProperty("line.separator")+line3);
				ret.add(line1);
				line1=null;
				continue;
			}
			throw new IOException("Format mismatch");
		}
		//System.out.println(ret);
		String[] retValue=new String[ret.size()];
		ret.toArray(retValue);
		return retValue;
	}
	private static String getThirdStatementLine(String ln) {
		if(ln.trim().matches(DETAILS_MASK))return ln.trim();
		return null;
	}
	private static String getSecondStatementLine(String ln) {
		if(ln.trim().matches(REFERENCE_MASK))return ln.trim();
		return null;
	}
	private static String getFirstStatementLine(String ln) throws IOException {
		try {
			StringTokenizer st=new StringTokenizer(ln);
			
			String value=st.nextToken(" ");
			//System.out.println("value="+value);
			//System.out.println("value="+value.matches(VALUE_DATE_MASK));
			if(!value.matches(VALUE_DATE_MASK))throw new IOException("Value date format is corrupt");
			
			String entr=st.nextToken(" ");
			//System.out.println("entr="+entr);
			//System.out.println("entr="+entr.matches(ENTRY_DATE_MASK));
			
			String f=null;
			if(entr.matches(ENTRY_DATE_MASK)) {f=st.nextToken(" ");}
			else {f=entr;entr=null;}
			//System.out.println("f="+f);
			//System.out.println("f="+f.matches(FUNDS_CODE_MASK));
			
			String code=null;
			if(f.matches(FUNDS_CODE_MASK)) {code=st.nextToken(" ");}
			else {code=f;f=null;}
			//System.out.println("code="+code);
			//System.out.println("code="+code.matches(CODE_MASK));
			if(!code.matches(CODE_MASK)) throw new IOException ("Trabsaction Type or Identification Code or both are corrupt");
			
			String reference=st.nextToken("#").trim();
			//System.out.println("reference='"+reference+"'");
			//System.out.println("reference="+reference.matches(REFERENCE_MASK));
			if(!reference.matches(REFERENCE_MASK)) throw new IOException ("Reference is corrupt");
			
			String amount=st.nextToken("#");
			//System.out.println("amount='"+amount+"'");
			//System.out.println("amount="+amount.matches(AMOUNT_MASK));
			if(!amount.matches(AMOUNT_MASK)) throw new IOException ("Amount is corrupt");
			
			String ma=st.nextToken().trim();
			//System.out.println("ma='"+ma+"'");
			//System.out.println("ma="+ma.matches(DEBIT_CREDIT_MARK_MASK));
			if(!ma.matches(DEBIT_CREDIT_MARK_MASK)) throw new IOException ("D/C mark is corrupt");
			
			//System.exit(1);
			return value+(entr==null?"":entr)+ma+(f==null?"":f)+amount+code+reference;
		}
		catch(NoSuchElementException e) {throw new IOException(e.getMessage());}
	}
	
	private static int contain(String[] field, PrtBodyItem prt) {
		for(int i=0;i<field.length;i++) {
			if(field[i].equals(prt.getNumber()))return i;
		}
		return -1;
	}
	
	static ArrayList<PrtBodyItem[]> getArrayValue(ArrayList<PrtBodyItem> body,String[] start,String[] end,String[] field) {
		int s=0;
		if(start!=null) {
			s=PrtBodyItem.findFirstOccurenceOf(body, start);
			//System.out.println("start="+s);
			if(s<0)return null;
			s++;
		}
		int e=body.size();
		if(end!=null) {
			e=PrtBodyItem.findFirstOccurenceOfFrom(body, end, s);
			//System.out.println("end="+e);
			if(e<0)e=body.size();
		}
		ArrayList<PrtBodyItem[]> ret=new ArrayList<PrtBodyItem[]>();
		int index=-1;
		PrtBodyItem[] line=null;
		for(int i=s;i<e;i++) {
			if(index<0) {
				line=new PrtBodyItem[field.length];
				index=0;
			}
			int j=contain(field,body.get(i));
			//System.out.println("i="+i+", j="+j);
			if(j<0)continue;
			if(j>=index) {
				line[j]=body.get(i);
				index=j+1;
			}
			else {
				ret.add(line);
				line=new PrtBodyItem[field.length];
				line[j]=body.get(i);
				index=j+1;
			}
			if(index==field.length){
				ret.add(line);
				index=-1;
			}
		}
		if(index>0) {
			ret.add(line);
		}
		return ret;
	}
	
	public String toString() {
		return this.getNumber();
	}
}
