package org.pabk.emanager.routing;


class Recipient {
	private boolean enabled=false;
	private String eMailAddress=null;
	Recipient(String name, String separator, String domain, boolean enabled) {
		String[] tmp=name.split(" ");
		if(tmp.length!=2)throw new NullPointerException("Invalid Name");
		this.setEMailAddress(tmp[0]+separator+tmp[1]+"@"+domain);
		this.setEnabled(enabled);
	}
	String getEMailAddress() {return eMailAddress;}
	private void setEMailAddress(String eMailAddress) {this.eMailAddress = eMailAddress;}
	boolean isEnabled() {return enabled;}
	private void setEnabled(boolean enabled) {this.enabled = enabled;}
	
	public boolean equals(Object obj) {
		if(obj!=null) {
			if(obj instanceof String) {
				String[] tmp=((String) obj).split(" ");
				if(tmp.length!=2) return false;
				//String xxx=eMailAddress.split("@")[0];
				//String regex="(^"+tmp[0]+").+("+tmp[1]+"$)";
				//boolean bbb=xxx.matches(regex);
				return eMailAddress.split("@")[0].matches("(^"+tmp[0]+").+("+tmp[1]+"$)");
			}
			else if (obj instanceof Recipient) return ((Recipient) obj).getEMailAddress().equals(eMailAddress);
		}
		return false;
	}
	public String toString() {
		return eMailAddress+" ["+(enabled?"E":"N")+"]"; 
	}
}