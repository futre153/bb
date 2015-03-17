package org.pabk.emanager;



class Entry {
	Entry(Connection con,String cmd) {
		this.setCmd(cmd);
		this.setCon(con);
	}
	
	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	
	public Connection getCon() {
		return con;
	}

	public void setCon(Connection con) {
		this.con = con;
	}

	private String cmd;
	private Connection con;
}
