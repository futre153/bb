package org.pabk.transporter_dlx.core;

import java.util.Date;
import java.util.Properties;

public class SimpleTimer extends TimerEntryImpl {
	
	private long lastRun = -1;
	
	public long getNextRun() throws TimerException {
		long timeInterval = super.getNextRun();
		long time = new Date().getTime();
		if(lastRun < 0) {
			lastRun = time;
		}
		else {
			while (lastRun < time) {
				lastRun += timeInterval;
			}
		}
		return lastRun;
	}

	public long[] getNextRuns(int number) throws TimerException {
		long[] times = new long[number];
		times[0] = getNextRun();
		long timeInterval = super.getNextRun();
		for(int i = 1; i < number; i ++) {
			times[i] = times[i -1] + timeInterval;
		}
		return times;
	}

	public String loadTimer(Properties props) throws TimerException {
		String timerPrefix = Transporter.TIMER + "." + super.loadTimer(props) + ".";
		loadTimer(props, timerPrefix);
		return timerPrefix;
	}
	
	void loadTimer(Properties props, String timerPrefix) throws TimerException {
		setTimeInterval(Long.parseLong(props.getProperty(timerPrefix + TimerEntryImpl.INTERVAL, TimerEntryImpl.DEFAULT_INTERVAL)));
	}
}
