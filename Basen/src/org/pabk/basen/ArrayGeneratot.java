package org.pabk.basen;


public class ArrayGeneratot {

	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < 16; i ++) {
			if(i > 0) {
				sb.append(System.getProperty("line.separator"));
			}
			for (int j = 0; j < 16; j ++) {
				if(j > 0) {
					sb.append(' ');
				}
				sb.append("0x00");
				sb.append(Integer.toHexString(i));
				sb.append(Integer.toHexString(j));
				sb.append(',');
			}
		}
		System.out.println(sb);
	}

}
