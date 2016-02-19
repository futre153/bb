package org.pabk.util.interpreter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public abstract class InterpreterImpl implements Interpreter {
	
	private static final String LANGUAGE_NOT_INITIALIZED = "Language for interpreter is not initialized";
	private static Language lang = null;
	private Charset outputCharset = Charset.defaultCharset();
	private Charset inputCharset = Charset.defaultCharset();
	private String message;

	@Override
	final public void interpret(InputStream in, OutputStream out) {
		try {
			if(lang == null) {
				throw new IOException (LANGUAGE_NOT_INITIALIZED);
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, getInputCharset()));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, getOutputCharset()));
			String line = reader.readLine();
			String key = InterpreterImpl.getKeyword(line, lang);
			writer.write(line);
		}
		catch (Exception e) {
			e.printStackTrace();
			this.setMessage(e.getLocalizedMessage());
		}
	}

	private static String getKeyword(String line, Language lang) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Charset getInputCharset() {
		return inputCharset;
	}

	public void setInputCharset(String inputCharset) {
		try {
			this.inputCharset = Charset.forName(inputCharset);
		}
		catch(Exception e) {
			e.printStackTrace();
			this.setMessage(e.getLocalizedMessage());
		}
	}

	public Charset getOutputCharset() {
		return outputCharset;
	}

	public void setOutputCharset(String outputCharset) {
		try {
			this.outputCharset = Charset.forName(outputCharset);
		}
		catch(Exception e) {
			e.printStackTrace();
			this.setMessage(e.getLocalizedMessage());
		}
	}

}
