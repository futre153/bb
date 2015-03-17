package org.pabk.emanager.cmd;

import java.util.ArrayList;

import org.pabk.emanager.exc.SyntaxErrorException;

public abstract class CommandImpl implements Command {
	protected String[] syns;
	protected Argument[] args;
	protected char[][] mand;
	private static final Command[] cmds=new CommandImpl[] {
		new Exit(),
		new Shutdown()
	};
	
	public static final Command getCommand(String token) throws SyntaxErrorException {
		for(int i=0;i<cmds.length;i++) {
			if(cmds[i].getClass().getSimpleName().toLowerCase().equals(token.toLowerCase())) {
				return cmds[i];
			}
			for(int j=0;j<cmds[i].getSynonyms().length;j++) {
				if(token.toLowerCase().matches(cmds[i].getSynonyms()[j])) {
					return cmds[i];
				}
			}
		}
		throw new SyntaxErrorException("Unknown command - "+token); 
	}
	
	public String[] getSynonyms() {return syns;}
	
	public ArrayList<Argument> setArguments(ArrayList<Argument> args) throws SyntaxErrorException{
		ArrayList<Argument> objs=new ArrayList<Argument>();
		for(int i=0;i<this.args.length;i++) {
			for(int j=0;j<args.size();j++) {
				Argument arg=args.get(j);
				if(arg.getClass().getName().equals(this.args[i].getClass().getName())) {
					objs.add(args.remove(j));
					break;
				}
			}
			args.add(null);
		}
		if(args.size()>0) {
			throw new SyntaxErrorException("Argument "+args.get(0).getClass().getSimpleName()+
				" is not supported for command "+this.getClass().getSimpleName());
		}
		return objs;
	}
	
	public Object[] checkArguments(ArrayList<Argument> args)  throws SyntaxErrorException{
		SyntaxErrorException e = null;
		Object[] objs=new Object[]{};
		for(int i=0;i<mand.length;i++) {
			objs=new Object[args.size()];
			e=null;
			for(int j=0;j<mand[i].length;j++) {
				Argument arg=args.get(j);
				if(arg!=null && mand[i][j]=='F') {
					e=new SyntaxErrorException("Argument "+arg.getClass().getSimpleName()+" is not allowed");
					break;
				}
				else if (arg==null && mand[i][j]=='M') {
					e=new SyntaxErrorException("Argument "+this.args[j].getClass().getSimpleName()+" is mandatory");
					break;
				}
				objs[j]=(arg==null?null:arg.getValue());
			}
			if(e==null) {
				break;
			}
		}
		if(e!=null){throw e;}
		return objs;
	}
	
}
