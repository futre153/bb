package org.so.sms.notification.client;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;

import javax.wsdl.Definition;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.context.MessageContext;
import org.pabk.util.Const;

class NotificationRequest {
  	
	private static OMElement curx;
	private static Hashtable<String, String> foundCurr = new Hashtable<String, String>();

	public static SOAPEnvelope createEnvelope1(MessageContext msg, SOAPFactory fac, Definition def) {
		SOAPEnvelope env = msg.getEnvelope();
		SOAPBody body = env.getBody();
		SOAPEnvelope req = fac.createSOAPEnvelope(env.getNamespace());
		SOAPBody reqBody = fac.createSOAPBody(req);
		OMNamespace ns = fac.createOMNamespace(def.getTargetNamespace(), "");
		req.declareNamespace(ns);
		OMElement root = fac.createOMElement(Const.NOTIFICATION_REQUEST1_ROOT, ns);
		Iterator<?> ite = body.getChildElements();
		while (ite.hasNext()) {
			OMElement elem = (OMElement)ite.next();
			String text = elem.getText();
			OMElement elem2 = fac.createOMElement(elem.getLocalName(), ns);
			elem2.setText(text);
			root.addChild(elem2);
		}
		reqBody.addChild(root);
		return req;
	}
	
	public static SOAPEnvelope createEnvelope2 (MessageContext msg, SOAPFactory fac, OMElement docx) {
		System.out.println(docx);
		SOAPEnvelope env = msg.getEnvelope();
		SOAPBody body = env.getBody();
		SOAPEnvelope req = fac.createSOAPEnvelope(env.getNamespace());
		SOAPBody reqBody = fac.createSOAPBody(req);
		OMNamespace tem = fac.createOMNamespace(Const.TEMPURI_NAMESPACE, Const.TEMPURI_PREFIX);
		OMNamespace pabk = fac.createOMNamespace(Const.PABK_NAMESPACE, Const.PABK_PREFIX);
		req.declareNamespace(tem);
		req.declareNamespace(pabk);
		OMElement root = fac.createOMElement(Const.MESSAGE_SERVICE_REQUEST_ROOT, tem);
		OMElement message = fac.createOMElement(Const.MESSAGE_SERVICE_REQUEST_MESSAGE, tem);
		Hashtable<String, String> data = new Hashtable<String, String>();
		Iterator<?> ite = body.getChildElements();
		Const.setRequestDefaultValues(data);
		while (ite.hasNext()) {
			OMElement elem = (OMElement)ite.next();
			String key = elem.getLocalName();
			String value = elem.getText().trim();
			data.put(key, value);
		}
		String externalId = NotificationRequest.parseLines(data, ((OMElement) docx.getChildrenWithLocalName(Const.MESSAGE_EXTERNAL_ID).next()).getChildrenWithLocalName(Const.LINE_ELEMENT_NAME));
		String messageType = NotificationRequest.parseLines(data, ((OMElement) docx.getChildrenWithLocalName(Const.MESSAGE_MESSAGE_TYPE).next()).getChildrenWithLocalName(Const.LINE_ELEMENT_NAME));
		String recipient = NotificationRequest.parseLines(data, ((OMElement) docx.getChildrenWithLocalName(Const.MESSAGE_RECIPIENT).next()).getChildrenWithLocalName(Const.LINE_ELEMENT_NAME));
		String sendOn = NotificationRequest.parseLines(data, ((OMElement) docx.getChildrenWithLocalName(Const.MESSAGE_SEND_ON).next()).getChildrenWithLocalName(Const.LINE_ELEMENT_NAME));
		String sourceKey = NotificationRequest.parseLines(data, ((OMElement) docx.getChildrenWithLocalName(Const.MESSAGE_SOURCE_KEY).next()).getChildrenWithLocalName(Const.LINE_ELEMENT_NAME));
		String text = NotificationRequest.parseLines(data, ((OMElement) docx.getChildrenWithLocalName(Const.MESSAGE_TEXT).next()).getChildrenWithLocalName(Const.LINE_ELEMENT_NAME));
		text = (text.length() > Const.MAX_SMS_CHARACTER && NotificationRequest.getMessageType(data, data.get(Const.MESSAGE_NOTIFICATION_TYPE)).equals(Const.SMS_MESSAGE)) ? text.substring(0, Const.MAX_SMS_CHARACTER) : text;
		String validTo = NotificationRequest.parseLines(data, ((OMElement) docx.getChildrenWithLocalName(Const.MESSAGE_VALID_TO).next()).getChildrenWithLocalName(Const.LINE_ELEMENT_NAME));
		OMElement OMEI = fac.createOMElement(Const.MESSAGE_EXTERNAL_ID, pabk);
		OMElement OMMT = fac.createOMElement(Const.MESSAGE_MESSAGE_TYPE, pabk);
		OMElement OMRE = fac.createOMElement(Const.MESSAGE_RECIPIENT, pabk);
		OMElement OMSO = fac.createOMElement(Const.MESSAGE_SEND_ON, pabk);
		OMElement OMSK = fac.createOMElement(Const.MESSAGE_SOURCE_KEY, pabk);
		OMElement OMTE = fac.createOMElement(Const.MESSAGE_TEXT, pabk);
		OMElement OMVT = fac.createOMElement(Const.MESSAGE_VALID_TO, pabk);
		OMEI.setText(externalId);
		OMMT.setText(messageType);
		OMRE.setText(recipient);
		OMSO.setText(sendOn);
		OMSK.setText(sourceKey);
		OMTE.setText(text);
		OMVT.setText(validTo);
		message.addChild(OMEI);
		message.addChild(OMMT);
		message.addChild(OMRE);
		message.addChild(OMSO);
		message.addChild(OMSK);
		message.addChild(OMTE);
		message.addChild(OMVT);		
		root.addChild(message);
		reqBody.addChild(root);
		System.out.println(OMTE.getText());
		return req;
	}
	

	private static String parseLines(Hashtable<String, String> data, Iterator<?> nodes) {
		StringBuffer sb = new StringBuffer();
		boolean notFirst = false;
		while(nodes.hasNext()) {
			String append = NotificationRequest.parseLine(data, (OMElement) nodes.next()).trim();
			if(append.length() > 0) {
				if(notFirst) {
					sb.append(Const.LS);
				}
				notFirst = true;
				sb.append(append);
			}
		}
		return sb.toString();
	}

	private static String parseLine(Hashtable<String, String> data, OMElement item) {
		StringBuffer sb = new StringBuffer();
		String msgType = item.getAttributeValue(new QName(item.getNamespaceURI(), Const.LINE_ARGUMENTS, item.getPrefix()));
		if(msgType != null && msgType.equalsIgnoreCase(data.get(Const.MESSAGE_TYPE))) {
			Iterator <?> nl = item.getChildElements();
			while(nl.hasNext()) {
				sb.append(NotificationRequest.parseLineItem(data, (OMElement) nl.next()));
			}
		}
		return sb.toString();
	}

	private static String parseLineItem(Hashtable<String, String> data, OMElement item) {
		String name = item.getLocalName();
		Object[] atts = NotificationRequest.normalizeArguments(data, (NotificationRequest.getAttributes(item.getAllAttributes())));
		if(name.equalsIgnoreCase(Const.LINE_ITEM_FUNCTION)) {
			try {
				String fName = item.getText();
				Class<?>[] params = NotificationRequest.getParamTypes(data, atts);
				Object[] args = getArguments(data, atts);
				return (String) NotificationRequest.class.getDeclaredMethod(fName, params).invoke(null, args);
			} catch (Exception e) {
				e.printStackTrace();
				return Const.EMPTY_STRING;
			}
		}
		else {
			return item.getText();
		}
		
	}

	private static Object[] getArguments(Hashtable<String, String> data, Object[] atts) {
		if(atts == null) {
			return new Object[]{};
		}
		else {
			Object[] args = new Object[atts.length + 1];
			for(int i = 0; i < args.length; i ++) {
				if(i > 0) {
					args[i] = atts[i - 1];
				}
				else {
					args[i] = data;
				}
			}
			return args;
		}
	}

	private static Class<?>[] getParamTypes(Hashtable<String, String> data, Object[] atts) {
		if(atts == null) {
			return new Class<?>[]{};
		}
		else {
			Class<?>[] cls = new Class<?>[atts.length + 1];
			for(int i = 0; i < cls.length; i ++) {
				if(i > 0) {
					cls[i] = String.class;
				}
				else {
					cls[i] = data.getClass();
				}
			}
			return cls;
		}
	}

	private static Object[] normalizeArguments(Hashtable<String, String> data, String[] strings) {
		if(strings != null) {
			Object[] array = new String[strings.length];
			for(int i = 0; i < array.length; i ++) {
				array[i] = strings[i] == null ? null : (data.get(strings[i]) == null ? strings[i] : data.get(strings[i]));
				array[i] = ((String) array[i]).equalsIgnoreCase(Const.NULL_STRING) ? null : array[i];
			}
			return array;
		}
		return null;
	}

	private static String[] getAttributes(Iterator<?> atts) {
		while(atts.hasNext()) {
			OMAttribute att = (OMAttribute) atts.next();
			if(att.getLocalName().equalsIgnoreCase(Const.LINE_ITEM_ARGUMENTS)) {
				return att.getAttributeValue().split(Const.LINE_ITEM_ARGUMENTS_SEPARATOR, Const.MAX_SUPPORTTED_ARGUMENTS);
			}
		}
		return null;
	}
	public static final String getTimestamp (Hashtable<String, String> data, String timestamp, String ext, String format, String loc) {
		Date date = parseTimestamp(timestamp, ext);
		Locale locale = findLocale(loc);
		if(date != null) {
			try {
				return new SimpleDateFormat(format, locale).format(date);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return Const.EMPTY_STRING;
	}
	
	public static final String getSourceKey(Hashtable<String, String> data, String _null) {
		return Const.MESSAGE_SOURCE_KEY_DEFUALT_VALUE;
	}
	
	public static final String normalizeAmount (Hashtable<String, String> data, String number, String loc, String inverse, String format) {
		Locale locale = findLocale(loc);
		try {
			double value = Double.parseDouble(number);
			value = Boolean.parseBoolean(inverse) ? 0 - value: value;
			return new DecimalFormat(format, DecimalFormatSymbols.getInstance(locale)).format(value);
		}
		catch (Exception e) {
			e.printStackTrace();
			return Const.EMPTY_STRING;
		}
	}
	
	private static Locale findLocale(String loc) {
		Locale[] locs = Locale.getAvailableLocales();
		for(int i = 0; i < locs.length; i ++) {
			if(locs[i].getCountry().equalsIgnoreCase(loc)) {
				return locs[i];
			}
		}
		return Locale.getDefault();
	}
	
	public static final String right(Hashtable<String, String> data, String string, String nr) {
		try {
			return string.substring(string.length() - Integer.parseInt(nr));
		}
		catch (Exception e) {
			e.printStackTrace();
			return Const.EMPTY_STRING;
		}
	}
	
	private static Date parseTimestamp(String timestamp, String ext) {
		try {
			long e = 1000 * 60 * 60 * 24;
			switch(ext.charAt(ext.length() - 1)) {
			case 'M':
				e /= 60;
			case 'H':
				e /= 24;
			case 'D':
				break;
			default:
				e = 0;
			}
			e *= Long.parseLong(ext.substring(0, ext.length() - 1));
			Calendar cal  = GregorianCalendar.getInstance();
			if(timestamp != null) {
				cal.clear();
				cal.set(Calendar.YEAR, Integer.parseInt(timestamp.substring(0, 4)));
				cal.set(Calendar.MONTH, Integer.parseInt(timestamp.substring(4, 6)) - 1);
				cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(timestamp.substring(6, 8)));
				cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timestamp.substring(8, 10)));
				cal.set(Calendar.MINUTE, Integer.parseInt(timestamp.substring(10, 12)));
				cal.set(Calendar.SECOND, Integer.parseInt(timestamp.substring(12, 14)));
			}
			return new Date(cal.getTimeInMillis() + e);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	private static final String[] parseParentheses(String[] chain) {
		int s = -1;
		int e = -1;
		for(int i = 0; i < chain.length; i ++) {
			if(chain[i].charAt(0) == '(') {
				s = i;
			}
			if(chain[i].charAt(chain[i].length() - 1) == ')') {
				e = i;
				if(s < 0) {
					return null;
				}
				else if (e == s) {
					chain[i] = chain[i].substring(1, chain[i].length() - 1);
					s = -1;
					e = s;
					continue;
				}
				String[] array = new String[e - s + 1];
				System.arraycopy(chain, s, array, 0, array.length);
				array[0] = array[0].replaceFirst(LEFT_PAR_MASK, Const.EMPTY_STRING);
				array[array.length - 1] = array[array.length - 1].replaceFirst(RIGHT_PAR_MASK, Const.EMPTY_STRING);
				String tmp = parseExpression(array);
				if(tmp == null || tmp.length() == 0) {
					return null;
				}
				for(int j = 1; chain[s].charAt(j) == '('; j ++) {
					tmp = "(" + tmp;
				}
				for(int j = chain[s + array.length -1].length() - 2; chain[s + array.length -1].charAt(j) == ')'; j --) {
					tmp += ")";
				}
				chain[s] = tmp;
				array = new String[chain.length - e + s];
				System.arraycopy(chain, 0, array, 0, s + 1);
				System.arraycopy(chain, e + 1, array, s + 1, chain.length - e - 1);
				i = -1;
				s = -1;
				e = s;
				chain = array;
			}
		}
		if(s < 0) {
			return chain;
		}
		return null;
	}
	
	public static final String expression(Hashtable<String, String> data, String exp, String ret) {
		String[] chain = checkExpression(data, exp, ret);
		if(chain == null) {
			return Const.EMPTY_STRING;
		}
		else {
			String tmp = parseExpression(chain);
			if(tmp == null) {
				return Const.EMPTY_STRING;
			}
			else {
				try {
					if(Double.parseDouble(tmp) == 0) {
						return Const.EMPTY_STRING;
					}
					else {
						return ret;
					}
				}
				catch(Exception e) {
					return Const.EMPTY_STRING;
				}
			}
		}
	}
	
	private static final String NOT = "NOT";
	private static final String AND = "AND";
	private static final String OR = "OR";
	private static final String EQUAL = "==";
	private static final String GREATER = ">";
	private static final String LESSER = "<";
	private static final String NOT_EQUAL = "!=";
	private static final String PARENTHESES_MASK = "\\(.+\\)";
	private static final String DIGITS_MASK = "\\d+";
	private static final String LEFT_PAR_MASK = "^\\(\\(*";
	private static final String RIGHT_PAR_MASK = "\\)*\\)$";
	
	private static String[] checkExpression(Hashtable<String, String> data, String exp, String ret) {
		String[] chain = exp.split(" ");
		for(int i = 0; i < chain.length; i ++) {
			while(chain[i].matches(PARENTHESES_MASK)) {
				chain[i] = chain[i].substring(1, chain[i].length() - 1);
			}
			if(chain[i].equalsIgnoreCase(NOT)) {
				continue;
			}
			if(chain[i].replaceFirst(LEFT_PAR_MASK, Const.EMPTY_STRING).equalsIgnoreCase(NOT)) {
				continue;
			}
			if(chain[i].equalsIgnoreCase(AND)) {
				continue;
			}
			if(chain[i].equalsIgnoreCase(OR)) {
				continue;
			}
			if(chain[i].equalsIgnoreCase(EQUAL)) {
				continue;
			}
			if(chain[i].equalsIgnoreCase(NOT_EQUAL)) {
				continue;
			}
			if(chain[i].equalsIgnoreCase(GREATER)) {
				continue;
			}
			if(chain[i].equalsIgnoreCase(LESSER)) {
				continue;
			}
			if(chain[i].equalsIgnoreCase(LESSER + EQUAL)) {
				continue;
			}
			if(chain[i].equalsIgnoreCase(GREATER + EQUAL)) {
				continue;
			}
			String tmp = chain[i].replaceFirst(LEFT_PAR_MASK, Const.EMPTY_STRING).replaceFirst(RIGHT_PAR_MASK, Const.EMPTY_STRING);
			if(!tmp.matches(DIGITS_MASK)) {
				tmp = data.get(tmp);
			}
			if(tmp != null) {
				for(int j = 0; chain[i].charAt(j) == '('; j ++) {
					tmp = "(" + tmp;
				}
				for(int j = chain[i].length() - 1; chain[i].charAt(j) == ')'; j --) {
					tmp += ")";
				}
				chain[i] = tmp;
				continue;
			}
			return null;
		}
		return chain;
	}

	private static String parseExpression(String[] chain) {
		chain = parseParentheses(chain);
		if(chain == null) {
			return null;
		}
		if(chain.length == 1) {
			return chain[0];
		}
		chain = parseNot(chain);
		if(chain == null) {
			return null;
		}
		if(chain.length == 1) {
			return chain[0];
		}
		chain = parseRelated(chain);
		if(chain == null) {
			return null;
		}
		if(chain.length == 1) {
			return chain[0];
		}
		chain = parseEquality(chain);
		if(chain == null) {
			return null;
		}
		if(chain.length == 1) {
			return chain[0];
		}
		chain = parseLogical(chain);
		if(chain == null) {
			return null;
		}
		if(chain.length == 1) {
			return chain[0];
		}
		return null;
	}
	
	private static String[] parseLogical(String[] chain) {
		for(int i = 0; i < chain.length; i ++) {
			int o = -1;
			if (chain[i].equalsIgnoreCase(AND)) {
				o = 1;
			}
			else if (chain[i].equalsIgnoreCase(OR)) {
				o = 2;
			}
			else {
				continue;
			}
			if(o > 0 && (i < 1) || (i + 1 == chain.length)){
				return null;
			}
			String tmp = null;
			try {
				double a = Double.parseDouble(chain[i - 1]);
				double b = Double.parseDouble(chain[i + 1]);
				switch(o) {
				case 1:
					tmp = (a > 0) && (b > 0) ? "1" : "0";
					break;
				case 2:
					tmp = (a > 0) || (b > 0) ? "1" : "0";
					break;
				default:
					return null;
				}
			}
			catch(Exception ee) {
				ee.printStackTrace();
				return null;
			}
			chain[i - 1] = tmp;
			String[] array = new String[chain.length - 2];
			System.arraycopy(chain, 0, array, 0, i);
			System.arraycopy(chain, i + 2, array, i, chain.length - i - 2);
			chain = array;
			i --;
		}
		return chain;
	}
	
	private static String[] parseEquality(String[] chain) {
		for(int i = 0; i < chain.length; i ++) {
			int o = -1;
			if (chain[i].equalsIgnoreCase(EQUAL)) {
				o = 1;
			}
			else if (chain[i].equalsIgnoreCase(NOT_EQUAL)) {
				o = 2;
			}
			else {
				continue;
			}
			if(o > 0 && (i < 1) || (i + 1 == chain.length)){
				return null;
			}
			String tmp = null;
			try {
				double a = Double.parseDouble(chain[i - 1]);
				double b = Double.parseDouble(chain[i + 1]);
				switch(o) {
				case 1:
					tmp = a == b ? "1" : "0";
					break;
				case 2:
					tmp = a != b ? "1" : "0";
					break;
				default:
					return null;
				}
			}
			catch(Exception ee) {
				ee.printStackTrace();
				return null;
			}
			chain[i - 1] = tmp;
			String[] array = new String[chain.length - 2];
			System.arraycopy(chain, 0, array, 0, i);
			System.arraycopy(chain, i + 2, array, i, chain.length - i - 2);
			chain = array;
			i --;
		}
		return chain;
	}
	
	private static String[] parseRelated(String[] chain) {
		for(int i = 0; i < chain.length; i ++) {
			int o = -1;
			if (chain[i].equalsIgnoreCase(GREATER)) {
				o = 1;
			}
			else if (chain[i].equalsIgnoreCase(LESSER)) {
				o = 2;
			}
			else if (chain[i].equalsIgnoreCase(GREATER + EQUAL)) {
				o = 3;
			}
			else if (chain[i].equalsIgnoreCase(GREATER + EQUAL)) {
				o = 4;
			}
			else {
				continue;
			}
			if(o > 0 && (i < 1) || (i + 1 == chain.length)){
				return null;
			}
			String tmp = null;
			try {
				double a = Double.parseDouble(chain[i - 1]);
				double b = Double.parseDouble(chain[i + 1]);
				switch(o) {
				case 1:
					tmp = a > b ? "1" : "0";
					break;
				case 2:
					tmp = a < b ? "1" : "0";
					break;
				case 3:
					tmp = a >= b ? "1" : "0";
					break;
				case 4:
					tmp = a <= b ? "1" : "0";
					break;
				default:
					return null;
				}
			}
			catch(Exception ee) {
				ee.printStackTrace();
				return null;
			}
			chain[i - 1] = tmp;
			String[] array = new String[chain.length - 2];
			System.arraycopy(chain, 0, array, 0, i);
			System.arraycopy(chain, i + 2, array, i, chain.length - i - 2);
			chain = array;
			i --;
		}
		return chain;
	}

	private static String[] parseNot(String[] chain) {
		for(int i = 0; i < chain.length; i ++) {
			if(chain[i].equalsIgnoreCase(NOT)) {
				if(i + 1 == chain.length) {
					return null;
				}
				try {
					int x = Integer.parseInt(chain[i + 1]);
					if(x == 0) {
						chain[i] = "1";
					}
					else {
						chain[i] = "0";
					}
					String [] array = new String[chain.length - 1];
					System.arraycopy(chain, 0, array, 0, i + 1);
					System.arraycopy(chain, i + 2, array, i + 1, chain.length - i - 2);
					chain = array;
				}
				catch(Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		return chain;
	}
	
	public static final String getCurrency(Hashtable<String, String> data, String value) {
		if(value.matches(Const.ISO_CURRENCY_MASK)) {
			return value;
		}
		else if (value.matches(Const.NUMERIC_CURRENCY_MASK)) {
			if(curx == null) {
				curx = _Stub.loadXMLFileOM(Const.CURRENCY_LIST_FILE, false);
			}
			if(curx != null) {
				return getCurrency(value);
			}
		}
		return Const.EMPTY_STRING;
	}
	
	private static String getCurrency(String num) {
		String value = foundCurr.get(num);
		if(value == null) {
			//Iterator<?> ite = curx.getChildrenWithLocalName(Const.CURRENCY_LIST_NAME);
			//if(ite.hasNext()) {
				Iterator<?> ite = curx.getChildrenWithLocalName(Const.CURRENCY_ITEM_NAME);
				while(ite.hasNext()) {
					try {
						OMElement elem = (OMElement) ite.next();
						if(num.equals(((OMElement) elem.getChildrenWithLocalName(Const.CURRENCY_CODE_NAME).next()).getText())) {
							String iso = ((OMElement) elem.getChildrenWithLocalName(Const.CURRENCY_ISO_NAME).next()).getText();
							foundCurr.put(num, iso);
							return iso;
						}
					}
					catch(Exception e) {
						e.printStackTrace();
						break;
					}
				}
			//}
			value = Const.EMPTY_STRING;
		}
		return value;
	}
	
	public static final String get(Hashtable<String, String> data, String value) {
		return value;
	}
	public static final String getMessageType(Hashtable<String, String> data, String notsType) {
		try {
			switch(Integer.parseInt(notsType)) {
			case 1:
				return Const.SMS_MESSAGE;
			case 2:
				return Const.EMAIL_MESSAGE;
			default:
				return Const.EMPTY_STRING;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return Const.EMPTY_STRING;
		}
	}
}