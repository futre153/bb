package org.pabk.transporter_dlx.core;

import java.util.Calendar;
import java.util.Properties;

abstract class TimerEntryImpl implements TimerEntry {
	
	private static final long MIN_TIME_INTERVAL = 1000 * 60;
	private static final long MAX_TIME_INTERVAL = 1000 * 60 * 60 * 24;
	private static final String TI_LESS = "Time interval cannot be less than %d";
	private static final String TI_GREATHER = "Time interval cannot be greather than %d";
	private static final String TI_NOT_DEF = "Time interval is not defined";
	private static final long DAY_BASE = 1000 * 60 * 60 * 24;
	private static final String CYCLING_ERROR = "Cycling problem when weekends checking runs";
	public static final String INTERVAL = "interval";
	public static final String DEFAULT_INTERVAL = Long.toString(MIN_TIME_INTERVAL);
	private static final String NO_TIMER_DEFINED = "No timer defined, thread %s will be stopped";
	private static int[] WEEKEND_DEF = new int[]{Calendar.SATURDAY, Calendar.SUNDAY};
	private long timeInterval = -1;
	private String name;
	
	public void setTimeInterval(long intervalInMillis) throws TimerException {
		if(intervalInMillis < MIN_TIME_INTERVAL) {
			throw new TimerException(String.format(TI_LESS, MIN_TIME_INTERVAL));
		}
		if(intervalInMillis > MAX_TIME_INTERVAL) {
			throw new TimerException(String.format(TI_GREATHER, MAX_TIME_INTERVAL));
		}
		timeInterval = intervalInMillis;
	}

	public long getTimeInterval() {
		return timeInterval;
	}

	public long getNextRun() throws TimerException {
		long interval = getTimeInterval();
		if(interval < 0) {
			throw new TimerException(TI_NOT_DEF);
		}
		return interval;
	}
	
	private static boolean isWeekendDay(long run) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(run);
		for(int i = 0; i < WEEKEND_DEF.length; i++) {
			if(c.get(Calendar.DAY_OF_WEEK) == WEEKEND_DEF[i]) {
				return true;
			}
		}
		return false;
	}
	
	protected long checkWeekends(long run) throws TimerException {
		for(int i = 0; i < Calendar.DAY_OF_WEEK; i ++) {
			if(isWeekendDay(run)) {
				run += DAY_BASE;
				continue;
			}
			return run;
		}
		throw new TimerException(CYCLING_ERROR);
	}
		
	public boolean isRunTime(long time) throws TimerException {
		long next = getNextRun();
		return time < next;
	}
	
	public String loadTimer(Properties props) throws TimerException {
		String timer = props.getProperty(Transporter.TIMER);
		if(timer == null) {
			throw new TimerException(String.format(NO_TIMER_DEFINED, props.getProperty(Transporter.NAME)));
		}
		setName(timer);
		return timer;
	}
	
	abstract void loadTimer(Properties props, String timerPrefix) throws TimerException;

	public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}
}
