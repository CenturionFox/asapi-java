package com.attributestudios.api.util.crypto;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.attributestudios.api.util.logging.LoggingUtil;
import com.attributestudios.api.util.logging.SimpleLogFormatter;

/**
 * Provides utility methods for hashing objects and resources.
 * 
 * @author  Bridger Maskrey (maskreybe@live.com)
 * @version 1.0.1
 * @date.   2014-08-22
 * @edited. 2014-08-25
 */
public class HashUtils
{
	/**
	 * The main logger for the hash utility class.
	 */
	private static Logger hashLog = LoggingUtil.constructLogger("Hashing Function", new SimpleLogFormatter());
	
	/**
	 * Hashes a string as a string of hexadecimal values via the specified hashing 
	 * 	algorithm.
	 * @param  toHash   The string to hash
	 * @param  hashType The type of algorithm to apply to the string
	 * @return The string as it is hashed by the specified hashing algorithm; if
	 * 			   hashing fails, return null.
	 * @throws     IOException Thrown if a resource fails to be closed.
	 * @see        HashingType The HashingType enum for the different valid algorithms.
	 * @since      1.0.0
	 * @deprecated Use {@link HashUtils#hashAs(String, HashingType)} instead.
	 */
	public static String hashStringAs(String toHash, HashingType hashType) throws IOException
	{
		return hashAs(toHash, hashType);
	}
	
	/**
	 * Hashes a string as a string of hexadecimal values via the specified hashing 
	 * 	algorithm.
	 * @param  toHash   The string to hash
	 * @param  hashType The type of algorithm to apply to the string
	 * @return The string as it is hashed by the specified hashing algorithm; if
	 * 			   hashing fails, return null.
	 * @throws IOException Thrown if a resource fails to be closed.
	 * @see    HashingType The HashingType enum for the different valid algorithms.
	 * @since  1.0.1
	 */
	public static String hashAs(String toHash, HashingType hashType) throws IOException
	{
		hashLog.finest("Hashing string " + toHash + " as " + hashType.getHashType());
		
		try(BufferedInputStream sread = new BufferedInputStream(new ByteArrayInputStream(toHash.getBytes("UTF-8"))))
		{
			return hashAs(sread, hashType);
		}
	}
	
	/**
	 * Hashes a file as a string of hexadecimal values via the specified hashing
	 *     algorithm.
	 * @param  toHash	The file to hash.
	 * @param  hashType	The type of algorithm to apply to the file.
	 * @return The file as it is hashed by the specified hashing algorithm; if
	 * 			   hashing fails, return null.
	 * @throws IOException	Thrown if a resource fails to be closed, or if the
	 * 						    specified file cannot be found.
	 * @see    HashingType	The HashingType enum for the different valid algorithms.
	 * @since  1.0.1
	 */
	public static String hashAs(File toHash, HashingType hashType) throws IOException
	{
		hashLog.finest("Hashing file " + toHash.getAbsolutePath() + " as " + hashType.getHashType());
		
		try(BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(toHash)))
		{
			return hashAs(inputStream, hashType);
		}
	}
	
	/**
	 * Hashes an input stream as a string of hexadecimal values via the specified
	 * 	   hashing algorithm.
	 * @param  input		The input stream to hash.
	 * @param  hashType	The type of algorithm to apply to the file.
	 * @return The input stream as it is hashed by the specified hashing algorithm;
	 * 			   if hashing fails, return null.
	 * @throws IOException	Thrown if the input stream fails to be read.
	 * @see    HashingType	The HashingType enum for the different valid algorithms.
	 * @since  1.0.1
	 */
	public static String hashAs(InputStream input, HashingType hashType) throws IOException
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance(hashType.getHashType());
			
			byte[] buffer = new byte[256];
			
			while(input.read(buffer) != -1)
			{
				md.update(buffer);
			}
		
			return byteArrayToHex(md.digest());
		}
		catch (NoSuchAlgorithmException e)
		{
			LoggingUtil.writeStackTraceToLogger(hashLog, 
												e,
												"Hashing as " + hashType.hashType + " failed: ",
												Level.SEVERE);
		}
		
		return null;
	}
	
	/**
	 * Converts an array of bytes to a String object consisting of
	 * 	   the hexadecimal values of all bytes present in the array.
	 * @param  bytes The byte array to convert to a string.
	 * @return The converted string.
	 * @since  1.0.1
	 */
	public static String byteArrayToHex(byte[] bytes)
	{
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		
		for(byte b : bytes)
		{
			sb.append(String.format("%02x", b & 0xff));
		}
		
		return sb.toString();
	}
	
	/**
	 * Enumeration of the valid MessageDigest algorithms for convenience.
	 * @author  Bridger Maskrey
	 * @version 1.0.0
	 * @date.   2014-08-22
	 * @since   1.0.0
	 */
	public static enum HashingType
	{
		/**
		 * Represents the MessageDigest 5 hashing algorithm.
		 * @since 1.0.0
		 */
		MD5("MD5"),
		/**
		 * Represents the Secure Hashing Algorithm 1 hashing algorithm.
		 * @since 1.0.0
		 */
		SHA1("SHA-1"),
		/**
		 * Represents the Secure Hashing Algorithm 256 hashing algorithm.
		 * @since 1.0.0
		 */
		SHA256("SHA-256");
		
		/**
		 * The name of the associated hashing algorithm.
		 */
		private String hashType;
		
		/**
		 * Creates a new HashingType with the specified algorithm name.
		 * @param type The name of the associated hashing algorithm.
		 */
		private HashingType(String type)
		{
			this.hashType = type;
		}
		
		/**
		 * Gets the name of the hashing algorithm to pass to the
		 * 	MessageDigest.
		 * @return The name of the hashing algorithm formatted such
		 * 			that the {@link MessageDigest#getInstance(String)}
		 * 			function can recognize it.
		 */
		public String getHashType()
		{
			return this.hashType;
		}
	}
}
