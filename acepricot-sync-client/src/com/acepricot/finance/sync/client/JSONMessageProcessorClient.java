package com.acepricot.finance.sync.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.acepricot.finance.sync.share.JSONMessage;
import com.google.gson.Gson;

public class JSONMessageProcessorClient {
	
	private JSONMessageProcessorClient() {}
	
	static final JSONMessage responseProcessor(InputStream in) throws IOException {
		JSONMessage msg = new JSONMessage();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int i = 0;
		while((i = in.read()) >= 0) {
			out.write(i);
		}
		//client.close();
		InputStreamReader bin = new InputStreamReader(new ByteArrayInputStream(out.toByteArray()), "UTF-8");
		try {
			msg = new Gson().fromJson(bin, JSONMessage.class);
		}
		catch(Exception e) {
			msg = new JSONMessage("error", new Object[]{"UNKNOWN"});
			bin = new InputStreamReader(new ByteArrayInputStream(out.toByteArray()), "UTF-8");
		}
		if(msg == null) {
			msg = new JSONMessage("error", new Object[]{"NULL"});
		}
		//System.out.println(msg.getHeader());
		//System.out.println(Arrays.toString(msg.getBody()));
		//System.out.println(msg.getBody()[0]);
		char[] chr = new char[1024];
		i = 0;
		while((i = bin.read(chr)) >= 0) {
			System.out.println(new String(chr, 0, i));
		}
		return msg;
		
	}
}
