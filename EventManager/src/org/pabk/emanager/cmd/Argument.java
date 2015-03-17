package org.pabk.emanager.cmd;

import java.util.ArrayList;

public interface Argument {
	String[] getSynonyms();

	void setArgument(ArrayList<String> params);

	Object getValue();
}
