package com.bb.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

final class CoreRules {
	
	private static final String ENCODING = "US-ASCII";
	private static final int MAX_READED_CHARS = 1024;
	
	
	static final int ALPHA 		= 0x01;
	static final int BIT 		= 0x02;
	static final int CHAR 		= 0x03;
	static final int CR	 		= 0x04;
	static final int CRLF 		= 0x05;
	static final int CTL 		= 0x06;
	static final int DIGIT 		= 0x07;
	static final int DQUOTE		= 0x08;
	static final int HEXADIG	= 0x09;
	static final int HTAB 		= 0x0A;
	static final int LF	 		= 0x0B;
	static final int LWSP 		= 0x0C;
	static final int OCTET 		= 0x0D;
	static final int SP 		= 0x0E;
	static final int WCHAR 		= 0x0F;
	static final int WSP 		= 0x10;
		
	static final int TOKEN 			= 0x20;
	static final int TCHAR 			= 0x21;
	static final int QUOTED_STRING 	= 0x22;
	static final int QDTEXT 		= 0x23;
	static final int QUOTED_PAIR 	= 0x24;
	static final int QPCHAR		 	= 0x25;
	
	static final int EXACT_STRING 	= 0xA0;
	
	
	private BufferedReader reader;
	private char[] buffer = new char[0];
	private int pointer = 0;
	private int offset = 0;
	
	private CoreRules(InputStream in) throws IOException {
		reader = new BufferedReader(new InputStreamReader(in, ENCODING));
	}
	
	static CoreRules getInstance(InputStream stream) throws IOException {
		return new CoreRules(stream);
	}
	
	static CoreRules getInstance(String stream) throws IOException {
		return new CoreRules(new ByteArrayInputStream (stream.getBytes(ENCODING)));
	}
	
	static String read (CoreRules br, int rule, int max, String ex) throws IOException {
		switch (rule) {
		case CHAR:
			return readOctets (br, Range.CHAR, max);
		case ALPHA:
			return readOctets (br, Range.ALPHA, max);
		case DIGIT:
			return readOctets (br, Range.DIGIT, max);
		case CTL:
			return readOctets (br, Range.CTL, max);
		case CR:
			return readOctets (br, Range.CR_R, max);
		case LF:
			return readOctets (br, Range.LF_R, max);
		case SP:
			return readOctets (br, Range.SP, max);
		case HTAB:
			return readOctets (br, Range.HTAB, max);
		case DQUOTE:
			return readOctets (br, Range.DQUOTE, max);
		case CRLF:
			return readExactString (br, Range.CRLF.getExactString());
		case WSP:
			return readOctets (br, Range.WSP, max);
		/*case WSP:
			return readLinearWhiteSpace(br);
		case TEXT:
			return readText(br);*/
		case HEXADIG:
			return readOctets(br, Range.HEXADIG, max);
		case TCHAR:
		case TOKEN:
			return readOctets(br, Range.TCHAR, max);
		case QUOTED_STRING:
			return readQuotedString (br);
		case QDTEXT:
			return readOctets (br, Range.QDTEXT, max);
		case QUOTED_PAIR:
			return readQuotedPair (br);
		case QPCHAR:
			return readOctets(br, Range.QPCHAR, max);
		
		
		case EXACT_STRING:
			return readExactString (br, ex);
		case OCTET:
		default:
			return readOctets (br, Range.OCTET, max);
		}
	}
	
		
	private static String readQuotedPair(CoreRules br) throws IOException {
		StringBuffer sb = new StringBuffer();
		int p = br.getPointer();
		String pfx = read (br, EXACT_STRING, 0, new String(new char[]{'\\'}));
		if(pfx.length() == 1) {
			String qptext = read(br, QPCHAR, 1, null);
			if(qptext.length() == 1) {
				sb.append(pfx);
				sb.append(qptext);
			}
			else {
				br.setPointer(p);
			}
		}
		return sb.toString();
	}

	private static String readQuotedString(CoreRules br) throws IOException {
		StringBuffer sb = new StringBuffer();
		int p = br.pointer;
		String q = read (br, EXACT_STRING, Range.NULL, Range.DQUOTE.getExactString());
		if(q.length() != 0) {
			sb.append(q);
			while (true) {
				String qdtext = read (br, QDTEXT, 0, null);
				if(qdtext.length() > 0) {
					sb.append(qdtext);
				}
				else {
					String qpair = read (br, QUOTED_PAIR, 0, null);
					if(qpair.length() > 0) {
						sb.append(qpair);
					}
					else {
						break;
					}
				}
			}
			q = read (br, EXACT_STRING, Range.NULL, Range.DQUOTE.getExactString());
			if(q.length() == 1) {
				sb.append(q);
			}
			else {
				sb.delete(0, sb.length());
				br.pointer = p;
			}
		}
		return sb.toString();
	}
/*
	private static String readText(CoreRules br) throws IOException {
		StringBuffer sb = new StringBuffer();
		while(true) {
			String txt = readOctets(br, Range.TEXT, 0);
			String lws = read (br, LWS, -1);
			if(txt.length() != 0) {
				sb.append(txt);
			}
			if(lws.length() != 0) {
				sb.append(lws);
			}
			if(sb.length() == 0 && lws.length() == 0) {
				break;
			}
		}
		return sb.toString();
	}

	private static String readLinearWhiteSpace(CoreRules br) throws IOException {
		StringBuffer sb = new StringBuffer();
		int p = br.pointer;
		String crlf = read (br, CRLF, -1);
		String lws = readOctets (br, Range.LWS, 0);
		if(lws.length() != 0) {
			if(crlf.length() != 0) {
				sb.append(crlf);
			}
			sb.append(lws);
		}
		else {
			br.pointer = p;
		}
		return sb.toString();
	}
*/
	private static String readExactString(CoreRules br, String ex) throws IOException {
		StringBuffer sb = new StringBuffer();
		int p = br.pointer;
		String tmp = read (br, OCTET, ex.length(), null);
		if(tmp.equals(ex)) {
			sb.append(ex);
		}
		else {
			br.pointer = p;
		}
		return sb.toString();
	}

	private static String readOctets (CoreRules br, Range r, int max) throws IOException {
		StringBuffer sb = new StringBuffer();
		while (true) {
			if(br.pointer == br.offset) {
				if(!extendBuffer(br)) {
					break;
				}
			}
			char c = br.buffer[br.pointer];
			br.pointer ++;
			if(r.accept(c)) {
				sb.append(c);
			}
			else {
				br.pointer --;
				break;
			}
			if(max > 0) {
				max --;
				if (max == 0) {
					break;
				}
			}
		}
		return sb.toString();
	}
	
	private static boolean extendBuffer(CoreRules br) throws IOException {
		char[] tmp = new char[MAX_READED_CHARS];
		int i = br.reader.read(tmp);
		if(i > 0) {
			char[] _new = new char[br.buffer.length + i];
			System.arraycopy(br.buffer, 0, _new, 0, br.buffer.length);
			System.arraycopy(tmp, 0, _new, br.buffer.length, i);
			br.buffer = null;
			tmp = null;
			br.buffer = _new;
			br.offset += i;
			return true;
		}
		else {
			return false;
		}
	}

	int getPointer() {
		return pointer;
	}

	void setPointer(int p) {
		pointer = p;		
	}
}
