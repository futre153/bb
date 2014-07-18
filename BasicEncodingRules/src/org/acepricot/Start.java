package org.acepricot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.acepricot.ber.BER;
import org.acepricot.ber.BERIOStream;


public class Start {

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		/*String xxx= "MIICszCCAZsCAQAwbjEfMB0GA1UEAxMWQWxsaWFuY2UgQWNjZXNzIGFjY2VzczEM"+
					"MAoGA1UECxMDRElUMRswGQYDVQQKExJQb3N0b3ZhIGJhbmthIGEucy4xEzARBgNV"+
					"BAcTCkJyYXRpc2xhdmExCzAJBgNVBAYTAlNLMIIBIjANBgkqhkiG9w0BAQEFAAOC"+
					"AQ8AMIIBCgKCAQEAvg5rduZiR+KmMgg6cCBzHBV8bSnSN0op7kd7MjMj6xKFxPvm"+
					"KUtADBdILzcHB0HkczS82WrtLilNlCVSBYUV6O/NSLAWRMHeazdVea/Bziq699sh"+
					"tOaSkhsNSK1gz4lqDTTHldmeQfM7R1qYsAlmdbqDbG40ZAJ1Cs+CuOSDjcqyAKXt"+
					"zdFVfiFIdkO4IVSbVazO5n910YfU4u0VpidXhkLNI+5/xa4zENWeXjtJCczaFp7v"+
					"N4TDwfLwPlu/pTyH3xnxtQnv1sDMiY41qHRzyWrApokpJ2Vuj/I5yKWVqnh5XxfY"+
					"zj8Ixi+rc3vo9w07inZFeHsfpgGRbWoXE4P/wwIDAQABoAAwDQYJKoZIhvcNAQEF"+
					"BQADggEBAGXNjHlduzf4m6kHyoGLIIInoxqkvd+Cq5q7b6oRO9uV3pY9IXWYKp23"+
					"3gEfXROnIEDgQkgo/QzqImmyFtm+cnqgO6LA3GaaZLB3lMYDan3iwlPoI/AueLdU"+
					"7d3gxSUHXgHDQc19IUj2TrV9KaXhvajzZrdOvPibDNfdKk0HJyUoFq0Xrb1kjtxZ"+
					"ywdr+RW685DKm6a9Oj5O65lxmA0cDPsBi9zsWdXB6hCaf4wy+4SGwk7hyoZPgEGR"+
					"6A5IrahRiKF7Q7MZq/swXmPJoVYO131CWhTGLlTLKW35HiJGVMC1A1eR/TaTNGLk"+
					"tRIaUbBqDFizZWUMd54XyUr+RPKqnak=";
*/		//byte[] v = new byte[0x0FFFFFFF];
		@SuppressWarnings("unused")
		String xxx= "";//MHMwMAQRUmVnaXN0cmF0aW9uRW1haWwGAAQZYnJhbmlzbGFiLmJyYW5keXNAcGFiay5zazA/BBBEaWdlc3RlZFBhc3N3b3JkBglghkgBZQMEAgEEIMHrPpW+xypR19Ds4eN/zwBM6z8CZemoCZJQHRnGMu1x";
					//System.out.println(Arrays.toString(Base64Coder.decode(xxx)));
		BERIOStream enc = new BERIOStream();
		//enc.setInputStream(new ByteArrayInputStream(Base64Coder.decode(xxx)));
		BER ber = new BER();
		//AceData data = new AceData();
		//CertificateRequest req = new CertificateRequest("request");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		enc.setOutputStream(out);
		ber.setValue(new byte[0]);
		ber.encode(enc);
		enc.setInputStream(new ByteArrayInputStream(out.toByteArray()));
		try {
			ber.decode(enc);
//			req.loadFromExisting(ber);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(ber);
		//RegistrationRequest data = new RegistrationRequest("branislab.brandys@pabk.sk", null, "Maly strakaty pes".getBytes());
		//AceDataItem item = new AceDataItem("1", "2.80.236",new byte[]{0});
		//ByteArrayOutputStream out = new ByteArrayOutputStream();
		//enc.setOutputStream(out);
		//data.encode(enc);
		//System.out.println(Base64Coder.encode(out.toByteArray()));
		/*
		System.out.println(data.get("RegistrationEmail").getName());
		System.out.println(data.get("RegistrationEmail").getOid());
		System.out.println(new String(data.get("RegistrationEmail").getItemValue()));
		System.out.println(data.get("DigestedPassword").getName());
		System.out.println(data.get("DigestedPassword").getOid());
		System.out.println(data.get("DigestedPassword").getItemValue());
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update("Maly strakaty pes".getBytes());
		System.out.println(Arrays.equals(data.get("DigestedPassword").getItemValue(), md.digest()));
		*/
	}
}
