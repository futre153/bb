package com.bb.ofkpuweb.login;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pabk.html.Doctype;
import org.pabk.html.Html;
import org.pabk.html.Link;

/**
 * Servlet implementation class LoginFailed
 */
@WebServlet("/LoginFailed")
public class LoginFailed extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Properties props;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginFailed() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("Inicializacia servleta");
        super.init(config);
        try {
        	String loginProPath = config.getInitParameter(FormLogin.LOGIN_PROPERTIES_PARAM_NAME);
        	System.out.println(FormLogin.getResourcePath(FormLogin.class) );
        	props = new Properties();
	        props.loadFromXML(FormLogin.class.getResourceAsStream(FormLogin.getResourcePath(FormLogin.class) + FormLogin.DEFAULT_PROPERTIES));
	        props = new Properties(props);
	        props.loadFromXML(config.getServletContext().getResourceAsStream(loginProPath));
        }
        catch (IOException e) {
        	throw new ServletException (e);
        }
        
        
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding(props.getProperty(FormLogin.LOGIN_ENCODING_KEY));
		Html html = Html.getInstance(props.getProperty(FormLogin.LOGIN_TITLE_KEY));
		html.setDoctype(Doctype.HTML_5);
		html.addMetadata(null, null, null, null, props.getProperty(FormLogin.LOGIN_ENCODING_KEY));
		html.addLink(Link.STYLEHEET, new String[] {FormLogin.STYLESHEET_RELATION, FormLogin.TYPE_TEXT_CSS, FormLogin.STYLE_URL});
		html.getBody().appendChild(FormLogin.getMainFrame(request, props, FormLogin.ERROR));
		
		
		//html.getBody().appendChild(TextTag.getInstance(props.getProperty("test")));
		
		
		html.doFinal(response.getWriter());
		response.getWriter().flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
