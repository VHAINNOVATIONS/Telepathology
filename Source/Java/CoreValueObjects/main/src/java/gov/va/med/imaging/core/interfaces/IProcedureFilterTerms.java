package gov.va.med.imaging.core.interfaces;

import gov.va.med.imaging.core.interfaces.exceptions.ProcedureFilterTermsException;

import java.util.AbstractList;

public interface IProcedureFilterTerms
{
	//-----------------------------------
	// properties
	//-----------------------------------
	
	/**
	 * @return a collection of procedure filter terms
	 */
	public abstract AbstractList<String> getFilterTerms();
	
	/**
	 * Reload the internal collection of filter terms.
	 * @param filterTerms - a collection of filter terms to initialize from. 
	 */
	public abstract void setFilterTerms(AbstractList<String> filterTerms);
	
	/**
	 * Add a single filter term to the internal filter term collection. 
	 * @param filterTerm - the filter term to add
	 */
	public abstract void setFilterTerm(String filterTerm);
	
	//-----------------------------------
	// persistence methods
	//-----------------------------------
	/**
	 * @throws ProcedureFilterTermsException
	 * @return true if the filter terms were successfully loaded from file
	 */
	public abstract boolean loadProcedureFilterTermsFromFile() throws ProcedureFilterTermsException;
	
	/**
	 * @throws ProcedureFilterTermsException
	 * @return true if the filter terms were successfully saved to file
	 */
	public abstract boolean saveProcedureFilterTermsToFile() throws ProcedureFilterTermsException;

}	
