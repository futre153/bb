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
	public static final String SQL_OPERATION_HEADER 		= "SQLOperation";
	public static final String SQL_DATA_HEADER 				= "SQLData";
	public static final String CLIENT_ID_HEADER				= "ClientId";
	public static final String VERSION_HEADER 				= "Version";
	public static final String RESPONSE 					= "0";
	public static final String ERROR_MESSAGE 				= "999";
	public static final String REGISTRATION_REQUEST 		= "1";
	public static final String REGISTRATION_RESPONSE 		= "2";
	public static final String SYNCHRONOZATION_REQUEST		= "3";
	public static final String FILEPART_UPLOAD 				= "4";
	public static final String FILE_UPLOAD					= "5";
	public static final String SYNCHRONIZATION_CONF_REQUEST = "6";
	public static final String SYNCHRONIZATION_CONF_RESPONSE= "7";
	public static final String SYNCHRONIZATION_START_REQUEST= "8";
	public static final String SQL_OPERATION 				= "9";
	public static final String REMOVE_NODE 					= "10";
	public static final String DEREGISTRATION_REQUEST 		= "11";
	public static final String OK_RESPONSE 					= "OK";
	public static final String ERROR_RESPONSE 				= "ERROR";
	public static final String SQL_OPERATION_DELETE 		= "DELETE";
	public static final String SQL_TABLE_NAME_KEY 			= "TABLE@_NAME";
	public static final String SQL_WHERE_KEY 				= "SQL@_WHERE";
	public static final String SQL_OPERATION_INSERT 		= "INSERT";
	public static final String SQL_OPERATION_UPDATE 		= "UPDATE";
	
	protected String messageType = AceData.RESPONSE;
	
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
	
	public void setMessageType(String type) {
		this.messageType = type;
	}
	
	public String getMessageType() {
		return this.messageType;
	}
}
