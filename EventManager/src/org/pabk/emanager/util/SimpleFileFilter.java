package org.pabk.emanager.util;

import java.io.File;
import java.io.FileFilter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class SimpleFileFilter implements FileFilter {
	
	private static final String DEFAULT_PATTERN = ".+";
	private static final String TO_STRING = "%s and keep days = %d";
	private Pattern pattern;
	private int keepDays;
	private long delay;
	
	public SimpleFileFilter (String filenamePattern) {
		this(filenamePattern, 0, 0);
	}
	
	public SimpleFileFilter (String filenamePattern, int days) {
		this(filenamePattern, 0, days);
	}
		
	public SimpleFileFilter (String filenamePattern, int flags, int days) {
		keepDays = days;
		delay = ((long) days) * 24 * 60 * 60 * 1000;
		filenamePattern = filenamePattern == null ? DEFAULT_PATTERN : filenamePattern;
		flags = flags < 0x00 || flags > 0x1FF ? 0x00 : flags;
		try {
			pattern = Pattern.compile(filenamePattern, flags);
		}
		catch (PatternSyntaxException e) {
			pattern = Pattern.compile(DEFAULT_PATTERN);
		}
		
	}
		
	@Override
	public boolean accept(File file) {
		long actual = new Date().getTime();
		Matcher m1 = pattern.matcher(file.getAbsolutePath());
		Matcher m2 = pattern.matcher(file.getName()); 
		return file.isFile() && (m1.matches() || m2.matches()) && ((delay < (actual - file.lastModified())) || (delay <= 0));
	}
	
	public String toString() {
		return String.format(TO_STRING, this.pattern.toString(), keepDays);
	}
	
}
