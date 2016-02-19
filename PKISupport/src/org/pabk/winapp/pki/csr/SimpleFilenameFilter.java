package org.pabk.winapp.pki.csr;

import java.io.File;
import java.io.FilenameFilter;

public class SimpleFilenameFilter implements FilenameFilter {
	
	private String mask;

	public SimpleFilenameFilter(String mask) {
		this.mask = mask;
	}

	@Override
	public boolean accept(File dir, String name) {
		return name.matches(mask);
	}

}
