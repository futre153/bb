package org.acepricot.finance.web.msgs;

import java.io.IOException;

public class FileUploadRequest extends AceData {

	

	public FileUploadRequest (String filename, int filepartCount, String digestAlgorithm, byte[] digest) throws IOException {
		super();
		AceDataItemImpl item1 = new AceDataItemImpl(FILENAME_HEADER, "", filename.getBytes());
		AceDataItemImpl item2 = new AceDataItemImpl(FILEPART_COUNT_HEADER, "", Integer.toString(filepartCount).getBytes());
		AceDataItemImpl item3 = new AceDataItemImpl(FILE_DIGEST_HEADER, digestAlgorithm, digest);
		this.setConstructedContent(item1, item2, item3);
		this.setMessageType(FILE_UPLOAD);
	}

}
