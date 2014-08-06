package com.acepricot.finance.sync.share.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class PreparedBuffer {

	private ArrayList <Object> buffer = new ArrayList<Object>(); 
	
	private int index = 0x01;
		
	public void append(Object ...objs) {
		for(int i = 0; i < objs.length; i ++) {
			this.buffer.add(objs[i]);
		}
	}
	
	public void setAll(PreparedStatement ps) {
		while(set(ps));
	}
	
	public boolean set(PreparedStatement ps) {
		if(this.buffer.size() == 0) {
			return false;
		}
		try {
			ps.setObject(index, this.buffer.remove(0));
			index ++;
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	public void close() {
		this.buffer.clear();
		index = 0x01;
	}
	
}
