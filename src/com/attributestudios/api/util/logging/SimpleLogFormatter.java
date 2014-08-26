package com.attributestudios.api.util.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Formats a logger's ouput in a simple, single-line format.
 * 
 * @author  Bridger Maskrey (maskreybe@live.com)
 * @version 1.0.0
 * @date.   2014-08-12
 */
public class SimpleLogFormatter extends Formatter
{
	public static DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

	/**
	 * Formats the specified log record in a simplified
	 * 	style.
	 * 
	 * @param  logRecord The record to format.
	 * @return The formatted string version of the record.
	 */
	@Override
	public String format(LogRecord logRecord)
	{
		// Create a new string builder to build the formatted log entry.
		StringBuilder logFormatted = new StringBuilder(64);
		
		// Format the current date from the logger.
		String dateString = dateFormatter.format(new Date(logRecord.getMillis()));
		
		// Append all parts of the log together
		logFormatted.append(dateString).append(" [").append(logRecord.getLoggerName()).append("] ")
			.append(logRecord.getLevel()).append(": ").append(logRecord.getMessage()).append("\n");
		
		// Return the formatted log string.
		return logFormatted.toString();
	}

}
