/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Nov 20, 2009
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
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
package gov.va.med.imaging;

import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author vhaiswwerfej
 *
 */
public class TransactionContextHelper 
{
	
	public static void setTransactionContextFields(String requestType, String patientIcn)
	{
		setTransactionContextFields(requestType, patientIcn, null);
	}

	public static void setTransactionContextFields(String requestType, String patientIcn, String urn)
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setTransactionId(new GUID().toString());
		transactionContext.setRequestType("VIX Web App " + requestType);
		transactionContext.setPatientID(patientIcn);
		if(urn != null)
		{
			transactionContext.setUrn(urn);
		}
	}
}
