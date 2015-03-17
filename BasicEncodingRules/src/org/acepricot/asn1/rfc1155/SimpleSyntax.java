package org.acepricot.asn1.rfc1155;

import org.acepricot.asn1.Choice;
import org.acepricot.asn1.impl.ImpInteger;
import org.acepricot.asn1.impl.ImpNull;
import org.acepricot.asn1.impl.ImpOID;
import org.acepricot.asn1.impl.ImpOctetString;

public class SimpleSyntax extends Choice {

	private static final String VERSION = "version";
	private static final String STRING = "string";
	private static final String OBJECT = "object";
	private static final String EMPTY = "empty";

	protected SimpleSyntax(int optional, String name) {
		super(optional, name, new ImpInteger(VERSION), new ImpOctetString(STRING), new ImpOID(OBJECT), new ImpNull(EMPTY));
		// TODO Auto-generated constructor stub
	}

}
