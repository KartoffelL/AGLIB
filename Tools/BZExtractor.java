package Kartoffel.Licht.Tools;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//
public class BZExtractor {
//	
//	public static void main(String[] args) throws IOException {
//		BZExtractor ex = new BZExtractor(new File("C:\\Users\\Anwender\\Downloads\\vlc-contrib-x86_64-w64-mingw32-bfa174413ee3bc474fea069820676e23fa26c552.tar.bz2"));
//	}
//
//	public BZExtractor(File f) throws IOException {
//		this(new FileInputStream(f));
//	}
//	
//	public BZExtractor(InputStream is) throws IOException {
//		int current = 0; //https://en.wikipedia.org/wiki/Bzip2
//		if(is.read() != 'B' || is.read() != 'Z' || is.read() != 'h')
//			throw new RuntimeException("Not an valid Bzip2 file!");
//		int blockSize = is.read()-48; // in 100kb
//		System.out.println("Blocksize: " + blockSize);
//		byte[] compressed_magic = new byte[] {(byte) is.read(), (byte) is.read(), (byte) is.read(), (byte) is.read(), (byte) is.read(), (byte) is.read()};
//		byte[] crc = new byte[] {(byte) is.read(), (byte) is.read(), (byte) is.read(), (byte) is.read()};
////		byte
//	}
//
}
