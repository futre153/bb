package com.acepricot.finance.sync.share;

import java.util.ArrayList;

import com.acepricot.finance.sync.AppConst;

public class JSONMessage {

	private static final String LS = System.getProperty("line.separator");
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

	public JSONMessage returnOK(Object ... responses) {
		if(responses == null) {
			responses = new Object[]{};
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
		return null;
	}
}
