package org.pabk.basen;

import java.security.InvalidKeyException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

public class Start {

	public static void main(String[] args) throws InvalidKeyException, SignatureException, NoSuchProviderException {
		
		
		
		
			/*
			KeyPair pair = KeyUtils.createJKSKeyEntry("RSA", null, 2048);
			int serialNo = 11111112;
			String signature = "SHA256withRSA";
			String[] issuer = new String[] {"OU", "I.CA - Provider of Certification Services", "O", "První certifikaèní autorita, a.s.", "CN", "I.CA - Standard Certification Authority, 09/2009", "C", "CZ"};
			
			long notBefore = new Date().getTime();
			long notAfter = new Date().getTime() + 121453152;
			String[] subject = new String[] {
				"SERIALNUMBER", "ICA - 723454",
				"OU", "DIT/certisws",
				"O", "Poštová banka, a.s.",
				"L", "Bratislava, Dvoøákovo nábrežie 4, 81102",
				"CN", "Branislav Brandys",
				"ST", "Bratislava 4",
				"C", "SK"};
			
			BERImpl ber = Certificate.createCertificate(pair, serialNo, signature, issuer, notBefore, notAfter, subject);
			Certificate cer = new Certificate("jano", true);
			cer.setBERObject(ber);
			//System.out.println(cer);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ber.encode(out);
			ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
			cer = new Certificate ("brano", in);
			System.out.println(cer.getCertificate());
			Object[] extensions = {
					"2.5.29.15", "1111",
					"2.5.29.17", new String[]{"iPAddress", "10.1.1.41", "dNSName", "P3600X006", "dNSName", "P3600X006.pabk.sk", "rfc822Name", "branislav.brandys@pbit.sk"},
					"2.5.29.37", new String[]{"1.3.6.1.5.5.7.3.1", "1.3.6.1.5.5.7.3.2"}};
			BERImpl req = Request.createRequest(pair, subject, extensions);
			out = new ByteArrayOutputStream();
			req.encode(out);
			System.out.println("-----BEGIN NEW CERTIFICATE REQUEST-----");
			System.out.print(Base64Coder.encodeLines(out.toByteArray()));
			System.out.println("-----END NEW CERTIFICATE REQUEST-----");*/
	
		
		
		System.exit(1);
		/*
		byte[] b = {0, -1, 127, 52, 5, -24, 2, 5 ,5,100,5,5,5,5,5,4,44,44,44,44,44,44,45, 4,47,86,86,86,33,33,3,-128,1,0, -1, 127, 52, 5, -24, 2, 5 ,5,100,5,5,5,5,5,4,44,44,44,44,44,44,45, 4,47,86,86,86,33,33,3,-128,1,0, -1, 127, 52, 5, -24, 2, 5 ,5,100,5,5,5,5,5,4,44,44,44,44,44,44,45, 4,47,86,86,86,33,33,3,-128,1,0, -1, 127, 52, 5, -24, 2, 5 ,5,100,5,5,5,5,5,4,44,44,44,44,44,44,45, 4,47,86,86,86,33,33,3,-128,1,0, -1, 127, 52, 5, -24, 2, 5 ,5,100,5,5,5,5,5,4,44,44,44,44,44,44,45, 4,47,86,86,86,33,33,3,-128,1,0, -1, 127, 52, 5, -24, 2, 5 ,5,100,5,5,5,5,5,4,44,44,44,44,44,44,45, 4,47,86,86,86,33,33,3,-128,1,0, -1, 127, 52, 5, -24, 2, 5 ,5,100,5,5,5,5,5,4,44,44,44,44,44,44,45, 4,47,86,86,86,33,33,3,-128,1,0, -1, 127, 52, 5, -24, 2, 5 ,5,100,5,5,5,5,5,4,44,44,44,44,44,44,45, 4,47,86,86,86,33,33,3,-128,1};
		System.out.println(DatatypeConverter.printHexBinary(b));
		String ls = System.getProperty("line.separator");
		StringBuffer sb = new StringBuffer();
		sb.append("jano");
		sb.append(ls);
		sb.append("peter");
		System.out.println(Arrays.toString(sb.toString().split(ls)));
		String[] ret = new String[(b.length - 1) / 16 + 1];
		System.out.println((b.length - 17) / 16);
		System.out.println(ret.length);
		int l = (16*16) & 0xFFFF;
		System.out.println(String.format("%4s", Integer.toHexString(l)).replace(' ', '0'));
		System.out.println(String.format("%2s", Integer.toHexString(b[0] & 0xFF)).replace(' ', '0'));
		System.out.println(String.format("%2d", b[0] & 0xFF).replace(' ', '0'));
		System.out.println(Arrays.toString(EncoderUtil.printHex(new ByteArrayInputStream(b))));
		System.exit(1);
		*/
		//FileInputStream in = new FileInputStream("D:\\Dokumenty\\SWIFT\\certs\\swift_code_R6.cer");"
		/*
		FileInputStream in = new FileInputStream("D:\\Dokumenty\\SWIFT\\certs\\brandys.cer");
		FullCert cert = new FullCert("alias", in);
		System.out.println(cert.getCertificate());
		in.close();
		*/
		/*
		FileInputStream in = new FileInputStream("D:\\Dokumenty\\SWIFT\\certs\\CRL_1500000_19137.crl");
		FullCRL crl = new FullCRL("alias", in);
		System.out.println(crl.getCrl());
		in.close();
		*/
		/*
		FileInputStream in = new FileInputStream("M:\\Test\\inmsgiq\\20120806_2240_000719.p7e");
		ContentInfo pkcs7 = new ContentInfo("pkcs7", false);
		BERImpl ber = new BERImpl();
		ber.decode(in);
		pkcs7.setBERObject(ber);
		System.out.println(pkcs7);
		in.close();
		*/
		/*File f = new File ("D:\\Dokumenty\\SWIFT\\certs\\swift_code_R6_bkp.cer");
		if(f.exists()) {
			f.delete();
		}
		f.createNewFile();
		FileOutputStream out = new FileOutputStream(f);
		ber.encode(out);
		out.close();
		Certificate cert = new Certificate("swift_code_R6", false);
		cert.setBERObject(ber);
		*/
		/*
		System.out.println(ber.get_class());
		System.out.println(ber.getLength());
		ber.set_class(BERImpl.CONTEXT_SPECIFIC_CLASS);
		ber.setConstructed(BERImpl.CONSTRUCTED_ENCODING);
		ber.setContent(ber);
		ber.setTag(16);
		ber.setLength(-254852);
		InputStream in = ber.getIdentifierOctets();
		int i = 0;
		while((i = in.read()) >= 0) {
			System.out.println(Integer.toHexString(i));
		}
		System.out.println("Lenght");
		in = ber.getLengthOctets();
		i = 0;
		while((i = in.read()) >= 0) {
			System.out.println(Integer.toHexString(i));
		}
		*/
	}

}
