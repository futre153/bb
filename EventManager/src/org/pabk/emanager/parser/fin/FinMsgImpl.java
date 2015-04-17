package org.pabk.emanager.parser.fin;


public abstract class FinMsgImpl implements Message {
	
	abstract void parseBasicHeader();
	abstract void parseAppHeader();
	abstract void parseUserHeader();
	abstract void parseText();
	abstract void parseTrailer();
	
	

}
