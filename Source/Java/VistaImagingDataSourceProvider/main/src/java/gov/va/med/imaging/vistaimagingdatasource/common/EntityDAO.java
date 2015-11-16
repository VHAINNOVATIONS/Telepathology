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
package gov.va.med.imaging.vistaimagingdatasource.common;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.InvalidUserCredentialsException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.exceptions.DaoMethodNotImplementedException;
import gov.va.med.imaging.datasource.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.exchange.business.PersistentEntity;
import gov.va.med.imaging.exchange.business.storage.exceptions.CreationException;
import gov.va.med.imaging.exchange.business.storage.exceptions.DeletionException;
import gov.va.med.imaging.exchange.business.storage.exceptions.RetrievalException;
import gov.va.med.imaging.exchange.business.storage.exceptions.UpdateException;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.vistadatasource.session.VistaSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

public abstract class EntityDAO<T extends PersistentEntity> {
	protected static final String FIELD_SEPARATOR1 = StringUtils.TILDE; // P34 initialization RPCs result separator
	protected static final String FIELD_SEPARATOR2 = StringUtils.COMMA; // p34 initialization RPCs data separator
//	protected char separator = '~';
	protected char dbSeparator = '`'; // Backtick  -- P34 db input name-value pair separator, and output result
	protected static final String DB_OUTPUT_SEPARATOR1 = StringUtils.BACKTICK; // -- P34 db output result pair separator
	protected static final String DB_OUTPUT_SEPARATOR2 = StringUtils.STICK;;   // -- P34 db output name-value pair separator
	protected static final String LINE_SEPARATOR = "\r\n";
	protected static final int MAX_M_STRING_LENGTH = 240; // *** <-- 240 is correct

	protected VistaSession session;
	protected VistaSessionFactory sessionFactory;

	protected Logger logger = Logger.getLogger(this.getClass());

	protected void setSessionFactory(VistaSessionFactory sessionFactory) 
	{
		this.sessionFactory = sessionFactory;
	}

	protected VistaSession getVistaSession() throws IOException, ConnectionException, MethodException 
	{
		if (session == null) {
			session = sessionFactory.getVistaSession();
		}
		return session;
	}
	

	//
	// Execute an RPC
	//
	public String executeRPC(VistaQuery vm) throws MethodException, ConnectionException {
		String result = null;
		VistaSession localVistaSession = null;
		try 
		{
			logVistaQuery(vm);
			localVistaSession = getVistaSession();
			result = localVistaSession.call(vm);
			logVistaQueryResults(vm, result);
			
		} catch (IOException e) {
			throw new ConnectionException(e);
		} catch (VistaMethodException e) {
			throw new MethodException(e.getMessage());
		} catch (InvalidVistaCredentialsException e) {
			throw new InvalidCredentialsException(e.getMessage());
		}
		catch(InvalidUserCredentialsException icX)
		{
			throw icX;
		}
		catch (Exception e) {
			throw new MethodException(e.getMessage());
		} finally {
			try {
				if(localVistaSession != null)
					localVistaSession.close();
			} catch (Throwable x) {
			}
		}

		// Return the result
		return result;

	}


	//
	// Create and supporting methods
	//
	public T create(T t) throws MethodException, ConnectionException 
	{
		VistaQuery vm = generateCreateQuery(t);
		return translateCreate(t, executeRPC(vm));
	}

	public VistaQuery generateCreateQuery(T t) throws MethodException
	{
		throw new DaoMethodNotImplementedException();
	}

	public T translateCreate(T t, String returnValue) throws MethodException, CreationException
	{
		throw new DaoMethodNotImplementedException();
	}

	//
	// Get Entity by Id and supporting methods
	//
	public T getEntityById(String id) throws MethodException, ConnectionException 
	{
		VistaQuery vm = generateGetEntityByIdQuery(id);
		return translateGetEntityById(id, executeRPC(vm));
	}

	public VistaQuery generateGetEntityByIdQuery(String id) throws MethodException
	{
		throw new DaoMethodNotImplementedException();
	}

	public T translateGetEntityById(String id, String returnValue) throws MethodException, RetrievalException
	{
		throw new DaoMethodNotImplementedException();
	}


	//
	// Get by Criteria and supporting methods
	//
	public T getEntityByCriteria(Object criteria) throws MethodException, ConnectionException 
	{
		VistaQuery vm = generateGetEntityByCriteriaQuery(criteria);
		return translateGetEntityByCriteria(criteria, executeRPC(vm));
	}

	public VistaQuery generateGetEntityByCriteriaQuery(Object criteria) throws MethodException
	{
		throw new DaoMethodNotImplementedException();
	}

	public T translateGetEntityByCriteria(Object criteria, String returnValue) throws MethodException, RetrievalException
	{
		throw new DaoMethodNotImplementedException();
	}


	//
	// Get by Criteria and supporting methods
	//
	public T getEntityByExample(T t) throws MethodException, ConnectionException 
	{
		VistaQuery vm = generateGetEntityByExampleQuery(t);
		return translateGetEntityByExample(t, executeRPC(vm));
	}

	public VistaQuery generateGetEntityByExampleQuery(T t) throws MethodException
	{
		throw new DaoMethodNotImplementedException();
	}

	public T translateGetEntityByExample(T t, String returnValue) throws MethodException, RetrievalException
	{
		throw new DaoMethodNotImplementedException();
	}

	//
	// Retrieve by Example and supporting methods
	//
	public List<T> findByExample(T t) throws MethodException, ConnectionException 
	{
		VistaQuery vm = generateFindByExampleQuery(t);
		return translateFindByExample(t, executeRPC(vm));
	}

	public VistaQuery generateFindByExampleQuery(T t) throws MethodException
	{
		throw new DaoMethodNotImplementedException();
	}

	public List<T> translateFindByExample(T t, String returnValue) throws MethodException, RetrievalException
	{
		throw new DaoMethodNotImplementedException();
	}

	//
	// Retrieve by Criteria and supporting methods
	//
	public List<T> findByCriteria(Object criteria) throws MethodException, ConnectionException 
	{
		VistaQuery vm = generateFindByCriteriaQuery(criteria);
		return translateFindByCriteria(criteria, executeRPC(vm));
	}

	public VistaQuery generateFindByCriteriaQuery(Object criteria) throws MethodException
	{
		throw new DaoMethodNotImplementedException();
	}

	public List<T> translateFindByCriteria(Object criteria, String returnValue) throws MethodException, RetrievalException
	{
		throw new DaoMethodNotImplementedException();
	}

	//
	// Retrieve All and supporting methods
	//
	public List<T> findAll() throws MethodException, ConnectionException 
	{
		VistaQuery vm = generateFindAllQuery();
		return translateFindAll(executeRPC(vm));
	}

	public VistaQuery generateFindAllQuery() throws MethodException
	{
		throw new DaoMethodNotImplementedException();
	}

	public List<T> translateFindAll(String returnValue) throws MethodException, RetrievalException
	{
		throw new DaoMethodNotImplementedException();
	}

	//
	// Update and supporting methods
	//
	public T update(T t) throws MethodException, ConnectionException 
	{
		VistaQuery vm = generateUpdateQuery(t);
		return translateUpdate(t, executeRPC(vm));
	}

	public VistaQuery generateUpdateQuery(T t) throws MethodException
	{
		throw new DaoMethodNotImplementedException();
	}

	public T translateUpdate(T t, String returnValue) throws MethodException, UpdateException
	{
		throw new DaoMethodNotImplementedException();
	}


	//
	// Delete by Example and delete by id and supporting methods
	//
	public void delete(T t) throws MethodException, ConnectionException 
	{
		VistaQuery vm = generateDeleteQuery(t);
		translateDelete(executeRPC(vm));
	}

	public void delete(int id) throws MethodException, ConnectionException 
	{
		VistaQuery vm = generateDeleteQuery(id);
		translateDelete(executeRPC(vm));
	}

	public VistaQuery generateDeleteQuery(T t) throws MethodException
	{
		throw new DaoMethodNotImplementedException();
	}

	public VistaQuery generateDeleteQuery(int id) throws MethodException
	{
		throw new DaoMethodNotImplementedException();
	}

	public void translateDelete(String returnValue) throws DeletionException, MethodException
	{
		throw new DaoMethodNotImplementedException();
	}

	
	// Miscellaneous utility methods
	protected String translateNewEntityIEN(String returnValue, Boolean doCreate) throws CreationException
	{
		String[] results = StringUtils.Split(returnValue, DB_OUTPUT_SEPARATOR1);
		String ien = "-1";
		
		if (results[0].equals("0")) // Successful operation
		{
			ien = results[2];
		} 
		else if (doCreate)
			throw new CreationException("Could not create record: " + results[1]);
		
		return ien;
	}
	
	// ------------------------ private translate utility ---------------------------------------
	protected HashMap <String, String> stringToHashMap(String inStr)
	{
		HashMap <String, String> hm = new HashMap <String, String>();
		if (inStr.length()>MAX_M_STRING_LENGTH) {
			int begI, endI=0;
			for(int index=0; endI<inStr.length(); ++index)
			{
				begI=index*MAX_M_STRING_LENGTH;
				endI=(index+1)*MAX_M_STRING_LENGTH;
				if (endI >= inStr.length())
					endI = inStr.length();
				hm.put(String.valueOf(index+1), inStr.substring(begI, endI));
			}
		} else
			hm.put("1", inStr);
		return hm;
	}


	protected static void stringToHashMap2(HashMap <String, String> hm, String prefix, String inStr)
	{
		if (inStr.length()>MAX_M_STRING_LENGTH) {
			int begI, endI=0;
			for(int index=0; endI<inStr.length(); ++index)
			{
				begI=index*MAX_M_STRING_LENGTH;
				endI=(index+1)*MAX_M_STRING_LENGTH;
				if (endI >= inStr.length())
					endI = inStr.length();
				String num = String.valueOf(index+1);
				for (int i=num.length(); i<3; i++)
				  num ="0" + num;
				hm.put(prefix + num, inStr.substring(begI, endI));
			}
		} else
			hm.put(prefix + "001", inStr);
	}

	private void logVistaQuery(VistaQuery vm){
		logger.info("RPC Request: "+StringUtils.displayEncodedChars(vm.toString()));
	}
	
	private void logVistaQueryResults(VistaQuery vm, String results){
		logger.info(vm.getRpcName()+" RPC Results:"+StringUtils.displayEncodedChars(results));
	}

}
