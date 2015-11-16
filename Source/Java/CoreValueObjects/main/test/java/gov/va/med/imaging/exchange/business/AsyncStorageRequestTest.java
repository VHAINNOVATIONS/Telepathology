package gov.va.med.imaging.exchange.business;

import junit.framework.TestCase;
import gov.va.med.imaging.exchange.business.storage.AsyncStorageRequest;

public class AsyncStorageRequestTest extends TestCase {
	public void testSerialize()
	{
		AsyncStorageRequest request = new AsyncStorageRequest("Token");
		request.setNumAttempts(0);
		String result = request.serializeUsingXStream();
		System.out.print(result);
	}
}
