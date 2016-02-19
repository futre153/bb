package org.pabk.util.interpreter;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class Language {
	
	private static final char DEFAULT_ARGUMENT_PREFIX = '-';
	private static final char DEFAULT_PARAMETER_SEPARATOR = ' ';
	private static final char[] DEFAULT_PARAMETER_QUOTES = {'"', '\''};
	private static final String DEFAULT_PARAMETER_ESCAPE_CHARACTERS = "\\";
	private static final String DEFAULT_NOT_BREAK_CHARACTERS = "\\";
	private static final String LANGUAGE_SCHEMA_RESOURCE = "language.xsd";
	private char argPfx = DEFAULT_ARGUMENT_PREFIX;
	private char parSep = DEFAULT_PARAMETER_SEPARATOR;
	private char[] parQuo = DEFAULT_PARAMETER_QUOTES;
	private String parEsc = DEFAULT_PARAMETER_ESCAPE_CHARACTERS;
	private String notBre = DEFAULT_NOT_BREAK_CHARACTERS;
	
	public Language (String langPath) throws SAXException, IOException, ParserConfigurationException {
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema sch = factory.newSchema(Language.class.getResource('/' + Language.class.getPackage().toString().split(" ")[1].replace('.', '/') + '/' + LANGUAGE_SCHEMA_RESOURCE));
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document doc = dbf.newDocumentBuilder().parse(new File(langPath));
		sch.newValidator().validate(new DOMSource(doc));
	}

	public char getArgPfx() {
		return argPfx;
	}

	public void setArgPfx(char argPfx) {
		this.argPfx = argPfx;
	}

	public char getParSep() {
		return parSep;
	}

	public void setParSep(char parSep) {
		this.parSep = parSep;
	}

	public char[] getParQuo() {
		return parQuo;
	}

	public void setParQuo(String parQuo) {
		this.parQuo = parQuo.toCharArray();
	}

	public String getParEsc() {
		return parEsc;
	}

	public void setParEsc(String parEsc) {
		this.parEsc = parEsc;
	}

	public String getNotBre() {
		return notBre;
	}

	public void setNotBre(String notBre) {
		this.notBre = notBre;
	}
}
