package org.acepricot.finance.web.ws;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.acepricot.ber.BERIOStream;
import org.acepricot.finance.web.msgs.AceData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Message {
	private static final String NULL_ID 					= "NULL0000";
	public static final String DEFAULT_TEXT_ENCODING 		= "hex";
	private static final String DEFAULT_ENCODING_ALGORITHM 	= "AES/ECB/PKCS5Padding";
	private static final String REFERENCE_ENCODING_ALGORITHM= "AES/ECB/PKCS5Padding";
	private static final String DEFAULT_DIGEST_ALGORITHM 	= "SHA-256";
	private static final String BASE64_TEXT_ENCODING 		= "base64";
	private static final String NONE_TEXT_ENCODING 			= "none";
	private static final String SOAP_NAMESPACE 				= "http://schemas.xmlsoap.org/soap/envelope/";
	private static final String SOAP_PREFIX 				= "soap";
	private static final String SOAP_BODY_ELEMENT 			= "Body";
	private static final String SOAP_ENVELOPE_ELEMENT 		= "Envelope";
	private static final String ACEPRICOT_NAMESPACE  		= "http://ws.server.finance.acepricot.org";
	private static final String ACEPRICOT_PREFIX 			= "ax21";
	private static final String ACEPRICOT_MESSAGE 			= "message";
	private static final String ACEPRICOT_REFERENCE 		= "reference";
	private static final String ACEPRICOT_CONTENT 			= "content";
	private static final String ACEPRICOT_OPERATION 		= "getMessage";
	private static final String NULL_MESSAGE_ID 			= "Message action ID shall be mandatory";
	//public static final String REQUEST_FOR_REGISTRATION		= "1";
	
	private String reference;
	private String content;
	@SuppressWarnings("unused")
	private String id;
	private final BERIOStream enc = new BERIOStream();
	
	private static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	private static final long MAX_TIME_DELAY = 1000*60*10;
	private static final String MESSAGE_OUT_OF_TIME = "Message is out of allowed time interval";
	private static final String MESSAGE_DIGEST_MISHMATCH = "Security violation: message digest is not correct";
	
	private static DocumentBuilder db = null;
	private static Message message;
	
	private Message(){};
	
	public static Message getInstance() {
		if(message == null) {
			message = new Message();
		}
		message.setReference(null);
		message.setContent(null);
		return message;
	}
	
	public String getReference() {
		return reference;
	}
	private void setReference(String reference) {
		this.reference = reference;
	}
	
	public void setMessage(String id, String textEncoding, String encodingAlgorithm, String digestAlgorithm, String msgId, byte[] content) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
		/*String xxx = "zatulany maly pes";
		xxx = toHexString(xxx);
		System.out.println(xxx);
		System.out.println(new String(fromHexString(xxx)));
		*/
		if(msgId == null) {
			throw new IOException(NULL_MESSAGE_ID);
		}
		if(id == null) {
			id = NULL_ID;
		}
		if(textEncoding == null) {
			textEncoding = DEFAULT_TEXT_ENCODING;
		}
		if(encodingAlgorithm == null) {
			encodingAlgorithm = DEFAULT_ENCODING_ALGORITHM;
		}
		if(digestAlgorithm == null) {
			digestAlgorithm = DEFAULT_DIGEST_ALGORITHM;
		}
		String timestamp = encodeText(Long.toString(new Date().getTime()).getBytes(), DEFAULT_TEXT_ENCODING);
		String pass = Long.toHexString((long) (Math.random()*Long.MAX_VALUE)); 
		MessageDigest messageDigest = MessageDigest.getInstance(digestAlgorithm);
		messageDigest.update(content);
		String digest = encodeText(messageDigest.digest(),DEFAULT_TEXT_ENCODING);
		//must be set
		//String messageId = "1";
		
		id = String.format("%8s", id);
		textEncoding = String.format("%8s", textEncoding);
		encodingAlgorithm = String.format("%32s", encodingAlgorithm);
		digestAlgorithm = String.format("%32s", digestAlgorithm);
		pass = String.format("%16s", pass);
		digest = String.format("%64s", digest);
		timestamp = String.format("%64s", timestamp);
		msgId = String.format("%32s", msgId);
		
		SecretKeySpec key = new SecretKeySpec(pass.getBytes(), 0, 16, encodingAlgorithm.split("\\/")[0].trim());
		Cipher cipher = Cipher.getInstance(encodingAlgorithm);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		setContent(encodeText(cipher.doFinal(content),textEncoding.trim()));
		
		String reference = id + textEncoding + encodingAlgorithm + digestAlgorithm + pass + digest + timestamp + msgId;
		
		key = new SecretKeySpec(decodeText("7a1b6f1a76177900204d2c403919690c7f",DEFAULT_TEXT_ENCODING.trim()), 0, 16, REFERENCE_ENCODING_ALGORITHM.split("\\/")[0].trim());
		cipher = Cipher.getInstance(REFERENCE_ENCODING_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		setReference(encodeText(cipher.doFinal(reference.getBytes()),DEFAULT_TEXT_ENCODING));
	}
	
	private static String encodeText(byte[] b, String textEncoding) throws NoSuchAlgorithmException {
		if(textEncoding.equals(DEFAULT_TEXT_ENCODING)) {
			return toHexString(b);
		}
		else if(textEncoding.equals(BASE64_TEXT_ENCODING)) {
			return new String(b);
		}
		else if(textEncoding.equals(NONE_TEXT_ENCODING)) {
			return new String(b);
		}
		else {
			throw new NoSuchAlgorithmException("Unknown text encoding algorithm");
		}
	}
	
	public Document createSOAPEnvelope() throws ParserConfigurationException {
		if(db == null) {
			 db = dbf.newDocumentBuilder();
		}
		Document d = db.newDocument();
		Element envelope = d.createElementNS(SOAP_NAMESPACE, SOAP_PREFIX + ":" + SOAP_ENVELOPE_ELEMENT);
		Element body = d.createElementNS(SOAP_NAMESPACE, SOAP_PREFIX + ":" + SOAP_BODY_ELEMENT);
		Element operation = d.createElementNS(ACEPRICOT_NAMESPACE, ACEPRICOT_PREFIX + ":" + ACEPRICOT_OPERATION);
		Element message = d.createElementNS(ACEPRICOT_NAMESPACE, ACEPRICOT_PREFIX + ":" + ACEPRICOT_MESSAGE);
		Element reference = d.createElementNS(ACEPRICOT_NAMESPACE, ACEPRICOT_PREFIX + ":" + ACEPRICOT_REFERENCE);
		Element content = d.createElementNS(ACEPRICOT_NAMESPACE, ACEPRICOT_PREFIX + ":" + ACEPRICOT_CONTENT);
		//Element message = d.createElementNS("",  ACEPRICOT_MESSAGE);
		//Element reference = d.createElementNS("", ACEPRICOT_REFERENCE);
		//Element content = d.createElementNS("", ACEPRICOT_CONTENT);
		reference.appendChild(d.createTextNode(this.getReference()));
		content.appendChild(d.createTextNode(this.getContent()));
		message.appendChild(reference);
		message.appendChild(content);
		operation.appendChild(message);
		body.appendChild(operation);
		envelope.appendChild(body);
		d.appendChild(envelope);
		return d;
	}
	
	
	public static byte[] decodeText(String string, String textEncoding) throws NoSuchAlgorithmException {
		if(textEncoding.equals(DEFAULT_TEXT_ENCODING)) {
			return fromHexString(string);
		}
		else if(textEncoding.equals(BASE64_TEXT_ENCODING)) {
			return string.getBytes();
		}
		else if(textEncoding.equals(NONE_TEXT_ENCODING)) {
			return string.getBytes();
		}
		else {
			throw new NoSuchAlgorithmException("Unknown text encoding algorithm");
		}
	}
	private static byte[] fromHexString(String string) {
		byte[] p = string.toLowerCase().getBytes(); 
		byte[] r = new byte[p.length/2];
		for(int i = 0; i < r.length; i++) {
			int h = p[i*2]&0xFF;
			int l = p[i*2 + 1]&0xFF;
			int bh = h>0x39?h-0x57:h-0x30;
			int bl = l>0x39?l-0x57:l-0x30;
			r[i] = (byte) (bh<<0x04|bl);
		}
		for(int i = r.length - 1; i > 0; i--) {
			r[i] ^= r[i-1];
		}
		return r;
	}
	private static String toHexString(byte[] b) {
		for(int i = 1; i < b.length; i++) {
			b[i] ^= b[i-1];
		}
		byte[] r = new byte[b.length * 2];
		for(int i = 0; i < b.length; i++) {
			int h = b[i]&0xF0; 
			int l = b[i]&0x0F;
			h >>= 4;
			h = h<0x0A?h+0x30:h+0x57;
			l = l<0x0A?l+0x30:l+0x57;
			r[2*i] = (byte) h;
			r[2*i + 1] = (byte) l;
		}
		return new String(r);
	}
	/*
	private static String toHexString(String string) {
		return toHexString(string.getBytes());
	}*/
	public String getContent() {
		return content;
	}
	private void setContent(String content) {
		this.content = content;
	}
	public Message parseSOAP(InputStream in) throws ParserConfigurationException, SAXException, IOException {
		if(db == null) {
			 db = dbf.newDocumentBuilder();
		}
		Document d = db.parse(in);
		Element root = d.getDocumentElement();
		NodeList nl = root.getElementsByTagName(ACEPRICOT_PREFIX + ":" + ACEPRICOT_REFERENCE);
		this.setReference(nl.item(0).getTextContent());
		nl = root.getElementsByTagName(ACEPRICOT_PREFIX + ":" + ACEPRICOT_CONTENT);
		this.setContent(nl.item(0).getTextContent());
		return this;
	}
	
	public AceData getMessage() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
		byte[] r = decodeText(this.getReference(), Message.DEFAULT_TEXT_ENCODING);
		SecretKeySpec key = new SecretKeySpec(decodeText("7a1b6f1a76177900204d2c403919690c7f",DEFAULT_TEXT_ENCODING.trim()), 0, 16, REFERENCE_ENCODING_ALGORITHM.split("\\/")[0].trim());
		Cipher cipher = Cipher.getInstance(REFERENCE_ENCODING_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key);
		r = cipher.doFinal(r);
		id = new String(r, 0, 8).trim();
		String te = new String(r, 8, 8).trim();
		String ea = new String(r, 16, 32).trim();
		String da = new String(r, 48, 32).trim();
		String pa = new String(r, 80, 16);
		String di = new String(r, 96, 64);
		String ti = new String(r, 160, 64).trim();
		String mi = new String(r, 224, 32).trim();
		Date d = new Date(Long.parseLong(new String(decodeText(ti, DEFAULT_TEXT_ENCODING))));
		Date t1 = new Date();
		Date t2 = new Date(t1.getTime() + MAX_TIME_DELAY);
		if(d.before(t1) && d.after(t2)) {
			throw new IOException(MESSAGE_OUT_OF_TIME);
		}
		byte[] c = decodeText(this.getContent(), te);
		key = new SecretKeySpec(pa.getBytes(), 0, 16, ea.split("\\/")[0].trim());
		cipher = Cipher.getInstance(ea);
		cipher.init(Cipher.DECRYPT_MODE, key);
		c = cipher.doFinal(c);
		MessageDigest messageDigest = MessageDigest.getInstance(da);
		messageDigest.update(c);
		String digest = encodeText(messageDigest.digest(),DEFAULT_TEXT_ENCODING);
		if(!digest.equalsIgnoreCase(di)) {
			throw new IOException(MESSAGE_DIGEST_MISHMATCH);
		}
		AceData data = new AceData();
		ByteArrayInputStream out = new ByteArrayInputStream(c);
		enc.setInputStream(out);
		data.decode(enc);
		data.setMessageType(mi);
		return data;
	}
	
}
