package org.pabk.basen.asn1;

import java.io.IOException;
import java.util.ArrayList;

import org.pabk.basen.ber.BERImpl;

public abstract class ASN1Impl extends ArrayList<ASN1Impl[]> implements ASN1Object {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String LINE_SEPARATOR_KEY = "line.separator";
	private String name;
	private BERImpl ber;
	private int _class;
	private int constructed;
	private int tag;
	private boolean implicit;
	private boolean optional;
	private ASN1Impl[] sequences;
	/*
	protected ASN1Impl (String name, int _class, int pc, int tag, boolean implicit, boolean optional, ASN1Impl ... sequences) {
		//this();
		this.setName(name);
		this.set_class(_class);
		this.setConstructed(pc);
		this.setTag(tag);
		this.setImplicit(implicit);
		this.setOptional(optional);
		this.setSequences(sequences);
	}
	*/
	/*
	protected ASN1Impl() {
		// TODO Auto-generated constructor stub
	}
	*/
	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public boolean setBERObject(BERImpl ber) throws IOException {
		this.ber = ber;
		return implicit || (!implicit && this.checkId(ber));
	}
	
	public BERImpl getBERObject() {
		return ber;
	}

	public int get_class() {
		return _class;
	}

	public void set_class(int _class) {
		this._class = _class;
	}

	public int getConstructed() {
		return constructed;
	}

	public void setConstructed(int constructed) {
		this.constructed = constructed;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public boolean isImplicit() {
		return implicit;
	}

	public void setImplicit(boolean implicit) {
		this.implicit = implicit;
	}
	
	public abstract ASN1Impl clone();

	public ASN1Impl[] getSequences() {
		return sequences;
	}

	public void setSequences(ASN1Impl ... sequences) {
		this.sequences = sequences;
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	public boolean checkId(BERImpl ber) {
		return (this.get_class() == ber.get_class()) && (this.getConstructed() == ber.getConstructed()) && (this.getTag() == ber.getTag());		
	}
	
	public void setValue(Object ... objs) throws IOException {
		if(objs != null && objs.length == 1 && objs[0] != null) {
			BERImpl ber = new BERImpl();
			ber.set_class(this.get_class());
			ber.setConstructed(this.getConstructed());
			ber.setTag(this.getTag());
			ber.setEncoder();
			ber.getEncoder().setValue(ber, objs[0]);
			this.setBERObject(ber);
		}
		else {
			throw new IOException ("Failed to set Value to " + this.getClass().getSimpleName());
		}
	}
	
	public String toString() {
		return join(this.getBERObject().getEncoder().toString(this.getBERObject()), System.getProperty(LINE_SEPARATOR_KEY));
	}
	
	public static String[] split(String string) {
		return string.split(System.getProperty(LINE_SEPARATOR_KEY));
	}
	
	private String join(String[] array, String delimiter) {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < array.length; i ++) {
			if(i > 0) {
				sb.append(delimiter);
			}
			sb.append(array[i]);
		}
		return sb.toString();
	}

	public static ASN1Impl clone (ASN1Impl _new, ASN1Impl old) {
		_new.setName(old.getName());
		_new.set_class(old.get_class());
		_new.setConstructed(old.getConstructed());
		_new.setTag(old.getTag());
		_new.setImplicit(old.isImplicit());
		_new.setOptional(old.isOptional());
		ASN1Impl[] seq = ASN1Impl.clone(old.getSequences());
		if(seq != null) {
			_new.setSequences(seq);
		}
		return _new;
	}
	
	public static ASN1Impl[] clone(ASN1Impl ... seq) {
		if(seq == null) {
			return null;
		}
		else {
			ASN1Impl[] ret = new ASN1Impl[seq.length];
			for (int i = 0; i < seq.length; i ++) {
				if(seq[i] != null) {
					ret[i] = seq[i].clone();
				}
			}
			return ret;
		}
	}

	
}
