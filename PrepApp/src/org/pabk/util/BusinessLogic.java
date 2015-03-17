package org.pabk.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import org.apache.axiom.soap.SOAPEnvelope;

public class BusinessLogic {
  
	

	public static void process(SOAPEnvelope env, Exception fault) {
		try {
			File f;
			StringBuffer sb = new StringBuffer();
			sb.append(Const.REQUEST);
			sb.append(Const.LS);
			sb.append(Base64Coder.encodeString(env.toString()));
			sb.append(Const.LS);
			sb.append(Const.EXECUTION_TIME);
			sb.append(Const.LS);
			sb.append(Calendar.getInstance().getTimeInMillis());
			sb.append(Const.LS);
			sb.append(Const.FAULT);
			sb.append(Const.LS);
			if (fault != null) {
				sb.append(Base64Coder.encodeString(fault.getMessage()));
			}
			else sb.append(Const.NULL_FAULT_CODE);
			do f = new File(getName());  while (f.exists());
			f.createNewFile();
			FileOutputStream out = new FileOutputStream(f);
			out.write(sb.toString().getBytes());
			out.close();
		}	
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getName() throws Exception {
		String name = Const.get(Const.SCRAP_PATH_KEY) + Const.PS + Const.SCRAP_FILENAME + Const.SEPARATOR;
		String rnd = Integer.toString((int)(Math.random() * 10000.0D));
		name = name + "0000".substring(rnd.length()) + rnd + Const.SEPARATOR + Long.toString(Calendar.getInstance().getTimeInMillis());
		name = name + Const.EXTENTION;
		return name;
	}
}