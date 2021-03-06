/**
 * Main utility package for the attribute API
 */
package com.attributestudios.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.Properties;

import com.attributestudios.api.util.logging.LoggingUtil;
import com.attributestudios.api.util.logging.SimpleLogFormatter;

/**
 * Constructs a hash map of name / value pairs that
 * 	represents the contents of a specified localization
 * 	file.  This class also has an identifier used to
 * 	determine the current language; this should conform
 *  to the W3 standard language information style
 *  (e.g. en-US).
 * <br><hr>
 * <b>Language File Specifications</b><br><br>
 * The language file is a "UTF-8 w/o BOM"-encoded file that
 * 	follows a simple name / value pair system of the format:
 * <br><br>
 * <code>[unlocalized-name]=[localized-name]</code>
 * <br><br>
 * Lines that should not be read into the localizer (comments)
 * 	are designated by '<code>;</code>' or '<code>#</code>' at the beginning of
 * 	the line.  Note that comments may not appear on the same
 * 	line as a name / value pair.  '\' is used to escape {@linkplain
 *  ConfigurationLoader#escapedCharacters special characters}. 
 *  Lines should be delimited by the CR/LF (windows) line endings.
 *  They are virtually identical to .properties files.
 * 
 * @author  Bridger Maskrey (maskreybe@live.com)
 * @version 1.1.0
 * @date.   2014-08-12
 */
public class Localizer extends Properties
{	
	private static final long serialVersionUID = 8681727683816048138L;
	
	/**
	 * A map of initialized localizations, populated when the {@link #initialize()} 
	 * 	   method is invoked on an instantiated localizer.  Localizers in this hash map
	 *     are defined via their {@link Localizer#languageID} field, and can be accessed
	 *     thusly via the {@link #getLocale(String)} method.
	 * @since 1.0.0
	 */
	private static HashMap<String, Localizer> registeredLocalizers = new HashMap<String, Localizer>();
	public Logger genericLogger;
	
	/**
	 * The ID of the localizer language.
	 */
	private String languageID;
	
	/**
	 * Constructs a new Localizer.  This localizer reads in tags from the
	 * 	   specified language properties file
	 * 
	 * @param fileStream The stream from which to read the input lang file.
	 * @param languageID The W3 standard language ID used to identify this
	 * 		localization file.
	 */
	public Localizer(String languageID)
	{
		// Set up stream and language tag properties
		super();
		
		this.languageID = languageID;
		
		this.genericLogger = LoggingUtil.constructLogger("Localization", new SimpleLogFormatter());
	}
	
	@Override
	public void load(InputStream inStream) throws IOException
	{
		super.load(inStream);
		
		Localizer.registeredLocalizers.put(this.languageID, this);
	}

//	/**
//	 * Translates an unlocalized value into the localized alternative stored in the
//	 * 	   locale's dictionary.
//	 * @param unlocalizedKey The unlocalized name of the key.  If the locale is uninitialized or
//	 * 			   if the key is not extant in the dictionary, then the key is returned unlocalized.
//	 * @return The localized version of the unlocalized key.
//	 * @since 1.0.0
//	 * @see #getValue(String, String) The string value translation function from ConfigurationLoader.
//	 */
//	@Override
//	public String getProperty(String unlocalizedKey)
//	{
//		return this.getProperty(unlocalizedKey, unlocalizedKey);
//	}
//	
//	@Override
//	public String getProperty(String unlocalizedKey, String defaultValue)
//	{
//		String localized = super.getProperty(unlocalizedKey, defaultValue);
//		
//		this.genericLogger.finest("Localizing key " + unlocalizedKey + " as " + localized);
//		
//		return localized;
//	}
	
	@Deprecated
	public String localize(String unlocalizedKey)
	{
		return this.getProperty(unlocalizedKey);
	}
	
	/**
	 * Returns a locale via the language ID string it was registered with.
	 * @param languageID The W3 standard language tag supplied when the
	 * 			   locale was instantiated.
	 * @return The Localizer object stored with the specified language ID.
	 * @since 1.0.0
	 */
	public static Localizer getLocale(String languageID)
	{
		Localizer loc = registeredLocalizers.get(languageID);
		
		if(loc == null)
			Logger.getLogger("Localization").warning("Language " + languageID + " could not be found. " +
						   	   "Perhaps it was not initialized?");
		return loc;
	}	
}
