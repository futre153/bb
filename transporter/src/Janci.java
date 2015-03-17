import org.pabk.util.Base64Coder;
import org.pabk.util.Huffman;

import com.eurogiro.common.exception.BusinessException;
import com.eurogiro.common.server.util.EncryptDecryptUtil;


public class Janci {

	public static void main(String[] args) throws Exception {
		  EncryptDecryptUtil en = new EncryptDecryptUtil();
		  String jano = null;
		   try { jano = EncryptDecryptUtil.getDecrypted("s6uurNSqXiw=nzml1ktySUShDTAt2Fgm8DkpeG+2G3/j");
		   } catch (BusinessException e) {
		     System.out.println("Passphrase for private key not parsed successfully");
		     System.err.println(e); }
		   
		System.out.println(jano);
		String john = Huffman.encode(jano, null); 
		System.out.println(john);
		String enj = Huffman.decode(john, null);
		
		System.out.println(enj);
	}

}
