/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Aug 27, 2012
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWPETERB
  Description: 

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
package gov.va.med.imaging.exchange.business.dicom;


import gov.va.med.imaging.exchange.business.DicomCommonTestBase;

import org.junit.After;
import org.junit.Before;

/**
 * @author VHAISWPETERB
 *
 */
public class DicomAETest extends DicomCommonTestBase{

	/**
	 * @param arg0
	 */
	public DicomAETest(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public void testServiceAndRoleDuplicateService1(){
		DicomAE ae = new DicomAE("FUDGE", "660");
		ae.addAEServiceAndRole("C-FIND", "SCU");
		ae.addAEServiceAndRole("C-MOVE", "SCU");
		ae.addAEServiceAndRole("C-STORE", "SCU");
		ae.addAEServiceAndRole("C-STORE", "SCP");
		
		assertTrue(ae.isServiceAndRoleValid("C-STORE", "SCU"));
		assertTrue(ae.isServiceAndRoleValid("C-STORE", "SCP"));
		assertTrue(ae.isServiceAndRoleValid("C-FIND", "SCU"));
		assertTrue(ae.isServiceAndRoleValid("C-MOVE", "SCU"));
	}
	
	public void testServiceAndRoleDuplicateService2(){
		DicomAE ae = new DicomAE("FUDGE", "660");
		ae.addAEServiceAndRole("C-STORE", "SCP");
		ae.addAEServiceAndRole("C-FIND", "SCU");
		ae.addAEServiceAndRole("C-MOVE", "SCU");
		ae.addAEServiceAndRole("C-STORE", "SCU");
		
		assertTrue(ae.isServiceAndRoleValid("C-STORE", "SCU"));
		assertTrue(ae.isServiceAndRoleValid("C-STORE", "SCP"));
		assertTrue(ae.isServiceAndRoleValid("C-FIND", "SCU"));
		assertTrue(ae.isServiceAndRoleValid("C-MOVE", "SCU"));
		assertFalse(ae.isServiceAndRoleValid("C-MOVE", "SCP"));
	}
	
	public void testServiceAndRoleDuplicateService3(){
		DicomAE ae = new DicomAE("FUDGE", "660");
		ae.addAEServiceAndRole("C-FIND", "SCU");
		ae.addAEServiceAndRole("C-MOVE", "SCU");
		ae.addAEServiceAndRole("C-STORE", "SCU");
		ae.addAEServiceAndRole("C-STORE", "SCP");
		
		assertTrue(ae.isServiceAndRoleValid("C-Store", "SCU"));
		assertTrue(ae.isServiceAndRoleValid("C-STORE", "SCP"));
		assertTrue(ae.isServiceAndRoleValid("C-Find", "SCU"));
		assertTrue(ae.isServiceAndRoleValid("C-Move", "SCU"));
		assertFalse(ae.isServiceAndRoleValid("C-FIND", "SCP"));
	}
	
	public void testRemoteAETitleNotValid(){
		DicomAE ae = new DicomAE("FUDGE", "660");
		ae.setResultCode(-1);
		ae.addAEServiceAndRole("C-FIND", "SCU");
		ae.addAEServiceAndRole("C-MOVE", "SCU");
		ae.addAEServiceAndRole("C-STORE", "SCU");
		ae.addAEServiceAndRole("C-STORE", "SCP");
		
		assertFalse(ae.isRemoteAEValid());
	}

	public void testRemoteAETitleValidWithZero(){
		DicomAE ae = new DicomAE("FUDGE", "660");
		ae.setResultCode(0);
		ae.addAEServiceAndRole("C-FIND", "SCU");
		ae.addAEServiceAndRole("C-MOVE", "SCU");
		ae.addAEServiceAndRole("C-STORE", "SCU");
		ae.addAEServiceAndRole("C-STORE", "SCP");
		
		assertTrue(ae.isRemoteAEValid());
	}

	public void testRemoteAETitleValidWithPositiveNumber(){
		DicomAE ae = new DicomAE("FUDGE", "660");
		ae.setResultCode(7);
		ae.addAEServiceAndRole("C-FIND", "SCU");
		ae.addAEServiceAndRole("C-MOVE", "SCU");
		ae.addAEServiceAndRole("C-STORE", "SCU");
		ae.addAEServiceAndRole("C-STORE", "SCP");
		
		assertTrue(ae.isRemoteAEValid());
	}

}
