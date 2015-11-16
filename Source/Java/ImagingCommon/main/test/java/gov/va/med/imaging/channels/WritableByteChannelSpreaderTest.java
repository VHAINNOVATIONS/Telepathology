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

import gov.va.med.imaging.StorageTestUtility;
import gov.va.med.imaging.channels.events.StorageByteChannelEvent;
import gov.va.med.imaging.channels.events.StorageByteChannelListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class WritableByteChannelSpreaderTest
extends AbstractByteChannelTest
implements StorageByteChannelListener
{
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}
	
	public void testStaticSingleThreadWrite()
	{
		TestInputData testData = new TestInputData();
		File[] destinationFiles = new File[10];
		WritableByteChannel[] destinationChannels = new WritableByteChannel[10];
		
		try
		{
			// create destination files
			for(int n=0; n < destinationChannels.length; ++n)
			{
				destinationFiles[n] = File.createTempFile(this.getClass().getSimpleName(), ("." + testData.getTestDataExtension()) );
				destinationFiles[n].deleteOnExit();
				destinationChannels[n] = Channels.newChannel( 
					new FileOutputStream(destinationFiles[n])
				);
				System.out.println( this.getClass().getSimpleName() + ".testStaticSingleThreadWrite writing to [" + destinationFiles[n].getPath() );
			}
			
			ByteBuffer buffy = ByteBuffer.allocateDirect(1024);
			ReadableByteChannel inChannel = testData.getTestDataAsReadableByteChannel();
			
			// write the source channel to all of the destinations
			while( inChannel.read(buffy) > 0 )
			{
				buffy.flip();
				WritableByteChannelSpreader.write(buffy, destinationChannels);
				buffy.clear();
			}
			inChannel.close();

			// close the destination (file) channels
			for(int n=0; n < destinationChannels.length; ++n)
				destinationChannels[n].close();
			
			// compare the file written to the original content
			for(int n=0; n < destinationFiles.length; ++n)
				StorageTestUtility.compareFileToInputStream(destinationFiles[n], testData.getTestDataAsInputStream(), "none");
			
			// delete the files
			for(int n=0; n < destinationChannels.length; ++n)
				destinationFiles[n].delete();			
		}
		catch (IOException X)
		{
			X.printStackTrace();
		}
	}

	/*
	 * Test method for 'gov.va.med.imaging.storage.impl.WritableByteChannelSpreader.write(ByteBuffer)'
	 */
	public void testWrite()
	{
		TestInputData testData = new TestInputData();
		File[] destinationFiles = new File[10];
		WritableByteChannel[] destinations = new WritableByteChannel[10];
		
		try
		{
			// create destination files
			for(int n=0; n < destinations.length; ++n)
			{
				destinationFiles[n] = File.createTempFile(this.getClass().getSimpleName(), ("." + testData.getTestDataExtension()) );
				destinationFiles[n].deleteOnExit();
				destinations[n] = Channels.newChannel( 
					new FileOutputStream(destinationFiles[n])
				);
				System.out.println( this.getClass().getSimpleName() + " writing to [" + destinationFiles[n].getPath() );
			}
			
			WritableByteChannelSpreader outChannel = new WritableByteChannelSpreader(
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
				},
				"WritableByteChannelSpreader"
			);
			outChannel.addStorageByteChannelListener(this);
			
			ReadableByteChannel inChannel = testData.getTestDataAsReadableByteChannel();

			pipeReadableByteChannelToWritableChannel(inChannel, outChannel);
			
			inChannel.close();
			outChannel.close();
			
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

	public void storageByteChannelClosed(StorageByteChannelEvent event)
	{
		System.out.println(event.toString());
	}

	public void storageByteChannelRead(StorageByteChannelEvent event)
	{
		System.out.println(event.toString());
	}

	public void storageByteChannelWrite(StorageByteChannelEvent event)
	{
		System.out.println(event.toString());
	}

	
}
