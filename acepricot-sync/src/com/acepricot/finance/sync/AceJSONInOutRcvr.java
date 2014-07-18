package com.acepricot.finance.sync;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		String id = req.getParameter(AppConst.JSON_ID_PARAM_KEY);
		if(id != null) {
			return new JSONMessage(AppConst.JSON_UPLOAD_VALUE, new Object[] {id, req.getInputStream()});
		}
		else {
			throw new IOException(AppError.getMessage(AppError.HTTP_ID_PARAM_ERROR, AppConst.JSON_ID_PARAM_KEY, req.getMethod()));
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
		//System.out.println(res.getContentType());
		//System.out.println(res.getCharacterEncoding());
		//res.setContentLength(GSON.toJson(msg).getBytes(res.getCharacterEncoding()).length);
		res.getWriter().append(GSON.toJson(msg));
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
