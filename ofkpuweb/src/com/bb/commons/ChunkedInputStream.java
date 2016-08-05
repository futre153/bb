package com.bb.commons;

import java.io.IOException;
import java.io.InputStream;

public class ChunkedInputStream extends InputStream {
	
	private static int CHUNK_LENGTH = 4096;
	private static String CRLF = "\r\n";
	
	private InputStream in;
	private int[] b = new int [Integer.toHexString(CHUNK_LENGTH).length()  + CRLF.length() + CHUNK_LENGTH + CRLF.length()];
	private int pointer = -1;
	private int length = 0;
	private boolean eof = false;
	
	public ChunkedInputStream(InputStream inputStream) {
		in = inputStream;
	}
	
	@Override
	public int read() throws IOException {
		if(pointer < 0) {
			if(eof) {
				return -1;
			}
			else {
				length = 0;
				int x = Integer.toHexString(CHUNK_LENGTH).length()  + CRLF.length();
				for (;length < CHUNK_LENGTH; length ++) {
					if ((b[length + x] = in.read()) < 0) {
						break;
					}
				}
				eof = length == 0;
				String len = Integer.toHexString(length);
				int index = Integer.toHexString(CHUNK_LENGTH).length() - len.length();
				pointer = index;
				for (int i = 0; i < len.length(); i ++) {
					b[index] = len.charAt(i);
					index ++;
					length ++;
				}
				b[index] = CRLF.charAt(0);
				b[index + 1] = CRLF.charAt(1);
				length += 2;
				b[pointer + length] = CRLF.charAt(0);
				b[pointer + length + 1] = CRLF.charAt(1);
				length = pointer + length + 2;
				return this.read();
			}
		}
		else {
			int r = b[pointer];
			pointer ++;
			if(pointer == length) {
				pointer = -1;
			}
			return r;
		}
	}
	
	
	public void close () throws IOException {
		in.close();
	}
}
