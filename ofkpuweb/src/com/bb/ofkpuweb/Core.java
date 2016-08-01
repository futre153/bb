package com.bb.ofkpuweb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pabk.html.A;
import org.pabk.html.Button;
import org.pabk.html.Div;
import org.pabk.html.Doctype;
import org.pabk.html.Html;
import org.pabk.html.Link;
import org.pabk.html.Tag;
import org.pabk.html.TextTag;
import org.pabk.web.db.DBConnector;

/**
 * Servlet implementation class Core
 */
abstract class Core extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	//character variables
	protected static final char NULL_CONTROL_CHAR			= 0x00;
	protected static final char BELL_CONTROL_CHAR			= 0x07;
	protected static final char BACKSPACE_CONTROL_CHAR		= 0x08;
	protected static final char HORIZONTAL_TAB_CONTROL_CHAR	= 0x09;
	protected static final char LINE_FEED_CONTROL_CHAR		= 0x0a;
	protected static final char VERTICAL_TAB_CONTROL_CHAR	= 0x0b;
	protected static final char FORM_FEED_CONTROL_CHAR		= 0x0c;
	protected static final char CARRIAGE_RETURN_CONTROL_CHAR= 0x0d;

	protected static final char SPACE_CHAR = ' ';
	protected static final char BACKSLASH_CHAR = '\\';
	public static final char DOT_CHAR = '.';
	public static final char SLASH_CHAR = '/';
	protected static final char LESS_THAN_CHAR = '<';
	protected static final char GREATHER_THAN_CHAR = '>';
	protected static final char AND_CHAR = '&';
	protected static final char COMMA_CHAR = ',';
	protected static final char PLUS_CHAR = '+';
	protected static final char UNDERSCORE_CHAR = '_';
	protected static final char ZERO_CHAR = '0';
	protected static final char UPPER_Y_CHAR = 'Y';
	protected static final char UPPER_N_CHAR = 'N';
	protected static final char LEFT_BRACKET_CHAR = '(';
	protected static final char RIGHT_BRACKET_CHAR = ')';

	private static final String PROPERTIES_CONST = ".properties.xml";
	private static final String CONFIG_PATH = "conf";
	private static final String DB_CONFIG_PATH = CONFIG_PATH + SLASH_CHAR + "db";
	
	private static final String DSN_KEY = "core.dsn";

	private static final String CREATE_DB_SQL = "create_db.sql";

	

	
	
	private Properties props = null;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    protected Core() {
        super();
 
        // TODO Auto-generated constructor stub
    }
    
    
    
    private static void createDatabase(ServletConfig config, Connection con, String createDBBatch) throws IOException {
    	InputStream stream = null;
    	Statement statement = null;
    	try {
    		con.setAutoCommit(false);
    		stream = config.getServletContext().getResourceAsStream(DB_CONFIG_PATH + SLASH_CHAR + createDBBatch);
    		statement = con.createStatement();
    		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    		StringBuffer sb = new StringBuffer();
    		boolean notNull = false;
    		while (true) {
    			String line = reader.readLine();
    			if(line != null) {
    				if(line.length() == 0) {
    					continue;
    				}
    				else {
    					if(line.charAt(line.length() - 1) != ';') {
    						sb.append(SPACE_CHAR);
        					sb.append(line);
        					continue;
    					}
    					else {
    						line = line.substring(0, line.length() - 1);
    					}
    				}
    			}
    			if(line != null && line.length() > 0) {
    				sb.append(SPACE_CHAR);
					sb.append(line);
    			}
    			String query = sb.toString().trim();
    			if(query.length() > 0) {
    				System.out.println(query);
    				statement.addBatch(query);
    				notNull = true;
    				sb = new StringBuffer();
    			}
    			if(line == null) {
    				break;
    			}
    		}
    		if(notNull) {
    			statement.executeBatch();
    			con.commit();
    		}
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		try {con.rollback();} catch (Exception e1) {}
    		throw new IOException(e);
    	}
    	finally {
    		if (stream != null) {
    			stream.close();
    		}
    		if(statement != null) {
    			try {con.setAutoCommit(true);} catch (Exception e) {}
    			try {statement.close();} catch (Exception e) {}
    		}
    	}
    }
    
    private static Properties loadProperties(ServletConfig config, Properties def, Class<?> cl) throws ServletException {
    	Properties props = new Properties(def == null ? new Properties() : def);
       	try {
       		System.out.println("REQOURCE PATH");
       		System.out.println((SLASH_CHAR + cl.getPackage().getName().replace(DOT_CHAR, SLASH_CHAR) + SLASH_CHAR + cl.getSimpleName().toLowerCase() + PROPERTIES_CONST));
       		System.out.println("SERVLET PATH");
       		System.out.println(CONFIG_PATH + SLASH_CHAR + cl.getSimpleName().toLowerCase() + PROPERTIES_CONST);
       		System.out.println(config.getServletContext().getContextPath());
       		props.loadFromXML(cl.getResourceAsStream((SLASH_CHAR + cl.getPackage().getName().replace(DOT_CHAR, SLASH_CHAR) + SLASH_CHAR + cl.getSimpleName().toLowerCase() + PROPERTIES_CONST)));
       		props.loadFromXML(config.getServletContext().getResourceAsStream(CONFIG_PATH + SLASH_CHAR + cl.getSimpleName().toLowerCase() + PROPERTIES_CONST));
       		
       	}
       	catch (Exception e) {
       		e.printStackTrace();
       		throw new ServletException(e);
       	}
       	
       	return props;
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		if(props == null) {
        	props = Core.loadProperties(config, props, Core.class);
        	Connection con = null;
           	try {
	        	con = DBConnector.lookup(props.getProperty(DSN_KEY));
	       		createDatabase(config, con, CREATE_DB_SQL);
	       		con.close();
           	}
           	catch (Exception e) {
           		e.printStackTrace();
           	}
           	finally {
           		try {if (con != null) con.close();} catch (Exception e) {}
           	}
        }
		props = Core.loadProperties(config, props, this.getClass());
	}
	
	protected Properties getProperties() {
		return this.props;
	}
	
	protected static final String CLASS_ATT_NAME = "class";
	private static final String DEFAULT_PAGE_TITLE = "Prosím zadajte titulok stránky";
	private static final String PAGE_TITLE_ATT_NAME = "core.pageTitle.att.name";
	private static final String PAGE_ENCODING_ATT_NAME = "core.pageEncoding.att.name";
	protected static final String PAGE_CONTENT_ATT_NAME = "core.pageContent.att.name";

	private static final String PAGE_CONTENT_ERROR_CLASS = "page-content-error";

	private static final String PAGE_CONTENT_ERROR_MESSAGE_KEY = "core.pageContent.errorMessage";

	private static final String DEFAULT_PAGE_ENCODING = "UTF-8";

	private static final String ERROR_MESSAGE_OUTER_FRAME_CLASS = "error-message-outer-frame";
	private static final String ERROR_MESSAGE_INNER_FRAME_CLASS = "error-message-inner-frame";
	private static final String ERROR_MESSAGE_TEXT_FRAME_CLASS = "error-message-text-frame";

	private static final String REFRESH_HTTP_EQUIV = "refresh";
	private static final String REDIRECT_CONTENT = "%d; URL=%s";
	private static final String NAME_ATT_NAME = "name";
	private static final String VALUE_ATT_NAME = "value";
	private static final String TYPE_ATT_NAME = "type";
	protected static final String ID_ATT_NAME = "id";
	protected static final String ONCLICK_ATT_NAME = "onclick";
	protected static final String ONMOUSEOVER_ATT_NAME = "onmouseover";
	protected static final String ONMOUSEOUT_ATT_NAME = "onmouseout";
	private static final String DISABLED_ATT_NAME = "disabled";
	private static final String EMPTY = "";

	private static final String ERROR_MESSAGE_BUTTON_FRAME_CLASS = "error-message-button-frame";
	private static final String TYPE_BUTTON = "button";
	private static final String MASK_LOCATION = "core.mask.location";
	private static final int DEFAULT_ERROR_MESSAGE_REFRESH_RATE = 30;
	private static final String ERROR_MESSAGE_REFERESH_RATE_KEY = "core.pageContent.errorMessage.refereshRate";
	private static final String ERROR_MESSAGE_BUTTON_TEXT_KEY = "core.pageContent.errorNessage.buttonText";
	protected static final String PAGE_STYLE_ATT_NAME = "core.pageStyle.att.name";
	private static final String STYLESHEET_RELATION = "stylesheet";
	private static final String TYPE_TEXT_CSS = "text/css";
	private static final String ERROR_MESSAGE_STYLE_URL = "core.pageContent.errorMessage.styleUrl";

	private static final String ERROR_FRAME = "frame";
	private static final String FRAME1_CLASS = "frame1";
	private static final String FRAME2_CLASS = "frame2";
	private static final String FRAME3_CLASS = "frame3";
	private static final String FRAME4_CLASS = "frame4";
	private static final String FRAME5_CLASS = "frame5";
	private static final String FRAME6_CLASS = "frame6";

	private static final String DEFAULT_LANGUAGE = "sk";

	private static final String LANG_ATT_NAME = "lang";
	
	
	
	
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Properties props = getProperties();
		
		//temporary request attributes
		Object title = request.getAttribute(PAGE_TITLE_ATT_NAME);
		Object pageEncoding = request.getAttribute(PAGE_ENCODING_ATT_NAME);
		Object tag = request.getAttribute(PAGE_CONTENT_ATT_NAME);
		Object styleUrl = request.getAttribute(PAGE_STYLE_ATT_NAME);
		
		
		int errorMessgeRefreshRate = DEFAULT_ERROR_MESSAGE_REFRESH_RATE;
		try {errorMessgeRefreshRate = Integer.parseInt(props.getProperty(ERROR_MESSAGE_REFERESH_RATE_KEY));}catch (Exception e) {}
		
		Html page = Html.getInstance(title != null && title instanceof String ? ((String) title) : DEFAULT_PAGE_TITLE);
		page.setDoctype(Doctype.HTML_5);
		//page style settings
		if (styleUrl != null && styleUrl instanceof String) page.addLink(Link.STYLEHEET, new String[] {STYLESHEET_RELATION, TYPE_TEXT_CSS, request.getServletContext().getContextPath() + Core.SLASH_CHAR + styleUrl});
		// page encoding settings
		response.setCharacterEncoding(pageEncoding != null && pageEncoding instanceof String ? (String) pageEncoding : DEFAULT_PAGE_ENCODING);
		page.addMetadata(null, null, null, null, pageEncoding != null && pageEncoding instanceof String ? (String) pageEncoding : DEFAULT_PAGE_ENCODING);
		page.setAttribute(LANG_ATT_NAME, DEFAULT_LANGUAGE);
		PrintWriter out = response.getWriter();
		
		tag = tag != null && tag instanceof Tag ? tag : Core.getErrorMessage (props, page, props.getProperty(PAGE_CONTENT_ERROR_MESSAGE_KEY), request.getRequestURL().toString(), errorMessgeRefreshRate, request.getRequestURL().toString(), props.getProperty(ERROR_MESSAGE_BUTTON_TEXT_KEY), request.getServletContext().getContextPath() + Core.SLASH_CHAR + props.getProperty(ERROR_MESSAGE_STYLE_URL));
		page.getBody().appendChild((Tag) tag);
		
		page.doFinal(out);
		
		
		
		
		
		
		out.flush();
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	private static Button getInstance(String name, String value, String type, String text, String onclick, boolean disabled) {
		Button button = Button.getInstance(text);
		if(name != null) {
			button.setAttribute(Core.NAME_ATT_NAME, name);
		}
		if(value != null) {
			button.setAttribute(Core.VALUE_ATT_NAME, value);
		}
		if(type != null) {
			button.setAttribute(Core.TYPE_ATT_NAME, type);
		}
		if(onclick != null) {
			button.setAttribute(Core.ONCLICK_ATT_NAME, onclick);
		}
		if(disabled) {
			button.setAttribute(Core.DISABLED_ATT_NAME, Core.EMPTY);
		}
		return button;
	}
	
	protected static Tag getErrorMessage(Properties props, Html page, String message, String url, int refreshRate, String refreshUrl, String buttonText, String styleUrl) {
		page.addMetadata(REFRESH_HTTP_EQUIV, null, null, String.format(REDIRECT_CONTENT, refreshRate, refreshUrl), null);
		page.addLink(Link.STYLEHEET, new String[] {STYLESHEET_RELATION, TYPE_TEXT_CSS, styleUrl});
		Tag innerFrame = Core.getDiv(ERROR_MESSAGE_INNER_FRAME_CLASS, Core.getDiv(ERROR_MESSAGE_TEXT_FRAME_CLASS, TextTag.getInstance(message)));
		innerFrame.appendChild(Core.getDiv(ERROR_MESSAGE_BUTTON_FRAME_CLASS, Core.getInstance(null, null, TYPE_BUTTON, buttonText, String.format(props.getProperty(MASK_LOCATION), url), false)));
		Tag div = Core.getDiv(ERROR_FRAME, Core.getDiv(FRAME1_CLASS, Core.getDiv(FRAME2_CLASS, Core.getDiv(FRAME3_CLASS, Core.getDiv(FRAME4_CLASS, Core.getDiv(FRAME5_CLASS, Core.getDiv(FRAME6_CLASS, Core.getDiv(null, Core.getDiv(null, Core.getDiv(PAGE_CONTENT_ERROR_CLASS, Core.getDiv(ERROR_MESSAGE_OUTER_FRAME_CLASS, innerFrame)))))))))));
		return div;
	}
	
	protected static Tag getLink (String url, String className, Object child) {
		A a = A.getInstance(url);
		if(className != null) {
			a.setAttribute(CLASS_ATT_NAME, className);
		}
		if(child != null) {
			a.removeChild(a.lastChild());
			if(child instanceof String) {
				a.appendText((String) child);
			}
			else if (child instanceof Tag) {
				a.appendChild((Tag) child);
			}
			else {
				a.appendText(child.toString());
			}
		}
		return a;
	}
	
	protected static Tag getDiv(String className, Tag child) {
		Tag div = Div.getInstance();
		if(className != null) {
			div.setAttribute(CLASS_ATT_NAME, className);
		}
		if(child != null) {
			div.appendChild(child);
		}
		return div;
	}

}
