package org.pabk.emanager.parser.fin;

import java.io.IOException;
import java.io.InputStreamReader;

public interface Message {
	public BasicHeader getBasicHeader();
	public ApplicationHeaderBlock getAppHeader();
	public UserHeaderBlock getUserHeader();
	public TextBlock getText();
	public TrailersBlock getTrailer();
	void parse(InputStreamReader in) throws IOException;
}
