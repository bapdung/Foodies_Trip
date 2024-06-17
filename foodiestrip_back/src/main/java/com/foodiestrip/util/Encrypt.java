package com.foodiestrip.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class Encrypt {
	private static final int SALT_SIZE = 16;
	
	public String getSalt() {
		// "SHA1PRNG"은 알고리즘 이름
//		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		
		// 1. Random. byte 객체 생성
		SecureRandom sr = new SecureRandom();
		byte salt[] = new byte[SALT_SIZE];
		
		// 2. 난수 생성
		sr.nextBytes(salt);
		
		// 3. byte to string (10진수의 문자열로 변경)
//		String salt = new String(Base64.getEncoder().encode(bytes));
		StringBuilder sb = new StringBuilder();
		for (byte b : salt) {
			sb.append(String.format("%02x", b));
		}
		
		return sb.toString();
	}
	
	public String hashing(String pwd, String salt) {
		String result = null;
		
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			String pwdAndSalt = pwd + salt;
			
			// pwd+salt 암호화 (평문 + salt)
			md.update(pwdAndSalt.getBytes());
			
			byte temp[] = md.digest();
			
			// 출력
			StringBuilder sb = new StringBuilder();
			for (byte b : temp) {
				sb.append(String.format("%02x", b));
			}
			
			result = sb.toString();
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
