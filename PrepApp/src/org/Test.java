package org;

import org.pabk.util.Huffman;

public class Test {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//InputStream in = Test.class.getClass().getResourceAsStream("/org/pabk/resources/properties.xml");
		//Properties pro = new Properties();
		//pro.loadFromXML(in);
		//in.close();
		
		System.out.println(Huffman.decode("8zu1w0iiEwH0AA==", null));
		System.out.println(Huffman.encode("Nikoleta-3", null));
	}

}
