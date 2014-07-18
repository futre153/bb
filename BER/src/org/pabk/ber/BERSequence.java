package org.pabk.ber;



public class BERSequence extends BER {
	public BERSequence(String name, Object[] seq) {
		this.ido=IdentifierOctets.SEQUENCE_ID;
		this.coo=new SequenceContent(seq);
		this.name=name;
	}
	private BERSequence(String name){this.name=name;}
	/*public void initSequence(Object[] seq) {
		this.ido=IdentifierOctets.SEQUENCE_ID;
		this.coo=new SequenceContent(seq);
	}*/
	@Override
	public BERSequence clone() throws CloneNotSupportedException {
		BERSequence ber=new BERSequence(this.name);
		ber.ido=IdentifierOctets.SEQUENCE_ID;
		ber.lno=this.lno==null?null:this.lno.clone();
		Object[] obj=new Object[((BER[]) this.coo.getValue()).length];
		for(int i=0;i<obj.length;i++) {
			obj[i]=((BER[]) this.coo.getValue())[i].clone();
		}
		ber.coo=new SequenceContent(obj);
		ber.eoc=this.eoc;
		return ber;
	}
	
	public void setValue(Object seq) {
		this.coo=new SequenceContent((Object[]) seq);
	}
	
}
