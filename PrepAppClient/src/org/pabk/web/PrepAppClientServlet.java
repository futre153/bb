package org.pabk.web;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.pabk.html.Br;
import org.pabk.html.Div;
import org.pabk.html.Doctype;
import org.pabk.html.Form;
import org.pabk.html.Hr;
import org.pabk.html.Html;
import org.pabk.html.HtmlTag;
import org.pabk.html.Input;
import org.pabk.html.Label;
import org.pabk.html.Link;
import org.pabk.html.Span;
import org.pabk.html.Table;
import org.pabk.html.Tag;
import org.pabk.html.Td;
import org.pabk.html.TextTag;
import org.pabk.html.Tr;
import org.w3c.dom.Document;

public class PrepAppClientServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Properties _default;
	private static ScrapEntries entries;
	private static ScrapEntries archiveEntries;
	private static DocumentBuilder builder;
	private static String liveStoreName;
	@SuppressWarnings("unused")
	private static String archiveStoreName;
	private static int liveRefreshRate = -1;
	private static int archiveRefreshRate = -1;
	private static Locale locale;
	
	private static final String DEFAULT_CHARSET = "UTF-8";
	private static final String USER_SETTINGS_ATTS = "user-settings";
	private static final String DEFAULT_PROPERTIES_URL = "data/default.properties.xml";
	private static final String APP_NAME = "Prehliadač SMS notifikácií pre Prepaid karty";
	private static final String STYLES_HREF = "index.css";
	private static final String MAIN_CONTAINER_ID = "main-container";
	private static final String HEADER_ID = "header";
	private static final String APP_NAME_CLASS = "app-name";
	private static final String MENU_ID = "menu";
	private static final String FORM_METHOD = "post";
	//private static final String FORM_ACTION = HtmlTag.EMPTY_ATT_VALUE;
	private static final String FORM_ACTION = null;
	private static final String DISPLAY_ACTION = "display";
	private static final String INPUT_HIDDEN = "hidden";
	private static final String CURRENT_VIEW_KEY = "currentView";
	private static final String INPUT_SUBMIT = "submit";
	private static final String MENU_ITEM_ARCHIVE = "zobraz archív";
	private static final String USER_MANAGEMENT_KEY = "userManagement";
	private static final String MENU_ITEM_USER_MANAGEMENT = "správa prístupov";
	private static final String USER_DETAILS_ID = "user-details";
	private static final String USER_DETAILS_TITLE = "DN aktuálne prihláseného používateľa";
	private static final String REMOTE_USER_KEY = "remoteUser";
	private static final String DATA_URL = "data/";
	private static final String USER_SETTINGS_COMMENT = "User settings for %s";
	private static final String USER_SETTINGS_ENCODING = DEFAULT_CHARSET;
	private static final String USER_DETAILS_KEY = "userDetails";
	private static final String INPUT_IMAGE = "image";
	private static final String ALT = "alt";
	private static final String USER_ICON_ALT = "user-icon";
	private static final String SRC = "src";
	private static final String USER_IMAGE_SRC = "images/user.png";
	private static final String TITLE = "title";
	private static final String USER_DETAILS_IMAGE_TITLE = "zobraz podrobnosti o používateľovi";
	
	
	private static final ArrayList<Worker> workers = new ArrayList<Worker>();
	private static final String REFRESH = "refresh";
	private static final String WAIT_REFRESH_RATE = "0.1";
	private static final String LANGUAGE_KEY = "language";
	private static final String DATETIME_FORMAT_KEY = "dateTimeFormat";
	private static final String COMMON_ACTION_NOT_DEFINED_ERROR_MESSAGE = "Operácia s názvom %s neexistuje pre spracovanie jednotlivých záznamov!";
	private static final String OPEN_COMMON_PROGRESS_NOT_DEFINED_ERROR_MESSAGE = "Obrazovka č. %d$2 neexistuje pre operáciu %s$1";
	private static final String PARAM_NAME_COMMON_OPEN_VIEW = "view";
	private static final String RPC_VIEW = "rpc";
	private static final String XML_VIEW = "xml";
	private static final String SMS_VIEW = "sms";
	private static final String[] COMMON_OPEN_VIEWS = {RPC_VIEW, XML_VIEW, SMS_VIEW};
	private static final int RPC_VIEW_INDEX = 0;
	private static final int XML_VIEW_INDEX = 1;
	private static final String ENTRY_INFO_ID = "entry-info";
	private static final String ENTRY_INFO_CAPTION = "Podrobné zobrazenie aktívneho záznamu %s";
	private static final String ENTRY_INFO_HEADER_ID = "entry-info-header";
	private static final String ENTRY_INFO_LEFT_BANNER_ID = "entry-info-left-banner";
	private static final Object NO_INTERNAL_ID_INFO = "nepridelené - buď z dôvodu chyby pri spacovaní požiadavky, alebo je záznam staršieho typu, kde interné ID nebolo prideľované";
	private static final String INTERNAL_ID_CLASS = "internal-id";
	private static final String INTERNAL_ID_CAPTION = "Interné ID";
	private static final String EXECUTION_TIME_CLASS = "execution-time";
	private static final String EXECUTION_TIME_CAPTION = "Dátum a čas spracovania požiadavky";
	private static final String RESULT_CAPTION = "Výsledok spracovania požiadavky";
	private static final String UNKNOWN_RESULT_INFO = "výsledok spracovania požiadavky nie je známy";
	private static final String OK_RESULT_INFO = "požiadavka na notifikáciu bola spracovaná a odoslaná v poriadku";
	private static final String FAULT_RESULT_INFO = "spracovanie požiadavky skončilo s chybou";
	private static final String DISPLAY_ERROR_SUBMIT_VALUE = "Zobraziť chybu";
	private static final String DISABLED = "disabled";
	private static final String EMPTY = "";
	private static final String RECEPTION_TINE_CLASS = "reception-time";
	private static final String RECEPTION_TIME_CAPTION = "Dátum a čas prijatia notifikácie klientom";
	private static final String UNKNOWN_RECEPTION_TIME_INFO = "nezistený - dá sa zistiť s použitím funkcie nižšie";
	private static final String UNDETECTABLE_RECEPTION_TIME_INFO = "nezistiteľný - nie je možné zistiť z dôvodu chýbajúceho interného ID";
	private static final String GET_RECEPTION_TIME_SUBMIT_VALUE = "Zistiť čas doručenia";
	private static final String BUTTONS_INFO_CLASS = "buttons";
	private static final String BUTTONS_INFO_CAPTION = "Operácie so záznamom";
	private static final String ARCHIVE_BUTTON_VALUE = "Archivovať";
	private static final String DELETE_BUTTON_VALUE = "Zmazať";
	private static final String HIDDEN = "hidden";
	private static final String HIDDEN_CLASS = HIDDEN;
	private static final String INPUT_RPC_VALUE = "rpc";
	private static final String RPC_DISPLAY_ID = "rpc-display";
	private static final String XML_DISPLAY_ID = "xml-display";
	private static final String INPUT_XML_VALUE = "xml";
	private static final String INPUT_SMS_VALUE = "sms";
	private static final String SMS_DISPLAY_ID = "sms-display";
	private static final String ENTRY_INFO_CONTENT_ID = "entry-info-content";
	private static final String INDENT_AMOUNT = "2";
	private final ArrayList<Worker> localWorkers = new ArrayList<Worker>();
	private static Transformer trans;
	private static SimpleDateFormat dateformat;
	
	
	public void init() {
		System.out.println("INIT started");
		if(_default == null) {
			InputStream in = this.getServletContext().getResourceAsStream(DEFAULT_PROPERTIES_URL);
			_default = new Properties();
			try {
				_default.loadFromXML(in);
				liveStoreName = _default.getProperty(LIVE_VIEW_NAME_KEY);
				archiveStoreName = _default.getProperty(ARCHIVE_VIEW_NAME_KEY);
				liveRefreshRate = Integer.parseInt(_default.getProperty(LIVE_REFRESH_RATE_KEY));
				archiveRefreshRate = Integer.parseInt(_default.getProperty(ARCHIVE_REFRESH_RATE_KEY));
				setLocale(findLocale(_default.getProperty(LANGUAGE_KEY)));
				setDateformat(new SimpleDateFormat (_default.getProperty(DATETIME_FORMAT_KEY), getLocale()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(builder == null) {
			try {
				builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		}
		if(trans == null) {
			try {
				trans = TransformerFactory.newInstance().newTransformer();
				trans.setOutputProperty(OutputKeys.INDENT, "yes");
				trans.setOutputProperty(OutputKeys.METHOD, "xml");
				trans.setOutputProperty(OutputKeys.ENCODING, DEFAULT_CHARSET);
				trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", INDENT_AMOUNT);
			} catch (TransformerConfigurationException | TransformerFactoryConfigurationError e) {
				e.printStackTrace();
			}
		}
		
		/*try {
			Worker.loadRecords(new Worker(), _default, new ScrapEntries(_default), "archive");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		if(entries == null) {
			Worker.loadActiveRecords(_default, this.getWorkers());
		}
		if(archiveEntries == null) {
			Worker.loadArchiveRecords(_default, this.getWorkers());
		}
		System.out.println(_default);
	}
	
	private Locale findLocale(String locale) {
		Locale[] locales = Locale.getAvailableLocales();
		for(int i = 0; i < locales.length; i ++) {
			if(locales[i].getDisplayName().equals(locale)) {
				return locales[i];
			}
		}
		return Locale.getDefault();
	}
	
	protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if(!req.getParameterNames().hasMoreElements()) {
			this.doPost(req, resp);
		}
		else {
			super.doGet(req, resp);
		}
	}
	
	protected void doPost (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		displayParams(req);
		displayAtts(req);
		resp.setCharacterEncoding(DEFAULT_CHARSET);
		PrintWriter out = resp.getWriter();
		HttpSession ses = req.getSession();
		Object settings = ses.getAttribute(USER_SETTINGS_ATTS);
		Properties user = (Properties) (settings == null ? getUserSettings(req) : settings);
		
/*

		RequestFacade rf = (RequestFacade) req;
		String user = rf.getRemoteUser();
		byte[] b = user.getBytes();
		out.println(rf.getCharacterEncoding());
		out.println(new String(b, "ISO-8859-1"));
		
		out.println(ses.getClass().getName());
		out.println(rf.getUserPrincipal());*/
		
		Html page = Html.getInstance(APP_NAME);
		page.setDoctype(Doctype.HTML_5);
		//page.addMetadata("Content-type", null, null, "text/html; charset=" + DEFAULT_CHARSET, null);
		page.addMetadata(null, null, null, null, DEFAULT_CHARSET);
		page.addLink(Link.STYLEHEET, new String[]{"stylesheet", "text/css", STYLES_HREF});
		page.setAttribute("lang", "sk-SK");
		checkWorkers(this.localWorkers);
		int dis = prepareAction(user, req, this.getWorkers());
		if(workers.size() > 0 || localWorkers.size() > 0) {
			page.addMetadata(REFRESH, null, null, WAIT_REFRESH_RATE, null);
			page.getBody().appendChild(PrepAppClientServlet.getWaitContainer(user, localWorkers));
		}
		else {
			Div tag = Div.getInstance();
			tag.setId(MAIN_CONTAINER_ID);
			page.getBody().appendChild(tag);
			resetAttributes(req, PARAM_NAME_SELECT_ALL, PARAM_NAME_DISPLAY);
			System.out.println("live=" + entries.size());
			System.out.println("archive=" + archiveEntries.size());
			System.out.println("local=" + ((ScrapEntries)req.getSession().getAttribute(LOCAL_ENTRIES_KEY)).size());
			switch(dis) {
			case DEFAULT_DISPLAY_ACTION:
				tag.appendChild(PrepAppClientServlet.getMainContainer(user, (ScrapEntries)req.getSession().getAttribute(LOCAL_ENTRIES_KEY)));
				break;
			case COMMON_ACTION:
				tag.appendChild(PrepAppClientServlet.getCommonActionContainer(user, req));
			default:
				/* TODO error */
			}
			
		}
		page.doFinal(out);
		out.flush();
		displayParams(req);
		displayAtts(req);
	}
	
	private static Tag getCommonActionContainer(Properties user, HttpServletRequest req) {
		String action = user.getProperty(PARAM_NAME_ACTION);
		if (action != null) {
			if(action.equals(OPEN_ACTION)) {
				return getOpenCommonActionContainer(user, req);
				
			}
			/*else if (action.equals(DELETE_ACTION)) {
				return getArchiveCommonActionContainer(user);
			}
			else if (action.equals(DELIVERY_ACTION)) {
				return getOpenErrorCommonActionContainer(user);
			}
			else if (action.equals(ARCHIVE_ACTION)) {
				return getDeleteCommonActionContainer(user);
			}
			else if (action.equals(OPEN_ERROR_ACTION)) {
				return getDeliveryCommonActionContainer(user);
			}*/
		}
		return getErrorContainer(COMMON_ACTION_NOT_DEFINED_ERROR_MESSAGE, action);
	}

	private static Tag getOpenCommonActionContainer(Properties user, HttpServletRequest req) {
		try {
			String progress = getParameter(req, PARAM_NAME_PROGRESS);
			switch (progress != null ? Integer.parseInt(progress) : PROGRESS_NOT_STARTED) {
			case PROGRESS_NOT_STARTED:
				return getOpenCommonNotStartedContainer(user, req);
			case OPEN_COMMON_ACTION:
				//return OpenCommonActionContainer(user);
			default:
				return getErrorContainer(OPEN_COMMON_PROGRESS_NOT_DEFINED_ERROR_MESSAGE, OPEN_ACTION, progress);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return getErrorContainer(e);
		}
	}
	
	private static Tag getOpenCommonNotStartedContainer(Properties user, HttpServletRequest req) {
		Hashtable<String, Object> entry = getEntry((ScrapEntries) req.getSession().getAttribute(LOCAL_ENTRIES_KEY), NAME_KEY, user.getProperty(PARAM_NAME_ENTRY_NAME));
		int view = indexOf (getParameter (req, PARAM_NAME_COMMON_OPEN_VIEW), COMMON_OPEN_VIEWS, RPC_VIEW_INDEX);
		Div tag = Div.getInstance();
		tag.setId(ENTRY_INFO_ID);
		tag.appendChild(getEntryInfoHeader(getStringValue(entry, NAME_KEY, null)));
		tag.appendChild(getEntryInfoLeftBanner(entry));
		tag.appendChild(getEntryInfoContent(view, entry));
		return tag;
	}
	
	
	
	private static Tag getEntryInfoContent(int view, Hashtable<String, Object> entry) {
		Div content = Div.getInstance();
		content.setId(ENTRY_INFO_CONTENT_ID);
		content.appendChild(getEntryInfoContentCaption(view));
		content.appendChild(getEntryInfoContentBody(view, entry));
		return content;
	}
	
	
	

	private static final String ENTRY_INFO_CONTENT_CAPTION_CLASS = "entry-info-content-caption";
	private static final String ENTRY_INFO_CONTENT_BODY_CLASS = "entry-info-content-body";
	private static final String ENTRY_INFO_CAPTION_TEXT = "Zobrazené ako %s";
	private static final String RPC_CAPTION_TEXT = "RPC žiadosť";
	private static final String XML_CAPTION_TEXT = "XML žiadosť";
	private static final String SMS_CAPTION_TEXT = "výsledná SMS správa";
	private static final String ENTRY_INFO_CONTENT_BOOKMARK_CLASS = "entry-info-content-bookmark";
	private static final String ENTRY_INFO_CONTENT_FRAME_CLASS = "entry-info-content-frame";
	private static final String[] ENTRY_INFO_CAPTION_TEXT_SUFFIX = {RPC_CAPTION_TEXT, XML_CAPTION_TEXT, SMS_CAPTION_TEXT};
	private static final String RPC_ACTIVE_CLASS = "rpc-active";
	private static final String XML_ACTIVE_CLASS = "xml-active";
	private static final String SMS_ACTIVE_CLASS = "sms-active";
	private static final String[] ACTIVE_CLASS = {RPC_ACTIVE_CLASS, XML_ACTIVE_CLASS, SMS_ACTIVE_CLASS};
	private static final String FIRST_COL_CLASS = "first-col";
	private static final String RPC_COL_CLASS = "rpc-col";
	private static final String XML_COL_CLASS = "xml-col";
	private static final String SMS_COL_CLASS = "sms-col";
	private static final String EMPTY_COL_CLASS = "empty-col";
	private static final String LAST_COL_CLASS = "last-col";
	private static final String[] ENTRY_INFO_CONTENT_TABLE_COLS = {FIRST_COL_CLASS, RPC_COL_CLASS, XML_COL_CLASS, SMS_COL_CLASS, EMPTY_COL_CLASS, LAST_COL_CLASS};
	private static final String COMMON_RPC_CLASS = "common-rpc";
	private static final String COMMON_XML_CLASS = "common-xml";
	private static final String COMMON_SMS_CLASS = "common-sms";
	private static final String FIRST_CLASS = "first";
	private static final String COMMON_CLASS = "common";
	private static final String LAST_CLASS = "last";
	private static final String FIRST_TOP_CLASS = "first-top";
	private static final String COMMON_RPC_TOP_CLASS = "common-rpc-top";
	private static final String COMMON_XML_TOP_CLASS = "common-xml-top";
	private static final String COMMON_SMS_TOP_CLASS = "common-sms-top";
	private static final String COMMON_TOP_CLASS = "common-top";
	private static final String LAST_TOP_CLASS = "last-top";
	private static final String FIRST_CENTER = "first-center";
	private static final String ENTRY_REQUEST_CONTENT_CLASS = "entry-request-content";
	private static final String LAST_CENTER = "last-center";
	private static final String FIRST_BOTTOM = "first-bottom";
	private static final String COMMON_BOTTOM = "common-bottom";
	private static final String LAST_BOTTOM = "last-bottom";
	private static final String[][] ENTRY_INFO_CONTENT_TABLE_CELL_CLASSES = {
		{FIRST_CLASS, COMMON_RPC_CLASS, COMMON_XML_CLASS, COMMON_SMS_CLASS, COMMON_CLASS, LAST_CLASS},
		{FIRST_TOP_CLASS, COMMON_RPC_TOP_CLASS, COMMON_XML_TOP_CLASS, COMMON_SMS_TOP_CLASS, COMMON_TOP_CLASS, LAST_TOP_CLASS},
		{FIRST_CENTER, ENTRY_REQUEST_CONTENT_CLASS, LAST_CENTER, null, null, null},
		{FIRST_BOTTOM, COMMON_BOTTOM, LAST_BOTTOM, null, null, null}
	};
	private static final String EMPTY_ROW_CLASS = "empty-row";
	private static final String RPC_LABEL_TEXT = "RPC";
	private static final String XML_LABEL_TEXT = "XML";
	private static final String SMS_LABEL_TEXT = "SMS";
	private static final String MARK_START_MASK = "\\<";
	private static final String MORE_THAN_MASK = "XXXX";
	private static final String MARK_START_HTML = "&lt;<span" + MORE_THAN_MASK;
	private static final String MARK_END_MASK = "\\>";
	private static final String MARK_END_HTML = "</span" + MORE_THAN_MASK + "&gt;";
	private static final String ENTRY_INFO_XMLSPAN_ID = "entry-info-xml-id";
	
	
	private static Tag getEntryInfoContentBody(int view, Hashtable<String, Object> entry) {
		Div body = Div.getInstance();
		body.setClassName(ENTRY_INFO_CONTENT_BODY_CLASS);
		Div bookmark = Div.getInstance();
		bookmark.setClassName(ENTRY_INFO_CONTENT_BOOKMARK_CLASS);
		Div frame = Div.getInstance();
		frame.setClassName(ENTRY_INFO_CONTENT_FRAME_CLASS);
		Table table = Table.getInstance(null, 6, 4, null);
		table.setClassName(ACTIVE_CLASS [view]);
		table.setColgroup(ENTRY_INFO_CONTENT_TABLE_COLS);
		Tr[] rows = table.getRows();
		for(int i = 0; i < rows.length; i ++) {
			Td[] cells = rows[i].getCells();
			for(int j = 0; j < cells.length; j ++) {
				if(ENTRY_INFO_CONTENT_TABLE_CELL_CLASSES[i][j] != null) {
					cells[j].setClassName(ENTRY_INFO_CONTENT_TABLE_CELL_CLASSES[i][j]);
				}
				else {
					rows[i].removeChild(cells[j]);
				}
			}
			
		}
		rows[1].setClassName(EMPTY_ROW_CLASS);
		rows[2].getCells()[1].setAttribute(COLSPAN, "4");
		rows[3].setClassName(EMPTY_ROW_CLASS);
		rows[3].getCells()[1].setAttribute(COLSPAN, "4");
		rows[0].getCells()[1].setInline(true);
		rows[0].getCells()[2].setInline(true);
		rows[0].getCells()[3].setInline(true);
		Label label = Label.getInstance(RPC_DISPLAY_ID);
		label.appendChild(TextTag.getInstance(RPC_LABEL_TEXT));
		label.setInline(true);
		table.setContent(label, 1, 0);
		label = Label.getInstance(XML_DISPLAY_ID);
		label.appendChild(TextTag.getInstance(XML_LABEL_TEXT));
		label.setInline(true);
		table.setContent(label, 2, 0);
		label = Label.getInstance(SMS_DISPLAY_ID);
		label.appendChild(TextTag.getInstance(SMS_LABEL_TEXT));
		label.setInline(true);
		table.setContent(label, 3, 0);
		Div div = Div.getInstance();
		div.appendChild(view == RPC_VIEW_INDEX ? getEntryInfoRpcView(entry) : (view == XML_VIEW_INDEX ? getEntryInfoXmlView(entry) : getEntryInfoSmsView(entry)));
		table.setContent(div, 1, 2);
		frame.appendChild(table);
		bookmark.appendChild(frame);
		body.appendChild(bookmark);
		return body;
	}

	
	private static Tag getEntryInfoSmsView(Hashtable<String, Object> entry) {
		// TODO Auto-generated method stub
		return null;
	}

	private static Tag getEntryInfoXmlView(Hashtable<String, Object> entry) {
		Object doc = entry.get(REQUEST_KEY);
		if(doc != null && doc instanceof Document) {
			Document d = (Document) doc;
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(out);
			    Source src = new DOMSource(d);
			    Result dest = new StreamResult(ps);
			    trans.transform(src, dest);
			    ps.flush();
			    BufferedReader buf = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(out.toByteArray())));
			    Span span = Span.getInstance();
			    span.setInline(true);
			    span.setId(ENTRY_INFO_XMLSPAN_ID);
			    //span.setAttribute(STYLE, ENTRY_INFO_XMLSPAN_STYLE);
			    String line = buf.readLine();
				if(line != null) {
					span.appendChild(TextTag.getInstanceNBSP(line.replaceAll(MARK_START_MASK, MARK_START_HTML).replaceAll(MARK_END_MASK, MARK_END_HTML).replaceAll(MORE_THAN_MASK, ">")));
					while((line = buf.readLine()) != null) {
						span.appendChild(Br.BASE_BR);
						span.appendChild(TextTag.getInstanceNBSP(line.replaceAll(MARK_START_MASK, MARK_START_HTML).replaceAll(MARK_END_MASK, MARK_END_HTML).replaceAll(MORE_THAN_MASK, ">")));
					}
				}
				return span;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return TextTag.getInstance(entry.toString()); 
	}
	
	private static final String MSG_TYPE_KEY = "MsgType";
	private static final int FDS_DOBI = 6;
	private static final String INSTITUTION_ID_KEY = "InstitutionId";
	private static final String NOTIFICATION_TYPE_KEY = "NotificationType";
	static final String CONTACT_KEY = "Contact";
	private static final String PROCESS_DATE_KEY = "ProcessDate";
	private static final String CARD_TYPE_KEY = "CardType";
	static final String PAYMENT_AMT_KEY = "PaymentAmt";
	static final String PAYMENT_CCY_KEY = "PaymentCCY";
	private static final String PAYMENT_INDICATOR_KEY = "PaymentIndicator";
	private static final String AVAILABLE_BALANCE_KEY = "AvailableBalance";
	private static final String ACCOUNT_ID_KEY = "AccountId";
	private static final String TRACE_ID_KEY = "TraceID";
	private static final String TIMESTAMP_KEY = "Timestamp";
	
	private static final String[] FDS_DOBI_KEYS = {MSG_TYPE_KEY, INSTITUTION_ID_KEY, NOTIFICATION_TYPE_KEY, CONTACT_KEY, PROCESS_DATE_KEY, CARD_TYPE_KEY, PAYMENT_AMT_KEY, PAYMENT_CCY_KEY, PAYMENT_INDICATOR_KEY, AVAILABLE_BALANCE_KEY, ACCOUNT_ID_KEY, TRACE_ID_KEY, TIMESTAMP_KEY};
	private static final String CARD_NUMBER_KEY = "CardNumber";
	private static final String TRANSACTION_SOURCE_KEY = "TransactionSource";
	private static final String TRANSACTION_TYPE_KEY = "TransactionType";
	static final String TRANSACTION_CURRENCY_KEY = "TransactionCurrency";
	static final String TRANSACTION_AMOUNT_KEY = "TransactionAmount";
	private static final String ACCOUNT_CURRENCY_KEY = "AccountCurrency";
	private static final String TRANSACTION_DATETIME_KEY = "TransactionDateTime";
	private static final String MERCHANT_NAME_KEY = "MerchantName";
	private static final String TERMINAL_OWNER_NAME_KEY = "TerminalOwnerName";
	private static final String MERCHANT_CITY_KEY = "MerchantCity";
	private static final String MERCHANT_STATE_KEY = "MerchantState";
	private static final String TXN_ID_KEY = "TxnId";
	private static final String NOT_APPROVED_KEY = "NotApproved";
	private static final String LANGUAGE_FDS_KEY = "Language";
	private static final String TERMINAL_ID_KEY = "TerminalId";
	
	private static final String[] FDS_DEFAULT = {MSG_TYPE_KEY, INSTITUTION_ID_KEY, CARD_NUMBER_KEY, NOTIFICATION_TYPE_KEY, CONTACT_KEY, TRANSACTION_SOURCE_KEY, TRANSACTION_TYPE_KEY, TRANSACTION_AMOUNT_KEY, TRANSACTION_CURRENCY_KEY, AVAILABLE_BALANCE_KEY, ACCOUNT_CURRENCY_KEY, TRANSACTION_DATETIME_KEY, MERCHANT_NAME_KEY, TERMINAL_OWNER_NAME_KEY, MERCHANT_CITY_KEY, MERCHANT_STATE_KEY, TXN_ID_KEY, NOT_APPROVED_KEY, LANGUAGE_FDS_KEY, TERMINAL_ID_KEY};
	private static final String ENTRY_INFO_KEYSPAN_STYLE = "font-weight: bolder;";
	private static final String ENTRY_INFO_SPAN_STYLE = "font-family: \"courier new\"; font-size: 90%;";
	
	private static final String ENTRY_INFO_EQUAL_TEXT = " = ";
	private static final String ENTRY_INFO_QOUTESPAN_STYLE = "color: red;";
	private static final char DOUBLE_QOUTE_CHAR = '"';
	private static final String ENTRY_INFO_VALUESPAN_STYLE = "color: green;";
	
	private static Tag getEntryInfoRpcView(Hashtable<String, Object> entry) {
		String[] keys = null;
		switch (entry.get(MSG_TYPE_KEY) == null ? -1 : Integer.parseInt((String) entry.get(MSG_TYPE_KEY))) {
		case FDS_DOBI:
			keys = FDS_DOBI_KEYS;
			break;
		default:
			keys = FDS_DEFAULT;
		}
		Span span = Span.getInstance();
		span.setAttribute(STYLE, ENTRY_INFO_SPAN_STYLE);
		span.setInline(true);
		boolean first = true;
		int l = 0;
		for(int i = 0; i < keys.length; i ++) {
			l = entry.get(keys[i]) == null ? l : (l < keys[i].length() ? keys[i].length(): l);
		}
		for(int i = 0; i < keys.length; i ++) {
			Object value = entry.get(keys[i]);
			if(value != null) {
				if(!first) {
					span.appendChild(Br.BASE_BR);
				}
				Span keyspan = Span.getInstance();
				keyspan.setInline(true);
				keyspan.setAttribute(STYLE, ENTRY_INFO_KEYSPAN_STYLE);
				span.appendChild(TextTag.getInstanceNBSP(keys[i]));
				for(int j = 0; j < (l - keys[i].length()); j ++) {
					span.appendChild(TextTag.NBSP);
				}
				Span qoute = Span.getInstance();
				qoute.setInline(true);
				qoute.setAttribute(STYLE, ENTRY_INFO_QOUTESPAN_STYLE);
				qoute.appendChild(TextTag.getInstance(new String(new char[]{DOUBLE_QOUTE_CHAR})));
				Span valspan = Span.getInstance();
				valspan.setInline(true);
				valspan.setAttribute(STYLE, ENTRY_INFO_VALUESPAN_STYLE);
				valspan.appendChild(TextTag.getInstanceNBSP(value.toString()));
				span.appendChild(keyspan);
				span.appendChild(TextTag.getInstanceNBSP(ENTRY_INFO_EQUAL_TEXT));
				span.appendChild(qoute);
				span.appendChild(valspan);
				span.appendChild(qoute);
				first = false;
			}
		}
		return span;
	}

	private static Tag getEntryInfoContentCaption(int view) {
		Div caption = Div.getInstance();
		caption.setClassName(ENTRY_INFO_CONTENT_CAPTION_CLASS);
		Table table = Table.getInstance(null, 1, 1, null);
		table.setContent(TextTag.getInstance(String.format(ENTRY_INFO_CAPTION_TEXT, ENTRY_INFO_CAPTION_TEXT_SUFFIX[view])), 0, 0);
		caption.appendChild(table);
		return caption;
	}

	private static Tag getEntryInfoLeftBanner(Hashtable<String, Object> entry) {
		Div div = Div.getInstance();
		div.setId(ENTRY_INFO_LEFT_BANNER_ID);
		div.appendChild(getInternalIdInfo(getStringValue(entry, INTERNAL_ID_KEY, NO_INTERNAL_ID_INFO)));
		div.appendChild(getExecutionTimeInfo((Date) getValue(entry, EXECUTIONTIME_KEY, getDateFromName(getStringValue(entry, NAME_KEY, null)))));
		div.appendChild(getResultInfo(getStringValue(entry, FAULT_KEY, null)));
		div.appendChild(getReceptionTimeInfo(getStringValue(entry, INTERNAL_ID_KEY, null) != null, (Date) getValue(entry, RECEPTIONTIME_KEY, null)));
		div.appendChild(getButtonsInfo());
		div.appendChild(getHiddenInputsInfo());
		return div;
	}

	private static Tag getHiddenInputsInfo() {
		Div div = Div.getInstance();
		div.setClassName(HIDDEN_CLASS); 
		div.appendChild(getForm(FORM_METHOD, FORM_ACTION, getInput(false, PARAM_NAME_DISPLAY, INPUT_HIDDEN, Integer.toString(COMMON_ACTION_CONTINUE)), getInput(false, PARAM_NAME_COMMON_OPEN_VIEW, INPUT_SUBMIT, INPUT_RPC_VALUE, ID, RPC_DISPLAY_ID)));
		div.appendChild(getForm(FORM_METHOD, FORM_ACTION, getInput(false, PARAM_NAME_DISPLAY, INPUT_HIDDEN, Integer.toString(COMMON_ACTION_CONTINUE)), getInput(false, PARAM_NAME_COMMON_OPEN_VIEW, INPUT_SUBMIT, INPUT_XML_VALUE, ID, XML_DISPLAY_ID)));
		div.appendChild(getForm(FORM_METHOD, FORM_ACTION, getInput(false, PARAM_NAME_DISPLAY, INPUT_HIDDEN, Integer.toString(COMMON_ACTION_CONTINUE)), getInput(false, PARAM_NAME_COMMON_OPEN_VIEW, INPUT_SUBMIT, INPUT_SMS_VALUE, ID, SMS_DISPLAY_ID)));
		return div;
	}

	private static Tag getButtonsInfo() {
		Div div = Div.getInstance();
		div.setClassName(BUTTONS_INFO_CLASS);
		Table table = Table.getInstance(BUTTONS_INFO_CAPTION, 1, 1, null);
		table.setContent(getForm(null, null, getInput(false, null, INPUT_SUBMIT, ARCHIVE_BUTTON_VALUE)), 0, 0);
		table.appendContent(getForm(null, null, getInput(false, null, INPUT_SUBMIT, DELETE_BUTTON_VALUE)), 0, 0);
		div.appendChild(table);
		return div;
	}

	private static Tag getReceptionTimeInfo(boolean id, Date value) {
		Div div = Div.getInstance();
		div.setClassName(RECEPTION_TINE_CLASS);
		Table table = Table.getInstance(RECEPTION_TIME_CAPTION, 1, 2, null);
		table.setContent(value != null ? TextTag.getInstanceNBSP(getDateformat().format(value)) : (id ? TextTag.getInstance(UNKNOWN_RECEPTION_TIME_INFO) : TextTag.getInstance(UNDETECTABLE_RECEPTION_TIME_INFO)), 0, 0);
		/*TODO upravit*/
		table.setContent((value == null && id) ? getForm(null, null, getInput(false, null, INPUT_SUBMIT, GET_RECEPTION_TIME_SUBMIT_VALUE)) : getForm(null, null, getInput(false, null, INPUT_SUBMIT, GET_RECEPTION_TIME_SUBMIT_VALUE, DISABLED, EMPTY)), 0, 1);
		div.appendChild(table);
		return div;
	}

	private static Tag getResultInfo(String result) {
		Div div = Div.getInstance();
		div.setClassName(RESULT_CLASS);
		Table table = Table.getInstance(RESULT_CAPTION, 1, 2, null);
		table.setContent(result == null ? TextTag.getInstance(UNKNOWN_RESULT_INFO) : (result.equals(OK_RESULT) ? TextTag.getInstance(OK_RESULT_INFO) : TextTag.getInstance(FAULT_RESULT_INFO)), 0, 0);
		/*TODO upravit*/
		table.setContent((result != null && !result.equals(OK_RESULT)) ? getForm(null, null, getInput(false, null, INPUT_SUBMIT, DISPLAY_ERROR_SUBMIT_VALUE)) : getForm(null, null, getInput(false, null, INPUT_SUBMIT, DISPLAY_ERROR_SUBMIT_VALUE, DISABLED, EMPTY)), 0, 1);
		div.appendChild(table);
		return div;
	}

	private static Tag getExecutionTimeInfo(Date value) {
		Div div = Div.getInstance();
		div.setClassName(EXECUTION_TIME_CLASS);
		Table table = Table.getInstance(EXECUTION_TIME_CAPTION, 1, 1, null);
		table.setContent(value == null ? TextTag.NBSP : TextTag.getInstanceNBSP(getDateformat().format(value)), 0, 0);
		div.appendChild(table);
		return div;
	}

	private static Object getDateFromName(String name) {
		Date date = null;
		try {
			date = new Date(Long.parseLong(name.substring(name.lastIndexOf(UNDERSCORE_CHAR) + 1, name.lastIndexOf(DOT_CHAR))));
		}
		catch (Exception e) {}
		return date;
	}

	private static Tag getInternalIdInfo(String id) {
		Div div = Div.getInstance();
		div.setClassName(INTERNAL_ID_CLASS);
		Table table = Table.getInstance(INTERNAL_ID_CAPTION, 1, 1, null);
		table.setContent(TextTag.getInstance(id), 0, 0);
		div.appendChild(table);
		return div;
	}

	private static Tag getEntryInfoHeader(String name) {
		Div div = Div.getInstance();
		div.setId(ENTRY_INFO_HEADER_ID);
		Table table = Table.getInstance(null, 1, 1, null);
		table.setContent(TextTag.getInstance(String.format(ENTRY_INFO_CAPTION, name)), 0, 0);
		div.appendChild(table);
		return div;
	}

	private static int indexOf (String key, String[] array, int defaultIndex) {
		int index = defaultIndex;
		if(!(key == null || array == null)) {
			for(int i = 0; i < array.length; i ++) {
				if(key.equals(array[i])) {
					index = i;
					break;
				}
			}
		}
		return index;
	}
	
	public static String getStringValue (Hashtable<String, Object> entry, String key, Object _default) {
		String value = (String) getValue(entry, key, _default);
		return (String) ((value != null && value.length() == 0) ? _default : value);
	}
	
	public static Object getValue (Hashtable<String, Object> entry, String key, Object _default) {
		Object value = _default;
		if(entry != null && key != null) {
			_default = entry.get(key);
			value = _default != null ? _default : value;
		}
		return value;
	}
	
	
	public static Hashtable<String, Object> getEntry(ScrapEntries entries, String key, String value) {
		Hashtable<String, Object> entry = null;
		for(int i = 0; i < entries.size(); i ++) {
			entry = entries.get(i);
			if(entry.get(key).equals(value)) {
				break;
			}
		}
		return entry;
	}
	
	private static final String ERROR_MESSAGE_ID = "error-message";
	private static final String ERROR_CAPTION_CLASS = "error-caption";
	private static final String ERROR_MESSAGE_CAPTION_TEXT = "Chybové hlásenie";
	private static final String ERROR_CONTENT_CLASS = "error-content";
	private static final String INPUT_VALUE_CLOSE = "Zavrieť";
	private static final String ERROR_BUTTON_CLASS = "error-button";
	private static final String UNKNOWN_ERROR_TEXT = "Neznáma chyba";
	private static final String STYLE = "style";
	private static final String CSS_TEXT_ALIGN_LEFT = "text-align: left;";
	
	private static Tag getErrorContainer(Object e, Object ...objects) {
		Div errorMsg = Div.getInstance();
		errorMsg.setId (ERROR_MESSAGE_ID);
		
		Div errCaption = Div.getInstance();
		errCaption.setClassName(ERROR_CAPTION_CLASS);
		Table table = Table.getInstance(null, 1, 1, null);
		table.setContent(TextTag.getInstance(ERROR_MESSAGE_CAPTION_TEXT), 0, 0);
		errCaption.appendChild(table);
		errorMsg.appendChild(errCaption);
		
		Div errContent = Div.getInstance();
		errContent.setClassName(ERROR_CONTENT_CLASS);
		table = Table.getInstance(null, 1, 1, null);
		table.setContent((e instanceof Exception) ? getErrorExceptionContainer((Exception) e) : (e == null ? TextTag.getInstance(UNKNOWN_ERROR_TEXT) : TextTag.getInstance(String.format((String) e, objects))), 0, 0);
		errContent.appendChild(table);
		errorMsg.appendChild(errContent);
		
		Div errButton = Div.getInstance();
		errButton.setClassName(ERROR_BUTTON_CLASS);
		table = Table.getInstance(null, 1, 1, null);
		table.setContent(getForm(FORM_METHOD, FORM_ACTION, getInput (false, PARAM_NAME_DISPLAY, INPUT_HIDDEN, Integer.toString(DEFAULT_DISPLAY_ACTION)), getInput(false, null, INPUT_SUBMIT, INPUT_VALUE_CLOSE)), 0, 0);
		errButton.appendChild(table);
		errorMsg.appendChild(errButton);
		
		return errorMsg;
	}

	private static Tag getErrorExceptionContainer(Exception e) {
		e.printStackTrace();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
		e.printStackTrace(writer);
		writer.flush();
		BufferedReader buf = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(out.toByteArray())));
		Div div = Div.getInstance();
		try {
			String line = buf.readLine();
			if(line != null) {
				div.appendChild(TextTag.getInstanceNBSP(line));
				while((line = buf.readLine()) != null) {
					div.appendChild(Br.BASE_BR);
					div.appendChild(TextTag.getInstanceNBSP(line));
				}
			}
			else {
				div.appendChild(TextTag.getInstance(UNKNOWN_ERROR_TEXT));
			}
		} catch (IOException e1) {}
		div.setAttribute(STYLE, CSS_TEXT_ALIGN_LEFT);
		return div;
	}

	private void displayAtts(HttpServletRequest req) {
		System.out.println("ATTRIBUTES");
		Enumeration<String> map = req.getSession().getAttributeNames();
		while (map.hasMoreElements()) {
			String name = map.nextElement();
			Object obj = req.getSession().getAttribute(name);
			if(obj instanceof String) {
				System.out.println(name + "='" + obj.toString() + "';");
			}
			else {
				System.out.println(name + "='" + obj.getClass().getSimpleName() + "';");
			}
		}
	}

	private void displayParams(HttpServletRequest req) {
		System.out.println("PARAMETERS");
		Enumeration<String> map = req.getParameterNames();
		while (map.hasMoreElements()) {
			String name = map.nextElement();
			System.out.println(name + "='" + req.getParameter(name) + "';");
		}
	}

	private static final String PARAM_NAME_DISPLAY = "display";
	private static final int DEFAULT_DISPLAY_ACTION = 1;
	private static final int COMMON_ACTION = 2;
	private static final String LIVE_REFRESH_RATE_KEY = "liveRefreshRate";
	private static final String ARCHIVE_REFRESH_RATE_KEY = "archiveRefreshRate";
	private static final String LOCAL_ENTRIES_KEY = "local-entries";
	private static final String SELECT_ALL_ID = "select-all";
	private static final String PARAM_NAME_SELECT_ALL = SELECT_ALL_ID;
	private static final String DESELECT_ALL_ID = "deselect-all";
	private static final String COMMON_TASK_PARAM_NAME_MASK = "\\@((open)|(delete])|(archive)|(delivery)|(open\\-error))\\_scrap\\_\\d{4}\\_\\d+\\.txt\\.[xy]";
	private static final String COMMON_TAST_PARAM_VALUE_MASK = "\\d+";
	private static final char AT_CHAR = '@';
	private static final char UNDERSCORE_CHAR = '_';
	private static final char DOT_CHAR = '.';
	private static final int OPEN_COMMON_ACTION = 3;
	private static final String PARAM_NAME_ENTRY_NAME = "entry-name";
	private static final String PARAM_NAME_ACTION = "action";
	private static final String PARAM_NAME_PROGRESS = "progress";
	private static final int PROGRESS_NOT_STARTED = 0;
	private static final int COMMON_ACTION_CONTINUE = 3;
	
	
	private static int prepareAction(Properties user, HttpServletRequest req, ArrayList<Worker> worker) {
		user.setProperty(ALL_ENTRY_CHECKED_KEY, Boolean.toString(getParameter(req, PARAM_NAME_SELECT_ALL) != null));
		String display = getParameter(req, PARAM_NAME_DISPLAY);
		display = display == null ? getCommomTaskParameter(req) : display;
		int disp = (display == null || display.length() == 0) ? DEFAULT_DISPLAY_ACTION : convertToDisplay(display);
		switch (disp) {
		case COMMON_ACTION:
			disp = prepareCommonAction (display, user, req, worker);
			break;
		case COMMON_ACTION_CONTINUE:
			disp = prepareCommonActionCont (user, req);
			break;
		case DEFAULT_DISPLAY_ACTION:
			prepareSearchAction(user, req, worker);
		default:
			break;
		}
		return disp;
	}
	
	
	private static int prepareCommonActionCont(Properties user, HttpServletRequest req) {
		String progress = getParameter(req, PARAM_NAME_PROGRESS);
		if(progress != null) {
			user.setProperty(PrepAppClientServlet.PARAM_NAME_PROGRESS, progress);
		}
		String view = getParameter(req, PARAM_NAME_COMMON_OPEN_VIEW);
		if (view != null) {
			user.setProperty(PrepAppClientServlet.PARAM_NAME_COMMON_OPEN_VIEW, view);
		}
		req.getSession().setAttribute(PARAM_NAME_DISPLAY, Integer.toString(COMMON_ACTION));
		return COMMON_ACTION;
	}

	private static int prepareCommonAction(String display, Properties user, HttpServletRequest req, ArrayList<Worker> workers) {
		int d = DEFAULT_DISPLAY_ACTION;
		int start = display.indexOf(UNDERSCORE_CHAR);
		int end = display.lastIndexOf(DOT_CHAR);
		if(start > 0 && end > 0) {
			d = COMMON_ACTION;
			String action = display.substring(1, start);
			String item = display.substring(start + 1, end);
			
			user.setProperty(PARAM_NAME_ACTION, action);
			user.setProperty(PARAM_NAME_ENTRY_NAME, item);
			user.setProperty(PARAM_NAME_DISPLAY, Integer.toString(d));
			req.getSession().setAttribute(PARAM_NAME_DISPLAY, user.getProperty(PARAM_NAME_DISPLAY));
		}
		else {
			prepareSearchAction(user, req, workers);
		}
		return d;
	}

	private static String getCommomTaskParameter(HttpServletRequest req) {
		String name = findParamName(req.getParameterMap(), COMMON_TASK_PARAM_NAME_MASK, COMMON_TAST_PARAM_VALUE_MASK);
		return name == null ? findAttName(req.getSession(), COMMON_TASK_PARAM_NAME_MASK, COMMON_TAST_PARAM_VALUE_MASK) : name;
	}
	
	
	
	private static String findAttName(HttpSession ses, String nameMask, String valueMask) {
		Enumeration<String> map = ses.getAttributeNames();
		String name = null;
		while(map.hasMoreElements()) {
			String key = map.nextElement();
			Object value = ses.getAttribute(key);
			if(key.matches(nameMask) && value != null && value instanceof String && ((String) value).matches(valueMask)) {
				name = (String) value;
				break;
			}
		}
		return name;
	}

	private static String findParamName(Map<String, String[]> map, String nameMask, String valueMask) {
		String name = null;
		Iterator<String> keys = map.keySet().iterator();
		while(keys.hasNext()) {
			String key = keys.next();
			if(key.matches(nameMask)) {
				String[] values = map.get(key);
				if(values != null) {
					int i = 0;
					for(; i < values.length; i ++) {
						if(!values[i].matches(valueMask)) {
							break;
						}
					}
					if(i >= values.length) {
						name = key;
					}
					break;
				}
			}
		}
		return name;
	}

	private static void resetAttributes(HttpServletRequest req, String ...paramNames) {
		for(int i = 0; i < paramNames.length; i ++) {
			req.getSession().removeAttribute(paramNames[i]);
		}
	}
	
	private static String getParameter(HttpServletRequest req, String paramName) {
		String param = req.getParameter(paramName);
		String att = (String) req.getSession().getAttribute(paramName);
		if(param == null) {
			return att;
		}
		else {
			return param;
		}
		/*else {
			req.getSession().setAttribute(paramName, param);
			return param;
		}*/
	}

	private static void prepareSearchAction(Properties user, HttpServletRequest req,  ArrayList<Worker> workers) {
		boolean live = user.getProperty(CURRENT_VIEW_KEY).equalsIgnoreCase(liveStoreName);
		int rRate = archiveRefreshRate;
		ScrapEntries entries = PrepAppClientServlet.getArchiveEntries();
		if(live) {
			rRate = liveRefreshRate;
			entries = PrepAppClientServlet.getLiveEntries();
		}
		if((entries.getTimestamp() + rRate) > System.currentTimeMillis()) {
			//System.out.println("entries are ready to search " + new Date(entries.getTimestamp() + rRate) + ", " + new Date(System.currentTimeMillis()) + ", " + rRate);
			ScrapEntries found = (ScrapEntries) req.getSession().getAttribute(LOCAL_ENTRIES_KEY);
			ScrapEntries local = new ScrapEntries(user);
			/*if(found != null) {
				System.out.println((found == null) + ", " + (found != null && (found.getTimestamp() < entries.getTimestamp())));
			}*/
			if(found == null || (found != null && (found.getTimestamp() < entries.getTimestamp()))) {
				if (Worker.searchRecords (entries, workers, local)) {
					req.getSession().setAttribute(LOCAL_ENTRIES_KEY, local);
				}
			}
			found = null;
		}
		else {
			//System.out.println("entries nneds to be updated "+ new Date(entries.getTimestamp() + rRate) + ", " + new Date(System.currentTimeMillis()) + ", " + rRate);
			if(live) {
				Worker.loadActiveRecords(user, workers);
			}
			else {
				Worker.loadArchiveRecords(user, workers);
			}
		}
	}

	private static ScrapEntries getLiveEntries() {
		return entries;
	}

	private static ScrapEntries getArchiveEntries() {
		return archiveEntries;
	}

	private static int convertToDisplay(String display) {
		int dis = DEFAULT_DISPLAY_ACTION;
		if(display.charAt(0) == AT_CHAR) {
			dis = COMMON_ACTION;
		}
		else {
			try {
				dis = Integer.parseInt(display);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return dis;
	}

	private static void checkWorkers(ArrayList<Worker> workers) {
		for(int i = 0; i < workers.size(); i ++) {
			Worker w = workers.get(i);
			if(!w.isAlive() && (w.getWorkId() == 0)) {
				workers.remove(w);
			}
		}
		workers = PrepAppClientServlet.getGLobalWorkers();
		for(int i = 0; i < workers.size(); i ++) {
			Worker w = workers.get(i);
			if(!w.isAlive() && (w.getWorkId() == 0)) {
				workers.remove(w);
			}
		}
	}


	private static final String REFRESH_BLOCK_ID = "refresh-block";
	private static final String TABLE_CONTENT_CLASS = "table-content";
	private static final String REFRESH_BLOCK_TABLE_CAPTION = "Prosím čakajte na vykonanie nasledujúcich operácií";
	private static final String SLIDER_CLASS = "slider";
	private static final String COLSPAN = "colspan";
	private static final String TASK_CLASS = "task";
	
	
	private static final String HIGHLIGHT_CLASS = "highlight";
	
	private static Tag getWaitContainer(Properties prop, ArrayList<Worker> local) {
		Div div = Div.getInstance();
		div.setId(MAIN_CONTAINER_ID);
		Div refreshBlock = Div.getInstance();
		refreshBlock.setId(REFRESH_BLOCK_ID);
		Div tableFrame = Div.getInstance();
		tableFrame.setClassName(TABLE_FRAME_CLASS);
		Div tableContent = Div.getInstance();
		tableContent.setClassName(TABLE_CONTENT_CLASS);
		Table table = getRefreshBlockTable(prop, local);
		tableContent.appendChild(table);
		tableFrame.appendChild(tableContent);
		refreshBlock.appendChild(tableFrame);
		div.appendChild(refreshBlock);
		return div;
	}

	private static Table getRefreshBlockTable(Properties prop, ArrayList<Worker> local) {
		Table table = Table.getInstance(REFRESH_BLOCK_TABLE_CAPTION, 100, (workers.size() + local.size()) * 2, null);
		Tr[] tr = table.getRows();
		for(int i = 0 ; i < tr.length; i += 2) {
			int index = ((i / 2) < workers.size()) ? (i / 2) : (i / 2 - workers.size());
			Worker w = ((i / 2) < workers.size()) ? workers.get(index) : local.get(index);
			int p = w.getPercentage();
			tr[i].setClassName(TASK_CLASS);
			Tag tag =(HtmlTag) tr[i].getChildren().remove(0);
			tr[i].getChildren().removeAll(tr[i].getChildren());
			tag.setAttribute(COLSPAN, "100");
			tag.appendChild(TextTag.getInstance(w.getDescription()));
			tr[i].appendChild(tag);
			tr[i + 1].setClassName(SLIDER_CLASS);
			Td[] cells = tr[i + 1].getCells();
			for(int j = 0; j < cells.length; j ++) {
				String cName = (j == 0 ? FIRST_CLASS : "") + (j == (cells.length - 1) ? LAST_CLASS: "") + (j <= p ? HIGHLIGHT_CLASS : "");
				if(cName.length() > 0) {
					cells[j].setClassName(cName);
				}
				cells[j].setInline(true);
			}
		}
		return table;
	}

	private Object getUserSettings(HttpServletRequest req) {
		String user = req.getRemoteUser();
		Properties prop = new Properties(_default);
		//System.out.println(prop);
		try {
			prop.loadFromXML(this.getServletContext().getResourceAsStream(DATA_URL + DatatypeConverter.printHexBinary(user.getBytes())));
		} catch (Exception e) {
			try {
				File f = new File (this.getServletContext().getRealPath(DATA_URL + DatatypeConverter.printHexBinary(user.getBytes())));
				if(!f.exists()) {
					f.createNewFile();
				}
				FileOutputStream out = new FileOutputStream(f);
				prop.storeToXML(out, String.format(USER_SETTINGS_COMMENT, user), USER_SETTINGS_ENCODING);
				out.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		prop.setProperty(REMOTE_USER_KEY, user);
		prop.setProperty(ALL_ENTRY_CHECKED_KEY, Boolean.toString(false));
		req.getSession().setAttribute(USER_SETTINGS_ATTS, prop);
		//System.out.println(prop);
		return prop;
	}

	private static Tag getMainContainer(Properties prop, ScrapEntries entries) {
		Div div = Div.getInstance();
		div.setId(MAIN_CONTAINER_ID);
		div.appendChild(PrepAppClientServlet.getHeader(prop));
		div.appendChild(PrepAppClientServlet.getContent(prop, entries));
		div.appendChild(PrepAppClientServlet.getFooter(prop));
		return div;
	}
	private static final String FOOTER_ID = "footer";
	private static final String MASS_ARCHIVE_ID = "mass-archive";
	private static final String MASS_DELETE_ID = "mass-delete";
	private static final String MASS_BUTTONS_CLASS = "mass-buttons";
	private static final String BUTTON_LABEL_CLASS = "button-label";
	private static final String MASS_ARCHIVE_BUTTON_TEXT = "Archivuj označené";
	private static final String DESELECT_ALL_BUTTON_TEXT = "Zrušiť všetky";
	private static final String SELECT_ALL_BUTTON_TEXT = "Označ všetky";
	private static final String MASS_DELETE_BUTTON_TEXT = "Zmaž označené";
	private static final String COPYRIGHT_INFO_CLASS = "copyright-info";
	private static final String COPYRIGHT_TEXT = "Poštová banka, a.s., Dvořákovo nábr. 4, 801 02 Bratislava, SK prep-app-client, verzia 1.0 - &copy; - 2015";
	
	private static Tag getFooter(Properties prop) {
		Div footer = Div.getInstance();
		footer.setId(FOOTER_ID);
		Div div = Div.getInstance();
		div.setClassName(MASS_BUTTONS_CLASS);
		div.appendChild(getLabelButton(SELECT_ALL_ID, SELECT_ALL_BUTTON_TEXT));
		div.appendChild(getLabelButton(DESELECT_ALL_ID, DESELECT_ALL_BUTTON_TEXT));
		div.appendChild(getLabelButton(MASS_ARCHIVE_ID, MASS_ARCHIVE_BUTTON_TEXT));
		div.appendChild(getLabelButton(MASS_DELETE_ID, MASS_DELETE_BUTTON_TEXT));		
		footer.appendChild(div);
		div = Div.getInstance();
		div.setClassName(COPYRIGHT_INFO_CLASS);
		div.appendChild(Hr.BASE_HR);
		div.appendChild(TextTag.getInstanceNBSP(COPYRIGHT_TEXT));
		footer.appendChild(div);
		return footer;
	}

	private static Tag getLabelButton (String formId, String buttonText) {
		Div div = Div.getInstance();
		Label label = Label.getInstance(formId);
		label.setClassName(BUTTON_LABEL_CLASS);
		label.appendChild(TextTag.getInstance(buttonText));
		div.appendChild(label);
		return div;
	}

	private static final String CONTENT_ID = "content";
	private static final String DATA_CONTAINER_CLASS = "data-container";
	private static final String TABLE_FRAME_CLASS = "table-frame";
	private static final String DISPLAYED_RECORDS_KEY = "displayedRecords";
	private static final String ID = "ID";
	private static final String TEXT = "Text";
	private static final String SENT = "Odoslané";
	private static final String RESULT = "Výsledok";
	private static final String DELIVERED = "Doručené";
	private static final String TASKS = "Úlohy";
	private static final String[] TABLE_HEADER = {"&nbsp;", ID, TEXT, SENT, RESULT, DELIVERED, TASKS};
	private static final String IDT = "Interné identifikačné číslo";
	private static final String TEXTT = "Zakódovaný text požiadavky z FDS";
	private static final String SENTT = "Čas spracovania a odoslanie SMS z PB";
	private static final String RESULTT = "Výsledok spracovania žiadosti o odoslanie SMS";
	private static final String DELIVEREDT = "Čas prijatia SMS klientom";
	private static final String TASKST = "Povolené úlohy, ktoré môžete vykonávať so záznamom";
	private static final String CHECK = "označte pre hromadné spracovanie";
	private static final String[] TABLLE_HEADER_TITLE = {CHECK, IDT, TEXTT, SENTT, RESULTT, DELIVEREDT, TASKST};
	private static final String CLASS = "class";
	private static final String FIRST = "first";
	private static final String COMMON = "common";
	private static final String LAST = "last";
	private static final String[] TABLLE_HEADER_CLASS = {FIRST, COMMON, COMMON, COMMON, COMMON, COMMON, LAST};
	private static final String IDC = "id-col";
	private static final String TEXTC = "request-col";
	private static final String SENTC = "exec-time-col";
	private static final String RESULTC = "result-col";
	private static final String DELIVEREDC = "deliv-time-col";
	private static final String TASKSC = "tasks-col";
	private static final String CHECKC = "checkbox-col";
	private static final String[] TABLE_COLGROUPS = {CHECKC, IDC, TEXTC, SENTC, RESULTC, DELIVEREDC, TASKSC};
	private static final String ACTUAL_PAGE_KEY = "actualPage";
	private static final String MAIN_FORM_ID = "main-form";
	
	
	
	private static final String SELECT_ALL_FORM_ID = "select-all-form" ;
	private static final String DESELECT_ALL_FORM_ID = "deselect-all-form";
	
	
	
	private static Tag getContent(Properties prop, ScrapEntries entries) {
		Div content = Div.getInstance();
		content.setId(CONTENT_ID);
		Div dataContainer = Div.getInstance();
		dataContainer.setClassName(DATA_CONTAINER_CLASS);
		Div tableFrame = Div.getInstance();
		tableFrame.setClassName(TABLE_FRAME_CLASS);
		Form form = getForm(FORM_METHOD, FORM_ACTION, getInput(false, MASS_ARCHIVE_ID, INPUT_SUBMIT, EMPTY, ID, MASS_ARCHIVE_ID, CLASS, HIDDEN), getInput(false, MASS_DELETE_ID, INPUT_SUBMIT, EMPTY, ID, MASS_DELETE_ID, CLASS, HIDDEN));
		form.setId(MAIN_FORM_ID);
		Table table = getContentTable(prop, entries);
		form.appendChild(table);
		tableFrame.appendChild(form);
		form = getForm(FORM_METHOD, FORM_ACTION, getInput(false, SELECT_ALL_ID, INPUT_SUBMIT, EMPTY, ID, SELECT_ALL_ID, CLASS, HIDDEN));
		form.setId(SELECT_ALL_FORM_ID);
		tableFrame.appendChild(form);
		form = getForm(FORM_METHOD, FORM_ACTION, getInput(false, DESELECT_ALL_ID, INPUT_SUBMIT, EMPTY, ID, DESELECT_ALL_ID, CLASS, HIDDEN));
		form.setId(DESELECT_ALL_FORM_ID);
		tableFrame.appendChild(form);
		dataContainer.appendChild(tableFrame);
		content.appendChild(dataContainer);
		return content;
	}
	
	private static final String ODD_CLASS = "odd";
	private static final String EVEN_CLASS = "even";
	
	private static final String BOTTOM_CLASS_SUFFIX = "-bottom";
	
	private static final String ID_CLASS = "id";
	private static final String ERROR_ID_TITLE = "ID nebolo vrátené z dôvodu chyby alebo ide o starú verziu záznamu";
	private static final String REQUEST_CLASS = "request";
	private static final String EXEC_TIME_CLASS = "exec-time";
	private static final String RESULT_CLASS = "result";
	private static final String OK_RESULT = "OK";
	private static final String DELIVERY_CLASS = "deliv-time";
	private static final String NO_DELIVERY_TITLE = "Pre získanie času doručenia je nutné použiť akciu z povolených úloh, ak je to možné";
	private static final String TASKS_CLASS = "tasks";
	private static final String UNKNOVN_RECEPTIONTIME = "čas neznámy";
	private static final String OPEN_ACTION = "open";
	private static final String DISPLAY_ENTRY_TITLE = "zobraz záznam";
	private static final String DISPLAY_ENTRY_ALT = DISPLAY_ENTRY_TITLE;
	private static final String DISPLAY_ENTRY_SRC = "images/open.png";
	private static final String ACQUIRE_RECEPTION_ENTRY_TITLE = "získaj čas doručenia";
	private static final String ACQUIRE_RECEPTION_ENTRY_ALT = ACQUIRE_RECEPTION_ENTRY_TITLE;
	private static final String ACQUIRE_RECEPTION_ENTRY_SRC = "images/delivery.png";
	private static final String DISPLAY_ENTRY_ERROR_TITLE = "zobraz chybu";
	private static final String DISPLAY_ENTRY_ERROR_ALT = DISPLAY_ENTRY_ERROR_TITLE;
	private static final String DISPLAY_ENTRY_ERROR_SRC = "images/error.png";
	private static final String ARCHIVE_ENTRY_TITLE = "archivuj záznam";
	private static final String ARCHIVE_ENTRY_ALT = ARCHIVE_ENTRY_TITLE;
	private static final String ARCHIVE_ENTRY_SRC = "images/archive.png";
	private static final String DELETE_ENTRY_TITLE = "zmaž záznam";
	private static final String DELETE_ENTRY_ALT = DISPLAY_ENTRY_TITLE;
	private static final String DELETE_ENTRY_SRC = "images/delete.png";
	private static final String DELIVERY_ACTION = "delivery";
	private static final String OPEN_ERROR_ACTION = "open-error";
	private static final String ARCHIVE_ACTION = "archive";
	private static final String DELETE_ACTION = "delete";
	private static final String RESULT_ERROR_TEXT = "ERROR";
	private static final String CHECKBOX_CLASS = "checkbox";
	private static final String MASS_CHECKBOX = "mass";
	private static final String INPUT_CHECKBOX = "checkbox";
	private static final String ALL_ENTRY_CHECKED_KEY = "allEntriesChecked";
	
	
	private static Table getContentTable(Properties prop, ScrapEntries entries) {
		int maxRows = Integer.parseInt(prop.getProperty(DISPLAYED_RECORDS_KEY));
		int page = Integer.parseInt(prop.getProperty(ACTUAL_PAGE_KEY));
		boolean checked = Boolean.parseBoolean(prop.getProperty(ALL_ENTRY_CHECKED_KEY));
		int maxPage = ((entries.size() + 1) / maxRows) - 1;
		page = page > maxPage ? maxPage : page;
		int index = page * maxRows;
		int length = (entries.size() - index) > maxRows ? maxRows : entries.size() - index;
		Table table = Table.getInstance(null, 7, maxRows, TABLE_HEADER);
		table.setColgroup(TABLE_COLGROUPS);
		List<Tag> list = table.getHeader().getChildren().get(0).getChildren();
		for(int i = 0; i < list.size(); i ++) {
			list.get(i).setAttribute(TITLE, TABLLE_HEADER_TITLE[i]);
			list.get(i).setAttribute(CLASS, TABLLE_HEADER_CLASS[i]);
		}
		Tr[] rows = table.getRows();
		for(int i = 0; i < rows.length; i ++) {
			rows[i].setClassName(((i % 2) == 0) ? ODD_CLASS : EVEN_CLASS);
			Td[] cells = rows[i].getCells();
			for(int j = 0; j < cells.length; j ++) {
				cells[j].setClassName (((j == 0 || j == (cells.length -1)) ? (j == 0 ? FIRST_CLASS : LAST_CLASS) : COMMON_CLASS) + ((i == (rows.length - 1)) ? BOTTOM_CLASS_SUFFIX : EMPTY));
				if(length > 0) {
					table.setContent(getCellContent (j, cells[j], entries.get(index), checked),  j, i);
				}
			}
			length --;
			index ++;
		}
		return table;
	}

	private static Tag getCellContent(int index, Td cell, Hashtable<String, Object> entry, boolean checked) {
		String name = entry.get(NAME_KEY) == null ? EMPTY : entry.get(NAME_KEY).toString();
		switch (index) {
		case 0:
			return getCellContent (cell, null, CHECKBOX_CLASS, getInput(checked, MASS_CHECKBOX/* + UNDERSCORE + name*/, INPUT_CHECKBOX, name));
		case 1: 
			return getCellContent (cell, getTitle(entry.get(INTERNAL_ID_KEY), ERROR_ID_TITLE, true), ID_CLASS, getText(entry.get(INTERNAL_ID_KEY)));
		case 2: 
			return getCellContent (cell, getTitle(entry.get(ENCODED_REQUEST_KEY), (String) entry.get(ENCODED_REQUEST_KEY), false), REQUEST_CLASS, getText(entry.get(ENCODED_REQUEST_KEY)));
		case 3: 
			return getCellContent (cell, null, EXEC_TIME_CLASS, getDatetime(entry.get(EXECUTIONTIME_KEY)));
		case 4: 
			return getCellContent (cell, getResultTitle(entry.get(FAULT_KEY)), RESULT_CLASS, getResultText(entry.get(FAULT_KEY)));
		case 5: 
			return getCellContent (cell, getTitle (entry.get(RECEPTIONTIME_KEY), NO_DELIVERY_TITLE, true), DELIVERY_CLASS, getReceptionText(entry.get(RECEPTIONTIME_KEY)));
		case 6: 
			return getCellContent (cell, null, TASKS_CLASS, 
					getTask(name, OPEN_ACTION, DISPLAY_ENTRY_ALT, DISPLAY_ENTRY_TITLE, DISPLAY_ENTRY_SRC, true),
					TextTag.NBSP,
					getTask(name, DELIVERY_ACTION, ACQUIRE_RECEPTION_ENTRY_ALT, ACQUIRE_RECEPTION_ENTRY_TITLE, ACQUIRE_RECEPTION_ENTRY_SRC, checkIfExists(entry.get(INTERNAL_ID_KEY))),
					checkIfExists(entry.get(INTERNAL_ID_KEY)) ? TextTag.NBSP : null,
					getTask(name, OPEN_ERROR_ACTION, DISPLAY_ENTRY_ERROR_ALT, DISPLAY_ENTRY_ERROR_TITLE, DISPLAY_ENTRY_ERROR_SRC, !checkValue (entry.get(FAULT_KEY), OK_RESULT)),
					!checkValue (entry.get(FAULT_KEY), OK_RESULT) ? TextTag.NBSP : null,
					getTask(name, ARCHIVE_ACTION, ARCHIVE_ENTRY_ALT, ARCHIVE_ENTRY_TITLE, ARCHIVE_ENTRY_SRC, true),
					TextTag.NBSP,
					getTask(name, DELETE_ACTION, DELETE_ENTRY_ALT, DELETE_ENTRY_TITLE, DELETE_ENTRY_SRC, true));
		default:
		}
		return null;
	}
	
	
	
	private static Tag getResultText(Object result) {
		return result != null ? ((!result.equals(OK_RESULT) && result.toString().length() > 0)) ? getText(RESULT_ERROR_TEXT) : getText(result) : getText(result);
	}

	private static boolean checkValue(Object error, Object value) {
		return checkIfExists(error) ? (value != null ? error.equals(value) : false) : (value == null ? true : false);
	}

	private static boolean checkIfExists(Object object) {
		return !(object == null || (object != null && object.toString().length() == 0));
	}

	private static Tag getTask(String name, String action, String alt, String title, String src, boolean visible) {
		/*Form form = null;
		if (visible) {
			
			form = getForm(FORM_METHOD, FORM_ACTION, getInput(action, INPUT_HIDDEN, name), getInput(null, INPUT_IMAGE, null, ALT, alt, TITLE, title, SRC, src));
			form.setInline(true);
			
		}
		return form;*/
		Tag tag = null;
		if(visible) {
			tag = getInput (false, "@" + action + "_" + name, INPUT_IMAGE, null, ALT, alt, TITLE, title, SRC, src);
			tag.setInline(true);
		}
		return tag;
	}
	
	private static Tag getReceptionText (Object date) {
		return date != null ? (date instanceof Date ? getText (getDateformat().format(date)) : (date.toString().length() > 0 ? TextTag.getInstanceNBSP(date.toString()) : TextTag.getInstanceNBSP(UNKNOVN_RECEPTIONTIME))) : TextTag.getInstanceNBSP(UNKNOVN_RECEPTIONTIME);
	}
	
	private static String getResultTitle(Object result) {
		return (result != null && result.toString().length() > 0 && !result.toString().equals(OK_RESULT)) ? result.toString() : null;
	}

	private static Tag getText(Object object) {
		return object != null ? (object.toString().length() > 0 ? TextTag.getInstanceNBSP(object.toString()) : TextTag.NBSP) : TextTag.NBSP;
	}

	private static Tag getDatetime(Object date) {
		return date != null ? (date instanceof Date ? getText (getDateformat().format(date)) : getText(date)) : TextTag.NBSP;
	}

	private static String getTitle(Object value, String title, boolean reverse) {
		return (value != null && value.toString().length() > 0) ? (reverse ? null : title) : (reverse ? title : null);
	}

	private static Tag getCellContent (Td cell, String title, String className, Tag ...contents) {
		cell.setInline(true);
		setTitle(cell, title);
		Div div = Div.getInstance();
		div.setInline(true);
		div.setClassName(className);;
		for (int i = 0; i < contents.length; i ++) {
			if(contents[i] != null) {
				div.appendChild(contents[i]);
			}
		}
		return div;
	}
		
	static final String LIVE_STORE_KEY = "liveStore";
	static final String ARCHIVE_FILE_KEY = "archiveFile";
	static final String LIVE_VIEW_NAME_KEY = "liveViewName";
	static final String LIVE_STORE_ENCODING_KEY = "liveStoreEncoding";
	static final String ARCHIVE_FILE_ENCODING_KEY = "archiveFileEncoding";
	static final String REQUEST_KEY = "REQUEST";
	static final String EXECUTIONTIME_KEY = "EXECUTIONTIME";
	static final String FAULT_KEY = "FAULT";
	static final String RECEPTIONTIME_KEY = "RECEPTIONTIME";
	static final String INTERNAL_ID_KEY = "INTERNAL_ID";
	static final String NAME_KEY = "ENTRY_NAME";
	static final String SOAP_ENVELOPE = "envelope";
	static final String SOAP_BODY = "body";
	static final String FILTER_FLAG_KEY = "filterFlag";
	static final String TIME_FILTER_MIN_KEY = "timeFilterMinDate";
	static final String TIME_FILTER_MAX_KEY = "timeFilterMaxDate";
	static final String MOBILE_NO_FILTER_LIST_KEY = "mobileNoFilterList";
	static final String SEPARATOR_KEY = "separator";
	static final String AMOUNT_FILTER_MIN_KEY = "AmountFilterMin";
	static final String AMOUNT_FILTER_MAX_KEY = "AmountFilterMax";
	static final String AMOUNT_FILTER_CURRENCY_KEY = "AmountFilterCrrency";
	static final int MAX_NUMBERS = 10;
	
	
	
	static final String ARCHIVE_VIEW_NAME_KEY = "archiveViewName";
	public static final String ENCODED_REQUEST_KEY = "ENCODED_REQUEST";
	private static final String CHECKED = "checked";
	
	

	private static Tag getUserDetails(Properties prop) {
		Div div = Div.getInstance();
		div.setId(USER_DETAILS_ID);
		Span span = Span.getInstance();
		setTitle (span, USER_DETAILS_TITLE);
		span.appendChild(TextTag.getInstanceNBSP(prop.getProperty(REMOTE_USER_KEY) + "  "));
		span.setInline(true);
		div.appendChild(span);
		Form form = getForm (FORM_METHOD, FORM_ACTION, getInput(false, DISPLAY_ACTION, INPUT_HIDDEN, prop.getProperty(USER_DETAILS_KEY)), getInput (false, null, INPUT_IMAGE, null, ALT, USER_ICON_ALT, SRC, USER_IMAGE_SRC, TITLE, USER_DETAILS_IMAGE_TITLE));
		form.setInline(true);
		div.appendChild(form);
		div.appendChild(Hr.BASE_HR);
		return div;
	}

	private static void setTitle(Tag tag, String title) {
		if(tag != null && title != null && title.length() > 0) {
			tag.setAttribute(TITLE, title);		
		}
	}

	private static Tag getHeader(Properties prop) {
		Div div = Div.getInstance();
		div.setId(HEADER_ID);
		div.appendChild(getAppName());
		div.appendChild(getMenu(prop));
		div.appendChild(getUserDetails(prop));
		return div;
	}

	private static Tag getMenu(Properties prop) {
		Div div = Div.getInstance();
		div.setId(MENU_ID);
		Form form = getForm(FORM_METHOD, FORM_ACTION, getInput(false, DISPLAY_ACTION, INPUT_HIDDEN, prop.getProperty(CURRENT_VIEW_KEY)), getInput(false, null, INPUT_SUBMIT, MENU_ITEM_ARCHIVE));
		form.setInline(true);
		div.appendChild(form);
		form = getForm(FORM_METHOD, FORM_ACTION, getInput(false, DISPLAY_ACTION, INPUT_HIDDEN, prop.getProperty(USER_MANAGEMENT_KEY)), getInput(false, null, INPUT_SUBMIT, MENU_ITEM_USER_MANAGEMENT));
		form.setInline(true);
		div.appendChild(form);
		div.appendChild(Hr.BASE_HR);
		return div;
	}

	private static Form getForm(String formMethod, String formAction, Tag ... tags) {
		Form form = Form.getInstance(formAction, formMethod);
		for(int i = 0; i < tags.length; i ++) {
			form.appendChild(tags[i]);
		}
		return form;
	}

	private static Tag getInput(boolean checked, String ... atts) {
		Input input = Input.getInstance(atts[0], atts[1], atts[2]);
		if(checked) {
			input.setAttribute(CHECKED, EMPTY);
		}
		for(int i = 3; i < atts.length; i += 2) {
			input.setAttribute(atts[i], atts[i + 1]);
		}
		return input;
	}

	private static Tag getAppName() {
		Div div = Div.getInstance();
		div.setClassName(APP_NAME_CLASS);
		div.appendChild(TextTag.getInstance(APP_NAME));
		return div;
	}

	public ArrayList<Worker> getWorkers() {
		return this.localWorkers;
	}

	public static ArrayList<Worker> getGLobalWorkers() {
		return workers;
	}

	public static void setActiveEntries(ScrapEntries entries) {
		PrepAppClientServlet.entries = entries;		
	}

	public static DocumentBuilder getDOMBuilder() {
		return PrepAppClientServlet.builder;
	}

	public static void setArchiveEntries(ScrapEntries entries) {
		PrepAppClientServlet.archiveEntries = entries;	
	}

	public static Locale getLocale() {
		return locale;
	}

	public static void setLocale(Locale locale) {
		PrepAppClientServlet.locale = locale;
	}

	public static SimpleDateFormat getDateformat() {
		return dateformat;
	}

	public static void setDateformat(SimpleDateFormat dateformat) {
		PrepAppClientServlet.dateformat = dateformat;
	}
}
