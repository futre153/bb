package org.pabk.emanager.parser;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Hashtable;

public class TextParser {
	
	private static final int TEXT_TYPE = 0;
	private static final int EXPRESSION_TYPE = 1;
	public static final char EXPRESSION_MARKER = '%';
	private static final String FUNCTION_SEPARATOR = "[|]";
	private static final String DEFAULT_FUNCION_CLASSNAME = "org.pabk.emanager.util.Sys";
	private String source;
	private int pointer=-1;
	private Hashtable<String, Object> table;
	private int type=-1;
	
	private class Literal {
		private String value;
		private int type;
		//private Hashtable<String, Object> table;
		/*public byte[] getValue() {
			switch(type) {
			case TEXT_TYPE: return value.getBytes();
			case EXPRESSION_TYPE: return parseExpression(value);
			default:return null;
			}
		}*/
		public byte[] getValue(String enc) throws UnsupportedEncodingException {
			switch(type) {
			case TEXT_TYPE: return value.getBytes(enc);
			case EXPRESSION_TYPE: return TextParser.parseExpression(value,table,enc);
			default:return null;
			}
		}
		
		
	}
	
	
	
	
	private TextParser(String src, Hashtable<String, Object> tab) {
		source=src;
		table=tab;
		pointer=0;
		type=0;
	}
	
	
	
	
	
	
	private static byte[] parseExpression(String expression, Hashtable<String, Object> table, String enc) {
		if(expression==null)return null;
		String[] s=expression.split(FUNCTION_SEPARATOR);
		//System.out.println("sliteral="+s.length);
		//System.out.println("sliteral="+expression);
		//System.out.println("sliteral="+enc);
		//String xxx=(String)table.get(s[0]);
		//xxx+="RRR";
		//System.out.println(xxx.getClass());
		//System.out.println("sliteral="+xxx);
		switch(s.length) {
		case 1:
			try {return ((String)(table.get(s[0]))).getBytes(enc);}
			catch (Exception e) {
				try {return s[0].getBytes(enc);}catch(UnsupportedEncodingException e1){return s[0].getBytes();}
			}
		case 2:
			//System.out.println("EXPRESSION");
			Object param=table.get(s[0]);
			String ret=null;
			try {
				Method method=Class.forName(DEFAULT_FUNCION_CLASSNAME).getDeclaredMethod(s[1], new Class<?>[]{param.getClass()});
				ret=(String) method.invoke(null, new Object[]{param});
			}
			catch (NoSuchMethodException | SecurityException | ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {				
				ret=table.get(s[0]).toString();
			}
			try {return ret.getBytes(enc);} catch (UnsupportedEncodingException e) {ret.getBytes();}
		default:
			try {return expression.getBytes(enc);}catch(UnsupportedEncodingException e){}
		}
		return expression.getBytes();
	}

	public static String parse(String text, Hashtable<String, Object> tab, String encoding) {
		//System.out.println("text="+text);
		//System.out.println("enc="+encoding);
		TextParser parser=new TextParser(text, tab);
		try {return parser.parse(encoding);}
		catch (UnsupportedEncodingException e){return text;}
	}

	private String parse(String enc) throws UnsupportedEncodingException {
		Literal lit=null;
		String tmp=new String(new byte[]{}/*,enc*/);
		while((lit=next())!=null) {
			//System.out.println("literal="+lit.value);
			//System.out.println("literal="+lit.type);
			//System.out.println("literal="+lit.getValue(enc));
			tmp=tmp.concat(new String(lit.getValue(enc),enc));
		}
		return tmp;
	}

	private Literal next() {
		if(pointer>=source.length())return null;
		Literal l=new Literal();
		//l.table=this.table;
		int index=-1;
		switch(type) {
		case TEXT_TYPE:
			l.type=TEXT_TYPE;
			type=EXPRESSION_TYPE;
			index=source.indexOf(EXPRESSION_MARKER, pointer);
			if(index<0) {
				l.value=source.substring(pointer);
				pointer=source.length();
			}
			else {
				l.value=source.substring(pointer,index);
				pointer=index+1;
			}
			return l;
		case EXPRESSION_TYPE:
			type=TEXT_TYPE;
			index=source.indexOf(EXPRESSION_MARKER, pointer);
			if(index<0) {
				l.type=TEXT_TYPE;
				l.value=source.substring(pointer);
				pointer=source.length();
			}
			else {
				l.type=EXPRESSION_TYPE;
				l.value=source.substring(pointer,index);
				pointer=index+1;
			}
			return l;
		default:return null;		
		}
	}

	public static byte[] parseExpression(String text,Hashtable<String, Object> join) {
		return parseExpression(text,join,Charset.defaultCharset().toString());
	}

}
