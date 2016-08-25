package com.bb.http;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class CommonContent extends ContentImpl {
	private static final String DEFAULT_CHARSET = "UTF-8";
	private static final String IMAGE_DATA_URL_MASK = "data:%s;base64,%s";
	
	private byte[] data = new byte[0];
	private String charset = DEFAULT_CHARSET;
	
	public CommonContent (String defaultCharset) {
		charset = defaultCharset;
	}
	
	public CommonContent(){}
	
	public byte[] getData() {
		return data;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		String t = getContentType();
		String d = getDispType();
		String m = super.getMime();
		if(t == null) {
			if(d.equals(ContentImpl.FORM_DATA_CD)) {
				try {
					sb.append(new String (getData(), charset));
				} catch (UnsupportedEncodingException e) {
					sb.append (new String(getData()));
				}
			}
		}
		else {
			if(t.equals(ContentImpl.IMAGE_CT)) {
				sb.append(String.format(IMAGE_DATA_URL_MASK, m, Base64.getEncoder().encodeToString(getData())));
			}
		}
		return sb.toString();
	}
	
	public void setData(byte[] b) {
		byte[] dst = new byte[data.length + b.length];
		byte[] src = data;
		System.arraycopy(src, 0, dst, 0, src.length);
		System.arraycopy(b, 0, dst, src.length, b.length);
		data = dst;
		src = null;
	}
}
