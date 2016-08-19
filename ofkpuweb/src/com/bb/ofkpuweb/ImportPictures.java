package com.bb.ofkpuweb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.pabk.emanager.sql.sap.Identifier;
import org.pabk.emanager.sql.sap.SchemaName;
import org.pabk.emanager.sql.sap.TableName;
import org.pabk.html.Form;
import org.pabk.html.Input;
import org.pabk.html.Select;
import org.pabk.html.Table;
import org.pabk.html.Tag;
import org.pabk.html.TextTag;
import org.pabk.web.db.DBConnector;
import org.pabk.web.db.Rows;

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
	
	private static final int ACT_NONE = 0;
	private static final String PICTURE_FILE_TEXT_KEY = "ip.pictureFile.text";
	private static final String TEXT_TYPE = "text";
	private static final String DEFAULT_GALLERY_ID_KEY = "ip.default.galleryId";
	private static final String MAX_OPTION_LENGHT_KEY = "ip.maxOptionLength";
	private static final String DEFAULT_ARTICLE_ID_KEY = "ip.default.articleId";
	private static final String FILE_INPUT_ONCHANGE_KEY = "ip.input.file.onchange";
	private static final String COLSPAN_ATT_NAME = "colspan";
	private static final String ID_TEXT_KEY = "ip.id.text";
	private static final String GALLERY_ID_TEXT_KEY = "ip.galleryId.text";
	private static final String ARTICLE_ID_TEXT_KEY = "ip.articleId.text";
	private static final String DESCRIPTION_TEXT_KEY = "ip.description.text";
	private static final String TYPE_SUBMIT = "submit";
	private static final int ACT_ADD = 1;
	private static final String SUBMIT_TEXT_KEY = "ip.sumbit.text";
	private static final String TEXT_FILE = "file";
	private static final String ONCHANGE_ATT_NAME = "onchange";
	private static final String ENTYPE_ATT_NAME = "enctype";
	private static final String METHOD_POST = "POST";
	private static final String MULTIPART = "multipart/form-data";

	
       
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
		//request.setAttribute(Core.PAGE_ONLOAD_ATT_NAME, String.format(props.getProperty(Ofkpuweb.WINDOW_ONLOAD_KEY), OFK_HLN_ID, Long.parseLong(props.getProperty(TOP_INTERVAL_VALUE_KEY)), OFK_PRRS_CLASS, OFK_ITM_ID, Long.parseLong(props.getProperty(PARTNERS_INTERVAL_VALUE_KEY))));
		super.doGet(request, response);
	}

	private static Tag getPage(HttpServletRequest request, Properties props) throws IOException {
		
		Connection con = null;
		int len = Integer.parseInt(props.getProperty(MAX_OPTION_LENGHT_KEY));
		try {
			String[]  params = actionProcessor(request, props);
			Table table = Table.getInstance(props.getProperty(CAPTION_KEY), 1, 1, null);
			Table work = Table.getInstance((params[0] != null ? params[0] : null), 3, 5, null);
			Tag main = Form.getInstance(request.getRequestURL().toString(), METHOD_POST);
			main.setAttribute(ENTYPE_ATT_NAME, MULTIPART);
			table.setContent(main, 0, 0);
			Input file = Input.getInstance(FIL_PARAM, TEXT_FILE, null);
			file.setAttribute(ONCHANGE_ATT_NAME, String.format(props.getProperty(FILE_INPUT_ONCHANGE_KEY), FIL_PARAM, MIM_PARAM));
			//Button fileButton = Core.getInstance(null, null, TYPE_BUTTON, props.getProperty(FILE_BUTTON_TEXT_KEY), props.getProperty(FILE_BUTTON_ONCLICK_KEY), false);
			Input id = Input.getInstance(PID_PARAM, TEXT_TYPE, params[2]);
			Input mime = Input.getInstance(MIM_PARAM, HIDDEN_TYPE, params[4]);
			Input des = Input.getInstance(DES_PARAM, TEXT_TYPE, params[5]);
			con = DBConnector.lookup(props.getProperty(Core.DSN_KEY));
			Rows rows = Utils.getAllRows(props, props.getProperty(Core.DB_PHOTO_GALLERIES_KEY), con);
			Select gid = Select.getInstance(GID_PARAM, getStringsFromStrings(rows, props.getProperty(Core.DB_PHOTO_GALLERIES_NAME_KEY), len), getStringsFromLongs(rows, props.getProperty(Core.DB_PHOTO_GALLERIES_ID_KEY), len), getOptionIndex(props.getProperty(Core.DB_PHOTO_GALLERIES_ID_KEY), rows, props.getProperty(DEFAULT_GALLERY_ID_KEY), params[3]));
			rows = Utils.getAllRows(props, props.getProperty(Core.DB_ARTICLES_KEY), con);
			Select aid = Select.getInstance(AID_PARAM, getStringsFromStrings(rows, props.getProperty(Core.DB_ARTICLES_CAPTION_KEY), len), getStringsFromLongs(rows, props.getProperty(Core.DB_ARTICLES_ID_KEY), len), getOptionIndex(props.getProperty(Core.DB_ARTICLES_ID_KEY), rows, props.getProperty(DEFAULT_ARTICLE_ID_KEY), params[4]));
			work.setBody (TextTag.getInstance(props.getProperty(PICTURE_FILE_TEXT_KEY)), file);
			work.getRows()[0].getCells()[1].setAttribute(COLSPAN_ATT_NAME, "2");
			work.addRow(new Tag[]{TextTag.getInstanceNBSP(props.getProperty(ID_TEXT_KEY)), id, TextTag.NBSP});
			work.addRow(new Tag[]{TextTag.getInstanceNBSP(props.getProperty(GALLERY_ID_TEXT_KEY)), gid, TextTag.NBSP});
			work.addRow(new Tag[]{TextTag.getInstanceNBSP(props.getProperty(ARTICLE_ID_TEXT_KEY)), aid, TextTag.NBSP});
			work.addRow(new Tag[]{TextTag.getInstanceNBSP(props.getProperty(DESCRIPTION_TEXT_KEY)), des, TextTag.NBSP});
			work.addRow(new Tag[]{Core.getInstance(ACT_PARAM, Integer.toString(ACT_ADD), TYPE_SUBMIT, props.getProperty(SUBMIT_TEXT_KEY), null, false), TextTag.NBSP, TextTag.NBSP});
			main.appendChild(work);
			main.appendChild(mime);
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
	
	private static void importPicture (String[] p, Part part, Properties props) throws IOException {
		InputStream in = null;
		Connection con = null;
		Blob blob = null;
		try {
			in = part.getInputStream();
			con = DBConnector.lookup(props.getProperty(DSN_KEY));
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] b = new byte[2048];
			int i = -1;
			while ((i = in.read(b)) >= 0) {
				out.write(b, 0, i);
			}
			blob = con.createBlob();
			blob.setBytes(1, out.toByteArray());
			TableName tableName = new TableName (new SchemaName(new Identifier(props.getProperty(Core.DB_KEY))) , new Identifier(props.getProperty(Core.DB_PHOTOS_KEY)));
			ArrayList<Object> v = new ArrayList<Object>();
			ArrayList<String> c = new ArrayList<String>();
			if(p[2] != null || p[2].length() > 0) {
				v.add(Long.parseLong(p[2]));
				c.add(props.getProperty(Core.DB_PHOTOS_ID_KEY));
			}
			if(p[3] != null || p[3].length() > 0) {
				v.add(Long.parseLong(p[3]));
				c.add(props.getProperty(Core.DB_PHOTOS_GALLERY_ID_KEY));
			}
			if(p[4] != null || p[4].length() > 0) {
				v.add(Long.parseLong(p[4]));
				c.add(props.getProperty(Core.DB_PHOTOS_ARTICLE_ID_KEY));
			}
			if(p[4] != null || p[4].length() > 0) {
				v.add(p[4]);
				c.add(props.getProperty(Core.DB_PHOTOS_MIME_KEY));
			}
			if(p[5] != null || p[5].length() > 0) {
				v.add(p[5]);
				c.add(props.getProperty(Core.DB_PHOTOS_DESCRIPTION_KEY));
			}
			v.add(blob);
			c.add(props.getProperty(Core.DB_PHOTOS_PHOTO_KEY));
			
			DBConnector.insert(con, DBConnector.createInsert(tableName, v.toArray(new Object[v.size()]), c.toArray(new String[c.size()])));
		}
		catch (Exception e) {
			throw new IOException (e);
		}
		finally {
			try {if(in != null)in.close();} catch (Exception e){}
			try {if(con != null)con.close();} catch (Exception e){}
			try {if(blob != null)Utils.freeXlob(blob);} catch (Exception e){}
		}
	}
	
	
	private static String[] actionProcessor(HttpServletRequest request, Properties props) {
		String[] tags = new String[6];
		try {
			String act = request.getParameter(ACT_PARAM);
			if(act == null) {
				act = Integer.toString((int) request.getAttribute(ACT_PARAM));
			}
			switch (act == null ? ACT_NONE : Integer.parseInt(act)) {
			case ACT_ADD:
				InputStream in = request.getInputStream();
				byte[] b = new byte[request.getContentLength()];
				in.read(b);
				System.out.println(new String(b, "UTF-8"));
				//tags[2] = getParameter(request.getPart(PID_PARAM));
				//importPicture(tags ,request.getPart(FIL_PARAM), props);
			default:
				tags[1] = request.getParameter(FIL_PARAM);
				tags[2] = request.getParameter(PID_PARAM);
				tags[3] =request.getParameter(GID_PARAM);
				tags[4] =request.getParameter(MIM_PARAM);
				tags[5] =request.getParameter(DES_PARAM);
			}
		}
		catch (Exception e) {
			tags[0] = e.getMessage();
		}
		return tags;
	}

	private static String getParameter(Part part) {
		String content = part.getContentType();
		return part.toString();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute(ACT_PARAM, ACT_ADD);
		doGet(request, response);
	}

}
