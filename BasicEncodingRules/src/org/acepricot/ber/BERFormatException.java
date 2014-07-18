package org.acepricot.ber;

import java.io.IOException;

public class BERFormatException extends IOException {

	public BERFormatException(String format) {
		super(format);
	}
	public BERFormatException(Exception e) {
		super(e);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static final String UNEXPECTED_END_OF_STREAM = "Unexpected enf of stream reached";
	static final String TAG_NUMBER_LIMIT = "Tag number higher than %d are not supported by this version";
	static final String FORBIDEN_LANGTH_VALUE = "Value %d is not allowed for first length octet";
	static final String UNSUPPORTED_LENGTH_OCTET = "Content length greather than %d is not suported";
	static final String UNKNOWN_LENGTH_ERROR = "Byte array is out of range (%d)";
	static final String CONTENT_OCTETS_LENGTH_MISH_MATCH = "Content octets length mish-match";
	public static final String TAG_NUMBER_NOT_MATCH = "Wrong tag number %d, %d is expected";
	public static final String CLASS_NOT_MATCH = "Wrong class type %s, %s is expected";
	public static final String TAG_ENCODING_NOT_MATCH = "Wrong tag encoding %s, %s encoding is expected";
	public static final String VALUE_DECODE_MISSING = "No decoder defined for %s tag %d";
	public static final String NULL_CONSTRUCTED_CONTENT = "Constructed content is null";
	public static final String VALUE_UNCODE_MISSING = "No encoder defined for %s tag %d";
	public static final String WRONG_VALUE_CLASS = "Wrong class type %s for %d. %s is expected";
	public static final String WRONG_OID_VALUE_FORMAT = "Wrong oid value format";
	public static final String NO_SUCH_BER = "No such BER object whith name %s";
	public static final String NO_SUCH_OID = "No such OID object whith name %s";;

}
