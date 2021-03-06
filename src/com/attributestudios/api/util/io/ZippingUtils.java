package com.attributestudios.api.util.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.attributestudios.api.util.logging.LoggingUtil;
import com.attributestudios.api.util.logging.SimpleLogFormatter;

/**
 * Supplies quick and simple utility functions for compressing
 * 	and expanding files in the '.zip' file format.
 * 
 * @author  Bridger Maskrey (maskreybe@live.com)
 * @version 1.0.0
 * @date.	2014-08-18
 */
public class ZippingUtils
{
	/**
	 * Main logger for the ZippingUtils class.
	 */
	private static Logger zipLogger = LoggingUtil.constructLogger("Zip / Unzip", new SimpleLogFormatter());
	
	/**
	 * Expands files from a specified zipped folder to the specified directory.
	 * @param zippedFile The file to unzip.
	 * @param unzipDirectory The directory in which to unzip the file.
	 * @throws IOException Thrown when for some reason the file cannot be unzipped.
	 */
	public void extract(File zippedFile, File unzipDirectory) throws IOException
	{
		zipLogger.info("Unzipping file " + zippedFile + " to directory " + unzipDirectory);
		
		if(unzipDirectory.exists() && !unzipDirectory.isDirectory())
		{
			zipLogger.severe("Unable to unzip! " + unzipDirectory + " exists, and it is not a directory!");
			throw new IOException(unzipDirectory + " is not a directory.");
		}
		
		// Make parent directory.
		unzipDirectory.mkdirs();
		
		// Try with resources to load zip stream, etc.
		try(ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zippedFile)))
		{
			ZipEntry entry;
						
			while((entry = zipInputStream.getNextEntry()) != null)
			{
				String entryFileName = entry.getName();
				
				zipLogger.finer("Found zip entry " + entryFileName);
				
				String entryFullPath = (unzipDirectory + File.separator + entryFileName).trim();
				
				zipLogger.finer("Full output path enumerated to " + entryFullPath);
				
				File entryOutputFile = new File(entryFullPath);
				
				if(entry.isDirectory())
				{
					// Entry is in fact a directory, so lets just make directories and leave it at that.
					entryOutputFile.mkdirs();
					zipLogger.finer("Entry " + entryFileName + " is a directory. Moving to next.");
					continue;
				}
				
				zipLogger.finer("Make parent directories for " + entryFileName);
				entryOutputFile.getParentFile().mkdirs();
				
				zipLogger.finer("Writing " + entryFileName + " to uncompressed file " + entryOutputFile);
				
				// Another try with resources, this time to write the contents of the entry file.
				try(BufferedOutputStream entryOutputStreamBuffer = new BufferedOutputStream(new FileOutputStream(entryOutputFile)))
				{
					int bufferSize = 1024;
					byte[] buffer = new byte[bufferSize];
					int read;
					
					while((read = zipInputStream.read(buffer, 0, bufferSize)) >= 0)
					{
						entryOutputStreamBuffer.write(buffer, 0, read);
					}
				}
			}
		}
	}
	
	//TODO: zipFiles(File outputFile, File directoryToZip)
	
	//TODO: zipFiles(File outputFile, File...listFiles)
	
	//TODO: zipFiles(File outputFile, Container<File> files)
	
	//TODO: zipFiles(File outputFile, Container<File[]> files)
}
