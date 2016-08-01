/* 
 * Copyright (C) 2009  "Darwin V. Felix" <dfelix@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
 
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.Configuration;

import org.pabk.net.http.DefaultContent;
import org.pabk.net.http.HttpClientConst;
import org.pabk.net.http.SimpleClient;
import org.pabk.net.http.WSContent;

@SuppressWarnings("unused")
public final class Test {
 
    private Test() {
        // default private
    }
 
    public static void main(final String[] args) throws Exception {
    	
    	
    	/*SimpleClient.getConnection("www.sme.sk");
    	        SimpleClient.getMessage();
    	        String auth=SimpleClient.getTicket();
    	        System.out.println(auth);
    	        SimpleClient.negotiate(auth);
    	        SimpleClient.getMessage();
    	        System.exit(1);
    	   */     
    			//_Stub stub=new _Stub();
    			//stub.requestNotification(stub._temp_loadMsgContext());
    			
    	        /*Authenticator au=new MyAuthenticator();
    			Authenticator.setDefault(au);
    			String text="http://hubert.os.sk/fds_test.asmx?WSDL";
    			String local="http://localhost:8080";
    			
    			Messanger m=new Messanger();
    			m.setURL(text);
    			System.out.println(m.execute(true));
    			InputStream inc=m.getInputStream();
    			int length=m.getContentLength();
    			//byte[] bt=new byte[m.getContentLength()];
    			//inc.read(bt);
    			String enc = m.getContentEndoding();
    			String content=null;
    			//if(enc==null) {content=new String(bt);} else {content=new String(bt,m.getContentEndoding());}
    			InputStreamReader content1= new InputStreamReader(inc);
    			for (int i=0; i != -1; i = content1.read())
    			{
    				System.out.print((char) i);  
    			}
    			
    			m.close();
    			//System.out.println(content);
    			 * 
    			 */
    			//System.exit(1);
    			/*
    			Socket s=new Socket("localhost",8080);
    			InputStream in=s.getInputStream();
    			OutputStream out=s.getOutputStream();
    			*/
    	String nl=System.getProperty("line.separator");
    		/*
    	String body="<?xml version='1.0' encoding='utf-8'?><env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" + nl +
    		"<env:Body>" + nl +
    		"<Notification xmlns=\"\">" + nl +
    		"<MsgType xmlns=\"\">0</MsgType>" + nl +
    		"<InstitutionId xmlns=\"\">6500</InstitutionId>" + nl +
    		"<CardNumber xmlns=\"\">6762414010000055</CardNumber>" + nl +
    		"<NotificationType xmlns=\"\">1</NotificationType>" + nl +
    		"<Contact xmlns=\"\">+421903245465</Contact>" + nl +
    		"<TransactionSource xmlns=\"\">2</TransactionSource>" + nl +
    		"<TransactionType xmlns=\"\">15</TransactionType>" + nl +
    		"<TransactionAmount xmlns=\"\">000000000010.00</TransactionAmount>" + nl +
    		"<TransactionCurrency xmlns=\"\">EUR</TransactionCurrency>" + nl +
    		"<AvailableBalance xmlns=\"\">00000000002.01</AvailableBalance>" + nl +
    		"<AccountCurrency xmlns=\"\">EUR</AccountCurrency>" + nl +
    		"<TransactionDateTime xmlns=\"\">20110726121712</TransactionDateTime>" + nl +
    		"<MerchantName xmlns=\"\">N/A</MerchantName>" + nl +
    		"<TerminalOwnerName xmlns=\"\">Testovanie Modelu</TerminalOwnerName>" + nl +
    		"<MerchantCity xmlns=\"\">Bratislava</MerchantCity>" + nl +
    		"<MerchantState xmlns=\"\">SK</MerchantState>" + nl +
    		"<TxnId xmlns=\"\">3678764500104785922432173419</TxnId>" + nl +
    		"</Notification>" + nl +
    		"</env:Body></env:Envelope>";
    			
    		*/	
    		/*
    			
    			String body=
    				"<env:Envelope" +nl+
    				"xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\""+nl+
    				"xmlns=\"http://www.firstdata.sk/txn-notify/soap/\"" +nl+
    				">"+nl+
    				"<env:Body>"+nl+
    				"<MsgType>0</MsgType>"+nl+
    				"<InstitutionId xmlns=\"\">0000</InstitutionId>"+nl+
    				"<CardNumber xmlns=\"\">6762414010000030</CardNumber>"+nl+
    				"<NotificationType>1</NotificationType>"+nl+
    				"<Contact xmlns=\"\">+421907735794</Contact>"+nl+
    				"<TransactionSource xmlns=\"\">1</TransactionSource>"+nl+
    				"<TransactionType xmlns=\"\">10</TransactionType>"+nl+
    				"<TransactionAmount xmlns=\"\">+00000000010.00</TransactionAmount>"+nl+
    				"<TransactionCurrency xmlns=\"\">001</TransactionCurrency>"+nl+
    				"<AvailableBalance xmlns=\"\">+00000000045.00</AvailableBalance>"+nl+
    				"<AccountCurrency xmlns=\"\">784</AccountCurrency>"+nl+
    				"<TransactionDateTime xmlns=\"\">20151201105024</TransactionDateTime>"+nl+
    				"<MerchantName xmlns=\"\">BRATISLAVA,STUROVA 5</MerchantName>"+nl+
    				"<TerminalOwnerName xmlns=\"\">OTP</TerminalOwnerName>"+nl+
    				"<MerchantCity xmlns=\"\">BRATISLAVA</MerchantCity>"+nl+
    				"<MerchantState xmlns=\"\">SK</MerchantState>"+nl+
    				"<TxnId xmlns=\"\">3894926321376034818124812662</TxnId>"+nl+
    				"<NotApproved xmlns=\"\">APP</NotApproved>"+nl+
    				//"<Language xmlns=\"\">sk</L anguage>"+nl+
    				//"<TerminalId xmlns=\"\">0000000000000001</TerminalId>"+nl+
    			    "</env:Body>"+nl+
    			    "</env:Envelope>"+nl;
    			*/
    	//String body = "<?xml version='1.0' encoding='utf-8'?><env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\"><env:Body><MsgType>0</MsgType><InstitutionId>6500</InstitutionId><CardNumber>6762414010017265</CardNumber><NotificationType>1</NotificationType><Contact>0903245465</Contact><TransactionSource>1</TransactionSource><TransactionType>10</TransactionType><TransactionAmount>+00000000030.00</TransactionAmount><TransactionCurrency>EUR</TransactionCurrency><AvailableBalance>+00000000009.81</AvailableBalance><AccountCurrency>EUR</AccountCurrency><TransactionDateTime>20121224152156</TransactionDateTime><MerchantName>BRATISLAVA,CACHTICKA 25</MerchantName><TerminalOwnerName>POB</TerminalOwnerName><MerchantCity>BRATISLAVA</MerchantCity><MerchantState>SK</MerchantState><TxnId>4200000000000000000253541268</TxnId></env:Body></env:Envelope>";
    			//String body= "<?xml version='1.0' encoding='utf-8'?><env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\"><env:Body><MsgType>0</MsgType><InstitutionId>6500</InstitutionId><CardNumber>6762414010016622</CardNumber><NotificationType>1</NotificationType><Contact>0903245465</Contact><TransactionSource>2</TransactionSource><TransactionType>10</TransactionType><TransactionAmount>+00000000000.40</TransactionAmount><TransactionCurrency>EUR</TransactionCurrency><AvailableBalance>+00000000007.65</AvailableBalance><AccountCurrency>EUR</AccountCurrency><TransactionDateTime>20141124121640</TransactionDateTime><MerchantName>Junacka ul. 6</MerchantName><TerminalOwnerName>Junacka ul. 6</TerminalOwnerName><MerchantCity>Bratislava</MerchantCity><MerchantState>SK</MerchantState><TxnId>4100000000000000001551477626</TxnId></env:Body></env:Envelope>";
    					String body = "<?xml version='1.0' encoding='utf-8'?>"
    					+ "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">"
    					+ 	"<env:Body>"
    					+ 		"<MsgType>6</MsgType>"
    					+ 		"<InstitutionId>6500</InstitutionId>"
    					+ 		"<NotificationType>1</NotificationType>"
    					+ 		"<Contact>+421907735794</Contact>"
    					+ 		"<ProcessDate>28.01.2016</ProcessDate>"
    					+ 		"<CardType />"
    					+ 		"<PaymentAmt>-00000000030.00</PaymentAmt>"
    					+ 		"<PaymentCCY>EUR</PaymentCCY>"
    					+ 		"<PaymentIndicator>5169</PaymentIndicator>"
    					+ 		"<AvailableBalance>+00000000053.07</AvailableBalance>"
    					+ 		"<AccountId>0000244500002544</AccountId>"
    					+ 		"<TraceID />"
    					+ 		"<Timestamp>20160621140500</Timestamp>"
    					+ 	"</env:Body>"
    					+ "</env:Envelope>";
    			
    				/*
    			String body=""+
    				"<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
    				"<env:Body>"+
    			    "<MsgType>6</MsgType>"+
    			    "<InstitutionId>6500</InstitutionId>"+
    			    "<NotificationType>1</NotificationType>"+
    			    "<Contact>+421903245465</Contact>"+
    			    "<ProcessDate>27.07.2015</ProcessDate>"+
    			    "<CardType />"+
    			    "<PaymentAmt>-00000000001.00</PaymentAmt>"+
    			    "<PaymentCCY>EUR</PaymentCCY>"+
    			    "<PaymentIndicator>048</PaymentIndicator>"+
    			    "<AvailableBalance>+00000000001.00</AvailableBalance>"+
    			    "<AccountId>0000002200000011</AccountId>"+
    			    "<TraceID />"+
    			    "<Timestamp>20150727140000</Timestamp>"+
    			  "</env:Body>"+
    			"</env:Envelope>";*/
    		/*
    	
    	String body = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + 
    					"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
    					"<soap:Body>" +
    					"<SendSMS xmlns=\"https://hubert.os.sk\">" +
    					"<MSISDN>421903245465</MSISDN>" +
    					"<Token>Test over http://195.168.11.134/ib_sms_server.asmx</Token>" +
    					"</SendSMS>" +
    					"</soap:Body>" +
    					"</soap:Envelope>";*/
    	//SimpleClient mes = new SimpleClient("http://www.google.sk/", true);
    	//SimpleClient mes = new SimpleClient("http://hubert.os.sk/fds_notification.asmx", true);
    	SimpleClient mes = new SimpleClient("http://sprep02ba/ws/services/notification_test/forward", false);
    	//SimpleClient mes = new SimpleClient("http://P3600X006:8080/PrepApp/services/notification/forward", false);
    	//SimpleClient mes = new SimpleClient("http://p3600x006:8080/PrepApp/ngw_conf/dobi-sms-text.xml", false);
    	//SimpleClient mes = new SimpleClient("http://195.168.11.134/ib_sms_server.asmx", true);
    	//mes.setAuthentication(HttpClientConst.NEGOTIATE_AUTHENTICATION);
		//DefaultContent con=new DefaultContent();
    	//WSContent ws = new WSContent(body, "http://172.27.48.250/Notification");
    	WSContent ws = new WSContent(body, "");
    	//ws.setAction("http://172.27.48.250/Notification");
    	ws.setContent(body);
		InputStream in = mes.execute(ws);
    		    
    			/*
    			String soap_body=
    				"<SOAPBody " +nl+
    				"xmlns=\"http://www.firstdata.sk/txn-notify/soap\"" +nl+
    				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +nl+
    				"xsi:schemaLocation=\"http://www.firstdata.sk/txn-notify/soap http://localhost:8080/axis2/fds_conf/fds_soap.xsd \">"+nl+
    				"<MsgType>0</MsgType>"+nl+
    				"<InstitutionId>0000o</InstitutionId>"+nl+
    				"<CardNumber>6762414010000030</CardNumber>"+nl+
    			    "<NotificationType>2</NotificationType>"+nl+
    			    "<Contact>peter.hapuska@kulahula.sk</Contact>"+nl+
    			    "<ProcessDate>26.05.2011</ProcessDate>"+nl+
    			    "<CardType>000222</CardType>"+nl+
    			    "<PaymentAmt>+00000001111.11</PaymentAmt>"+nl+
    			    "<PaymentCCY>978</PaymentCCY>"+nl+
    			    "<PaymentIndicator>90123456</PaymentIndicator>"+nl+
    			    "<AvailableBalance>+00000055555.52</AvailableBalance>"+nl+
    			    "<AccountId>0000002200000011</AccountId>"+nl+
    			    "<TraceID>123456789</TraceID>"+nl+
    			    "<Timestamp>01140601181000</Timestamp>"+nl+
    			    "</SOAPBody>";
    			
    			
    			try {
    			      // define the type of schema - we use W3C:
    			      //String schemaLang = "http://www.w3.org/2001/XMLSchema";
    				String schemaLang = XMLConstants.W3C_XML_SCHEMA_NS_URI;

    			      // get validation driver:
    			      SchemaFactory factory = SchemaFactory.newInstance(schemaLang);

    			      // create schema by reading it from an XSD file:
    			      Schema schema = factory.newSchema(new File(XSD));
    			      Validator validator = schema.newValidator();

    			      // at last perform validation:
    			      validator.validate(new StreamSource(new ByteArrayInputStream(soap_body.getBytes())));

    			    }catch (SAXException ex) {
    			      ex.printStackTrace();
    			      System.exit(1);
    			    } catch (Exception ex) {
    			      ex.printStackTrace();
    			      System.exit(1);
    			    }

    			System.out.println("Parsing OK");
    			System.exit(1);
    			*/
    			//_Stub stub=new _Stub();
    			//stub.requestNotification(stub._temp_loadMsgContext());
    			
    			
    			//out.write((header+body).getBytes());
    			//System.out.println(header+body);
    			
    			int b;
    			while((b=in.read())>=0) {
    				System.out.print(new Character((char) b));
    			}
    			System.out.println();
    			mes.close();
    	
    	
    	/*
    	File f = new File("C:\\Users\\Brandys\\Desktop\\spnego.txt");
    	InputStream in = new FileInputStream(f);
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	int b;
    	while((b = in.read()) >= 0) {
    		out.write(b);
    	}
    	in.close();
    	in = new ByteArrayInputStream(Base64Coder.decode(out.toString()));
    	Spnego spnego = new Spnego();
    	BERIOStream io = new BERIOStream();
    	io.setInputStream(in);
    	spnego.decode(io);
    	BER spnegoOID = spnego.getCHildNode(0);
    	BER initToken = spnego.getCHildNode(1);
    	System.out.println("SPNEGO OID = " + spnego.getContentDecoder().decodeValue(spnegoOID));
    	BER mechTypes = initToken.getCHildNode(0).getCHildNode(0);
    	int i = 0;
    	while (true) {
    		try {
    			System.out.println("mech " + (i + 1) + ": " + spnego.getContentDecoder().decodeValue((mechTypes.getCHildNode(0).getCHildNode(i))));
    		}
    		catch(Exception e) {
    			//e.printStackTrace();
    			break;
    		}
    		i ++;
    	}
    	BERIOStream io2 = new BERIOStream();
    	io2.setInputStream(new ByteArrayInputStream(initToken.getCHildNode(0).getCHildNode(1).getCHildNode(0).getValue()));
    	NegToken token = new NegToken();
    	token.decode(io2);
    	BER encPart = token.getCHildNode(2).getCHildNode(0).getCHildNode(3).getCHildNode(0).getCHildNode(0).getCHildNode(3).getCHildNode(0);
    	BER encType = encPart.getCHildNode(0).getCHildNode(0);
    	BER encKey = encPart.getCHildNode(1).getCHildNode(0);
    	BER encData = encPart.getCHildNode(2).getCHildNode(0);
    	System.out.println("Enc-part encoding algorithm: " + Arrays.toString(encType.getValue()));
    	System.out.println("Enc-part secret key ?: " + Arrays.toString(encKey.getValue()));
    	System.out.print("Enc-part encoding algorithm: ");
    	for(i = 0; i < encData.getValue().length; i++) {
    		System.out.print(String.format("%2s", Integer.toHexString(encData.getValue()[i] & 0xFF)).replaceAll(" ", "0"));
    	}
    	System.out.println();
    	System.out.println("OK, " + String.format("%2s", Integer.toHexString(0)).replaceAll(" ", "0"));
    	*/
    	
    	/*
    	// Domain (pre-authentication) account
        final String username = "brandys";
        
        // Password for the pre-auth acct.
        final String password = "Nikoleta-1";
        
        // Name of our krb5 config file
        final String krbfile = "D:\\Temp\\krb5.conf";
        
        // Name of our login config file
        final String loginfile = "D:\\Temp\\login.conf";
        
        
        
        
        
        
        // Name of our login module
        final String module = "com.sun.security.jgss.krb5.login";
        
        // set some system properties
        System.setProperty("java.security.krb5.conf", krbfile);
        System.setProperty("java.security.auth.login.config", loginfile);
        //System.setProperty("sun.security.krb5.debug", "true");
        
        
        HTTPClient client = new HTTPClient();
        client.setURL("https://p3600x006:8443/PrepAppClient/login");
        int status = client.execute(false);
        System.out.println(status);
        
        // assert 
        Test.validate(username, password, krbfile, loginfile, module);
 
        final CallbackHandler handler = 
        Test.getUsernamePasswordHandler(username, password);
 
        final LoginContext loginContext = new LoginContext(module, handler);
 
        // attempt to login
        loginContext.login();
 
        // output some info
        System.out.println("Subject=" + loginContext.getSubject());
 
        // logout
        loginContext.logout();
 
       System.out.println("Connection test successful.");*/
    }
 
    private static void validate(final String username, final String password
        , final String krbfile, final String loginfile, final String moduleName) 
        throws FileNotFoundException, NoSuchAlgorithmException {
 
        // confirm username was provided
        if (null == username || username.isEmpty()) {
            throw new IllegalArgumentException("Must provide a username");
        }
 
        // confirm password was provided
        if (null == password || password.isEmpty()) {
            throw new IllegalArgumentException("Must provide a password");
        }
 
        // confirm krb5.conf file exists
        if (null == krbfile || krbfile.isEmpty()) {
            throw new IllegalArgumentException("Must provide a krb5 file");
        } else {
            final File file = new File(krbfile);
            if (!file.exists()) {
                throw new FileNotFoundException(krbfile);
            }
        }
 
        // confirm loginfile
        if (null == loginfile || loginfile.isEmpty()) {
            throw new IllegalArgumentException("Must provide a login file");
        } else {
            final File file = new File(loginfile);
            if (!file.exists()) {
                throw new FileNotFoundException(loginfile);
            }
        }
 
        // confirm that runtime loaded the login file
        final Configuration config = Configuration.getConfiguration();
 
        // confirm that the module name exists in the file
        if (null == config.getAppConfigurationEntry(moduleName)) {
            throw new IllegalArgumentException("The module name " 
                    + moduleName + " was not found in the login file");
        }        
    }
 
    private static CallbackHandler getUsernamePasswordHandler(
        final String username, final String password) {
 
        final CallbackHandler handler = new CallbackHandler() {
            public void handle(final Callback[] callback) {
                for (int i=0; i<callback.length; i++) {
                    if (callback[i] instanceof NameCallback) {
                        final NameCallback nameCallback = (NameCallback) callback[i];
                        nameCallback.setName(username);
                    } else if (callback[i] instanceof PasswordCallback) {
                        final PasswordCallback passCallback = (PasswordCallback) callback[i];
                        passCallback.setPassword(password.toCharArray());
                    } else {
                        System.err.println("Unsupported Callback: " 
                                + callback[i].getClass().getName());
                    }
                }
            }
        };
 
        return handler;
    }
}