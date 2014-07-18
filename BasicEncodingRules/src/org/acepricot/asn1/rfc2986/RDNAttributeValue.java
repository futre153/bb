package org.acepricot.asn1.rfc2986;

import org.acepricot.asn1.ASN1NodeImpl;
import org.acepricot.asn1.BMPString;
import org.acepricot.asn1.Choice;
import org.acepricot.asn1.PrintableString;
import org.acepricot.asn1.TeletexString;
import org.acepricot.asn1.UTF8String;
import org.acepricot.asn1.UniversalString;
import org.acepricot.ber.BERConst;

class RDNAttributeValue extends Choice {
	
	private TeletexString teletexString = new TeletexString (BERConst.FALSE, BERConst.TRUE, "value");
	private PrintableString printableString = new  PrintableString(BERConst.FALSE, BERConst.TRUE, "value"); 
	private UniversalString universalString = new  UniversalString (BERConst.FALSE, BERConst.TRUE, "value");
	private UTF8String utf8String = new  UTF8String (BERConst.FALSE, BERConst.TRUE, "value");
	private BMPString bmpString = new  BMPString (BERConst.FALSE, BERConst.TRUE, "value");
	
	private ASN1NodeImpl[] directoryStringChoice = {
			teletexString,
			printableString,
			universalString,
			utf8String,
			bmpString
	};
	
	RDNAttributeValue (String name) {
		super (BERConst.FALSE, name);
		this.setSeq(directoryStringChoice);
	};
}
