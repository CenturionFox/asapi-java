package com.attributestudios.api.util.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import com.attributestudios.api.util.logging.LoggingUtil;
import com.attributestudios.api.util.logging.SimpleLogFormatter;

/**
 * Provides a utility for downloading a file from the internet.
 * 
 * @author  Bridger Maskrey (maskreybe@live.com)
 * @version 1.0.0
 * @date.	2014-08-18
 */
public class ResourceDownloader
{
	/**
	 * A logger with which to output any download-specific debug information.
	 */
	private static Logger dlLogger = LoggingUtil.constructLogger("Downloader", new SimpleLogFormatter());

	/**
	 * Downloads a file from a specified URL to the downloaded location.
	 * 
	 * @param resourceOnWeb 	 The URL of the web resource to download.
	 * @param downloadedLocation The file to save the downloaded data to.
	 * @return The downloaded file, if it exists. Otherwise, return null.
	 * @throws IOException Thrown if any portion of the download fails to complete.
	 */
	public File downloadFile(URL resourceOnWeb, File downloadedLocation) throws IOException
	{
		// Make all directories for the downloaded file.
		dlLogger.info("Making parent directories for " + downloadedLocation + "...");
		downloadedLocation.getParentFile().mkdirs();
		
		// If the file exists, remove it.
		if(downloadedLocation.exists())
		{
			dlLogger.warning("Output file already exists! Removing...");
			downloadedLocation.delete();
		}
		
		dlLogger.info("Connecting to " + resourceOnWeb);
		// Open a connection to the website.
		URLConnection connection = resourceOnWeb.openConnection();
		long contentLength = connection.getContentLengthLong();
		
		// Warn if the download is of indeterminate size.
		if(contentLength == -1L)
		{
			dlLogger.warning("Server did not provide a file size for the downloaded file!");
		}
		else
		{
			dlLogger.info(resourceOnWeb + " reports size of " + contentLength + " bytes.");
		}
		
		long currentTime = System.currentTimeMillis();
		
		dlLogger.info("Downloading file from " + resourceOnWeb);		
		// Try with resources to download and write the file.
		try(InputStream downloadStream = connection.getInputStream();
			BufferedInputStream downloadStreamBuffer = new BufferedInputStream(downloadStream);
			FileOutputStream output = new FileOutputStream(downloadedLocation);
			BufferedOutputStream out = new BufferedOutputStream(output))
		{
			int bufferSize = 1024; //TODO: Make this alterable
			byte[] buffer = new byte[bufferSize];
			
			int read;
			
			while((read = downloadStreamBuffer.read(buffer, 0, bufferSize)) > -1)
			{
				out.write(buffer, 0, read);
				if(System.currentTimeMillis() - currentTime >= 5000)
				{
					dlLogger.fine("Downloaded " + downloadedLocation.length() + " out of " + contentLength);
					currentTime = System.currentTimeMillis();
				}
			}
			
		}
		
		return downloadedLocation.exists() ? downloadedLocation : null;	
	}
}
