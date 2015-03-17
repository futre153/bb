package org.pabk.transporter_dlx.core;

import java.util.Calendar;
import java.util.Properties;

public class BasedTimer extends TimerEntryImpl {

	private static final long HOUR_BASE = 60 * 60 * 1000;
	private static final String BTI_BASE_NOT_EXISTS = "Time base %d does not exists";
	private static final String BTI_BASE_NOT_DEFINED = "Time base does not defined yet";
	private static final String TIME_BASE = "timeBase";
	private static final int DEFAULT_TIME_BASE = -1;
	private static final String START_AT = "startAt";
	private static final int DEFAULT_ATART_AT = 0;
	private static final String CYCLES = "cycles";
	private static final int DEFAULT_CYCLES = 0;
	private static final String RUN_ON_WEEKEND = "runOnWeekend";
	private static final boolean DEFAULT_RUN_ON_WEEKEND = false;
	private static final String RUN_ON_HOLYDAYS = "runOnHolydays";
	private static final boolean DEFAULT_RUN_ON_HOLYDAYS = false;
	private int timeBase = DEFAULT_TIME_BASE;
	private long startAt = 0;
	private int cycles = 0;
	private boolean runOnWeekend = false;
	private boolean runOnHolydays = false;
		
	public BasedTimer() {
		
	}
	
	public BasedTimer(long intervalInMillis, int timeBase, long startAt, int cycles) throws TimerException {
		setTimeInterval(intervalInMillis, timeBase, startAt, cycles);
	}
	
	public void setTimeInterval(long setTimeInterval) throws TimerException {
		setTimeInterval (setTimeInterval, Calendar.DAY_OF_WEEK, 0, 0);
	}	
	
	public void setTimeInterval(long intervalInMillis, int timeBase) throws TimerException {
		setTimeInterval(intervalInMillis, timeBase, 0L, 0);
	}
	
	public void setTimeInterval(long intervalInMillis, int timeBase, long startAt) throws TimerException {
		setTimeInterval(intervalInMillis, timeBase, startAt, 0);
	}
	
	public String loadTimer(Properties props) throws TimerException {
		String timerPrefix = Transporter.TIMER + "." + super.loadTimer(props) + ".";
		loadTimer(props, timerPrefix);
		return timerPrefix;
	}
	
	void loadTimer(Properties props, String timerPrefix) throws TimerException {
		setTimeInterval(Long.parseLong(props.getProperty(timerPrefix + TimerEntryImpl.INTERVAL, TimerEntryImpl.DEFAULT_INTERVAL)));
		timeBase = Integer.parseInt(props.getProperty(timerPrefix + BasedTimer.TIME_BASE, Integer.toString(timeBase)));
		startAt = Long.parseLong(props.getProperty(timerPrefix + BasedTimer.START_AT, Long.toString(BasedTimer.DEFAULT_ATART_AT)));
		cycles = Integer.parseInt(props.getProperty(timerPrefix + BasedTimer.CYCLES, Integer.toString(BasedTimer.DEFAULT_CYCLES)));
		runOnWeekend = Boolean.parseBoolean(props.getProperty(timerPrefix + BasedTimer.RUN_ON_WEEKEND, Boolean.toString(BasedTimer.DEFAULT_RUN_ON_WEEKEND)));
		runOnHolydays = Boolean.parseBoolean(props.getProperty(timerPrefix + BasedTimer.RUN_ON_HOLYDAYS, Boolean.toString(BasedTimer.DEFAULT_RUN_ON_HOLYDAYS)));
	}
	
	public void setTimeInterval(long intervalInMillis, int timeBase, long startAt, int cycles) throws TimerException {
		this.cycles  = cycles;
		setTimeBase(timeBase);
		setFirstStartInBase(startAt);
		super.setTimeInterval(intervalInMillis);
	}
	
	private void setFirstStartInBase(long startAt) throws TimerException {
		this.startAt  = startAt % getTimeBaseValue();
	}
	
	private void setTimeBase(int timeBase) throws TimerException {
		switch(timeBase) {
		case Calendar.HOUR:
		case Calendar.DAY_OF_WEEK:
		case Calendar.WEEK_OF_MONTH:
			this.timeBase = timeBase;
			break;
		default:
			throw new TimerException(String.format(BTI_BASE_NOT_EXISTS, timeBase));
		}
	}
	
	private long getTimeBaseValue() throws TimerException {
		long base = HOUR_BASE;
		switch (timeBase) {
		case Calendar.WEEK_OF_MONTH:
			base *= 7;
		case Calendar.DAY_OF_WEEK:
			base *= 24;
		case Calendar.HOUR:
			return base;
		default:
			throw new TimerException(BTI_BASE_NOT_DEFINED);
		}
	}
	
	private long nextRun(long actual) throws TimerException {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(actual);
		switch(timeBase) {
		case Calendar.WEEK_OF_MONTH:
			c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		case Calendar.DAY_OF_WEEK:
			c.set(Calendar.HOUR_OF_DAY, 0);
		case Calendar.HOUR:
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			break;
		default:
			throw new TimerException(BTI_BASE_NOT_DEFINED);
		}
		long base = getTimeBaseValue();
		
		
		
		
		//System.out.println(new Date(actual) + ", " + new Date(c.getTimeInMillis()) + ", " + new Date(c.getTimeInMillis() + startAt));
		long start = c.getTimeInMillis() + (startAt < 0 ? 0 : startAt);
		long run = start;
		if(start <= actual) {
			int cycle = (int) ((actual - start) / getTimeInterval());
			//System.out.println(cycle);
			if(cycles > 0) {
				if(cycle < (cycles - 1)) {
					run += (cycle + 1) * getTimeInterval();
				}
				else {
					run += base;
				}
			}
			else {
				run += (cycle + 1) * getTimeInterval();
			}
		}
		if(!isRunOnWeekend()) {
			run = checkWeekends(run);
		}
		return run;
	}
	
	public long getNextRun() throws TimerException {
		Calendar c = Calendar.getInstance();
		return nextRun(c.getTimeInMillis());
		
	}

	public long[] getNextRuns(int number) throws TimerException {
		long[] times = new long[number];
		times[0] = getNextRun();
		for(int i = 1; i < number; i ++) {
			times[i] = nextRun(times[i - 1]);
		}
		return times;
	}

	public boolean isRunOnWeekend() {
		return runOnWeekend;
	}

	public void setRunOnWeekend(boolean runOnWeekend) {
		this.runOnWeekend = runOnWeekend;
	}

	public boolean isRunOnHolydays() {
		return runOnHolydays;
	}

	public void setRunOnHolydays(boolean runOnHolydays) {
		this.runOnHolydays = runOnHolydays;
	}

}
