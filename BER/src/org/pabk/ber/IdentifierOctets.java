package org.pabk.ber;

import java.io.IOException;

class IdentifierOctets implements BaseBER {
	
	protected static final int UNIVERSAL		= 0x00;
	protected static final int APPLICATION		= 0x40;
	protected static final int CONTEXT_SPECIFIC	= 0x80;
	protected static final int PRIVATE			= 0xC0;
	
	private static final int CONSTRUCTED_VALUE	= 0x20;
	
	private static final int MIN_NUMBER_VALUE	= 0x00;
	private static final int MAX_NUMBER_VALUE	= 0x7FFFFFFF;
	private static final int MAX_LOW_NUMBER		= 0x1E;
	private static final int HIGH_VALUE_MASK	= 0x80;
	
	public static final int EOC_TAG_NUMBER		= 0x00;
	public static final int BOOLEAN_TAG_NUMBER	= 0x01;
	public static final int INTEGER_TAG_NUMBER	= 0x02;
	private static final int BITSTRING_TAG_NUMBER = 0x03;
	public static final int OCTETSTRING_TAG_NUMBER	= 0x04;
	private static final int NULL_TAG_NUMBER 	= 0x06;
	private static final int OID_TAG_NUMBER 	= 0x06;
	private static final int SEQUENCE_TAG_NUMBER = 0x10;
	private static final int SET_TAG_NUMBER = 0x11;
	private static final int UTFSTRING_TAG_NUMBER = 0x0C;
	private static final int PRINTABLESTRING_TAG_NUMBER = 0x13;
	
	final static IdentifierOctets EOC_ID=new IdentifierOctets(UNIVERSAL,false,EOC_TAG_NUMBER);
	final static IdentifierOctets BOOLEAN_ID=new IdentifierOctets(UNIVERSAL,false,BOOLEAN_TAG_NUMBER);
	final static IdentifierOctets INTEGER_ID=new IdentifierOctets(UNIVERSAL,false,INTEGER_TAG_NUMBER);
	final static IdentifierOctets BITSTRING_CONSTRUCTED_ID=new IdentifierOctets(UNIVERSAL,true,BITSTRING_TAG_NUMBER);
	final static IdentifierOctets BITSTRING_PRIMITIVE_ID=new IdentifierOctets(UNIVERSAL,false,BITSTRING_TAG_NUMBER);
	final static IdentifierOctets OCTETSTRING_PRIMITIVE_ID=new IdentifierOctets(UNIVERSAL,false,OCTETSTRING_TAG_NUMBER);
	static final IdentifierOctets UTFSTRING_PRIMITIVE_ID = new IdentifierOctets(UNIVERSAL,false,UTFSTRING_TAG_NUMBER);
	static final IdentifierOctets PRINTABLESTRING_PRIMITIVE_ID = new IdentifierOctets(UNIVERSAL,false,PRINTABLESTRING_TAG_NUMBER);
	final static IdentifierOctets OCTETSTRING_CONSTRUCTED_ID=new IdentifierOctets(UNIVERSAL,true,OCTETSTRING_TAG_NUMBER);
	final static IdentifierOctets UTFSTRING_CONSTRUCTED_ID=new IdentifierOctets(UNIVERSAL,true,UTFSTRING_TAG_NUMBER);
	final static IdentifierOctets PRINTABLESTRING_CONSTRUCTED_ID=new IdentifierOctets(UNIVERSAL,true,PRINTABLESTRING_TAG_NUMBER);
	final static IdentifierOctets NULL_ID = new IdentifierOctets(UNIVERSAL,false,NULL_TAG_NUMBER);;
	final static IdentifierOctets OID_ID=new IdentifierOctets(UNIVERSAL,false,OID_TAG_NUMBER);
	final static IdentifierOctets SEQUENCE_ID=new IdentifierOctets(UNIVERSAL,true,SEQUENCE_TAG_NUMBER);
	final static IdentifierOctets SET_ID=new IdentifierOctets(UNIVERSAL,true,SET_TAG_NUMBER);
	
	
	
	
	//constant strings
	private static final String CLASS_MISMATCH = "Class of the tag mis-match";
	private static final String NUMBER_MISMATCH = "Tag number is out of supported range ("+MIN_NUMBER_VALUE+" - "+MAX_NUMBER_VALUE+")";
	private static final String CLASS_DECODE_MISMATCH = "Class of the tag is failed to decode";
	private static final String CONSTRUCTED_DECODE_MISMATCH = "Constructed indicator is failed to decode";
	private static final String TAGNUMBER_DECODE_MISMATCH = "Tag number is failed to decode";
	
	private int _class;
	private boolean constructed;
	private int tagNumber;
	
	
	public IdentifierOctets(int _class, boolean constructed, int number) {
		super();
		setClassOfTag(_class);
		this.constructed = constructed;	
		setNumber(number);
	}
	
	public int getClassOfTag() {return _class;}
	public boolean isConstructed() {return constructed;}
	public boolean isApplication() {return (this._class&APPLICATION)>0;}
	public boolean isTagged(){return (this._class&CONTEXT_SPECIFIC)>0;}
	
	public int getNumber() {return tagNumber;}
	
	private void setClassOfTag(int c) {
		if(!((c==UNIVERSAL)||(c==APPLICATION)||(c==CONTEXT_SPECIFIC)||(c==PRIVATE))) {
			throw new UnsupportedOperationException(CLASS_MISMATCH);
		}
		this._class = c;
	}
	
	private void setNumber(int n) {
		if((n<MIN_NUMBER_VALUE) || (n>MAX_NUMBER_VALUE)) {
			throw new UnsupportedOperationException(NUMBER_MISMATCH);
		}
		this.tagNumber=n;
	}
	
	@Override
	public Object getValue() throws UnsupportedOperationException {
		int l=1;
		if(tagNumber>MAX_LOW_NUMBER) {
			l+=((Integer.numberOfTrailingZeros(Integer.highestOneBit(tagNumber))/Integer.numberOfTrailingZeros(HIGH_VALUE_MASK))+1);
		}
		Octet[] os=new Octet[l];
		os[0]=new Octet(_class+(constructed?CONSTRUCTED_VALUE:UNIVERSAL)+((tagNumber>MAX_LOW_NUMBER)?(MAX_LOW_NUMBER+1):tagNumber));
				
		int tmp=tagNumber;
		for(int i=1; i<os.length;i++) {
			Octet o=new Octet((int) (tmp%HIGH_VALUE_MASK),HIGH_VALUE_MASK-1);
			if(i>1) {
				o.setOctet(HIGH_VALUE_MASK, HIGH_VALUE_MASK);
			}
			tmp/=HIGH_VALUE_MASK;
			os[os.length-i]=o;
		}
		return os;
	}
	
	@Override
	public void encode(Encoder encoder) throws IOException {encoder.write(getBytes());}
	
	@Override
	public void decode(Encoder encoder, long l) throws IOException,UnsupportedOperationException {
		int c=encoder.read();
		int b=c;
		b&=(MAX_LOW_NUMBER+1);
		if(b>MAX_LOW_NUMBER) {
			b=0;
			while(true) {
				int s=encoder.read();
				b<<=(Integer.bitCount(HIGH_VALUE_MASK-1));
				b+=(s&(HIGH_VALUE_MASK-1));
				if(s<HIGH_VALUE_MASK)break;
			}
		}
		String err=null;
		if((c&PRIVATE)!=_class)err=CLASS_DECODE_MISMATCH;
		if((constructed && ((c&CONSTRUCTED_VALUE)==0)) || ((!constructed) && ((c&CONSTRUCTED_VALUE)>0))) {
			err=CONSTRUCTED_DECODE_MISMATCH;
		}
		System.out.println(b+","+tagNumber);
		if(b!=tagNumber)err=TAGNUMBER_DECODE_MISMATCH;	
		if(err!=null) {
			encoder.retBytes(new IdentifierOctets(c&PRIVATE,(c&CONSTRUCTED_VALUE)>0,b).getBytes());
			throw new UnsupportedOperationException(err);
		}
	}

	@Override
	public long length() throws UnsupportedOperationException {return ((Octet[])getValue()).length;}

	@Override
	public byte[] getBytes() throws UnsupportedOperationException {
		return Octet.toBytes((Octet[]) getValue());
	}

	public static IdentifierOctets getTaggedInstance(boolean c,int nr,boolean a) {
		return new IdentifierOctets(a?IdentifierOctets.APPLICATION:IdentifierOctets.CONTEXT_SPECIFIC,c,nr);
	}

	@Override
	public void clearContent() {}

	public static IdentifierOctets decodeAny(Encoder en, int i) {
		// TODO Auto-generated method stub
		return null;
	}
}
