package org.pabk.transporter_dlx.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class MultiTimer extends TimerEntryImpl {
	
	private static final String METHOD_NOT_IMPLEMENTED = "Method %s is not implemeted";
	private static final String MULTITIMER_ERROR = "No particular timer is definod for this multitimer";
	private List<TimerEntryImpl> watchDog;
	
	public long getNextRun() throws TimerException {
		long run = -1;
		for(int i = 0; i < watchDog.size(); i ++) {
			long r = watchDog.get(i).getNextRun();
			run = run < 0 ? r : (r < run ? r : run);
			if(run == r) {
				setName(watchDog.get(i).getName());
			}
		}
		return run;
	}
	
	public static long[] removeDuplicates(long[] a) {
		int x = a.length;
		for(int j = 1; j < x; j ++) {
			for(int y = 0; (j + y) < x; y ++) {
				if(a[j - 1] == (a[j + y])) {
					if((j + y) < (x - 1)) {
						continue;
					}
					else {
						y ++;
					}
				}
				if(y > 0) {
					System.arraycopy(a, j + y, a, j, x - j - y);
					x -= y;
					j --;
				}
				break;
			}
		}
		return Arrays.copyOf(a, x);
	}
	
	public long[] getNextRuns(int l) throws TimerException {
		long[] runs = new long[2 * l];
		System.arraycopy(watchDog.get(0).getNextRuns(l), 0, runs, 0, l);
		for(int i = 1; i < watchDog.size(); i ++) {
			System.arraycopy(watchDog.get(i).getNextRuns(l), 0, runs, l, l);
			Arrays.sort(runs);
			MultiTimer.removeDuplicates(runs);
		}
		return Arrays.copyOf(runs, l);
	}

	public String loadTimer(Properties props) throws TimerException {
		String[] timers = super.loadTimer(props).split(",");
		watchDog = new ArrayList<TimerEntryImpl>();
		for(int i = 0; i < timers.length; i ++) {
			try {
				String timerPrefix = Transporter.TIMER + "." + timers[i] + ".";
				TimerEntryImpl tei = (TimerEntryImpl) Class.forName(props.getProperty(timerPrefix + Transporter.TIMER_CLASS)).newInstance();
				tei.loadTimer(props, timerPrefix);
				tei.setName(timers[i]);
				watchDog.add(tei);
			}
			catch(Exception e) {}
		}
		if(watchDog.size() == 0) {
			throw new TimerException(MULTITIMER_ERROR);
		}
		return this.toString();
	}

	@Override
	void loadTimer(Properties props, String timerPrefix) throws TimerException {
		throw new TimerException(String.format(METHOD_NOT_IMPLEMENTED, "loadTimer(Properties props, String timerPrefix)"));		
	}
}
