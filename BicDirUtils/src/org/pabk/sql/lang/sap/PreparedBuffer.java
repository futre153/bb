package org.pabk.sql.lang.sap;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class PreparedBuffer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList <Object> buffer = new ArrayList<Object>(); 
	
	private int index = 0x00;
		
	public void append(Object ...objs) {
		for(int i = 0; i < objs.length; i ++) {
			this.buffer.add(objs[i]);
		}
	}
	
	public void setAll(PreparedStatement ps) {
		while(set(ps));
	}
	
	public boolean set(PreparedStatement ps) {	
		if(this.buffer.size() == index) {
			index = 0x00;
			return false;
		}
		try {
			ps.setObject(index + 1, this.buffer.get(index));
			index ++ ;
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
}
