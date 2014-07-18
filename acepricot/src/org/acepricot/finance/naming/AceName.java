package org.acepricot.finance.naming;

import java.util.Enumeration;
import java.util.Vector;

import javax.naming.InvalidNameException;
import javax.naming.Name;

public class AceName implements Name {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int EQUAL 			= 0;
	private static final int GREATHER_THAN 	= 1;
	private static final int LESS_THAN 		= -1;
	private Vector<String> comps = new Vector<String>();
	
		
	@Override
	public Name add(String comp) throws InvalidNameException {
		return add(this.size(), comp);
	}

	@Override
	public Name add(int index, String comp) throws InvalidNameException {
		try {
			comps.add(index, comp);
			return this;
		}
		catch(Exception e) {
			throw new InvalidNameException(e.getMessage());
		}
		
	}

	@Override
	public Name addAll(Name name) throws InvalidNameException {
		return addAll(this.size(), name);
	}

	@Override
	public Name addAll(int index, Name name) throws InvalidNameException {
		Enumeration<String> c = name.getAll();
		while(c.hasMoreElements()) {
			add(index, c.nextElement());
			index++;
		}
		return this;
	}

	@Override
	public int compareTo(Object obj) {
		if(obj instanceof AceName) {
			AceName name = (AceName) obj;
			if(this.size() == name.size()) {
				return EQUAL;
			}
			else if (this.size() > name.size()) {
				return GREATHER_THAN;
			}
			else {
				return LESS_THAN;
			}
		}
		throw new ClassCastException();
	}

	@Override
	public boolean endsWith(Name name) {
		try {
			return name.equals(this.getSuffix(this.size() - name.size()));
		}
		catch (Exception e) {
			return false;
		}
	}

	@Override
	public String get(int index) {
		return this.comps.get(index);
	}

	@Override
	public Enumeration<String> getAll() {
		return this.comps.elements();
	}

	@Override
	public Name getPrefix(int index) {
		Name name = new AceName();
		for(int i = 0; i < index; i++) {
			try {
				name.add(this.get(i));
			}
			catch (InvalidNameException e) {
				throw new ArrayIndexOutOfBoundsException(e.getMessage());
			}
		}
		return name;
	}

	@Override
	public Name getSuffix(int index) {
		if(index > this.size()) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
		Name name = new AceName();
		for(int i = index + 1; i < this.size(); i++) {
			try {
				name.add(this.get(i));
			}
			catch (InvalidNameException e) {
				throw new ArrayIndexOutOfBoundsException(e.getMessage());
			}
		}
		return name;
	}

	@Override
	public boolean isEmpty() {
		return this.size() == 0;
	}

	@Override
	public Object remove(int index) throws InvalidNameException {
		String comp = this.comps.remove(index);
		return comp;
	}

	@Override
	public int size() {
		return this.comps.size();
	}

	@Override
	public boolean startsWith(Name name) {
		try {
			return name.equals(this.getPrefix(name.size()));
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public Name clone() {
		Enumeration<String> c = this.getAll();
		Name name = new AceName();
		while(c.hasMoreElements()) {
			try {
				name.add(new String(c.nextElement().getBytes()));
			}
			catch(InvalidNameException e) {}
		}
		return name;
	}
	
}
