package org.pabk.emanager.parser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.pabk.emanager.db.DBConnector;
import org.pabk.emanager.sql.sap.Identifier;
import org.pabk.emanager.sql.sap.SchemaName;
import org.pabk.emanager.sql.sap.TableName;
import org.pabk.emanager.util.Base64Coder;
import org.pabk.emanager.util.Const;



public class EMessageParser {
	
	private static final String MAIL_TABLE_NAME="MAIL_MSGS";
	private static final String DB_SCHEMA_NAME = "SWNT2";
	private static final String DB_NAME = "SWNT2";
	
	public static final String ID="ID";
	public static final String STAT="STATUS";
	public static final String RECS="RECIPIENTS";
	public static final String SUBJ="SUBJECT";
	public static final String BODY="BODY";
	public static final String ATT1="ATTACHMENT1";
	public static final String ATT2="ATTACHMENT2";
	public static final String TIME="TIME_STAMP";
	public static final String ENCG="ENCODING";
	
	
	@SuppressWarnings("unused")
	private static final String[] MAIL_MESSAGE_FIELDS={
		STAT,RECS,SUBJ,BODY,ATT1,ATT2,TIME,ENCG
	};
	
	public static void addMessage(String[] comp) {
		ArrayList<String> header=new ArrayList<String>();
		ArrayList<String> values=new ArrayList<String>();
		//System.out.println("MAIL MESSAGE BEING ADDED");
		header.add(STAT);
		values.add(Const.NOT_EXECUTED);
		
		header.add(RECS);
		values.add(comp[0]);
		
		if(comp[1]!=null) {
			header.add(SUBJ);
			//values.add(Base64Coder.encodeString(comp[1]));
			try {
				values.add(new String(Base64Coder.encode(comp[1].getBytes(comp[5]))));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(comp[2]!=null) {
			header.add(BODY);
			try {
				values.add(new String(Base64Coder.encode(comp[2].getBytes(comp[5]))));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(comp[3]!=null) {
			header.add(ATT1);
			values.add(Base64Coder.encodeString(comp[3]));
		}
		
		if(comp[4]!=null) {
			header.add(ATT2);
			values.add(Base64Coder.encodeString(comp[4]));
		}

		header.add(TIME);
		values.add(new Timestamp(Calendar.getInstance().getTimeInMillis()).toString());
		
		header.add(ENCG);
		values.add(comp[5]);
		
		String[] h=new String[header.size()];
		String[] v=new String[values.size()];		
		
		try {
			commonInsert(DB_NAME, DB_SCHEMA_NAME, MAIL_TABLE_NAME, h, v);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	static void commonInsert(String dsn, String schemaName, String tableName, String[] columns, String[] values) throws SQLException {
		Connection con = null;
		Exception error = null;
		try {
			con = DBConnector.lookup(dsn);
			con.setAutoCommit(false);
			org.pabk.emanager.sql.sap.Insert insert = DBConnector.createInsert (new TableName(new SchemaName(new Identifier(schemaName)), new Identifier(tableName)), (Object) values, columns);
			DBConnector.insert(con, insert, true);
		}
		catch(Exception e) {
			error = e;
			try {if(con != null) con.rollback();} catch(Exception e1){}
		}
		finally {
			try {if(con != null) con.setAutoCommit(true);}catch(Exception e){}
			try {if(con != null) con.close();}catch(Exception e){}
		}
		if(error != null) {
			throw new SQLException (error);
		}
	}
	
	
	
	
	/*
	public static ArrayList<Hashtable<String, Object>> loadMessages(Object locker) {

		
		ArrayList<Hashtable<String, Object>> obj=new ArrayList<Hashtable<String, Object>>();
		try {
			DBConnector db=DBConnector.getDb(true,locker);
			db.select(getMailTableName(), null, "WHERE "+STAT+" = '0'");
			while(true) {
				try {
					Object[] res=null;
					try {
						db.next();
						res=db.getRow(Sys.join(new String[]{ID}, MAIL_MESSAGE_FIELDS));
					}
					catch(Exception e) {break;}
					Hashtable<String, Object> table=new Hashtable<String, Object>();
					table.put(ID, res[0]);
					//System.out.println("ID\t: "+ID+"='"+res[0]+"'");
					for(int i=1;i<res.length;i++) {
						//System.out.println(i+"\t: "+MAIL_MESSAGE_FIELDS[i-1]+"='"+res[i]+"'");
						table.put(MAIL_MESSAGE_FIELDS[i-1], res[i]==null?"":res[i]);
					}
					obj.add(table);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			db.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		//System.exit(1);
		return obj;
		
	}
*/
	public static MimeMessage createEMailMsg(Hashtable<String, Object> table, Properties pro) throws MessagingException, IOException {
				
		MimeMessage email=new MimeMessage(Session.getDefaultInstance(pro,null));
		String enc=(String) table.get(ENCG);
		email.setRecipients(Message.RecipientType.TO, Base64Coder.decodeString((String)table.get(RECS)));
		//email.setFrom(new InternetAddress("branislav.brandys@pabk.sk"));
		//email.addRecipient(Message.RecipientType.TO, new InternetAddress("branislav.brandys@pabk.sk"));
		//System.out.println("Subject="+Base64Coder.decodeString((String) table.get(SUBJ)));
		String subj=new String(Base64Coder.decode((String) table.get(SUBJ)),enc);
		email.setSubject(subj,enc);
		//email.setSubject(MimeUtility.encodeText((String) table.get(SUBJ), (String) table.get(ENCG), "B"));
		MimeMultipart mp=new MimeMultipart();
		addAttachment(mp,(String) table.get(ATT1),enc);
		addAttachment(mp,(String) table.get(ATT2),enc);
		email.setSentDate(new Date());
		MimeBodyPart mbp=new MimeBodyPart();
		String text=new String(Base64Coder.decode((String) table.get(BODY)),enc);
		mbp.setText(text,enc,"plain");
		mp.addBodyPart(mbp);
		
		//testovanie prijemcov
		/*
		mbp=new MimeBodyPart();
		mbp.attachFile("recipients.txt");
		mbp.setText(Base64Coder.decodeString((String)table.get(RECS)));
		mp.addBodyPart(mbp);
		
		*/
		
		email.setContent(mp);
		try {
			Transport.send(email);
		}
		catch(Exception e) {
			throw new IOException(e);
		}
		return email;
		
	}

	private static void addAttachment(MimeMultipart mp, String s, String enc) throws IOException, MessagingException {
		//System.out.println("Add attachment: "+(s!=null && s.length()!=0)+":"+Base64Coder.decodeString(s));
		if(s!=null && s.length()!=0) {
			String[] atts=Base64Coder.decodeString(s).split(",");
			String fileName=atts[0];
			for(int i=1;i<atts.length-1;i++) {
				fileName+=(","+atts[i]);
			}
			MimeBodyPart mbp=new MimeBodyPart();
			mbp.attachFile("tmp\\"+atts[atts.length-1]);
			mbp.setFileName(fileName);
			mp.addBodyPart(mbp);
		}
	}
/*
	public static void setStatus(Hashtable<String, Object> table, int i) throws SQLException {
		DBConnector.getDb(false,null).update(
			getMailTableName(),
			new String[]{EMessageParser.STAT},
			new String[]{Integer.toString(i)},
			"WHERE "+EMessageParser.ID+" = '"+table.get(ID)+"'");
	}
*/
	public static String getMailTableName() {
		return MAIL_TABLE_NAME;
	}


}
