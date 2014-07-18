package org.acepricot.finance.web.msgs;

import java.io.IOException;

import org.acepricot.ber.BER;
import org.acepricot.ber.BERConst;

public class AceDataItemImpl extends AceDataItem {
	
	private BER itemName;
	private BER itemAlgorithmOid;
	private BER itemValue;
	
	public AceDataItemImpl(String name, String oid, byte[] value) throws IOException {
		super();
		this.setName(new BER());
		this.itemName.setIdOctets(BERConst.UNIVERSAL_CLASS, BERConst.PRIMITIVE_ENCODING, BERConst.OCTETSTRING_TAG_NUMBER);
		this.getContentEncoder().encodeValue(name.getBytes(), itemName, true);
		this.setOid (new BER());
		this.itemAlgorithmOid.setIdOctets(BERConst.UNIVERSAL_CLASS, BERConst.PRIMITIVE_ENCODING, BERConst.OBJECTIDENTIFIER_TAG_NUMBER);
		this.getContentEncoder().encodeValue(oid, itemAlgorithmOid, true);
		this.setItemValue (new BER());
		this.itemValue.setIdOctets(BERConst.UNIVERSAL_CLASS, BERConst.PRIMITIVE_ENCODING, BERConst.OCTETSTRING_TAG_NUMBER);
		this.getContentEncoder().encodeValue(value, itemValue, true);
		this.setConstructedContent(new BER[]{itemName, itemAlgorithmOid, itemValue});
	}
	

	public AceDataItemImpl() throws IOException {
		super();
	}


	public String getName() throws IOException {
		try {
			return new String((byte[])this.getContentDecoder().decodeValue(itemName));
		}
		catch(Exception e) {
			throw new IOException(e);
		}
		
	}
	
	public String getOid() throws IOException {
		try {
			return (String) this.getContentDecoder().decodeValue(itemAlgorithmOid);
		}
		catch(Exception e) {
			throw new IOException(e);
		}
		
	}
	
	public byte[] getItemValue() throws IOException {
		try {
			return (byte[]) this.getContentDecoder().decodeValue(itemValue);
		}
		catch(Exception e) {
			throw new IOException(e);
		}
		
	}

	public void setName(BER itemName) {
		this.itemName = itemName;
	}

	public void setOid(BER itemAlgorithmOid) {
		this.itemAlgorithmOid = itemAlgorithmOid;
	}

	public void setItemValue(BER itemValue) {
		this.itemValue = itemValue;
	}
}
