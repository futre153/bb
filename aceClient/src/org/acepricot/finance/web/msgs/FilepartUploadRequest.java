package org.acepricot.finance.web.msgs;

import java.io.IOException;

public class FilepartUploadRequest extends AceData {

	

	public FilepartUploadRequest(String filename, int filepartId, byte[] filepart) throws IOException {
		super();
		AceDataItemImpl item1 = new AceDataItemImpl(FILENAME_HEADER, "", filename.getBytes());
		AceDataItemImpl item2 = new AceDataItemImpl(FILEPART_ID_HEADER, "", Integer.toString(filepartId).getBytes());
		AceDataItemImpl item3 = new AceDataItemImpl(FILEPART_HEADER, "", filepart);
		this.setConstructedContent(item1, item2, item3);
		this.setMessageType(FILEPART_UPLOAD);
	}

}
