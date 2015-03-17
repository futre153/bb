package org.pabk.emanager.util;

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


public class Huffman {
	
	private static final int MAX_ITEMS=257;
	private static final int MAX_BYTES = 2048;
	private static int[][] coder;
	private static int[][] decoder;
		
	public static final void loadTable(String file) throws IOException {
		File f=new File(file);
		if(f.exists()) {
			coder=new int[MAX_ITEMS][2];
			decoder=new int[MAX_ITEMS][2];
			FileInputStream fin=new FileInputStream(f);
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
			for(int i=0;i<Huffman.coder.length;i++) {
				try {
					for(int j=0;j<Huffman.coder[i].length;j++) {
						coder[i][j]=in.readInt();
						decoder[i][j]=coder[i][j];
					}
				}
				catch(Exception e){}
			}
			in.close();
			bin.close();
			for(int i=0;i<decoder.length;i++) {
				for(int j=i+1;j<decoder.length;j++) {
					if(decoder[i][1]>decoder[j][1]) {
						int x=decoder[i][0];
						int y=decoder[i][1];
						decoder[i][0]=decoder[j][0];
						decoder[i][1]=decoder[j][1];
						decoder[j][0]=x;
						decoder[j][1]=y;
					}
				}
			}
		}
	}
	
	public static final String decode (String s, String c) throws Exception {
		return new String(Huffman.decode(Base64Coder.decode(s)),c);
	}
	public static final String decode (String s) throws Exception {
		return new String(Huffman.decode(Base64Coder.decode(s)),Charset.defaultCharset());
	}
	public static final String encode(String s) throws Exception {
		return new String(Base64Coder.encode(Huffman.encode(s.getBytes(Charset.defaultCharset()))));
	}
	public static final String encode(String s, String c) throws Exception {
		return Base64Coder.encodeString(new String(Huffman.encode(s.getBytes(c)),c));
	}
	
	public static final void decompress (String src, String dst) throws Exception {
		File source=new File(src);
		if(!source.exists())throw new IOException ("Source file does not exists!");
		FileInputStream in=new FileInputStream(source);
		File destination =new File(dst);
		if(destination.exists()) {destination.delete();}
		FileOutputStream out=new FileOutputStream(destination);
		decode(in,out);
		in.close();
		out.close();
	}
	
	public static final void compress (String src, String dst) throws Exception {
		File source=new File(src);
		if(!source.exists())throw new IOException ("Source file does not exists!");
		FileInputStream in=new FileInputStream(source);
		File destination =new File(dst);
		if(destination.exists()) {destination.delete();}
		FileOutputStream out=new FileOutputStream(destination);
		encode(in,out);
		in.close();
		out.close();
	}
	
	public static final byte[] decode(byte[] b) throws Exception {
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		ByteArrayInputStream in=new ByteArrayInputStream(b);
		decode(in,out);
		in.close();
		b=out.toByteArray();
		out.close();
		return b;
	}
	
	public static final void decode(InputStream in, OutputStream out) throws Exception {
		int code[]=new int[]{1,in.read(),0x80}; {
			while(true) {
				int c=decodeCharacter(code, in);
				if(c>255)break;
				out.write(c);
			}
		}
	}
	
	private static int decodeCharacter(int[] code, InputStream in) throws Exception {
		for(int i=0;i<decoder.length;i++) {
			while(Integer.highestOneBit(decoder[i][1])!=Integer.highestOneBit(code[0])) {
				shift(code,in);
			}
			if(decoder[i][1]==code[0]) {
				code[0]=1;
				return decoder[i][0];
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

	public static final byte[] encode(byte[] b) throws Exception {
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		ByteArrayInputStream in=new ByteArrayInputStream(b);
		encode(in,out);
		in.close();
		b=out.toByteArray();
		out.close();
		return b;
	}
	
	
	public static final void encode(InputStream in, OutputStream out) throws Exception {
		int[] code=new int[]{0,0};
		while(true) {
			int b=in.read();
			if(b<0) break;
			Huffman.encodeCharacter(code, coder[b][1]);
			Huffman.enrollCharacter(out,code);
		}
		Huffman.encodeCharacter(code, coder[256][1]);
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
	
	public static void main(String a[]) throws Exception {
		Huffman.loadTable("C:\\temp\\table.txt");
		String s="admin";
		String en=encode(s);
		System.out.println(en);
		String de=decode(en);
		System.out.println(de);
	}
	
}
