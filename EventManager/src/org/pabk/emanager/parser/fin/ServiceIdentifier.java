package org.pabk.emanager.parser.fin;

public abstract class ServiceIdentifier {
	final public static String MESSAGE						= "01";
	final public static String LOGIN_REQUEST_MESSAGE		= "02";
	final public static String SELECT_COMMAND				= "03";
	final public static String QUIT_COMMAND					= "05";
	final public static String LOGOUT_COMMAND				= "06";
	final public static String SYSTEM_REQUEST_TO_REMOVE_LT	= "14";
	final public static String ACK_OF_GPA_AND_FIN_MESSAGES	= "21";
	final public static String LOGIN_POSITIVE_ACK			= "22";
	final public static String ACK_OF_SELECT_REQUEST		= "23";
	final public static String QUIT_ACK						= "25";
	final public static String LOGOUT_ACK					= "26";
	final public static String LOGIN_NEGATIVE_ACK			= "42";
	final public static String SELECT_NEGATIVE_ACK			= "43";
}
