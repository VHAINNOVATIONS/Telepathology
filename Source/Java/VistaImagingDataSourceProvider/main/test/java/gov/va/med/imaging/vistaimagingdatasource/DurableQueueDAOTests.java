package gov.va.med.imaging.vistaimagingdatasource;

import org.junit.Assert;
import org.junit.Test;

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.DurableQueue;
import gov.va.med.imaging.exchange.business.DurableQueueMessage;
import gov.va.med.imaging.url.vista.StringUtils;

import java.util.List;

public class DurableQueueDAOTests{
	@Test
	public void testTranslateFindAll(){
		StringBuilder builder = new StringBuilder();
		builder.append("0^^4\r\n");
		builder.append("IEN^NAME^QUEUE TYPE^ACTIVE^NUM RETRIES^RETRY DELAY IN SECONDS^TRIGGER DELAY IN SECONDS\r\n");
		builder.append("3^ASYNC_ICON_QUEUE^I^1^3^1200^1200\r\n");
		builder.append("2^Async Storage Request Error Queue^E^1^5^1200^1200\r\n");
		builder.append("1^Async Storage Request Queue^S^1^5^1200^1200\r\n");
		builder.append("4^E-Mail Queue^M^1^5^1200^1200");
		DurableQueueDAO dao = new DurableQueueDAO(null);
		List<DurableQueue> queues = dao.translateFindAll(builder.toString());
		Assert.assertTrue(queues.size() == 4);
		for (int i=0; i < queues.size(); i++){
			Assert.assertTrue(queues.get(i).getId() > 0);
			Assert.assertTrue(queues.get(i).getName() != null);
			Assert.assertTrue(queues.get(i).getName().length() > 0);
			Assert.assertTrue(queues.get(i).getType() != null);
			Assert.assertTrue(queues.get(i).getType().length() > 0);
			Assert.assertTrue(queues.get(i).getIsActive());
			Assert.assertTrue(queues.get(i).getRetryDelayInSeconds() == 1200);
			Assert.assertTrue(queues.get(i).getTriggerDelayInSeconds() == 1200);
		}
	}
	
/*	
	@Test
	public void testTranslateDurableQueueMessage(){
		StringBuilder builder = new StringBuilder();
		builder.append("0^^5\r\n");
		builder.append("<QUEUEMESSAGE\r\n");
		builder.append("PK=\"5\"\r\n");
		builder.append("QUEUE=\"4\"\r\n");
		builder.append("PRIORITY=\"50\"\r\n");
		builder.append("ENQUEUEDDATETIME=\"20120206.133321\"\r\n");
		builder.append("EARLIESTDELIVERYDATETIME=\"\"\r\n");
		builder.append("EXPIRATIONDATETIME=\"\"\r\n");
		builder.append("MESSAGEGROUPID=\"vhaiswimgvms107\"\r\n");
		builder.append("MESSAGE=\"Bill.Peterson@va.gov^DICOM Storage SCP Replaced DUP/Illegal UID^P116_SCU -&gt; Warning=Replaced: DUP StdIUID; DUP SerIUID; DUP SOPIUID;=1.2.840.113754.1.4.660.6879793.8749.1.20612.361^0\" >\r\n");
		builder.append("</QUEUEMESSAGE >");
		DurableQueueDAO dao = new DurableQueueDAO(null);
		try {
			DurableQueueMessage message = dao.translateDurableQueueMessage(builder.toString());
			Assert.assertEquals(5, message.getId());
			Assert.assertEquals(4, message.getQueueId());
			Assert.assertEquals(50, message.getPriority());
			//Assert.assertEquals(20120206.133321, message.getExpirationDateTime());
			Assert.assertEquals("", message.getMinDeliveryDateTime());
			Assert.assertEquals("vhaiswimgvms107", message.getMessageGroupId());
			Assert.assertEquals("Bill.Peterson@va.gov^DICOM Storage SCP Replaced DUP/Illegal UID^P116_SCU ->; Warning=Replaced: DUP StdIUID; DUP SerIUID; DUP SOPIUID;=1.2.840.113754.1.4.660.6879793.8749.1.20612.361^0", message.getMessage());
		} catch (MethodException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
*/	
/*	
	@Test
	public void testTranslateDurableQueueMessage() throws Exception{
		StringBuilder builder = new StringBuilder();
		builder.append("0^^2\r\n");
		builder.append("This is a test message.");
		DurableQueueDAO dao = new DurableQueueDAO(null);
		DurableQueueMessage qmessage = dao.translateDurableQueueMessage(builder.toString());
		Assert.assertTrue(qmessage.getId() == 2);
		Assert.assertTrue(qmessage.getMessage().length() > 0);
	}
*/

	
	@Test
	public void testTranslateDurableQueueMessage() throws Exception{
		StringBuilder builder = new StringBuilder();
		builder.append("0^^1\r\n<QUEUEMESSAGE\r\nPK=\"1\"\r\nQUEUE=\"4\"\r\nPRIORITY=\"50\"\r\nENQUEUEDDATETIME=\"20130329.162616\"\r\nEARLIESTDELIVERYDATETIME=\"\"\r\nEXPIRATIONDATETIME=\"\"\r\nMESSAGEGROUPID=\"vhaiswimgvms107\"\r\n");
		builder.append("MESSAGE=\"\r\n");
		builder.append("&lt;gov.va.med.imaging.exchange.business.QueuedEmailMessage&gt;|/|  &lt;recipients&gt;|/|    &lt;string&gt;Bill.Peterson@va.gov&lt;/string&gt;|/|  &lt;/recipients&gt;|/|  &lt;subjectLine&gt;DICOM Storage SCP IOD Violation Error - The Storage process will continue for this obj\r\n");
		builder.append("ect.&lt;/subjectLine&gt;|/|  &lt;messageBody&gt;P116_SCU -&amp;gt; |/|Modality Device: , , &amp;#xd;|/|SOP Class: 1.2.840.10008.5.1.4.1.1.11.2&amp;#xd;|/|Error:: PresentationStateRelationshipMacro/E_REFERENCED_SERIES_SEQUENCE/E_REFERENCED_IMAGE_SEQUENCE/: E_REFERE\r\n");
		builder.append("NCED_SERIES_SEQUENCE/Item[1]/E_REFERENCED_IMAGE_SEQUENCE: Incorrect number of sequence items - got 75 but expected 1-1&amp;#xd;|/||/|SOP Instance UID -&amp;gt; |/|1.3.6.1.4.1.5962.99.1.3084.1360.1364588977572.1.1.1.0.1|/||/|DICOM object not dumped \r\n");
		builder.append("to local file.|/|Notice: The problem may need to be corrected by the device vendor.&lt;/messageBody&gt;|/|  &lt;urgent&gt;false&lt;/urgent&gt;|/|  &lt;retryCount&gt;0&lt;/retryCount&gt;|/|  &lt;messageCount&gt;1&lt;/messageCount&gt;|/|  &lt;bodyByteSize&gt;0&lt;/bodyByteSize&gt;|/|  &lt;dateTimeP\r\n");
		builder.append("osted&gt;|/|    &lt;time&gt;1364588986440&lt;/time&gt;|/|    &lt;timezone&gt;America/New_York&lt;/timezone&gt;|/|  &lt;/dateTimePosted&gt;|/|&lt;/gov.va.med.imaging.exchange.business.QueuedEmailMessage&gt;\" >\r\n");
		builder.append("</QUEUEMESSAGE >");
		DurableQueueDAO dao = new DurableQueueDAO(null);
		DurableQueueMessage qmessage = dao.translateDurableQueueMessage(builder.toString());
		
		String output = StringUtils.displayEncodedChars(qmessage.getMessage());
		System.out.println("Encrypted Message: \n"+output);
		String result = "<gov.va.med.imaging.exchange.business.QueuedEmailMessage>   <recipients>     <string>Bill.Peterson@va.gov</string>   </recipients>   <subjectLine>DICOM Storage SCP IOD Violation Error - The Storage process will continue for this object.</subjectLine>   <messageBody>P116_SCU -&gt;  Modality Device: , , &#xd; SOP Class: 1.2.840.10008.5.1.4.1.1.11.2&#xd; Error:: PresentationStateRelationshipMacro/E_REFERENCED_SERIES_SEQUENCE/E_REFERENCED_IMAGE_SEQUENCE/: E_REFERENCED_SERIES_SEQUENCE/Item[1]/E_REFERENCED_IMAGE_SEQUENCE: Incorrect number of sequence items - got 75 but expected 1-1&#xd;  SOP Instance UID -&gt;  1.3.6.1.4.1.5962.99.1.3084.1360.1364588977572.1.1.1.0.1  DICOM object not dumped to local file. Notice: The problem may need to be corrected by the device vendor.</messageBody>   <urgent>false</urgent>   <retryCount>0</retryCount>   <messageCount>1</messageCount>   <bodyByteSize>0</bodyByteSize>   <dateTimePosted>     <time>1364588986440</time>     <timezone>America/New_York</timezone>   </dateTimePosted> </gov.va.med.imaging.exchange.business.QueuedEmailMessage>";
		Assert.assertTrue(qmessage.getMessage().equals(result));
		//Assert.assertTrue(qmessage.getMessage().length() > 0);
	}

	

}
