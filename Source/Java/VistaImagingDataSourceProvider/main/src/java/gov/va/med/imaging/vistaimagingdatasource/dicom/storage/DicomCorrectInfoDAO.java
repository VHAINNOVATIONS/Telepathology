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

package gov.va.med.imaging.vistaimagingdatasource.dicom.storage;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.dicom.DicomCorrectInfo;
import gov.va.med.imaging.exchange.business.dicom.exceptions.DicomCorrectException;
import gov.va.med.imaging.exchange.business.storage.exceptions.CreationException;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.util.HashMap;

public class DicomCorrectInfoDAO extends EntityDAO<DicomCorrectInfo>
{
	private static final String RPC_GET_COUNT = "MAGV DICOM GET COUNT";
	private static final String MACHINE_ID = "MACHID";
	private static final String SERVICE_TYPE = "SERVTYPE";
	//
	// RPC Names
	//
	private String RPC_CREATE = "MAGV CREATE DICOM FAILED IMAGE";
	
	// Constructor
	public DicomCorrectInfoDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}
	
	public Integer getDicomCorrectCount(DicomCorrectInfo info)
	throws MethodException, ConnectionException 
	{
		VistaQuery vm = generateGetDicomCorrectCountQuery(info);
		return translateGetDicomCorrectCount(executeRPC(vm));
	}

	public VistaQuery generateGetDicomCorrectCountQuery(DicomCorrectInfo info) 
	{
		VistaQuery vm = new VistaQuery(RPC_GET_COUNT);
		vm.addParameter(VistaQuery.LITERAL, info.getHostname());
		vm.addParameter(VistaQuery.LITERAL, info.getServiceType());
		return vm;
	}

	public Integer translateGetDicomCorrectCount(String returnValue)
	throws DicomCorrectException
	{
		String[] lines = StringUtils.Split(returnValue, LINE_SEPARATOR);
		if (lines[0].startsWith("0"))
		{
			String[] fields = StringUtils.Split(lines[0], DB_OUTPUT_SEPARATOR1);
			return Integer.parseInt(fields[2]);
		}
		else
		{
			String[] resultFields = StringUtils.Split(lines[0], DB_OUTPUT_SEPARATOR1);
			throw new DicomCorrectException(resultFields[1]);
		}
	}



}
