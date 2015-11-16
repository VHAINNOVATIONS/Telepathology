/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: 
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswpeterb
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

package gov.va.med.imaging.vistaimagingdatasource.dicom.storage;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.dicom.CFindResults;
import gov.va.med.imaging.exchange.business.dicom.DicomRequestParameters;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Set;

import javax.sql.rowset.RowSetMetaDataImpl;

public class CFindResultsDAO extends EntityDAO<CFindResults> {

	private String RPC_MAG_CFIND_QUERY = "MAG CFIND QUERY";
	private static final String CRLF = "\r\n";
	private static final String CR = "\r";
	private static String MAXRETURN = "500";
	
	
	// Constructor
	public CFindResultsDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}

	public CFindResults getCFindResultsByCriteria(Object criteria)
			throws MethodException, ConnectionException {

		String result = "0";
		String offset = "0";
		int ready = 0;
		int ok;
		int count = 0;
		int colpos = 0;
		int metacol = 0;
		int empty;
		int errrtn = 0;
		String column = null;
		int rowcnt = 0;
		String line = null;
		HashMap<String, String> tagparam = new HashMap<String, String> ();
		HashMap<String, String> header = new HashMap<String, String> ();
		RowSetMetaDataImpl metadata = new RowSetMetaDataImpl ();
		
		DicomRequestParameters request = (DicomRequestParameters)criteria;

		CFindResults queryResult;
		try {
			queryResult = new CFindResults();
		} catch (SQLException sqlX) {
			throw new MethodException(sqlX.getMessage());
		}

		try{
		  logger.debug ("got rowset");
		  Set<String> keylist = request.keySet ();
		  Object keys[] = keylist.toArray ();
		  for (int i = 0; i < keylist.size (); i++){
		    String kk;
		    String vv;
		    int k;
		    kk = String.valueOf (i + 1);
		    byte dumptag[] = ((String)keys[i]).getBytes();
		    byte dumpval[] = ((String)request.get (keys[i])).getBytes();
		    vv = "";
		    // Filter out any control characters in the input tag
		    for (k = 0; k < dumptag.length; k++){
		    	if ((dumptag [k] > 31) && (dumptag [k] < 127))
		            vv = vv + String.valueOf ((char) dumptag [k]);
		    }
		    vv = vv + "|1|1|";
		    // Filter out any control characters in the input data
		    for (k = 0; k < dumpval.length; k++){
		      if ((dumpval [k] > 31) && (dumpval [k] < 127))
		          vv = vv + String.valueOf ((char) dumpval [k]);
		    }
		    logger.debug ("Add " + kk + " = " + vv);
		    tagparam.put (kk, vv);
		  }
		  logger.debug ("Starting a new Query");
		  VistaQuery vm = new VistaQuery(RPC_MAG_CFIND_QUERY);
		  vm.addParameter(VistaQuery.LIST, tagparam);
		  vm.addParameter(VistaQuery.LITERAL, result);
		  vm.addParameter(VistaQuery.LITERAL, offset);
		  vm.addParameter(VistaQuery.LITERAL, MAXRETURN);
		  String rtn = null;
		  try {
			  logger.debug ("Result from first Query");
			  rtn = this.executeRPC(vm);
		  }
		  catch(Exception ex) {
			  logger.error(ex.getMessage());
		        throw new MethodException(ex.getMessage());
		  }
		  if(rtn == null){
		        throw new MethodException("No data returned from RPC Call.");
		  }
		  if (Integer.parseInt (StringUtils.MagPiece(rtn, ",", 1)) == 0){
		      result = StringUtils.MagPiece (rtn, ",", 2);
		  }
		  if (Integer.parseInt (result) == 0){
		      throw new MethodException ("Query request failed..." + rtn);
		  }
		  ready = 0;
		  empty = 0;
		  errrtn = 0;
		  while (ready < 1){
			  logger.debug ("Waiting for TaskMan to complete task # " + result);
		      vm.clear();
		      vm.setRpcName(RPC_MAG_CFIND_QUERY);
		      vm.addParameter(VistaQuery.LITERAL, request);
		      vm.addParameter(VistaQuery.LITERAL, result);
		      vm.addParameter(VistaQuery.LITERAL, offset);
		      vm.addParameter(VistaQuery.LITERAL, MAXRETURN);
		      rtn = null;
		      try{
		          rtn = this.executeRPC(vm);
		      }
		      catch(Exception ex){
		          throw new MethodException(ex.getMessage());
		      }
		      if(rtn == null){
		          throw new MethodException("No data returned from RPC Call.");
		      }
		      count = Integer.parseInt (StringUtils.MagPiece(rtn, ",", 1));
		      if (count != -1){
		        ready = 1;
		        empty = 1;
		        errrtn = 0;
		        for (int i = 0; i < count; i++){
		          line = StringUtils.MagPiece(rtn, "\n", i + 2);
		          if (line.length () < 19)
		              empty = 0;
		          if ((line.length () > 18)
		                  && !(line.substring (0, 18).equals ("0000,0902^No match")))
		            empty = 0;
		          if ((line.length () > 9)
		                  && (line.substring (0, 9).equals ("0000,0902"))){
		          	  ok = 0;
		          	  if ((line.length () > 18)
		          	          && (line.substring (0, 19).equals ("0000,0902^Result # ")))
		          	  	ok = 1;
		          	  if (ok < 1)
		          	  	errrtn = 1;
		          	}
		        }
		        if ((empty < 1) & (errrtn > 0)){
		          throw new MethodException (rtn);
		        }
		      }
		      try{
		          Thread.sleep (500);
		        }
		        catch (InterruptedException e){
		        	logger.debug ("Awake!");
		        }
		    }
		    ready = 0;
		    if (empty > 0){
		    	logger.debug ("Empty result set, creating pro-forma header");
		        colpos = 0;
		        for (int i = 0; i < keylist.size (); i++){
		          String vv;
		          int k;
		          byte dumptag[] = ((String)keys[i]).getBytes();
		          vv = "";
		          // Filter out any control characters in the input tag
		          for (k = 0; k < dumptag.length; k++){
		          	if ((dumptag [k] > 31) && (dumptag [k] < 127))
		                  vv = vv + String.valueOf ((char) dumptag [k]);
		          }
		          colpos++;
		          column = String.valueOf (colpos);
		          metacol = colpos;
		          header.put (column, vv);
		          logger.debug ("Column " + colpos + " = " + vv);
		        }
		    }
		    else{
		        while (ready < 1){
		        	logger.debug ("Establishing Column Headers, offset = " + offset);
		          count = Integer.parseInt (StringUtils.MagPiece(rtn, ",", 1));
		          if (count == 0){
		            ready = 1;
		          }
		          else{
		            offset = StringUtils.MagPiece(rtn, ",", 2);
		            for (int i = 0; i < count; i++){
		              line = StringUtils.MagPiece(rtn, "\n", i + 2);
		              if ((line.length () > 18)
		                      && (line.substring (0, 19).equals ("0000,0902^Result # "))){
		                rowcnt++; // new record
		                colpos = 0;
		                if (rowcnt > 1){
		                  ready = 1;
		                }
		              }
		              else{
		                if (rowcnt == 1){
		                  colpos++;
		                  column = String.valueOf (colpos);
		                  metacol = colpos;
		                  header.put (column, StringUtils.MagPiece(line, "^", 1));
		                  logger.debug ("Column " + colpos + " = " + StringUtils.MagPiece(line, "^", 1));
		                }
		              }
		            }
		            vm.clear();
		            vm.setRpcName(RPC_MAG_CFIND_QUERY);
		            vm.addParameter(VistaQuery.LITERAL, request);
		            vm.addParameter(VistaQuery.LITERAL, result);
		            vm.addParameter(VistaQuery.LITERAL, offset);
		            vm.addParameter(VistaQuery.LITERAL, MAXRETURN);
		            rtn = null;
		            try{
		                rtn = this.executeRPC(vm);
		            }
		            catch(Exception ex){
		                throw new MethodException(ex.getMessage());
		            }
		            if(rtn == null){
		                throw new MethodException("No data returned from RPC Call.");
		            }
		          }
		       }
		    }
		    try{
		    	logger.debug ("There are " + metacol + " columns.");
		        metadata.setColumnCount (metacol);
		        for (int i = 1; i <= metacol; i++){
		            rtn = header.get (String.valueOf (i)).toString ();
		            metadata.setColumnName (i, rtn);
		            metadata.setColumnType (i, Types.VARCHAR);
		            logger.debug ("Result-Column " + i + " = " + rtn);
		        }
		        queryResult.setMetaData (metadata);
		        queryResult.setType (ResultSet.TYPE_SCROLL_INSENSITIVE);
		        queryResult.setConcurrency (ResultSet.CONCUR_UPDATABLE);
		        queryResult.setFetchDirection (ResultSet.FETCH_UNKNOWN);
		    }
		    catch (SQLException e){
		    	logger.error("Trace:", e);
		    }
		    if (empty < 1){
		        offset = "0";
		        ready = 0;
		        colpos = 0;
		        rowcnt = 0;
		        try{
		          queryResult.moveToInsertRow ();
		          while (ready < 1){
		        	  logger.debug ("Fetching results from Vista, offset = " + offset);
		            vm.clear();
		            vm.setRpcName(RPC_MAG_CFIND_QUERY);
		            vm.addParameter(VistaQuery.LITERAL, request);
		            vm.addParameter(VistaQuery.LITERAL, result);
		            vm.addParameter(VistaQuery.LITERAL, offset);
		            vm.addParameter(VistaQuery.LITERAL, MAXRETURN);
		            rtn = null;
		            try{
		                rtn = this.executeRPC(vm);
		            }
		            catch(Exception ex){
		                throw new MethodException(ex.getMessage());
		            }
		            if(rtn == null){
		                throw new MethodException("No data returned from RPC Call.");
		            }
		            count = Integer.parseInt (StringUtils.MagPiece(rtn, ",", 1));
		            if (count == 0){
		              ready = 1;
		            }
		            else{
		              offset = StringUtils.MagPiece(rtn, ",", 2);
		              for (int i = 0; i < count; i++){
		                // deliver data to result-set
		                line = StringUtils.MagPiece(rtn, "\n", i + 2);
		                if ((line.length () > 18)
		                        && (line.substring (0, 19).equals ("0000,0902^Result # "))){
		                  if ((rowcnt > 0) && (colpos > 0)){
		                	  logger.debug ("Insert row " + rowcnt + " with " + colpos + " columns");
		                    queryResult.insertRow ();
		                    logger.debug ("now at row # " + queryResult.getRow ());
		                  }
		                  rowcnt++; // new record
		                  colpos = 0;
		                }
		                else{
		                  if (rowcnt > 0){
		                    colpos++; // add to current record
		                    String dataTag = StringUtils.MagPiece(line, "^", 1);
		                    String c = String.valueOf (colpos);
		                    String tagFromMap = header.get(c);
		
		                    String data = StringUtils.MagPiece(line, "^", 2);
		                    String cleanData;
		
		                    if(dataTag.equals(tagFromMap)){
		                    	if(data.endsWith(CR)){
		                    		cleanData = data.substring(0, (data.length()-1));
		                    	}
		                    	else if(data.endsWith(CRLF)){
		                    		cleanData = data.substring(0, (data.length()-2));
		                    	}
		                    	else{
		                    		cleanData = data;
		                    	}
		                    }
		                    else{
		                    	cleanData = "";
		                    }
		                    logger.debug ("Row " + rowcnt + ", column " + colpos + " = "
		                        + cleanData);
		                    queryResult.updateString (colpos, cleanData);
		                  }
		                }
		              }
		            }
		          }
		          if (colpos > 0){
		        	  logger.debug ("Insert row " + rowcnt + " with " + colpos + " columns");
		            queryResult.insertRow ();
		          }
		        }
		        catch (SQLException e){
		        	logger.error("Trace:", e);
		        }
		    }
		    logger.debug ("Notify VistA that query is complete");
		    vm.clear();
		    vm.setRpcName(RPC_MAG_CFIND_QUERY);
		    vm.addParameter(VistaQuery.LITERAL, request);
		    vm.addParameter(VistaQuery.LITERAL, result);
		    vm.addParameter(VistaQuery.LITERAL, "-1");
		    vm.addParameter(VistaQuery.LITERAL, MAXRETURN);
		    rtn = null;
		    try{
		        rtn = this.executeRPC(vm);
		    }
		    catch(Exception ex){
		        throw new MethodException(ex.getMessage());
		    }
		    if(rtn == null){
		        throw new MethodException("No data returned from RPC Call.");
		    }
		  queryResult.moveToCurrentRow();
		  return queryResult;
		}
		catch (SQLException e){
		  throw new MethodException ("Cannot create result-set.");
		}
	}	
}
