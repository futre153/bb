package org;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.pabk.util.Base64Coder;
import org.pabk.util.Huffman;
import org.so.sms.notification.client._Stub;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Test {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//InputStream in = Test.class.getClass().getResourceAsStream("/org/pabk/resources/properties.xml");
		//Properties pro = new Properties();
		//pro.loadFromXML(in);
		//in.close();
		
		System.out.println(Huffman.decode("8zu1w0iiEwH0AA==", null));
		System.out.println(Huffman.encode("Nikoleta-8", null));
		System.out.println(Huffman.encode("brandys", null));
		
		double d = -10000005;
		System.out.println(new DecimalFormat("#,##0.00").format(d));
		System.out.println(new DecimalFormat("#,##0.00").format(0 - d));
		
		String[] c = new String[] {"0","1","2","3","4","5"};
		int s = 2;
		int e = 5;
		String[] array = new String[c.length - e + s];
		System.arraycopy(c, 0, array, 0, s);
		System.arraycopy(c, e + 1, array, s + 1, c.length - e - 1);
		System.out.println(Arrays.toString(array));
		
		String jano = "(((((not))))))d)";
		System.out.println(jano.replaceFirst("\\)*\\)$", ""));
		
		String body = "PD94bWwgdmVyc2lvbj0nMS4wJyBlbmNvZGluZz0ndXRmLTgnPz48ZW52OkVudmVsb3BlIHhtbG5zOmVudj0iaHR0cDovL3NjaGVtYXMueG1sc29hcC5vcmcvc29hcC9lbnZlbG9wZS8iPjxlbnY6Qm9keT48TXNnVHlwZT4wPC9Nc2dUeXBlPjxJbnN0aXR1dGlvbklkPjY1MDA8L0luc3RpdHV0aW9uSWQ+PENhcmROdW1iZXI+Njc2MjQxNDAxMDAxNzI2NTwvQ2FyZE51bWJlcj48Tm90aWZpY2F0aW9uVHlwZT4xPC9Ob3RpZmljYXRpb25UeXBlPjxDb250YWN0PjA5NDk3MTEzNzc8L0NvbnRhY3Q+PFRyYW5zYWN0aW9uU291cmNlPjE8L1RyYW5zYWN0aW9uU291cmNlPjxUcmFuc2FjdGlvblR5cGU+MTA8L1RyYW5zYWN0aW9uVHlwZT48VHJhbnNhY3Rpb25BbW91bnQ+KzAwMDAwMDAwMDMwLjAwPC9UcmFuc2FjdGlvbkFtb3VudD48VHJhbnNhY3Rpb25DdXJyZW5jeT5FVVI8L1RyYW5zYWN0aW9uQ3VycmVuY3k+PEF2YWlsYWJsZUJhbGFuY2U+KzAwMDAwMDAwMDA5LjgxPC9BdmFpbGFibGVCYWxhbmNlPjxBY2NvdW50Q3VycmVuY3k+RVVSPC9BY2NvdW50Q3VycmVuY3k+PFRyYW5zYWN0aW9uRGF0ZVRpbWU+MjAxMjEyMjQxNTIxNTY8L1RyYW5zYWN0aW9uRGF0ZVRpbWU+PE1lcmNoYW50TmFtZT5CUkFUSVNMQVZBLENBQ0hUSUNLQSAyNTwvTWVyY2hhbnROYW1lPjxUZXJtaW5hbE93bmVyTmFtZT5QT0I8L1Rlcm1pbmFsT3duZXJOYW1lPjxNZXJjaGFudENpdHk+QlJBVElTTEFWQTwvTWVyY2hhbnRDaXR5PjxNZXJjaGFudFN0YXRlPlNLPC9NZXJjaGFudFN0YXRlPjxUeG5JZD40MjAwMDAwMDAwMDAwMDAwMDAwMjUzNTQxMjY4PC9UeG5JZD48L2VudjpCb2R5PjwvZW52OkVudmVsb3BlPg==";
		System.out.println(Base64Coder.decodeString(body));
		
		
		
		/*
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = db.newDocument();
		FileInputStream in = new FileInputStream ("C:\\Users\\brandys\\Desktop\\test.txt");
		InputStreamReader reader = new InputStreamReader(in, "UTF-8");
		BufferedReader buf = new BufferedReader(reader);
		String line = null;
		Element root = doc.createElementNS("http://www.example.org/CurrencyCodes", "cur:list");
		while((line = buf.readLine()) != null) {
			System.out.println(line);
			Element cur = doc.createElement("cur:item");
			Element iso = doc.createElement("cur:iso");
			Element cod = doc.createElement("cur:code");
			Element dig = doc.createElement("cur:digits");
			Element nam = doc.createElement("cur:name");
			Element cts = doc.createElement("cur:countries");
			String[] itm = line.split("\t", 5);
			iso.setTextContent(itm[0].trim().substring(itm[0].length() - 3));
			cod.setTextContent(itm[1].trim().substring(itm[1].length() - 3));
			dig.setTextContent(itm[2].trim().substring(itm[2].length() - 1));
			nam.setTextContent(normalize(itm[3].trim()));
			itm = itm[4].trim().split(",");
			for(int i = 0; i < itm.length; i ++) {
				String con = itm[i].trim();
				if(con.length() > 0) {
					Element ctr = doc.createElement("cur:country");
					ctr.setTextContent(normalize(itm[i]));
					cts.appendChild(ctr);
				}
			}
			cur.appendChild(iso);
			cur.appendChild(cod);
			cur.appendChild(dig);
			cur.appendChild(nam);
			cur.appendChild(cts);
			root.appendChild(cur);
		}
		doc.appendChild(root);
		doc.normalizeDocument();
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File("C:\\Users\\brandys\\Desktop\\file.xml"));
		System.out.println(transformer.getOutputProperty(OutputKeys.ENCODING));
		transformer.transform(source, result);
		buf.close();*/
	}
/*
	private static String normalize(String trim) {
		int s = 0, e = 0;
		for(int i = 0; i < trim.length(); i ++) {
			char c = trim.charAt(i);
			@SuppressWarnings("unused")
			int x = c;
			if(c > ' ' && c < 128) {
				s = i;
				break;
			}
		}
		for(int i = trim.length() - 1; i >= 0; i --) {
			char c = trim.charAt(i);
			if(c > ' ' && c < 128) {
				e = i;
				break;
			}
		}
		return trim.substring(s, e + 1);
	}
*/
}
