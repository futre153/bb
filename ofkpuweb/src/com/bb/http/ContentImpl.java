package com.bb.http;

import java.io.IOException;
import java.io.InputStream;

abstract class ContentImpl implements Content {

	private static final String NULL_MEDIA_TYPE = "Media type cannot have a null value";
	private static final char MEDIA_TYPE_SEPARATOR = '/';
	private static final char PARAM_SEPARATOR = ';';
	private static final String MISSING_SEPARATOR = "Missing separator between mesia type and subtype";
	private static final String NULL_MEDIA_SUBTYPE = "Media subtype cannot have a null value";
	private static final String INLINE_DISP_TYPE = "inline";
	private static final String ATTACHMENT_DISP_TYPE = "attachment";
	protected static final String IMAGE_CT = "image";
	protected static final String FORM_DATA_CD = "form-data";
	public static final String CHARSET_PM = "cahrset";
	private InputStream in;
	private String type;
	private String subtype;
	private Parameters contentTypeParams = new Parameters();
	private long length;
	private String dispType;
	private Parameters contentDispParams = new Parameters();
	
	
	@Override
	public void setInputStream(InputStream in) {
		this.in = in;
	}

	@Override
	public void setContentType(String type) throws IOException {
		CoreRules br = CoreRules.getInstance(type);
		this.type = CoreRules.read(br, CoreRules.TOKEN, 0, null);
		if(type.length() == 0) {
			throw new IOException(NULL_MEDIA_TYPE);
		}
		if(CoreRules.read(br, CoreRules.EXACT_STRING, 0, new String(new char[]{MEDIA_TYPE_SEPARATOR})).length() != 1) {
			throw new IOException(MISSING_SEPARATOR);
		}
		subtype = CoreRules.read(br, CoreRules.TOKEN, 0, null);
		if(subtype.length() == 0) {
			throw new IOException (NULL_MEDIA_SUBTYPE);
		}
		while (true) {
			CoreRules.read(br, CoreRules.WSP, 0, null);
			if(CoreRules.read(br, CoreRules.EXACT_STRING, 0, new String(new char[]{PARAM_SEPARATOR})).length() == 1) {
				CoreRules.read(br, CoreRules.WSP, 0, null);
			}
			else {
				break;
			}
			Parameters.addParameters (getContentTypeParams(), br);
		}
		
		
	}

	@Override
	public void setContentDisposition(String disp) throws IOException {
		CoreRules cr = CoreRules.getInstance(disp);
		int p = cr.getPointer();
		String type = getDispositionType(cr);
		if(type.length() > 0) {
			setDispType(type);
			while (true) {
				CoreRules.read(cr, CoreRules.WSP, 0, null);
				if(CoreRules.read(cr, CoreRules.EXACT_STRING, 0, new String(new char[]{PARAM_SEPARATOR})).length() == 1) {
					CoreRules.read(cr, CoreRules.WSP, 0, null);
				}
				else {
					break;
				}
				Parameters.addParameters (getContentDispParams(), cr);
			}	
		}
		else {
			cr.setPointer(p);
		}
	}

	private String getDispositionType(CoreRules cr) throws IOException {
		int p = cr.getPointer();
		String type = CoreRules.read(cr, CoreRules.EXACT_STRING, 0, INLINE_DISP_TYPE);
		if(type.length() == 0) {
			cr.setPointer(p);
		}
		type = CoreRules.read(cr, CoreRules.EXACT_STRING, 0, ATTACHMENT_DISP_TYPE);
		if(type.length() == 0) {
			cr.setPointer(p);
		}
		return CoreRules.read(cr, CoreRules.TOKEN, 0, null);
	}

	@Override
	public void setContentLength(long length) {
		this.length = length;
	}

	public Parameters getContentTypeParams() {
		return contentTypeParams;
	}

	public void setContentTypeParams(Parameters contentTypeParams) {
		this.contentTypeParams = contentTypeParams;
	}

	public InputStream getInputStream() {
		return in;
	}

	public String getDispType() {
		return dispType;
	}

	public void setDispType(String dispType) {
		this.dispType = dispType;
	}

	public Parameters getContentDispParams() {
		return contentDispParams;
	}

	public void setContentDispParams(Parameters contentDispParams) {
		this.contentDispParams = contentDispParams;
	}

	public String getContentType() {
		return type;
	}

	public String getMime() {
		return (type != null && subtype != null) ? (type + MEDIA_TYPE_SEPARATOR + subtype) : null;
	}

}
