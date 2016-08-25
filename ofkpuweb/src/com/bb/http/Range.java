package com.bb.http;

import java.util.Arrays;

public final class Range {
	
	
	static final char NULL 	= 0x00;
	static final char SOH 	= 0x01;
	static final char STX 	= 0x02;
	static final char ETX 	= 0x03;
	static final char EOT 	= 0x04;
	static final char ENQ 	= 0x05;
	static final char ACK 	= 0x06;
	static final char BEL 	= 0x07;
	static final char BS 	= 0x08;
	static final char TAB 	= 0x09;
	static final char LF 	= 0x0A;
	static final char VT 	= 0x0B;
	static final char FF 	= 0x0C;
	static final char CR 	= 0x0D;
	static final char SO 	= 0x0E;
	static final char SI 	= 0x0F;
	static final char DLE 	= 0x10;
	static final char DC1 	= 0x11;
	static final char DC2 	= 0x12;
	static final char DC3 	= 0x13;
	static final char DC4 	= 0x14;
	static final char NAK 	= 0x15;
	static final char SYN 	= 0x16;
	static final char ETB 	= 0x17;
	static final char CAN 	= 0x18;
	static final char EM 	= 0x19;
	static final char SUB 	= 0x1A;
	static final char ESC 	= 0x1B;
	static final char FS 	= 0x1C;
	static final char GS 	= 0x1E;
	static final char RS 	= 0x1E;
	static final char US 	= 0x1F;
	static final char DEL 	= 0x7F;
	
	
	private static final int MIN_VLAUE = 0;
	private static final int MAX_VALUE = 255;
	private static final int RANGE = MAX_VALUE + 1;
	private static final int FCHAR = MAX_VALUE + 2;
	
	static final Range ALPHA 	= new Range (RANGE, 'A', 'Z', RANGE, 'a', 'z');
	static final Range BIT 		= new Range ('0', '1');
	static final Range CHAR 	= new Range (RANGE, NULL + 1, DEL);
	static final Range CR_R 	= new Range (CR);
	static final Range CRLF 	= new Range (CR, LF);
	static final Range CTL 		= new Range (RANGE, NULL, US, DEL);
	static final Range DIGIT 	= new Range (RANGE, '0', '9');
	static final Range DQUOTE	= new Range ('"');
	static final Range HEXADIG	= new Range (RANGE, '0', '9', RANGE, 'A', 'F');
	static final Range HTAB 	= new Range (TAB);
	static final Range LF_R 	= new Range (LF);
	static final Range LWSP 	= new Range ();
	static final Range OCTET 	= new Range (RANGE, NULL, MAX_VALUE);
	static final Range SP 		= new Range (' ');
	static final Range WCHAR 	= new Range (RANGE, '!', '~');
	static final Range WSP 		= new Range (' ', TAB);
	static final Range TCHAR	= new Range (RANGE, '0', '9', RANGE, 'A', 'Z', RANGE, 'a', 'z', '!', '#', '$', '%', '&', '\'', '*', '+', '-', '.', '^', '_', '`', '|', '~');
	static final Range QDTEXT 	= new Range (TAB, ' ', '!', RANGE, '#', '\'', RANGE, '*', '[', RANGE, ']', '~', RANGE, 0x80, 0xFF);
	static final Range QPCHAR	= new Range (RANGE, '!', '~', RANGE, 0x80, 0xFF, TAB, ' ');
	//static final Range UPALPHA = new Range (MAX_VALUE + 1, MIN_UPALPHA_VLAUE, MAX_UPALPHA_VALUE);
	//static final Range LOALPHA = new Range (MAX_VALUE + 1, MIN_LOALPHA_VLAUE, MAX_LOALPHA_VALUE);
	
	
	
	
	
	
	
	
	
	//static final Range TEXT = new Range (MAX_VALUE + 1, MIN_VLAUE, MAX_VALUE, MAX_VALUE + 2, 0, MAX_VALUE + 2, 0, MAX_VALUE + 2, 2, MAX_VALUE + 2, 3, MAX_VALUE + 2, 4, MAX_VALUE + 2, 5, MAX_VALUE + 2, 6, MAX_VALUE + 2, 7,
	//		MAX_VALUE + 2, 8, MAX_VALUE + 2, HT_CHAR, MAX_VALUE + 2, LF_CHAR, MAX_VALUE + 2, 11, MAX_VALUE + 2, 12, MAX_VALUE + 2, CR_CHAR, MAX_VALUE + 2, 14, MAX_VALUE + 2, 15, MAX_VALUE + 2, 16, MAX_VALUE + 2, 17, MAX_VALUE + 2, 18,
	//		MAX_VALUE + 2, 19, MAX_VALUE + 2, 20, MAX_VALUE + 2, 21, MAX_VALUE + 2, 22, MAX_VALUE + 2, 23, MAX_VALUE + 2, 24, MAX_VALUE + 2, 25, MAX_VALUE + 2, 26, MAX_VALUE + 2, 27, MAX_VALUE + 2, 28, MAX_VALUE + 2, 29,
	//		MAX_VALUE + 2, 30, MAX_VALUE + 2, 31);
	//static final Range HEX = new Range (MAX_VALUE + 1, MIN_DIGIT_VLAUE, MAX_DIGIT_VALUE, MAX_VALUE + 1, 'A', 'F', MAX_VALUE + 1, 'a', 'f');
	//static final Range TOKEN = new Range (MAX_VALUE + 1, MIN_CHAR_VLAUE, MAX_CHAR_VALUE, MAX_VALUE + 2, 0, MAX_VALUE + 2, 0, MAX_VALUE + 2, 2, MAX_VALUE + 2, 3, MAX_VALUE + 2, 4, MAX_VALUE + 2, 5, MAX_VALUE + 2, 6, MAX_VALUE + 2, 7,
	//		MAX_VALUE + 2, 8, MAX_VALUE + 2, HT_CHAR, MAX_VALUE + 2, LF_CHAR, MAX_VALUE + 2, 11, MAX_VALUE + 2, 12, MAX_VALUE + 2, CR_CHAR, MAX_VALUE + 2, 14, MAX_VALUE + 2, 15, MAX_VALUE + 2, 16, MAX_VALUE + 2, 17, MAX_VALUE + 2, 18,
	//		MAX_VALUE + 2, 19, MAX_VALUE + 2, 20, MAX_VALUE + 2, 21, MAX_VALUE + 2, 22, MAX_VALUE + 2, 23, MAX_VALUE + 2, 24, MAX_VALUE + 2, 25, MAX_VALUE + 2, 26, MAX_VALUE + 2, 27, MAX_VALUE + 2, 28, MAX_VALUE + 2, 29,
	//		MAX_VALUE + 2, 30, MAX_VALUE + 2, 31, MAX_VALUE + 2, '(', MAX_VALUE + 2, ')', MAX_VALUE + 2, '<', MAX_VALUE + 2,  '>', MAX_VALUE + 2, '@', MAX_VALUE + 2, ',', MAX_VALUE + 2, ';',
	//		MAX_VALUE + 2, ':', MAX_VALUE + 2, '\\', MAX_VALUE + 2, '"', MAX_VALUE + 2, '/', MAX_VALUE + 2, '[', MAX_VALUE + 2, ']', MAX_VALUE + 2, '?', MAX_VALUE + 2, '=', MAX_VALUE + 2, '{',
	//		MAX_VALUE + 2, '}', MAX_VALUE + 2, SP_CHAR, MAX_VALUE + 2, HT_CHAR);
	//static final Range SEPARATORS = new Range ('(', ')', '<', '>', '@', ',', ';', ':', '\\', '"', '/', '[', ']', '?', '=','{' ,'}' ,SP_CHAR, HT_CHAR);
	
	
	private int [] ranges = new int[0];
	private char[] allowedChars = new char[0];
	private char[] forbiddenChars = new char[0];
	
	public Range (int ...is) {
		for (int i = 0; i < is.length; i ++) {
			switch (is[i]) {
			case RANGE:
				addInterval(this, is[i + 1], is[i + 2]);
				i += 2;
				break;
			case FCHAR:
				switch (is[i + 1]) {
				case RANGE:
					for(int j = is[i + 2]; j < is[i + 3]; j ++) {
						addCharacter(this, false, j);
					}
					i += 3;
					break;
				default:
					addCharacter(this, false, is[i + 1]);
					i ++;
				}
			default:
				addCharacter(this, true, is[i]);
			}
		}
	}

	private static boolean addCharacter(Range range, boolean b, int i) {
		try {
			char[] src = b ? range.allowedChars : range.forbiddenChars;
			char[] dst = new char[src.length + 1];
			System.arraycopy(src, 0, dst, 0, src.length);
			dst[dst.length - 1] = (char) i;
			Arrays.sort(dst);
			if(b) {
				range.allowedChars = dst;
			}
			else {
				range.forbiddenChars = dst;
			}
			src = null;
			return true;
		}
		catch (Exception e) {
			return false;
		}
		
	}

	private static boolean addInterval(Range range, int i, int j) {
		try {
			if(i < MIN_VLAUE || i > MAX_VALUE || j < MIN_VLAUE || j > MAX_VALUE) {
				return false;
			}
			else {
				if (i > j) {
					int x = j;
					j = i;
					i = x;
				}
				int[] src = range.ranges;
				int[] dst = new int[src.length + 2];
				System.arraycopy(src, 0, dst, 0, src.length);
				dst[dst.length - 2] = i;
				dst[dst.length - 1] = j;
				range.ranges = dst;
				src = null;
				return true;
			}
		}
		catch (Exception e) {
			return false;
		}
	}

	boolean accept(char read) {
		int i = 0;
		for (; i < ranges.length; i += 2) {
			if(read >= ranges[i] && read <= ranges[i + 1]) {
				break;
			}
		}
		if(i == ranges.length) {
			if(Arrays.binarySearch(this.allowedChars, read) < 0) {
				return false;
			}
		}
		return forbiddenChars.length > 0 ? Arrays.binarySearch(forbiddenChars, read) < 0 : true;
	}

	String getExactString() {
		return new String(this.allowedChars);
	}
}
