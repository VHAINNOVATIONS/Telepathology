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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

/**
 * 
 * @author beckey
 * created: Sep 23, 2005 at 4:03:54 PM
 *
 * This class will read from an array of readable byte channels in order.
 * It will read from the first channel in the array until it returns EOF,
 * then the second, etc ...
 */
public class GatheringByteChannel
implements ReadableByteChannel
{
	private ReadableByteChannel[] sourceChannels = null;
	private int currentChannelIndex = 0;
	
	public GatheringByteChannel(ReadableByteChannel[] sourceChannels)
	{
		this.sourceChannels = sourceChannels;
	}

	public int read(ByteBuffer dst) throws IOException
	{
		int channelReadResult = sourceChannels[currentChannelIndex].read(dst);
		if(channelReadResult == -1)
		{
			++currentChannelIndex;
			if(currentChannelIndex >= sourceChannels.length)
				channelReadResult = -1;
			else
				channelReadResult = read(dst);
		}
		return channelReadResult;
	}

	public boolean isOpen()
	{
		for(int n=0; n < sourceChannels.length; ++n)
			if(sourceChannels[n].isOpen())
				return true;
		
		return false;
	}

	public void close() throws IOException
	{
		for(int n=0; n < sourceChannels.length; ++n)
			sourceChannels[n].close();
	}

}
