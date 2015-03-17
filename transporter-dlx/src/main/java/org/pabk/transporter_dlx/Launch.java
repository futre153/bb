package org.pabk.transporter_dlx;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.pabk.transporter_dlx.core.BasedTimer;
import org.pabk.transporter_dlx.core.MultiTimer;
import org.pabk.transporter_dlx.core.TimerException;

public class Launch {
	
	public static void main(String[] args) throws TimerException {
		long[] l = new long[]{new Long(1),new Long(0),new Long(3),new Long(0),new Long(0),new Long(0),new Long(0),new Long(0)};
		System.out.println(Arrays.toString(l));
		long[] s = MultiTimer.removeDuplicates(l);
		System.out.println(Arrays.toString(l));
		System.out.println(Arrays.toString(s));
		long[] x = new long[s.length];
		System.arraycopy(s, 0, x, 0, x.length);
		System.out.println(Arrays.toString(x));
		System.exit(1);
		BasedTimer bt = new BasedTimer (10*60*1000, Calendar.WEEK_OF_MONTH, 1000*60*60*24*4 + 1000*60*60*16 + 1000*60*40, 2);
		System.out.println(new Date() + ", " + new Date().getTime() + "\r\n");
		long[] t = bt.getNextRuns(10);
		for(int i = 0; i < t.length; i ++) {
			System.out.println(new Date(t[i]) + ", " + t[i]);
		}
	}
}
