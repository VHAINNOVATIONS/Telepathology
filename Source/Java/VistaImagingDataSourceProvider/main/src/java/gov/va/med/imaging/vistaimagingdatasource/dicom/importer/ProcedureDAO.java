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

package gov.va.med.imaging.vistaimagingdatasource.dicom.importer;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.dicom.importer.Procedure;
import gov.va.med.imaging.exchange.business.dicom.importer.exceptions.OutsideLocationConfigurationException;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.util.ArrayList;
import java.util.List;

public class ProcedureDAO extends BaseImporterDAO<Procedure>
{
	private static String FIND_ALL = "MAGV GET RADIOLOGY PROCEDURES";
	//
	// Constructor
	//
	public ProcedureDAO(){}
	public ProcedureDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}
	
	public List<Procedure> findProceduresForDivision(String divisionId, String imagingLocationIen, String procedureIen) 
	throws MethodException, ConnectionException 
	{
		VistaQuery vm = generateFindProceduresForDivisionQuery(divisionId, imagingLocationIen, procedureIen);
		String results = executeRPC(vm);
		return translateFindProceduresForDivision(results);
		
	}

	public VistaQuery generateFindProceduresForDivisionQuery(String divisionId, String imagingLocationIen, String procedureIen) throws MethodException
	{
		VistaQuery vm = new VistaQuery(FIND_ALL);
		vm.addParameter(VistaQuery.LITERAL, divisionId);
		vm.addParameter(VistaQuery.LITERAL, imagingLocationIen);
		vm.addParameter(VistaQuery.LITERAL, procedureIen);
		
		return vm;
	}
	
	public List<Procedure> translateFindProceduresForDivision(String results) 
	throws MethodException
	{
		try
		{
			// Create the OrderingLocation list
			List<Procedure> procedures = new ArrayList<Procedure>();
			
			// Split the result into lines
			String[] lines = StringUtils.Split(results, LINE_SEPARATOR);
	
			// Populate the list, starting with line 1 (if it exists). Line 0 gives the record count.
			for (int i=1; i<lines.length; i++)
			{
				String[] fields = StringUtils.Split(lines[i], StringUtils.CARET);
				
				int ien = 0;
				int imagingTypeId = 0;
				int imagingLocationId = 0;
				int hospitalLocationId = 0;
				
				try
				{
					ien = Integer.parseInt(fields[1]);
					imagingTypeId = Integer.parseInt(fields[4]);
					imagingLocationId = Integer.parseInt(fields[6]);
					hospitalLocationId = Integer.parseInt(fields[7]);
				}
				catch (Exception e)
				{
					logger.error("Parse exception creating Procedure: [" + fields[0] + "], [" 
								 + fields[1] + "], [" + fields[4] + "], [" + fields[6] +"], [" + fields[7] +"]");
				}
				
				procedures.add(new Procedure(fields[0], 
						ien, 
						imagingTypeId,
						imagingLocationId,
						hospitalLocationId));
			}
			
			return procedures;	
		}
		catch (Exception e)
		{
			logger.error("Unable to retrieve procedure list...", e);
			throw new OutsideLocationConfigurationException();
		}
	}
	
}
