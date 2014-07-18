package org.pabk.ber;

class Octet {
	
	private static final int DEFAULT_VALUE	= 0x0;
	private static final int MIN_VALUE		= 0x0;
	private static final int MAX_VALUE		= 0xFF;
	
	int _value;
	
	Octet() {_value=DEFAULT_VALUE;}
	
	Octet(int s) {this(s,MAX_VALUE);}
	
	Octet(int s, int m) {this(s,m,true);}
	
	Octet(int s, int m, boolean ignoreRewrite) {setOctet(s,m,ignoreRewrite);}
	
	
	void setOctet(int s, int m, boolean ignoreRewrite) {
		check(s);
		check(m);
		if(!ignoreRewrite) {
			if((_value&m)!=0x0)
				throw new UnsupportedOperationException ("Value "+s+" rewrites stored data");
		}
		/*int x=_value|m;
		x^=m;
		int y=s&m;
		x|=y;*/
		_value=((_value|m)^m)|(s&m);
	}
	
	void setOctet(int s, int m) {setOctet(s,m,true);}
	
	void setOctet(int s) {setOctet(s,0xFF);}
	
	int getValue() {
		return getValue(Octet.MAX_VALUE);
	}
	
	int getValue(int m) {return this._value&m;}
	
	private void check (int i) {
		if(i<MIN_VALUE || i>MAX_VALUE)
			throw new UnsupportedOperationException ("Value "+i+" is out of range");
	}
	
	static byte[] toBytes(Octet[] os) {
		byte[] b=new byte[os.length];
		for(int i=0;i<os.length;i++) {
			b[i]=(byte) os[i]._value;
		}
		return b;
	}
	
}
