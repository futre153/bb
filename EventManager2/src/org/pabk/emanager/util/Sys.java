package org.pabk.emanager.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.Properties;

import org.pabk.emanager.Loader;
import org.pabk.emanager.Sleeper;

import ch.qos.logback.classic.Logger;

public class Sys {
	private static final Object parse(Class<?> _class, String value, String key, String handlerName) throws IOException {
		try {
			if(_class.equals(String.class)) {
				return value;
			}
			else if(_class.equals(int.class)) {
				try {
					return Integer.parseInt(value);
				}
				catch(Exception e) {
					throw new IOException(String.format(Const.PROPERTY_PARSE_INTEGER_ERROR, key, value, handlerName));
				}
			}
			else if(_class.equals(byte.class)) {
				try {
					return (byte) Integer.parseInt(value);
				}
				catch(Exception e) {
					throw new IOException(String.format(Const.PROPERTY_PARSE_BYTE_ERROR, key, value, handlerName));
				}
			}
			else if(_class.equals(long.class)) {
				try {
					return Long.parseLong(value);
				}
				catch(Exception e) {
					throw new IOException(String.format(Const.PROPERTY_PARSE_LONG_ERROR, key, value, handlerName));
				}
			}
			else {
				throw new IOException("");
			}
		}
		catch(Exception e) {
			throw new IOException (e);
		}
	}
	
	
	public static final Object getProperty(Object object, String key, String _default, boolean notNull, Class<?> _class, String separator) throws IOException {
		Properties p = null;
		Logger log = null;
		String msg;
		String className = Loader.class.getSimpleName();
		String sep = separator == null ? Const.DEFAULT_PROPERTY_SEPARATOR : separator;
		//log = (Logger) object.getClass().getDeclaredField(Const.LOGGER_FIELD_NAME).get(object);
		//p = (Properties) object.getClass().getDeclaredField(Const.PROPERTIES_FIELD_NAME).get(object);
		try {
			if(object == null) {
				throw new IOException(String.format(Const.NULL_PROPERTIES_ERROR, className)); 
			}
			if(object instanceof Properties) {
				p = (Properties) object;
			}
			else {
				className = null;
				Class<?> clazz = object instanceof Class ? (Class<?>) object : object.getClass();
				className = clazz.getSimpleName();
				if(object instanceof Class) {
					
					Class<?> clazz = (Class<?>) object;
					className = clazz.getSimpleName();
					p = (Properties) clazz.getDeclaredField(Const.PROPERTIES_FIELD_NAME).get(object);
					try {
						log = (Logger) clazz.getDeclaredField(Const.LOGGER_FIELD_NAME).get(object);
					}
					catch (Exception e) {}
				}
				else {
					className = object.getClass().getSimpleName();
					p = (Properties) object.getClass().getDeclaredField(Const.PROPERTIES_FIELD_NAME).get(object);
					try {
						log = (Logger) object.getClass().getDeclaredField(Const.LOGGER_FIELD_NAME).get(object);
					}
					catch (Exception e) {}
				}
			}
			if(p == null) {
				throw new IOException(String.format(Const.NULL_PROPERTIES_ERROR, className));
			}
			if(key == null) {
				throw new IOException(String.format(Const.KEY_PROPERTY_ERROR));
			}
			String value = p.getProperty(key);
			if(value == null) {
				if(_default == null && notNull) {
					throw new IOException (String.format(Const.PROPERTY_NULL_ERROR, key));
				}
				value = _default;
				msg = String.format(Const.DEFAULT_PROPERTY_SET, value, key);
				if(log == null) {
					System.out.println(msg);
				}
				else {
					log.info(msg);
				}
				if(value == null) {
					return value;
				}
			}
			else {
				msg = String.format(Const.PROPERTY_SET, value, key);
				if(log == null) {
					System.out.println(msg);
				}
				else {
					log.info(String.format(Const.PROPERTY_SET, value, key));
				}
			}
			try {
				return parse(_class, value, key, className);
			}
			catch (IOException e) {
				try {
					return _class.getDeclaredConstructor(String.class).newInstance(value);
				} catch (Exception e1) {
					try {
						String[] array = value.split(sep);
						Object obj = Array.newInstance(_class, array.length);
						for(int i = 0; i < array.length; i ++) {
							Array.set(obj, i, parse(_class, array[i], key, className));
						}
						return obj;
					}
					catch (Exception e2) {
						throw new IOException(String.format(Const.PROPERTY_CAST_ERROR, _class.getSimpleName(), key, value, className));
					}
				}
			}
		}
		catch (Exception e) {
			msg = e.getMessage();
			if(log == null) {
				System.out.println(msg);
			}
			else {
				log.error(msg);
			}
			throw new IOException(e);
		}
	}
	
	public static final String concatenate(Object[] array, Character delimiter) {
		if(array==null)return null;
		StringBuffer buf=new StringBuffer();
		for(int i=0;i<array.length;i++) {
			if(i>0 && delimiter!=null) {
				buf.append(delimiter);
			}
			buf.append(array[i]);
		}
		return buf.toString();
	}
	public static String createTmpFile(byte[] parseExpression) throws IOException {
		String path=Const.TMP_PATH+System.getProperty("file.separator");
		String name=null;
		File f;
		while(true) {
			name=Const.TMP_FILENAME+Calendar.getInstance().getTimeInMillis();
			f=new File(path+name);
			if(!f.exists()) break;
			new Sleeper().sleep(10);
		}
		f.createNewFile();
		FileOutputStream out=new FileOutputStream(f);
		out.write(parseExpression);
		out.close();
		return f.getName();
	}
}
