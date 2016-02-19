package org.pabk.basen.ber.encoders;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.math.BigInteger;

import org.ietf.jgss.Oid;
import org.pabk.basen.asn1.oid.OidRepository;
import org.pabk.basen.ber.BERImpl;

public class EncoderUtil {

	private static final String[] NULL_VALUE = {"[NULL]"};
	private static final int MAX_READED_BYTES = 4096;
	private static final String FALSE = "FALSE";
	private static final String TRUE = "TRUE";

	private EncoderUtil(){}
	
	private static String[] printBinary(byte[] b, int length, int ext) {
		String[] ret = new String[(length - 1) / 8 + 1];
		int i = 0;
		for(; i < length; i += 8) {
			StringBuffer sb = new StringBuffer();
			sb.append(String.format("%4s", Integer.toHexString((i + ext * 8) & 0xFFFF)).replace(' ', '0').toUpperCase());
			int k = (i + 8 < length) ? (i + 8) : length;
			for(int j = i; j < k; j ++) {
				sb.append(' ');
				sb.append(printByteBinary(b[j]));
			}
			ret[i/8] = sb.toString();
		}
		return ret;
	}
	
	private static String printByteBinary(byte b) {
		StringBuffer sb = new StringBuffer(8);
		int i = 0x80;
		for (i = 0x80; i > 0; i >>= 0x01) {
			sb.append((b & i) > 0 ? "1" : "0");
		}
		return sb.toString();
	}

	private static String[] printHex(byte[] b, int length, int ext) {
		String[] ret = new String[(length - 1) / 16 + 1];
		int i = 0;
		for(; i < length; i += 16) {
			StringBuffer sb = new StringBuffer();
			StringBuffer sb2 = new StringBuffer();
			sb2.append(' ');
			sb.append(String.format("%4s", Integer.toHexString((i + ext * 16) & 0xFFFF)).replace(' ', '0').toUpperCase());
			sb.append(' ');
			int k = (i + 8 < length) ? (i + 8) : length;
			int l = 0;
			
			for(int j = i; j < k; j ++) {
				sb.append(String.format("%2s", Integer.toHexString(b[j] & 0xFF)).replace(' ', '0').toUpperCase());
				sb.append(' ');
				sb2.append(b[j] < 33 ? '.' : (char)(b[j] & 0xFF));
				l ++;
			}
			for (; l < 8; l ++) {
				sb.append("   ");
				sb2.append(' ');
			}
			sb.append('|');
			k = (i + 16 < length) ? (i + 16) : length;
			l = 0;
			for(int j = i + 8; j < k; j ++) {
				sb.append(' ');
				sb.append(String.format("%2s", Integer.toHexString(b[j] & 0xFF)).replace(' ', '0').toUpperCase());
				sb2.append(b[j] < ' ' ? '.' : (char)(b[j] & 0xFF));
				l ++;
			}
			for (; l < 8; l ++) {
				sb.append("   ");
				sb2.append(' ');
			}
			ret[i/16] = sb.toString() + sb2.toString();
		}
		return ret;
	}
	
	public static String[] printBinary(InputStream in) {
		byte[] b = new byte[MAX_READED_BYTES];
		String[] retVal = new String[0];
		int off = 0;
		int i;
		try {
			while ((i = in.read(b, off, MAX_READED_BYTES - off)) >= 0) {
				off = i % 8;
				if(i < 8) {
					break;
				}
				String[] ret = printBinary(b, i - off, retVal.length);
				System.arraycopy(b, i - off, b, 0, off);
				String[] tmp = new String[ret.length + retVal.length];
				System.arraycopy(retVal, 0, tmp, 0, retVal.length);
				System.arraycopy(ret, 0, tmp, retVal.length, ret.length);
				retVal = tmp;
			}
			if(off > 0) {
				String[] ret = printBinary(b, off, retVal.length);
				String[] tmp = new String[ret.length + retVal.length];
				System.arraycopy(retVal, 0, tmp, 0, retVal.length);
				System.arraycopy(ret, 0, tmp, retVal.length, ret.length);
				retVal = tmp;
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
			retVal = new String[0];
		}
		finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return retVal;
	}
	
	public static String[] printHex(InputStream in) {
		byte[] b = new byte[MAX_READED_BYTES];
		int off = 0;
		String[] retVal = new String[0];
		int i;
		try {
			while ((i = in.read(b, off, MAX_READED_BYTES - off)) >= 0) {
				off = i % 16;
				if(i < 16) {
					break;
				}
				String[] ret = printHex(b, i - off, retVal.length);
				System.arraycopy(b, i - off, b, 0, off);
				String[] tmp = new String[ret.length + retVal.length];
				System.arraycopy(retVal, 0, tmp, 0, retVal.length);
				System.arraycopy(ret, 0, tmp, retVal.length, ret.length);
				retVal = tmp;
			}
			if(off > 0) {
				String[] ret = printHex(b, off, retVal.length);
				String[] tmp = new String[ret.length + retVal.length];
				System.arraycopy(retVal, 0, tmp, 0, retVal.length);
				System.arraycopy(ret, 0, tmp, retVal.length, ret.length);
				retVal = tmp;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			retVal = new String[0];
		}
		finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(retVal.length == 0) {
			return NULL_VALUE;
		}
		else {
			return retVal;
		}
		
	}
	
	public static String[] parseBoolean(InputStream in) throws IOException {
		try {
			int i = in.read();
			if(i < 0) {
				throw new IOException ("End of stream reached while parsing BERBoolean value");
			}
			return new String[]{i == 0 ? FALSE : TRUE};
		}
		catch(Exception e) {
			throw new IOException (e);
		}
		finally {
			in.close();
		}
	}

	public static String[] parseInteger(InputStream in) throws IOException {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] b = new byte[MAX_READED_BYTES];
			int i;
			while((i = in.read(b)) >= 0) {
				out.write(b, 0, i);
			}
			if(out.size() == 0) {
				throw new IOException ("End of stream reached while parsing BERInteger value");
			}
			return new String[]{new BigInteger(out.toByteArray()).toString()};
		}
		catch(Exception e) {
			throw new IOException (e);
		}
		finally {
			in.close();
		}
	}
	
	public static String[] parseBitString(InputStream in) throws IOException {
		int i = -1;
		try {
			i = in.read();
			if(i < 0) {
				throw new IOException("Unexpected end of stream reached while parsing BitString value");
			}
			if(i > 7 || i < 0) {
				throw new IOException ("Initial octet of BitString value is out of range");
			}
		}
		catch(Exception e) {
			throw new IOException (e);
		}
		finally {
			in.close();
		}
		String[] retValue = printBinary(in);
		if(retValue == null || retValue.length == 0) {
			return NULL_VALUE;
		}
		else {
			retValue[retValue.length - 1].substring(0, retValue.length - (8 - i) & 0x07);
			return retValue;
		}
	}

	public static String join(Object array, CharSequence delim) {
		if(array.getClass().isArray()) {
			StringBuffer sb = new StringBuffer();
			int l = Array.getLength(array);
			for (int i = 0; i < l; i ++) {
				if(i > 0) {
					sb.append(delim);
				}
				sb.append(Array.get(array, i).toString());
			}
			return sb.toString();
		}
		return null;
	}

	public static String[] parseOid(BERImpl ber) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ber.encode(out);
		try {
			Oid oid = new Oid(out.toByteArray());
			return new String[] {oid.toString(), OidRepository.getDescription(oid)}; 
		}
		catch(Exception e) {
			throw new IOException (e);
		}
	}
}
