package org.acepricot.finance.web.msgs;

import java.io.IOException;

import org.acepricot.ber.BER;
import org.acepricot.ber.BERConst;

abstract class AceDataItem extends BER {
	 
	 AceDataItem() throws IOException {
		 this.setIdOctets(BERConst.UNIVERSAL_CLASS, BERConst.CONSTRUCTED_ENCODING, BERConst.SEQUENCE_TAG_NUMBER);
		 this.getContentDecoder().setDefinite(true);
		 this.getContentEncoder().setDefinite(true);
		 super.setDefiniteLength(true);
	 }
	  
	 abstract void setName(BER ber);
	 abstract void setOid(BER ber);
	 abstract void setItemValue(BER ber);
	 abstract String getName() throws IOException;
	 abstract String getOid() throws IOException;
	 abstract byte[] getItemValue() throws IOException;
}
