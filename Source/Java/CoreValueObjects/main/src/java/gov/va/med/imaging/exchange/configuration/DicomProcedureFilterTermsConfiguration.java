/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 19, 2010
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
package gov.va.med.imaging.exchange.configuration;

import java.util.ArrayList;
import java.util.List;

import gov.va.med.imaging.facade.configuration.AbstractBaseFacadeConfiguration;
import gov.va.med.imaging.facade.configuration.FacadeConfigurationFactory;
import gov.va.med.imaging.facade.configuration.exceptions.CannotLoadConfigurationException;

/**
 * @author vhaiswwerfej
 *
 */
public class DicomProcedureFilterTermsConfiguration
extends AbstractBaseFacadeConfiguration
{
	private List<String> filterTerms = new ArrayList<String>();
	
	public DicomProcedureFilterTermsConfiguration()
	{
		super();
	}

	public List<String> getFilterTerms()
	{
		return filterTerms;
	}

	public void setFilterTerms(List<String> filterTerms)
	{
		this.filterTerms = filterTerms;
	}

	@Override
	public AbstractBaseFacadeConfiguration loadDefaultConfiguration()
	{
		filterTerms.add("CT");
		filterTerms.add("RAD CT");
		filterTerms.add("CR");
		filterTerms.add("RAD CR");
		filterTerms.add("DR");
		filterTerms.add("RAD DR");
		filterTerms.add("MR");
		filterTerms.add("RAD MR");
		filterTerms.add("US");
		filterTerms.add("RAD US");
		filterTerms.add("DX");
		filterTerms.add("RAD DX");
		filterTerms.add("RF");
		filterTerms.add("RAD RF");
		filterTerms.add("NM");
		filterTerms.add("RAD NM");
		filterTerms.add("XA");
		filterTerms.add("RAD XA");
		filterTerms.add("PT");
		filterTerms.add("RAD PT");
		filterTerms.add("DS");
		filterTerms.add("RAD DS");
		// 	P104 - allow additional procedure filter terms to be served to the DoD though the exchange interface
		filterTerms.add("MG");
		filterTerms.add("RAD MG");
		filterTerms.add("AS");
		filterTerms.add("RAD AS");
		filterTerms.add("BI");
		filterTerms.add("RAD BI");
		filterTerms.add("CD");
		filterTerms.add("RAD CD");
		filterTerms.add("CF");
		filterTerms.add("RAD CF");
		filterTerms.add("CP");
		filterTerms.add("RAD CP");
		filterTerms.add("CS");
		filterTerms.add("RAD CS");
		filterTerms.add("DD");
		filterTerms.add("RAD DD");
		filterTerms.add("DG");
		filterTerms.add("RAD DG");
		filterTerms.add("DM");
		filterTerms.add("RAD DM");
		filterTerms.add("EC");
		filterTerms.add("RAD EC");
		filterTerms.add("ES");
		filterTerms.add("RAD ES");
		filterTerms.add("FA");
		filterTerms.add("RAD FA");
		filterTerms.add("FS");
		filterTerms.add("RAD FS");
		filterTerms.add("GM");
		filterTerms.add("RAD GM");
		filterTerms.add("IO");
		filterTerms.add("RAD IO");
		filterTerms.add("LP");
		filterTerms.add("RAD LP");
		filterTerms.add("LS");
		filterTerms.add("RAD LS");
		filterTerms.add("MA");
		filterTerms.add("RAD MA");
		filterTerms.add("MS");
		filterTerms.add("RAD MS");
		filterTerms.add("RG");
		filterTerms.add("RAD RG");
		filterTerms.add("ST");
		filterTerms.add("RAD ST");
		filterTerms.add("TG");
		filterTerms.add("RAD TG");
		filterTerms.add("XC");
		filterTerms.add("RAD XC");
		return this;
	}
	
	public static synchronized DicomProcedureFilterTermsConfiguration getConfiguration()	
	{
		try
		{
			return FacadeConfigurationFactory.getConfigurationFactory().getConfiguration(
					DicomProcedureFilterTermsConfiguration.class);
		}
		catch(CannotLoadConfigurationException clcX)
		{
			// no need to log, already logged
			return null;
		}
	}
	
	public static void main(String [] args)
	{
		DicomProcedureFilterTermsConfiguration config = getConfiguration();
		config.storeConfiguration();
	}

}
