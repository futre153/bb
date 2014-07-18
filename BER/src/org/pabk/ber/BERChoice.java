package org.pabk.ber;

import java.io.IOException;

public class BERChoice extends BER {
	private Object[] choice;
	private int index=-1;
	private int fIndex=-1;
	
	public BERChoice(String name, Object[] a, int i) {
		choice=a;
		index=(i<0?-1:i);
		fIndex=index;
		this.name=name;
	}
	
	public Object getValue() {return ((BER) choice[index]).getValue();}
	
	@Override
	public BERChoice clone() throws CloneNotSupportedException {
		Object[] obj=null;
		if(choice!=null) {
			obj=new Object[choice.length];
			for(int i=0;i<obj.length;i++) {
				obj[i]=((BER)choice[i]).clone();
			}
		}
		return new BERChoice(this.name, obj,index);
	}
	
	
	public void decode(Encoder en, long l) throws IOException {
		/*for(int j=0;j<choice.length;j++) {
  			if(((BER)choice[j]).getName()==null)((BER)choice[j]).setName(name);
 		}*/
		if(index>=0) {((BER)this.choice[index]).decode(en, l);}
		else {
			for(int i=0;i<this.choice.length;i++) {
				try {((BER)this.choice[i]).decode(en, -1);}
				catch(UnsupportedOperationException e) {e.printStackTrace();continue;}
				index=i;
				return;
			}
			throw(new IOException ("The choice not choosen"));
			//decode(en,l);
		}
	}
	
	public void encode(Encoder en) throws IOException {
		if(index<0) throw new UnsupportedOperationException("The choice not choosen");
		en.write(getBytes());
	}
	
	public byte[] getBytes() throws UnsupportedOperationException { 
		if(index<0) throw new UnsupportedOperationException("The choice not choosen");
		return ((BER) this.choice[index]).getBytes();
	}
	
	public long length()throws UnsupportedOperationException { 
		if(index<0) throw new UnsupportedOperationException("The choice not choosen");
		return ((BER) this.choice[index]).getBytes().length;
	}
	public String toString() {
		return choice[index].toString();
	}
	
	public BER forName (String name) {
		BER ber=this;
		if(ber.getName()!=null)if(ber.getName().equals(name)) {return ber;}
		for(int i=0;i<choice.length;i++) {
			BER ret=((BER)choice[i]).forName(name);
			if(ret!=null)return ret;
		}
		return null;	
	}
	
	public void clearContent() {
		for(int i=0;i<choice.length;i++) {
			((BER)choice[i]).clearContent();
		}
		index=fIndex;
	}
	
}
