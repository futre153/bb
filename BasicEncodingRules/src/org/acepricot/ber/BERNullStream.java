package org.acepricot.ber;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BERNullStream extends BERIOStream {

	@Override
	public void write(int i) throws IOException {
	}

	@Override
	public void write(byte[] b) throws IOException {
	}

	@Override
	public int read() throws IOException {
		return 0;
	}

	@Override
	public int read(byte[] b) throws IOException {
		return 0;
	}

	@Override
	public int read(byte[] b, int s, int l) throws IOException {
		return 0;
	}

	@Override
	public void setOutputStream(OutputStream out) {
	}

	@Override
	public void setInputStream(InputStream in) {
	}

	@Override
	public void close () throws IOException {
	}
	
}
