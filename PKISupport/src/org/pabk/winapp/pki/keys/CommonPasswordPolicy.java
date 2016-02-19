package org.pabk.winapp.pki.keys;

import java.util.Arrays;

public class CommonPasswordPolicy implements PasswordPolicy {
	
	private static final int DEFAULT_MIN_LENGTH = 8;
	private static final int DEFAULT_MAX_LENGTH = 32;
	private static final boolean DEFAULT_LOWERCASE = true;
	private static final boolean DEFAULT_UPPERCASE = true;
	private static final boolean DEFAULT_DIGIT = true;
	/*
	private static final String NULL_VALUE = "Password cannot have a null value";
	private static final String MIN_VALUE = "Password must have at least %d characters";
	private static final String MAX_VALUE = "Password cannot have more than %d characters";
	private static final String LOWER_CASE = "Password must have at least one lower case character";
	private static final String UPPER_CASE = "Password must have at least one upper case character";
	private static final String DIGIT = "Password must have at least one digit character";
	private static final String SPECIAL = "Password must have at least one special character. Allowed are %s";
	private static final String ALLOWED = "Character %s is not allowed";
	*/
	private static final String NULL_VALUE = "Heslo nesmie maù nulov˙ hodnotu";
	private static final String MIN_VALUE = "Heslo musÌ obsahovaù prinajmenöom %d znakov";
	private static final String MAX_VALUE = "Heslo musÌ maù menej ako %d znakov";
	private static final String LOWER_CASE = "Heslo musÌ obsahovaù aspoÚ jeden mal˝ abecedn˝ znak (a-z)";
	private static final String UPPER_CASE = "Heslo musÌ obsahovaù aspoÚ jeden veæk˝ abecedn˝ znak (A-Z)";
	private static final String DIGIT = "Heslo musÌ obsahovaù aspoÚ jeden ËÌseln˝ znak (0-9)";
	private static final String SPECIAL = "Heslo musÌ obsahovaù aspoÚ jeden öpeci·lny znak." + System.getProperty("line.separator") + "PovolenÈ znaky s˙ %s";
	private static final String ALLOWED = "Znak %s nie je povolen˝";
	private int minLength = DEFAULT_MIN_LENGTH;
	private int maxLength = DEFAULT_MAX_LENGTH;
	private boolean lowerCase = DEFAULT_LOWERCASE;
	private boolean upperCase = DEFAULT_UPPERCASE;
	private boolean digit = DEFAULT_DIGIT;
	private char[] specials;
	private int[][] allowed;
	private String msg = null;
	
	public CommonPasswordPolicy (int minLength, int maxLength, boolean lowerCase, boolean upperCase, boolean digit, String special, int ... alls) {
		this.setMinLength(minLength);
		this.setMaxLength(maxLength);
		this.setLowerCase(lowerCase);
		this.setUpperCase(upperCase);
		this.setDigit(digit);
		if(special != null) {
			this.setSpecials(special.toCharArray());
		}
		this.setAllowed(alls);
	}
	
	@Override
	public boolean checkPassword(char[] password) {
		this.setMessage(null);
		if(password == null) {
			this.setMessage(NULL_VALUE);
			return false;
		}
		if(password.length < this.getMinLength()) {
			this.setMessage(String.format(MIN_VALUE, this.getMinLength()));
			return false;
		}
		if(password.length > this.getMaxLength()) {
			this.setMessage(String.format(MAX_VALUE, this.getMaxLength()));
			return false;
		}
		if(!this.isLowerCase(password)) {
			this.setMessage(LOWER_CASE);
			return false;
		}
		if(!this.isUpperCase(password)) {
			this.setMessage(UPPER_CASE);
			return false;
		}
		if(!this.isDigit(password)) {
			this.setMessage(DIGIT);
			return false;
		}
		if(!this.isSpecial(password)) {
			this.setMessage(String.format(SPECIAL, Arrays.toString(specials)));
			return false;
		}
		if(!this.isAllowed(password)) {
			this.setMessage(String.format(ALLOWED, this.getErrorMessage()));
			return false;
		}
		return true;
	}

	@Override
	public String getErrorMessage() {
		return msg;
	}
	
	private void setMessage(String msg) {
		this.msg = msg;
	}
	
	public int getMinLength() {
		return minLength;
	}

	public void setMinLength(int minLength) {
		this.maxLength = this.maxLength < minLength ? minLength : this.maxLength;
		this.minLength = minLength;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.minLength = this.minLength > maxLength ? maxLength : this.minLength;
		this.maxLength = maxLength;
	}

	private boolean isLowerCase(char[] password) {
		if(this.lowerCase) {
			for(char c: password) {
				if(Character.isLowerCase(c)) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	public void setLowerCase(boolean lowerCase) {
		this.lowerCase = lowerCase;
	}

	private boolean isUpperCase(char[] password) {
		if(this.upperCase) {
			for(char c: password) {
				if(Character.isUpperCase(c)) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	public void setUpperCase(boolean upperCase) {
		this.upperCase = upperCase;
	}

	private boolean isDigit(char[] password) {
		if(this.digit) {
			for(char c: password) {
				if(Character.isDigit(c)) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	public void setDigit(boolean digit) {
		this.digit = digit;
	}

	private boolean isSpecial(char[] password) {
		if(this.specials != null) {
			for(char c: password) {
				for(char e: specials) {
					if(e == c) {
						return true;
					}
				}
			}
			return false;
		}
		return true;
	}

	public void setSpecials(char[] chars) {
		if (chars.length > 0) {
			Arrays.sort(chars);
			specials = chars;
		}
		else {
			specials = null; 
		}
	}

	public boolean isAllowed(char[] password) {
		if(this.allowed != null) {
			for(int[] interval : this.allowed) {
				if(interval.length == 2) {
					for(char c : password) {
						if(c < interval[0] || c > interval[1]) {
							this.setMessage(new String(new char[]{c}));
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	public void setAllowed(int... alls) {
		if(alls != null && alls.length > 0 && (alls.length % 2) == 0) {
			this.allowed = new int[alls.length / 2][2];
			for(int i = 0; i < alls.length; i += 2) {
				this.allowed[i / 2][0] = alls[i];
				this.allowed[i / 2][1] = alls[i + 1];
			}
		}
		else {
			this.allowed = null;
		}
	}

}
