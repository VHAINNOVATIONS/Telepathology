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
import gov.va.med.imaging.core.interfaces.IProcedureFilterTerms;
import gov.va.med.imaging.core.interfaces.exceptions.ApplicationConfigurationException;
import gov.va.med.imaging.core.interfaces.exceptions.ProcedureFilterTermsException;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.AbstractList;
import java.util.ArrayList;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.log4j.Logger;

/**
 * @author VHAISWBUCKD
 *
 */
public abstract class ProcedureFilterTerms implements IProcedureFilterTerms
{
	// public static
	public static boolean loadFilterTermsfromFile = true; // for use by unit tests - not production code
	// protected
	protected String procedureFilterTermsFilespec = null;
	// private
	private IAppConfiguration appConfiguration = null;
	private final Logger logger = Logger.getLogger(this.getClass());
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock readLock = rwl.readLock();
    private final Lock writeLock = rwl.writeLock();
    private AbstractList<String> filterTerms = new ArrayList<String>();
    
	//-----------------------------------
	// constructor
	//-----------------------------------

	public ProcedureFilterTerms() 
	{
	}
    
	public ProcedureFilterTerms(IAppConfiguration appConfiguration) 
	{
		this.appConfiguration = appConfiguration;
	}

	//-----------------------------------
	// IProcedureFilterTerms public properties
	//-----------------------------------
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.interfaces.IProcedureFilterTerms#getFilterTerms()
	 */
	public AbstractList<String> getFilterTerms()
	{
		this.readLock.lock();
		try
		{
			return new ArrayList<String>(this.filterTerms); 
		}
		finally {this.readLock.unlock();}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.interfaces.IProcedureFilterTerms#setFilterTerms(java.util.Collection)
	 */
	public void setFilterTerms(AbstractList<String> filterTerms)
	{
		this.writeLock.lock();
		try
		{
			this.filterTerms.clear();
			this.filterTerms.addAll(filterTerms);
			this.validateAndReduceFilterTerms();
		}
		finally {this.writeLock.unlock();}
	}

	public void setFilterTerm(String filterTerm)
	{
		this.writeLock.lock();
		try
		{
			if (filterTerm != null && filterTerm.length() > 0 && !this.filterTerms.contains(filterTerm))
			{
				this.filterTerms.add(filterTerm);
			}
		}
		finally {this.writeLock.unlock();}
	}
	
	//-----------------------------------
	// IProcedureFilterTerms methods
	//-----------------------------------

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.interfaces.IProcedureFilterTerms#loadProcedureFilterTermsFromFile()
	 */
	public boolean loadProcedureFilterTermsFromFile()
	{
		boolean success = false;
		String filespec = null;

		this.readLock.lock();
		try {filespec = this.procedureFilterTermsFilespec;}
		finally {this.readLock.unlock();}
		success = this.loadProcedureFilterTermsFromFile(filespec);
		return success;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.interfaces.IProcedureFilterTerms#saveProcedureFilterTermsToFile()
	 */
	public boolean saveProcedureFilterTermsToFile()
	{
		boolean success = false;
		String filespec = null;

		this.readLock.lock();
		try {filespec = this.procedureFilterTermsFilespec;}
		finally {this.readLock.unlock();}
		success = this.saveProcedureFilterTermsToFile(filespec);
		return success;
	}

	//-----------------------------------
	// public methods
	//-----------------------------------
	
	/**
	 * @param set the fully qualified file name that specifies where the persisted state of this object is stored
	 */
	public void setProcedureFilterTermsFilespec(String fileSpec) {
		this.writeLock.lock();
		try {this.procedureFilterTermsFilespec = fileSpec;}
		finally {this.writeLock.unlock();}
	}

	/**
	 * @return the fully qualified file that contains the persisted state of this object
	 */
	public String getProcedureFilterTermsFilespec()
	{
		String fileSpec = null;
		this.readLock.lock();
		try {fileSpec = this.procedureFilterTermsFilespec;}
		finally {this.readLock.unlock();}
		return fileSpec;
	}
	
	/**
	 * @param terms - the ProcedureFilterTerms object that contains the state to use
	 * This public method takes the state of the passed terms param and overlays it onto the current instance
	 * Note: transient properties do not participate
	 */
	public void assignState(ProcedureFilterTerms terms)
	{
		if (terms != null)
		{
			this.writeLock.lock();
			terms.readLock.lock();
			try
			{
				this.filterTerms.clear();
				this.filterTerms.addAll(terms.filterTerms);
			}
			finally
			{
				this.writeLock.unlock();
				terms.readLock.unlock();
			}
		}
	}

	/*
	 * The following method was generated by Eclipse. If you regenerate this method, note that:
	 * 	transient property filterTermsFilename does not participate.
	 * 	locking must be added back in manually
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		this.readLock.lock();
		try
		{
			result = prime * result
					+ ((filterTerms == null) ? 0 : filterTerms.hashCode());
		}
		finally {this.readLock.unlock();}
		return result;
	}

	/* 
	 * The following method was generated by Eclipse. If you regenerate this method, note that:
	 * 	transient property filterTermsFilename does not participate.
	 * 	locking must be added back in manually
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		this.readLock.lock();
		try
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final ProcedureFilterTerms other = (ProcedureFilterTerms) obj;
			if (filterTerms == null)
			{
				if (other.filterTerms != null)
					return false;
			}
			else if (!filterTerms.equals(other.filterTerms))
				return false;
			return true;
		}
		finally {this.readLock.unlock();}
	}
	
	//-----------------------------------
	// protected methods
	//-----------------------------------

	/**
	 * Derived classes call this from their Spring init method
	 * @throws ProcedureFilterTermsException
	 */
	protected void init() throws ProcedureFilterTermsException
	{
		this.markPropertyAsTransient("procedureFilterTermsFilespec"); //determined at run time from derived class
	} 
	
	protected String getProcedureFilterTermFilespec(String filename)
	{
		String fileSpec = null;
		try
		{
			fileSpec = this.appConfiguration.getVixConfigurationDirectory();
		}
		catch (ApplicationConfigurationException ex)
		{
			fileSpec = System.getenv("vixconfig");
			if (fileSpec == null)
			{
				fileSpec = "c:/VixConfig";
			}
		}
		// add the trailing file separator character if necessary
		if (!fileSpec.endsWith("\\") || !fileSpec.endsWith("/"))
		{
			fileSpec += "/";
		}
		fileSpec += filename;

		return fileSpec;
	}

	/**
	 * Initialize the list of procedure filter terms. This method is called from the derived classes init method
	 * if the filter terms cannot be loaded from file.
	 */
	protected abstract void initProcedureFilterTermsFromCode(); 
	
	//-----------------------------------
	// private methods
	//-----------------------------------
	
	public boolean loadProcedureFilterTermsFromFile(String fileSpec) {
		boolean success = false;
		XMLDecoder xmlDecoder = null;
		ProcedureFilterTerms terms = null;

		if (fileSpec != null)
		{
			File filterTermFile = new File(fileSpec);
			if (filterTermFile.exists())
			{
				try
				{
					xmlDecoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(fileSpec))); // throws FileNotFoundException
					terms = (ProcedureFilterTerms) xmlDecoder.readObject();
					if (terms != null)
					{
						this.assignState(terms); // this method obtains a write lock
						logger.info(this.getClass().getSimpleName() + " loaded from " + fileSpec);
						success = true;
					}
				}
				catch (FileNotFoundException ex)
				{
					logger.error(ex.getMessage());
				}
				catch (ArrayIndexOutOfBoundsException ex)
				{
					logger.error("ProcedureFilterTerms.loadProcedureFilterTermsFromFile: ArrayIndexOutOfBoundsException : " + ex.getMessage());
				}
				finally
				{
					if (xmlDecoder != null)
					{
						xmlDecoder.close();
					}
				}
			}
		}
		return success;
	}

	public boolean saveProcedureFilterTermsToFile(String fileSpec) {
		boolean success = false;
		XMLEncoder xmlEncoder = null;

		this.readLock.lock();
		try
		{
			xmlEncoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(fileSpec)));
			xmlEncoder.writeObject(this);
			logger.info(this.getClass().getSimpleName() + " saved to: " + fileSpec);
			success = true;
		}
		catch (FileNotFoundException ex)
		{
			logger.error("ProcedureFilterTerms.saveProcedureFilterTermsToFile: " + ex.getMessage());
		}
		finally
		{
			this.readLock.unlock();
			if (xmlEncoder != null)
			{
				xmlEncoder.close();
			}
		}

		return success;
	}
	
	
	/**
	 * Mark a bean property as transient so that the XmlEncoder will not include it as part of the
	 * persisted state.
	 * @param propertyName - the property to mark as transient
	 * @throws ProcedureFilterTermsException
	 */
	private void markPropertyAsTransient(String propertyName) throws ProcedureFilterTermsException
	{
		BeanInfo info = null;
		boolean propertySet = false;
		
		try {
			info = Introspector.getBeanInfo(this.getClass());
			PropertyDescriptor[] propertyDescriptors = info.getPropertyDescriptors();
			for (int i = 0; i < propertyDescriptors.length; ++i)
			{
				PropertyDescriptor pd = propertyDescriptors[i];
				if (pd.getName().equals(propertyName))
				{
					pd.setValue("transient", Boolean.TRUE);
					propertySet = true;
					break;
				}
			}
			if (!propertySet)
			{
				throw new ProcedureFilterTermsException("markPropertyAsTransient: property not found : " + propertyName);
			}
		}
		catch (IntrospectionException ex) {
			throw new ProcedureFilterTermsException(ex.getMessage());
		}
	}

	/**
	 * Validate each filter term to ensure it is not the empty string. Remove any filter terms that are.
	 * Note: this method should only be called within the scope of a write lock
	 */
	private void validateAndReduceFilterTerms()
	{
		for (int i = this.filterTerms.size() - 1 ; i >= 0 ; i--)
		{
			if (this.filterTerms.get(i).length() == 0)
			{
				this.filterTerms.remove(i);
			}
		}
	}
}
