package org.acepricot.ber;

public class BERConst {
	//common
	public static final int TRUE 						= 0x01;
	public static final int FALSE 						= 0x00;
	public static final int ANY 						=-0x03;
	public static final int CHOICE 						=-0x02;
	public static final int NOT_DEFINED 				=-0x01;
	// tag numbers
	public static final int EOC_TAG_NUMBER		 		= 0x00;
	public static final int BOOLEAN_TAG_NUMBER 			= 0x01;
	public static final int INTEGER_TAG_NUMBER 			= 0x02;
	public static final int BITSTRING_TAG_NUMBER 		= 0x03;
	public static final int OCTETSTRING_TAG_NUMBER 		= 0x04;
	public static final int NULL_TAG_NUMBER 			= 0x05;
	public static final int OBJECTIDENTIFIER_TAG_NUMBER	= 0x06;
	public static final int OBJECTDESCRIPTOR_TAG_NUMBER	= 0x07;
	public static final int EXTERNAL_TAG_NUMBER 		= 0x08;
	public static final int REAL_TAG_NUMBER 			= 0x09;
	public static final int ENUMERATED_TAG_NUMBER 		= 0x0A;
	public static final int EDDEDPVD_TAG_NUMBER 		= 0x0B;
	public static final int UTF8STRING_TAG_NUMBER 		= 0x0C;
	public static final int RELATIVEOID_TAG_NUMBER 		= 0x0D;
	public static final int RESERVER1_TAG_NUMBER 		= 0x0E;
	public static final int RESERVED2_TAG_NUMBER 		= 0x0F;
	public static final int SEQUENCE_TAG_NUMBER 		= 0x10;
	public static final int SET_TAG_NUMBER 				= 0x11;
	public static final int NUMERICSTRING_TAG_NUMBER 	= 0x12;
	public static final int PRINTABLESTRING_TAG_NUMBER 	= 0x12;
	public static final int T61STRING_TAG_NUMBER 		= 0x14;
	public static final int VIDEOTEXSTRING_TAG_NUMBER 	= 0x15;
	public static final int IA5STRING_TAG_NUMBER 		= 0x16;
	public static final int UTCTIME_TAG_NUMBER 			= 0x17;
	public static final int GENERALIZEDTIME_TAG_NUMBER 	= 0x18;
	public static final int GRAPHICSTRING_TAG_NUMBER 	= 0x19;
	public static final int VISIBLESTRING_TAG_NUMBER 	= 0x1A;
	public static final int GENERALSTRING_TAG_NUMBER 	= 0x1B;
	public static final int UNIVERSALSTRING_TAG_NUMBER 	= 0x1C;
	public static final int CHARACTERSTRING_TAG_NUMBER 	= 0x1D;
	public static final int BMPSTRING_TAG_NUMBER 		= 0x1E;
	// class types
	public static final int UNIVERSAL_CLASS				= 0x00;
	public static final int APPLICATION_CLASS			= 0x40;
	public static final int CONTEXT_SPECIFIC_CLASS		= 0x80;
	public static final int PRIVATE_CLASS				= 0xC0;
	//p/c flag
	public static final int PRIMITIVE_ENCODING		 	= 0x00;
	public static final int CONSTRUCTED_ENCODING	 	= 0x20;
	// class names
	private static final String UNIVERSAL_CLASS_NAME 	= "Universal";
	private static final String APPLICATION_CLASS_NAME	= "Application";
	private static final String CONTEXT_SPECIFIC_CLASS_N= "Context-specific";
	private static final String PRIVATE_CLASS_NAME 		= "Private";
	
	private static final String PRIMITIVE_ENCODING_NAME	= "Primitive";
	private static final String CONSTRUCTED_ENCODING_NAM= "Constructed";
		
	private static final String UNKNOWN_NAME 			= "Unknown";
				
	private BERConst(){}

	public static String getClassType(int classType) {
		switch(classType) {
		case UNIVERSAL_CLASS: return UNIVERSAL_CLASS_NAME;
		case APPLICATION_CLASS: return APPLICATION_CLASS_NAME;
		case CONTEXT_SPECIFIC_CLASS: return CONTEXT_SPECIFIC_CLASS_N;
		case PRIVATE_CLASS: return PRIVATE_CLASS_NAME;
		default: return UNKNOWN_NAME;
		}
		
	}

	public static String getEncodingName(boolean primitive) {
		if(primitive) return PRIMITIVE_ENCODING_NAME;
		return CONSTRUCTED_ENCODING_NAM;
	}
}
