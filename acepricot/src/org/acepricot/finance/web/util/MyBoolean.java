package org.acepricot.finance.web.util;

public class MyBoolean {
	public static int TRUE = 1;
	public static int FALSE = 0;
	private MyBoolean(){}
	public static String toString(int value) {
		return Integer.toString(value);
	}
}
