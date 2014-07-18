package org.acepricot.finance.web.msgs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Hashtable;

public class SQLRequest extends AceData {

	public SQLRequest(String SQLOperation, Hashtable<String, Object> data) throws IOException {
		super();
		AceDataItemImpl item1 = new AceDataItemImpl(SQL_OPERATION_HEADER, "", SQLOperation.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream oout = new ObjectOutputStream(out);
		oout.writeObject(data);
		AceDataItemImpl item2 = new AceDataItemImpl(SQL_DATA_HEADER, "", out.toByteArray());
		this.setConstructedContent(item1, item2);
		this.setMessageType(SQL_OPERATION);
	}

	
}
