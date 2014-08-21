package com.acepricot.finance.sync.share;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class JSONMessage {

	private static final String LS = System.getProperty("line.separator");
	public static final String DOWNLOAD_HEADER = "download";
	private static final String RESPONSE_HEADER = "response";
	public static final int EMPTY_REQUEST = 0x10;
	public static final int NO_ACTION = 0x11;
	public static final int REQUEST_PENDING_RESPONSE = 0x400;
	public static final int RESPONSE_FOR_PENDING_NO_OPERATION = 0x401;
	public static final int RESPONSE_FOR_PENDING_RESULT_FAILED = 0x402;
	public static final int RESPONSE_FOR_PENDING_RESULT_OK = 0x403;
	public static final int BUSY_RESPONSE = 0x404;
	public static final int INSERT_NO_ACTION = 0x410;
	public static final int INSERT_OPERATION = 0x411;
	public static final int INSERT_UPDATE_PK = 0x412;
	public static final int FORCE_OPERATION = 0xFFF;
	public static final int REQUEST_FOR_FORCE = 0xFFE;
	
			
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
	
	public JSONMessage appendBody(Object ... responses) {
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
		return this;
	}
	
	public JSONMessage returnOK(Object ... responses) {
		if(responses == null) {
			responses = new Object[0];
		}
		if(header == null) {
			setHeader(RESPONSE_HEADER);
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
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		String nl = System.getProperty("line.separator");
		sb.append("Header:");
		sb.append(nl);
		sb.append('\t');
		sb.append(this.getHeader());
		sb.append(nl);
		sb.append("Body:");
		Object[] body = this.getBody();
		for(int i = 0; i < body.length; i ++) {
			Object obj = body[i];
			if(obj instanceof String) {
				String[] a = ((String) obj).split(nl);
				for(int j = 0; j < a.length; j ++) {
					sb.append(nl);
					sb.append('\t');
					if(j > 0) {
						sb.append('\t');
					}
					sb.append(a[j]);
				}
			}
			else {
				if(obj != null) {
					sb.append(nl);
					sb.append('\t');
					if (obj.getClass().isArray()) {
						int k = Array.getLength(obj);
						sb.append('[');
						for(int j = 0; j < k; j ++) {
							if(j > 0) {
								sb.append(", ");
							}
							sb.append('"');
							sb.append(Array.get(obj, j));
							sb.append('"');
						}
						sb.append(']');
					}
					else {
						sb.append(obj);
					}
				}
				else {
					sb.append(nl);
					sb.append('\t');
					sb.append(obj);
				}
			}
		}
		return sb.toString();
	}
}
