package org.acepricot.finance.web.util;


public class Sys {
	/*
	private static final String TMP_PATH = "tmp";
	private static final String TMP_FILENAME = "_tempfile_";
	private static final char[] INVALID_FILENAME_CHARS = new char[]{32,34,37,42,46,47,58,60,62,63,92,124};
	private static final int MAX_READED_BYTES = 4096;
	public static final String THIS = "org.pabk.emanager.util.Sys";
	public static final String GET_MT = "getMT";
	private static final String SEPA_DN_MASK = "ou=sepa,o=deutdeff,o=swift";
	private static final String SEPA_FILETYPE = "SDD";
	private static final String T2D_DN_MASK = ".+trgtxepm.+";
	private static final String T2D_FILETYPE = "T2 Directory";
	private static final String UNKNOWN_FILE = "neznámy";
	private static final String UNKNOWN_FILE2 = "neznámeho";
		*/
	private Sys(){}
	
	public static final String concatenate(Object[] array, Character delimiter) {
		if(array==null)return null;
		StringBuffer buf=new StringBuffer();
		for(int i=0;i<array.length;i++) {
			if(i>0 && delimiter!=null) {
				buf.append(delimiter);
			}
			buf.append(array[i]);
		}
		return buf.toString();
	}
	/*
	public static byte[] join(byte[] a, byte[] b) {
		byte[] c=new byte[a.length+b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

	public static byte[][] cut(byte[] buffer, int pointer) {
		byte[] a=Arrays.copyOf(buffer, pointer);
		byte[] b=new byte[buffer.length-pointer];
		System.arraycopy(buffer, pointer, b, 0, b.length);
		return new byte[][]{a,b};
	}
*/
	public static Object[] envelope(Object[] array, char c) {
		if(array==null)return null;
		Object[] retValue=new Object[array.length];
		for(int i=0;i<array.length;i++) {
			retValue[i]=(c+array[i].toString()+c);
		}
		return retValue;
	}
/*
	public static String[] join(String[] a, String[] b) {
		String[] c=new String[a.length+b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

	public static Hashtable<String,Object> join(Hashtable<String,Object> a,Hashtable<String,Object> b) {
		Hashtable<String,Object> c=new Hashtable<String, Object>();
		c.putAll(a);
		Iterator<String> key=b.keySet().iterator();
		while(key.hasNext()) {
			String k=key.next();
			String l=k;
			if(c.containsKey(k))l+="2";
			c.put(l, b.get(k)); 
		}
		return c;
		
	}
	
	public static String decodeB64(String s) {
		try {return Base64Coder.decodeString(s);}
		catch(Exception e) {return s;}
	}
	
	public static String shortName(String filename) {
		String[] tmp=filename.split("\\"+System.getProperty("file.separator"));
		if(tmp[tmp.length-1].length()==0) {
			if(tmp.length==1) return tmp[0];
			return tmp[tmp.length-2];
		}
		return tmp[tmp.length-1];
	}
	
	public static byte[] getTempFile(String fName) throws IOException {
		FileInputStream in=new FileInputStream(TMP_PATH+System.getProperty("file.separator")+fName);
		byte[] b=new byte[MAX_READED_BYTES];
		int i=-1;
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		while((i=in.read(b))>=0) {
			out.write(b,0,i);
		}
		in.close();
		b=out.toByteArray();
		out.close();
		return b;
	}
	
	public static String getPathOfTmpFile(String shortFilename) {
		return TMP_PATH+System.getProperty("file.separator")+shortFilename;
	}
	
	
	public static String createTmpFile(byte[] parseExpression) throws IOException {
		String path=TMP_PATH+System.getProperty("file.separator");
		String name=null;
		File f;
		while(true) {
			name=TMP_FILENAME+Calendar.getInstance().getTimeInMillis();
			f=new File(path+name);
			if(!f.exists()) break;
			new Sleeper().sleep(10);
		}
		f.createNewFile();
		FileOutputStream out=new FileOutputStream(f);
		out.write(parseExpression);
		out.close();
		return f.getName();
	}
	
	public static boolean deleteTmpFile(String filename) {
		String path=TMP_PATH+System.getProperty("file.separator")+filename;
		File f=new File(path);
		if(f.exists() && f.isFile()) {
			return f.delete();
		}
		else {
			return false;
		}
	}
	
	public static String number(String nr) {
		try {
			return Long.toString(Long.parseLong(nr)); 
		}
		catch(Exception e) {
			return nr;
		}
	}
	
	public static InetAddress DNSLookup(String host) throws UnknownHostException {
		if(host==null)return InetAddress.getLocalHost();
		return InetAddress.getByName((String) host);
	}
	
	public static String lookup(String host) {
		try {return DNSLookup(host).getHostName();} catch (UnknownHostException e) {return null;}
	}
	
	public static String mainRecipient(Integer i) {
		return Distribution.getMainRecipient();
	}
	
	
	public static String getFin(String uumid) {
		return uumid.substring(12,15);
	}
	
	public static String getBIC(String uumid) {
		return uumid.substring(1, 12);
	}
	public static String getReference(String uumid) {
		return uumid.substring(15);
	}
	public static String nackLine(String code) {
		if(code.length()<=3)return "";
		return number(code.substring(code.length()-3));
	}
	public static String nackCode(String code) {
		return code.substring(0,3);
	}
	public static String msgStatusDesc(String ms) {
		return Distribution.getCode(ms);
	}
	public static String nackDesc(String nack) {
		return Distribution.getCode(nackCode(nack));
	}
	public static String getFullDateFromMIRsk(String mir) {
		return PrtBodyItem.getFullDate(mir.substring(0,6));
	}
	public static String getFullDateSK(String date) {
		return PrtBodyItem.getFullDate(date);
	}
	public static String getBICFromMIR(String mir) {
		if(mir.length()==28) return mir.substring(6,14)+mir.substring(15,18);
		return mir;
	}
	public static String getBICfromDN(String dn) {
		try {
			dn=Base64Coder.decodeString(dn);
			String[] a=dn.split(",");
			dn=a[a.length-2];
			a=dn.split("=");
			if(!a[0].toLowerCase().equals("o")) throw new Exception();
			dn=a[1].toUpperCase();
			if(dn.matches("[0-9A-Z]{8)")) return dn;
			throw new Exception();
		}
		catch (Exception e){
			return dn;
		}
	}
	public static String getType2(String dn) {
		dn=Base64Coder.decodeString(dn);
		if(dn.matches(SEPA_DN_MASK)) return SEPA_FILETYPE;
		else if(dn.matches(T2D_DN_MASK)) return T2D_FILETYPE;
		else return UNKNOWN_FILE2;
	}
	public static String getType(String dn) {
		dn=Base64Coder.decodeString(dn);
		if(dn.matches(SEPA_DN_MASK)) return SEPA_FILETYPE;
		else if(dn.matches(T2D_DN_MASK)) return T2D_FILETYPE;
		else return UNKNOWN_FILE;
	}
	public static String getTime(String time) {
		try {
			int h=Integer.parseInt(time.substring(0,2));
			int m=Integer.parseInt(time.substring(2,4));
			return (h<10?"0":"")+Integer.toString(h)+":"+(m<10?"0":"")+Integer.toString(m);
		}
		catch(Exception e) {
			return time;
		}
	}
	
	public static String concatenate(Object[] array, String delimiter) {
		if(array==null)return null;
		StringBuffer buf=new StringBuffer();
		for(int i=0;i<array.length;i++) {
			if(i>0 && delimiter!=null) {
				buf.append(delimiter);
			}
			buf.append(array[i]);
		}
		return buf.toString();
	}
	
	public static int getNrMT(String s) {
		try {
			return Integer.parseInt(getShortMT(s));
		}
		catch(Exception e){return -1;}
	}
	
	public static String getShortMT(String s) {
		try {
			return s.split(" ")[1].split("\\.")[0];
		}
		catch(Exception e) {
			return s;
		}
	}
	public static String getDirection(String s) {
		if(s.equals(SAAPrtMsgParser.PRT_MSG_SENT))return "Odoslaná";
		else if(s.equals(SAAPrtMsgParser.PRT_MSG_RECEIVED))return "Prijatá";
		else return s;
	}
	
	public static String getMT(String s) {
		try {
			return s.split(" ")[1].trim();
		}
		catch(Exception e) {
			return s;
		}
	}

	public static String getShortDirection(String s) {
		if(s.equals(SAAPrtMsgParser.PRT_MSG_SENT))return "do";
		else if(s.equals(SAAPrtMsgParser.PRT_MSG_RECEIVED))return "od";
		else return s;
	}

	public static String ignoreInvalidFilenameChars(String s, char r) {
		StringBuffer retValue=new StringBuffer();
		//System.out.println("s=\""+s+"\"");
		char[] chars=s.toCharArray();
		for(int i=0;i<chars.length;i++) {
			if(Arrays.binarySearch(Sys.INVALID_FILENAME_CHARS, chars[i])<0)	retValue.append(chars[i]);
			else retValue.append(r);
		}
		return retValue.toString();
	}

	public static String getTmpFilesPath() {
		return Sys.TMP_PATH;
	}

	public static Object getActualDate() {
		Calendar cal=GregorianCalendar.getInstance();
		int d=cal.get(Calendar.DATE);
		int m=cal.get(Calendar.MONTH)+1;
		int r=cal.get(Calendar.YEAR);
		return PrtBodyItem.getFullDate(d, m, r>99?r%100:r);
	}

	public static String getISOTime() {
		Calendar c=GregorianCalendar.getInstance();
		int r=c.get(Calendar.YEAR);
		int m=c.get(Calendar.MONTH)+1;
		int d=c.get(Calendar.DATE);
		return (r<10?"":Integer.toString(r))+(m<10?"":Integer.toString(m))+(d<10?"":Integer.toString(d));
	}*/

	public static String concatenate(String[] array, String delimiter) {
		if(array==null)return null;
		StringBuffer buf=new StringBuffer();
		for(int i=0;i<array.length;i++) {
			if(i>0 && delimiter!=null) {
				buf.append(delimiter);
			}
			buf.append(array[i]);
		}
		return buf.toString();
	}
}
