package com.bb.http;

import java.io.IOException;
import java.util.Hashtable;

final class Parameters extends Hashtable<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final char PARAM_EQUAL_SIGN = '=';
	private static final String PARAM_EQUAL_SIGN_MISSING = "Equal sign must be presented between parameter name and value";
	private static final String PARAM_VALUE_NULL = "Parameter value cannot be null";

	static void addParameters(Parameters params, CoreRules br) throws IOException {
		String error = null;
		int p = br.getPointer();
		String name = CoreRules.read(br, CoreRules.TOKEN, 0, null);
		String value = null;
		if(name.length() > 0) {
			if (CoreRules.read(br, CoreRules.EXACT_STRING, 0, new String(new char[]{PARAM_EQUAL_SIGN})).length() == 1) {
				int p2 = br.getPointer();
				value = CoreRules.read(br, CoreRules.TOKEN, 0, null);
				if(value.length() == 0) {
					br.setPointer(p2);
					value = CoreRules.read(br, CoreRules.QUOTED_STRING, 0, null);
					if(value.length() == 0) {
						error = PARAM_VALUE_NULL;
					}
				}					
			}
			else {
				error = PARAM_EQUAL_SIGN_MISSING;
			}
			if(error != null) {
				br.setPointer(p);
				throw new IOException (error);
			}
			else {
				params.put(name, value);
			}
		}	
	}
}
