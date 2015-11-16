/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Nov, 2009
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswlouthj
  Description: DICOM Study cache manager. Maintains the cache of study instances
  			   and expires old studies after 15 minutes. 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */

package gov.va.med.imaging.vistaimagingdatasource.storage;

import gov.va.med.imaging.exchange.business.storage.Artifact;
import gov.va.med.imaging.exchange.business.storage.ArtifactInstance;

import java.util.List;

import junit.framework.Assert;


public class BaseArtifactDAOTests
{
	protected void validateArtifactAndKeys(Artifact artifact)
	{
		validateArtifact(artifact);
		validateKeyList(artifact);
	}
	
	protected void validateArtifactAndKeysAndInstances(Artifact artifact)
	{
		validateArtifactAndKeys(artifact);
		validateArtifactInstances(artifact, 2);
		validateNoArtifactRetentionPoliciesOrFulfillments(artifact);
	}
	
	protected void validateArtifactGraph(Artifact artifact)
	{
		validateArtifactAndKeys(artifact);
		validateArtifactInstances(artifact, 2);
		validateArtifactAndKeysAndInstances(artifact);
		validateArtifactRetentionPoliciesAndFulfillments(artifact);
	}
	
	// Validation methods
	private void validateArtifact(Artifact artifact)
	{
		Assert.assertTrue(artifact.getId()==3);
		Assert.assertTrue(artifact.getArtifactToken().equals("MYTOKEN00003"));
		Assert.assertTrue(artifact.getArtifactDescriptorId() == 2);
		Assert.assertTrue(artifact.getKeyListId() == 15);
		Assert.assertTrue(artifact.getSizeInBytes() == 3728);
		Assert.assertTrue(artifact.getCRC().equals("42FD200003"));
		Assert.assertTrue(artifact.getCreatedBy().equals("Master MS"));
		Assert.assertTrue(artifact.getCreatedDateTime().equals("20100408.162353"));
		Assert.assertTrue(artifact.getLastAccessDateTime().equals("20100408.222222"));
	}

	private void validateKeyList(Artifact artifact)
	{
		Assert.assertTrue(artifact.getKeyList().size() == 4);
		
		//
		// Check that levels are sorted correctly
		//
		Assert.assertTrue(artifact.getKeyList().get(0).getLevel() == 1);
		Assert.assertTrue(artifact.getKeyList().get(1).getLevel() == 2);
		Assert.assertTrue(artifact.getKeyList().get(2).getLevel() == 3);
		Assert.assertTrue(artifact.getKeyList().get(3).getLevel() == 4);
		
		//
		// Check that values are correct
		//
		Assert.assertTrue(artifact.getKeyList().get(0).getValue().equals("Patient=1"));
		Assert.assertTrue(artifact.getKeyList().get(1).getValue().equals("StudyUID=1.2.840.113754.1.4.671.6939684.8882.1.31506.11"));
		Assert.assertTrue(artifact.getKeyList().get(2).getValue().equals("SeriesUID=1.2.840.113754.1.4.671.6939684.8882.2.31506.111"));
		Assert.assertTrue(artifact.getKeyList().get(3).getValue().equals("SopIUID=1.2.840.113754.1.4.671.6939684.8882.3.31506.1111"));
		
	}
	
	private void validateArtifactInstances(Artifact artifact, int expectedCount)
	{
		if (expectedCount == 0)
		{
			Assert.assertTrue(artifact.getArtifactInstances() == null);
		}
		else
		{
			List<ArtifactInstance> instances = artifact.getArtifactInstances();
			
			// Validate the count
			Assert.assertTrue(instances.size() == 2);
			
			// Validate the first one
			Assert.assertTrue(instances.get(0).getId() == 1);
			Assert.assertTrue(instances.get(0).getArtifactId() == 2);
			Assert.assertTrue(instances.get(0).getProviderId() == 3);
			Assert.assertTrue(instances.get(0).getCreatedDateTime().equals("20100408.111111"));
			Assert.assertTrue(instances.get(0).getLastAccessDateTime().equals("20100408.222222"));
			Assert.assertTrue(instances.get(0).getUrl().equals("http://vhaiswimmclu4/user1$/CsabaT/ImageCache/imageout1.dcm"));
			
			// Validate the second one
			Assert.assertTrue(instances.get(1).getId() == 4);
			Assert.assertTrue(instances.get(1).getArtifactId() == 5);
			Assert.assertTrue(instances.get(1).getProviderId() == 6);
			Assert.assertTrue(instances.get(1).getCreatedDateTime().equals("20100408.444444"));
			Assert.assertTrue(instances.get(1).getLastAccessDateTime().equals("20100408.555555"));
			Assert.assertTrue(instances.get(1).getUrl().equals("http://vhaiswimmclu4/user1$/CsabaT/ImageCache/imageout2.dcm"));
			
		}
	}
	
	private void validateArtifactRetentionPoliciesAndFulfillments(Artifact artifact)
	{
	}
	
	private void validateNoArtifactRetentionPoliciesOrFulfillments(Artifact artifact) 
	{
		// Verify no ArtifactRetentionPolicies
		Assert.assertTrue(artifact.getArtifactRetentionPolicies() == null);
		
		// Verify no Fulfillments
		List<ArtifactInstance> instances = artifact.getArtifactInstances();
		if (instances != null)
		{
			for (ArtifactInstance instance : instances)
			{
				Assert.assertTrue(instance.getRetentionPolicyFulfillments() == null);
			}
		}
	}

	//
	// XML strings
	//
	protected String getArtifactAndKeyListXmlString()
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append("0^^20\r\n");
		builder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");
		builder.append("<ARTIFACTS>\r\n");
		builder.append(" <ARTIFACT\r\n");
		builder.append(" PK=\"3\"\r\n");
		builder.append(" ARTIFACTTOKEN=\"MYTOKEN00003\"\r\n");
		builder.append(" ARTIFACTDESCRIPTORFK=\"2\"\r\n");
		builder.append(" KEYLISTFK=\"15\"\r\n");
		builder.append(" SIZEINBYTES=\"3728\"\r\n");
		builder.append(" CRC=\"42FD200003\"\r\n");
		builder.append(" CREATEDBY=\"Master MS\"\r\n");
		builder.append(" CREATEDDATETIME=\"20100408.162353\"\r\n");
		builder.append(" LASTACCESSDATETIME=\"20100408.222222\" >\r\n");
		builder.append(" <KEYS>\r\n");
		builder.append(" <KEY VALUE=\"Patient=1\" LEVEL=\"1\" />\r\n");
		builder.append(" <KEY VALUE=\"StudyUID=1.2.840.113754.1.4.671.6939684.8882.1.31506.11\" LEVEL=\"2\" />\r\n");
		builder.append(" <KEY VALUE=\"SeriesUID=1.2.840.113754.1.4.671.6939684.8882.2.31506.111\" LEVEL=\"3\" />\r\n");
		builder.append(" <KEY VALUE=\"SopIUID=1.2.840.113754.1.4.671.6939684.8882.3.31506.1111\" LEVEL=\"4\" />\r\n");
		builder.append(" </KEYS>\r\n");
		builder.append(" </ARTIFACT>\r\n");
		builder.append(" </ARTIFACTS>");
		
		return builder.toString();

	}

	protected String getArtifactAndInstancesXmlString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("0^^20\r\n");
		builder.append(" <?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");
		builder.append(" <ARTIFACTS>\r\n");
		builder.append(" <ARTIFACT\r\n");
		builder.append(" PK=\"3\"\r\n");
		builder.append(" ARTIFACTTOKEN=\"MYTOKEN00003\"\r\n");
		builder.append(" ARTIFACTDESCRIPTORFK=\"2\"\r\n");
		builder.append(" KEYLISTFK=\"15\"\r\n");
		builder.append(" SIZEINBYTES=\"3728\"\r\n");
		builder.append(" CRC=\"42FD200003\"\r\n");
		builder.append(" CREATEDBY=\"Master MS\"\r\n");
		builder.append(" CREATEDDATETIME=\"20100408.162353\"\r\n");
		builder.append(" LASTACCESSDATETIME=\"20100408.222222\" >\r\n");
		builder.append(" <KEYS>\r\n");
		builder.append(" <KEY VALUE=\"SeriesUID=1.2.840.113754.1.4.671.6939684.8882.2.31506.111\" LEVEL=\"3\" />\r\n");
		builder.append(" <KEY VALUE=\"StudyUID=1.2.840.113754.1.4.671.6939684.8882.1.31506.11\" LEVEL=\"2\" />\r\n");
		builder.append(" <KEY VALUE=\"Patient=1\" LEVEL=\"1\" />\r\n");
		builder.append(" <KEY VALUE=\"SopIUID=1.2.840.113754.1.4.671.6939684.8882.3.31506.1111\" LEVEL=\"4\" />\r\n");
		builder.append(" </KEYS>\r\n");
		builder.append(" <ARTIFACTINSTANCES>\r\n");
		builder.append(" <ARTIFACTINSTANCE\r\n");
		builder.append(" PK=\"1\"\r\n");
		builder.append(" ARTIFACTFK=\"2\"\r\n");
		builder.append(" PROVIDERFK=\"3\"\r\n");
		builder.append(" CREATEDDATETIME=\"20100408.111111\"\r\n");
		builder.append(" LASTACCESSDATETIME=\"20100408.222222\"\r\n");
		builder.append(" URL=\"http://vhaiswimmclu4/user1$/CsabaT/ImageCache/imageout1.dcm\" >\r\n");
		builder.append(" </ARTIFACTINSTANCE >\r\n");
		builder.append(" <ARTIFACTINSTANCE\r\n");
		builder.append(" PK=\"4\"\r\n");
		builder.append(" ARTIFACTFK=\"5\"\r\n");
		builder.append(" PROVIDERFK=\"6\"\r\n");
		builder.append(" CREATEDDATETIME=\"20100408.444444\"\r\n");
		builder.append(" LASTACCESSDATETIME=\"20100408.555555\"\r\n");
		builder.append(" URL=\"http://vhaiswimmclu4/user1$/CsabaT/ImageCache/imageout2.dcm\" >\r\n");
		builder.append(" </ARTIFACTINSTANCE >\r\n");
		builder.append(" </ARTIFACTINSTANCES>\r\n");
		builder.append(" </ARTIFACT>\r\n");
		builder.append("</ARTIFACTS>");

		return builder.toString();
	}
	protected String getFullArtifactGraphXmlString()
	{
		StringBuilder builder = new StringBuilder();

		builder.append("0^^20\r\n");
		builder.append(" <?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");
		builder.append(" <ARTIFACTS>\r\n");
		builder.append(" <ARTIFACT\r\n");
		builder.append(" PK=\"3\"\r\n");
		builder.append(" ARTIFACTTOKEN=\"MYTOKEN00003\"\r\n");
		builder.append(" ARTIFACTDESCRIPTORFK=\"2\"\r\n");
		builder.append(" KEYLISTFK=\"15\"\r\n");
		builder.append(" SIZEINBYTES=\"3728\"\r\n");
		builder.append(" CRC=\"42FD200003\"\r\n");
		builder.append(" CREATEDBY=\"Master MS\"\r\n");
		builder.append(" CREATEDDATETIME=\"20100408.162353\"\r\n");
		builder.append(" LASTACCESSDATETIME=\"20100408.222222\" >\r\n");
		builder.append(" <KEYS>\r\n");
		builder.append(" <KEY VALUE=\"SeriesUID=1.2.840.113754.1.4.671.6939684.8882.2.31506.111\" LEVEL=\"3\" />\r\n");
		builder.append(" <KEY VALUE=\"StudyUID=1.2.840.113754.1.4.671.6939684.8882.1.31506.11\" LEVEL=\"2\" />\r\n");
		builder.append(" <KEY VALUE=\"Patient=1\" LEVEL=\"1\" />\r\n");
		builder.append(" <KEY VALUE=\"SopIUID=1.2.840.113754.1.4.671.6939684.8882.3.31506.1111\" LEVEL=\"4\" />\r\n");
		builder.append(" </KEYS>\r\n");
		builder.append(" <ARTIFACTINSTANCES>\r\n");
		builder.append(" <ARTIFACTINSTANCE\r\n");
		builder.append(" PK=\"1\"\r\n");
		builder.append(" ARTIFACTFK=\"2\"\r\n");
		builder.append(" PROVIDERFK=\"3\"\r\n");
		builder.append(" CREATEDDATETIME=\"20100408.111111\"\r\n");
		builder.append(" LASTACCESSDATETIME=\"20100408.222222\"\r\n");
		builder.append(" URL=\"http://vhaiswimmclu4/user1$/CsabaT/ImageCache/imageout1.dcm\" >\r\n");
		builder.append(" </ARTIFACTINSTANCE >\r\n");
		builder.append(" <ARTIFACTINSTANCE\r\n");
		builder.append(" PK=\"4\"\r\n");
		builder.append(" ARTIFACTFK=\"5\"\r\n");
		builder.append(" PROVIDERFK=\"6\"\r\n");
		builder.append(" CREATEDDATETIME=\"20100408.444444\"\r\n");
		builder.append(" LASTACCESSDATETIME=\"20100408.555555\"\r\n");
		builder.append(" URL=\"http://vhaiswimmclu4/user1$/CsabaT/ImageCache/imageout2.dcm\" >\r\n");
		builder.append(" </ARTIFACTINSTANCE >\r\n");
		builder.append(" </ARTIFACTINSTANCES>\r\n");
		builder.append(" <ARTIFACTRETENTIONPOLICYS>\r\n");
		builder.append(" <ARTIFACTRETENTIONPOLICY\r\n");
		builder.append(" PK=\"1\"\r\n");
		builder.append(" ARTIFACTFK=\"1\"\r\n");
		builder.append(" RETENTIONPOLICYFK=\"1\"\r\n");
		builder.append(" CREATEDDATETIME=\"20100408.162353\"\r\n");
		builder.append(" ISACTIVE=\"1\"\r\n");
		builder.append(" ISSATISFIED=\"1\"\r\n");
		builder.append(" SATISFIEDDATETIME=\"20100408.162353\" >\r\n");
		builder.append(" <RETENTIONPOLICYFULFILLMENTS>\r\n");
		builder.append(" <RETENTIONPOLICYFULFILLMENT\r\n");
		builder.append(" PK=\"1\"\r\n");
		builder.append(" ARTIFACTRETENTIONPOLICYFK=\"1\"\r\n");
		builder.append(" ARTIFACTINSTANCEFK=\"1\"\r\n");
		builder.append(" CREATEDDATETIME=\"20100408.162353\" >\r\n");
		builder.append(" </RETENTIONPOLICYFULFILLMENT >\r\n");
		builder.append(" <RETENTIONPOLICYFULFILLMENT\r\n");
		builder.append(" PK=\"2\"\r\n");
		builder.append(" ARTIFACTRETENTIONPOLICYFK=\"1\"\r\n");
		builder.append(" ARTIFACTINSTANCEFK=\"2\"\r\n");
		builder.append(" CREATEDDATETIME=\"20100408.162353\" >\r\n");
		builder.append(" </RETENTIONPOLICYFULFILLMENT >\r\n");
		builder.append(" </RETENTIONPOLICYFULFILLMENTS>\r\n");
		builder.append(" </ARTIFACTRETENTIONPOLICY >\r\n");
		builder.append(" <ARTIFACTRETENTIONPOLICY\r\n");
		builder.append(" PK=\"2\"\r\n");
		builder.append(" ARTIFACTFK=\"1\"\r\n");
		builder.append(" RETENTIONPOLICYFK=\"2\"\r\n");
		builder.append(" CREATEDDATETIME=\"20100408.162353\"\r\n");
		builder.append(" ISACTIVE=\"1\"\r\n");
		builder.append(" ISSATISFIED=\"0\"\r\n");
		builder.append(" SATISFIEDDATETIME=\"\" >\r\n");
		builder.append(" <RETENTIONPOLICYFULFILLMENTS>\r\n");
		builder.append(" <RETENTIONPOLICYFULFILLMENT\r\n");
		builder.append(" PK=\"3\"\r\n");
		builder.append(" ARTIFACTRETENTIONPOLICYFK=\"2\"\r\n");
		builder.append(" ARTIFACTINSTANCEFK=\"1\"\r\n");
		builder.append(" CREATEDDATETIME=\"20100408.162353\" >\r\n");
		builder.append(" </RETENTIONPOLICYFULFILLMENT >\r\n");
		builder.append(" <RETENTIONPOLICYFULFILLMENT\r\n");
		builder.append(" PK=\"4\"\r\n");
		builder.append(" ARTIFACTRETENTIONPOLICYFK=\"2\"\r\n");
		builder.append(" ARTIFACTINSTANCEFK=\"2\"\r\n");
		builder.append(" CREATEDDATETIME=\"20100408.162353\" >\r\n");
		builder.append(" </RETENTIONPOLICYFULFILLMENT >\r\n");
		builder.append(" </RETENTIONPOLICYFULFILLMENTS>\r\n");
		builder.append(" </ARTIFACTRETENTIONPOLICY >\r\n");
		builder.append(" </ARTIFACTRETENTIONPOLICYS>\r\n");
		builder.append(" </ARTIFACT>\r\n");
		builder.append("</ARTIFACTS>");
		
		return builder.toString();
	}
}
