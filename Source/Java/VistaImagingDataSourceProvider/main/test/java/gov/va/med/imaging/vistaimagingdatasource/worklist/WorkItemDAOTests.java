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

package gov.va.med.imaging.vistaimagingdatasource.worklist;

import static org.junit.Assert.*;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.WorkItemCounts;
import gov.va.med.imaging.exchange.business.storage.Provider;
import gov.va.med.imaging.exchange.business.storage.exceptions.CreationException;
import gov.va.med.imaging.exchange.business.storage.exceptions.RetrievalException;
import gov.va.med.imaging.exchange.business.storage.exceptions.UpdateException;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class WorkItemDAOTests
{
	private WorkItemDAO dao = new WorkItemDAO();

	@Test
	public void testTranslateGetWorkItemCounts() throws MethodException
	{

		StringBuilder builder = new StringBuilder();
		builder.append("0`4\r\n");
		builder.append("SUBTYPE`STATUS`COUNT\r\n");
		builder.append("DicomCorrect`New`78\r\n");
		builder.append("DicomCorrect`Staged`7\r\n");
		builder.append("DirectImport`FailedImport`1\r\n");
		builder.append("DirectImport`New`4");

		WorkItemCounts counts = dao.translateGetWorkItemCounts(builder.toString());

		Assert.assertEquals(78, counts.getCountForSubtypeAndStatus("DicomCorrect", "New"));
		Assert.assertEquals(7, counts.getCountForSubtypeAndStatus("DicomCorrect", "Staged"));
		Assert.assertEquals(1, counts.getCountForSubtypeAndStatus("DirectImport", "FailedImport"));
		Assert.assertEquals(4, counts.getCountForSubtypeAndStatus("DirectImport", "New"));
		Assert.assertEquals(0, counts.getCountForSubtypeAndStatus("DirectImport", "ReadyForImport"));
		Assert.assertEquals(0, counts.getCountForSubtypeAndStatus("NetworkImport", "New"));

	}
	
	@Test
	public void testTranslateAddTagToExistingWorkItem1(){
		StringBuilder builder = new StringBuilder();
		builder.append("-11`Invalid data:1.2.840.113754.1.4.660.35168913351.165165161.3251.8646.1.91108~");
		builder.append("1.2.840.113754.1.4.660.35168913351.165165161.3251.8646.1.91108.221~");
		builder.append("1.2.840.113754.1.7.660.391725968.25884136615.3516516.9.10.2746.33.24.56555224.1~");
		builder.append("PxE52yps002Qrs813m6kwjY1003k4bFF Field: #2006.94111,1");
		
		try{
			dao.translateAddTagToExistingWorkItem(builder.toString());
			fail();
		}
		catch(UpdateException uX){
			fail();
		}
		catch(MethodException mX){
			
		}	
	}

	@Test
	public void testTranslateAddTagToExistingWorkItem2(){
		StringBuilder builder = new StringBuilder();
		builder.append("0`Successful");
		
		try{
			dao.translateAddTagToExistingWorkItem(builder.toString());
		}
		catch(UpdateException uX){
			fail();
		}
		catch(MethodException mX){
			fail();
		}
	}

	@Test
	public void testTranslateAddTagToExistingWorkItem3(){
		StringBuilder builder = new StringBuilder();
		builder.append("-9`No work item ID provided");
		
		try{
			dao.translateAddTagToExistingWorkItem(builder.toString());
			fail();
		}
		catch(UpdateException uX){
		}
		catch(MethodException mX){
			fail();			
		}
	}

	@Test
	public void testTranslateAddTagToExistingWorkItem4(){
		StringBuilder builder = new StringBuilder();
		builder.append("-5`No work item with matching ID provided");
		
		try{
			dao.translateAddTagToExistingWorkItem(builder.toString());
			fail();
		}
		catch(UpdateException uX){
		}
		catch(MethodException mX){
			fail();			
		}
	}

	@Test
	public void testTranslateUpdateWorkItem1(){
		StringBuilder builder = new StringBuilder();
		builder.append("-11`Invalid data:1.2.840.113754.1.4.660.35168913351.165165161.3251.8646.1.91108~");
		builder.append("1.2.840.113754.1.4.660.35168913351.165165161.3251.8646.1.91108.221~");
		builder.append("1.2.840.113754.1.7.660.391725968.25884136615.3516516.9.10.2746.33.24.56555224.1~");
		builder.append("PxE52yps002Qrs813m6kwjY1003k4bFF Field: #2006.94111,1");
		
		try{
			dao.translateUpdateWorkItem(builder.toString());
			fail();
		}
		catch(UpdateException uX){
			fail();
		}
		catch(MethodException mX){
			
		}	
	}

	@Test
	public void testTranslateUpdateWorkItem2(){
		StringBuilder builder = new StringBuilder();
		builder.append("0`Successful");
		
		try{
			dao.translateUpdateWorkItem(builder.toString());
		}
		catch(UpdateException uX){
			fail();
		}
		catch(MethodException mX){
			fail();
		}
	}

	@Test
	public void testTranslateUpdateWorkItem3(){
		StringBuilder builder = new StringBuilder();
		builder.append("-9`No work item ID provided");
		
		try{
			dao.translateUpdateWorkItem(builder.toString());
			fail();
		}
		catch(UpdateException uX){
		}
		catch(MethodException mX){
			fail();			
		}
	}

	@Test
	public void testTranslateUpdateWorkItem4(){
		StringBuilder builder = new StringBuilder();
		builder.append("-6`Work item ID not found");
		
		try{
			dao.translateUpdateWorkItem(builder.toString());
			fail();
		}
		catch(UpdateException uX){
		}
		catch(MethodException mX){
			fail();			
		}
	}

}