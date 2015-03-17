package org.pabk.emanager.cmd;

import java.util.ArrayList;

import org.pabk.emanager.exc.SyntaxErrorException;


public interface Command {
	String[] getSynonyms();
	ArrayList<Argument> setArguments(ArrayList<Argument> args) throws SyntaxErrorException;
	Object[] checkArguments(ArrayList<Argument> args) throws SyntaxErrorException;
}
