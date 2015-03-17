package org.pabk.emanager.util;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class SimpleFileFilter implements FileFilter {
	
	private static final String DEFAULT_PATTERN = ".+";
	private Pattern pattern;
	
	public SimpleFileFilter (String filenamePattern) {
		this(filenamePattern, 0);
	}
	
	public SimpleFileFilter (String filenamePattern, int flags) {
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
		Matcher m1 = pattern.matcher(file.getAbsolutePath());
		Matcher m2 = pattern.matcher(file.getName()); 
		return file.isFile() && (m1.matches() || m2.matches());
	}
	
	public String toString() {
		return this.pattern.toString();
	}
	
}
