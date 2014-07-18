package org.pabk.ber;

import java.io.IOException;

public class Tagged extends BER {
	private boolean implicit=false;
	private BER tag;
	
	public Tagged(String name,Object ber,int nr, boolean i, boolean a) {
		implicit=i;
		tag=(BER) ber;
		this.ido=IdentifierOctets.getTaggedInstance(this.tag.ido.isConstructed(), nr, a);
		this.name=name;
	}
	
	public void clearContent() {this.tag.clearContent();}
	
	@Override
	public Tagged clone() throws CloneNotSupportedException {
		return new Tagged(this.name,tag.clone(),this.ido.getNumber(),implicit,this.ido.getClassOfTag()==IdentifierOctets.APPLICATION);
	}
	/*
	public boolean isImplicit(){return implicit;}
	public BER getObject(){return tag;}
	*/
	@Override
	public Object getValue() throws UnsupportedOperationException {return tag.getValue();}

	@Override
	public void decode(Encoder en, long l) throws IOException {
		this.level=en.getLevel();
		ido.decode(en, -1);
		if(lno==null)lno=new LengthOctets();
		lno.decode(en, -1);
		if(!implicit) {
			tag.ido.decode(en, -1);
			if(tag.lno==null)tag.lno=new LengthOctets();
			tag.lno.decode(en, -1);
		}
		tag.coo.decode(en, (long)lno.getValue());		
		if(!implicit) {
			if(!tag.lno.isDefinite()) {
				tag.eoc=EOCOctets.EOC;
				tag.eoc.decode(en, 2);
			}
			else {
				if(((long)tag.lno.getValue())!=tag.coo.length()) throw new IOException ("Length of content octets is not equal to definite length");
			}
		}
		if(!lno.isDefinite()) {
			eoc=EOCOctets.EOC;
			eoc.decode(en, 2);
		}
		else {
			if(((long)lno.getValue())!=tag.coo.length()) throw new IOException ("Length of content octets is not equal to definite length");
		}
		if(l>=0 && this.length()!=l) throw new IOException("Length of object is not equal to definite length");
	}

	@Override
	public void encode(Encoder en) throws IOException {
		ido.encode(en);
		lno.encode(en);
		if(!implicit) {
			tag.ido.encode(en);
			tag.lno.encode(en);
		}
		tag.coo.encode(en);
		if(tag.eoc!=null) eoc.encode(en);
		
	}

	@Override
	public long length() throws UnsupportedOperationException {
		if(tag.eoc!=null) throw new UnsupportedOperationException ("Indefinite length");
		return ido.length()+lno.length()+tag.coo.length()+(implicit?0:(tag.ido.length()+tag.lno.length()));
		
	}

	@Override
	public byte[] getBytes() throws UnsupportedOperationException {
		byte[] b=BER.join(ido.getBytes(), lno.getBytes());
		if(!implicit) {b=BER.join(BER.join(b, tag.ido.getBytes()), tag.lno.getBytes());}
		b=BER.join(b, tag.coo.getBytes());
		if(tag.eoc!=null) {b=BER.join(b,tag.eoc.getBytes());}
		return b;
	}

	public void initTagged(BER ber, int nr, boolean a) {
		this.tag=ber;
		this.ido=IdentifierOctets.getTaggedInstance(this.tag.ido.isConstructed(), nr, a);
	}
	public void setValue(Object obj) {this.tag.setValue(obj);}
	
	public String toString() {
		String tmp=BER.tabbed(level);
		if(this.ido.isApplication())tmp+="APPLICATION ";
		tmp+=("["+this.ido.getNumber()+"] ");
		if(implicit)tmp+="IMPLICIT ";
		tmp+=(this.tag.getClass().getSimpleName().substring(3).toUpperCase()+" "+name);//+" level="+level);
		if(implicit) {
			if(lno==null)tmp+=(" "+lno.toString());
		}
		else {
			if(tag.lno==null)tmp+=(" "+tag.lno.toString());
		}
		if(tag.coo==null) {
			tmp+="\r\n"+BER.tabbed(level)+BER.tabbed(1)+"Content is null";	
		}
		else {
			if(tag.coo.size()==0) {
				tmp+=("\r\n"+BER.tabbed(level)+BER.tabbed(1)+"value="+tag.coo.toString());
			}
			else {
				tmp+=" {";
				for(int i=0;i<tag.coo.size();i++) {
					tmp+=("\r\n"+tag.coo.get(i).toString());
				}
				tmp+=("\r\n"+BER.tabbed(level)+"}");
			}
		}
		return tmp;
	}
	
	public BER forName (String name) {
		BER ber=this;
		if(ber.getName()!=null)if(ber.getName().equals(name)) {return ber;}
		ber=this.tag;
		if(ber.getName()!=null)if(ber.getName().equals(name)) {return ber;}
		if(ber.ido.isConstructed()) {
			for(int i=0;i<ber.coo.size();i++) {
				 BER ret=ber.coo.get(i).forName(name);
				 if(ret!=null)return ret;
			}
		}
		return null;	
	}
	
	
}
