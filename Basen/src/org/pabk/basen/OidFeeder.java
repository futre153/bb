package org.pabk.basen;

import org.pabk.basen.asn1.oid.OidEntryImpl;
import org.pabk.basen.asn1.oid.OidRepository;

public class OidFeeder {
	private static String name = "1.2.840.113549.1.1.11";
	private static String desc = "SHA256 with RSA Encryption";

	public static void main (String[] args) {
		OidEntryImpl entry = new OidEntryImpl();
		entry.setName(name);
		entry.setDescription(desc);
		OidRepository.add(entry);
		OidRepository.save();
	}
}
