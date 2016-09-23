package com.bb.ofkpuweb;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.pabk.emanager.sql.sap.CompPred;
import org.pabk.emanager.sql.sap.FixedPointLiteral;
import org.pabk.emanager.sql.sap.Identifier;
import org.pabk.emanager.sql.sap.LimitClause;
import org.pabk.emanager.sql.sap.OrderClause;
import org.pabk.emanager.sql.sap.RowCount;
import org.pabk.emanager.sql.sap.SchemaName;
import org.pabk.emanager.sql.sap.SortSpec;
import org.pabk.emanager.sql.sap.TableName;
import org.pabk.emanager.sql.sap.UnsInt;
import org.pabk.emanager.sql.sap.WhereClause;
import org.pabk.web.db.DBConnector;
import org.pabk.web.db.Row;
import org.pabk.web.db.Rows;

import com.bb.commons.Article;
import com.bb.commons.Partner;
import com.bb.commons.Photo;
import com.bb.commons.ShortMessage;
import com.bb.commons.TempArticle;

final class Utils {
	
	private static final Object BR = "<br/>";
	private static final int MAX_READED_BYTES = 2048;
	private static final int MAX_READED_CHARS = 2048;
	
	private Utils(){};
		
	static Rows getRows (Connection con, String schema, String table, WhereClause where) throws SQLException {
		return DBConnector.select(con, DBConnector.createSelect().addFromClause(new TableName(new SchemaName(new Identifier(schema)), new Identifier(table))).addTableSpec(where));
	}
	
	private static TempArticle loadTempArticleFromRow (Row row, Properties props, String charset, Connection con) throws IOException, SQLException {
		TempArticle article = new TempArticle((long) row.get(props.getProperty(Core.DB_ARTICLES_TMP_ID_KEY)));
		article.setCaption((String) row.get(props.getProperty(Core.DB_ARTICLES_TMP_CAPTION_KEY)));
		//article.setContent(Utils.readCharsToHtml(getClobReader(row, props.getProperty(Core.DB_ARTICLES_TMP_CONTENT_KEY), charset)));
		article.setContent(Utils.read(getClobReader(row, props.getProperty(Core.DB_ARTICLES_TMP_CONTENT_KEY), charset)));
		article.setModified(((Timestamp) row.get(props.getProperty(Core.DB_ARTICLES_TMP_MODIFIED_KEY))).getTime());
		article.setCategoryId((long) row.get(props.getProperty(Core.DB_ARTICLES_TMP_CATEGORY_ID_KEY)));
		article.setAuthor((String) row.get(props.getProperty(Core.DB_ARTICLES_TMP_AUTHOR_KEY)));
		article.setCreated(((Timestamp) row.get(props.getProperty(Core.DB_ARTICLES_TMP_CREATED_KEY))).getTime());
		article.setArticleId((long) row.get(props.getProperty(Core.DB_ARTICLES_TMP_ARTICLE_ID_KEY)));
		article.setLocked(((String) row.get(props.getProperty(Core.DB_ARTICLES_TMP_LOCKED_KEY))).charAt(0));
		String pids = (String) row.get(props.getProperty(Core.DB_ARTICLES_PHOTO_IDS_KEY));
		article.setPhotos(loadPhotosFromRows(getPhotosForAtricle(con, props, pids == null || pids.length() == 0? new String[0] : pids.split(new String(new char[]{Article.PHOTO_IDS_SEPARATOR}))), props));
		Utils.freeXlob (row.get(props.getProperty(Core.DB_ARTICLES_TMP_CONTENT_KEY)));
		return article;
	}
	
	
	private static Article loadArticleFromRow (Row row, Properties props, String charset, Connection con) throws IOException, SQLException {
		Article article = new Article((long) row.get(props.getProperty(Core.DB_ARTICLES_ID_KEY)));
		article.setCaption((String) row.get(props.getProperty(Core.DB_ARTICLES_CAPTION_KEY)));
		//article.setContent(Utils.readCharsToHtml(getClobReader(row, props.getProperty(Core.DB_ARTICLES_CONTENT_KEY), charset)));
		article.setContent(Utils.read(getClobReader(row, props.getProperty(Core.DB_ARTICLES_CONTENT_KEY), charset)));
		article.setModified(((Timestamp) row.get(props.getProperty(Core.DB_ARTICLES_MODIFIED_KEY))).getTime());
		article.setCategoryId((long) row.get(props.getProperty(Core.DB_ARTICLES_CATEGORY_ID_KEY)));
		article.setAuthor((String) row.get(props.getProperty(Core.DB_ARTICLES_AUTHOR_KEY)));
		String pids = (String) row.get(props.getProperty(Core.DB_ARTICLES_PHOTO_IDS_KEY));
		article.setPhotos(loadPhotosFromRows(getPhotosForAtricle(con, props, pids == null || pids.length() == 0 ? new String[0]: pids.split(new String(new char[]{Article.PHOTO_IDS_SEPARATOR}))), props));
		Utils.freeXlob (row.get(props.getProperty(Core.DB_ARTICLES_CONTENT_KEY)));
		return article;
	}
	
	private static Rows getPhotosForAtricle(Connection con, Properties props, String[] ids) throws SQLException {
		String schema = props.getProperty(Core.DB_KEY);
		String table = props.getProperty(Core.DB_PHOTOS_KEY);
		Object[] where = ids.length == 0 ? null : new Object[ids.length * 2 - 1];
		for(int i = 0; i < ids.length; i ++) {
			if(i > 0) {
				where[i * 2 - 1] = WhereClause.OR;
			}
			where[i * 2] = new CompPred(new Object[]{new Identifier(props.getProperty(Core.DB_PHOTOS_ID_KEY))}, new Object[]{Long.parseLong(ids[i])}, CompPred.EQUAL);
		}
		return where == null ? new Rows() : Utils.getRows(con, schema, table, new WhereClause(where));
	}
	
	private static Photo[] loadPhotosFromRows (Rows rows, Properties props) {
		rows = rows == null ? new Rows() : rows;
		Photo[] photos = new Photo[rows.size()];
		for (int i = 0; i < photos.length; i ++) {
			photos[i] = loadPhotoFromRow (rows.get(i), props);
		}
		return photos;
	}
	
	public static void freePhotos (Article[] articles) throws SQLException {
		for (int i = 0; i < articles.length; i ++) {
			Article article = articles[i];
			int l = article.getPhotos().length;
			for (int j = 0; j < l; j ++) {
				Utils.freeXlob(article.getPhotos()[j]);
			}
		}
	}
	
	private static Photo loadPhotoFromRow(Row row, Properties props) {
		Photo photo = new Photo((long) row.get(props.getProperty(Core.DB_PHOTOS_ID_KEY)));
		photo.setGalleryId((long) row.get(props.getProperty(Core.DB_PHOTOS_GALLERY_ID_KEY)));
		photo.setData(row.get(props.getProperty(Core.DB_PHOTOS_PHOTO_KEY)));
		photo.setDescription ((String) row.get(props.getProperty(Core.DB_PHOTOS_DESCRIPTION_KEY)));
		photo.setMime ((String) row.get(props.getProperty(Core.DB_PHOTOS_MIME_KEY)));
		return photo;
	}

	public static Article getArticle(Properties props, Connection con, long index, String charset) throws SQLException, IOException {
		String schema = props.getProperty(Core.DB_KEY);
		String table = props.getProperty(Core.DB_ARTICLES_KEY);
		WhereClause where = new WhereClause(new CompPred(new Object[]{new Identifier(props.getProperty(Core.DB_ARTICLES_ID_KEY))}, new Object[]{index}, CompPred.EQUAL));
		return loadArticleFromRow (Utils.getRows(con, schema, table, where).get(0), props, charset, con);
	}

	static void freeXlob(Object obj) throws SQLException {
		if(obj instanceof Clob) {
			((Clob) obj).free();
		}
		else if (obj instanceof Blob) {
			((Blob) obj).free();
		}
	}

	private static Reader getClobReader(Row row, String column, String charset) throws SQLException, IOException {
		Object obj = row.get(column);
		System.out.println(obj.getClass().getName());
		String s = null;
		if(obj instanceof Clob) {
			return ((Clob) obj).getCharacterStream();
		}
		else if (obj instanceof String) {
			s = (String) obj;
		}
		else {
			s = obj.toString();
		}
		return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(s.getBytes(charset)), charset));
	}
	
	private static String read (Reader reader) throws IOException {
		StringBuffer sb = new StringBuffer();
		char[] c = new char[MAX_READED_CHARS];
		int i = -1;
		while ((i = reader.read(c)) >= 0) {
			sb.append(c, 0, i);
		}
		reader.close();
		return sb.toString();
	}
	
	public static String readCharsToHtml (String text) throws IOException {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new StringReader(text));
		String line = null;
		boolean first = true;
		while((line = br.readLine()) != null) {
			if(!first) {
				sb.append(BR);
			}
			else {
				first = false;
			}
			sb.append(line);
		}
		br.close();
		return sb.toString();
	}

	public static Photo getPhotoFromDb (Properties props, Connection con, long id) throws SQLException {
		String schema = props.getProperty(Core.DB_KEY);
		String table = props.getProperty(Core.DB_PHOTOS_KEY);
		WhereClause where = new WhereClause(new CompPred(new Object[]{new Identifier(props.getProperty(Core.DB_PHOTOS_ID_KEY))}, new Object[]{id}, CompPred.EQUAL));
		return loadPhotoFromRow (Utils.getRows(con, schema, table, where).get(0), props);
	}

	public static void write(InputStream in, OutputStream out) throws IOException {
		byte[] b = new byte[MAX_READED_BYTES];
		int i = -1;
		while ((i = in.read(b)) >=0) {
			out.write(b, 0, i);
		}
	}
	
	public static TempArticle[] loadTempArticleFromRows (Connection con, Rows rows, Properties props, String charset) throws IOException, SQLException {
		rows = rows == null ? new Rows() : rows;
		TempArticle[] articles = new TempArticle[rows.size()];
		for (int i = 0; i < articles.length; i ++) {
			articles[i] = loadTempArticleFromRow (rows.get(i), props, charset, con);
		}
		return articles;
	}
	
	public static Article[] loadArticlesFromRows (Connection con, Rows rows, Properties props, String charset) throws IOException, SQLException {
		rows = rows == null ? new Rows() : rows;
		Article[] articles = new Article[rows.size()];
		for (int i = 0; i < articles.length; i ++) {
			articles[i] = loadArticleFromRow (rows.get(i), props, charset, con);
		}
		return articles;
	}
	
	public static ShortMessage[] loadShortMessagesFromRows (Properties props, Rows rows) {
		rows = rows == null ? new Rows() : rows;
		ShortMessage[] msgs = new ShortMessage[rows.size()];
		for(int i = 0; i < msgs.length; i ++) {
			msgs[i] = loadShortMessageFromRow(props, rows.get(i));
		}
		return msgs;
	}
	
	private static ShortMessage loadShortMessageFromRow(Properties props, Row row) {
		ShortMessage msg = new ShortMessage();
		msg.setId((long) row.get(props.getProperty(Core.DB_SHORT_MESSAGES_ID_KEY)));
		msg.setInserted(((Timestamp) row.get(props.getProperty(Core.DB_SHORT_MESSAGES_INSERTED_KEY))).getTime());
		msg.setCaption((String) row.get(props.getProperty(Core.DB_SHORT_MESSAGES_CAPTION_KEY)));
		msg.setText((String) row.get(props.getProperty(Core.DB_SHORT_MESSAGES_TEXT_KEY)));
		return msg;
	}

	public static Rows getActualArticles(Properties props, Connection con, int limit, long defaultArticle, int daysAgo) throws SQLException {
		String schema = props.getProperty(Core.DB_KEY);
		String table = props.getProperty(Core.DB_ARTICLES_KEY);
		WhereClause where = new WhereClause (
			new CompPred(new Object[]{new Identifier(props.getProperty(Core.DB_ARTICLES_MODIFIED_KEY))}, new Object[]{Utils.getTimestamp(daysAgo, true, Calendar.DAY_OF_YEAR)}, CompPred.GREATHER),
			WhereClause.AND,
			new CompPred(new Object[]{new Identifier(props.getProperty(Core.DB_ARTICLES_ID_KEY))}, new Object[]{defaultArticle}, CompPred.NOT_EQUAL));
		OrderClause order = new OrderClause(new SortSpec(false, new Identifier(props.getProperty(Core.DB_ARTICLES_MODIFIED_KEY))));
		LimitClause limitClause = new LimitClause(new RowCount (new UnsInt(new FixedPointLiteral(limit))));
		return DBConnector.select(con, DBConnector.createSelect().addFromClause(new TableName(new SchemaName(new Identifier(schema)), new Identifier (table))).addTableSpec(where).addSelectSpec(order).addSelectSpec(limitClause));
	}
	
	public static Rows getArticlesForCategory (Properties props, Connection con, int limit, long defaultArticle, long category) throws SQLException {
		String schema = props.getProperty(Core.DB_KEY);
		String table = props.getProperty(Core.DB_ARTICLES_KEY);
		WhereClause where = new WhereClause (
			new CompPred(new Object[]{new Identifier(props.getProperty(Core.DB_ARTICLES_CATEGORY_ID_KEY))}, new Object[]{category}, CompPred.EQUAL),
			WhereClause.AND,
			new CompPred(new Object[]{new Identifier(props.getProperty(Core.DB_ARTICLES_ID_KEY))}, new Object[]{defaultArticle}, CompPred.NOT_EQUAL));
		OrderClause order = new OrderClause(new SortSpec(false, new Identifier(props.getProperty(Core.DB_ARTICLES_MODIFIED_KEY))));
		LimitClause limitClause = new LimitClause(new RowCount (new UnsInt(new FixedPointLiteral(limit))));
		return DBConnector.select(con, DBConnector.createSelect().addFromClause(new TableName(new SchemaName(new Identifier(schema)), new Identifier (table))).addTableSpec(where).addSelectSpec(order).addSelectSpec(limitClause));
	}
	
	public static Rows getActualShortMessages (Properties props, Connection con, int limit, int daysAgo) throws SQLException {
		String schema = props.getProperty(Core.DB_KEY);
		String table = props.getProperty(Core.DB_SHORT_MESSAGES_KEY);
		WhereClause where = new WhereClause (new CompPred(new Object[]{new Identifier(props.getProperty(Core.DB_SHORT_MESSAGES_INSERTED_KEY))}, new Object[]{Utils.getTimestamp(daysAgo, true, Calendar.DAY_OF_YEAR)}, CompPred.GREATHER));
		OrderClause order = new OrderClause(new SortSpec(false, new Identifier(props.getProperty(Core.DB_SHORT_MESSAGES_INSERTED_KEY))));
		LimitClause limitClause = new LimitClause(new RowCount (new UnsInt(new FixedPointLiteral(limit))));
		return DBConnector.select(con, DBConnector.createSelect().addFromClause(new TableName(new SchemaName(new Identifier(schema)), new Identifier (table))).addTableSpec(where).addSelectSpec(order).addSelectSpec(limitClause));
	}
	
	public static Rows getAllRows (Properties props, String table, Connection con) throws SQLException {
		return DBConnector.select(con, DBConnector.createSelect().addFromClause(new TableName(new SchemaName(new Identifier(props.getProperty(Core.DB_KEY))), new Identifier(table))));
	}
	
	private static String getTimestamp(int daysAgo, boolean back, int type) {
		long l = 1;
		switch (type) {
		case Calendar.DAY_OF_YEAR: l *= 24;
		case Calendar.HOUR: l *= 60;
		case Calendar.MINUTE: l *= 60;
		case Calendar.SECOND: l *= 1000;
		}
		l = new Date().getTime() - daysAgo * l * (back ? 1 : -1);
		Timestamp  timestamp = new Timestamp (l);
		return timestamp.toString() ;
	}

	public static Partner[] getPartners(Connection con, Properties props) throws SQLException {
		String schema = props.getProperty(Core.DB_KEY);
		String table = props.getProperty(Core.DB_PARTNERS_KEY);
		Rows rows = DBConnector.select(con, DBConnector.createSelect().addFromClause(new TableName(new SchemaName(new Identifier(schema)), new Identifier(table))));
		Partner[] partners = new Partner[rows.size()];
		table = props.getProperty(Core.DB_PARTNER_TYPES_KEY);
		TableName tableName = new TableName (new SchemaName(new Identifier(schema)), new Identifier(table));
		for(int i = 0; i < rows.size(); i ++) {
			Row row = rows.get(i);
			partners[i] = new Partner();
			partners[i].setId((long) row.get(props.getProperty(Core.DB_PARTNERS_ID_KEY)));
			partners[i].setPhoto_id((long) row.get(props.getProperty(Core.DB_PARTNERS_PHOTO_ID_KEY)));
			partners[i].setName((String) row.get(props.getProperty(Core.DB_PARTNERS_NAME_KEY)));
			partners[i].setUrl((String) row.get(props.getProperty(Core.DB_PARTNERS_URL_KEY)));
			partners[i].setType((String) DBConnector.select(con, DBConnector.createSelect().addFromClause(tableName).addTableSpec(new WhereClause(new CompPred(new Object[]{new Identifier(props.getProperty(Core.DB_PARTNER_TYPES_ID_KEY))}, new Object[]{((long) row.get(props.getProperty(Core.DB_PARTNERS_TYPE_ID_KEY)))}, CompPred.EQUAL)))).get(0).get(props.getProperty(Core.DB_PARTNER_TYPES_NAME_KEY)));
		}
		return partners;
	}

	public static String getCategoryName(Connection con, Properties props, long categoryId) throws SQLException {
		Rows rows = Utils.getRows(con, props.getProperty(Core.DB_KEY), props.getProperty(Core.DB_CATEGORIES_KEY), new WhereClause(new CompPred(new Object[]{props.getProperty(Core.DB_CATEGORIES_ID_KEY)}, new Object[]{categoryId}, CompPred.EQUAL)));
		if(rows.size() == 1) {
			return (String) rows.get(0).get(props.getProperty(Core.DB_CATEGORIES_NAME_KEY));
		}
		return null;
	}

	public static long getCategoryId(Connection con, Properties props, String name) throws SQLException {
		Rows rows = Utils.getRows(con, props.getProperty(Core.DB_KEY), props.getProperty(Core.DB_CATEGORIES_KEY), new WhereClause(new CompPred(new Object[]{new Identifier(props.getProperty(Core.DB_CATEGORIES_NAME_KEY))}, new Object[]{name}, CompPred.EQUAL)));
		if(rows.size() == 1) {
			return (long) rows.get(0).get(props.getProperty(Core.DB_CATEGORIES_ID_KEY));
		}
		return 1;
	}
	
}
