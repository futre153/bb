package org.pabk.proxyforwarder;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

import org.pabk.net.http.HttpClientConst;
import org.pabk.net.http.auth.BBAuthenticator;

public class Listener extends Thread{

	private static final ArrayList<Listener> list = new ArrayList<Listener>();
	//private static final String PABK_PROXY = "proxy.pabk.sk";
	//private static final int PABK_PORT = 3128;
	private Socket socket, pocket;
	private InputStream in, pin;
	private OutputStream out, pout;
	private static final byte[] CRLF = new byte[]{13,10};
	private static final String TRANSFER_ENCODINGS = "Transfer-Encoding";
	private static final String CHUNKED = "chunked";
	private static final String CONNECT = "CONNECT";
	private static final String HEAD = "HEAD";
	private static final String LOG_NAME = "log_";
	private static final String LOG_EXT = ".log";
	private static final String LOG_END_EXT = ".txt";
	private OutputStream log;
	private File logFile;
	
	Listener(Socket s) {
		try {
			this.socket = s;
			in = socket.getInputStream();
			out = socket.getOutputStream();
			openProxy();
			Listener.list.add(this);
			if(ProxyForwarder.debug) {
				File f = new File(ProxyForwarder.logPath + System.getProperty("file.separator") + LOG_NAME + this.getName() + LOG_EXT);
				if(f.exists()) {
					f.delete();
				}
				logFile = f;
				log = new FileOutputStream(f);
			}
			log(this.getName() + " was opened");
		}
		catch(Exception e) {
			close();
		}
	}
	
	private void log(String string) {
		if(ProxyForwarder.debug) {
			String ext = " on " + this.getName() + " on request nr. " + count;
			try {
				log.write(string.getBytes());
				log.write(ext.getBytes());
				log.write(CRLF);
			}
			catch(Exception e) {
				System.err.println("Log is broken. Application will be stopped");
				System.exit(1);
			}
		}
	}

	private void request() throws Exception {
		ArrayList<String> request = null;
		ArrayList<String> response = null;
		request = readWriteHeader(in, pout, true);
		readWriteContent(in, pout, request, true);
		String method = request.get(0).split(" ")[0].trim();
		response = readWriteHeader(pin, out, false);
		if(!method.equals(HEAD)) {
			readWriteContent(pin, out, response, false);
		}
		String con = findValueOfHeader("Proxy-Connection", response);
		if(con!=null && con.equals("close")) {
			log("PROXY reset connection");
			close();
			throw new IOException("PROXY reset connection");
		}
		int rs = Integer.parseInt(response.get(0).split(" ")[1].trim());
		if(rs == 200 && method.equals(CONNECT)) {
			Thread t1 = new InOutStream(in, pout);
			Thread t2 = new InOutStream(pin, out);
			t1.start();
			t2.start();
			t1.join();
			t2.join();
			close();
		}
	}
	
	
	private void checkProxyInput() throws IOException {	
		checkProxy();
		checkPInput();
	}

	private void checkProxyOutput() throws IOException {
		checkProxy();
		checkPOutput();
	}

	private void checkClientInput() throws IOException {	
		checkClient();
		checkCInput();
	}

	private void checkClientOutput() throws IOException {
		checkClient();
		checkCOutput();
	}
	
	private void checkPInput() throws IOException {
		if(pocket.isInputShutdown()) {
			log("Proxy Input is down");
			throw new IOException("Proxy Input is down");
		}
	}
	
	private void checkPOutput() throws IOException {
		if(pocket.isOutputShutdown()) {
			log("Proxy Output is down");
			throw new IOException("Proxy Output is down");
		}
	}
	
	private void checkCOutput() throws IOException {
		if(socket.isOutputShutdown()) {
			log("Client Output is down");
			throw new IOException("Client Output is down");
		}
	}
	
	private void checkCInput() throws IOException {
		if(socket.isInputShutdown()) {
			log("Client Input is down");
			throw new IOException("Client Input is down");
		}
	}
	
	private void checkClient() throws IOException {
		if(socket.isClosed()) {
			log("Client socket is closed");
			throw new IOException("Client socket is closed");
		}
	}
	
	private void checkProxy() throws IOException {
		if(pocket.isClosed()) {
			log("Proxy socket is closed");
			throw new IOException("Proxy socket is closed");
		}
	}
	
	
	private boolean checkAll() {
		boolean proxy = true;
		try {checkProxy();} catch(IOException e) {proxy = false;}
		if(proxy) {
			try {checkPInput();} catch(IOException e) {proxy = false;}
			try {checkPOutput();} catch(IOException e) {proxy = false;}
		}
		boolean client = true;
		try {checkClient();}
		catch (IOException e) {client = false;}
		if(client) {
			try {checkCInput();} catch (IOException e) {client = false;}
			try {checkCOutput();} catch (IOException e) {client = false;}
		}
		return client && proxy;
	}
	
	private ArrayList<String> readWriteHeader(InputStream ins, OutputStream outs, boolean request) throws Exception {
		if(request) {
			checkProxyOutput();
			checkClientInput();
		}
		else {
			checkProxyInput();
			checkClientOutput();
		}
		ArrayList<String> header = new ArrayList<String>();
		//BufferedReader bin = new BufferedReader(new InputStreamReader(ins));
		String line = null;
		while(true) {
			//line = bin.readLine();
			line = Listener.readLine(ins);
			if(line == null) {
				if(request) {
					log("Client CLOSED connection on " + this.getName() + " on request nr. " + count);
				}
				else {
					log("Proxy CLOSED connection on " + this.getName() + " on request nr. " + count);
					
				}
				throw new IOException("InputStream is EOF");
				
			}
			if(line.isEmpty()) {
				break;
			}
			header.add(line);
			outs.write(line.getBytes());
			outs.write(CRLF);
		}
		if(request) {
			URL url;
			try {
				url = new URL(header.get(0).split(" ")[1]);
			}
			catch (Exception e) {
				url = new URL("https://" + header.get(0).split(" ")[1]);
			}
			String service = HttpClientConst.get(HttpClientConst.PROXY_SERVICE_KEY);
			String spnego = HttpClientConst.PROXY_AUTHENTICATION + ": Negotiate " + BBAuthenticator.getSpnego(url, service);
			header.add(spnego);
			outs.write(spnego.getBytes());
			outs.write(CRLF);
		}
		outs.write(CRLF);
		log(header.toString());
		outs.flush();
		return header;
	}
	
	private static String readLine(InputStream ins) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int h = -1, l = -1;
		boolean crlf = false;
		while(!crlf) {
			l = ins.read();
			if(l < 0) {
				break;
			}
			out.write(l);
			crlf = (l == CRLF[1] && h == CRLF[0]);
			h = l;
		}
		if(crlf) {
			return new String(out.toByteArray(), 0, out.size() - 2);
		}
		if(out.size() > 0) {
			return new String(out.toByteArray(), 0, out.size());
		}
		return null;
	}
	
	
	private void openProxy() throws IOException {
		this.pocket = new Socket(ProxyForwarder.proxy, ProxyForwarder.port);
		pin = pocket.getInputStream();
		pout = pocket.getOutputStream();
	}
	
	private class InOutStream extends Thread {
		private InputStream in;
		private OutputStream out;
		private InOutStream(InputStream in, OutputStream out) {
			this.in = in;
			this.out = out;
		}
		public void run() {
			try {
				int b;
				while((b = this.in.read()) >= 0) {
					this.out.write(b);
				}
			}
			catch(Exception e) {
				
			}
		}
	}
	
	
	private void close() {
		closeObject(in);
		closeObject(out);
		closeObject(pin);
		closeObject(pout);
		closeObject(socket);
		closeObject(pocket);
	}

	private void closeObject(Closeable closeable) {
		try {closeable.close();} catch (IOException e) {} finally {closeable = null;}
	}

	
	void shutdown() {
		close();
	}
	
	private int count = 0;
	
	
	
	
	
	private void readWriteContent(InputStream ins, OutputStream outs, ArrayList<String> header, boolean request) throws Exception {
		if(request) {
			checkProxyOutput();
			checkClientInput();
		}
		else {
			checkProxyInput();
			checkClientOutput();
		}
		int l = getContentLength(header);
		if(request) {
			log("REQUEST Content length is " + l);
		}
		else {
			log("RESPONSE Content length is " + l);
		}
		if(l >= 0) {
			readWrite(ins, outs, l, request);
		}
		else {
			BufferedReader bin = new BufferedReader(new InputStreamReader(ins));
			while(true) {
				String line = bin.readLine();
				l = getChunkSize(line);
				if(l < 0) {
					throw new IOException("Invalid chunk");
				}
				outs.write(line.getBytes());
				outs.write(CRLF);
				readWrite(ins, outs, l, request);
				if(l <= 0) {
					line = bin.readLine();
					outs.write(line.getBytes());
					outs.write(CRLF);
					if(!line.isEmpty()) {
						line = bin.readLine();
						if(!line.isEmpty()) {
							log("Invalid end of chunked encodings");
							throw new IOException("Invalid end of chunked encodings");
						}
						outs.write(CRLF);
					}
					if(request) {
						log("get REQUEST chunked content");
					}
					else {
						log("get RESPONSE chunked content");
					}
					break;
				}
				outs.flush();
			}
		}
		//return out.toByteArray();
	}
	
	private int getChunkSize(String line) throws Exception {
		int l = line.indexOf(';');
		if(l < 0) {
			l = line.length();
		}
		return Integer.parseInt(line.substring(0, l), 16);
	}

	private void readWrite(InputStream ins, OutputStream outs, int l, boolean request) throws IOException {
		while(l > 0) {
			byte[] b = new byte[l];
			if(request) {
				log("REQUEST - wait for read (" + l + ")");
			}
			else {
				log("RESPONSE - wait for read (" + l + ")");
			}
			int i = ins.read(b);
			if(i < 0) {
				if(request) {
					log("failed to read REQUEST content");
				}
				else {
					log("failed to read RESPONSE content");
				}
				throw new IOException("failed to read content");
			}
			outs.write(b, 0, i);
			outs.flush();
			l -= i;
			if(request) {
				log("REQUEST content: read " + i + "bytes, " + l + "remaining.");
			}
			else {
				log("RESPONSE content: read " + i + "bytes, " + l + "remaining.");
			}
		}
		if(request) {
			log("get REQUEST content");
		}
		else {
			log("get RESPONSE content");
		}
	}
	
	
	private int getContentLength(ArrayList<String> request) throws Exception {
		int l = 0;
		String value = findValueOfHeader(HttpClientConst.CONTENT_LENGTH, request);
		if(value != null) {
			l = Integer.parseInt(value);
		}
		value = findValueOfHeader(TRANSFER_ENCODINGS, request);
		if(value != null  && value.equals(CHUNKED)) {
			l = -1;
		}
		return l;
	}

	private String findValueOfHeader(String name, ArrayList<String> header) {
		for(int i = 0; i < header.size(); i++) {
			int index = header.get(i).indexOf(':');
			if(index < 0) {
				continue;
			}
			String[] a = header.get(i).split(":", 2);
			if(a[0].contains(name)) {
				return a[1].trim();
			}
		}
		return null;
	}

	
	public void run() {
		while(checkAll()) {
			try {
				request();
			}
			catch (Exception e) {
				log("Error procesing request");
			}
			finally {
				close();
			}
		}
		Listener.getListeners().remove(this);
		log(this.getName() + " was closed");
		if(ProxyForwarder.debug) {
			closeObject(log);
			File f = new File(logFile.getAbsolutePath() + LOG_END_EXT);
			if(f.exists()) {
				f.delete();
			}
			if(!f.exists()) {
				logFile.renameTo(f);
			}
		}
	}
		
	
	static ArrayList<Listener> getListeners() {
		return Listener.list ;
	}
}
