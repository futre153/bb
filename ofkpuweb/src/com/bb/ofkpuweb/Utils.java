package com.bb.ofkpuweb;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.servlet.ServletOutputStream;

import org.pabk.emanager.sql.sap.CompPred;
import org.pabk.emanager.sql.sap.FixedPointLiteral;
import org.pabk.emanager.sql.sap.Identifier;
import org.pabk.emanager.sql.sap.LimitClause;
import org.pabk.emanager.sql.sap.OrderClause;
import org.pabk.emanager.sql.sap.Query;
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
import com.bb.commons.Photo;

final class Utils {
	
	private static final Object BR = "<br/>";
	private static final int MAX_READED_BYTES = 2048;
	
	private Utils(){};
		
	static Rows getRows (Connection con, String schema, String table, WhereClause where) throws SQLException {
		return DBConnector.select(con, DBConnector.createSelect().addFromClause(new TableName(new SchemaName(new Identifier(schema)), new Identifier(table))).addTableSpec(where));
	}
	
	private static Article loadArticleFromRow (Row row, Properties props, String charset, Connection con) throws IOException, SQLException {
		Article article = new Article((int) row.get(props.getProperty(Core.DB_ARTICLES_ID_KEY)));
		article.setCaption((String) row.get(props.getProperty(Core.DB_ARTICLES_CAPTION_KEY)));
		article.setContent(Utils.readCharsToHtml(getClobReader(row, props.getProperty(Core.DB_ARTICLES_CONTENT_KEY), charset)));
		article.setModified(((Timestamp) row.get(props.getProperty(Core.DB_ARTICLES_MODIFIED_KEY))).getTime());
		article.setPhotos(loadPhotosFromRows(getPhotosForAtricle(con, props, (int) article.getIndex()), props));
		Utils.freeXlob (row, props.getProperty(Core.DB_ARTICLES_CONTENT_KEY));
		return article;
	}
	
	private static Rows getPhotosForAtricle(Connection con, Properties props, int index) throws SQLException {
		String schema = props.getProperty(Core.DB_KEY);
		String table = props.getProperty(Core.DB_PHOTOS_KEY);
		WhereClause where = new WhereClause(new CompPred(new Object[]{new Identifier(props.getProperty(Core.DB_PHOTOS_ARTICLE_ID_KEY))}, new Object[]{index}, CompPred.EQUAL));
		return Utils.getRows(con, schema, table, where);
	}
	
	private static Photo[] loadPhotosFromRows (Rows rows, Properties props) {
		rows = rows == null ? new Rows() : rows;
		Photo[] photos = new Photo[rows.size()];
		for (int i = 0; i < photos.length; i ++) {
			photos[i] = loadPhotoFromRow (rows.get(i), props);
		}
		return photos;
	}
	
	
	private static Photo loadPhotoFromRow(Row row, Properties props) {
		// TODO Auto-generated method stub
		return null;
	}

	public static Article getArticle(Properties props, Connection con, int index, String charset) throws SQLException, IOException {
		String schema = props.getProperty(Core.DB_KEY);
		String table = props.getProperty(Core.DB_ARTICLES_KEY);
		WhereClause where = new WhereClause(new CompPred(new Object[]{new Identifier(props.getProperty(Core.DB_ARTICLES_ID_KEY))}, new Object[]{index}, CompPred.EQUAL));
		return loadArticleFromRow (Utils.getRows(con, schema, table, where).get(0), props, charset, con);
	}

	private static void freeXlob(Row row, String column) throws SQLException {
		Object obj = row.get(column);
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

	private static String readCharsToHtml (Reader reader) throws IOException {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(reader);
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

	public static Photo getPhotoFromDb (Properties props, Connection con, int id) throws SQLException {
		Photo photo = new Photo(id);
		String schema = props.getProperty(Core.DB_KEY);
		String table = props.getProperty(Core.DB_PHOTOS_KEY);
		WhereClause where = new WhereClause(new CompPred(new Object[]{new Identifier(props.getProperty(Core.DB_PHOTOS_ID_KEY))}, new Object[]{id}, CompPred.EQUAL));
		Row row = loadPhotoFromRow (Utils.getRows(con, schema, table, where), props)[0];
		photo.setArticleId((long) row.get(props.getProperty(Core.DB_PHOTOS_ARTICLE_ID_KEY)));
		photo.setGalleryId((long) row.get(props.getProperty(Core.DB_PHOTOS_GALLERY_ID_KEY)));
		photo.setData(row.get(props.getProperty(Core.DB_PHOTOS_PHOTO_KEY)));
		photo.setDescription ((String) row.get(props.getProperty(Core.DB_PHOTOS_DESCRIPTION_KEY)));
		photo.setMime ((String) row.get(props.getProperty(Core.DB_PHOTOS_MIME_KEY)));
		return photo;
	}

	public static void write(InputStream in, OutputStream out) throws IOException {
		byte[] b = new byte[MAX_READED_BYTES];
		int i = -1;
		while ((i = in.read(b)) >=0) {
			out.write(b, 0, i);
		}
	}
	
	public static Article[] loadArticlesFromRows (Rows rows, Properties props, String charset) throws IOException, SQLException {
		rows = rows == null ? new Rows() : rows;
		Article[] articles = new Article[rows.size()];
		for (int i = 0; i < articles.length; i ++) {
			articles[i] = loadArticleFromRow (rows.get(i), props, charset);
		}
		return articles;
	}
	
	//private static Rows = 
	
	public static Rows getActualArticles(Properties props, Connection con, int limit, int defaultArticle, int daysAgo) throws SQLException {
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
	
}
