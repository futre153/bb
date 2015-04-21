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
		basicHeader.parse(in);
		parseContent(in);		
	}

	@Override
	public void parseContent(InputStreamReader in) throws IOException {
		while (true) {
			char c = BlockImpl.readCharacter(in, null);
			String bi;
			if(c == SAARJEParser.RJE_MESSAGE_SEPARATOR) {
				break;
			}
			else if (c == SAARJEParser.BLOCK_ID_START_INDICATOR) {
				bi = BlockImpl.readTo(in, BlockImpl.END_OF_BLOCK_ID,SAARJEParser.MIN_BLOCK_ID_LENGTH, SAARJEParser.MAX_BLOCK_ID_LENGTH, BlockImpl.BLOCK_ID_PATTERN);
			}
			else if (c == '\r' || c == '\n') {
				throw new IOException(BlockImpl.END_OF_STREAM);
			}
			else {
				throw new IOException(SAARJEParser.FAILED_ID);
			}
			if(bi.equals(BlockImpl.BASIC_HEADER) && message == null) {
				message = new RJEFinMessage();
				message.basicHeader = new BasicHeader();
				message.basicHeader.setBlockIdentifier(bi);
				message.basicHeader.parseBlockContent(in);
				message.parseContent(in);
				break;
			}
			else if(bi.equals(BlockImpl.APPLICATION_HEADER) && applicationHeader == null) {
				applicationHeader = new ApplicationHeader();
				applicationHeader.parseBlockContent(in);
			}
			else if(bi.equals(BlockImpl.USER_HEADER) && userHeader == null) {
				userHeader = new UserHeader(bi);
				userHeader.parseBlockContent(in);
			}
			else if(bi.equals(BlockImpl.TEXT) && text == null) {
				text = new Text(bi, getAppHeader());
				text.parseBlockContent(in);
			}
			else if(bi.equals(BlockImpl.TRAILERS) && trailers == null) {
				trailers = new Trailers(bi);
				trailers.parseBlockContent(in);
			}
			else if(bi.equals(BATCH_TRAILERS) && batchTrailers == null) {
				batchTrailers = new CommonBlock(bi);
				batchTrailers.parseBlockContent(in);
			}
			else {
				throw new IOException(String.format(UNKNOWN_BLOCK, bi));
			}
		}
		
	}


}
