package org.pabk.emanager.cmd;

import org.pabk.emanager.exc.SyntaxErrorException;

public abstract class ArgumentImpl implements Argument {
	protected String[] syns;
	protected Object[] value;
	private static final Argument[] args=new ArgumentImpl[] {
		
	};
	
	public static final Argument getArgument(String token) throws SyntaxErrorException {
		for(int i=0;i<args.length;i++) {
			if(args[i].getClass().getSimpleName().toLowerCase().equals(token.toLowerCase())) {
				try {return args[i].getClass().newInstance();}
				catch (InstantiationException | IllegalAccessException e) {
					throw new SyntaxErrorException("Failed to create arguments");
				}
			}
			for(int j=0;j<args[i].getSynonyms().length;j++) {
				if(token.toLowerCase().matches(args[i].getSynonyms()[j])) {
					try {return args[i].getClass().newInstance();}
					catch (InstantiationException | IllegalAccessException e) {
						throw new SyntaxErrorException("Failed to create arguments");
					}
				}
			}
		}
		throw new SyntaxErrorException("Unknown argument - "+token); 
	}
	public String[] getSynonyms() {return syns;}
	
}
