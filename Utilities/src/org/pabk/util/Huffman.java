package org.pabk.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Hashtable;



public class Huffman {
	
	private static final int MAX_ITEMS=257;
	private static final int MAX_BYTES = 2048;
	private static final Huffman huffman = new Huffman();
	private static final Hashtable<String, Coder> CODERS = new Hashtable<String, Coder>();
	private static final String DEFAULT_CODER = "?";
	private static final String HA_TABLE_PATH = "/org/pabk/util/ha-table.txt";
	
	private class Coder {
		private int[][] coder;
		private int[][] decoder;
	}
	
	private Huffman() {};
	
	private static final Coder loadTable(String file) throws IOException {
		if(file == null) {
			file = DEFAULT_CODER;
		}
		Coder c = CODERS.get(file);
		if(c == null) {
			InputStream fin=null;
			if(file.equals(DEFAULT_CODER)) {
				fin = Huffman.class.getResourceAsStream(HA_TABLE_PATH);
			}
			else {
				File f=new File(file);
				if(f.exists()) {
					fin=new FileInputStream(f);
				}
			}
			if(fin != null) {
				c = huffman.new Coder();
				c.coder=new int[MAX_ITEMS][2];
				c.decoder=new int[MAX_ITEMS][2];
				
				ByteArrayOutputStream bout=new ByteArrayOutputStream();
				byte[] b=new byte[MAX_BYTES];
				while(true) {
					int i=fin.read(b);
						if(i<0) break;
					bout.write(b, 0, i);
				}
				fin.close();
				ByteArrayInputStream bin=new ByteArrayInputStream(Base64Coder.decodeLines(bout.toString()));
				ObjectInputStream in=new ObjectInputStream(bin);
				bout.close();
				for(int i=0;i<c.coder.length;i++) {
					try {
						for(int j=0;j<c.coder[i].length;j++) {
							c.coder[i][j]=in.readInt();
							c.decoder[i][j]=c.coder[i][j];
						}
					}
					catch(Exception e){}
				}
				in.close();
				bin.close();
				for(int i=0;i<c.decoder.length;i++) {
					for(int j=i+1;j<c.decoder.length;j++) {
						if(c.decoder[i][1]>c.decoder[j][1]) {
							int x=c.decoder[i][0];
							int y=c.decoder[i][1];
							c.decoder[i][0]=c.decoder[j][0];
							c.decoder[i][1]=c.decoder[j][1];
							c.decoder[j][0]=x;
							c.decoder[j][1]=y;
						}
					}
				}
				Huffman.CODERS.put(file, c);
			}
		}
		return c;
	}
	
	public static final String decode (String s, String c, String table) throws Exception {
		byte[] dec = Huffman.decode(Base64Coder.decode(s), table); 
		return new String(dec, c);
	}
	public static final String decode (String s, String table) throws Exception {
		byte[] dec = Huffman.decode(Base64Coder.decode(s), table);
		Charset c = Charset.defaultCharset();
		return new String(dec, c);
	}
	public static final String encode(String s, String table) throws Exception {
		byte[] enc = Huffman.encode(s.getBytes(Charset.defaultCharset()), table);
		return new String(Base64Coder.encode(enc));
	}
	public static final String encode(String s, String c, String table) throws Exception {
		byte[] enc = Huffman.encode(s.getBytes(c), table);
		return Base64Coder.encodeString(new String(enc, c));
	}
	
	static final void decompress (String src, String dst, String table) throws Exception {
		File source=new File(src);
		if(!source.exists())throw new IOException ("Source file does not exists!");
		FileInputStream in=new FileInputStream(source);
		File destination =new File(dst);
		if(destination.exists()) {destination.delete();}
		FileOutputStream out=new FileOutputStream(destination);
		decode(in,out, table);
		in.close();
		out.close();
	}
	
	static final void compress (String src, String dst, String table) throws Exception {
		File source=new File(src);
		if(!source.exists())throw new IOException ("Source file does not exists!");
		FileInputStream in=new FileInputStream(source);
		File destination =new File(dst);
		if(destination.exists()) {destination.delete();}
		FileOutputStream out=new FileOutputStream(destination);
		encode(in,out,table);
		in.close();
		out.close();
	}
	
	private static final byte[] decode(byte[] b, String table) throws Exception {
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		ByteArrayInputStream in=new ByteArrayInputStream(b);
		decode(in,out, table);
		in.close();
		b=out.toByteArray();
		out.close();
		return b;
	}
	
	private static final void decode(InputStream in, OutputStream out, String table) throws Exception {
		Coder cd = loadTable(table);
		int code[]=new int[]{1,in.read(),0x80}; {
			while(true) {
				int c=decodeCharacter(code, in, cd);
				if(c>255)break;
				out.write(c);
			}
		}
	}
	
	private static int decodeCharacter(int[] code, InputStream in, Coder cd) throws Exception {
		for(int i=0;i<cd.decoder.length;i++) {
			while(Integer.highestOneBit(cd.decoder[i][1])!=Integer.highestOneBit(code[0])) {
				shift(code,in);
			}
			if(cd.decoder[i][1]==code[0]) {
				code[0]=1;
				return cd.decoder[i][0];
			}
		}
		throw new IOException("Failed to decode character!");
	}

	private static void shift(int[] code, InputStream in) throws Exception {
		if(code[2]==0) {code[1]=in.read();code[2]=0x80;}
		if(code[1]<0)throw new IOException("Failed to decode EOF reached!");
		code[0]=((code[1]&code[2])>0)?((code[0]<<1)^1):(code[0]<<1);
		code[2]>>=1;
	}

	private static final byte[] encode(byte[] b, String table) throws Exception {
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		ByteArrayInputStream in=new ByteArrayInputStream(b);
		encode(in,out, table);
		in.close();
		b=out.toByteArray();
		out.close();
		return b;
	}
	
	
	private static final void encode(InputStream in, OutputStream out, String table) throws Exception {
		Coder cd = loadTable(table);
		int[] code=new int[]{0,0};
		while(true) {
			int b=in.read();
			if(b<0) break;
			Huffman.encodeCharacter(code, cd.coder[b][1]);
			Huffman.enrollCharacter(out,code);
		}
		Huffman.encodeCharacter(code, cd.coder[256][1]);
		Huffman.enrollCharacter(out,code);
		if(code[1]>0) {
			out.write(code[0]<<(8-code[1]));
		}
	}
	
	private static void enrollCharacter(OutputStream out, int[] code) throws Exception {
		out.write(rollToBytes(code[0],code[1]));
		code[1]%=8;
		code[0]^=((code[0]>>code[1])<<code[1]);
	}

	private static byte[] rollToBytes(int x, int p) {
		byte[] b=new byte[p/8];
		x>>=(p%8);
		for(int i=0;i<b.length;i++) {
			byte xx=(byte) (x&0x0FF);
			b[b.length-1-i]=xx;
			x=x>>8;
		}
		return b;
	}

	private static final void encodeCharacter(int[] code, int c) throws Exception {
		int x=Integer.highestOneBit(c);
		code[0]<<=(Integer.numberOfTrailingZeros(x));
		code[0]|=(c&(x-1));
		code[1]+=Integer.numberOfTrailingZeros(x);
	}
	/*
	public static void main(String a[]) throws Exception {
		Huffman.loadTable("C:\\temp\\table.txt");
		String s="admin";
		String en=encode(s);
		System.out.println(en);
		String de=decode(en);
		System.out.println(de);
	}
	*/
}
