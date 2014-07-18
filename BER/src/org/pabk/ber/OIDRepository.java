package org.pabk.ber;

import java.lang.reflect.Field;

class OIDRepository {
	
	static String[] repository=new String[] {
		"1|ISO|International Organization for Standardization (ISO)"
	};
	static String[] iso_1=new String[] {
		"3|Identified-Organization|Organization identification schemes registered according to ISO/IEC 6523-2"
	};
	static String[] identified_organization_2=new String[] {
		"6|dod|United States Department of Defense (DoD)"
	};
	static String[] dod_3=new String[] {
		"1|internet|Internet"	
	};
	static String[] internet_4=new String[] {
		"4|.private|Private projects"
	};
	static String[] _private_5=new String[] {
		"1|enterprise|Private enterprises"
	};
	static String[] enterprise_6=new String[] {
		"18494|swift|S.W.I.F.T. SCRL"	
	};
	static String[] swift_7=new String[] {
		"2|saa|Alliance Access"
	};
	static String[] saa_8=new String[] {
		"1|saatraps|Alliance Access traps message"	
	};
	static String[] saatraps_9=new String[] {
		"1|Istance|Unique identifier of the SAA instance",
		"2|Date|Date, expressed as dd/mm/yyyy",
		"3|Time|Time, expressed as hh:mm:ss",
		"4|Generated-by|Component (as per the event template)",
		"5|Event-number|32 bits number",
		"6|Event-severity|Severity (Fatal, Severe, Warning, or Information)",
		"7|Event-class|Class (Operator, Data, Backup/Restore, Restart/Stop, Update BIC, Process, System, Software, Message, Communication, Security, Network",
		"8|Event-name|Name (as per the event template)",
		"9|Event-description|The actual event text"
	};
	
	private OIDRepository() {}
	
	public static String[] getObject(int[] oid) {
		String[] retValue=null;
		if(oid!=null) {
			String fieldName="repository";
			try {
				Class<?> cl=Class.forName("org.pabk.ber.OIDRepository");
				for(int i=0;i<oid.length;i++) {
					retValue=null;
					Field fl=cl.getDeclaredField(fieldName);
					String[] tmp=(String[]) fl.get(null);
					fieldName=null;
					for(int j=0;j<tmp.length;j++) {
						String[] tmp1=tmp[j].split("\\|");
						if(tmp1[0].equals(Integer.toString(oid[i]))) {
							retValue=new String[]{tmp1[1].replace("\\.", ""),tmp1[2]};
							fieldName=tmp1[1].replace('-','_');
							fieldName=fieldName.replace('.', '_');
							fieldName+=("_"+(i+1));
							fieldName=fieldName.toLowerCase();
							break;
						}
					}
				}
			}
			catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return retValue;
	}
	
	
}
