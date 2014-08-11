package com.acepricot.finance.sync.client;

import java.util.Date;

import com.acepricot.finance.sync.share.JSONMessage;

public class Heartbeat extends JSONMessage {
	private Heartbeat() {
		this.setHeader(JSONMessageProcessorClient.HEARTBEAT_HEADER);
		this.setBody(new Object[]{new Date().getTime()});
	}
	public static JSONMessage getInstance() {
		return new Heartbeat();
	}
}
