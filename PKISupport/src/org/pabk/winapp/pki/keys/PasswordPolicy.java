package org.pabk.winapp.pki.keys;

public interface PasswordPolicy {
	boolean checkPassword(char[] password);
	String getErrorMessage();
}
