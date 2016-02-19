package org.pabk.basen.ber;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.pabk.basen.ber.encoders.BEREncoder;
import org.pabk.basen.ber.encoders.ConstrustedEncoder;
import org.pabk.basen.ber.encoders.OctetStringEncoder;
import org.pabk.basen.ber.encoders.OidEncoder;
import org.pabk.basen.ber.encoders.UniversalTagEncoder;

public class BERImpl extends ArrayList<BERImpl> implements BERObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID 			= 0x01L;
	private static final int NOT_DEFINED 				= 0xFFFFFFFF;
	private static final long INDEFINITE = NOT_DEFINED;
	public static final int CLASS_MASK		 			= 0xC0;
	private static final int CONSTRUCTED_MASK		 	= 0x20;
	public static final int UNIVERSAL_CLASS 			= 0x00 << Integer.numberOfTrailingZeros(CLASS_MASK);
	public static final int APPLICATION_CLASS 			= 0x01 << Integer.numberOfTrailingZeros(CLASS_MASK);
	public static final int CONTEXT_SPECIFIC_CLASS 		= 0x02 << Integer.numberOfTrailingZeros(CLASS_MASK);
	public static final int PRIVATE_CLASS 				= 0x03 << Integer.numberOfTrailingZeros(CLASS_MASK);
	public static final int PRIMITIVE_ENCODING 			= 0x00 << Integer.numberOfTrailingZeros(CONSTRUCTED_MASK);
	public static final int CONSTRUCTED_ENCODING 		= 0x01 << Integer.numberOfTrailingZeros(CONSTRUCTED_MASK);
	private static final int UNIVERSAL_TAGS_MASK 		= 0x1F;
	private static final int SEVEN_BIT_MASK 			= 0x7F;
	private static final int EIGHT_BIT_MASK 			= 0xFF;
	private static final String END_OF_STREAM = "Unexpected end of stream reached";
	private static final String TAG_NUMBER_NOT_SUPPORTED = "Tag number %s is not supported, maximal value is %d";
	private static final String UNSUPPORTED_LENGTH = "Maximal supported length of content is %s";
	private static final String ENCODING_LENGTH_ERROR = "Primitive encoding cannot have an indefinitive length";
	private static final int MAX_READED_BYTES = 1024 * 16;
	
	public static final int CHOICE = -99;
	
	public static final int BOOLEAN_TAG = 0x01;
	public static final int INTEGER_TAG = 0x02;
	public static final int BITSTRING_TAG 	= 0x03;
	public static final int OCTETSTRING_TAG = 0x04;
	public static final int NULL_TAG = 0x05;
	public static final int OBJECT_IDENTIFIER_TAG = 0x06;
	public static final int UTF8_STRING_TAG = 0x0C;
	public static final int SEQUENCE_TAG 	= 0x10;
	public static final int SET_TAG = 0x11;
	public static final int PRINTABLE_STRING_TAG = 0x13;
	public static final int TELETEX_STRING_TAG = 0x14;
	public static final int IA5_STRING_TAG = 0x16;
	public static final int UTC_TIME_TAG = 0x17;
	public static final int GENERALIZED_TIME_TAG = 0x18;
	public static final int UNIVERSAL_STRING_TAG = 0x1C;
	public static final int BMP_STRING_TAG = 0x1E;
						
	private int _class = NOT_DEFINED;
	private int constructed = NOT_DEFINED;
	private int tag = NOT_DEFINED;
	private long length = INDEFINITE;
	private Object content;
	private BEREncoder encoder;
	
	public BERImpl() {
		super();
		reset();
	}
	
	public BERImpl(int tag) {
		this();
		this.tag = tag;
	}
	
	public BERImpl(int tag, int constructed) {
		this(tag);
		this.constructed = constructed;
		this._class = BERImpl.UNIVERSAL_CLASS;
		this.setEncoder();
	}
	
	public BERImpl(int tag, int constructed, int _class) {
		this (tag);
		this.constructed = constructed;
		this._class = _class;
		this.setEncoder();
	}
	
	private void reset() {
		this.set_class(NOT_DEFINED);
		this.setConstructed(NOT_DEFINED);
		this.setTag(NOT_DEFINED);
		this.setLength(INDEFINITE);
		this.setContent(null);
		this.setEncoder();
		this.clear();
	}

	@Override
	public InputStream getIdentifierOctets() {
		int clazz = this.get_class();
		int pc = this.getConstructed();
		int tag = this.getTag();
		if(clazz > NOT_DEFINED && pc > NOT_DEFINED && tag > NOT_DEFINED) {
			byte[] b = new byte[6];
			int l = b.length - 1;
			if(tag >= UNIVERSAL_TAGS_MASK) {
				b[l] = (byte) (tag & SEVEN_BIT_MASK);
				tag >>= Integer.bitCount(SEVEN_BIT_MASK);
				l --;
				while(tag > 0) {
					b[l] = (byte) (tag & SEVEN_BIT_MASK | (SEVEN_BIT_MASK + 0x01));
					tag >>= Integer.bitCount(SEVEN_BIT_MASK);
					l --;
				}
			}
			tag = this.getTag();
			b[l] = (byte) (_class | pc | (tag >=  UNIVERSAL_TAGS_MASK ? UNIVERSAL_TAGS_MASK : tag));
			return new ByteArrayInputStream(b, l, b.length);
		}
		return null;
	}
	
	private static int read(InputStream in) throws IOException {
		int i = in.read();
		if(i < 0) {
			throw new IOException(END_OF_STREAM); 
		}
		return i;
	}
	
	public long decodeIdOctets(InputStream in) throws IOException {
		long length = 0;
		int i = read(in);
		length ++;
		this.set_class(i);
		this.setConstructed(i);
		i &= BERImpl.UNIVERSAL_TAGS_MASK;
		if(i < UNIVERSAL_TAGS_MASK) {
			this.setTag(i);
		}
		else {
			long l = 0;
			i = -1;
			do {
				i = read(in);
				length ++;
				l <<= Integer.bitCount(SEVEN_BIT_MASK);
				l |= (i & BERImpl.SEVEN_BIT_MASK);
			}
			while (i > SEVEN_BIT_MASK);
			if(l > Integer.MAX_VALUE) {
				throw new IOException (String.format(TAG_NUMBER_NOT_SUPPORTED, l, Integer.MAX_VALUE));
			}
			this.setTag((int) l);
		}
		return length;
	}
	
	@Override
	public InputStream getLengthOctets() {
		if(this.get_class() > NOT_DEFINED && this.getConstructed() > NOT_DEFINED && this.getTag() > NOT_DEFINED) {
			if(this.hasDefiniteLength()) {
				byte[] b = new byte[9];
				int i = b.length - 1;
				long l = this.getLength();
				if(l > SEVEN_BIT_MASK) {
					while (l > 0) {
						b[i] = (byte) (l & EIGHT_BIT_MASK);
						l >>= Integer.bitCount(EIGHT_BIT_MASK);
						i --;
					}
				}
				l = this.getLength();
				b[i] = (byte) (l > SEVEN_BIT_MASK ? SEVEN_BIT_MASK + b.length - i : l);
				return new ByteArrayInputStream(b, i, b.length);
			}
			else {
				return new ByteArrayInputStream(new byte[]{(byte) (BERImpl.SEVEN_BIT_MASK + 1)});
			}
		}
		return null;
	}
	
	public long decodeLengthOctets(InputStream in) throws IOException {
		long length = 0;
		int i = read(in);
		length ++;
		if(i == (BERImpl.SEVEN_BIT_MASK + 1)) {
			if(this.hasPrimitiveEncoding()) {
				throw new IOException (ENCODING_LENGTH_ERROR);
			}
			this.setLength(INDEFINITE);
		}
		else {
			if(i > BERImpl.SEVEN_BIT_MASK) {
				i &= BERImpl.SEVEN_BIT_MASK;
				if(i > 8) {
					throw new IOException(String.format(UNSUPPORTED_LENGTH, Long.MAX_VALUE));
				}
				long l = 0;
				for (int j = 0; j < i; j ++) {
					int b = read(in);
					length ++;
					l <<= Integer.bitCount(EIGHT_BIT_MASK);
					l |= b;
				}
				if(l < 0) {
					throw new IOException(String.format(UNSUPPORTED_LENGTH, Long.MAX_VALUE));
				}
				this.setLength(l);
			}
			else {
				this.setLength(i);
			}
		}
		return length;
	}
	
	@Override
	public InputStream getContentOctets() {
		try {
			return this.getEncoder().encode(this);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public long decodeContent(InputStream in) throws IOException {
		return this.getEncoder().decode(this, in);
	}
	
	@Override
	public void encode(OutputStream out) throws IOException {
		try {
			if(this.hasPrimitiveEncoding() && this.hasIndefiniteLength()) {
				throw new IOException (ENCODING_LENGTH_ERROR);
			}
			writeAll(getIdentifierOctets(), out);
			writeAll(getLengthOctets(), out);
			if(this.hasConstructedEncoding()) {
				for(int i = 0; i < this.size(); i ++) {
					this.get(i).encode(out);
				}
				if(this.hasIndefiniteLength()) {
					out.write(0);
					out.write(0);
				}
			}
			else {
				InputStream in = getContentOctets();
				if(this.hasDefiniteLength()) {
					writeTo(in, out, this.getLength());
				}
				else {
					writeAll(in, out);
				}
			}
		}
		catch (Exception e) {
			throw new IOException (e);
		}
	}

	private void writeTo(InputStream in, OutputStream out, long l) throws IOException {
		try {
			byte[] b = new byte[MAX_READED_BYTES];
			int i = 0;
			while (l > 0) {
				i = in.read(b, 0, (int) (l < MAX_READED_BYTES ? l : MAX_READED_BYTES));
				if(i < 0) {
					throw new IOException(END_OF_STREAM);
				}
				out.write(b, 0, i);
				l -= i;
			}
		}
		catch (Exception e) {
			throw new IOException (e);
		}
		finally {
			in.close();
		}
		
	}

	private static void writeAll(InputStream in, OutputStream out) throws IOException {
		try {
			byte[] b = new byte[MAX_READED_BYTES];
			int i = 0;
			while ((i = in.read(b)) >= 0) {
				out.write(b, 0, i);
			}
		}
		catch (Exception e) {
			throw new IOException (e);
		}
		finally {
			in.close();
		}
	}

	@Override
	public void decode(InputStream in) throws IOException {
		decodeIdOctets(in);
		decodeLengthOctets(in);
		if(getEncoder() == null) {
			setEncoder();
		}
		decodeContent(in);
	}

	@Override
	public boolean isUniversal() {
		return this.get_class() == UNIVERSAL_CLASS;
	}

	@Override
	public boolean isApplication() {
		return this.get_class() == APPLICATION_CLASS;
	}

	@Override
	public boolean isContextSpecific() {
		return this.get_class() == CONTEXT_SPECIFIC_CLASS;
	}

	@Override
	public boolean isPrivate() {
		return this.get_class() == PRIVATE_CLASS;
	}

	@Override
	public boolean hasPrimitiveEncoding() {
		return this.getConstructed() == PRIMITIVE_ENCODING;
	}

	@Override
	public boolean hasConstructedEncoding() {
		return this.getConstructed() == CONSTRUCTED_ENCODING;
	}

	@Override
	public boolean hasDefiniteLength() {
		return this.getLength() >= 0;
	}

	@Override
	public boolean hasIndefiniteLength() {
		return this.getLength() < 0;
	}

	public final int get_class() {
		return _class;
	}

	public final void set_class(int _class) {
		this._class = _class < 0 ? _class : _class & CLASS_MASK;
	}

	public final int getConstructed() {
		return constructed;
	}

	public final void setConstructed(int constructed) {
		this.constructed = constructed < 0 ? constructed : constructed & CONSTRUCTED_MASK;
	}

	public final int getTag() {
		return tag;
	}

	public final void setTag(int tag) {
		this.tag = tag;
	}

	public final long getLength() {
		return length;
	}

	public final void setLength(long length) {
		this.length = length;
	}

	public final Object getContent() {
		return content;
	}

	public final void setContent(Object content) {
		Object tmp = this.getContent();
		this.content = content;
		try {
			this.setLength(this.getEncoder().getLength(this));
		} catch (Exception e) {
			this.content = tmp;
		}
	}

	public BEREncoder getEncoder() {
		return encoder;
	}

	public void setEncoder() {
		int clazz = this.get_class();
		int pc = this.getConstructed();
		int tag = this.getTag();
		if(clazz == NOT_DEFINED || pc == NOT_DEFINED || tag == NOT_DEFINED) {
			this.encoder = null;
		}
		else {
			if(this.hasConstructedEncoding()) {
				this.encoder = new ConstrustedEncoder();
			}
			else {
				if(this.isUniversal()) {
					switch (tag) {
					case BERImpl.OBJECT_IDENTIFIER_TAG:
						this.encoder = new OidEncoder();
						break;
					default:
						this.encoder = new UniversalTagEncoder();
					}
				}
				else {
					this.encoder = new OctetStringEncoder();
				}
			}
		}
	}

	@Override
	public InputStream getEOFOctets() {
		if(this.get_class() > NOT_DEFINED && this.getConstructed() > NOT_DEFINED && this.getTag() > NOT_DEFINED) {
			return new ByteArrayInputStream(new byte[this.hasDefiniteLength() ? 0x00 : 0x02]);
		}
		return null;
	}

	public boolean isEOC() {
		return (this.get_class() | this.getConstructed() | this.getTag() | this.getLength()) == 0x00;
	}

}
