package org.pabk.transporter_dlx.core;

import java.util.Properties;

interface TimerEntry {
	void setTimeInterval(long intervalInMillis) throws TimerException;
	long getTimeInterval();
	long getNextRun() throws TimerException;
	long[] getNextRuns(int i) throws TimerException;
	boolean isRunTime(long time) throws TimerException;
	String loadTimer(Properties props) throws TimerException;
	String getName();
}
