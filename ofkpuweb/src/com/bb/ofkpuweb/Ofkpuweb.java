package com.bb.ofkpuweb;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pabk.html.HtmlTag;
import org.pabk.html.Img;
import org.pabk.html.Tag;
import org.pabk.html.TextTag;

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
	private static final String MAIN_LOGO_URL_KEY = "ofkpuweb.mailLogo.url";
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
	private static final String MENU_ITEM_ONMOUSEOVER_KEY = "ofkpuweb.menu.item.%d.onmouseover";
	private static final String MENU_ITEM_ONMOUSEOUT_KEY = "ofkpuweb.menu.item.%d.onmouseout";
	private static final String MENU_ITEM_ONCLICK_KEY = "ofkpuweb.menu.item.%d.onclick";
	private static final String MENU_ITEM_URL_KEY = "ofkpuweb.menu.item.%d.url";
	private static final String MENU_ITEM_ITEM_KEY = "ofkpuweb.menu.item.%d.%d";
	private static final String MENU_ITEM_ITEM_URL_KEY = "ofkpuweb.menu.item.%d.%d.url";
	private static final String OFK_MENU_CONTENT_CLASS = "ofk-menu-content";
	private static final String OFK_MENU_CLASS = "ofk-menu";
	private static final String OFK_MENU_INVISIBLE_CLASS = "ofk-menu-invisible";
	private static final String MASK_MENU_ID = "ofk-menu-%d";
	private static final String OFK_MENU_CAPTION_CLASS = "ofk-menu-caption";
	private static final String OFK_MENU_ITEMS_CLASS = "ofk-menu-items";
	private static final String OFK_MENU_GROUP_CLASS = "ofk-menu-group";
	private static final String OFK_MENU_GROUP_VR_CLASS = "ofk-menu-group-vr";
	private static final String OFK_MENU_CONTAINER_CLASS = "ofk-menu-container";
	private static final String OFK_MENU_ITEM_ITEM_CLASS = "ofk-menu-item-item";
	private static final String OFK_MENU_ITEM_ITEM_PFX_CLASS = "ofk-menu-item-item-pfx";
	private static final String MENU_ITEM_ITEM_PFX_KEY = "ofkpuweb.menu.item.pfx";
	private static final String OFK_MENU_ITEM_ITEM_LINK_CLASS = "ofk-menu-item-item-link";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Properties props = getProperties();
		Tag body = Core.getDiv(OFK_BDY_CLASS, Core.getDiv(OFK_HDR_CLASS, getHeader(request, props)));
		body.appendChild(getContent(props));
		request.setAttribute(Core.PAGE_CONTENT_ATT_NAME, body);
		request.setAttribute(Core.PAGE_STYLE_ATT_NAME, props.getProperty(STYLE_URL_KEY));
		super.doGet(request, response);
	}

	private static Tag getContent(Properties props) {
		Tag hdr = Core.getDiv(OFK_CNT_CLASS, getContentOfMenu(props));
		return hdr;
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
		Tag menu = Core.getDiv(OFK_MENU_CLASS + Core.SPACE_CHAR + OFK_MENU_INVISIBLE_CLASS + Core.SPACE_CHAR + OFK_MAX_WIDTH_CLASS, Core.getDiv(OFK_MENU_CAPTION_CLASS, Core.getLink(props.getProperty(String.format(MENU_ITEM_URL_KEY, index)), null, TextTag.getInstance(item))));
		menu.setAttribute(ID_ATT_NAME, String.format(MASK_MENU_ID, index));
		Tag content = Core.getDiv(OFK_MENU_ITEMS_CLASS + Core.SPACE_CHAR + OFK_MAX_WIDTH_CLASS , null);
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
				if(item != null) {
					Tag menuItem = Core.getDiv(OFK_MENU_ITEM_ITEM_CLASS, Core.getDiv(OFK_MENU_ITEM_ITEM_PFX_CLASS, TextTag.getInstance(props.getProperty(MENU_ITEM_ITEM_PFX_KEY))));
					menuItem.appendChild(Core.getDiv(OFK_MENU_ITEM_ITEM_LINK_CLASS, Core.getLink(props.getProperty(String.format(MENU_ITEM_ITEM_URL_KEY, index, idx)), null, TextTag.getInstance(item))));
					container.appendChild(menuItem);
				}
				else {
					_continue = false;
					break;
				}
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
		String item = null;
		for (int i = 1; (item = props.getProperty(String.format(MENU_ITEM_KEY, i))) != null; i ++) {
			System.out.println(String.format(MENU_ITEM_KEY, i));
			Tag menuFrame = Core.getDiv(OFK_MENU_FRAME_CLASS, null);
			Tag menuItem = Core.getDiv(OFK_MENU_ITEM_CLASS, TextTag.getInstance(item));
			String over = props.getProperty(String.format(MENU_ITEM_ONMOUSEOVER_KEY, i));
			if(over != null) {
				menuItem.setAttribute(ONMOUSEOVER_ATT_NAME, over);
			}
			String out = props.getProperty(String.format(MENU_ITEM_ONMOUSEOUT_KEY, i));
			if(out != null) {
				menuItem.setAttribute(ONMOUSEOUT_ATT_NAME, out);
			}
			String click = props.getProperty(String.format(MENU_ITEM_ONCLICK_KEY, i));
			if(click != null) {
				menuItem.setAttribute(ONCLICK_ATT_NAME, click);
			}
			menuFrame.appendChild(menuItem);
			menus.appendChild(menuFrame);
		}
		return menus;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
}
