/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 26, 2009
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
package gov.va.med.imaging.datasource;

import java.util.List;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.CprsIdentifier;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.annotations.SPI;
import gov.va.med.imaging.exchange.business.Study;

/**
 *  * This class defines the Service Provider Interface (SPI) for the ExternalPackageDataSource class. 
 * All the abstract methods in this class must be implemented by each 
 * data source service provider who wishes to supply the implementation of a 
 * ExternalPackageDataSource for a particular datasource type.
 * 
 * This SPI is used to translate data from a non-Imaging package and return Imaging data.
 * 
 * @author vhaiswwerfej
 *
 */
@SPI(description="This SPI is used to translate data from a non-Imaging package and return Imaging data.")
public interface ExternalPackageDataSourceSpi 
extends VersionableDataSourceSpi
{
	
	/**
	 * Given an external CPRS identifier string, retrieve the study that is associated with that string. 
	 * 
	 * @param patientIcn The patient the cprs Identifier is for
	 * @param cprsIdentifier The value recieved from CPRS
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract List<Study> getStudiesFromCprsIdentifier(RoutingToken globalRoutingToken,
			String patientIcn, CprsIdentifier cprsIdentifier)
	throws MethodException, ConnectionException;

}
