/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 7, 2012
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging.encryption;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import gov.va.med.imaging.Base64;
import gov.va.med.imaging.encryption.exceptions.AesEncryptionException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author VHAISWWERFEJ
 *
 */
public class AesEncryption
{
	
	private final static byte [] sessionKey = "0123456789abcdef".getBytes();
	
	/**
	 * Rijndael encrypts and base 32 encodes the clearText string to be used for the AWIV
	 * @param clearText
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static String encrypt(String clearText) 
	throws AesEncryptionException
	{
		byte [] bytes = clearText.getBytes();
		
		Cipher cipher = null;
		try
		{
			cipher = Cipher.getInstance("AES");
		} 
		catch (NoSuchAlgorithmException e)
		{
			throw new AesEncryptionException("Exception finding AES algorithm", e);		
		}
		catch (NoSuchPaddingException e)
		{
			throw new AesEncryptionException("Exception finding AES algorithm", e);
		}		
		
		try
		{
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(sessionKey, "AES"));
		}
		catch (InvalidKeyException e)
		{
			throw new AesEncryptionException("Exception initializing cipher", e);
		}
		
		byte[] ciphertext = null;
		try
		{
			ciphertext = cipher.doFinal(bytes);
		} 
		catch (IllegalBlockSizeException e)
		{
			throw new AesEncryptionException("Exception during encryption", e);
		} 
		catch (BadPaddingException e)
		{
			throw new AesEncryptionException("Exception during encryption", e);
		}
		//System.out.println("Encrypted length: " + ciphertext.length);
		String encodedResult = Base64.encodeBytes(ciphertext);
		
		encodedResult = encodedResult.replace("\n", "");
		return encodedResult;
	}
	
	/**
	 * Rijndael encrypts and base 32 encodes the clearText string to be used for the AWIV
	 * @param initializationVector
	 * @param clearText
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static String encrypt(byte[] iv, String clearText) 
	throws AesEncryptionException
	{
		byte[] bytes = clearText.getBytes();
        
		Cipher cipher = null;
		try
		{
	        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		} 
		catch (NoSuchAlgorithmException e)
		{
			throw new AesEncryptionException("Exception finding AES algorithm", e);		
		}
		catch (NoSuchPaddingException e)
		{
			throw new AesEncryptionException("Exception finding AES algorithm", e);
		}		
		
		try
		{
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(sessionKey, "AES"), new IvParameterSpec(iv));
		}
		catch (InvalidAlgorithmParameterException e)
		{
			throw new AesEncryptionException("Invalid algorithm parameter", e);
		}
		catch (InvalidKeyException e)
		{
			throw new AesEncryptionException("Exception initializing cipher", e);
		}
		
		byte[] ciphertext = null;
		try
		{
			ciphertext = cipher.doFinal(bytes);
		} 
		catch (IllegalBlockSizeException e)
		{
			throw new AesEncryptionException("Exception during encryption", e);
		} 
		catch (BadPaddingException e)
		{
			throw new AesEncryptionException("Exception during encryption", e);
		}
		//System.out.println("Encrypted length: " + ciphertext.length);
		return encodeByteArray(ciphertext);
	}

	public static byte[] getInitializationVector()
	{
		// Create a byte array holding 16 bytes
		byte[] byteArray = new byte[16];
		
		// Fill it with random data
		Random r = new Random();
		r.nextBytes(byteArray);
		
		// Convert it to a string and return it
		return byteArray;

	}
	
	public static String encodeByteArray(byte[] byteArray)
	{
		String encodedResult = Base64.encodeBytes(byteArray);
		
		encodedResult = encodedResult.replace("\n", "");
		return encodedResult;
	}
	
	public static String decrypt(String base64Encrypted)
	throws AesEncryptionException
	{
		
		byte [] encoded = Base64.decode(base64Encrypted);
		//System.out.println("Base64 decoded, encrypted length: " + encoded.length);
		Cipher cipher = null;
		try
		{
			cipher = Cipher.getInstance("AES");
		} 
		catch (NoSuchAlgorithmException e)
		{
			throw new AesEncryptionException("Exception finding AES algorithm", e);		
		}
		catch (NoSuchPaddingException e)
		{
			throw new AesEncryptionException("Exception finding AES algorithm", e);
		}		
		
		try
		{
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(sessionKey, "AES"));
		}
		catch (InvalidKeyException e)
		{
			throw new AesEncryptionException("Exception initializing cipher", e);
		}
		
		byte[] decodedText = null;
		try
		{
			decodedText = cipher.doFinal(encoded);
		} 
		catch (IllegalBlockSizeException e)
		{
			throw new AesEncryptionException("Exception during decryption", e);
		} 
		catch (BadPaddingException e)
		{
			throw new AesEncryptionException("Exception during decryption", e);
		}
		return new String(decodedText);
	}

}
