package org.pabk.ber;

import java.io.IOException;

abstract class BER implements BaseBER {

	IdentifierOctets ido;
	LengthOctets lno;
	ContentOctets coo;
	EOCOctets eoc;
	String name;
	int level=-1;
	
	public BER setName(String name) {
		this.name=name;
		return this;
	}
	public String getName(){return name;}
		
	public void decode(Encoder en, long l) throws IOException {
		System.out.println(this.name+","+this.getClass().getSimpleName());
		level=en.getLevel();
		ido.decode(en, -1);
		if(lno==null)lno=new LengthOctets();
		lno.decode(en, -1);
		coo.decode(en, (long)lno.getValue());
		if(!lno.isDefinite())eoc=EOCOctets.EOC;
		if(eoc!=null) {
			eoc.decode(en, 2);
		}
		else {
			/*
			long val1=(long)lno.getValue();
			long val2=coo.length();
			*/
			if(((long)lno.getValue())!=coo.length()) throw new IOException ("Length of content octets is not equal to definite length");
			if(l>=0) {
				if(this.length()!=l) throw new IOException("Length of object is not equal to definite length");
			}
		}
	}
	
	public void encode(Encoder en) throws IOException {
		ido.encode(en);
		lno.encode(en);
		coo.encode(en);
		if(eoc!=null) eoc.encode(en);
	}
	
	public Object getValue() throws UnsupportedOperationException {return coo.getValue();}
	
	public long length() throws UnsupportedOperationException {
		if(eoc!=null) throw new UnsupportedOperationException ("Indefinite length");
		return ido.length()+lno.length()+coo.length();
	}
	
	public byte[] getBytes()throws UnsupportedOperationException {
		byte[]b=BER.join(BER.join(ido.getBytes(), lno.getBytes()), coo.getBytes());
		if(eoc!=null) {b=BER.join(b,eoc.getBytes());}
		return b;
	}
	
	public static byte[] join(byte[] a, byte[] b) {
		byte[] c=new byte[a.length+b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

	public static byte[] cut(byte[] a, int i, int pos) {
		byte[] b=new byte[pos-i];
		System.arraycopy(a, i, b, 0, b.length);
		return b;
	}
	
	abstract public BER clone() throws CloneNotSupportedException;
	
	public String toString() {
		String tmp=BER.tabbed(level)+(getClass().getSimpleName().substring(3).toUpperCase()+" "+name);//+" level="+level);
		if(lno==null)tmp+=(" "+lno.toString());
		if(coo==null) {
			tmp+="\r\n"+BER.tabbed(level)+BER.tabbed(1)+"Content is null";	
		}
		else {
			if(coo.size()==0) {
				tmp+=("\r\n"+BER.tabbed(level)+BER.tabbed(1)+"value="+coo.toString());
			}
			else {
				tmp+=" {";
				for(int i=0;i<coo.size();i++) {
					tmp+=("\r\n"+coo.get(i).toString());
				}
				tmp+=("\r\n"+BER.tabbed(level)+"}");
			}
		}
		return tmp;
	}
	
	public static String tabbed(int t) {
		StringBuffer tmp=new StringBuffer();
		t=t<0?t=0:t;
		//for(int i=0;i<t;i++) {tmp.append('\t');}
		for(int i=0;i<t;i++) {tmp.append("  ");}
		return tmp.toString();
	}
	
	public BER copy(String newName) {
		BER clone;
		try {clone=this.clone();}
		catch (CloneNotSupportedException e) {throw new UnsupportedOperationException(e.getMessage());}
		if(newName!=null)clone.setName(newName);
		return clone;
	}
	
	public BER forName (String name) {
		BER ber=this;
		if(ber.getName()!=null)if(ber.getName().equals(name)) {return ber;}
		if(ber.ido.isConstructed()) {
			for(int i=0;i<ber.coo.size();i++) {
				 BER ret=ber.coo.get(i).forName(name);
				 if(ret!=null)return ret;
			}
		}
		return null;	
	}
	
	public void setValue(Object obj) {}
	
	public void clearContent() {
		this.lno.clearContent();
		this.coo.clearContent();
	}
	
}
