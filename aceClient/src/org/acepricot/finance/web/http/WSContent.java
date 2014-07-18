package org.acepricot.finance.web.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class WSContent extends ContentImpl {
	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final String DEFAULT_TYPE = "text/xml";
	private String action="";
	private byte[] ins;
	@Override
	public int getLength() throws NullPointerException {
		if(ins==null){throw new NullPointerException("Content is null");}
		return ins.length;
	}

	@Override
	public byte[] getContent() throws NullPointerException {
		if(ins==null){throw new NullPointerException("Content is null");}
		return ins;
	}

	@Override
	public void setContent(Object obj) throws UnsupportedOperationException, IOException {
		if(obj instanceof File) {
			byte[] b=new byte[(int) ((File) obj).length()];
			FileInputStream in=new FileInputStream((File)obj);
			in.read(b);
			in.close();
			ins = b;
		}
		else if(obj instanceof String) {
			ins = ((String)obj).getBytes(DEFAULT_ENCODING);
		}
		else if (obj instanceof byte[]) {
			ins = (byte[]) obj;
		}
		else {
			throw new UnsupportedOperationException(obj.getClass().getName()+" is not supported");
		}
	}
	
	public String getEncoding() {
		return DEFAULT_ENCODING;
	}

	@Override
	public String getType() {
		return DEFAULT_TYPE;
	}
	
	public String getAction(){
		return action;
	}
	
	public void setAction(String action) {
		this.action=action;
	}
}
