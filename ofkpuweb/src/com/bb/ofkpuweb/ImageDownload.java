package com.bb.ofkpuweb;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pabk.web.db.DBConnector;

import com.bb.commons.ChunkedInputStream;
import com.bb.commons.Photo;

/**
 * Servlet implementation class ImageDownload
 */
@WebServlet("/image")
public class ImageDownload extends Core {
	private static final long serialVersionUID = 1L;
	private static final int NOT_FOUND = 404;
	private static final String PARAM_IDI_NAME = "idi";
	private static final String ERROR_PARAMETER_IDI_KEY = "id.error.idi";
	private static final int HTTP_OK = 200;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ImageDownload() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection con = null;
		InputStream in = null;
		Photo photo = null;
		Properties props = getProperties();
		try {
			String id = request.getParameter(PARAM_IDI_NAME);
			if(id == null) {
				throw new ServletException(props.getProperty(ERROR_PARAMETER_IDI_KEY));
			}
			int index = Integer.parseInt(id);
			con = DBConnector.lookup(props.getProperty(DSN_KEY));
			photo = Utils.getPhotoFromDb (props, con, index);
			//in = new ChunkedInputStream(photo.getInputStream());
			in = photo.getInputStream();
			response.setContentType(photo.getMime());
			//response.addHeader(Core.TRANSFER_ENCODING_HDR, Core.CHUNKED_ENCODING);
			Utils.write(in, response.getOutputStream());
			//response.setStatus(HTTP_OK);
		}
		catch (Exception e) {
			response.sendError(NOT_FOUND);
			
		}
		finally {
			if (con != null) {try {con.close();} catch (SQLException e) {}}
			if (photo != null) {try {photo.free();} catch (SQLException e1) {}}
			if (in != null) {try {in.close();} catch (IOException e) {}}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
