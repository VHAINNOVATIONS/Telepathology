package gov.va.med.imaging.exchange.storage.cache.mock;

import java.util.zip.Checksum;

public class MockChecksum
implements Checksum
{
	@Override
    public long getValue()
    {
        return 42;
    }

	@Override
    public void reset()
    {
    }

	@Override
    public void update(int b)
    {
    }

	@Override
    public void update(byte[] b, int off, int len)
    {
    }
}
