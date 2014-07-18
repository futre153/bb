package org.acepricot.ber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class BER extends BERImpl {
	
	private BERContentEncoder cenc;
	private BERContentDecoder cdec;
	
	@Override
	public BERContentDecoder getContentDecoder() {
		if(cdec == null) {
			cdec = BERDefaultContentEncoder.getInstance(this.getIDOctets().isPrimitive(),this.getLengthOctets().isDefinite());
			cenc = (BERContentEncoder) cdec;
		}
		return this.cdec;
	}

	@Override
	public BERContentEncoder getContentEncoder() {
		if(cenc == null) {
			cenc = BERDefaultContentEncoder.getInstance(this.getIDOctets().isPrimitive(),this.getLengthOctets().isDefinite());
			cdec = (BERContentDecoder) cenc;
		}
		return this.cenc;
	}
	/*
	public BER getCHild(int index) {
		return (BER) this.getConctructedContent().get(index);
	}
	*/
	public String toString() {
		String line = this.getClass().getName() + System.getProperty("line.separator");
		line += ("\t"+this.getIDOctets().getTagNumber()+", "+this.getIDOctets().getClassType()+", "+this.getIDOctets().isConstructed()+ System.getProperty("line.separator"));
		try {
			line+=("\t"+this.getLengthOctets().getContentLength()+ System.getProperty("line.separator"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(this.getIDOctets().isConstructed()) {
			for(int i = 0;i<this.getConctructedContent().size();i++) {
				line+=(this.getConctructedContent().get(i));
			}
		}
		else {
			try {
				line+=("\t" + Arrays.toString(this.getValue())+ System.getProperty("line.separator"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return line;
	}

	public int getTagNumber() {
		return this.getIDOctets().getTagNumber();
	}

	public int getClassType() {
		return this.getIDOctets().getClassType();
	}

	public boolean isPrimitive() {
		return this.getIDOctets().isPrimitive();
	}
	
	public BER getCHildNode(int index) throws IOException {
		if(this.getConctructedContent() == null) {
			throw new BERFormatException(String.format(BERFormatException.NULL_CONSTRUCTED_CONTENT));
		}
		try {
			if(index >= this.getConctructedContent().size()) {
				return null;
			}
			return (BER) this.getConctructedContent().get(index);
		}
		catch(Exception e) {
			throw new BERFormatException(e.getMessage());
		}
	}
	
	//public void setConstructedContent(BER[] nodes) {
	public void setConstructedContent(BER ... nodes) {
		super.setConstructedContent(new ArrayList<BERImpl>());
		for(int i = 0; i < nodes.length; i++) {
			this.getConctructedContent().add(nodes[i]);
		} 
	}
	
	public void setIdOctets(int clazz, int encoding, int tagNumber) throws IOException {
		this.getIDOctets().setClassType(clazz);
		this.getIDOctets().setConstructed(encoding);
		this.getIDOctets().setTagNumber(tagNumber);		
	}
	
	public void addConstructedContent(BERImpl ber) {
		if(this.getConctructedContent() == null) {
			this.setConstructedContent(new ArrayList<BERImpl>());
		}
		this.getConctructedContent().add(ber);
	}
	
	public void setDefiniteLength(boolean b) throws IOException {
		if(b) {
			this.getLengthOctets().setContentLength(0);
		}
		else {
			this.getLengthOctets().setValue(null);
		}
		
	}
	
}
