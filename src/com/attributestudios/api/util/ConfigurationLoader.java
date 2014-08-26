/**
 * 
 */
package com.attributestudios.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.attributestudios.api.util.logging.LoggingUtil;
import com.attributestudios.api.util.logging.SimpleLogFormatter;

/**
 * Constructs a hash map of name / value pairs that
 * 	represents the contents of a specified ".properties"-
 * 	formatted file.
 * <br><hr>
 * <b>Properties File Specification</b><br><br>
 * The configuration file is a "UTF-8 w/o BOM"-encoded file that
 * 	follows a simple name / value pair system of the format:
 * <br><br>
 * <code>[unlocalized-name]=[localized-name]</code>
 * <br><br>
 * Lines that should not be read (comments) are designated by 
 *  '<code>;</code>' or '<code>#</code>' at the beginning of the line.  Note that 
 *  comments may not appear on the same line as a name / value pair.  
 *  '\' is used to escape {@linkplain #allowedCharacters special characters}. 
 *  Lines should be delimited by the CR/LF (windows) line endings.
 *  
 * @author  Bridger Maskrey
 * @version 1.0.0
 * @date.   2014-08-13
 * @see Localizer
 */
public class ConfigurationLoader
{
	/**
	 * Represents a list of characters that may be escaped with '\' in the language
	 * 	file.  This list corresponds to the {@link #replaceCharacters} string in such
	 * 	a way that the indexes of the allowed and replacement characters is equal.
	 */
	private static final String allowedCharacters = "=nrtf";
	
	/**
	 * Represents a list of characters that will replace the escaped characters
	 * 	obtained form the localization file.  This list corresponds to the {@link
	 * 	#allowedCharacters} string in such a way that the indexes of allowed and
	 * 	replacement characters is equal.
	 */
	private static final String replaceCharacters = "=\n\r\t\f";
	
	/**
	 * The input stream from which to read in the configuration data.
	 */
	protected InputStream fileStream;
	
	protected HashMap<String, String> dictionary;
	
	protected boolean initialized = false;
	
	/**
	 * The main debug logger for a configuration loader.
	 * Any errors thrown by the loader or any output it this class writes
	 * 	is piped through this logger.
	 */
	protected Logger genericLogger = LoggingUtil.constructLogger("Configuration", new SimpleLogFormatter());
	
	/**
	 * Constructs a new ConfigurationLoader.
	 * The loader will read in tags constructed from the data obtained
	 * 	from the specified file stream.
	 * 
	 * @param fileStream The stream from which to read the input properties file.
	 */
	public ConfigurationLoader(InputStream fileStream)
	{
		this.fileStream = fileStream;
		
		// Initialize dictionary
		this.dictionary = new HashMap<String, String>();
	}
	
	public boolean initialize()
	{
		// Open the file stream with an InputStreamReader and encoding UTF-8 (no BOM)
		try(InputStreamReader reader = new InputStreamReader(this.fileStream, "UTF-8"))
		{
			// Open a scanner that will read through the reader
			try(Scanner scan = new Scanner(reader))
			{
				// Set the scanner delimiter to \\A (beginning of file)
				scan.useDelimiter("\\A");
				// Read the scanner
				String fileText = scan.hasNext() ? scan.next() : "";
				
				// If there is no data present then there is nothing to localize.
				if(!fileText.isEmpty())
				{
					// Split the lines by windows-default CR/LF line ends
					String[] lines = fileText.split("\r\n");
					
					// Iterate through the split lines
					for(int i = 0; i < lines.length; i++)
					{
						// If a line starts with ";" or "#" then it is a comment and should 
						//    be skipped.
						if(lines[i].matches("^[;#].+") || lines[i].isEmpty()) continue;
						
						// Split by "=" unless the character is preceded by a backslash
						//    Uses the lookback statement in parenthesis defines the backslash
						//    as the preceding character to look for.
						String[] pair = lines[i].split("(?<!\\\\)=");
						
						// If the pair is an incorrect length, throw an exception.
						if(pair.length != 2 || pair[0].isEmpty())
						{
							throw new IOException("Cannot parse value of [" + pair[0] + "]! The structure of the key is invalid." +
												  " (Line " + (i + 1) + ", length " + pair.length + " [" + lines[i] + "])");
						}
						
						// Replace all escape characters
						String key   = this.replaceControlCharacters(pair[0]);
						
						String value = this.replaceControlCharacters(pair[1]);
						
						this.genericLogger.config("Registering key " + key + " as " + value);
						
						this.dictionary.put(key, value);
					}
				}
				else
				{
					// No data was present, so quietly fail to localize anything
					this.genericLogger.warning("There was no data to localize!");
				}
			}
		}
		catch(IOException e)
		{
			// Sorry, but we couldn't localize the file, probably due to a malformed key.
			//    Prints a stack trace and quietly fails.
			LoggingUtil.writeStackTraceToLogger(this.genericLogger, 
												e,
												"Unable to initialize:",
												Level.SEVERE);
			return false;
		}
		
		this.initialized = true;
		
		return true;
	}
	
	/**
	 * Replaces any escaped characters with their replacements.
	 * @param string The string to edit.
	 * @return The edited sequence.
	 * @see #allowedCharacters The characters that may be escaped.
	 * @see #replaceCharacters The replacement values for the escaped characters.
	 */
	private String replaceControlCharacters(String string)
	{
		String ret = string;
		for(int i = 0; i < allowedCharacters.length(); i++)
		{
			char c = allowedCharacters.charAt(i);
			
			ret = ret.replaceAll("\\\\" + c, "" + replaceCharacters.charAt(i));
		}
		
		return ret;
	}
	
	public String getValue(String objectKey, String defaultValue)
	{
		// We aren't initialized yet... Why are we being told to localize?
		if(!this.initialized)
		{
			this.genericLogger.warning(this + " was not initialized before access! Unable to get property " + objectKey);
			return defaultValue;
		}
		
		// The specified key was never defined (or was defined incorrectly) in the dictionary file!
		if(!this.dictionary.containsKey(objectKey))
		{
			this.genericLogger.warning("The key " + objectKey + " was not found in the properties " + this + ". Unable to get property.");
			return defaultValue;
		}
		
		try
		{
			String ret = this.dictionary.get(objectKey);
			
			this.genericLogger.finest("Found property key " + objectKey + " as " + ret);
			
			return ret;
		} catch (Exception e)
		{
			LoggingUtil.writeStackTraceToLogger(this.genericLogger, e, "Unable to get property value:", Level.WARNING);
			return defaultValue;
		}
	}
}
