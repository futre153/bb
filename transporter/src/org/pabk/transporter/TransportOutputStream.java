package org.pabk.transporter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

public class TransportOutputStream extends ByteArrayOutputStream {

	private static final int MAX_TEXT_LENGTH = 50000;
	private static final double TEXT_KEEP_RATIO = 4D / 5D;
		
	private JTextArea textArea;
	private Charset charset;
	
	public TransportOutputStream(JTextArea textArea, String charset) {
		super();
        this.textArea = textArea;
        if(charset == null) {
        	this.charset = Charset.defaultCharset();
        }
        else {
        	this.charset = Charset.forName(charset);
        }
    }
	
	public void flush() throws IOException {
		super.flush();
	}
	
		
	public void write(byte[] b, int o, int l) {
		super.write(b, o, l);
		if(this.size() > 50) {
			print();
		}
	}
	
	public void print() {
		//Transporter.getErr().println(textArea.getDocument().getLength());
		if(this.size() > 0) {
			removeOldLines();
			String text = new String(this.toByteArray(), charset);
			textArea.append(text);
	        textArea.setCaretPosition(textArea.getDocument().getLength());
			this.reset();
		}
	}
	
	
	
	private void removeOldLines() {
		if(textArea.getDocument().getLength() > MAX_TEXT_LENGTH) {
			try {
				textArea.getDocument().remove(0, (int) (textArea.getDocument().getLength() - MAX_TEXT_LENGTH * TEXT_KEEP_RATIO));
			} catch (BadLocationException e) {}
		}
	}
}
