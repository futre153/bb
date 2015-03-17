package org.pabk.web;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class PrepAppServlet
 */
@WebServlet("/PrepAppServlet")
public class PrepAppServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String HTTP_OBJECT_MASK = PrepAppServlet.class.getPackage().getName() + ".%s%s" + HtmlObject.HTML_OBJECT_EXTENSION;
	private static final String DEFAULT_RESOURCE = "main";
	private static final String REFRESH = "refresh";
	private boolean refresh = true;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PrepAppServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			getGetPage(request, response);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new IOException(e);
		}
	}
	
	private void getGetPage(HttpServletRequest req, HttpServletResponse res) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		String resource = req.getRequestURI().replace(req.getContextPath(), "");
		InputStream in = req.getServletContext().getResourceAsStream(resource);
		Flushable out = null;
		HtmlObject content = null;
		if(in == null) {
			if(refresh) {
				content = (HtmlObject) Class.forName(String.format(HTTP_OBJECT_MASK, REFRESH.substring(0, 1).toUpperCase(), REFRESH.substring(1).toLowerCase())).newInstance();
				refresh = false;
			}
			else {
				content = (HtmlObject) Class.forName(String.format(HTTP_OBJECT_MASK, DEFAULT_RESOURCE.substring(0, 1).toUpperCase(), DEFAULT_RESOURCE.substring(1).toLowerCase())).newInstance();
				refresh = true;
			}
			out = res.getWriter();
		}
		else {
			content = new StreamedHtmlObject(req.getContentType(), in);
			out = res.getOutputStream();
		}
		content.doFinal((Closeable) out);
		out.flush();
	}
	
	
	private void getPostPage(HttpServletRequest req, HttpServletResponse res) throws InstantiationException, IllegalAccessException, ClassNotFoundException, UnsupportedOperationException, IOException {
		String resource = req.getRequestURI().replace(req.getContextPath(), "");
		InputStream in = req.getServletContext().getResourceAsStream(resource);
		Map<String, String[]> params = req.getParameterMap();
		Flushable out = null;
		HtmlObject content = null;
		if(in == null) {
			try {
				content = (HtmlObject) Class.forName(String.format(HTTP_OBJECT_MASK, resource.substring(0, 1).toUpperCase(), resource.substring(1).toLowerCase())).newInstance();
			}
			catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				content = (HtmlObject) Class.forName(String.format(HTTP_OBJECT_MASK, REFRESH.substring(0, 1).toUpperCase(), REFRESH.substring(1).toLowerCase())).newInstance();
				refresh = false;
			}
			content = content.action(params);
			out = res.getWriter();
		}
		else {
			content = new StreamedHtmlObject(req.getContentType(), in);
			out = res.getOutputStream();
		}
		content.doFinal((Closeable) out);
		out.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			getPostPage(request, response);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new IOException(e);		}
	}
}
