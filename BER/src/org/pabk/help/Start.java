package org.pabk.help;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.pabk.ber.BERSequence;
import org.pabk.ber.BaseBEREncoder;
import org.pabk.ber.Encoder;
import org.pabk.rfc.rfc2314.RFC2986;

public class Start {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		//byte i=1;
		//System.out.println(Integer.toHexString(i&0xFF));
		String xxx= "MIICszCCAZsCAQAwbjEfMB0GA1UEAxMWQWxsaWFuY2UgQWNjZXNzIGFjY2VzczEM"+
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
		//System.exit(0);
		Encoder en=new BaseBEREncoder();
		en.setLevel(0);
		System.out.println(Arrays.toString(Base64Coder.decode(xxx)));
		en.setInputStream(new ByteArrayInputStream(Base64Coder.decode(xxx)));
		
		BERSequence ber=RFC2986.getCertificateRequest("message");
		
		//BER tmp=ber.forName("trap");
		//tmp.setValue(new Object[]{});
		
		ber.decode(en,-1);
		en.close();
		//en.setLevel(0);
		//en.setInputStream(new FileInputStream("temp/trap1337240278961"));
		//ber=RFC1157.getMessage("message");
		//ber.decode(en,-1);
		System.out.println(ber.toString());
		ber.clearContent();
		//ber=RFC1157.getMessage("message");
		en.close();
		

		
	}

}
