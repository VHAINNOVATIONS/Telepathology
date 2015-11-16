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

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.dicom.CMoveResults;
import gov.va.med.imaging.exchange.business.dicom.DicomRequestParameters;
import gov.va.med.imaging.exchange.business.dicom.InstanceStorageInfo;
import gov.va.med.imaging.exchange.business.storage.exceptions.RetrievalException;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

public class CMoveResultsDAO extends EntityDAO<CMoveResults> {

    private String RPC_STUDY_UID_QUERY = "MAG STUDY UID QUERY";
	
	// Constructor
	public CMoveResultsDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO#generateGetEntityByCriteriaQuery(java.lang.Object)
	 */
	@Override
	public VistaQuery generateGetEntityByCriteriaQuery(Object criteria)
			throws MethodException {

		DicomRequestParameters request = (DicomRequestParameters)criteria;
	    String studyUID = null;
    	// Get the Study Instance UID.
    	if (request.containsKey ("0020,000D")){
    		studyUID = request.get("0020,000D"); // Study UID
    		if (studyUID == null || studyUID.equals("")){
    			throw new MethodException("No Study Instance UID");
    		}
    	}
		VistaQuery vm = new VistaQuery(RPC_STUDY_UID_QUERY);
		vm.addParameter(VistaQuery.LITERAL, studyUID);				
		vm.addParameter(VistaQuery.LITERAL, "1");	// PRMUID switch (0,1,2 or 3) -- applies for Legacy DB only:
										// 	0 returns (identical) original UID for all split multiframe objects -- wrong for C-MOVE
										//  1 returns the desired info the real UID the split MF study objects are filed with
										//	2 or 3 ???
		return vm;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO#translateGetEntityByCriteria(java.lang.Object, java.lang.String)
	 */
	@Override
	public CMoveResults translateGetEntityByCriteria(Object criteria,
			String returnValue) throws MethodException, RetrievalException {
		
		CMoveResults results = new CMoveResults();
    	if(returnValue == null){
    		throw new RetrievalException("No data returned from RPC Call.");
    	}
		String status = StringUtils.MagPiece(returnValue, StringUtils.CRLF, 1);
    	Integer code = Integer.parseInt(StringUtils.MagPiece(status, StringUtils.COMMA, 1));
    	if (code < 0){
        	//Check for duplicate Study Instance UID result code.
    		if(code == -13){
    			results.setDuplicateStudyInstanceUID(true);
    			return results;
    		}
    		else{
        		throw new RetrievalException ("Result Line: "+code+", "
        				+StringUtils.MagPiece(status, StringUtils.COMMA, 2));    			
    		}
		}
    	String[] lines = StringUtils.Split(returnValue, StringUtils.CRLF);

    	//i=1 because the first string states "## images found".
        for (int i=1; i<lines.length; i++){
			InstanceStorageInfo info = new InstanceStorageInfo();
			
			//Grab info from first partition.
			String image = StringUtils.MagPiece(lines[i], "|", 1);
        	String ien = StringUtils.MagPiece (image, "^", 1);
        	info.setObjectIdentifier(ien);
        	String dbData = StringUtils.MagPiece(image, "=", 2);
        	info.setType(dbData);
        	
        	//Use database type to determine how to handle rest of the returned value.
        	if(dbData.equals("OLD")){
        		//OLD represent data from IMAGE (#2005) File.  Note, there is much more 
        		//	data for OLD than NEW.
        		
        		String[] nameValuePairs = StringUtils.Split(lines[i], StringUtils.STICK);
        		
        		for(int k=1; k<nameValuePairs.length; k++){
	        		String name = StringUtils.MagPiece(nameValuePairs[k], StringUtils.EQUALS, 1);
	        		String value = StringUtils.MagPiece(nameValuePairs[k], StringUtils.EQUALS, 2);
	        		
	        		if(name.equals("IMGLOC")){
		        		//Get Image Location.
		        		info.setObjectStorageIdentifier(value);
	        		}
	        		else if(name.equals("NWLOCIEN")){
		        		//Get Image Network IEN and Authorization
	        			String nwlIEN = StringUtils.MagPiece(value, StringUtils.CARET, 1);
	        			info.setNetworkIEN(nwlIEN);
		        		String imgnwUsername = StringUtils.MagPiece(value, StringUtils.CARET, 2);
		        		info.setNetworkIENUsername(imgnwUsername);
		        		String imgnwPassword = StringUtils.MagPiece(value, StringUtils.CARET, 3);
		        		info.setNetworkIENPassword(imgnwPassword);
	        		}
	        		else if(name.equals("TXTLOC")){
		        		//Get the Text Location.
		        		info.setObjectSupportedTextStorageIdentifier(value);
	        		}		
	        		else if(name.equals("SOPCLASS")){
	        			//Get the SOP Class
	        			info.setSopClassUID(value);
	        		}
        		}
        	}
        	else if(dbData.equals("NEW")){
        		//NEW represents data from new Patch 34 data structure.
        		String[] nameValuePairs = StringUtils.Split(lines[i], StringUtils.STICK);
        		
        		for(int k=1; k<nameValuePairs.length; k++){
	        		String name = StringUtils.MagPiece(nameValuePairs[k], StringUtils.EQUALS, 1);
	        		String value = StringUtils.MagPiece(nameValuePairs[k], StringUtils.EQUALS, 2);
	        		if(name.equals("ARTKEY")){
		        		//Get Image Location.
	            		info.setArtifactKey(value);
	        		}
	        		else if(name.equals("SOPCLASS")){
	        			//Get the SOP Class
	        			info.setSopClassUID(value);
	        		}
        		}
        	}
        	else if (i > 1) {
        		// not first line, OLD or NEW --> wrong data format returned
        		throw new RetrievalException("Wrong item data returned from RPC Call: " + lines[i]);
        	}
    		results.add(info);
        }
        return results;
	}
}
