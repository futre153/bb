package org.pabk.ber;

import java.io.IOException;
import java.util.Arrays;

public class OctetStringContent extends ContentOctets {
	
	private byte[] value=null;
	private boolean constructed=false;
	private int level=-1;
	
	private static final long serialVersionUID = 1L;

	public OctetStringContent(boolean b) {constructed=b;}
	
	public OctetStringContent(byte[][] b) {
		if(b.length>1) {
			constructed=true;
			for(int i=0;i<b.length;i++) {
				BER ber=new BEROctetString("zzz",new byte[][]{b[i]});
				this.add(ber);
			}
		}
	}

	@Override
	public Object getValue() throws UnsupportedOperationException {
		if(constructed) {
			BER[] ber=new BER[this.size()];
			return this.toArray(ber);
		}
		if(value==null) throw new UnsupportedOperationException("The octetstring/bitstring value is not set");
		return value;
	}
		
	@Override
	public void decode(Encoder en, long l) throws IOException {
		level=en.getLevel();
		if(constructed) {
			en.setLevel(en.getLevel()+1);
			if(this.size()>0) {
				long x=0;
				for(int i=0;i<this.size();i++) {
					this.get(i).decode(en, -1);
					x+=this.get(i).length();
				}
				if(l>0) {
					if(x!=l)throw new IOException("Costructed octet string length failed");
				}
			}
			else {
				while(true) {
					BER ber=new BEROctetString("xxx");
					boolean ok=true;
					try {ber.ido.decode(en, -1);}
					catch(UnsupportedOperationException e) {ok=false;}
					//en.retBytes(ber.ido.getBytes());
					if(ok) {
						ber.decode(en, -1);
						this.add(ber);
					}
					else {
						ber=EOCOctets.EOC;
						ok=true;
						try {ber.ido.decode(en, -1);}
						catch(UnsupportedOperationException e) {ok=false;}
						//en.retBytes(ber.ido.getBytes());
						if(!ok) {
							en.setLevel(en.getLevel()-1);
							throw new UnsupportedOperationException("End of Contents is expected");
						}
					}
				}
			}
			en.setLevel(en.getLevel()-1);
		}
		else {
			byte[] b=new byte[(int) l];
			en.read(b);
			if(value==null)value=b;
			if(!Arrays.equals(value, b))throw new IOException("Octet string value is not equal with predefined value");
		}

	}

	@Override
	public void encode(Encoder en) throws IOException {
		en.write(getBytes());
	}

	@Override
	public long length() throws UnsupportedOperationException {
		long l=0;
		if(constructed) {
			for(int i=0; i<this.size();i++) {
				l+=this.get(i).length();
			}
		}
		else {
			l=value.length;
		}
		return l;
	}

	@Override
	public byte[] getBytes() throws UnsupportedOperationException {
		byte[] b=new byte[0];
		if(constructed) {
			for(int i=0; i<this.size();i++) {
				b=BER.join(b,this.get(i).getBytes());
			}
		}
		else {
			b=BER.join(b, value);
		}
		return b;
	}

	@Override
	public String toString() {
		String tmp="";
		int index=0;
		StringBuffer line=null;
		StringBuffer prt=null;
		String hex=null;
		if(value==null)	return null;
		while(index<value.length) {
			if((index&0x0F)==0) {
				if(line!=null) {
					line.append(" ");
					line.append(prt.toString().replaceAll("[\\r\\n\\t]", "."));
					tmp+=("\r\n"+BER.tabbed(level)+BER.tabbed(1)+line);
				}
				line=new StringBuffer();
				hex=Integer.toHexString(index).toUpperCase();
				line.append("00000000".substring(0,8-hex.length()));
				line.append(hex);
				line.append(": ");
				prt=new StringBuffer();
			}
			hex=Integer.toHexString(value[index]&0xFF).toUpperCase();
			line.append("0".substring(0,2-hex.length()));
			line.append(hex);
			line.append(" ");
			prt.append((char)value[index]);
			index++;
			if((index&0x0F)==0x08) {
				line.append("| ");
			}
		}
		while((index&0x0F)!=0) {
			line.append("   ");
			index++;
			if((index&0x0F)==0x08) {
				line.append("| ");
			}
		}
		line.append(" ");
		line.append(prt.toString().replaceAll("[\\r\\n\\t]", "."));
		tmp+=("\r\n"+BER.tabbed(level)+BER.tabbed(1)+line);
		return tmp;
	}
	
	public void clearContent() {
		this.value=null;
		this.clear();
	}
	
}
