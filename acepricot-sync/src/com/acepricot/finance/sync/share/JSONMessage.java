package com.acepricot.finance.sync.share;

import java.util.ArrayList;

public class JSONMessage {

	private static final String LS = System.getProperty("line.separator");
	public static final String DOWNLOAD_HEADER = "download";
	private String header;
	private Object[] body;
	
	
	public JSONMessage(String header) {
		this();
		setHeader(header);
	}

	public JSONMessage(String header, Object[] body) {
		this(header);
		setBody(body);
	}

	public JSONMessage() {
		super();
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public Object[] getBody() {
		return body;
	}

	public void setBody(Object[] body) {
		this.body = body;
	}
	
	public void appendBody(Object ... responses) {
		if(responses == null) {
			responses = new Object[0];
		}
		Object[] old = this.getBody();
		if(old == null) {
			old = new Object[0];
		}
		Object[] _new = new Object[responses.length + old.length];
		System.arraycopy(old, 0, _new, 0, old.length);
		System.arraycopy(responses, 0, _new, old.length, responses.length);
		this.setBody(_new);
	}
	
	public JSONMessage returnOK(Object ... responses) {
		if(responses == null) {
			responses = new Object[0];
		}
		Object[] body = new Object[responses.length + 1];
		body[0] = AppConst.OK_RESPONSE;
		for(int i = 0; i < responses.length ;i ++) {
			body[i + 1] = responses[i];
		}
		this.setBody(body);
		return this;
	}

	public JSONMessage sendAppError(Throwable e) {
		this.setHeader(AppConst.JSON_ERROR_MSG);
		ArrayList<Object> errs = new ArrayList<Object>();
		if(e != null) {
			do {
				errs.add(e.getMessage());
				StackTraceElement[] trace = e.getStackTrace();
				StringBuffer sb = new StringBuffer();
				if(trace != null) {
					for(int i = 0; i < trace.length; i ++) {
						if(i > 0) {
							sb.append(LS);
						}
						sb.append(trace[i].toString());
					}
				}
				errs.add(sb.toString());
				sb = null;
			}
			while((e = e.getCause()) != null);
		}
		Object[] objs = new Object[errs.size()];
		objs = errs.toArray(objs);
		this.setBody(objs);
		return this;
	}
	
	public JSONMessage sendAppError(String message) {
		this.setHeader(AppConst.JSON_ERROR_MSG);
		this.setBody(new String[]{message});
		return this;
	}

	public boolean isError() {
		return this.getHeader().equals(AppConst.JSON_ERROR_MSG);
	}
	
	public boolean isGetError() {
		return this.getHeader().equals(AppConst.JSON_GET_ERROR_MSG);
	}
}
