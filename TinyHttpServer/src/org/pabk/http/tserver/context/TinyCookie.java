package org.pabk.http.tserver.context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

class TinyCookie {
	
	public static final String TSID = "TSID";
	public static final String USERNAME = "USERNAME";
	public static final String DOMAIN = "DOMAIN";

	private static SimpleDateFormat df = null;
	
	private String name;
	private Object value;
	private long expires = -1;
	
	
	
	TinyCookie(String name, Object value, Date expires) {
		if(df == null) {
			df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.US);
			df.setTimeZone(TimeZone.getTimeZone("GMT"));
		}
		setName(name);
		setValue(value);
		setExpires(expires);
	}

	private void setExpires(Date expires) {
		if(expires == null) {
			this.expires = -1;
		}
		else {
			this.expires = expires.getTime();
		}
	}

	private void setValue(Object value) {
		this.value = value;		
	}

	private void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return getName() + "=" + getValue().toString() + (getExpires() < 0 ? "" : ("; expires=" + df.format(new Date(getExpires()))));
	}

	long getExpires() {
		return expires; 
	}

	Object getValue() {
		return value;
	}

	String getName() {
		return name;
	}

	public static Hashtable<String, TinyCookie> getCookies(List<String> list) {
		Hashtable<String, TinyCookie> tc = new Hashtable<String, TinyCookie>();
		if(list != null) {
			for(int i = 0; i < list.size(); i ++) {
				String[] s = list.get(i).split(";");
				for(int j = 0; j < s.length; j ++) {
					int index = s[j].indexOf('=');
					if(index > 0) {
						String name = s[j].substring(0, index).trim();
						String value = s[j].substring(index + 1).trim();
						tc.put(name, new TinyCookie(name, value, null));
					}
				}
			}
		}
		return tc;
	}
	
}
