package com.excelente.geek_soccer.purchase_pack;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	public static String md5Digest(final String text)
	{
	     try
	     {
	           // Create MD5 Hash
	           MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
	           digest.update(text.getBytes());
	           byte messageDigest[] = digest.digest();

	           // Create Hex String
	           StringBuffer hexString = new StringBuffer();
	           int messageDigestLenght = messageDigest.length;
	           for (int i = 0; i < messageDigestLenght; i++)
	           {
	                String hashedData = Integer.toHexString(0xFF & messageDigest[i]);
	                while (hashedData.length() < 2)
	                     hashedData = "0" + hashedData;
	                hexString.append(hashedData);
	           }
	           return hexString.toString();

	     } catch (NoSuchAlgorithmException e)
	     {
	           e.printStackTrace();
	     }
	     return ""; // if text is null then return nothing
	}
}
