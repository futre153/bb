package org.acepricot.asn1;

import java.io.IOException;

import org.acepricot.ber.BER;
import org.acepricot.ber.BERConst;
import org.acepricot.ber.BERFormatException;



abstract class ASN1NodeImpl implements ASN1Node {
	protected int tag = -1;
	protected int css = -1;
	protected int con = -1;
	protected int imp = -1;
	protected int opt = -1;
	protected ASN1NodeImpl[] cco = null;
	protected ASN1NodeImpl[] seq = null;
	protected Object val = null;
	protected String nme = null;
	protected BER ber = null;
		
	protected ASN1NodeImpl (int tag, int css, int con, int imp, int opt, String mne) {
		this.setTag(tag);
		this.setCss(css);
		this.setCon(con);
		this.setImp(imp);
		this.setOpt(opt);		
		this.setNme(mne);
	}

	protected int getTag() {
		return tag;
	}

	protected void setTag(int tag) {
		this.tag = tag;
	}

	protected int getCss() {
		return css;
	}

	protected void setCss(int css) {
		this.css = css;
	}

	protected int getCon() {
		return con;
	}

	protected void setCon(int con) {
		this.con = con;
	}

	protected int getImp() {
		return imp;
	}

	protected void setImp(int imp) {
		this.imp = imp;
	}

	protected int getOpt() {
		return opt;
	}

	protected void setOpt(int opt) {
		this.opt = opt;
	}

	protected ASN1NodeImpl[] getCco() {
		return cco;
	}

	protected void setCco(ASN1NodeImpl[] cco) {
		this.cco = cco;
	}

	protected ASN1NodeImpl[] getSeq() {
		return seq;
	}

	protected void setSeq(ASN1NodeImpl[] seq) {
		this.seq = seq;
	}

	protected Object getVal() {
		return val;
	}

	protected void setVal(Object val) {
		this.val = val;
	}

	protected String getNme() {
		return nme;
	}

	protected void setNme(String nme) {
		this.nme = nme;
	}

	protected BER getBer() {
		return ber;
	}

	protected void setBer(BER ber) {
		this.ber = ber;
	}
	
	//protected abstract ASN1NodeImpl clone();
	
	protected abstract void loadFromExisting(BER ber) throws IOException;

	public static void checkPrimitive(ASN1NodeImpl node, BER ber) throws IOException {
		checkTagNumberAndClassType(node, ber);
		if(!((node.getCon() == BERConst.PRIMITIVE_ENCODING) && ber.isPrimitive())) {
			throw new BERFormatException(String.format(BERFormatException.TAG_ENCODING_NOT_MATCH,BERConst.getEncodingName(ber.isPrimitive()), BERConst.getEncodingName(node.getCon() == BERConst.PRIMITIVE_ENCODING)));
		}
	}
	
	public static void checkConstructed(ASN1NodeImpl node, BER ber) throws IOException {
		checkTagNumberAndClassType(node, ber);
		if(!((node.getCon() == BERConst.CONSTRUCTED_ENCODING) && (!ber.isPrimitive()))) {
			throw new BERFormatException(String.format(BERFormatException.TAG_ENCODING_NOT_MATCH,BERConst.getEncodingName(ber.isPrimitive()), BERConst.getEncodingName(node.getCon() == BERConst.PRIMITIVE_ENCODING)));
		}
	}
	
	public static void checkTagNumberAndClassType(ASN1NodeImpl node, BER ber) throws IOException {
		checkTagNumber(node, ber);
		checkClassType(node, ber);
	}
	
	public static void checkClassType(ASN1NodeImpl node, BER ber) throws BERFormatException {
		if(node.getCss() != ber.getClassType()) {
			throw new BERFormatException(String.format(BERFormatException.CLASS_NOT_MATCH,BERConst.getClassType(ber.getClassType()), BERConst.getClassType(node.getCss())));
		}
	}
	
	public static void checkTagNumber(ASN1NodeImpl node, BER ber) throws IOException {
		if(node.getTag() != ber.getTagNumber()) {
			throw new BERFormatException(String.format(BERFormatException.TAG_NUMBER_NOT_MATCH,ber.getTagNumber(), node.getTag()));
		}
	}
}
