package com.bb.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public final class MultipartContent extends ContentImpl {
	
	private static final String BOUNDARY_KEY = "boundary";
	private static final String ENCODING = "us-ascii";
	private static final String CONTENT_TYPE_HEADER = "Content-Type";
	private static final char HEADER_SEPARATOR = ':';
	private static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
	private static final Object BOUNDARY_END = "--";
	private static final Object FORM_DISP_TYPE = "form-data";
	private static final Object NAME_KEY = "name";
	private ArrayList<ContentImpl> contents = new ArrayList<ContentImpl>();
	
	public MultipartContent(String contentType, long length, String contentDisp, InputStream in) throws IOException {
		super.setInputStream(in);
		super.setContentLength(length);
		if(contentDisp != null) {
			super.setContentDisposition(contentDisp);
		}
		setContentType(contentType);
	}
		
	public void setContentType (String type) throws IOException {
		super.setContentType(type);
		String boundary = (String) getContentTypeParams().get(BOUNDARY_KEY);
		findPart(boundary);
	}

	private void findPart(String boundary) throws IOException {
		InputStream in = getInputStream();
		//BufferedReader reader = new BufferedReader(new InputStreamReader(in, ENCODING));
		String charset = (String) getContentTypeParams().get(ContentImpl.CHARSET_PM);
		String line = readLine(in);
		if(line.equals(BOUNDARY_END + boundary)) {
			CommonContent content = charset == null ? new CommonContent() : new CommonContent(charset);
			while(true) {
				line = readLine(in);
				if(line.length() != 0) {
					if(line.startsWith(CONTENT_TYPE_HEADER)) {
						content.setContentType(line.substring(line.indexOf(HEADER_SEPARATOR) + 1).trim());
					}
					else if (line.startsWith(CONTENT_DISPOSITION_HEADER)) {
						content.setContentDisposition(line.substring(line.indexOf(HEADER_SEPARATOR) + 1).trim());
					}
				}
				else {
					byte[] bytes = loadBytes(in, (BOUNDARY_END + boundary).getBytes());
					line = readLine(in);
					content.setData(bytes);
					contents.add(content);
					if(line.equals(BOUNDARY_END)) {
						break;
					}
					content = charset == null ? new CommonContent() : new CommonContent(charset);
				}
			}
		}
		
	}

	private String readLine(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int b = -1;
		int c = 0;
		while((b = in.read()) >= 0) {
			out.write(b);
			if(c == 0) {
				c = b == Range.CR ? 1 : 0;
				continue;
			}
			if(c == 1 && b == Range.LF) break;
			c = 0;
		}
		byte[] src = out.toByteArray();
		byte[] dst = new byte[src.length - 2];
		System.arraycopy(src, 0, dst, 0, dst.length);
		src = null;
		return new String(dst, ENCODING);
	}

	private byte[] loadBytes(InputStream in, byte[] boundary) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int b = -1;
		int c = 0;
		while((b = in.read()) >= 0) {
			out.write(b);
			if(c == 0) {
				c = b == Range.CR ? 1 : 0;
				continue;
			}
			if(c == 1) {
				c = b == Range.LF ? 2 : 0;
				continue;				
			}
			if(c > 1 && c < (boundary.length + 1)) {
				c = b == boundary[c - 2] ? c + 1 : 0;
				continue;
			}
			if(c == (boundary.length + 1) && b == boundary[c - 2]) break;
			c = 0;			
			/*
			if(c == 0 && b == Range.CR) {
				c++;
				continue;
			}
			if(c == 1) {
				if(b == Range.LF) {
					c++;
					continue;
				}
				else {
					c = 0;
					continue;
				}
			}
			if(c > 1 && c < (boundary.length + 1)) {
				if(b == boundary[c - 2]) {
					c ++;
					continue;
				}
				else {
					c = 0;
					continue;
				}
			}
			if(c == (boundary.length + 1) && b == boundary[c - 2]) {
				break;
			}
			else {
				c = 0;
			}
			*/
		}
		byte[] src = out.toByteArray();
		byte[] dst = new byte[src.length - boundary.length - 2];
		System.arraycopy(src, 0, dst, 0, dst.length);
		src = null;
		return dst;
	}

	public String getParameter(String name) {
		for(int i = 0; i < contents.size(); i ++) {
			CommonContent content = (CommonContent) contents.get(i);
			if(content.getDispType().equals(FORM_DISP_TYPE)) {
				String key = (String) content.getContentDispParams().get(NAME_KEY);
				if(key.substring(1, key.length() -1).equals(name)) {
					return content.toString();
				}
			}
		}
		return null;
	}
	
}
