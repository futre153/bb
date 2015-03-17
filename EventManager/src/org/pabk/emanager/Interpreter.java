package org.pabk.emanager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.pabk.emanager.cmd.Argument;
import org.pabk.emanager.cmd.ArgumentImpl;
import org.pabk.emanager.cmd.Command;
import org.pabk.emanager.cmd.CommandImpl;
import org.pabk.emanager.exc.SyntaxErrorException;


public class Interpreter extends HandlerImpl {
	
	private static final ArrayList<Entry> list=new ArrayList<Entry>();
	private static Interpreter interpreter;
	
	
	class Msgs {

		public static final String WELCOME = "Event Distribution Application v1.0 - Welcome";
		public static final String INACTIVITY_TIMEOUT = "has been closed due to inactivity reason";
		public static final String SHUTDOWN_WARNING = "Application will be closed, please disconnect session";
		
	}
	
	private class Arg {
		private Arg(String token) {
			arg=token;
		}
		private String arg;
		private ArrayList<String> params=new ArrayList<String>();
	}
	
	private class Cmd {
		private Cmd(String cmd) {
			this.cmd=cmd;
		}
		private int index=0;
		private String key;
		private String cmd;
		private ArrayList<Arg> args=new ArrayList<Arg>();
	}
	
	public void init(Object[] args) {
		super.init(args);
		interpreter=this;
	}
	
	@Override
	public void businessLogic() {
		
		//System.out.println("Interpreter\r\n"+pro);
		
		while(!shutdown) {
			EventManager.getEventManager();
			if(list.size()>0) {
				if(shutdown)break;
				interpret(list.remove(0));
			}
			if(shutdown)break;
			sleep=new Sleeper();
			log.info("Module "+this.getClass().getName()+" goes to SLEEP");
			sleep.sleep(0);
			log.info("Module "+this.getClass().getName()+" WAKE UP now");
		}
	}
	
	public synchronized final void addEntry(Connection con, String cmd) {
		Interpreter.list.add(new Entry(con,cmd));
	}

	public synchronized void sleep() {
		try {
			sleep.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void interpret(Entry entry) {
		Cmd cmd=new Cmd(entry.getCmd().trim());
		String token=getNextToken(cmd);
		if(token!=null) {
			cmd.key=token;
			try {
				while(true) {
					Arg arg=getNextArgument(cmd);
					if(arg!=null) {cmd.args.add(arg);}else {break;}
				}
				String[] msg=execute(cmd,entry);
				if(msg!=null) {
					for(int i=0;i<msg.length;i++) {
						entry.getCon().send(msg[i]);
					}
					entry.getCon().send("OK");
				}
			}
			catch (SyntaxErrorException e) {
				entry.getCon().sendError(e);
			}
		}
	}

	private String[] execute(Cmd cmd, Entry entry) throws SyntaxErrorException {
		Command key=CommandImpl.getCommand(cmd.key);
		ArrayList<Argument> args=new ArrayList<Argument>();
		for(int i=0;i<cmd.args.size();i++) {
			Argument arg=ArgumentImpl.getArgument(cmd.args.get(i).arg);
			args.add(arg);
			arg.setArgument(cmd.args.get(i).params);
		}
		args=key.setArguments(args);
		Object[] objs=key.checkArguments(args);
		Object[] objs2=new Object[objs.length+1];
		Class<?>[] param=new Class<?>[objs.length+1];
		for(int i=0;i<objs.length;i++) {
			param[i]=objs[i].getClass();
			objs2[i]=objs[i];
		}
		param[param.length-1]=entry.getClass();
		objs2[objs2.length-1]=entry;
		String methodName="invoke"+key.getClass().getSimpleName().substring(0,1).toUpperCase()+key.getClass().getSimpleName().substring(1).toLowerCase();
		Method method;
		try {
			method = this.getClass().getMethod(methodName, param);
			return (String[]) method.invoke(this, objs2);
		}
		catch (NoSuchMethodException | SecurityException | 
				IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			//e.printStackTrace();
			throw new SyntaxErrorException(e.getMessage());
		}
	}

	public final String[] invokeExit(Entry entry) {
		entry.getCon().send("OK");
		entry.getCon().close();
		return null;
	}
	
	public final String[] invokeShutdown(Entry entry) {
		EventManager em=EventManager.getEventManager();
		//entry.getCon().send("OK");
		ArrayList<HandlerImpl> mods=em.getMods();
		for(int i=0;i<mods.size();i++) {
			HandlerImpl mod=mods.get(i);
			//System.out.println(mod.getClass().getName());
			mod.shutdown=true;
			if(mod.sleep!=null)	mod.sleep.wakeup();
		}
		em.shutdown=true;
		return null;
	}
	
	private Arg getNextArgument(Cmd cmd) throws SyntaxErrorException {
		Arg arg=null;
		String token=this.getNextToken(cmd);
		if(token!=null) {
			if(!isArgument(token)) {
				throw new SyntaxErrorException("Syntax error on "+cmd.index);
			}
			arg=new Arg(token);
			while(true) {
				int i=cmd.index;
				token=this.getNextToken(cmd);
				if(token!=null) {
					if(!isArgument(token)) {
						arg.params.add(token);
					}
					else {
						cmd.index=i;
						break;
					}
				}
				else{
					break;
				}
			}
		}
		return arg;
	}

	private boolean isArgument(String token) {
		return token.charAt(0)=='-';
	}

	private String getNextToken(Cmd cmd) {
		String retValue=null;
		if(cmd.index<cmd.cmd.length()) {
			int i=cmd.cmd.indexOf(' ',cmd.index);
			if(i<0) {
				retValue=cmd.cmd.substring(cmd.index);
				cmd.index=cmd.cmd.length();
			}
			else {
				retValue=cmd.cmd.substring(cmd.index,i);
				cmd.index=i+1;
			}
		}
		//System.out.println("'"+retValue+"',"+cmd.index);
		return retValue;
	}

	public static Interpreter getInterpreter() {
		return interpreter;
	}
	
}
