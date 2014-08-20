package com.acepricot.finance.sync;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Iterator;

class TableSchema {
	protected int index = -1;
	protected Rows rows;
	
	protected TableSchema(){}
	
	public void reset() {
		this.index = -1;
	}
	
	boolean hasNext() {
		return (index + 1) <= rows.size();
	}
	
	boolean next() {
		if(hasNext()) {
			index ++;			
			Row row = rows.get(index);
			Iterator<String> keys = row.keySet().iterator();
			while(keys.hasNext()) {
				String key = keys.next();
				try {
					Field field = this.getClass().getDeclaredField(key.toLowerCase());
					if(field.getType().equals(int.class)) {
						field.set(this, DBConnector.intValue(row.get(key)));
					}
					else if(field.getType().equals(BigDecimal.class)) {
						field.set(this, DBConnector.bigDecimalValue(row.get(key)));
					}
					else if(field.getType().equals(String.class)) {
						field.set(this, DBConnector.toString(row.get(key)));
					}
				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
					return false;
				}
			}
			return true;
		}
		else {
			return false;
		}
	}
}
