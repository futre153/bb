package org.pabk.emanager.parser.fin;

import java.io.IOException;
import java.io.InputStreamReader;

public class RJEFinMessage implements Message {
	
	private static final String BATCH_TRAILERS = "S";
	private static final String UNKNOWN_BLOCK = "Block \"%s\" is not known";
	private BasicHeader basicHeader;
	private ApplicationHeader applicationHeader;
	private RJEFinMessage message;
	private UserHeader userHeader;
	private Text text;
	private Trailers trailers;
	private CommonBlock batchTrailers;
		
	@Override
	public BasicHeader getBasicHeader() {
		return this.basicHeader;
	}

	@Override
	public ApplicationHeader getAppHeader() {
		return this.applicationHeader;
	}

	@Override
	public UserHeader getUserHeader() {
		return this.userHeader;
	}

	@Override
	public Text getText() {
		return this.text;
	}

	@Override
	public Trailers getTrailer() {
		return this.trailers;
	}

	@Override
	public void parse(InputStreamReader in) throws IOException {
		basicHeader = new BasicHeader();
		basicHeader.setBlockIdentifier(BlockImpl.BASIC_HEADER);
		basicHeader.parseBlockContent(in);
		while (true) {
			String bi = BlockImpl.readBlockId(in);
			if(bi.equals(BlockImpl.BASIC_HEADER) && message == null) {
				message = new RJEFinMessage();
				message.parse(in);
			}
			if(bi.equals(BlockImpl.APPLICATION_HEADER) && applicationHeader == null) {
				applicationHeader = new ApplicationHeader();
				applicationHeader.parse(in);
			}
			if(bi.equals(BlockImpl.USER_HEADER) && userHeader == null) {
				userHeader = new UserHeader(bi);
				userHeader.parseBlockContent(in);
			}
			if(bi.equals(BlockImpl.TEXT) && text == null) {
				text = new Text(bi, getAppHeader());
				text.parseBlockContent(in);
			}
			if(bi.equals(BlockImpl.TRAILERS) && trailers == null) {
				trailers = new Trailers(bi);
				trailers.parseBlockContent(in);
			}
			if(bi.equals(BATCH_TRAILERS) && batchTrailers == null) {
				batchTrailers = new CommonBlock(bi);
				batchTrailers.parseBlockContent(in);
			}
			else {
				throw new IOException(String.format(UNKNOWN_BLOCK, bi));
			}
		}
		
		
	}

}
