package org.pabk.basen.ber.encoders;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.pabk.basen.ber.BERImpl;

public class ConstrustedEncoder implements BEREncoder {

	private static final String LENGTH_MISH_MATCH = "BER object with definitive length cannot has a child with indefinte length";
	
	@Override
	public InputStream encode(BERImpl ber) throws IOException {
		return null;
	}

	@Override
	public long decode(BERImpl ber, InputStream in) throws IOException {
		long length = ber.getLength();
		while (true) {
			BERImpl child = new BERImpl();
			long l = child.decodeIdOctets(in);
			l += child.decodeLengthOctets(in);
			if(child.getEncoder() == null) {
				child.setEncoder();
			}
			long cl = child.decodeContent(in);
			l = ber.hasIndefiniteLength() ? cl : (l + cl);
			if(ber.hasDefiniteLength() && child.hasIndefiniteLength()) {
				throw new IOException (String.format(LENGTH_MISH_MATCH));
			}
			else {
				length -= l;
			}
			ber.add(child);
			if(length == 0) {
				break;
			}
			if(child.isEOC() && ber.hasIndefiniteLength()) {
				break;
			}
		}
		return ber.getLength();
	}

	@Override
	public long getLength(BERImpl ber) {
		long l = 0;
		for(int i = 0; i < ber.size(); i ++) {
			BERImpl child = ber.get(i);
			if(child.hasIndefiniteLength()) {
				return child.getLength();
			}
			l += child.getLength();
			l += ((ByteArrayInputStream) child.getIdentifierOctets()).available();
			l += ((ByteArrayInputStream) child.getIdentifierOctets()).available();
		}
		return l;
	}

	@Override
	public void setValue(BERImpl ber, Object obj) throws IOException {
		try {
			ber.clear();
			BERImpl[] seq = (BERImpl[]) obj;
			long l = 0;
			for (int i = 0; i < seq.length; i ++) {
				l += (l >= 0 ? seq[i].getLength() : 0);
				l += (l >= 0 ? seq[i].getIdentifierOctets().available() : 0);
				l += (l >= 0 ? seq[i].getLengthOctets().available() : 0);
				ber.add(seq[i]);
			}
			ber.setLength(l);
		}
		catch(Exception e) {
			throw new IOException(e);
		}
				
	}

	private static final ArrayList<String> toString(BERImpl ber, ArrayList<String> lines) {
		if(ber.getConstructed() > 0) {
			for(int i = 0; i < ber.size(); i ++) {
				String [] line = ber.get(i).getEncoder().toString(ber.get(i));
				for(int j = 0; j < line.length; j ++) {
					lines.add(line[j]);
				}
			}
		}
		else {
			String [] line = ber.getEncoder().toString(ber);
			for(int i = 0; i < line.length; i ++) {
				lines.add(line[i]);
			}
		}
		return lines;
	}
	
	@Override
	public String[] toString(BERImpl ber) {
		ArrayList<String> lines = new ArrayList<String>();
		lines = ConstrustedEncoder.toString(ber, lines);
		String[] retValue = new String[lines.size()];
		return lines.toArray(retValue);
	}
}
