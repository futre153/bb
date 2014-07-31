package com.acepricot.finance.sync;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.acepricot.finance.sync.share.AppConst;
import com.acepricot.finance.sync.share.JSONMessage;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

/**
 * Servlet implementation class AceJSONInOutRcvr
 */
public class AceJSONInOutRcvr extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static final Gson GSON = new Gson();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AceJSONInOutRcvr() {
        super();
        // TODO Auto-generated constructor stub
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doAction(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doAction(request, response);
	}
	
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doAction(request, response); 
	}
	
	private static final void doAction(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.setContentType(req.getContentType());
		try {
			doCheck(req, res);
			if(req.getMethod().equals(AppConst.GET_METHOD)) {
				doAction(req, res, doGetJSON(req));
			}
			else if (req.getMethod().equals(AppConst.POST_METHOD)) {
				doAction(req, res, doPostJSON(req));
			}
			else if (req.getMethod().equals(AppConst.PUT_METHOD)) {
				doAction(req, res, doPutJSON(req));
			}
			else {
				throw new IOException(AppError.getMessage(AppError.HTTP_METHOD_ERROR, req.getMethod()));
			}
		}
		catch(IOException e) {
			doMessage(res, new JSONMessage().sendAppError(e));
		}
	}
	
	private static final void doCheck(HttpServletRequest req, HttpServletResponse res) throws IOException {
		boolean jsonct = req.getContentType().contains(HttpConst.JSON_CT);
		boolean h2dbct = req.getContentType().contains(HttpConst.H2DB_CT);
		boolean get = req.getMethod().equals(AppConst.GET_METHOD);
		boolean post = req.getMethod().equals(AppConst.POST_METHOD);
		boolean put = req.getMethod().equals(AppConst.PUT_METHOD);
		if(!((jsonct & (post | get)) | (put & h2dbct))) {
			throw new IOException(AppError.getMessage(AppError.HTTP_CT_ERROR, req.getContentType(), req.getMethod()));
		}
	}
	
	private static JSONMessage doPutJSON(HttpServletRequest req) throws IOException {
		Cookie[] c = req.getCookies();
		String id = null;
		String uri = null;
		boolean last = false;
		for (int i = 0; i < c.length; i ++) {
			id = c[i].getName();
			uri = c[i].getValue();
			if(uri.matches(AppConst.JSON_LAST_URI_MASK)) {
				last = true;
				uri = uri.substring(AppConst.LAST_URI_SIGN.length());
			}
			if(id != null && uri != null) { 
				if(id.matches(AppConst.JSON_ID_MASK) && uri.matches(AppConst.JSON_URI_MASK)) {
					break;
				}
			}
			id = null;
			uri = null;
		}
		if(id != null) {
			return new JSONMessage(AppConst.JSON_UPLOAD_VALUE, new Object[] {id, uri, req.getInputStream(), last});
		}
		else {
			throw new IOException(AppError.getMessage(AppError.HTTP_ID_PARAM_ERROR, AppConst.JSON_ID_MASK, req.getMethod()));
		}
	}
	
	private static final JSONMessage doPostJSON(HttpServletRequest req) throws IOException {
		try {
			String enc = req.getHeader(HttpConst.CONTENT_ENCODING);
			InputStream in = req.getInputStream();
			if(enc != null) {
				if(enc.equals(HttpConst.GZIP)) {
					in = new GZIPInputStream(in);	
				}
				else {
					throw new IOException ("Unknown content encoding");
				}
			}
			return GSON.fromJson(new InputStreamReader(in, req.getCharacterEncoding()), JSONMessage.class);
		} catch (JsonSyntaxException | JsonIOException | IOException e) {
			throw new IOException (e);
		} 
	}
	
	private static final JSONMessage doGetJSON(HttpServletRequest req) throws IOException {
		String header = req.getParameter(AppConst.JSON_HEADER_KEY);
		if(header == null) {
			throw new IOException(AppError.getMessage(AppError.HTTP_PARAM_ERROR, AppConst.JSON_HEADER_KEY));
		}
		JSONMessage msg = new JSONMessage(header);
		String body = req.getParameter(AppConst.JSON_BODY_KEY);
		if(body == null) {
			body = "[]";
		}
		try {
			msg.setBody(GSON.fromJson(body, Object[].class));
		}
		catch(JsonSyntaxException e) {
			throw new IOException (e);
		}
		return msg;
	}
	
	private static final void doMessage(HttpServletResponse res, JSONMessage msg) throws IOException {
		if(msg.getHeader() == null) {
			res.setContentType(HttpConst.H2DB_CT);
			for(int i = 3; i < msg.getBody().length; i += 2) {
				if((i + 1) < msg.getBody().length) {
					Cookie c = new Cookie((String) msg.getBody()[i], (String) msg.getBody()[i + 1]);
					c.setHttpOnly(true);
					c.setVersion(0);
					res.addCookie(c);
				}
			}
			res.getOutputStream().write((byte[]) msg.getBody()[1], 0, (int) msg.getBody()[2]);
		}
		else {
			res.setContentType(HttpConst.JSON_CT);
			res.getWriter().append(GSON.toJson(msg));
		}
		res.flushBuffer();
	}
	
	private static final void doAction(HttpServletRequest req, HttpServletResponse res, JSONMessage msg) throws IOException {
		try {
			doMessage(res, JSONMessageProcessor.getInstance().process(msg));
		} catch (IOException e) {
			throw new IOException (e);
		}
	}
}
