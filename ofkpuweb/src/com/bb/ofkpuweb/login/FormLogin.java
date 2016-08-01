package com.bb.ofkpuweb.login;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pabk.html.Div;
import org.pabk.html.Doctype;
import org.pabk.html.Form;
import org.pabk.html.Html;
import org.pabk.html.Input;
import org.pabk.html.Link;
import org.pabk.html.Tag;
import org.pabk.html.TextTag;
import org.pabk.html.Button;

/**
 * Servlet implementation class FormLogin
 */

public final class FormLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;

	static final String DEFAULT_PROPERTIES = "default.login.servlet.properties.xml";
	static final String LOGIN_PROPERTIES_PARAM_NAME = "login.properties.url";
	private static final char DOT_CHAR = '.';
	private static final char SLASH_CHAR = '/';
	private static final char VERT_LINE_CHAR = '|';
	private static final char BACKSLASH_CHAR = '\\';

	private static final String CLASS_ATT_NAME = "class";
	private static final String STYLE_ATT_NAME = "style";
	private static final String ID_ATT_NAME = "id";
	private static final String MAX_LENGTH_ATT_NAME = "maxLength";
	private static final String TYPE_ATT_NAME = "type";
	private static final String FORM_ATT_NAME = "form";
	
	private static final String LOGIN_PAGE_CLASS = "login-page";
	private static final String AUTH_DM_CLASS = "auth_DM";
	private static final String AUTH_CPC_CLASS = "auth_CPC";
	private static final String AUTH_APC_CLASS = "auth_APC";
	private static final String AUTH_LOC_CLASS = "auth_LOC";
	private static final String AUTH_FM_CLASS = "auth_FM";
	private static final String AUTH_KL_CLASS = "auth_KL";
	private static final String AUTH_JL_CLASS = "auth_JL";
	private static final String AUTH_JM_CLASS = "auth_JM";
	private static final String AUTH_KWC_CLASS = "auth_KWC";
	private static final String AUTH_NL_CLASS = "auth_NL";
	private static final String AUTH_JWC_CLASS = "auth_JWC";
	private static final String AUTH_FIC_CLASS = "auth_FIC";
	private static final String AUTH_LWC_CLASS = "auth_LWC";
	private static final String AUTH_MWC_CLASS = "auth_MWC";
	private static final String AUTH_JK_CLASS = "auth_JK";
	private static final String AUTH_LBC_CLASS = "auth_LBC";
	private static final String AUTH_GM_CLASS = "auth_GM";
	private static final String AUTH_OL_CLASS = "auth_OL";
	private static final String AUTH_HL_CLASS = "auth_HL";
	private static final String AUTH_KM_CLASS = "auth_KM";
	private static final String FRAME1_CLASS = "frame1";
	private static final String FRAME2_CLASS = "frame2";
	private static final String FRAME3_CLASS = "frame3";
	private static final String FRAME4_CLASS = "frame4";
	private static final String FRAME5_CLASS = "frame5";
	private static final String FRAME6_CLASS = "frame6";
	private static final String FRAME7_CLASS = "frame7";
	private static final String FRAME8_CLASS = "frame8";
	private static final String FRAME9_CLASS = "frame9";
	
	private static final String ERROR_CONTAINER_NOT_VISIBLE_STYLE = "display: none;";
	private static final String INPUT_TEXT_TYPE = "text";
	private static final int LOGIN_USERNAME_INPUT_MAX_LENGTH_VALUE = 150;
		//private static final String LOGIN_BACKGROUND_STYLE = "background-image:url('images/LoginBackground.gif');";
	private static final String LOGIN_BACKGROUND_STYLE = "background-image:url(\"%s\");";
	private static final String SUBMIT_BUTTON_TYPE = "submit";
	private static final String LOGIN_PASSWORD_STYLE = "width:100%;";
	private static final String INPUT_PASSWORD_TYPE = "password";
	
	static final String LOGIN_TITLE_KEY = "login.title";
	static final String LOGIN_ENCODING_KEY = "login.encoding";
	private static final String ERROR_CONTAINER_CAPTION_KEY = "login.error.caption";
	private static final String ERROR_MESSAGE_KEY = "login.error.message";
	private static final String LOGIN_BACKGROUND_IMAGE_KEY = "login.background.imageURL";
	private static final String LOGIN_CONTAINER_MENU_KEY = "login.menu";
	private static final String APPLICATION_FULLNAME_KEY = "application.fullName";
	private static final String LOGIN_USERNAME_LABEL_KEY = "login.username.label";
	private static final String LOGIN_USERNAME_NAME_KEY = "login.username.name";
	private static final String LOGIN_USERNAME_ID_KEY = "login.username.id";
	private static final String LOGIN_FORM_ACTION_KEY = "login.form.action";
	private static final String LOGIN_FORM_METHOD_KEY = "login.form.method";
	private static final String LOGIN_FORM_ID_KEY = "login.form.id";
	private static final String LOGIN_PASSWORD_LABEL_KEY = "login.password.label";
	private static final String LOGIN_SUBMIT_VALUE_KEY = "login.submit.value";
	private static final String LOGIN_SUBMIT_ID_KEY = "login.submit.id";
	private static final String LOGIN_PASSWORD_NAME_KEY = "login.password.name";
	private static final String LOGIN_PASSWORD_ID_KEY = "login.password.id";
	private static final String LOGIN_TEXT_ID_KEY = "login.text.id";
	private static final String LOGIN_TEXT_KEY = "login.text";
	private static final String LOGIN_FOOTER_TEXT_KEY = "login.footer.text";
	
	private static final int NO_ERROR = 0;

	static final String STYLESHEET_RELATION = "stylesheet";

	static final String TYPE_TEXT_CSS = "text/css";

	static final String STYLE_URL = "conf/theme.css";

	public static final int ERROR = 1;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	
	private Properties props;
		
    public FormLogin() throws IOException, ServletException {
        super();
        
        //props.loadFromXML(this.getServletContext().getResourceAsStream(config.getInitParameter(LOGIN_PROPERTIES_PARAM_NAME)));
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        //System.out.println("Inicializacia servleta");
    	System.out.println("admnin='" +  org.apache.catalina.realm.RealmBase.Digest("admin", "SHA", "UTF-8") + "'");
        super.init(config);
        try {
        	String loginProPath = config.getInitParameter(LOGIN_PROPERTIES_PARAM_NAME);
        	//System.out.println(FormLogin.getResourcePath(FormLogin.class) + DEFAULT_PROPERTIES);
        	props = new Properties();
	        props.loadFromXML(FormLogin.class.getResourceAsStream(FormLogin.getResourcePath(FormLogin.class) + DEFAULT_PROPERTIES));
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
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		response.setCharacterEncoding(props.getProperty(LOGIN_ENCODING_KEY));
		Html html = Html.getInstance(props.getProperty(LOGIN_TITLE_KEY));
		html.setDoctype(Doctype.HTML_5);
		html.addMetadata(null, null, null, null, props.getProperty(LOGIN_ENCODING_KEY));
		html.addLink(Link.STYLEHEET, new String[] {STYLESHEET_RELATION, TYPE_TEXT_CSS, STYLE_URL});
		html.getBody().appendChild(FormLogin.getMainFrame(request, props, NO_ERROR));
		
		
		//html.getBody().appendChild(TextTag.getInstance(props.getProperty("test")));
		
		
		html.doFinal(response.getWriter());
		response.getWriter().flush();
	}
	
	static Tag getMainFrame(HttpServletRequest request, Properties props, int error) {
		Tag main = Div.getInstance();
		main.setAttribute(CLASS_ATT_NAME, LOGIN_PAGE_CLASS);
		Tag parent = main;
		Tag child = Div.getInstance();
		child.setAttribute(CLASS_ATT_NAME, FRAME1_CLASS);
		parent.appendChild(child);
		parent = child;
		child = Div.getInstance();
		child.setAttribute(CLASS_ATT_NAME, AUTH_DM_CLASS + " " + FRAME2_CLASS);
		parent.appendChild(child);
		parent = child;
		child = Div.getInstance();
		child.setAttribute(CLASS_ATT_NAME, FRAME3_CLASS);
		parent.appendChild(child);
		parent = child;
		child = Div.getInstance();
		child.setAttribute(CLASS_ATT_NAME, FRAME4_CLASS);
		parent.appendChild(child);
		parent = child;
		child = Div.getInstance();
		child.setAttribute(CLASS_ATT_NAME, FRAME5_CLASS);
		parent.appendChild(child);
		parent = child;
		child = Div.getInstance();
		child.setAttribute(CLASS_ATT_NAME, FRAME6_CLASS);
		parent.appendChild(child);
		parent = child;
		child = Div.getInstance();
		parent.appendChild(child);
		parent = child;
		child = Div.getInstance();
		parent.appendChild(child);
		parent = child;
		child = FormLogin.getErrorContainer(props, error);
		parent.appendChild(child);
		child = FormLogin.getLoginContainer(props);
		parent.appendChild(child);
		child = Div.getInstance();
		child.setAttribute(CLASS_ATT_NAME, AUTH_HL_CLASS);
		child.appendChild(TextTag.getInstance(props.getProperty(LOGIN_FOOTER_TEXT_KEY)));
		parent.appendChild(child);
		return main;
	}

	private static Tag getLoginContainer(Properties props) {
		Tag login = Div.getInstance();
		login.setAttribute(CLASS_ATT_NAME, AUTH_FM_CLASS + " " + FRAME9_CLASS);
		login.setAttribute(STYLE_ATT_NAME, String.format(LOGIN_BACKGROUND_STYLE, props.getProperty(LOGIN_BACKGROUND_IMAGE_KEY)));
		Tag child = Div.getInstance();
		child.setAttribute(CLASS_ATT_NAME, AUTH_KL_CLASS);
		login.appendChild(child);
		Tag parent = child;
		String menu = null;
		String regex = new String(new char[]{BACKSLASH_CHAR, VERT_LINE_CHAR});
		for(int i = 0; (menu = props.getProperty(LOGIN_CONTAINER_MENU_KEY + DOT_CHAR + i)) != null; i ++) {
			child = Div.getInstance();
			String[] s = menu.split(regex);
			child.setAttribute(CLASS_ATT_NAME, AUTH_JL_CLASS);
			if(s[0].length() > 0) {
				child.setAttribute(ID_ATT_NAME, s[0]);
			}
			if(s[1].length() > 0) {
				child.setAttribute(STYLE_ATT_NAME, s[1]);
			}
			if(s[2].length() > 0) {
				child.appendChild(TextTag.getInstanceNBSP(s[2]));
			}
			parent.appendChild(child);
		}
		parent = Div.getInstance();
		parent.setAttribute(CLASS_ATT_NAME, AUTH_JM_CLASS);
		parent.appendChild(TextTag.getInstance(props.getProperty(APPLICATION_FULLNAME_KEY)));
		login.appendChild(parent);
		
		parent = Form.getInstance(props.getProperty(LOGIN_FORM_ACTION_KEY), props.getProperty(LOGIN_FORM_METHOD_KEY));
		parent.setAttribute(CLASS_ATT_NAME, AUTH_LWC_CLASS);
		parent.setAttribute(ID_ATT_NAME, props.getProperty(LOGIN_FORM_ID_KEY));
		
		login.appendChild(parent);
		parent.appendChild(FormLogin.getUserNameContainer(props));
		parent.appendChild(FormLogin.getPasswordContainer(props));
		parent.appendChild(FormLogin.getSubmitContainer(props));
		child = Div.getInstance();
		child.setAttribute(CLASS_ATT_NAME, AUTH_OL_CLASS);
		child.setAttribute(ID_ATT_NAME, props.getProperty(LOGIN_TEXT_ID_KEY));
		child.appendChild(TextTag.getInstance(props.getProperty(LOGIN_TEXT_KEY)));
		parent.appendChild(child);
		
		return login;
	}

	private static Tag getSubmitContainer(Properties props) {
		Tag submit = Div.getInstance();
		submit.setAttribute(CLASS_ATT_NAME, AUTH_KWC_CLASS);
		Tag parent = Div.getInstance();
		parent.setAttribute(CLASS_ATT_NAME, AUTH_MWC_CLASS);
		submit.appendChild(parent);
		parent = Div.getInstance();
		parent.setAttribute(CLASS_ATT_NAME, AUTH_JWC_CLASS);
		submit.appendChild(parent);
		Tag child = Button.getInstance(props.getProperty(LOGIN_SUBMIT_VALUE_KEY));
		child.setAttribute(ID_ATT_NAME, props.getProperty(LOGIN_SUBMIT_ID_KEY));
		child.setAttribute(CLASS_ATT_NAME, AUTH_JK_CLASS);
		child.setAttribute(TYPE_ATT_NAME, SUBMIT_BUTTON_TYPE);
		child.setAttribute(FORM_ATT_NAME, props.getProperty(LOGIN_FORM_ID_KEY));
		parent.appendChild(child);
		return submit;
	}

	private static Tag getPasswordContainer(Properties props) {
		Tag pass = Div.getInstance();
		pass.setAttribute(CLASS_ATT_NAME, AUTH_KWC_CLASS);
		Tag parent = Div.getInstance();
		parent.setAttribute(CLASS_ATT_NAME, AUTH_MWC_CLASS);
		pass.appendChild(parent);
		Tag child = Div.getInstance();
		child.setAttribute(CLASS_ATT_NAME, AUTH_NL_CLASS);
		child.appendChild(TextTag.getInstanceNBSP(props.getProperty(LOGIN_PASSWORD_LABEL_KEY)));
		parent.appendChild(child);
		parent = Div.getInstance();
		parent.setAttribute(CLASS_ATT_NAME, AUTH_JWC_CLASS);
		pass.appendChild(parent);
		child = Div.getInstance();
		child.setAttribute(CLASS_ATT_NAME, AUTH_LBC_CLASS + " " + AUTH_GM_CLASS);
		parent.appendChild(child);
		Tag input = Input.getInstance(props.getProperty(LOGIN_PASSWORD_NAME_KEY), INPUT_PASSWORD_TYPE, null);
		input.setAttribute(ID_ATT_NAME, props.getProperty(LOGIN_PASSWORD_ID_KEY));
		input.setAttribute(STYLE_ATT_NAME, LOGIN_PASSWORD_STYLE);
		input.setAttribute(CLASS_ATT_NAME, AUTH_FIC_CLASS);
		child.appendChild(input);
		return pass;
	}

	private static Tag getUserNameContainer(Properties props) {
		Tag username = Div.getInstance();
		username.setAttribute(CLASS_ATT_NAME, AUTH_KWC_CLASS);
		Tag parent = Div.getInstance();
		parent.setAttribute(CLASS_ATT_NAME, AUTH_MWC_CLASS);
		username.appendChild(parent);
		Tag child = Div.getInstance();
		child.setAttribute(CLASS_ATT_NAME, AUTH_NL_CLASS);
		child.appendChild(TextTag.getInstanceNBSP(props.getProperty(LOGIN_USERNAME_LABEL_KEY)));
		parent.appendChild(child);
		parent = Div.getInstance();
		parent.setAttribute(CLASS_ATT_NAME, AUTH_JWC_CLASS);
		username.appendChild(parent);
		child = Input.getInstance(props.getProperty(LOGIN_USERNAME_NAME_KEY), INPUT_TEXT_TYPE, null);
		child.setAttribute(MAX_LENGTH_ATT_NAME, Integer.toString(LOGIN_USERNAME_INPUT_MAX_LENGTH_VALUE));
		child.setAttribute(CLASS_ATT_NAME, AUTH_FIC_CLASS + " " + AUTH_KM_CLASS);
		child.setAttribute(ID_ATT_NAME, props.getProperty(LOGIN_USERNAME_ID_KEY));
		parent.appendChild(child);
		return username;
	}

	private static Tag getErrorContainer(Properties props, int errorIndex) {
		Tag error = Div.getInstance();
		error.setAttribute(CLASS_ATT_NAME, FRAME7_CLASS);
		if(errorIndex == 0) {
			error.setAttribute(STYLE_ATT_NAME, ERROR_CONTAINER_NOT_VISIBLE_STYLE);
		}
		Tag parent = error;
		Tag child = Div.getInstance();
		child.setAttribute(CLASS_ATT_NAME, AUTH_CPC_CLASS);
		parent.appendChild(child);
		parent = child;
		child = Div.getInstance();
		child.setAttribute(CLASS_ATT_NAME, AUTH_APC_CLASS + " " + FRAME8_CLASS);
		child.appendChild(TextTag.getInstance(props.getProperty(ERROR_CONTAINER_CAPTION_KEY)));
		parent.appendChild(child);
		child = Div.getInstance();
		child.setAttribute(CLASS_ATT_NAME, AUTH_LOC_CLASS);
		child.appendChild(TextTag.getInstance(props.getProperty(ERROR_MESSAGE_KEY + DOT_CHAR + errorIndex)));
		parent.appendChild(child);
		return error;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	static final String getResourcePath (Class<?> _class) {
		//System.out.println(SLASH_CHAR + _class.getPackage().getName().replace(DOT_CHAR, SLASH_CHAR) + SLASH_CHAR);
		return SLASH_CHAR + _class.getPackage().getName().replace(DOT_CHAR, SLASH_CHAR) + SLASH_CHAR;
	}
	

}
