package Kartoffel.Licht.Net;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import Kartoffel.Licht.Tools.Timer;

public class Chungus {
	
	private SecretKey KEY;
	public static IvParameterSpec IV = new IvParameterSpec(new byte[] {0x77, 0x48, 0x66, 0x18, 0x5b, 0x28, 0x5f, 0x19, 0x18, 0x4b, 0x6f, 0x5c, 0x48, 0x19, 0x21, 0x3e});
	private Cipher cipherDe;
	private Cipher cipherEn;
	
	public static int En_Time = 0;
	public static int De_Time = 0;

//	public static void main(String[] args) {
//		String plain = "hello World :) ma baby...";
//		Chungus s = new Chungus("gaga");
//		byte[] cr = s.encrypt(plain.getBytes());
//		System.out.println("Encrypted: " + new String(cr));
//		System.out.println("Decrypted: " + new String(s.decrypt(cr)));
//	}

	public Chungus(String password) {
		updatePassword(password);
	}
	
	public void updatePassword(String password) {
		try {
			KEY = getKeyFromPassword(password, invertString(password));
			cipherDe = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipherEn = Cipher.getInstance("AES/CBC/PKCS5Padding");
		    cipherDe.init(Cipher.DECRYPT_MODE, KEY, IV);
		    cipherEn.init(Cipher.ENCRYPT_MODE, KEY, IV);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private String invertString(String s) {
		String n = "";
		for(int i = 0; i < s.length(); i++)
			n = s.charAt(i)+n;
		return n;
	}
	public static SecretKey getKeyFromPassword(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
		SecretKey secret = new SecretKeySpec(factory.generateSecret(spec)
			.getEncoded(), "AES");
		return secret;
	}
	
	
	
	public byte[] encrypt(byte[] data, boolean fast) {
		if(fast) {
			long t = Timer.getTime();
			byte[] b = new byte[data.length];
			byte[] key = KEY.getEncoded();
			for(int i = 0; i < b.length; i++) //Simple XOR obfuscation
				b[i] = (byte) (data[i]^key[i%key.length]);
			En_Time = (int) ((Timer.getTime()-t)/1000);
			return b;
		}
		try {
			long t = Timer.getTime();
			byte[] b = cipherEn.doFinal(data);
			En_Time = (int) ((Timer.getTime()-t)/1000);
			return b;
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return data;
	}

	public byte[] decrypt(byte[] data, boolean fast) {
		if(fast) {
			long t = Timer.getTime();
			byte[] b = new byte[data.length];
			byte[] key = KEY.getEncoded();
			for(int i = 0; i < b.length; i++) //Simple XOR obfuscation
				b[i] = (byte) (data[i]^key[i%key.length]);
			De_Time = (int) ((Timer.getTime()-t)/1000);
			return b;
		}
		try {
			long t = Timer.getTime();
			byte[] b = cipherDe.doFinal(data);
			De_Time = (int) ((Timer.getTime()-t)/1000);
			return b;
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return data;
	}

}
