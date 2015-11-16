/*
 * Copyright (c) 2005, United States Veterans Administration
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list 
 * of conditions and the following disclaimer in the documentation and/or other 
 * materials provided with the distribution.
 * Neither the name of the United States Veterans Administration nor the names of its 
 * contributors may be used to endorse or promote products derived from this software 
 * without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED 
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR 
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH 
 * DAMAGE.
 */
 package gov.va.med.imaging;

import java.io.*;

import junit.framework.Assert;

public class StorageTestUtility
{
	/**
	 * Compare the contents of two files and call Assert.fail() if they differ
	 * 
	 * @param file1
	 * @param file2
	 * @param guid
	 * @throws FileNotFoundException
	 */
	public static void compareFiles(File file1, File file2, String guid)
	throws FileNotFoundException
	{
		compareFileToInputStream(file1, new FileInputStream(file2), guid);
	}

	/**
	 * Compare the contents of a file and an InputStream and call Assert.fail() if they differ
	 * 
	 * @param file1
	 * @param data2
	 * @param guid
	 * @throws FileNotFoundException
	 */
	public static void compareFileToInputStream(File file1, InputStream data2, String guid)
	throws FileNotFoundException
	{
		compareStreams(new FileInputStream(file1), data2, guid, file1.getPath());
	}
	/**
	 * Compare the contents of 2 InputStreams and call Assert.fail() if they differ
	 * 
	 * @param thing1
	 * @param thing2
	 * @param guid
	 * @param path
	 */
	public static void compareStreams(InputStream thing1, InputStream thing2, String guid, String path)
	{
		byte[] thing1Buffer = new byte[2048];
		byte[] thing2Buffer = new byte[2048];
		
		try
		{
			while(true)
			{
				int thing1BytesRead = thing1.read(thing1Buffer); 
				int thing2BytesRead = thing2.read(thing2Buffer);
				
				if( thing1BytesRead <= 0 || thing2BytesRead <= 0)
					break;

				if( thing1BytesRead != thing2BytesRead)
					Assert.fail("GUID [" + guid + "] stream length does not match [" + path + "]" );

				for(int n=0; n < thing1BytesRead; ++n)
					if(thing1Buffer[n] != thing2Buffer[n])
						Assert.fail("GUID [" + guid + "] stream contents do not match [" + path + "]" );
			}
		}
		catch (IOException e)
		{
			Assert.fail("GUID [" + guid + "] stream or [" + path + "] unreadable [" + e.getMessage() + "]" );
		}
	}
	
	/**
	 * 
	 * @param inStream
	 * @param outStream
	 * @param localBufferLength
	 * @throws IOException
	 */
	public static void pipeStreams(InputStream inStream, OutputStream outStream, int localBufferLength)
	throws IOException
	{
		byte[] localBuffer = new byte[localBufferLength];
		for(int bytesRead = inStream.read(localBuffer);
			bytesRead > 0;
			bytesRead = inStream.read(localBuffer) )
				outStream.write(localBuffer, 0, bytesRead);
		
	}

}
