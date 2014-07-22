package com.acepricot.finance.sync;

import java.util.Locale;

final class AppError {
	
	
	private static final String[] APP_ERRORS = {
		"Content type %s is not allowed for method %s",			//0x01 HTTP ERRORS
		"Parameter %s is not defined",
		"Error while procening message on HTTP method %s",
		"Parameter %s is missing in request of HTTP method %s",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"Group %s is not registered",						//0x10 - LOGIN ERRORS
		"Multiple registration of group %s",
		"Group %s is not enabled",
		"Incorrect password for group %s",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"Group %s is allready registered",						//0x20 - REGISTRATION ERRORS
		"Unexpected return value, while registering group %s. Please contact support",
		"Device name '%s' is allready used in this group",
		"Multiple device name '%s' in this group",
		"Unexpected return value, while registering device %s. Please contact support",
		"No enabled devices for this group",
		"Unexpected return value, while setting primary device (%d). Please contact support",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"File is allready reistered under group id %s. Please contact support",		//0x30 - File download - upload
		"File is multiple registered under group id %s. Please contact support",
		"Unexcpected return values for group id %s, while registering file for download. Please contact support",
		"File download is not allowed for group id %s. Please contact support"
	};
	
	public static final int HTTP_CT_ERROR = 0x00;
	public static final int HTTP_PARAM_ERROR = 0x01;
	public static final int HTTP_METHOD_ERROR = 0x02;
	public static final int HTTP_ID_PARAM_ERROR = 0x03;
		
	public static final int GROUP_NOT_REGISTERED = 0x10;
	public static final int GROUP_MULTIPLE_REGISTRATION = 0x11;
	public static final int GROUP_NOT_ENABLED = 0x12;
	public static final int LOGIN_FAILED = 0x13;
		
	public static final int ALLREADY_REGISTERED = 0x20;
	public static final int UNEXPECTED_RETURN_VALUE = 0x21;
	public static final int DUPLICATE_DEVICE_NAME = 0x22;
	public static final int MULTIPLE_DEVICE_NAME = 0x23;
	public static final int DEVICE_UNEXPECTED_RETURN_VALUE = 0x24;
	public static final int NO_ENABLED_DEVICES = 0x25;
	public static final int PRIMARY_KEY_UNEXPECTED_ERROR = 0x26;

	public static final int DUPLICATE_FILE_INSERT = 0x30;
	public static final int MULTIPLE_FILE_INSERT = 0x031;
	public static final int FILE_UNEXPECTED_RETURN_VALUE = 0x032;
	public static final int FILE_INSERT_NOT_ENABLED = 0x33;
	
	
	private AppError(){}

	public static String getMessage(int index, Object ... objs) {
		return String.format(Locale.getDefault(), APP_ERRORS[index], objs);
	}
}
