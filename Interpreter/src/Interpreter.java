import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.pabk.util.interpreter.Language;
import org.xml.sax.SAXException;


public class Interpreter {

	public static void main(String[] args) {
		try {
			Language lang = new Language("jano.xml");
		} catch (SAXException | IOException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
