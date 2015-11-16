/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Dec 01, 2007
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWBUCKD
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

import gov.va.med.imaging.core.interfaces.IAppConfiguration;
import gov.va.med.imaging.core.interfaces.exceptions.ProcedureFilterTermsException;

import java.util.ArrayList;


/**
 * @author VHAISWBUCKD
 * Do not add member variables here. Instead put them in ProcedureFilterTerms.
 */
public class ExchangeInterfaceProcedureFilterTerms extends ProcedureFilterTerms
{
	public final static String procedureFilterTermsFilename = "ExchangeInterfaceProcedureFilterTerms.xml";

	/**
	 * @param appConfiguration
	 */
	public ExchangeInterfaceProcedureFilterTerms(IAppConfiguration appConfiguration)
	{
		super(appConfiguration);
	}

	/**
	 * constructor used by XMLDecoder
	 */
	public ExchangeInterfaceProcedureFilterTerms()
	{
		super();
	}
	
	/**
	 * Initialization method called by Spring.
	 * Note that this method is not a part of the IProcedureFilterTerms interface
	 */
	public void init() throws ProcedureFilterTermsException
	{
		boolean success = false;
		super.init();
		String filespec = this.getProcedureFilterTermFilespec(procedureFilterTermsFilename);
		this.setProcedureFilterTermsFilespec(filespec);

		if (ProcedureFilterTerms.loadFilterTermsfromFile == true) 
		{
			success = this.loadProcedureFilterTermsFromFile();
		}
		
		if (success == false)
		{
			this.initProcedureFilterTermsFromCode();
			this.saveProcedureFilterTermsToFile();
		}
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.configuration.ProcedureFilterTerms#initProcedureFilterTermsFromCode()
	 */
	protected void initProcedureFilterTermsFromCode() 
	{
	    ArrayList<String> filterTerms = new ArrayList<String>();
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
		this.setFilterTerms(filterTerms);
	}

	/**
	 * Used by the VIX Installer to create the procedure filter terms associated with the current VIX release and
	 * persist to configuration file in the VixConfig directory.
	 * @param args - no arguments at this time
	 */
	public static void main(String [] args)
	{
		ExchangeInterfaceProcedureFilterTerms terms = new ExchangeInterfaceProcedureFilterTerms();
		String fileSpec = System.getenv("vixconfig"); // VIX Installer will ensure environment variable exists
		// add the trailing file separator character if necessary
		if (!fileSpec.endsWith("\\") || !fileSpec.endsWith("/"))
		{
			fileSpec += "/";
		}
		fileSpec += procedureFilterTermsFilename; 
		terms.procedureFilterTermsFilespec = fileSpec;
		terms.initProcedureFilterTermsFromCode();
		terms.saveProcedureFilterTermsToFile();
	}

}
