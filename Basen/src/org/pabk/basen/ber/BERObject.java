package org.pabk.basen.ber;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

interface BERObject {
	InputStream getIdentifierOctets();
	InputStream getLengthOctets();
	InputStream getContentOctets();
	InputStream getEOFOctets();
	void encode(OutputStream out) throws IOException;
	void decode(InputStream in) throws IOException;
	boolean isUniversal();
	boolean isApplication();
	boolean isContextSpecific();
	boolean isPrivate();
	boolean hasPrimitiveEncoding();
	boolean hasConstructedEncoding();
	boolean hasDefiniteLength();
	boolean hasIndefiniteLength();
}
