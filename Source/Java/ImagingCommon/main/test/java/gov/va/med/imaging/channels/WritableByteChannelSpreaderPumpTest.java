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
package gov.va.med.imaging.channels;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

import gov.va.med.imaging.StorageTestUtility;

/**
 * @author beckey
 * created: Aug 12, 2005 at 11:15:36 AM
 *
 * This class tests the multi-threaded Channel copy
 */
public class WritableByteChannelSpreaderPumpTest
extends AbstractByteChannelTest
{

	/*
	 * Class under test for void Catalog(String)
	 */
	public void testCatalogString()
	{
		TestInputData testData = new TestInputData();
		try
		{
			WritableByteChannelSpreaderPump cat = new WritableByteChannelSpreaderPump(
				testData.getTestDataAsReadableByteChannel(),
				new WritableByteChannel[] {Channels.newChannel(new OutputStream(){public void write(int i){}})}
			);

			cat.copy();
		}
		catch (IOException X)
		{
			X.printStackTrace();
		}
	}

	/**
	 * 
	 *
	 */
	public void testCatalogStringStringArray()
	{
		TestInputData testData = new TestInputData();
		File[] destinationFiles = new File[10];
		WritableByteChannel[] destinations = new WritableByteChannel[10];
		
		try
		{
			for(int n=0; n < destinations.length; ++n)
			{
				destinationFiles[n] = File.createTempFile(getClass().getSimpleName(), ("." + testData.getTestDataExtension()) );
				destinationFiles[n].deleteOnExit();
				destinations[n] = Channels.newChannel( 
					new FileOutputStream(destinationFiles[n])
				);
				System.out.println( "ByteChannelSpreaderTest writing to [" + destinationFiles[n].getPath() );
			}
			
			WritableByteChannelSpreaderPump cat = new WritableByteChannelSpreaderPump(
				testData.getTestDataAsReadableByteChannel(),
				new WritableByteChannel[] 
				{
					destinations[0],
					destinations[1],
					destinations[2],
					destinations[3],
					destinations[4],
					destinations[5],
					destinations[6],
					destinations[7],
					destinations[8],
					destinations[9],
				}
			);
			cat.copy();

			for(int n=0; n < destinations.length; ++n)
				destinations[n].close();
			
			for(int n=0; n < destinationFiles.length; ++n)
				StorageTestUtility.compareFileToInputStream(destinationFiles[n], testData.getTestDataAsInputStream(), "none");
			
			for(int n=0; n < destinations.length; ++n)
				destinationFiles[n].delete();
		}
		catch (IOException X)
		{
			X.printStackTrace();
		}
	}	
}


