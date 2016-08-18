package com.neptunedockyard;

import org.apache.commons.lang3.StringUtils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Transcode {
	
	private static Cipher cipher;
	private static KeySpec spec;
	private static SecretKeyFactory factory;
	private static SecretKey SecKey;
	private static SecretKey secret;
	private static String salt;

	public Transcode(String encType, String username, String password) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException {
		// TODO Auto-generated constructor stub
		System.out.println("Transcoder-\r\nencType: " + encType + " username: " + username + " password: " + password);
		switch(encType){
		case "AES": {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cryptoFactory(username, password, encType);
		} break;
		case "DES": {
			cipher = Cipher.getInstance("DES");
			cryptoFactory(username, password, encType);
		} break;
		default: {
		} break;
		}
		
	}
	
	public void cryptoFactory(String user, String pass, String encType) throws NoSuchAlgorithmException, InvalidKeySpecException{
		System.out.println("CryptoFactory-\r\nencType: " + encType + " username: " + user + " password: " + pass);
		salt = StringUtils.leftPad(user, 32, 'x');
		System.out.println("salt: " + salt + " length: " + salt.length());
		
		spec = new PBEKeySpec(pass.toCharArray(), salt.getBytes(), 65536, 128);			//AES128
		factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		SecKey = factory.generateSecret(spec);
		
		System.out.println("CryptoF1 SecretKey: " + SecKey.getEncoded().toString());
		
		switch(encType){
		case "AES": {
			secret = new SecretKeySpec(SecKey.getEncoded(), "AES");
		} break;
		case "DES": {
			secret = new SecretKeySpec(SecKey.getEncoded(), "DES");
		} break;
		}
		System.out.println("CryptoF2 SecretKey: " + SecKey.getEncoded().toString() + " Secret: " + secret.getEncoded().toString());
	}

	public String AESEncode(String text) throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
		// TODO Auto-generated constructor stub
		byte[] byteText = text.getBytes();
		System.out.println("AESE SecretKey: " + SecKey.getEncoded().toString() + " Secret: " + secret.getEncoded().toString());
		cipher.init(Cipher.ENCRYPT_MODE, secret);
		byte[] byteCipherText = cipher.doFinal(byteText);
		System.out.println("Original message: " + text);
		System.out.println("Encoded message: " + byteCipherText.toString());
		
		return byteCipherText.toString();
	}
	
	public static String DESEncode(String text) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		// TODO Auto-generated constructor stub
		KeyGenerator KeyGen = KeyGenerator.getInstance("DES");
		KeyGen.init(128);
		byte[] byteText = text.getBytes();

		cipher.init(Cipher.ENCRYPT_MODE, SecKey);
		byte[] byteCipherText = cipher.doFinal(byteText);
		
		return byteCipherText.toString();
	}
	
	public static String AESDecode(String text, String salt) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
		// TODO Auto-generated constructor stub
		byte[] cipherText = text.getBytes();
		
		cipher.init(Cipher.DECRYPT_MODE, SecKey);
		byte[] bytePlainText = cipher.doFinal(cipherText);
		
		return bytePlainText.toString();
	}
	
	public static String DESDecode(String text) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		// TODO Auto-generated constructor stub
//		Cipher DesCipher = Cipher.getInstance("DES");
		byte[] cipherText = text.getBytes();
		
		cipher.init(Cipher.DECRYPT_MODE, SecKey);
		byte[] bytePlainText = cipher.doFinal(cipherText);
		
		return bytePlainText.toString();
	}
}
