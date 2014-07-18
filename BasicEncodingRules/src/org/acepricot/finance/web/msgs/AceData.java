package org.acepricot.finance.web.msgs;

import java.io.IOException;

import org.acepricot.ber.BER;
import org.acepricot.ber.BERConst;
import org.acepricot.ber.BERFormatException;

public class AceData extends BER {
	
	public static final String RESPONSE_STATUS_HEADER 		= "ResponseStatus";
	public static final String ERROR_MESSAGE_HEADER 		= "ErrorMessage";
	public static final String FILEPART_HEADER 				= "FilepartData";
	public static final String FILENAME_HEADER 				= "Filename";
	public static final String FILEPART_ID_HEADER 			= "FilepartId";
	public static final String FILEPART_COUNT_HEADER 		= "FilepartCount";
	public static final String FILE_DIGEST_HEADER 			= "FileDigest";
	public static final String EMAIL_HEADER 				= "RegistrationEmail";
	public static final String DEFAULT_DIGEST_ALGORITHM 	= "SHA-256";
	public static final String PASSWORD_HEADER 				= "DigestedPassword";
	public static final String REQUEST_RESPONSE_TEXT 		= "UniqueId";
	public static final String CONFIGURATION_RESPONSE_HEAD 	= "ConfigurationFile";

	public AceData() throws IOException {
		this.setIdOctets(BERConst.UNIVERSAL_CLASS, BERConst.CONSTRUCTED_ENCODING, BERConst.SEQUENCE_TAG_NUMBER);
		this.setDefiniteLength(true);
		this.getContentDecoder().setDefinite(true);
		this.getContentEncoder().setDefinite(true);
		this.setConstructedContent();
	}
	
	public AceDataItemImpl get(String key) throws IOException {
		BER ber = null;
		for(int i = 0; (ber = this.getCHildNode(i)) != null; i++) {
			if(new String((byte[])ber.getCHildNode(0).getContentDecoder().decodeValue(ber.getCHildNode(0))).equals(key)) {
				AceDataItemImpl item = new AceDataItemImpl();
				item.setName(ber.getCHildNode(0));
				item.setOid(ber.getCHildNode(1));
				item.setItemValue(ber.getCHildNode(2));
				item.setConstructedContent(ber.getCHildNode(0),ber.getCHildNode(1),ber.getCHildNode(2));
				return item;
			}
		}
		throw new BERFormatException(String.format(BERFormatException.NO_SUCH_BER, key));
	}
	
}
