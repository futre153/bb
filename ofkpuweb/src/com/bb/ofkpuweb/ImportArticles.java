package com.bb.ofkpuweb;

import java.io.IOException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Date;
import java.util.Properties;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pabk.emanager.sql.sap.CompPred;
import org.pabk.emanager.sql.sap.Identifier;
import org.pabk.emanager.sql.sap.OrderClause;
import org.pabk.emanager.sql.sap.SQLSyntaxImpl;
import org.pabk.emanager.sql.sap.SchemaName;
import org.pabk.emanager.sql.sap.SortSpec;
import org.pabk.emanager.sql.sap.TableName;
import org.pabk.emanager.sql.sap.WhereClause;
import org.pabk.html.Form;
import org.pabk.html.Img;
import org.pabk.html.Input;
import org.pabk.html.Select;
import org.pabk.html.Span;
import org.pabk.html.Table;
import org.pabk.html.Tag;
import org.pabk.html.TextTag;
import org.pabk.html.Textarea;
import org.pabk.web.db.DBConnector;
import org.pabk.web.db.Rows;

import com.bb.commons.Article;
import com.bb.commons.TempArticle;
import com.bb.http.MultipartContent;

@WebServlet("/import/articles")
public class ImportArticles extends Core {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String IA_FNT_CLASS = "ia-fnt";
	private static final String STYLE_URL_KEY = "ia.style.url";
	private static final String SCRIPT_SOURCE_KEY = "ia.script.source";
	private static final String IA_DATEFORMAT_KEY = "ia.dateformat";
	private static final String IA_FORM_ATT_NAME = "ia.form";
	private static final char IA_INPUT_VALUE_SEPARATOR = Core.TILDE_CHAR;
	private static final String IA_PUBLISHED_ARTICLE_PARAM_NAME = "arp";
	private static final String IA_TEMP_ARTICLE_PARAM_NAME = "art";
	private static final String IA_CNT_ART_TXT_FLN_CLASS = "ia-cnt-art-txt-fln";
	private static final String IA_CNT_ART_TXT_LNE_CLASS = "ia-cnt-art-txt-lne";
	private static final String CONTENT_OPEN_IMAGE_URL_KEY = "ia.content.open.imageUrl";
	private static final String CONTENT_DELETE_IMAGE_URL_KEY = "ia.content.delete.imageUrl";
	private static final String HOMEPAGE_URL = "ia.homepage";
		
	private static SimpleDateFormat iaDateFormat;
    
 /**
  * @see Core#Core()
  */
 public ImportArticles() {
     super();
     // TODO Auto-generated constructor stub
 }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		System.out.println(super.getProperties().getProperty(IA_DATEFORMAT_KEY));
		System.out.println(Core.getLocale());
		iaDateFormat = new SimpleDateFormat(super.getProperties().getProperty(IA_DATEFORMAT_KEY), Core.getLocale());
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Object form = request.getSession().getAttribute(IA_FORM_ATT_NAME);
		String[] tags = form == null ? newArticle(new String[PARAM_LENGTH], request, getProperties()) : ((String[]) form);
		request.getSession().removeAttribute(IA_FORM_ATT_NAME);
		doPage(request, response, tags);
		super.doGet(request, response);
	}
	
	/**
	 * @throws  
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String[] tags = getParameters(request);
		actionProcessor(tags, request, getProperties());
		doPage(request, response, tags);
		super.doGet(request, response);
	}
	
	private static final String ONLOAD_FN = "init("
			+ "\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", "
			+ "\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", "
			+ "\"%s\" ,\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", "
			+ "\"%s\" ,\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", "
			+ "\"%s\" ,\"%s\", \"%s\", \"%s\", \"%s\", %d, "
			+ "\"%s\" ,\"%s\", %s, \"%s\" ,\"%s\", %s, \"%s\", \"%s\", \"%s\", "
			+ "\"%s\" ,\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", "
			+ "\"%s\" ,\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", "
			+ "\"%s\" ,\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", "
			+ "\"%s\" ,\"%s\", \"%s\", \"%s\");";
	
	private void doPage(HttpServletRequest request, HttpServletResponse response, String[] params) throws IOException {
		Properties props = getProperties();
		Tag body = Core.getDiv(IA_FNT_CLASS, null);
		getPage (request, props, body, params);
		request.setAttribute(Core.PAGE_CONTENT_ATT_NAME, body);
		request.setAttribute(Core.PAGE_STYLE_ATT_NAME, props.getProperty(STYLE_URL_KEY));
		request.setAttribute(Core.PAGE_SCRIPT_SOURCE_ATT_NAME, props.getProperty(SCRIPT_SOURCE_KEY));
		request.setAttribute(Core.PAGE_ONLOAD_ATT_NAME, String.format(ONLOAD_FN,
				IA_DIA_FRM_CLASS, IA_DIA_CAP_ID, IA_DIA_VAL_ID, IA_DIA_TXT_FRM_CLASS, IA_DIA_TXT_VAL_ID, IA_CNT_ART_CAP_CLASS,
				IA_CNT_ART_INF_DCR_CLASS, IA_CNT_ART_INF_AUT_CLASS, IA_CNT_ART_TXT_CLASS, IA_CNT_ART_TXT_FLN_CLASS, IA_CNT_ART_TXT_LNE_CLASS, props.getProperty(CONTENT_IMAGE_URL_KEY),
				ACP_PARAM, AAU_PARAM, ADC_PARAM, ALM_PARAM, APU_PARAM, AID_PARAM,
				API_PARAM, ATE_PARAM, AAC_PARAM, ATI_PARAM, ASI_PARAM, IA_PIC_FRM_CLASS,
				IA_PIC_BDY_CLASS, IA_PIC_GLS_CLASS, IA_PIC_AGA_CLASS, IA_PIC_NGA_CLASS, props.getProperty(CONTENT_DELETE_PICTURE_TEXT_KEY), 2,
				IA_ERR_FRM_CLASS, IA_ERR_MSG_CLASS, params[0] == null || params[0].length() == 0 ? null : (Core.DOUBLE_QUOTE_CHAR + params[0] + Core.DOUBLE_QUOTE_CHAR), IA_ADV_FRM_CLASS, IA_ADV_MSG_CLASS, params[13] == null || params[13].length() == 0 ? null : (Core.DOUBLE_QUOTE_CHAR + params[13] + Core.DOUBLE_QUOTE_CHAR), IA_CON_FRM_CLASS, IA_CON_MSG_CLASS, IA_CON_BUT_CLASS,
				IA_ART_FRM_CLASS, IA_TEMP_ARTICLE_PARAM_NAME, CONTENT_LOAD_TEMP_ARTICLE_FN, IA_ARP_FRM_CLASS, IA_PUBLISHED_ARTICLE_PARAM_NAME, CONTENT_LOAD_PUBLISHED_ARTICLE_2_FN,
				IA_ART_DEL_FRM_CLASS, IA_TEMP_ARTICLE_PARAM_NAME, CONTENT_REMOVE_TEMP_ARTICLE_FN, IA_ARP_DEL_FRM_CLASS, IA_PUBLISHED_ARTICLE_PARAM_NAME, CONTENT_REMOVE_PUBLISHED_ARTICLE_2_FN,
				IA_INPUT_VALUE_SEPARATOR, props.getProperty(CONTENT_OPEN_IMAGE_URL_KEY), props.getProperty(CONTENT_DELETE_IMAGE_URL_KEY), ACA_NEW_ARTICLE, ACA_LOAD_TEMP_ARTICLE, ACA_LOAD_PUBLISHED_ARTICLE,
				ACA_DELETE_TEMP_ARTICLE, ACA_DELETE_PUBLISHED_ARTICLE, ACA_SAVE_ARTICLE, props.getProperty(HOMEPAGE_URL)));
	}
	
	private static final Object CONTENT_REMOVE_TEMP_ARTICLE_FN = "delTempArticle(~)";
	private static final Object CONTENT_REMOVE_PUBLISHED_ARTICLE_2_FN = "delPublishedArticle(~)";
	private static final Object CONTENT_LOAD_TEMP_ARTICLE_FN = "loadTempArticle(~)";
	private static final Object CONTENT_LOAD_PUBLISHED_ARTICLE_2_FN = "loadPublishedArticle(~)";
	private static final String CONTENT_DELETE_PICTURE_TEXT_KEY = "ia.content.deletePicture.text";
	private static final Object IA_PIC_AGA_CLASS = "ia-pic-aga";
	private static final Object IA_PIC_NGA_CLASS = "ia-pic-nga";
	private static final String CONTENT_IMAGE_URL_KEY = "ia.content.imageUrl";
	private static final String IA_OPA_CLASS = "ia-opa";
	private static final String IA_DIA_CLASS = "ia-dia";
	private static final String IA_DIA_2_CLASS = "ia-dia-2";
	private static final String DIALOG_TEXT_CAPTION_KEY = "ia.dialog.text.caption";
	private static final int DIALOG_TEXT_COLS = 80;
	private static final int DIALOG_TEXT_ROWS = 50;
	private static final String IA_DIA_TXT_VAL_ID = "ia-dia-txt-val";
	private static final String DIALOG_TEXT_BUTTON_TEXT_KEY = "ia.dialog.text.button.text";
	private static final String DIALOG_TEXT_RETURN_FN = "returnTextDialogValue();";
	private static final String IA_DIG_BUT_CLASS = "ia-dig-but";
	private static final String IA_DIA_TXT_FRM_CLASS = "ia-dia-txt-frm";
	private static final String DIALOG_CAPTION_CAPTION_KEY = "ia.dialog.caption.caption";
	private static final int DIALOG_CAPTION_COLS = 50;
	private static final int DIALOG_CAPTION_ROWS = 4;
	private static final int DIALOG_CAPTION_MAX = 128;
	private static final int DIALOG_AUTHOR_MAX = 32;
	private static final String IA_DIA_VAL_ID = "ia-dia-val";
	private static final String DIALOG_CAPTION_BUTTON_TEXT_KEY = "ia.dialog.caption.button.text";
	private static final String DIALOG_CAPTION_RETURN_FN = "returnDialogValue();";
	private static final String IA_DIA_FRM_CLASS = "ia-dia-frm";
	private static final String DIALOG_ERROR_CAPTION_KEY = "ia.dialog.error.caption";
	private static final String DIALOG_ERROR_BUTTON_TEXT_KEY = "ia.dialog.error.button.text";
	private static final String DIALOG_ERROR_RETURN_FN = "returnError();";
	private static final String IA_DIA_CAP_ID = "ia-dia-cap";
	private static final String IA_ERR_MSG_CLASS = "ia-err-msg";
	private static final String IA_ERR_BUT_CLASS = "ia-err-but";
	private static final String IA_ERR_CAP_CLASS = "ia-err-cap";
	private static final String IA_ERR_BDY_CLASS = "ia-err-bdy";
	private static final String IA_ERR_2_CLASS = "ia-err-2";
	private static final String IA_ERR_FRM_CLASS = "ia-err-frm";
	private static final String DIALOG_ADVICE_CAPTION_KEY = "ia.dialog.advice.caption";
	private static final String DIALOG_ADVICE_BUTTON_TEXT_KEY = "ia.dialog.advice.button.text";
	private static final String DIALOG_ADVICE_RETURN_FN = "returnAdvice();";
	private static final String IA_ADV_MSG_CLASS = "ia-adv-msg";
	private static final String IA_ADV_BUT_CLASS = "ia-adv-but";
	private static final String IA_ADV_CAP_CLASS = "ia-adv-cap";
	private static final String IA_ADV_BDY_CLASS = "ia-adv-bdy";
	private static final String IA_ADV_2_CLASS = "ia-adv-2";
	private static final String IA_ADV_FRM_CLASS = "ia-adv-frm";
	private static final String DIALOG_CONFIRM_CAPTION_KEY = "ia.dialog.confirm.caption";
	private static final String DIALOG_CONFIRM_BUTTON_CONFIRM_TEXT_KEY = "ia.dialog.confirm.buttonConfirm.text";
	private static final String DIALOG_CONFIRM_BUTTON_CANCEL_TEXT_KEY = "ia.dialog.confirm.buttonCancel.text";
	private static final String DIALOG_CONFIRM_CONFIRM_RETURN_FN = "returnConfirm();";
	private static final String DIALOG_CONFIRM_CANCEL_RETURN_FN = "returnNotConfirm();";
	private static final String IA_CON_CAP_CLASS = "ia-con-cap";
	private static final String IA_CON_MSG_CLASS = "ia-con-msg";
	private static final String IA_CON_BUT_CLASS = "ia-con-but";
	private static final String IA_CNN_BUT_CLASS = "ia-cnn-but";
	private static final String IA_CON_2_CLASS = "ia-con-2";
	private static final String IA_CON_FRM_CLASS = "ia-con-frm";
	private static final String IA_DIA_ART_1_CLASS = "ia-dia-art-1";
	private static final String IA_ART_CAP_CLASS = "ia-art-cap";
	private static final String DIALOG_ARTICLE_ID_KEY = "ia.dialog.article.id";
	private static final String DIALOG_ARTICLE_CAPTION_KEY = "ia.dialog.article.caption";
	private static final String DIALOG_ARTICLE_AUTHOR_KEY = "ia.dialog.article.author";
	private static final String DIALOG_ARTICLE_LAST_CHANGE_KEY = "ia.dialog.article.lastChange";
	private static final String IA_DIA_ART_2_CLASS = "ia-dia-art-2";
	private static final String DIALOG_ARTICLE_BUTTON_TEXT_KEY = "ia.dialog.article.button.text";
	private static final String IA_ART_CALCEL_CLASS = "ia-art-cancel";
	private static final String IA_ART_DEL_FRM_CLASS = "ia-art-del-frm";
	private static final String DIALOG_ARTICLE_TMP_DELETE_CAPTION_KEY = "ia.dialog.articleTmp.delete.caption";
	private static final String DIALOG_ARTICLE_TMP_DELETE_FN = "cancelDelTemp();";
	private static final String DIALOG_ARTICLE_PUB_DELETE_CAPTION_KEY = "ia.dialog.articlePub.delete.caption";
	private static final String DIALOG_ARTICLE_PUB_DELETE_FN = "cancelDelPublished();";
	private static final String IA_ARP_DEL_FRM_CLASS = "ia-arp-del-frm";
	private static final String DIALOG_ARTICLE_TMP_CAPTION_KEY = "ia.dialog.articleTmp.caption";
	private static final String DIALOG_ARTICLE_PUB_CAPTION_KEY = "ia.dialog.articlePub.caption";
	private static final String DIALOG_ARTICLE_TMP_FN = "cancelLoadTemp();";
	private static final String DIALOG_ARTICLE_PUB_FN = "cancelLoadPublished();";
	private static final String IA_ART_FRM_CLASS = "ia-art-frm";
	private static final String IA_ARP_FRM_CLASS = "ia-arp-frm";
	private static final String DIALOG_IMAGE_CAPTION_KEY = "ia.dialog.image.caption";
	private static final String DIALOG_IMAGE_LOW_GALLERY_FN = "setLowGallery();";
	private static final String DIALOG_IMAGE_HIGH_GALLERY_FN = "setHighGallery();";
	private static final String IA_PIC_GLS_CLASS = "ia-pic-gls";
	private static final String IA_PIC_BDY_CLASS = "ia-pic-bdy";
	private static final String IA_PIC_FRM_CLASS = "ia-pic-frm";
	private static final String IA_FRM_CLASS = "ia-frm";
	private static final String IA_FRM_2_CLASS = "ia-frm-2";
	private static final String FORM_METHOD_KEY = "ia.form.method";
	private static final String FORM_ENCTYPE_KEY = "ia.form.enctype";
	private static final String EDITOR_CAPTION_KEY = "ia.editor.caption";
	private static final String IA_HDR_FRM_CLASS = "ia-hdr-frm";
	private static final String IA_HDR_FRM_2_CLASS = "ia-hdr-frm-2";
	private static final String IA_HDR_COL_1_CLASS = "ia-hdr-col-1";
	private static final String IA_HDR_COL_2_CLASS = "ia-hdr-col-2";
	private static final String IA_HDR_COL_3_CLASS = "ia-hdr-col-3";
	private static final String EDITOR_HEADER_CAPTION_KEY = "ia.editor.header.caption";
	private static final String EDITOR_HEADER_ICON_SOURCE_KEY = "ia.editor.header.icon.source";
	private static final String EDITOR_HEADER_EDIT_CAPTION_FN = "editCaption(\"%s\", \"%s\", %d, \"%s\");";
	private static final String IA_HDR_ACP_ID = "ia-hdr-acp";
	private static final String EDITOR_HEADER_CAPTION_TEXT_KEY = "ia.editor.header.caption.text";
	private static final String TAB_FLN_CLASS = "tab-fln";
	private static final String TAB_OLN_CLASS = "tab-oln";
	private static final String EDITOR_HEADER_AUTHOR_KEY = "ia.editor.header.author";
	private static final String EDITOR_HEADER_EDIT_AUTHOR_FN = "editAuthor(\"%s\", \"%s\", %d, \"%s\");";
	private static final String IA_HDR_AAU_ID = "ia-hdr-aau";
	private static final String EDITOR_HEADER_AUTHOR_TEXT_KEY = "ia.editor.header.author.text";
	private static final String TAB_LN_CLASS = "tab-ln";
	private static final String TAB_ELN_CLASS = "tab-eln";
	private static final String EDITOR_HEADER_CREATION_KEY = "ia.editor.header.creation";
	private static final String EDITOR_HEADER_MODIFICATION_KEY = "ia.editor.header.modification";
	private static final String EDITOR_HEADER_CATEGORY_KEY = "ia.editor.header.category";
	private static final String EDITOR_HEADER_PUBLISHED_KEY = "ia.editor.header.published";
	private static final String IA_CNT_FRM_CLASS = "ia-cnt-frm";
	private static final String IA_CNT_LBAN_CLASS = "ia-cnt-lban";
	private static final String IA_CNT_ART_FRM_CLASS = "ia-cnt-art-frm";
	private static final String IA_CNT_ART_CAP_CLASS = "ia-cnt-art-cap";
	private static final String IA_CNT_ART_INF_DCR_CLASS = "ia-cnt-art-inf-dcr";
	private static final String IA_CNT_ART_INF_AUT_CLASS = "ia-cnt-art-inf-aut";
	private static final String IA_CNT_ART_INF_CLASS = "ia-cnt-art-inf";
	private static final String CONTENT_ARTICLE_IFNO_AUTHOR_KEY = "ia.content.article.info.author";
	private static final String IA_CNT_ART_TXT_CLASS = "ia-cnt-art-txt";
	private static final String IA_CNT_RBAN_CLASS = "ia-cnt-rban";
	private static final String CONTENT_NEW_ARTICLE_BUTTON_TEXT_KEY = "ia.content.newArticle.buttonText";
	private static final String CONTENT_LOAD_ARTICLE_BUTTON_TEXT_KEY = "ia.content.loadArticle.buttonText";
	private static final String CONTENT_LOAD_PUBLISHED_ARTICLE_BUTTON_TEXT_KEY = "ia.content.loadPublishedArticle.buttonText";
	private static final String CONTENT_REMOVE_ARTICLE_BUTTON_TEXT_KEY = "ia.content.removeArticle.buttonText";
	private static final String CONTENT_REMOVE_PUBLISHED_ARTICLE_BUTTON_TEXT_KEY = "ia.content.removePublishedArticle.buttonText";
	private static final String CONTENT_SAVE_ARTICLE_BUTTON_TEXT_KEY = "ia.content.saveArticle.buttonText";
	private static final String CONTENT_EDIT_TEXT_BUTTON_TEXT_KEY = "ia.content.editText.buttonText";
	private static final String CONTENT_ADD_PICTURE_BUTTON_TEXT_KEY = "ia.content.addPicture.buttonText";
	private static final String CONTENT_REMOVE_PICTURES_BUTTON_TEXT_KEY = "ia.content.removePictures.buttonText";
	private static final String CONTENT_PUBLISH_ARTICLE_BUTTON_TEXT_KEY = "ia.content.publishArticle.buttonText";
	private static final String CONTENT_RETURN_BUTTON_TEXT_KEY = "ia.content.return.buttonText";
	private static final String CONTENT_NEW_ARTICLE_FN = "newArticle";
	private static final String CONTENT_LOAD_ARTICLE_FN = "loadArticle";
	private static final String CONTENT_LOAD_PUBLISHED_ARTICLE_FN = "loadPubArticle";
	private static final String CONTENT_REMOVE_ARTICLE_FN = "delArticle";
	private static final String CONTENT_REMOVE_PUBLISHED_ARTICLE_FN = "delPubArticle";
	private static final String CONTENT_RETURN_FN = "closeApp";
	private static final String CONTENT_CONFIRM_FN = "setConfirm (%s, \"%s\", \"%s\");";
	private static final String CONTENT_CONFIRM_NEW_ARTICLE_BUTTON_TEXT_KEY = "ia.content.confirm.newArticle.buttonText";
	private static final String CONTENT_CONFIRM_LOAD_ARTICLE_BUTTON_TEXT_KEY = "ia.content.confirm.loadArticle.buttonText";
	private static final String CONTENT_CONFIRM_LOAD_PUBLISHED_ARTICLE_BUTTON_TEXT_KEY = "ia.content.confirm.loadPublishedArticle.buttonText";
	private static final String CONTENT_CONFIRM_REMOVE_ARTICLE_BUTTON_TEXT_KEY = "ia.content.confirm.removeArticle.buttonText";
	private static final String CONTENT_CONFIRM_REMOVE_PUBLISHED_ARTICLE_BUTTON_TEXT_KEY = "ia.content.confirm.removePublishedArticle.buttonText";
	private static final String CONTENT_CONFIRM_RETURN_BUTTON_TEXT_KEY = "ia.content.confirm.return.buttonText";
	private static final String CONTENT_CONFIRM_NEW_ARTICLE_MESSAGE_KEY = "ia.content.confirm.newArticle.message";
	private static final String CONTENT_CONFIRM_LOAD_ARTICLE_MESSAGE_KEY = "ia.content.confirm.loadArticle.message";
	private static final String CONTENT_CONFIRM_LOAD_PUBLISHED_ARTICLE_MESSAGE_KEY = "ia.content.confirm.loadPublishedArticle.message";
	private static final String CONTENT_CONFIRM_REMOVE_ARTICLE_MESSAGE_KEY = "ia.content.confirm.removeArticle.message";
	private static final String CONTENT_CONFIRM_REMOVE_PUBLISHED_ARTICLE_MESSAGE_KEY = "ia.content.confirm.removePublishedArticle.message";
	private static final String CONTENT_CONFIRM_RETURN_MESSAGE_KEY = "ia.content.confirm.return.message";
	private static final String CONTENT_SAVE_ARTICLE_FN = "saveArticle();";
	private static final String CONTENT_EDIT_TEXT_FN = "editText();";
	private static final String CONTENT_ADD_PICTURE_FN = "addPicture(0);";
	private static final String CONTENT_REMOVE_PICTURES_FN = "removePicture(0);";
	private static final String EDITOR_HEADER_NO_PUBLISHED_KEY = "ia.editor.noPublished";
	private static final String CONTENT_PUBLISH_ARTICLE_FN = "publishArticle();";
	private static final String IA_FORM_ID = "ia-form";
	
	private static Tag getRightContent(Properties props) {
		Tag rban = Core.getDiv(IA_CNT_RBAN_CLASS, Core.getDiv(null, Core.getInstance(null, null, TYPE_BUTTON, props.getProperty(CONTENT_NEW_ARTICLE_BUTTON_TEXT_KEY), String.format(CONTENT_CONFIRM_FN, CONTENT_NEW_ARTICLE_FN, props.getProperty(CONTENT_CONFIRM_NEW_ARTICLE_BUTTON_TEXT_KEY), props.getProperty(CONTENT_CONFIRM_NEW_ARTICLE_MESSAGE_KEY)), false)));
		rban.appendChild(Core.getDiv(null, Core.getInstance(null, null, TYPE_BUTTON, props.getProperty(CONTENT_LOAD_ARTICLE_BUTTON_TEXT_KEY), String.format(CONTENT_CONFIRM_FN, CONTENT_LOAD_ARTICLE_FN, props.getProperty(CONTENT_CONFIRM_LOAD_ARTICLE_BUTTON_TEXT_KEY), props.getProperty(CONTENT_CONFIRM_LOAD_ARTICLE_MESSAGE_KEY)), false)));
		rban.appendChild(Core.getDiv(null, Core.getInstance(null, null, TYPE_BUTTON, props.getProperty(CONTENT_LOAD_PUBLISHED_ARTICLE_BUTTON_TEXT_KEY), String.format(CONTENT_CONFIRM_FN, CONTENT_LOAD_PUBLISHED_ARTICLE_FN, props.getProperty(CONTENT_CONFIRM_LOAD_PUBLISHED_ARTICLE_BUTTON_TEXT_KEY), props.getProperty(CONTENT_CONFIRM_LOAD_PUBLISHED_ARTICLE_MESSAGE_KEY)), false)));
		rban.appendChild(Core.getDiv(null, Core.getInstance(null, null, TYPE_BUTTON, props.getProperty(CONTENT_REMOVE_ARTICLE_BUTTON_TEXT_KEY), String.format(CONTENT_CONFIRM_FN, CONTENT_REMOVE_ARTICLE_FN, props.getProperty(CONTENT_CONFIRM_REMOVE_ARTICLE_BUTTON_TEXT_KEY), props.getProperty(CONTENT_CONFIRM_REMOVE_ARTICLE_MESSAGE_KEY)), false)));
		rban.appendChild(Core.getDiv(null, Core.getInstance(null, null, TYPE_BUTTON, props.getProperty(CONTENT_REMOVE_PUBLISHED_ARTICLE_BUTTON_TEXT_KEY), String.format(CONTENT_CONFIRM_FN, CONTENT_REMOVE_PUBLISHED_ARTICLE_FN, props.getProperty(CONTENT_CONFIRM_REMOVE_PUBLISHED_ARTICLE_BUTTON_TEXT_KEY), props.getProperty(CONTENT_CONFIRM_REMOVE_PUBLISHED_ARTICLE_MESSAGE_KEY)), false)));
		rban.appendChild(Core.getDiv(null, Core.getInstance(null, null, TYPE_BUTTON, props.getProperty(CONTENT_SAVE_ARTICLE_BUTTON_TEXT_KEY), CONTENT_SAVE_ARTICLE_FN, false)));
		rban.appendChild(Core.getDiv(null, Core.getInstance(null, null, TYPE_BUTTON, props.getProperty(CONTENT_EDIT_TEXT_BUTTON_TEXT_KEY), CONTENT_EDIT_TEXT_FN, false)));
		rban.appendChild(Core.getDiv(null, Core.getInstance(null, null, TYPE_BUTTON, props.getProperty(CONTENT_ADD_PICTURE_BUTTON_TEXT_KEY), CONTENT_ADD_PICTURE_FN, false)));
		rban.appendChild(Core.getDiv(null, Core.getInstance(null, null, TYPE_BUTTON, props.getProperty(CONTENT_REMOVE_PICTURES_BUTTON_TEXT_KEY), CONTENT_REMOVE_PICTURES_FN, false)));
		rban.appendChild(Core.getDiv(null, Core.getInstance(null, null, TYPE_BUTTON, props.getProperty(CONTENT_PUBLISH_ARTICLE_BUTTON_TEXT_KEY), CONTENT_PUBLISH_ARTICLE_FN, false)));
		rban.appendChild(Core.getDiv(null, Core.getInstance(null, null, TYPE_BUTTON, props.getProperty(CONTENT_RETURN_BUTTON_TEXT_KEY), String.format(CONTENT_CONFIRM_FN, CONTENT_RETURN_FN, props.getProperty(CONTENT_CONFIRM_RETURN_BUTTON_TEXT_KEY), props.getProperty(CONTENT_CONFIRM_RETURN_MESSAGE_KEY)), false)));
		return rban;
	}
	
	private static Tag getLeftContent (Properties props) {
		Tag lban = Core.getDiv(IA_CNT_ART_FRM_CLASS, Core.getDiv(IA_CNT_ART_CAP_CLASS, null));
		Tag dcr = Span.getInstance();
		dcr.setAttribute(CLASS_ATT_NAME, IA_CNT_ART_INF_DCR_CLASS);
		Tag aut = Span.getInstance();
		aut.setAttribute(CLASS_ATT_NAME, IA_CNT_ART_INF_AUT_CLASS);
		lban.appendChild(Core.getDiv(IA_CNT_ART_INF_CLASS, TextTag.getInstance(dcr + Core.NBSP_ENTITY + Core.VERTICAL_BAR_CHAR + Core.NBSP_ENTITY + props.getProperty(CONTENT_ARTICLE_IFNO_AUTHOR_KEY) + Core.NBSP_ENTITY + aut)));
		lban.appendChild(Core.getDiv(IA_CNT_ART_TXT_CLASS, null));
		return lban;
	}
	
	private static Tag getImportArticlesForm(Properties props, String[] params, Connection con) throws IOException, SQLException {
		Tag form = Form.getInstance(null, props.getProperty(FORM_METHOD_KEY));
		form.setAttribute(ENCTYPE_ATT_NAME, props.getProperty(FORM_ENCTYPE_KEY));
		form.setAttribute(ID_ATT_NAME, IA_FORM_ID);
		Table header = Table.getInstance(props.getProperty(EDITOR_CAPTION_KEY), 3, 1, null);
		header.setColgroup(IA_HDR_COL_1_CLASS, IA_HDR_COL_2_CLASS, IA_HDR_COL_3_CLASS);
		header.setBody(TextTag.getInstanceNBSP(props.getProperty(EDITOR_HEADER_CAPTION_KEY)), params[1] == null ? null : TextTag.getInstance(params[1]), getHeaderIcon(props.getProperty(EDITOR_HEADER_ICON_SOURCE_KEY), String.format(EDITOR_HEADER_EDIT_CAPTION_FN, IA_HDR_ACP_ID, ACP_PARAM, DIALOG_CAPTION_MAX, props.getProperty(EDITOR_HEADER_CAPTION_TEXT_KEY))));
		header.getRows()[0].getCells()[1].setId(IA_HDR_ACP_ID);
		header.getRows()[0].setClassName(TAB_FLN_CLASS + Core.SPACE_CHAR + TAB_OLN_CLASS);
		header.addRow(new Tag[]{TextTag.getInstanceNBSP(props.getProperty(EDITOR_HEADER_AUTHOR_KEY)), params[1] == null ? null : TextTag.getInstance(params[2]), getHeaderIcon(props.getProperty(EDITOR_HEADER_ICON_SOURCE_KEY), String.format(EDITOR_HEADER_EDIT_AUTHOR_FN, IA_HDR_AAU_ID, AAU_PARAM, DIALOG_AUTHOR_MAX, props.getProperty(EDITOR_HEADER_AUTHOR_TEXT_KEY)))});
		header.getRows()[1].getCells()[1].setId(IA_HDR_AAU_ID);
		header.getRows()[1].setClassName(TAB_LN_CLASS + Core.SPACE_CHAR + TAB_ELN_CLASS);
		header.addRow(new Tag[]{TextTag.getInstanceNBSP(props.getProperty(EDITOR_HEADER_CREATION_KEY)), TextTag.getInstance(params[3])});
		header.getRows()[2].getCells()[1].setAttribute(COLSPAN_ATT_NAME, "2");
		header.getRows()[2].setClassName(TAB_LN_CLASS + Core.SPACE_CHAR + TAB_OLN_CLASS);
		header.addRow(new Tag[]{TextTag.getInstanceNBSP(props.getProperty(EDITOR_HEADER_MODIFICATION_KEY)), TextTag.getInstance(params[4])});
		header.getRows()[3].getCells()[1].setAttribute(COLSPAN_ATT_NAME, "2");
		header.getRows()[3].setClassName(TAB_LN_CLASS + Core.SPACE_CHAR + TAB_ELN_CLASS);
		header.addRow(new Tag[]{TextTag.getInstanceNBSP(props.getProperty(EDITOR_HEADER_CATEGORY_KEY)), getHeaderSelect(con, props, params[12])});
		header.getRows()[4].getCells()[1].setAttribute(COLSPAN_ATT_NAME, "2");
		header.getRows()[4].setClassName(TAB_LN_CLASS + Core.SPACE_CHAR + TAB_OLN_CLASS);
		header.addRow(new Tag[]{TextTag.getInstanceNBSP(props.getProperty(EDITOR_HEADER_PUBLISHED_KEY)), TextTag.getInstance(params[5] == null || params[5].length() == 0 ? props.getProperty(EDITOR_HEADER_NO_PUBLISHED_KEY) : params[5])});
		header.getRows()[5].getCells()[1].setAttribute(COLSPAN_ATT_NAME, "2");
		header.getRows()[5].setClassName(TAB_LN_CLASS + Core.SPACE_CHAR + TAB_ELN_CLASS);
		form.appendChild(Input.getInstance(ACP_PARAM, HIDDEN_TYPE, params[1]));
		form.appendChild(Input.getInstance(AAU_PARAM, HIDDEN_TYPE, params[2]));
		form.appendChild(Input.getInstance(ADC_PARAM, HIDDEN_TYPE, params[3]));
		form.appendChild(Input.getInstance(ALM_PARAM, HIDDEN_TYPE, params[4]));
		form.appendChild(Input.getInstance(APU_PARAM, HIDDEN_TYPE, params[5]));
		form.appendChild(Input.getInstance(AID_PARAM, HIDDEN_TYPE, params[6]));
		form.appendChild(Input.getInstance(API_PARAM, HIDDEN_TYPE, params[7]));
		form.appendChild(Input.getInstance(ATE_PARAM, HIDDEN_TYPE, params[8]));
		form.appendChild(Input.getInstance(AAC_PARAM, HIDDEN_TYPE, params[9]));
		form.appendChild(Input.getInstance(ATI_PARAM, HIDDEN_TYPE, params[10]));
		form.appendChild(Input.getInstance(ASI_PARAM, HIDDEN_TYPE, params[11]));
		//form.appendChild(Input.getInstance(ACA_PARAM, HIDDEN_TYPE, params[12]));
		form.appendChild(Input.getInstance(ASE_PARAM, HIDDEN_TYPE, params[14]));
		form.appendChild(Core.getDiv(IA_HDR_FRM_CLASS, Core.getDiv(IA_HDR_FRM_2_CLASS, header)));
		Tag content = Core.getDiv(IA_CNT_FRM_CLASS, Core.getDiv(IA_CNT_LBAN_CLASS, getLeftContent(props)));
		content.appendChild(getRightContent(props));
		form.appendChild(content);
		return form;
	}
	
	private static Tag getHeaderSelect(Connection con, Properties props, String aca) throws SQLException {
		Rows rows = DBConnector.select(con, DBConnector.createSelect().addFromClause(new TableName(new SchemaName(new Identifier(props.getProperty(Core.DB_KEY))), new Identifier (props.getProperty(Core.DB_CATEGORIES_KEY)))).addSelectSpec(new OrderClause(new SortSpec(new Identifier(props.getProperty(Core.DB_CATEGORIES_ID_KEY))))));
		String[] options = new String[rows.size()];
		int selected = -1;
		for(int i = 0; i < rows.size(); i ++) {
			options[i] = (String) rows.get(i).get(props.getProperty(Core.DB_CATEGORIES_NAME_KEY));
			selected = aca != null && aca.equals(options[i]) && selected < 0 ? i : selected; 
		}
		Tag select = Select.getInstance(ACA_PARAM, options, options, selected < 0 ? 0 : selected);
		select.setAttribute(CLASS_ATT_NAME, IA_FNT_CLASS);
		select.setAttribute(FORM_ATT_NAME, IA_FORM_ID);
		return select;
	}

	private static Img getHeaderIcon(String src, String onclick) {
		Img image = Img.getInstance(src);
		image.setAttribute(ONCLICK_ATT_NAME, onclick);
		return image;
	}

	private static Tag getPictureDialog (Properties props) {
		Table table = Table.getInstance(props.getProperty(DIALOG_IMAGE_CAPTION_KEY), 10, 1, null);
		Tag low = Core.getDiv(null, TextTag.getInstance(Core.LT_ENTITY + Core.LT_ENTITY + Core.LT_ENTITY));
		low.setAttribute(ONCLICK_ATT_NAME, DIALOG_IMAGE_LOW_GALLERY_FN);
		Tag high = Core.getDiv(null, TextTag.getInstance(Core.GT_ENTITY + Core.GT_ENTITY + Core.GT_ENTITY));
		low.setAttribute(ONCLICK_ATT_NAME, DIALOG_IMAGE_HIGH_GALLERY_FN);
		table.setHeader(low, TextTag.NBSP, high);
		table.getHeader().getRows()[0].getChildren().get(1).setAttribute(CLASS_ATT_NAME, IA_PIC_GLS_CLASS);
		table.getHeader().getRows()[0].getChildren().get(1).setAttribute(COLSPAN_ATT_NAME, "8");
		table.getBody().setAttribute(CLASS_ATT_NAME, IA_PIC_BDY_CLASS);
		return Core.getDiv(IA_DIA_2_CLASS, table);
	}
	
	private static Tag getArticleDialog (String caption, String fn, Properties props) {
		Tag dialog = Core.getDiv(IA_DIA_ART_1_CLASS, Core.getDiv(IA_ART_CAP_CLASS, Core.getDiv(null, TextTag.getInstance(caption))));
		Table table = Table.getInstance(null, 5, 1, new String[]{props.getProperty(DIALOG_ARTICLE_ID_KEY), props.getProperty(DIALOG_ARTICLE_CAPTION_KEY), props.getProperty(DIALOG_ARTICLE_AUTHOR_KEY), props.getProperty(DIALOG_ARTICLE_LAST_CHANGE_KEY)});
		table.getHeader().getRows()[0].getChildren().get(table.getHeader().getRows()[0].getChildren().size() - 1).setAttribute(COLSPAN_ATT_NAME, "2");
		dialog.appendChild(Core.getDiv(IA_DIA_ART_2_CLASS, table));
		Tag button = Core.getInstance(null, null, TYPE_BUTTON, props.getProperty(DIALOG_ARTICLE_BUTTON_TEXT_KEY), fn, false);
		dialog.appendChild(Core.getDiv(IA_ART_CALCEL_CLASS, button));
		return dialog;
	}
	
	private static Tag getConfirmDialog (Properties props) {
		Table table = Table.getInstance(props.getProperty(DIALOG_CONFIRM_CAPTION_KEY), 2, 2, null);
		table.setContent(Core.getDiv(null, null), 0, 0);
		Tag confirm = Core.getInstance(null, null, TYPE_BUTTON, props.getProperty(DIALOG_CONFIRM_BUTTON_CONFIRM_TEXT_KEY), DIALOG_CONFIRM_CONFIRM_RETURN_FN, false);
		confirm.setAttribute(CLASS_ATT_NAME, IA_FNT_CLASS);
		table.setContent(confirm, 0, 1);
		Tag cancel = Core.getInstance(null, null, TYPE_BUTTON, props.getProperty(DIALOG_CONFIRM_BUTTON_CANCEL_TEXT_KEY), DIALOG_CONFIRM_CANCEL_RETURN_FN, false);
		cancel.setAttribute(CLASS_ATT_NAME, IA_FNT_CLASS);
		table.setContent(cancel, 1, 1);
		table.getCaption().setAttribute(CLASS_ATT_NAME, IA_CON_CAP_CLASS);
		table.getRows()[0].getCells()[0].setAttribute(Core.COLSPAN_ATT_NAME, "2");
		table.getRows()[0].getCells()[0].setAttribute(CLASS_ATT_NAME, IA_CON_MSG_CLASS);
		table.getRows()[0].removeChild(table.getRows()[0].getCells()[1]);
		table.getRows()[1].getCells()[0].setAttribute(CLASS_ATT_NAME, IA_CON_BUT_CLASS);
		table.getRows()[1].getCells()[1].setAttribute(CLASS_ATT_NAME, IA_CNN_BUT_CLASS);
		return Core.getDiv(IA_CON_2_CLASS, table);
	}
	
	private static Tag getErrorDialog (Properties props) {
		Table table = Table.getInstance(props.getProperty(DIALOG_ERROR_CAPTION_KEY), 1, 2, null);
		table.setContent(Core.getDiv(null, null), 0, 0);
		Tag button = Core.getInstance(null, null, TYPE_BUTTON, props.getProperty(DIALOG_ERROR_BUTTON_TEXT_KEY), DIALOG_ERROR_RETURN_FN, false);
		button.setAttribute(CLASS_ATT_NAME, IA_FNT_CLASS);
		table.setContent(button, 0, 1);
		table.getRows()[0].getCells()[0].setAttribute(CLASS_ATT_NAME, IA_ERR_MSG_CLASS);
		table.getRows()[1].getCells()[0].setAttribute(CLASS_ATT_NAME, IA_ERR_BUT_CLASS);
		table.getBody().setAttribute(CLASS_ATT_NAME, IA_ERR_BDY_CLASS);
		table.getCaption().setAttribute(CLASS_ATT_NAME, IA_ERR_CAP_CLASS);
		return Core.getDiv(IA_ERR_2_CLASS, table);
	}
	
	private static Tag getAdviceDialog (Properties props) {
		Table table = Table.getInstance(props.getProperty(DIALOG_ADVICE_CAPTION_KEY), 1, 2, null);
		table.setContent(Core.getDiv(null, null), 0, 0);
		Tag button = Core.getInstance(null, null, TYPE_BUTTON, props.getProperty(DIALOG_ADVICE_BUTTON_TEXT_KEY), DIALOG_ADVICE_RETURN_FN, false);
		button.setAttribute(CLASS_ATT_NAME, IA_FNT_CLASS);
		table.setContent(button, 0, 1);
		table.getRows()[0].getCells()[0].setAttribute(CLASS_ATT_NAME, IA_ADV_MSG_CLASS);
		table.getRows()[1].getCells()[0].setAttribute(CLASS_ATT_NAME, IA_ADV_BUT_CLASS);
		table.getBody().setAttribute(CLASS_ATT_NAME, IA_ADV_BDY_CLASS);
		table.getCaption().setAttribute(CLASS_ATT_NAME, IA_ADV_CAP_CLASS);
		return Core.getDiv(IA_ADV_2_CLASS, table);
	}
	
	private static Tag getCaptionDialog (Properties props) {
		Table table = Table.getInstance(props.getProperty(DIALOG_CAPTION_CAPTION_KEY), 1, 2, null);
		Tag textarea = Textarea.getInstance(DIALOG_CAPTION_COLS, DIALOG_CAPTION_ROWS, DIALOG_CAPTION_MAX);
		textarea.setAttribute(ID_ATT_NAME, IA_DIA_VAL_ID);
		table.setContent(textarea, 0, 0);
		Tag button = Core.getInstance(null, null, TYPE_BUTTON, props.getProperty(DIALOG_CAPTION_BUTTON_TEXT_KEY), DIALOG_CAPTION_RETURN_FN, false);
		button.setAttribute(CLASS_ATT_NAME, IA_FNT_CLASS);
		table.setContent(button, 0, 1);
		table.getRows()[1].getCells()[0].setAttribute(CLASS_ATT_NAME, IA_DIG_BUT_CLASS);
		table.getCaption().setId(IA_DIA_CAP_ID);
		return Core.getDiv(IA_DIA_2_CLASS, table);
	}
	
	private static Tag getTextDialog (Properties props) {
		Table table = Table.getInstance(props.getProperty(DIALOG_TEXT_CAPTION_KEY), 1, 2, null);
		Tag textarea = Textarea.getInstance(DIALOG_TEXT_COLS, DIALOG_TEXT_ROWS);
		textarea.setAttribute(ID_ATT_NAME, IA_DIA_TXT_VAL_ID);
		table.setContent(textarea, 0, 0);
		Tag button = Core.getInstance(null, null, TYPE_BUTTON, props.getProperty(DIALOG_TEXT_BUTTON_TEXT_KEY), DIALOG_TEXT_RETURN_FN, false);
		button.setAttribute(CLASS_ATT_NAME, IA_FNT_CLASS);
		table.setContent(button, 0, 1);
		table.getRows()[1].getCells()[0].setAttribute(CLASS_ATT_NAME, IA_DIG_BUT_CLASS);
		return Core.getDiv(IA_DIA_2_CLASS, table);
	}
	
	private static Tag getDialogTag (String className, Tag tag) {
		Tag dia = Core.getDiv(className, Core.getDiv(IA_OPA_CLASS, null));
		dia.appendChild(Core.getDiv(IA_DIA_CLASS, tag));
		return dia;
	}
	
	private void getPage(HttpServletRequest request, Properties props, Tag body, String[] params) throws IOException {
		Connection con = null;
		try {
			con = DBConnector.lookup(props.getProperty(DSN_KEY));
			Article[] pArt = loadPublishedArticles(props, con);
			TempArticle[] tArt = loadNonLockedTempArticles(props, con);
			for(int i = 0; i < pArt.length; i ++) {
				body.appendChild(getArticleInput(pArt[i], props, IA_PUBLISHED_ARTICLE_PARAM_NAME));
			}
			for(int i = 0; i < tArt.length; i ++) {
				body.appendChild(getArticleInput(tArt[i], props, IA_TEMP_ARTICLE_PARAM_NAME));
			}
			body.appendChild(getDialogTag (IA_DIA_TXT_FRM_CLASS, getTextDialog(props)));
			body.appendChild(getDialogTag (IA_DIA_FRM_CLASS, getCaptionDialog(props)));
			body.appendChild(getDialogTag (IA_ERR_FRM_CLASS, getErrorDialog(props)));
			body.appendChild(getDialogTag (IA_ADV_FRM_CLASS, getAdviceDialog(props)));
			body.appendChild(getDialogTag (IA_CON_FRM_CLASS, getConfirmDialog(props)));
			body.appendChild(getDialogTag (IA_ART_DEL_FRM_CLASS, getArticleDialog(props.getProperty(DIALOG_ARTICLE_TMP_DELETE_CAPTION_KEY), DIALOG_ARTICLE_TMP_DELETE_FN, props)));
			body.appendChild(getDialogTag (IA_ARP_DEL_FRM_CLASS, getArticleDialog(props.getProperty(DIALOG_ARTICLE_PUB_DELETE_CAPTION_KEY), DIALOG_ARTICLE_PUB_DELETE_FN, props)));
			body.appendChild(getDialogTag (IA_ART_FRM_CLASS, getArticleDialog(props.getProperty(DIALOG_ARTICLE_TMP_CAPTION_KEY), DIALOG_ARTICLE_TMP_FN, props)));
			body.appendChild(getDialogTag (IA_ARP_FRM_CLASS, getArticleDialog(props.getProperty(DIALOG_ARTICLE_PUB_CAPTION_KEY), DIALOG_ARTICLE_PUB_FN, props)));
			body.appendChild(getDialogTag (IA_PIC_FRM_CLASS, getPictureDialog(props)));
			body.appendChild(Core.getDiv(IA_FRM_CLASS, Core.getDiv(IA_FRM_2_CLASS, getImportArticlesForm(props, params, con))));
		}
		catch (Exception e) {
			throw new IOException (e);
		}
		finally {
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private static Input getArticleInput(Article art, Properties props, String name) throws IOException {
		Encoder enc = Base64.getEncoder();
		StringBuffer sb = new StringBuffer();
		sb.append(enc.encodeToString(Long.toString(art.getIndex()).getBytes(props.getProperty(ENCODING_KEY))));
		sb.append(IA_INPUT_VALUE_SEPARATOR);
		sb.append(enc.encodeToString(art.getCaption().getBytes(props.getProperty(ENCODING_KEY))));
		sb.append(IA_INPUT_VALUE_SEPARATOR);
		sb.append(enc.encodeToString(art.getAuthor() == null ? new byte[0] : art.getAuthor().getBytes(props.getProperty(ENCODING_KEY))));
		sb.append(IA_INPUT_VALUE_SEPARATOR);
		sb.append(enc.encodeToString(iaDateFormat.format(new Date(art.getModified())).getBytes(props.getProperty(ENCODING_KEY))));
		return Input.getInstance(name, HIDDEN_TYPE, sb.toString());
	}
	
	

	
	
	private static final String ACP_PARAM = "acp";
	private static final String AAU_PARAM = "aau";
	private static final String ADC_PARAM = "adc";
	private static final String ALM_PARAM = "alm";
	private static final String APU_PARAM = "apu";
	private static final String AID_PARAM = "aid";
	private static final String API_PARAM = "api";
	private static final String ATE_PARAM = "ate";
	private static final String AAC_PARAM = "aac";
	private static final String ATI_PARAM = "ati";
	private static final String ASI_PARAM = "asi";
	private static final String ACA_PARAM = "aca";
	private static final String ASE_PARAM = "ase";
	private static final String ACA_NEW_ARTICLE = "ana";
	private static final String ACA_LOAD_TEMP_ARTICLE = "alt";
	private static final String ACA_LOAD_PUBLISHED_ARTICLE = "alp";
	private static final String ACA_DELETE_TEMP_ARTICLE = "adt";
	private static final String ACA_DELETE_PUBLISHED_ARTICLE = "adp";
	private static final String ACA_SAVE_ARTICLE = "asa";
	private static final String ENCODING_KEY = "ia.encoding";
	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final char YES_CHAR = 'Y';
	private static final char NO_CHAR = 'N';
	private static final String ERROR_MSG_TEMP_ARTICLES_FAILED_KEY = "ia.errorMsg.tempArticleFailed";
	private static final int PARAM_LENGTH = 15;
	private static final String ERROR_ACTION_NOT_DEFINED_KEY = "ia.error.actionNotDefined";
	private static final String ERROR_PUBLISHED_ID_NOT_DEFINED_KEY = "ia.error.publishedIdNotDefined";
	private static final String ERROR_PUBLISHED_ID_NOT_FOUND_KEY = "ia.error.publishedIdNotFound";
	private static final String ADVICE_NEW_ARTICLE_KEY = "ia.advice.newArticle";
	private static final String ADVICE_LOAD_TEMP_ARTICLE_KEY = "ia.advice.loadTempArticle";
	private static final String ADVICE_LOAD_PUBLISHED_ARTICLE_KEY = "ia.advice.loadPublishedArticle";
	private static final String ERROR_INTEGRITY_FAILS_KEY = "ia.error.IntegrityFails";
	private static final String ADVICE_SAVE_ARTICLE_KEY = "ia.advice.saveArticle";
	private static final String ERROR_SAVE_FAILS_KEY = "ia.error.saveFails";
	
	private static String[] getParameters(HttpServletRequest request) throws IOException {
		String[] tags = new String[PARAM_LENGTH];
		MultipartContent multi = new MultipartContent(request.getContentType(), request.getContentLengthLong(), request.getHeader(CONTENT_DISPOSITION_HEADER), request.getInputStream());
		tags[0] = Core.EMPTY;
		tags[1] = multi.getParameter(ACP_PARAM);
		tags[2] = multi.getParameter(AAU_PARAM);
		tags[3] = multi.getParameter(ADC_PARAM);
		tags[4] = multi.getParameter(ALM_PARAM);
		tags[5] = multi.getParameter(APU_PARAM);
		tags[6] = multi.getParameter(AID_PARAM);
		tags[7] = multi.getParameter(API_PARAM);
		tags[8] = multi.getParameter(ATE_PARAM);
		tags[9] = multi.getParameter(AAC_PARAM);
		tags[10] = multi.getParameter(ATI_PARAM);
		tags[11] = multi.getParameter(ASI_PARAM);
		tags[12] = multi.getParameter(ACA_PARAM);
		tags[14] = multi.getParameter(ASE_PARAM);
		return tags;
	}
	
	private static String[] actionProcessor(String[] tags, HttpServletRequest request, Properties props) {
		/*
		 * 0	error message
		 * 1	article caption
		 * 2	article author
		 * 3	articles date of creation
		 * 4	article last modification
		 * 5	article published time
		 * 6	article temporary id
		 * 7	article photo identifiers
		 * 8	article text
		 * 9	import articles action name
		 * 10	article temporary id for action processor
		 * 11	article published id for action processor
		 * 12	article categories
		 * 13	advice message
		 * 14	integrity check
		 */
		
		Connection con = null;
		try {
			con = DBConnector.lookup(props.getProperty(DSN_KEY));
			
			
			Object obj = request.getSession().getAttribute(IA_FORM_ATT_NAME);
			String[] params = obj == null ? tags : ((String[]) obj);
			
			System.out.println("parameters = " +  Arrays.toString(tags));
			System.out.println("attributes = " + Arrays.toString(params));
			String key = getNewIntegrityKey (tags[14], params[14]);
			if(params[14] == null || tags[14] == null || ! tags[14].equals(params[14])) {
				tags[14] = key;
				unlockTempArticle(con, props, tags[6], true);
				newArticle(tags, request, props);
				tags[0] = props.getProperty(ERROR_INTEGRITY_FAILS_KEY);
				throw new IOException();
			}
			tags[14] = key;
			if(tags[9].equals(ACA_NEW_ARTICLE)) {
				unlockTempArticle(con, props, tags[6], true);
				newArticle(tags, request, props);
				tags[13] = props.getProperty(ADVICE_NEW_ARTICLE_KEY);
			}
			else if (tags[9].equals(ACA_LOAD_TEMP_ARTICLE)) {
				loadTempArticle(tags, props, con);
				tags[13] = String.format(props.getProperty(ADVICE_LOAD_TEMP_ARTICLE_KEY), tags[10]);
			}
			else if (tags[9].equals(ACA_LOAD_PUBLISHED_ARTICLE)) {
				loadPublishedArticle(tags, props, con);
				tags[13] = String.format(props.getProperty(ADVICE_LOAD_PUBLISHED_ARTICLE_KEY), tags[11], tags[10]);
			}
			else if (tags[9].equals(ACA_SAVE_ARTICLE)) {
				saveArticle(tags, props, con);
				tags[13] = String.format(props.getProperty(ADVICE_SAVE_ARTICLE_KEY), tags[10]);
			}
			else {
				tags[0] = String.format(props.getProperty(ERROR_ACTION_NOT_DEFINED_KEY), tags[9]);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			tags[0] = tags[0] == null || tags[0].length() == 0 ? e.getMessage() : tags[0];
			tags[0] = tags[0] == null || tags[0].length() == 0 ? e.toString() : tags[0];
		}
		finally {
			tags[9] = null;
			Encoder enc = Base64.getEncoder();
			String charset = props.getProperty(ENCODING_KEY);
			try {
				tags [0] = tags[0] == null || tags[0].length() == 0 ? tags[0] : enc.encodeToString(tags[0].getBytes(charset));
			}
			catch (Exception e) {
				tags[0] = "Unknown Error";
			}
			try {
				tags [13] = tags[13] == null || tags[13].length() == 0 ? tags[13] : enc.encodeToString(tags[13].getBytes(charset));
			}
			catch (Exception e) {
				tags[13] = "Unknown Error";
			}
			request.getSession().setAttribute(IA_FORM_ATT_NAME, tags);
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		//System.out.println(Arrays.toString(tags));
		return tags;
	}
	
	private static void saveArticle(String[] tags, Properties props, Connection con) throws Exception {
		SchemaName schema = new SchemaName(new Identifier(props.getProperty(DB_KEY)));
		TableName tableName = new TableName(schema, new Identifier(props.getProperty(Core.DB_ARTICLES_TMP_KEY)));
		if(tags[6] == null || tags[6].length() == 0) {
			String[] cols = {
				props.getProperty(Core.DB_ARTICLES_TMP_CAPTION_KEY),
				props.getProperty(Core.DB_ARTICLES_TMP_CONTENT_KEY),
				props.getProperty(Core.DB_ARTICLES_TMP_CREATED_KEY),
				props.getProperty(Core.DB_ARTICLES_TMP_CATEGORY_ID_KEY),
				props.getProperty(Core.DB_ARTICLES_TMP_PHOTO_IDS_KEY),
				props.getProperty(Core.DB_ARTICLES_TMP_AUTHOR_KEY),
				props.getProperty(Core.DB_ARTICLES_TMP_LOCKED_KEY),
			};
			Clob clob = con.createClob();
			clob.setString(1, tags[8]);
			Timestamp created = new Timestamp(iaDateFormat.parse(tags[3]).getTime());
			long categoryId = Utils.getCategoryId(con, props, tags[12]);
			String locked = new String(new char[]{YES_CHAR});
			Object[] values = new Object[] {tags[1], clob, created, categoryId, tags[7], tags[2], locked};
			boolean commit = false;
			try {
				if(DBConnector.insert(con, DBConnector.createInsert(tableName, values, cols), false) != 1) {
					throw new IOException (String.format(props.getProperty(ERROR_SAVE_FAILS_KEY), "XX"));
				}
				CompPred comp = new CompPred (new Object[] {
						new Identifier(props.getProperty(Core.DB_ARTICLES_TMP_CAPTION_KEY)),
						new Identifier(props.getProperty(Core.DB_ARTICLES_TMP_CREATED_KEY)),
						new Identifier(props.getProperty(Core.DB_ARTICLES_TMP_CATEGORY_ID_KEY)),
						new Identifier(props.getProperty(Core.DB_ARTICLES_TMP_PHOTO_IDS_KEY)),
						new Identifier(props.getProperty(Core.DB_ARTICLES_TMP_AUTHOR_KEY)),
						new Identifier(props.getProperty(Core.DB_ARTICLES_TMP_LOCKED_KEY))
					}, new Object[] {tags[8], created, categoryId, tags[7], tags[3], locked}, CompPred.EQUAL);
				Rows rows = DBConnector.select(con, DBConnector.createSelect().addFromClause(tableName).addTableSpec(new WhereClause(comp)));
				if(rows.size() < 1) {
					throw new IOException (String.format(props.getProperty(ERROR_SAVE_FAILS_KEY), "XX"));
				}
				tags[6] = Long.toString ((long)rows.get(0).get(props.getProperty(Core.DB_ARTICLES_TMP_ID_KEY)));
				tags[10] = tags[6];
			}
			catch (Exception e) {
				con.rollback();
				e.printStackTrace();
			}
			finally {
				con.commit();
				Utils.freeXlob(clob);
				con.setAutoCommit(commit);
			}
		}
		else {
			String[] cols = {
				props.getProperty(Core.DB_ARTICLES_TMP_CAPTION_KEY),
				props.getProperty(Core.DB_ARTICLES_TMP_CONTENT_KEY),
				props.getProperty(Core.DB_ARTICLES_TMP_MODIFIED_KEY),
				props.getProperty(Core.DB_ARTICLES_TMP_CATEGORY_ID_KEY),
				props.getProperty(Core.DB_ARTICLES_TMP_PHOTO_IDS_KEY),
				props.getProperty(Core.DB_ARTICLES_TMP_AUTHOR_KEY),
			};
			Clob clob = con.createClob();
			clob.setString(1, tags[8]);
			Object[] values = new Object[] {tags[1], clob, new Timestamp(new Date().getTime()), Utils.getCategoryId(con, props, tags[12]), tags[7], tags[2]};
			WhereClause whereClause = new WhereClause (new CompPred(new Object[]{new Identifier(props.getProperty(Core.DB_ARTICLES_TMP_ID_KEY))}, new Object[]{Long.parseLong(tags[10])}, CompPred.EQUAL));
			if(DBConnector.update(con, DBConnector.createUpdate(tableName, cols, values, whereClause)) != 1) {
				tags[0] = String.format(props.getProperty(ERROR_SAVE_FAILS_KEY), tags[10]);
				Utils.freeXlob(clob);
			}
			else {
				Utils.freeXlob(clob);
			}
		}
	}

	private static String getNewIntegrityKey(String tag, String att) {
		String key = null;
		do {
			key = Long.toString(new Date().getTime()) + Long.toString((long) Math.ceil((Math.random() * 100000)));
		}
		while ((tag != null && tag.equals(key)) || (att != null && att.equals(key)));
		return key;
	}

	private static void loadPublishedArticle(String[] tags, Properties props, Connection con) throws IOException {
		long index = -1;
		Article article = null;
		boolean auto = false;
		String encoding = props.getProperty(ENCODING_KEY);
		try {
			index = Long.parseLong(tags[11]);
		}
		catch (Exception e) {
			throw new IOException (props.getProperty(ERROR_PUBLISHED_ID_NOT_DEFINED_KEY));
		}
		try {
			article = Utils.getArticle(props, con, index, encoding);
		}
		catch (Exception e) {
			throw new IOException (String.format(props.getProperty(ERROR_PUBLISHED_ID_NOT_FOUND_KEY), index));
		}
		try {
			auto = con.getAutoCommit();
			con.setAutoCommit(false);
			SchemaName schema = new SchemaName(new Identifier(props.getProperty(DB_KEY)));
			TableName tableName = new TableName(schema , new Identifier(props.getProperty(Core.DB_ARTICLES_KEY)));
			String referenceName = null;
			WhereClause where = new WhereClause(new CompPred(new Object[]{new Identifier(props.getProperty(DB_ARTICLES_ID_KEY))}, new Object[]{index}, CompPred.EQUAL));
			if(DBConnector.delete(con, DBConnector.createDelete(tableName, referenceName, where), false) == 1) {
				tableName = new TableName(schema, new Identifier(props.getProperty(Core.DB_ARTICLES_TMP_KEY)));
				String[] strings = {
					props.getProperty(Core.DB_ARTICLES_TMP_ARTICLE_ID_KEY),
					props.getProperty(Core.DB_ARTICLES_TMP_CAPTION_KEY),
					props.getProperty(Core.DB_ARTICLES_TMP_CONTENT_KEY),
					props.getProperty(Core.DB_ARTICLES_TMP_CREATED_KEY),
					props.getProperty(Core.DB_ARTICLES_TMP_CATEGORY_ID_KEY),
					props.getProperty(Core.DB_ARTICLES_TMP_PHOTO_IDS_KEY),
					props.getProperty(Core.DB_ARTICLES_TMP_AUTHOR_KEY),
					props.getProperty(Core.DB_ARTICLES_TMP_LOCKED_KEY)
				};
				Object[] values = new Object[] {
					article.getIndex(),
					article.getCaption(),
					article.getContent(),
					new Timestamp(article.getModified()),
					article.getCategoryId(),
					Article.getPhotoIds(article.getPhotos(), Article.PHOTO_IDS_SEPARATOR),
					article.getAuthor(),
					new String(new char[]{YES_CHAR})
				};
				if(DBConnector.insert(con, DBConnector.createInsert(tableName, values, strings), false) == 1) {
					where = new WhereClause (new CompPred(new Object[]{new Identifier(props.getProperty(Core.DB_ARTICLES_TMP_ARTICLE_ID_KEY))}, new Object[]{article.getIndex()}, CompPred.EQUAL));
					TempArticle[] articles = Utils.loadTempArticleFromRows(con, DBConnector.select(con, DBConnector.createSelect().addFromClause(tableName).addTableSpec(where)), props, encoding);
					Utils.getCategoryName(con, props, articles[0].getCategoryId());
					if(articles.length == 1) {
						unlockTempArticle(con, props, tags[6], false);
						tags[1] = articles[0].getCaption();
						tags[2] = articles[0].getAuthor();
						tags[3] = iaDateFormat.format(new Date(articles[0].getCreated()));
						tags[4] = iaDateFormat.format(new Date(articles[0].getModified()));
						tags[5] = tags[3];
						tags[6] = Long.toString(articles[0].getIndex());
						tags[7] = Article.getPhotoIds(articles[0].getPhotos(), Article.PHOTO_IDS_SEPARATOR);
						tags[8] = articles[0].getContent();
						tags[9] = null;
						tags[10] = Long.toString(articles[0].getIndex());
						tags[11] = Long.toString(articles[0].getArticleId());
						tags[12] = Utils.getCategoryName(con, props, articles[0].getCategoryId());
					}
					else {
						throw new IOException(props.getProperty(String.format(props.getProperty(ERROR_PUBLISHED_ID_NOT_FOUND_KEY), index)));
					}
				}
				else {
					throw new IOException(props.getProperty(String.format(props.getProperty(ERROR_PUBLISHED_ID_NOT_FOUND_KEY), index)));
				}
			}
			else {
				throw new IOException(props.getProperty(String.format(props.getProperty(ERROR_PUBLISHED_ID_NOT_FOUND_KEY), index)));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		finally {
			try {
				con.commit();
				con.setAutoCommit(auto);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void loadTempArticle(String[] tags, Properties props, Connection con) throws IOException, SQLException {
		TempArticle[] articles = loadNonLockedTempArticlesById(props, con, Long.parseLong(tags[10]));
		if(articles.length != 1) {
			tags[0] = props.getProperty(ERROR_MSG_TEMP_ARTICLES_FAILED_KEY);
		}
		else {
			unlockTempArticle(con, props, tags[6], true);
			tags[1] = articles[0].getCaption();
			tags[2] = articles[0].getAuthor();
			tags[3] = iaDateFormat.format(new Date(articles[0].getCreated()));
			tags[4] = iaDateFormat.format(new Date(articles[0].getModified()));
			Timestamp pub = articles[0].getPublished();
			tags[5] = pub == null ? null : iaDateFormat.format(pub.getTime());
			tags[6] = Long.toString(articles[0].getIndex());
			tags[7] = Article.getPhotoIds(articles[0].getPhotos(), Article.PHOTO_IDS_SEPARATOR);
			tags[8] = articles[0].getContent();
			tags[9] = null;
			
		}
	}
	
	private static void unlockTempArticle(Connection con, Properties props, String id, boolean commit) {
		try {
			if(id != null && id.length() > 0) {
				long index = Long.parseLong(id);
				SchemaName schema = new SchemaName (new Identifier(props.getProperty(Core.DB_KEY)));
				TableName table = new TableName(schema, new Identifier(props.getProperty(Core.DB_ARTICLES_TMP_KEY)));
				WhereClause where = new WhereClause (new CompPred(new Object[]{new Identifier(props.getProperty(Core.DB_ARTICLES_TMP_ID_KEY))}, new Object[]{index}, CompPred.EQUAL));
				DBConnector.update(con, DBConnector.createUpdate(table, new String[]{props.getProperty(Core.DB_ARTICLES_TMP_LOCKED_KEY)}, new Object[]{new String(new char[]{NO_CHAR})}, where), commit);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static TempArticle[] loadNonLockedTempArticles(Properties props, Connection con) throws SQLException, IOException {
		SchemaName schema = new SchemaName(new Identifier(props.getProperty(Core.DB_KEY)));
		TableName table = new TableName(schema , new Identifier(props.getProperty(Core.DB_ARTICLES_TMP_KEY)));
		CompPred comp2 = new CompPred(new Object[]{new Identifier(props.getProperty(Core.DB_ARTICLES_TMP_LOCKED_KEY))}, new Object[]{new String(new char[]{YES_CHAR}).toUpperCase()}, CompPred.EQUAL);
		CompPred comp3 = new CompPred(new Object[]{new Identifier(props.getProperty(Core.DB_ARTICLES_TMP_LOCKED_KEY))}, new Object[]{new String(new char[]{YES_CHAR}).toLowerCase()}, CompPred.EQUAL);
		WhereClause where = new WhereClause(WhereClause.NOT, WhereClause.LEFT_BRACKET, comp2, WhereClause.OR, comp3, WhereClause.RIGHT_BRACKET);
		return Utils.loadTempArticleFromRows(con, DBConnector.select(con, DBConnector.createSelect().addFromClause(table).addTableSpec(where)), props, props.getProperty(ENCODING_KEY, DEFAULT_ENCODING));
	}
	
	private static Article[] loadPublishedArticles(Properties props, Connection con) throws SQLException, IOException {
		SchemaName schema = new SchemaName(new Identifier(props.getProperty(Core.DB_KEY)));
		TableName table = new TableName(schema , new Identifier(props.getProperty(Core.DB_ARTICLES_KEY)));
		CompPred comp = new CompPred(new Object[]{new Identifier(props.getProperty(Core.DB_ARTICLES_ID_KEY))}, new Object[]{1}, CompPred.NOT_EQUAL);
		return Utils.loadArticlesFromRows(con, DBConnector.select(con, DBConnector.createSelect().addFromClause(table).addTableSpec(new WhereClause(comp))), props, props.getProperty(ENCODING_KEY, DEFAULT_ENCODING));
	}
	
	private static TempArticle[] loadNonLockedTempArticlesById(Properties props, Connection con, long id) throws IOException, SQLException {
		SchemaName schema = new SchemaName(new Identifier(props.getProperty(Core.DB_KEY)));
		TableName table = new TableName(schema , new Identifier(props.getProperty(Core.DB_ARTICLES_TMP_KEY)));
		CompPred comp = new CompPred(new Object[]{new Identifier(props.getProperty(Core.DB_ARTICLES_TMP_ID_KEY))}, new Object[]{id}, CompPred.EQUAL);
		CompPred comp2 = new CompPred(new Object[]{new Identifier(props.getProperty(Core.DB_ARTICLES_TMP_LOCKED_KEY))}, new Object[]{new String(new char[]{YES_CHAR}).toUpperCase()}, CompPred.EQUAL);
		CompPred comp3 = new CompPred(new Object[]{new Identifier(props.getProperty(Core.DB_ARTICLES_TMP_LOCKED_KEY))}, new Object[]{new String(new char[]{YES_CHAR}).toLowerCase()}, CompPred.EQUAL);
		WhereClause where = new WhereClause(comp, WhereClause.AND, WhereClause.NOT, WhereClause.LEFT_BRACKET, comp2, WhereClause.OR, comp3, WhereClause.RIGHT_BRACKET);
		TempArticle[] temp = Utils.loadTempArticleFromRows(con, DBConnector.select(con, DBConnector.createSelect().addFromClause(table).addTableSpec(where)), props, props.getProperty(ENCODING_KEY, DEFAULT_ENCODING));
		if(temp.length == 1) {
			if(DBConnector.update(con, DBConnector.createUpdate(table, new String[]{props.getProperty(Core.DB_ARTICLES_TMP_LOCKED_KEY)}, new Object[]{new String(new char[]{YES_CHAR})}, new WhereClause(comp))) != 1) {
				temp = new TempArticle[0];
			}
		}
		return temp;
	}

	private static String[] newArticle(String tags[], HttpServletRequest request, Properties props) throws IOException {
		tags[1] = null;
		tags[2] = request.getRemoteUser();
		tags[3] = iaDateFormat.format(new Date());
		tags[4] = tags[3];
		tags[5] = null;
		tags[6] = null;
		tags[7] = null;
		tags[8] = null;
		tags[9] = null;
		tags[10] = null;
		tags[11] = null;
		return tags;
	}
}
