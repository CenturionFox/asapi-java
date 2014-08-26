package com.attributestudios.api.util.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for constructing formatted loggers.
 * 
 * @author  Bridger Maskrey (maskreybe@live.com)
 * @version 1.0.0
 * @date.	2014-08-12
 */
public class LoggingUtil
{
	/**
	 * Creates a new logger using {@link Logger#getLogger(String)}
	 * 	and sets the formatter to the specified formatter.
	 * 
	 * @param  loggerName The name of the logger to construct.
	 * @param  format	  The format of the constructed logger.
	 * @return A new logger with the specified name and formatter.
	 */
	public static Logger constructLogger(String loggerName, Formatter format)
	{
		// Construct the logger.
		Logger log = Logger.getLogger(loggerName);
		//log.setLevel(Level.FINE);
		
		// Create a new console handler and apply the formatter.
		ConsoleHandler temp = new ConsoleHandler();
		temp.setFormatter(format);
		//temp.setLevel(Level.FINE);
		
		// Set the logger formats
		log.setUseParentHandlers(false);
		log.addHandler(temp);
		
		// Return the log
		return log;
	}
	
	/**
	 * Aggregates and writes the stack trace of an exception into the specified
	 * 	logger at the specified level.
	 * @param log		The logger to write the exception into
	 * @param exception	The exception to log
	 * @param message	A custom message to display before the stack trace
	 * @param level		The level at which to display the exception stack trace
	 */
	public static void writeStackTraceToLogger(Logger log, Exception exception, String message, Level level)
	{
		// Set up stream utilities
		StringWriter stackResult = new StringWriter();
		PrintWriter resultWriter = new PrintWriter(stackResult);
		
		// Print the stack trace to the string writer via the print writer
		exception.printStackTrace(resultWriter);
		
		// Log the stack
		log.log(level, message + "\n" + stackResult.toString());
	}

}
