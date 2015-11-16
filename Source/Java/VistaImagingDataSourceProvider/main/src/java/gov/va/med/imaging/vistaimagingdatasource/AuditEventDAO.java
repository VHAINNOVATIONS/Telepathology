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

package gov.va.med.imaging.vistaimagingdatasource;

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.AuditEvent;
import gov.va.med.imaging.exchange.business.storage.exceptions.CreationException;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.util.HashMap;
import java.util.Set;

public class AuditEventDAO extends EntityDAO<AuditEvent> {

	private String RPC_LOG_AUDIT_EVENT = "MAG EVENT AUDIT";
	
	/**
	 * Constructor
	 * 
	 * @param sessionFactory
	 */
	public AuditEventDAO(VistaSessionFactory sessionFactory) {
		this.setSessionFactory(sessionFactory);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO#generateCreateQuery(gov.va.med.imaging.exchange.business.PersistentEntity)
	 */
	@Override
	public VistaQuery generateCreateQuery(AuditEvent auditEvent) throws MethodException {
		
		VistaQuery vm = new VistaQuery(RPC_LOG_AUDIT_EVENT);
		vm.addParameter(VistaQuery.LITERAL, auditEvent.getEvent());
		vm.addParameter(VistaQuery.LITERAL, auditEvent.getHostname());
		vm.addParameter(VistaQuery.LITERAL, auditEvent.getApplicationName());
		vm.addParameter(VistaQuery.LIST, this.buildRPCArrayParameter(auditEvent.getEventElements()));
		vm.addParameter(VistaQuery.LITERAL, auditEvent.getMessage());
		return vm;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO#translateCreate(gov.va.med.imaging.exchange.business.PersistentEntity, java.lang.String)
	 */
	@Override
	public AuditEvent translateCreate(AuditEvent auditEvent, String returnValue)
			throws MethodException, CreationException {

		// Check that the first line starts with zero
		Integer resultCode = Integer.parseInt(StringUtils.MagPiece(returnValue, StringUtils.COMMA, 1));

		if (resultCode < 0) {
			auditEvent.setSuccessful(false);
            logger.error(this.getClass().getName()+": VistA M error posting to Audit Log: "
            		+"\nmessage= "+auditEvent.getMessage()+", resulted in the error= "+returnValue);
		}
		else{
			auditEvent.setSuccessful(true);
		}
		return auditEvent;
	}
	
	private HashMap<String, String> buildRPCArrayParameter(HashMap<String, String> map){
		
		HashMap<String, String> rpcMap = new HashMap<String, String>();
		Set<String> keylist = map.keySet();
		Object keys[] = keylist.toArray();
		for (int i = 0; i < keylist.size(); i++)
		{
			String kk = new String();
			String vv = new String();
			kk = String.valueOf(i + 1);
			String mapName = (String)keys[i];
			String mapValue = (String)map.get(mapName);
			vv = mapName+StringUtils.BACKTICK+mapValue;
			rpcMap.put(kk, vv);
		}
		return rpcMap;
	}
}
