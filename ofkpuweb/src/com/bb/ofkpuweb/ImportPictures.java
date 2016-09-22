package com.bb.ofkpuweb;

import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pabk.emanager.sql.sap.Identifier;
import org.pabk.emanager.sql.sap.SchemaName;
import org.pabk.emanager.sql.sap.TableName;
import org.pabk.html.Form;
import org.pabk.html.Img;
import org.pabk.html.Input;
import org.pabk.html.Select;
import org.pabk.html.Table;
import org.pabk.html.Tag;
import org.pabk.html.TextTag;
import org.pabk.web.db.DBConnector;
import org.pabk.web.db.Rows;

import com.bb.http.MultipartContent;

/**
 * Servlet implementation class ImportPictures
 */
@WebServlet("/import/pictures")
public class ImportPictures extends Core {
	private static final long serialVersionUID = 1L;
	private static final String IP_CLASS = "ip";
	private static final String STYLE_URL_KEY = "ip.style.url";
	private static final String SCRIPT_SOURCE_KEY = "ip.script.source";
	private static final String CAPTION_KEY = "ip.caption.key";
	private static final String ACT_PARAM = "act";
	private static final String PID_PARAM = "pid";
	private static final String FIL_PARAM = "fil";
	private static final String GID_PARAM = "gid";
	private static final String AID_PARAM = "aid";
	private static final String MIM_PARAM = "mim";
	private static final String DES_PARAM = "des";
	
	private static final int ACT_NONE 	= 0;
	private static final int ACT_LOAD 	= 1;
	private static final int ACT_ADD_ 	= 2;
	private static final int ACT_OK__ 	= 3;
	private static final String PICTURE_FILE_TEXT_KEY = "ip.pictureFile.text";
	private static final String TEXT_TYPE = "text";
	private static final String DEFAULT_GALLERY_ID_KEY = "ip.default.galleryId";
	private static final String MAX_OPTION_LENGHT_KEY = "ip.maxOptionLength";
	private static final String DEFAULT_ARTICLE_ID_KEY = "ip.default.articleId";
	private static final String COLSPAN_ATT_NAME = "colspan";
	private static final String ID_TEXT_KEY = "ip.id.text";
	private static final String GALLERY_ID_TEXT_KEY = "ip.galleryId.text";
	private static final String ARTICLE_ID_TEXT_KEY = "ip.articleId.text";
	private static final String DESCRIPTION_TEXT_KEY = "ip.description.text";
	private static final String TYPE_SUBMIT = "submit";
	
	private static final String SUBMIT_TEXT_KEY = "ip.sumbit.text";
	private static final String TEXT_FILE = "file";
	private static final String METHOD_POST = "POST";
	private static final String MULTIPART = "multipart/form-data";
	
	private static final String FORM_ATT_NAME = "form-data";
	private static final String SUBMIT_ADD_KEY = "ip.submit.add";
	private static final String ERROR_NULL_DESCRIPTION_KEY = "ip.error.nullDescription";
	private static final String ERROR_NULL_PICTURE_KEY = "ip.erro.nullPiscture";
	private static final String ERROR_NO_PICTURE_KEY = "ip.error.noPicture";
	private static final String IMAGE_MIME_TYPE = "image";
	private static final String ERROR_OK_MESSAGE_KEY = "ip.error.OkMessage";
	private static final String SUBMIT_OK_TEXT_KEY = "ip.sumbit.ok.text";
	private static final String SUBMIT_CANCEL_TEXT_KEY = "ip.sumbit.cancel.text";
	private static final String SUBMIT_RETURN_TEXT_KEY = "ip.sumbit.return.text";
	private static final String SUBMIT_RETURN_ONCLICK_KEY = "ip.submit.onclick";

	
       
    /**
     * @see Core#Core()
     */
    public ImportPictures() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Properties props = getProperties();
		Tag body = Core.getDiv(IP_CLASS, getPage (request, props));
		request.setAttribute(Core.PAGE_CONTENT_ATT_NAME, body);
		request.setAttribute(Core.PAGE_STYLE_ATT_NAME, props.getProperty(STYLE_URL_KEY));
		request.setAttribute(Core.PAGE_SCRIPT_SOURCE_ATT_NAME, props.getProperty(SCRIPT_SOURCE_KEY));
		super.doGet(request, response);
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		actionProcessor(request, getProperties());
		doGet(request, response);
	}
	
	private static String[] actionProcessor(HttpServletRequest request, Properties props) {
		String[] tags = new String[8];
		try {
			MultipartContent multi = new MultipartContent(request.getContentType(), request.getContentLengthLong(), request.getHeader(CONTENT_DISPOSITION_HEADER), request.getInputStream());
			tags[1] = multi.getParameter(ACT_PARAM);
			tags[2] = multi.getParameter(PID_PARAM);
			tags[3] = multi.getParameter(FIL_PARAM);
			tags[4] = multi.getParameter(GID_PARAM);
			tags[5] = multi.getParameter(AID_PARAM);
			tags[6] = multi.getParameter(MIM_PARAM);
			tags[7] = multi.getParameter(DES_PARAM);
			//System.out.println(Arrays.toString(tags));
			switch ((int)parseLong (tags[1], ACT_NONE)) {
			case ACT_LOAD:
				loadPicture (tags, props, multi);
				request.getSession().setAttribute(FORM_ATT_NAME, tags);
				break;
			case ACT_ADD_:
				tags = (String[]) request.getSession().getAttribute(FORM_ATT_NAME);
				importPicture(tags, multi, props);
				request.getSession().removeAttribute(FORM_ATT_NAME);
				tags[1] = Integer.toString(ACT_OK__);
				tags[0] = props.getProperty(ERROR_OK_MESSAGE_KEY);
				break;
			case ACT_OK__:
			default:
				request.getSession().removeAttribute(FORM_ATT_NAME);
				tags = new String[8];
				tags[1] = Integer.toString(ACT_LOAD);
			}
		}
		catch (Exception e) {
			request.getSession().removeAttribute(FORM_ATT_NAME);
			tags = new String[8];
			tags[1] = Integer.toString(ACT_LOAD);
			tags[0] = e.getMessage();
		}
		finally {
			request.setAttribute(FORM_ATT_NAME, tags);
		}
		//System.out.println(Arrays.toString(tags));
		return tags;
	}
	
	private static void loadPicture(String[] tags, Properties props, MultipartContent multi) throws IOException {
		if(tags[3].length() == 0) {
			throw new IOException (props.getProperty(ERROR_NULL_PICTURE_KEY));
		}
		if(tags[7].length() == 0) {
			throw new IOException (props.getProperty(ERROR_NULL_DESCRIPTION_KEY));
		}
		int s = tags[3].indexOf(Core.COLON_CHAR);
		int e = tags[3].indexOf(Core.SEMICOLON_CHAR);
		if(s < 0 || e < 0 || (e - s) < 0) {
			throw new IOException (props.getProperty(ERROR_NO_PICTURE_KEY));
		}
		String mime = tags[3].substring(s + 1, e);
		if((s = mime.indexOf(SLASH_CHAR)) < 0) {
			throw new IOException (props.getProperty(ERROR_NO_PICTURE_KEY));
		}
		if(!mime.substring (0, s).equals(IMAGE_MIME_TYPE)) {
			throw new IOException (props.getProperty(ERROR_NO_PICTURE_KEY));
		}
		tags[6] = mime;
		tags[1] = Integer.toString(ACT_ADD_);
	}

	private static long parseLong(String s, long d) {
		try {
			return Long.parseLong(s);
		}
		catch (Exception e) {
			return d;
		}
	}
	
	private static Input getInput(String name, String type, String value, boolean disabled) {
		Input i = Input.getInstance(name, type, value);
		if (disabled) {
			i.setAttribute(DISABLED_ATT_NAME, Core.EMPTY);
		}
		return i;
	}
	
	private static Select getSelect(String name, String[] opts, String[] oids, int selected, boolean disabled) {
		Select sel = Select.getInstance(name, opts, oids, selected);
		if (disabled) {
			sel.setAttribute(DISABLED_ATT_NAME, Core.EMPTY);
		}
		return sel;
	}
	
	private static Tag getPage(HttpServletRequest request, Properties props) throws IOException {
		Connection con = null;
		try {
			String[] p = (String[]) request.getAttribute(FORM_ATT_NAME);
			p = p == null ? new String[8] : p;
			con = DBConnector.lookup(props.getProperty(Core.DSN_KEY));
			int len = Integer.parseInt(props.getProperty(MAX_OPTION_LENGHT_KEY));
			int a = (int)parseLong (p[1], ACT_LOAD);
			Table table = Table.getInstance(props.getProperty(CAPTION_KEY), 1, 1, null);
			Table work = Table.getInstance((p[0] != null ? p[0] : null), 3, 5, null);
			Tag main = Form.getInstance(request.getRequestURL().toString(), METHOD_POST);
			main.setAttribute(ENCTYPE_ATT_NAME, MULTIPART);
			table.setContent(main, 0, 0);
			
			Input fil = getInput(FIL_PARAM, TEXT_FILE, null, a != ACT_LOAD);
			Input pid = getInput(PID_PARAM, TEXT_TYPE, p[2], a != ACT_LOAD);
			Rows rows = Utils.getAllRows(props, props.getProperty(Core.DB_PHOTO_GALLERIES_KEY), con);
			Select gid = getSelect (GID_PARAM,
					getStringsFromStrings(rows, props.getProperty(Core.DB_PHOTO_GALLERIES_NAME_KEY), len),
					getStringsFromLongs(rows, props.getProperty(Core.DB_PHOTO_GALLERIES_ID_KEY), len),
					getOptionIndex(props.getProperty(Core.DB_PHOTO_GALLERIES_ID_KEY), rows, props.getProperty(DEFAULT_GALLERY_ID_KEY), p[4]),
					a != ACT_LOAD);
			rows = Utils.getAllRows(props, props.getProperty(Core.DB_ARTICLES_KEY), con);
			Select aid = getSelect(AID_PARAM,
					getStringsFromStrings(rows, props.getProperty(Core.DB_ARTICLES_CAPTION_KEY), len),
					getStringsFromLongs(rows, props.getProperty(Core.DB_ARTICLES_ID_KEY), len),
					getOptionIndex(props.getProperty(Core.DB_ARTICLES_ID_KEY), rows, props.getProperty(DEFAULT_ARTICLE_ID_KEY), p[5]),
					a != ACT_LOAD);
			Input mim = getInput(MIM_PARAM, HIDDEN_TYPE, p[6], a != ACT_LOAD);
			Input des = getInput(DES_PARAM, TEXT_TYPE, p[7], a != ACT_LOAD);
			
			work.setBody (TextTag.getInstance(props.getProperty(PICTURE_FILE_TEXT_KEY)), fil);
			work.getRows()[work.getRows().length - 1].getCells()[1].setAttribute(COLSPAN_ATT_NAME, "2");
			if(a == ACT_ADD_ || a == ACT_OK__) {
				work.addRow(new Tag[]{Img.getInstance(p[3])});
				work.getRows()[work.getRows().length - 1].getCells()[0].setAttribute(COLSPAN_ATT_NAME, "3");
			}
			Tag buttons = Core.getDiv(null, Core.getInstance(ACT_PARAM, Integer.toString(ACT_NONE), TYPE_SUBMIT, props.getProperty(a == ACT_OK__ ? SUBMIT_OK_TEXT_KEY : SUBMIT_CANCEL_TEXT_KEY), null, !(a == ACT_ADD_ || a ==ACT_OK__)));
			buttons.appendChild(Core.getInstance(null, null, TYPE_BUTTON, props.getProperty(SUBMIT_RETURN_TEXT_KEY), String.format(props.getProperty(SUBMIT_RETURN_ONCLICK_KEY), request.getContextPath()), false));
			work.addRow(new Tag[]{TextTag.getInstanceNBSP(props.getProperty(ID_TEXT_KEY)), pid, TextTag.NBSP});
			work.addRow(new Tag[]{TextTag.getInstanceNBSP(props.getProperty(GALLERY_ID_TEXT_KEY)), gid, TextTag.NBSP});
			work.addRow(new Tag[]{TextTag.getInstanceNBSP(props.getProperty(ARTICLE_ID_TEXT_KEY)), aid, TextTag.NBSP});
			work.addRow(new Tag[]{TextTag.getInstanceNBSP(props.getProperty(DESCRIPTION_TEXT_KEY)), des, TextTag.NBSP});
			work.addRow(new Tag[]{Core.getInstance(ACT_PARAM, Integer.toString(a), TYPE_SUBMIT, props.getProperty(SUBMIT_TEXT_KEY), null, !(a == ACT_LOAD)),
					Core.getInstance(ACT_PARAM, Integer.toString(a), TYPE_SUBMIT, props.getProperty(SUBMIT_ADD_KEY), null, !(a == ACT_ADD_)),
					buttons});
			main.appendChild(work);
			main.appendChild(mim);
			return table;
		}
		catch (Exception e) {
			throw new IOException (e);
		}
		finally {
			try {con.close();} catch (SQLException e) {}
		}
	}
	
	


	private static int getOptionIndex(String id, Rows rows, String def, String gid) {
		int index = -1;
		String[] xx = new String[rows.size()];
		for (int i = 0; i <rows.size(); i ++) {
			String x = Long.toString ((long) rows.get(i).get(id));
			xx[i] = x;
			if(x.equals(gid)) {
				return i;
			}
		}
		for (int i = 0; i < xx.length; i ++) {
			if(xx[i].equals(def)) {
				return i;
			}
		}
		return index;
	}

	private static String[] getStringsFromLongs(Rows rows, String key, int  len) {
		String[] names = new String[rows.size()];
		for(int i = 0; i < names.length; i ++) {
			names[i] = Long.toString((long) rows.get(i).get(key));
			if(names[i].length() > len) {
				names[i] = names[i].substring(0, len - 3) + "...";
			}
		}
		return names;
	}

	
	private static String[] getStringsFromStrings (Rows rows, String key, int len) {
		String[] names = new String[rows.size()];
		for(int i = 0; i < names.length; i ++) {
			names[i] = (String) rows.get(i).get(key);
			if(names[i].length() > len) {
				names[i] = names[i].substring(0, len - 3) + "...";
			}
		}
		return names;
	}
	
	private static void importPicture (String[] p, MultipartContent parts, Properties props) throws IOException {
		Connection con = null;
		Blob blob = null;
		try {
			con = DBConnector.lookup(props.getProperty(DSN_KEY));
			blob = con.createBlob();
			long pid = parseLong (p[2], -1);
			long gid = parseLong (p[4], -1);
			long aid = parseLong (p[5], -1);
			String mim = p[6];
			String des = p[7];
			int index = p[3].indexOf(COMMA_CHAR);
			blob.setBytes(1, Base64.getDecoder().decode(p[3].substring(index + 1)));
			TableName tableName = new TableName (new SchemaName(new Identifier(props.getProperty(Core.DB_KEY))) , new Identifier(props.getProperty(Core.DB_PHOTOS_KEY)));
			ArrayList<Object> v = new ArrayList<Object>();
			ArrayList<String> c = new ArrayList<String>();
			if(pid >= 0) {
				v.add(pid);
				c.add(props.getProperty(Core.DB_PHOTOS_ID_KEY));
			}
			if(gid >= 0) {
				v.add(gid);
				c.add(props.getProperty(Core.DB_PHOTOS_GALLERY_ID_KEY));
			}
			if(aid >= 0) {
				v.add(aid);
				c.add(props.getProperty(Core.DB_PHOTOS_ARTICLE_ID_KEY));
			}
			if(mim != null && mim.length() > 0) {
				v.add(mim);
				c.add(props.getProperty(Core.DB_PHOTOS_MIME_KEY));
			}
			if(des != null && des.length() > 0) {
				v.add(des);
				c.add(props.getProperty(Core.DB_PHOTOS_DESCRIPTION_KEY));
			}
			v.add(blob);
			c.add(props.getProperty(Core.DB_PHOTOS_PHOTO_KEY));
			//System.out.println(DBConnector.createInsert(tableName, v.toArray(new Object[v.size()]), c.toArray(new String[c.size()])).toSQLString(null));
			DBConnector.insert(con, DBConnector.createInsert(tableName, v.toArray(new Object[v.size()]), c.toArray(new String[c.size()])));
		}
		catch (Exception e) {
			throw new IOException (e);
		}
		finally {
			try {if(con != null)con.close();} catch (Exception e){}
			try {if(blob != null)Utils.freeXlob(blob);} catch (Exception e){}
		}
	}
	


}
