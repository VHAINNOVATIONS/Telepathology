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

import gov.va.med.imaging.exchange.business.PersistentEntity;
import gov.va.med.imaging.exchange.business.WorkItem;
import gov.va.med.imaging.exchange.business.WorkItemTag;
import gov.va.med.imaging.exchange.business.storage.exceptions.CreationException;
import gov.va.med.imaging.exchange.business.storage.exceptions.DeletionException;
import gov.va.med.imaging.exchange.business.storage.exceptions.RetrievalException;
import gov.va.med.imaging.exchange.business.storage.exceptions.UpdateException;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO;

import java.util.HashMap;
import java.util.List;

public class BaseWorkListDAO<T extends PersistentEntity> extends EntityDAO<T>
{
	protected static final String FIELD_SEPARATOR = StringUtils.BACKTICK;
	protected static final String LINE_SEPARATOR = "\r\n";
	protected static final String RPC_STATUS_SUCCESS = "0";
	protected static final String RPC_STATUS_ERROR = "1";

	@Override
	public T translateCreate(T t, String returnValue) throws CreationException
	{
		String[] results = StringUtils.Split(returnValue, FIELD_SEPARATOR);

		// There should be two parts: In both cases the first part is the status code. For failure, the
		// second part is the error message. For success, the second part is the new IEN
		if (results[0].equals("0"))
		{
			int id = Integer.parseInt(results[1]);
			t.setId(id);
		}
		else
		{
			throw new CreationException(results[1]);
		}
		
		return t;
	}


	@Override
	public T translateUpdate(T t, String returnValue) throws UpdateException
	{
		String[] results = StringUtils.Split(returnValue, FIELD_SEPARATOR);

		if (!results[0].equals("0"))
		{
			
			throw new UpdateException(results[1]);
		}
		
		return t;
	}

	@Override
	public void translateDelete(String returnValue) throws DeletionException
	{
		String[] results = StringUtils.Split(returnValue, FIELD_SEPARATOR);

		if (!results[0].equals("0"))
		{
			
			throw new DeletionException(results[1]);
		}
	}
	
	protected void checkRetrievalStatus(String[] resultLines) throws RetrievalException
	{
		// No results. See if it was just an empty list, or an error
		String[] statusLine = StringUtils.Split(resultLines[0], FIELD_SEPARATOR);
		if (!statusLine[0].equals("0"))
		{
			throw new RetrievalException(statusLine[1]);
		}
	}
	
	protected boolean getBooleanValue(String value) 
	{
		// TODO Auto-generated method stub
		return (value.equals("1") ? true : false);
	}

	
}
