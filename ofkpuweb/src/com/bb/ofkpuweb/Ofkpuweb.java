package com.bb.ofkpuweb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pabk.emanager.sql.sap.CompPred;
import org.pabk.emanager.sql.sap.Identifier;
import org.pabk.emanager.sql.sap.SchemaName;
import org.pabk.emanager.sql.sap.TableName;
import org.pabk.emanager.sql.sap.WhereClause;
import org.pabk.html.Div;
import org.pabk.html.Hr;
import org.pabk.html.HtmlTag;
import org.pabk.html.Img;
import org.pabk.html.Input;
import org.pabk.html.Table;
import org.pabk.html.Tag;
import org.pabk.html.Td;
import org.pabk.html.TextTag;
import org.pabk.util.Base64Coder;
import org.pabk.web.db.DBConnector;
import org.pabk.web.db.Rows;

import com.bb.commons.Article;
import com.bb.commons.Partner;
import com.bb.commons.ShortMessage;

public class Ofkpuweb extends Core {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String OFK_BDY_CLASS = "ofk-bdy";
	private static final String OFK_HDR_CLASS = "ofk-hdr";
	private static final String OFK_CNT_CLASS = "ofk-cnt";
	private static final String STYLE_URL_KEY = "ofkpuweb.styleUrl";
	private static final String OFK_CENTRAL_HDR_CLASS = "ofk-central-hdr";
	private static final String OFK_MAX_WIDTH_CLASS = "ofk-max-width";
	private static final String LOGGED_IN_TEXT_KEY = "ofkpuweb.text.loggedIn";
	private static final String OFK_HDR_INF_CLASS = "ofk-hdr-info";
	private static final String LOGIN_TEXT_KEY = "ofkpuweb.text.login";
	private static final String OFK_LOGOS_CLASS = "ofk-logos";
	private static final String OFK_MENUS_CLASS = "ofk-menus";
	private static final String OFK_MAIN_LOGO_CLASS = "ofk-main-logo";
	private static final String MAIN_LOGO_URL_KEY = "ofkpuweb.mainLogo.url";
	private static final String OFK_UNIONS_LOGOS_CLASS = "ofk-unions-logos";
	private static final String SFZ_PAGE_KEY = "ofkpuweb.sfz.link";
	private static final String SFZ_LOGO_URL_KEY = "ofkpuweb.sfzLogo.url";
	private static final String ZSFZ_PAGE_KEY = "ofkpuweb.zsfz.link";
	private static final String ZSFZ_LOGO_URL_KEY = "ofkpuweb.zsfzLogo.url";
	private static final String OBFZGA_PAGE_KEY = "ofkpuweb.obfzga.link";
	private static final String OBFZGA_LOGO_URL_KEY = "ofkpuweb.obfzgaLogo.url";
	private static final String OFK_EMPTY_LOGO_CLASS = "ofk-empty-logo";
	private static final String OFK_CAPTION_CLASS = "ofk-caption";
	private static final String OFK_CAPTION_1_CLASS = "ofk-caption-1";
	private static final String OFK_CLATION_2_CLASS = "ofk-caption-2";
	private static final String CAPTION_LINE1_KEY = "ofkpuweb.caption.line1";
	private static final String CAPTION_LINE2_KEY = "ofkpuweb.caption.line2";
	private static final String MENU_ITEM_KEY = "ofkpuweb.menu.item.%d";
	private static final String OFK_MENU_FRAME_CLASS = "ofk-menu-frame";
	private static final String OFK_MENU_ITEM_CLASS = "ofk-menu-item";
	private static final String MENU_ITEM_ONMOUSEOVER_KEY = "ofkpuweb.menu.item.onmouseover";
	private static final String MENU_ITEM_ONMOUSEOUT_KEY = "ofkpuweb.menu.item.onmouseout";
	private static final String MENU_ITEM_ONCLICK_KEY = "ofkpuweb.menu.item.%d.onclick";
	private static final String MENU_ITEM_URL_KEY = "ofkpuweb.menu.item.%d.url";
	private static final String MENU_ITEM_ITEM_KEY = "ofkpuweb.menu.item.%d.%d";
	private static final String MENU_ITEM_ITEM_URL_KEY = "ofkpuweb.menu.item.%d.%d.url";
	private static final String OFK_MENU_CONTENT_CLASS = "ofk-menu-content";
	private static final String OFK_MENU_CLASS = "ofk-menu";
	private static final String OFK_MENU_INVISIBLE_CLASS = "ofk-menu-invisible";
	private static final String MASK_MENU_ID = "ofk-menu-%d";
	private static final String MASK_MENU_EXIT_ID = "ofk-menu-exit-%d";
	private static final String OFK_MENU_CAPTION_CLASS = "ofk-menu-caption";
	private static final String OFK_MENU_ITEMS_CLASS = "ofk-menu-items";
	private static final String OFK_MENU_GROUP_CLASS = "ofk-menu-group";
	private static final String OFK_MENU_GROUP_VR_CLASS = "ofk-menu-group-vr";
	private static final String OFK_MENU_CONTAINER_CLASS = "ofk-menu-container";
	private static final String OFK_MENU_ITEM_ITEM_CLASS = "ofk-menu-item-item";
	private static final String OFK_MENU_ITEM_ITEM_PFX_CLASS = "ofk-menu-item-item-pfx";
	private static final String MENU_ITEM_ITEM_PFX_KEY = "ofkpuweb.menu.item.pfx";
	private static final String OFK_MENU_ITEM_ITEM_LINK_CLASS = "ofk-menu-item-item-link";
	private static final String OFK_MENU_EMPTY_CLASS = "ofk-menu-empty";
	private static final String SCRIPT_SOURCE_KEY = "ofkpuweb.jscript.source";
	private static final String OFK_MENU_VISIBLE_CLASS = "ofk-menu-visible";
	private static final String OFK_MENU_EXIT_CLASS = "ofk-menu-exit";
	private static final String MENU_EXIT_BGIMAGE_KEY = "ofkpuweb.menu.exit.bgImage";
	private static final String MOUSE_ITEMS_ONMOUSEOVER_KEY = "ofkpuweb.menu.items.onmouseover";
	private static final String MOUSE_ITEMS_ONMOUSEOUT_KEY = "ofkpuweb.menu.items.onmouseout";
	private static final String OFK_CNT_FRAME_CLASS = "ofk-cnt-frame";
	private static final String PAGE_ENCODING_KEY = "ofkpuweb.page.encoding";
	private static final String TOP_LIMIT_KEY = "ofkpuweb.top.limit";
	private static final String TOP_DAYS_AGE_KEY = "ofkpuweb.top.daysAgo";
	private static final String DEFAULT_ARTICLE_KEY = "ofkpuweb.fefault.article";
	private static final String WINDOW_ONLOAD_KEY = "ofkpuweb.window.onload";
	private static final String TOP_INTERVAL_VALUE_KEY = "ofkpuweb.top.interval.value";
	private static final int MAIN_PAGE = 0;
	private static final String OFK_HLN_CLASS = "ofk-hln";
	private static final String OFK_CNT_WIDTH_CLASS = "ofk-cnt-width";
	private static final String OFK_CNT_2_CLASS = "ofk-cnt-2";
	private static final String OFK_HLN_ID = OFK_HLN_CLASS;
	private static final String OFK_HLN_NAME = OFK_HLN_CLASS;
	private static final String LANGUAGE_KEY = "ofkpuweb.language";
	private static final String TOP_DATEFORMAT_KEY = "ofkpuweb.top.dateformat";
	private static final String SAB_DATEFORMAT_KEY = "ofkpuweb.sab.dateformat";
	private static final String SAB_ONE_ID_KEY = "ofkpuweb.sab.1.id";
	private static final String SAB_ONE_CATEGORY_ID_KEY = "ofkpuweb.sab.1.categoryId";
	private static final String OFK_BANS_CLASS = "ofk-bans";
	private static final String OFK_LBAN_CLASS = "ofk-lban";
	private static final String OFK_RBAN_CLASS = "ofk-rban";
	private static final String OFK_BTM_CLASS = "ofk-btm";
	private static final String PARTNERS_INTERVAL_VALUE_KEY = "ofkpuweb.partners.interval.value";
	private static final String OFK_BAN_CLASS = "ofk-ban";
	private static final String OKF_CNT_2_TOP_CLASS = "ofk-cnt-2-top";
	private static final String OFK_CNT_2_BANS_CLASS = "ofk-cnt-2-bans";
	private static final String OFK_CNT_2_RBAN_CLASS = "ofk-cnt-2-rban";
	private static final String OFK_CNT_2_LBAN_CLASS = "ofk-cnt-2-lban";
	private static final String MSG_DATEFORMAT_KEY = "ofkpuweb.msg.dateformat";
	private static final String OFK_CNT_PAD_CLASS = "ofk-cnt-pad";
	private static final String OFK_LBAN_SBOX_CLASS_PFX = "ofk-lban-sbox-";
	private static final String OFK_LBAN_LBOX_CLASS = "ofk-lban-lbox";
	
	private static SimpleDateFormat topDateFormat;
	private static SimpleDateFormat sabDateFormat;
	private static SimpleDateFormat msgDateFormat;
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		Core.setLocale(super.getProperties().getProperty(LANGUAGE_KEY, DEFAULT_LANGUAGE));
		topDateFormat = new SimpleDateFormat(super.getProperties().getProperty(TOP_DATEFORMAT_KEY), Core.getLocale());
		sabDateFormat = new SimpleDateFormat(super.getProperties().getProperty(SAB_DATEFORMAT_KEY), Core.getLocale());
		msgDateFormat = new SimpleDateFormat(super.getProperties().getProperty(MSG_DATEFORMAT_KEY), Core.getLocale());
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Properties props = getProperties();
		Tag body = Core.getDiv(OFK_BDY_CLASS, Core.getDiv(OFK_HDR_CLASS, getHeader(request, props)));
		body.appendChild(getContent(props));
		request.setAttribute(Core.PAGE_CONTENT_ATT_NAME, body);
		request.setAttribute(Core.PAGE_STYLE_ATT_NAME, props.getProperty(STYLE_URL_KEY));
		request.setAttribute(Core.PAGE_SCRIPT_SOURCE_ATT_NAME, props.getProperty(SCRIPT_SOURCE_KEY));
		request.setAttribute(Core.PAGE_ONLOAD_ATT_NAME, String.format(props.getProperty(Ofkpuweb.WINDOW_ONLOAD_KEY), OFK_HLN_ID, Long.parseLong(props.getProperty(TOP_INTERVAL_VALUE_KEY)), OFK_PRRS_CLASS, OFK_ITM_ID, Long.parseLong(props.getProperty(PARTNERS_INTERVAL_VALUE_KEY))));
		super.doGet(request, response);
	}

	private static Tag getContent(Properties props) throws IOException {
		Connection con = null;
		try {
			con = DBConnector.lookup(props.getProperty(Core.DSN_KEY));
			Tag cnt = Core.getDiv(OFK_CNT_CLASS, getContentOfMenu(props));
			Tag frame = Core.getDiv(OFK_CNT_FRAME_CLASS + Core.SPACE_CHAR + OFK_MAX_WIDTH_CLASS, null);
			Tag margin = Core.getDiv(OFK_CNT_PAD_CLASS, frame);
			Tag top = Core.getDiv(OKF_CNT_2_TOP_CLASS + Core.SPACE_CHAR + OFK_CNT_WIDTH_CLASS, null);
			Tag cnt2 = Core.getDiv(OFK_CNT_2_CLASS, top);
			Tag rban = Core.getDiv(OFK_CNT_2_RBAN_CLASS, null);
			Tag lban = Core.getDiv(OFK_CNT_2_LBAN_CLASS, null);
			Tag bans = Core.getDiv(OFK_CNT_2_BANS_CLASS + Core.SPACE_CHAR + OFK_CNT_WIDTH_CLASS, lban);
			bans.appendChild(rban);
			cnt2.appendChild(bans);
			Tag lBan = Core.getDiv(OFK_LBAN_CLASS, null);
			Tag rBan = Core.getDiv(OFK_RBAN_CLASS, null);
			bans = Core.getDiv(OFK_BANS_CLASS, lBan);
			bans.appendChild(rBan);
			getPageContent(top, lban, rban, props, con);
			frame.appendChild(cnt2);
			frame.appendChild(getBottomContent(props, con));
			cnt.appendChild(margin);
			return cnt;
		}
		catch (Exception e) {
			throw new IOException (e);
		}
		finally {
			try {con.close();} catch (SQLException e) {}
		}
	}

	private static Tag getBottomContent(Properties props, Connection con) throws SQLException, IOException {
		Tag lBan = Core.getDiv(OFK_LBAN_CLASS, getLongBox(getPartners(props, con)));
		Tag fb = getFacebookLink(props, 
				props.getProperty(FB_URL_KEY),
				props.getProperty(FB_DATA_TABS_KEY),
				props.getProperty(FB_DATA_HEIGHT_KEY),
				props.getProperty(FB_DATA_WIDTH_KEY),
				props.getProperty(FB_DATA_SMALL_HEADER_KEY),
				props.getProperty(FB_DATA_ADAPT_CONTAINER_WIDTH_KEY),
				props.getProperty(FB_DATA_HIDE_COVER_KEY),
				props.getProperty(FB_DATA_SHOW_FACEPILE_KEY));
		Tag rBan = Core.getDiv(OFK_RBAN_CLASS, getSmallBox(Ofkpuweb.getWindow(props.getProperty(FB_CAPTION_KEY), String.format(props.getProperty(WIN_LOGO_KEY), props.getProperty(MAIN_LOGO_URL_KEY)), null, null, Core.getDiv(OFK_FB_PAGE_CLASS, fb))));
		Tag bottom = Core.getDiv(OFK_BTM_CLASS + Core.SPACE_CHAR + OFK_BAN_CLASS + Core.SPACE_CHAR + Ofkpuweb.OFK_CNT_WIDTH_CLASS, lBan);
		bottom.appendChild(rBan);
		return bottom;
	}
	
	private static final String FB_PAGE_CLASS = "fb-page";
	private static final String FB_DATA_HREF_ATT_NAME = "data-href";
	private static final String FB_URL_KEY = "ofkpuweb.fb.url";
	private static final String FB_DATA_TABS_ATT_NAME = "data-tabs";
	private static final String FB_DATA_TABS_KEY = "ofkpuweb.fb.tabs";
	private static final String FB_DATA_HEIGHT_ATT_NAME = "data-height";
	private static final String FB_DATA_HEIGHT_KEY = "ofkpuweb.fb.height";
	private static final String FB_DATA_SMALL_HEADER_ATT_NAME = "data-small-header";
	private static final String FB_DATA_SMALL_HEADER_KEY = "ofkpuweb.fb.smallHeader";
	private static final String FB_DATA_ADAPT_CONTAINER_WIDTH_ATT_NAME = "data-adapt-container-width";
	private static final String FB_DATA_ADAPT_CONTAINER_WIDTH_KEY = "ofkpuweb.fb.url.adaptContainerWidth";
	private static final String FB_DATA_HIDE_COVER_ATT_NAME = "data-hide-cover";
	private static final String FB_DATA_HIDE_COVER_KEY = "ofkpuweb.fb.url.hideCover";
	private static final String FB_DATA_SHOW_FACEPILE_ATT_NAME = "data-show-facepile";
	private static final String FB_DATA_SHOW_FACEPILE_KEY = "ofkpuweb.fb.url.showFacepile";
	private static final String FB_CAPTION_KEY = "ofkpuweb.fb.caption";
	private static final String OFK_FB_PAGE_CLASS = "ofk-fb-page";
	private static final String FB_DATA_WIDTH_ATT_NAME = "data-width";
	private static final String FB_DATA_WIDTH_KEY = "ofkpuweb.fb.width";
	private static final String[] FB_ATTS = {
			FB_DATA_HREF_ATT_NAME,
			FB_DATA_TABS_ATT_NAME,
			FB_DATA_HEIGHT_ATT_NAME,
			FB_DATA_WIDTH_ATT_NAME,
			FB_DATA_SMALL_HEADER_ATT_NAME,
			FB_DATA_ADAPT_CONTAINER_WIDTH_ATT_NAME,
			FB_DATA_HIDE_COVER_ATT_NAME,
			FB_DATA_SHOW_FACEPILE_ATT_NAME
	};
	
	private static Tag getFacebookLink(Properties props, String ...values) {
		Tag fb = Core.getDiv(FB_PAGE_CLASS, null);
		for(int i = 0; i < FB_ATTS.length; i ++) {
			if(i <values.length && values[i] != null) {
				fb.setAttribute(FB_ATTS[i], values[i]);
			}
		}
		/*
		fb.setAttribute(FB_DATA_HREF_ATT_NAME, props.getProperty(FB_URL_KEY));
		fb.setAttribute(FB_DATA_TABS_ATT_NAME, props.getProperty(FB_DATA_TABS_KEY));
		fb.setAttribute(FB_DATA_HEIGHT_ATT_NAME, props.getProperty(FB_DATA_HEIGHT_KEY));
		fb.setAttribute(FB_DATA_WIDTH_ATT_NAME, props.getProperty(FB_DATA_WIDTH_KEY));
		fb.setAttribute(FB_DATA_SMALL_HEADER_ATT_NAME, props.getProperty(FB_DATA_SMALL_HEADER_KEY));
		fb.setAttribute(FB_DATA_ADAPT_CONTAINER_WIDTH_ATT_NAME, props.getProperty(FB_DATA_ADAPT_CONTAINER_WIDTH_KEY));
		fb.setAttribute(FB_DATA_HIDE_COVER_ATT_NAME, props.getProperty(FB_DATA_HIDE_COVER_KEY));
		fb.setAttribute(FB_DATA_SHOW_FACEPILE_ATT_NAME, props.getProperty(FB_DATA_SHOW_FACEPILE_KEY));*/
		return fb;
	}

	private static final String PARTNERS_CAPTION_KEY = "ofkpuweb.partners.caption";
	private static final String OFK_PRRS_CLASS = "ofk-prrs";
	private static final String OKF_PRRS_TOP_CLASS = "ofk-prrs-top";
	private static final String OKF_PRRS_BOT_CLASS = "ofk-prrs-bot";
	private static final String OFK_PRRS_TOP_ITM_CLASS = "ofk-prrs-top-itm";
	private static final String OFK_PRRS_BOT_ITM_CLASS = "ofk-prrs-bot-itm";
	private static final String OFK_ITM_ID = "ofk-itm-";
	private static final String OFK_ITM_1_ID = "ofk-itm-1";
	private static final String OFK_ITM_2_ID = "ofk-itm-2";
	private static final String OFK_ITM_3_ID = "ofk-itm-3";
	private static final String OFK_ITM_4_ID = "ofk-itm-4";
	private static final String OFK_ITM_5_ID = "ofk-itm-5";
	private static final String OFK_ITM_NAME = "ofk-itm-";
	private static final String OFK_PRR_TYPE_CLASS = "ofk-prr-type";
	private static final String OFK_PRR_CLASS = "ofk-prr";
	private static final String OFK_PRR_LOGO_CLASS = "ofk-prr-logo";
	private static final String OFK_PRR_NAME_CLASS = "ofk-prr-name";
	
	
	private static Tag getPartners(Properties props, Connection con) throws SQLException, IOException {
		Partner[] partners = Utils.getPartners(con, props);
		Tag top1 = Core.getDiv(OFK_PRRS_TOP_ITM_CLASS, getPartnerContent());
		top1.setAttribute(ID_ATT_NAME, OFK_ITM_1_ID);
		Tag top2 = Core.getDiv(OFK_PRRS_TOP_ITM_CLASS, getPartnerContent());
		top2.setAttribute(ID_ATT_NAME, OFK_ITM_2_ID);
		Tag top = Core.getDiv(OKF_PRRS_TOP_CLASS, top1);
		top.appendChild(top2);
		Tag bot1 = Core.getDiv(OFK_PRRS_BOT_ITM_CLASS, getPartnerContent());
		bot1.setAttribute(ID_ATT_NAME, OFK_ITM_3_ID);
		Tag bot2 = Core.getDiv(OFK_PRRS_BOT_ITM_CLASS, getPartnerContent());
		bot2.setAttribute(ID_ATT_NAME, OFK_ITM_4_ID);
		Tag bot3 = Core.getDiv(OFK_PRRS_BOT_ITM_CLASS, getPartnerContent());
		bot3.setAttribute(ID_ATT_NAME, OFK_ITM_5_ID);
		Tag bot = Core.getDiv(OKF_PRRS_BOT_CLASS, bot1);
		bot.appendChild(bot2);
		bot.appendChild(bot3);
		Tag cnt = Core.getDiv(OFK_PRRS_CLASS, top);
		cnt.appendChild(bot);
		for(int i = 0; i < partners.length; i ++) {
			cnt.appendChild(Input.getInstance(OFK_ITM_NAME + (i + 1), Core.HIDDEN_TYPE, getPartner(partners[i], props.getProperty(Ofkpuweb.PAGE_ENCODING_KEY))));
		}
		return Ofkpuweb.getWindow(props.getProperty(PARTNERS_CAPTION_KEY), String.format(props.getProperty(WIN_LOGO_KEY), props.getProperty(MAIN_LOGO_URL_KEY)), null, null, cnt);
	}

	private static String getPartner(Partner partner, String encoding) throws IOException {
		String s = partner.getName() + Core.VERTICAL_BAR_CHAR + partner.getType() + Core.VERTICAL_BAR_CHAR + partner.getPhoto_id() + Core.VERTICAL_BAR_CHAR + partner.getId() + Core.VERTICAL_BAR_CHAR + partner.getUrl();
		return Base64.getEncoder().encodeToString(s.getBytes(encoding));
	}

	private static Tag getPartnerContent() {
		Tag p = Core.getDiv(OFK_PRR_CLASS, Core.getDiv(OFK_PRR_TYPE_CLASS, null));
		p.appendChild(Core.getLink(Core.EMPTY, null, Core.getDiv(OFK_PRR_LOGO_CLASS, null)));
		p.appendChild(Core.getDiv(OFK_PRR_NAME_CLASS, null));
		return p;
	}

	private static void getPageContent(Tag top, Tag lban, Tag rban, Properties props, Connection con, long...acts) throws IOException, SQLException {
		
		
		switch (acts.length == 0 ? MAIN_PAGE : (int) acts[0]) {
		default:
			getMainPage(top, lban, rban, props, con);
		}
	}
	
	private static final String OFK_LBOX_CLASS = "ofk-lbox";
	private static final String SHORT_MESSAGES_CAPTION_KEY = "ofkpuweb.shortMessages.caption";
	private static final String SHORT_MESSAGES_FB_CAPTION_KEY = "ofkpuweb.shortMessages.fbCaption";
	private static final String SHORT_MESSAGES_LIMIT_KEY = "ofkpuweb.shortMessages.limit";
	private static final String SHORT_MESSAGES_DAYS_AGO_KEY = "ofkpuweb.shortMessages.daysAgo";
	private static final String OKF_SMBS_CLASS = "ofk-smbs";
	private static final String OFK_SMB_CLASS = "ofk-smb";
	private static final String OKF_SMB_DTE_CLASS = "ofk-smb-dte";
	private static final String OFK_SMB_CAP_CLASS = "ofk-smb-cap";
	private static final String OFK_SMB_TXT_CLASS = "ofk-smb-txt";
	private static final String OFK_SMB_HRU_CLASS = "ofk-smb-hru";
	private static final String SM_FB_URL_KEY = "ofkpuweb.shortMessages.fb.url";
	private static final String SM_FB_DATA_TABS_KEY = "ofkpuweb.shortMessages.fb.tabs";
	private static final String SM_FB_DATA_HEIGHT_KEY = "ofkpuweb.shortMessages.fb.height";
	private static final String SM_FB_DATA_SMALL_HEADER_KEY = "ofkpuweb.shortMessages.fb.smallHeader";
	private static final String SM_FB_DATA_ADAPT_CONTAINER_WIDTH_KEY = "ofkpuweb.shortMessages.fb.url.adaptContainerWidth";
	private static final String SM_FB_DATA_HIDE_COVER_KEY = "ofkpuweb.shortMessages.fb.url.hideCover";
	private static final String SM_FB_DATA_SHOW_FACEPILE_KEY = "ofkpuweb.shortMessages.fb.url.showFacepile";
	private static final String SM_FB_DATA_WIDTH_KEY = "ofkpuweb.shortMessages.fb.width";
	private static final String OFK_SMB_FB_CLASS = "ofk-smb-fb";
	private static final String OFK_LBAN_SBOX_1_CLASS = "ofk-lban-sbox-1";
	private static final String OFK_LBAN_SBOX_2_CLASS = "ofk-lban-sbox-2";
	
	
	private static void getMainPage (Tag top, Tag lban, Tag rban, Properties props, Connection con) throws IOException, SQLException {
		top.appendChild(getHeadlines(props, con));
		Tag lbox1_1 = Core.getDiv(OFK_LBAN_SBOX_1_CLASS, smallArticlesBox(con, props, Integer.parseInt(props.getProperty(SAB_ONE_CATEGORY_ID_KEY)), props.getProperty(SAB_ONE_ID_KEY)));
		Tag lbox1_2 = Core.getDiv(OFK_LBAN_SBOX_2_CLASS, smallShortMessageBox(props, con));
		Tag lbox1 = Core.getDiv(OFK_LBOX_CLASS, lbox1_1);
		lbox1.appendChild(lbox1_2);
		lban.appendChild(lbox1);
	}
	
	
	private static Tag smallShortMessageBox(Properties props, Connection con) throws SQLException {
		String[] caption = {props.getProperty(SHORT_MESSAGES_CAPTION_KEY), props.getProperty(SHORT_MESSAGES_FB_CAPTION_KEY)};
		int limit = Integer.parseInt(props.getProperty(SHORT_MESSAGES_LIMIT_KEY));
		int daysAgo = Integer.parseInt(props.getProperty(SHORT_MESSAGES_DAYS_AGO_KEY));
		Tag[] content = {getSmallMessagesBox(Utils.loadShortMessagesFromRows(props, Utils.getActualShortMessages(props, con, limit, daysAgo))), getFacebookBox(props)};
		return Ofkpuweb.getBookmarkWindow(caption, content);
	}

	private static Tag getFacebookBox(Properties props) {
		return Core.getDiv(OFK_SMB_FB_CLASS + Core.SPACE_CHAR + OFK_SBOX_HEIGHT_CLASS, getFacebookLink(props, 
				props.getProperty(SM_FB_URL_KEY),
				props.getProperty(SM_FB_DATA_TABS_KEY),
				props.getProperty(SM_FB_DATA_HEIGHT_KEY),
				props.getProperty(SM_FB_DATA_WIDTH_KEY),
				props.getProperty(SM_FB_DATA_SMALL_HEADER_KEY),
				props.getProperty(SM_FB_DATA_ADAPT_CONTAINER_WIDTH_KEY),
				props.getProperty(SM_FB_DATA_HIDE_COVER_KEY),
				props.getProperty(SM_FB_DATA_SHOW_FACEPILE_KEY)));
	}

	private static Tag getSmallMessagesBox(ShortMessage[] msgs) {
		Tag box = Core.getDiv(OKF_SMBS_CLASS + Core.SPACE_CHAR + OFK_SBOX_HEIGHT_CLASS, null);
		for(int i = 0; i < msgs.length; i ++) {
			if(i > 0) {
				box.appendChild(Core.getDiv(OFK_SMB_HRU_CLASS, null));
			}
			box.appendChild(getSmallMessageBox(msgs[i]));
		}
		return box;
	}

	private static Tag getSmallMessageBox(ShortMessage msg) {
		Tag box = Core.getDiv(OFK_SMB_CLASS, Core.getDiv(OKF_SMB_DTE_CLASS, TextTag.getInstance(msgDateFormat.format(new Date(msg.getInserted())))));
		box.appendChild(Core.getDiv(OFK_SMB_CAP_CLASS, TextTag.getInstance(msg.getCaption())));
		box.appendChild(Core.getDiv(OFK_SMB_TXT_CLASS, TextTag.getInstance(msg.getText())));
		return box;
	}

	private static final String OFK_SAB_CLASS = "ofk-sab";
	private static final String SMALL_ARTICLE_BOX_LIMIT_KEY = "ofkpuweb.smallArticleBox.limit";
	private static final String OFK_SAB_ART_CLASS = "ofk-sab-art";
	private static final String OFK_SAB_ART_HDR_CLASS = "ofk-sab-art-hdr";
	private static final String OFK_SAB_ART_LNK_CLASS = "ofk-sab-art-lnk";
	private static final String OFK_SAB_ART_BDY_CLASS = "ofk-sab-art-bdy";
	private static final String SMALL_ARTICLE_BOX_LINK_TEXT_KEY = "ofkpuweb.smallArticleBox.text";
	private static final String SAB_URL_TEXT_KEY = "ofkpuweb.smallArticleBox.urlText";
	private static final String SMALL_ARTICLE_BOX_MAX_ACTIVE_ARTICLES_KEY = "ofkpuweb.smallAticleBox.maxActiveArticles";
	private static final String OFK_SAB_HR_CLASS = "ofk-sab-hr";
	private static final String OFK_SBOX_HEIGHT_CLASS = "ofk-sbox-height";
	
	private static Tag smallArticlesBox (Connection con, Properties props, long category, String sabId) throws IOException {
		Article[] articles = null;
		try {
			TableName tableName = new TableName(new SchemaName(new Identifier(props.getProperty(Core.DB_KEY))), new Identifier(props.getProperty(Core.DB_CATEGORIES_KEY)));
			CompPred pred = new CompPred(new Object[]{new Identifier(props.getProperty(Core.DB_CATEGORIES_ID_KEY))}, new Object[]{category}, CompPred.EQUAL);
			String categoryName = (String) DBConnector.select(con, DBConnector.createSelect().addFromClause(tableName).addTableSpec(new WhereClause(pred))).get(0).get(props.getProperty(Core.DB_CATEGORIES_NAME_KEY));
			Tag small = Core.getDiv(OFK_SAB_CLASS + Core.SPACE_CHAR + OFK_SBOX_HEIGHT_CLASS, null);
			small.setAttribute(ID_ATT_NAME, sabId);
			int limit = Integer.parseInt(props.getProperty(SMALL_ARTICLE_BOX_LIMIT_KEY));
			int maxActive = Integer.parseInt(props.getProperty(SMALL_ARTICLE_BOX_MAX_ACTIVE_ARTICLES_KEY));
			articles = Utils.loadArticlesFromRows (con, Utils.getArticlesForCategory (props, con, limit, Long.parseLong(props.getProperty(DEFAULT_ARTICLE_KEY)), category), props, props.getProperty(Ofkpuweb.PAGE_ENCODING_KEY));
			for(int i = 0; i < articles.length; i ++) {
				if(i > 0) {
					small.appendChild(Core.getDiv(OFK_SAB_HR_CLASS, null));
				}
				small.appendChild(getSmallBoxArticle(props, articles[i], i < maxActive, sabId, i));
			}
			return getWindow(categoryName, String.format(props.getProperty(WIN_LOGO_KEY), props.getProperty(MAIN_LOGO_URL_KEY)), Core.EMPTY, props.getProperty(SAB_URL_TEXT_KEY), small);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new IOException (e);
		}
		finally {
			if(articles != null) {try {Utils.freePhotos(articles);} catch(SQLException e) {e.printStackTrace();}}
		}
		
	}
	
	private static final String SAB_MAX_CAPTION_LENGTH_KEY = "ofkpuweb.smallArticleBox.caption.maxLength";
	private static final String SAB_MAX_CONTENT_LENGTH_KEY = "ofkpuweb.smallArticleBox.content.maxLength";
	private static final String OFK_SAB_ART_PHO_CLASS = "ofk-sab-art-pho";
	private static final String SAB_PHOTO_KEY = "ofkpuweb.smallArticleBox.photo";
	private static final String OFK_SAB_ART_DTE_CLASS = "ofk-sab-art-dte";
	private static final String OFK_SAB_ART_LNK_2_CLASS = "ofk-sab-art-lnk-2";
	private static final String OFK_SAB_ART_BDY_INA_CLASS = "ofk-sab-art-bdy-ina";
	private static final String SAB_BUTTON_ONCLICK_KEY = "ofkpuweb.sab.buttonOnclick";
	private static final String OFK_HLN_FRM_CLASS = "ofk-hln-frm";
	
	private static Tag getSmallBoxArticle(Properties props, Article article, boolean active, String sabId, int index) {
		Table tableHdr = Table.getInstance(null, 2, 1, null);
		Integer mcl = Integer.parseInt(props.getProperty(SAB_MAX_CAPTION_LENGTH_KEY));
		String caption = article.getCaption().length() > mcl ? (article.getCaption().substring(0, article.getCaption().substring(0, mcl).lastIndexOf(Core.SPACE_CHAR)) + Core.SPACE_CHAR + Core.DOT_CHAR + Core.DOT_CHAR + Core.DOT_CHAR) : article.getCaption();
		tableHdr.setBody(new Object[]{TextTag.getInstance(caption), Core.getInstance(null, null, null, new String(new char[]{Core.PLUS_CHAR}), String.format(props.getProperty(SAB_BUTTON_ONCLICK_KEY), sabId, index, OFK_SAB_ART_CLASS, OFK_SAB_ART_BDY_CLASS, OFK_SAB_ART_BDY_INA_CLASS), active)});
		tableHdr.getRows()[0].getCells()[1].setAttribute(CLASS_ATT_NAME, OFK_SAB_ART_LNK_CLASS);
		Tag div = Core.getDiv(OFK_SAB_ART_CLASS, Core.getDiv(OFK_SAB_ART_HDR_CLASS, tableHdr));
		Table tableBdy = Table.getInstance(null, 2, 1, null);
		tableBdy.setBody(TextTag.NBSP, TextTag.getInstance(sabDateFormat.format(new Date(article.getModified()))));
		tableBdy.getRows()[0].getCells()[0].setAttribute(Core.ROWSPAN_ATT_NAME, "4");
		tableBdy.getRows()[0].getCells()[1].setAttribute(Core.CLASS_ATT_NAME, OFK_SAB_ART_DTE_CLASS);
		if(article.getPhotos().length > 0) {
			tableBdy.getRows()[0].getCells()[0].setAttribute(STYLE_ATT_NAME, String.format(props.getProperty(SAB_PHOTO_KEY), article.getPhotos()[0].getId()));
		}
		tableBdy.getRows()[0].getCells()[0].setAttribute(CLASS_ATT_NAME, OFK_SAB_ART_PHO_CLASS);
		mcl = Integer.parseInt(props.getProperty(SAB_MAX_CONTENT_LENGTH_KEY));
		String content = article.getContent().length() > mcl ? (article.getContent().substring(0, article.getContent().substring(0, mcl).lastIndexOf(Core.SPACE_CHAR)) + Core.SPACE_CHAR + Core.DOT_CHAR + Core.DOT_CHAR + Core.DOT_CHAR) : article.getContent();
		tableBdy.addRow(new Tag[]{TextTag.getInstance(content)});
		tableBdy.addRow(new Tag[]{Core.getLink(Core.EMPTY, null, TextTag.getInstance(props.getProperty(SMALL_ARTICLE_BOX_LINK_TEXT_KEY) + TOP_URL_TEXT))});
		tableBdy.addRow(new Tag[]{TextTag.NBSP});
		tableBdy.getRows()[2].getCells()[0].setClassName(OFK_SAB_ART_LNK_2_CLASS);
		div.appendChild(Core.getDiv(OFK_SAB_ART_BDY_CLASS + (!active ? (Core.SPACE_CHAR + OFK_SAB_ART_BDY_INA_CLASS) : Core.EMPTY), tableBdy));
		return div;
	}

	private static Tag getHeadlines (Properties props, Connection con) throws IOException {
	
		Article[] articles = null;
		try {
			Tag headlines = Core.getDiv(OFK_HLN_CLASS, null);
			Tag frame = Core.getDiv(OFK_HLN_FRM_CLASS + Core.SPACE_CHAR + OFK_CNT_WIDTH_CLASS, headlines);
			headlines.setAttribute(ID_ATT_NAME, OFK_HLN_ID);
			int limit = Integer.parseInt(props.getProperty(TOP_LIMIT_KEY));
			articles = Utils.loadArticlesFromRows (con, Utils.getActualArticles (props, con, limit, Long.parseLong(props.getProperty(DEFAULT_ARTICLE_KEY)), Integer.parseInt(props.getProperty(TOP_DAYS_AGE_KEY))), props, props.getProperty(Ofkpuweb.PAGE_ENCODING_KEY));
			System.out.println("Articles size = " + articles.length);
			for(int i = 0; i < articles.length; i ++) {
				headlines.appendChild(Input.getInstance(OFK_HLN_NAME + Core.DASH_CHAR + i, Core.HIDDEN_TYPE, getHeadlinesContent(articles[i], props, limit, i, articles.length)));
			}
			return frame;
		}
		catch (Exception e) {
			throw new IOException (e);
		}
		finally {
			if(articles != null) {try {Utils.freePhotos(articles);} catch(SQLException e) {e.printStackTrace();}}
		}
	}
	
	private static final String OFK_TOP4_INFO_CLASS = "ofk-top4-info";
	private static final String OKF_TOP4_DATE = "ofk-top4-date";
	private static final String OFK_TOP4_CAPTION_CLASS = "ofk-top4-caption";
	private static final int TOP_MAX_INDEX = 150;
	private static final String TOP_CONTENT_NOT_AVAILABLE_KEY = "ofkpuweb.top.contentNotAvailable";
	private static final String OFK_TOP4_FIRST_CLASS = "ofk-top4-first";
	private static final String TOP_CONTENT_URL_TEXT_KEY = "ofkpuweb.top.contentUrlText";
	private static final String OFK_TOP4_LINK_CLASS = "ofk-top4-link";
	private static final String TOP_URL_TEXT = "<span>" + Core.NBSP + Core.GT + "</span>";
	private static final String OFK_TOP4_CLASS = "ofk-top4";
	private static final String OFK_TOP4_BTNS_CLASS = "ofk-top4-btns";
	private static final String OFK_TOP_CLASS = "ofk-top";
	private static final String OFK_TOP2_CLASS = "ofk-top2";
	private static final String OFK_TOP3_CLASS = "ofk-top3";
	private static final String TOP_BUTTON_ONCLICK_KEY = "ofkpuweb.top.buttonOnclick";
	private static final String OFK_TOP4_INA_CLASS = "ofk-top4-ina";
	private static final String OFK_TOP4_BTN_CLASS = "ofk-top4-btn";
	private static final String OFK_TOP4_ACT_CLASS = "ofk-top4-act";
	private static final String	TOP_PHOTO_KEY = "ofkpuweb.top.photo";
	private static final String	TOP_MOTIV_KEY = "ofkpuweb.top.motiv";
		
	private static String getHeadlinesContent(Article article, Properties props, int limit, int id, int length) throws IOException {
		Tag top4info = Core.getDiv(OFK_TOP4_INFO_CLASS, Core.getDiv(OKF_TOP4_DATE, TextTag.getInstance(topDateFormat.format(new Date(article.getModified())))));
		top4info.appendChild(Core.getDiv(OFK_TOP4_CAPTION_CLASS, TextTag.getInstance(article.getCaption())));
		int index = article.getContent() == null ? 0 : (article.getContent().length() > TOP_MAX_INDEX ? TOP_MAX_INDEX : article.getContent().length());
		top4info.appendChild(Core.getDiv(OFK_TOP4_FIRST_CLASS, TextTag.getInstance(index == 0 ? props.getProperty (TOP_CONTENT_NOT_AVAILABLE_KEY) : (article.getContent().substring(0, index) + Core.SPACE_CHAR + Core.DOT_CHAR + Core.DOT_CHAR + Core.DOT_CHAR))));
		top4info.appendChild(Core.getDiv(OFK_TOP4_LINK_CLASS, Core.getLink(Core.EMPTY, null, props.getProperty(TOP_CONTENT_URL_TEXT_KEY) + TOP_URL_TEXT)));/* TODO url create */
		Tag top4 = Core.getDiv(OFK_TOP4_CLASS, top4info);
		Tag top4btns = Core.getDiv(OFK_TOP4_BTNS_CLASS, null);
		top4.appendChild(top4btns);
		Tag top3 = Core.getDiv(OFK_TOP3_CLASS, top4);
		top3.setAttribute(STYLE_ATT_NAME, String.format(props.getProperty(TOP_MOTIV_KEY), id + 1));
		Tag top = Core.getDiv(OFK_TOP_CLASS, Core.getDiv(OFK_TOP2_CLASS, top3));
		if(article.getPhotos().length > 0) {
			top.setAttribute(STYLE_ATT_NAME, String.format(props.getProperty(TOP_PHOTO_KEY), article.getPhotos()[0].getId()));
		}
		for(int i = 0; i < limit; i ++) {
			String className = i >= length ? OFK_TOP4_INA_CLASS : (OFK_TOP4_BTN_CLASS + (i == id ? (Core.SPACE_CHAR + OFK_TOP4_ACT_CLASS) : Core.EMPTY));
			top4btns.appendChild(Core.getDiv(className, Core.getInstance(null, null, null, Integer.toString (i + 1), String.format(props.getProperty(TOP_BUTTON_ONCLICK_KEY), i), i >= length)));
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, props.getProperty(Ofkpuweb.PAGE_ENCODING_KEY)));
		top.doFinal(writer, 5);
		writer.flush();
		writer.close();
		return Base64.getEncoder().encodeToString(out.toByteArray());
	}

	private static Tag getContentOfMenu(Properties props) {
		Tag com = Core.getDiv(OFK_MENU_CONTENT_CLASS + Core.SPACE_CHAR + OFK_MAX_WIDTH_CLASS, null);
		String item = null;
		for (int i = 1; (item = props.getProperty(String.format(MENU_ITEM_KEY, i))) != null; i ++) {
			com.appendChild(getMenu(props, i, item));
		}
		return com;
	}

	private static Tag getMenu(Properties props, int index, String item) {
		Tag menu = Core.getDiv(OFK_MENU_CLASS + Core.SPACE_CHAR + OFK_MENU_INVISIBLE_CLASS + Core.SPACE_CHAR + OFK_MAX_WIDTH_CLASS, Core.getDiv(OFK_MENU_EMPTY_CLASS, null));
		menu.appendChild(Core.getDiv(OFK_MENU_CAPTION_CLASS, Core.getLink(props.getProperty(String.format(MENU_ITEM_URL_KEY, index)), null, TextTag.getInstance(item))));
		menu.setAttribute(ID_ATT_NAME, String.format(MASK_MENU_ID, index));
		menu.setAttribute(ONMOUSEOVER_ATT_NAME, String.format(props.getProperty(MOUSE_ITEMS_ONMOUSEOVER_KEY), String.format(MASK_MENU_ID, index), OFK_MENU_CLASS + Core.SPACE_CHAR + OFK_MENU_VISIBLE_CLASS + Core.SPACE_CHAR + OFK_MAX_WIDTH_CLASS));
		menu.setAttribute(ONMOUSEOUT_ATT_NAME, String.format(props.getProperty(MOUSE_ITEMS_ONMOUSEOUT_KEY), String.format(MASK_MENU_ID, index), OFK_MENU_CLASS + Core.SPACE_CHAR + OFK_MENU_INVISIBLE_CLASS + Core.SPACE_CHAR + OFK_MAX_WIDTH_CLASS));
		Tag content = Core.getDiv(OFK_MENU_ITEMS_CLASS , null);
		boolean first = true;
		boolean _continue = true;
		int idx = 1;
		while (_continue) {
			Tag group = Core.getDiv(OFK_MENU_GROUP_CLASS, null);
			if(!first) {
				group.appendChild(Core.getDiv(OFK_MENU_GROUP_VR_CLASS, Core.getDiv(null, null)));
			}
			Tag container = Core.getDiv(OFK_MENU_CONTAINER_CLASS, null);
			int len = idx + 4;
			for(; idx < len; idx ++) {
				item = props.getProperty(String.format(MENU_ITEM_ITEM_KEY, index, idx));
				Tag menuItem = null;
				if(item != null) {
					menuItem = Core.getDiv(OFK_MENU_ITEM_ITEM_CLASS, Core.getDiv(OFK_MENU_ITEM_ITEM_PFX_CLASS, TextTag.getInstance(props.getProperty(MENU_ITEM_ITEM_PFX_KEY))));
					menuItem.appendChild(Core.getDiv(OFK_MENU_ITEM_ITEM_LINK_CLASS, Core.getLink(props.getProperty(String.format(MENU_ITEM_ITEM_URL_KEY, index, idx)), null, TextTag.getInstance(item))));
				}
				else {
					menuItem = Core.getDiv(OFK_MENU_ITEM_ITEM_CLASS, null);
					_continue = false;
				}
				container.appendChild(menuItem);
			}
			group.appendChild(container);
			first = false;
			content.appendChild(group);
		}
		menu.appendChild(content);
		return menu;
	}

	private static Tag getHeader(HttpServletRequest request, Properties props) {
		Tag hdr = Core.getDiv(OFK_MAX_WIDTH_CLASS, getMainLogo(request, props));
		hdr.appendChild(getCentralHeader(request, props));
		return hdr;
	}

	private static Tag getHeaderInfo(HttpServletRequest request, Properties props) {
		Tag info = Core.getDiv(OFK_HDR_INF_CLASS, Core.getDiv(null, request.getUserPrincipal() == null ? Core.getLink(request.getRequestURL().toString(), null, props.getProperty(LOGIN_TEXT_KEY)) : TextTag.getInstance(props.getProperty(LOGGED_IN_TEXT_KEY))));
		if(request.getUserPrincipal() != null) {
			info.appendChild(Core.getDiv(null, Core.getLink(HtmlTag.EMPTY_ATT_VALUE, null, request.getRemoteUser())));
		}
		return info;
	}

	private static Tag getMainLogo(HttpServletRequest request, Properties props) {
		return Core.getDiv(OFK_MAIN_LOGO_CLASS, Core.getLink(request.getServletContext().getContextPath(), null, Img.getInstance(props.getProperty(MAIN_LOGO_URL_KEY))));
	}

	private static Tag getCentralHeader(HttpServletRequest request, Properties props) {
		Tag centralHdr = Core.getDiv(OFK_CENTRAL_HDR_CLASS, getHeaderInfo(request, props));
		centralHdr.appendChild(getHeaderLogos(props));
		centralHdr.appendChild(getHeaderMenus(props));
		return centralHdr;
	}

	private static Tag getHeaderLogos(Properties props) {
		Tag logos = Core.getDiv(OFK_LOGOS_CLASS, null);
		Tag empty = Core.getDiv(OFK_EMPTY_LOGO_CLASS, null);
		Tag caption = Core.getDiv(OFK_CAPTION_CLASS, Core.getDiv(OFK_CAPTION_1_CLASS, TextTag.getInstance(props.getProperty(CAPTION_LINE1_KEY))));
		caption.appendChild(Core.getDiv(OFK_CLATION_2_CLASS, TextTag.getInstance(props.getProperty(CAPTION_LINE2_KEY))));
		Tag unions = Core.getDiv(OFK_UNIONS_LOGOS_CLASS, Core.getDiv(null, Core.getLink(props.getProperty(SFZ_PAGE_KEY), null, Img.getInstance(props.getProperty(SFZ_LOGO_URL_KEY)))));
		unions.appendChild(Core.getDiv(null, Core.getLink(props.getProperty(ZSFZ_PAGE_KEY), null, Img.getInstance(props.getProperty(ZSFZ_LOGO_URL_KEY)))));
		unions.appendChild(Core.getDiv(null, Core.getLink(props.getProperty(OBFZGA_PAGE_KEY), null, Img.getInstance(props.getProperty(OBFZGA_LOGO_URL_KEY)))));
		logos.appendChild(caption);
		logos.appendChild(empty);
		logos.appendChild(unions);
		return logos;
	}

	private static Tag getHeaderMenus(Properties props) {
		Tag menus = Core.getDiv(OFK_MENUS_CLASS, null);
		String over = props.getProperty(MENU_ITEM_ONMOUSEOVER_KEY);
		String out = props.getProperty(MENU_ITEM_ONMOUSEOUT_KEY);
		String item = null;
		for (int i = 1; (item = props.getProperty(String.format(MENU_ITEM_KEY, i))) != null; i ++) {
			Tag menuFrame = Core.getDiv(OFK_MENU_FRAME_CLASS, null);
			Tag menuItem = Core.getDiv(OFK_MENU_ITEM_CLASS, Core.getDiv(null, TextTag.getInstance(item)));
			
			String exitId = String.format(MASK_MENU_EXIT_ID, i);
			if(over != null) {
				menuFrame.setAttribute(ONMOUSEOVER_ATT_NAME, String.format(over, String.format(MASK_MENU_ID, i), OFK_MENU_CLASS + Core.SPACE_CHAR + OFK_MENU_VISIBLE_CLASS + Core.SPACE_CHAR + OFK_MAX_WIDTH_CLASS, exitId, props.getProperty(MENU_EXIT_BGIMAGE_KEY)));
			}
			if(out != null) {
				menuFrame.setAttribute(ONMOUSEOUT_ATT_NAME, String.format(out, String.format(MASK_MENU_ID, i), OFK_MENU_CLASS + Core.SPACE_CHAR + OFK_MENU_INVISIBLE_CLASS + Core.SPACE_CHAR + OFK_MAX_WIDTH_CLASS, exitId, Core.EMPTY));
			}
			String click = props.getProperty(String.format(MENU_ITEM_ONCLICK_KEY, i));
			if(click != null) {
				menuItem.setAttribute(ONCLICK_ATT_NAME, click);
			}
			menuFrame.appendChild(menuItem);
			Tag exit = Core.getDiv(OFK_MENU_EXIT_CLASS, null);
			exit.setAttribute(ID_ATT_NAME, exitId);
			menuFrame.appendChild(exit);
			menus.appendChild(menuFrame);
		}
		return menus;
	}
	private static final String OFK_WIN_CLASS = "ofk-win";
	private static final String OFK_WIN2_CLASS = "ofk-win2";
	private static final String OFK_WIN_HDR_CLASS = "ofk-win-hdr";
	private static final String OFK_WIN2_HDR_CLASS = "ofk-win2-hdr";
	private static final String OFK_WIN_LGO_CLASS = "ofk-win-lgo";
	private static final String WIN_LOGO_KEY = "ofkpuweb.win.logo";
	private static final String OFK_WIN_CAP_CLASS = "ofk-win-cap";
	private static final String OFK_WIN_LNK_CLASS = "ofk-win-lnk";
	private static final String OFK_WIN_CNT_CLASS = "ofk-win-cnt";
	private static final String OFK_WIN2_CNT_CLASS = "ofk-win2-cnt";
	private static final String OFK_WIN2_BMH_CLASS = "ofk-win2-bmh";
	private static final String OFK_WIN2_BMK_ACT_CLASS = "ofk-win2-bmh-act";
	private static final String OFK_BMH_ID = "ofk-bmh-";
	private static final String OFK_BMT_ID = "ofk-bmt-";
	private static final String BHM_ONCLICK = "activateBookmark(%d, \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\");";
	private static final String OFK_WIN2_BMH_EMP_CLASS = "ofk-win2-bhm-emp";
	private static final String OFK_WIN2_BMT_CLASS = "ofk-win2-bmt";
	private static final String OFK_WIN2_BMT_INA_CLASS = "ofk-win2-bmt-ina";
	private static final String OFK_SBOX_CLASS = "ofk-sbox";
	private static final String OFK_RBAN_SBOX_CLASS = "ofk-rban-sbox";
	
	private static Tag getSmallBox(Tag child) {
		return Core.getDiv(OFK_SBOX_CLASS, Core.getDiv(OFK_RBAN_SBOX_CLASS, child));
	}
	
	private static Tag getLongBox (Tag ...children) {
		Tag longBox = Core.getDiv(OFK_LBOX_CLASS, null);
		for(int i = 0; i < children.length; i ++) {
			switch (children.length) {
			case 1:
				longBox.appendChild(Core.getDiv(OFK_LBAN_LBOX_CLASS, children[i]));
				break;
			case 2:
				longBox.appendChild(Core.getDiv(OFK_LBAN_SBOX_CLASS_PFX + (i + 1), children[i]));
				break;
			default:
				longBox.appendChild(Core.getDiv(OFK_LBAN_SBOX_CLASS_PFX + children.length + Core.DOT_CHAR + (i + 1), children[i]));
			}
		}
		return longBox;
	}
	
	private static Tag getBookmarkWindow (String[] caption, Tag[] content) {
		Table table = Table.getInstance(null, caption.length + 1, 1, null);
		Tag cnt = Core.getDiv(OFK_WIN2_CNT_CLASS, null);
		for(int i = 0; i < caption.length; i ++) {
			Tag bmh = Core.getDiv(OFK_WIN2_BMH_CLASS + (i == 0 ? (Core.SPACE_CHAR + OFK_WIN2_BMK_ACT_CLASS) : Core.EMPTY), Core.getDiv(null, TextTag.getInstance(caption[i])));
			bmh.setAttribute(ID_ATT_NAME, OFK_BMH_ID + (i + 1));
			bmh.setAttribute(ONCLICK_ATT_NAME, String.format(BHM_ONCLICK, i + 1, OFK_BMH_ID, OFK_BMT_ID, OFK_WIN2_BMH_CLASS, OFK_WIN2_BMK_ACT_CLASS, OFK_WIN2_BMT_CLASS, OFK_WIN2_BMT_INA_CLASS));
			Tag bht = Core.getDiv(OFK_WIN2_BMT_CLASS + (i != 0 ? (Core.SPACE_CHAR + OFK_WIN2_BMT_INA_CLASS) : Core.EMPTY), content[i]);
			bht.setAttribute(ID_ATT_NAME, OFK_BMT_ID + (i + 1));
			cnt.appendChild(bht);
			table.setContent(bmh, i, 0);
		}
		table.getRows()[0].getCells()[content.length].setClassName(OFK_WIN2_BMH_EMP_CLASS);
		Tag win = Core.getDiv(OFK_WIN2_CLASS, Core.getDiv(OFK_WIN2_HDR_CLASS, table));
		win.appendChild(cnt);
		return win;
	}
	
	private static Tag getWindow (String caption, String logoUrl, String linkUrl, String linkText, Tag content) {
		Table table = Table.getInstance(null, 3, 1, null);
		table.setBody(new Object[]{TextTag.NBSP, TextTag.getInstance(caption), linkUrl == null ? TextTag.NBSP : Core.getLink(linkUrl, null, TextTag.getInstance(linkText + TOP_URL_TEXT))});
		Td[] cells = table.getRows()[0].getCells();
		cells[0].setAttribute(CLASS_ATT_NAME, OFK_WIN_LGO_CLASS);
		cells[0].setAttribute(STYLE_ATT_NAME, logoUrl);
		cells[1].setAttribute(CLASS_ATT_NAME, OFK_WIN_CAP_CLASS);
		cells[2].setAttribute(CLASS_ATT_NAME, OFK_WIN_LNK_CLASS);
		Tag win = Core.getDiv(OFK_WIN_CLASS, Core.getDiv(OFK_WIN_HDR_CLASS, table));
		win.appendChild(Core.getDiv(OFK_WIN_CNT_CLASS, content));
		return win;
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
}
