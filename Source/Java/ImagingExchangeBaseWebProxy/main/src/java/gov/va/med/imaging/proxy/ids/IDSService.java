/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 29, 2008
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
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
package gov.va.med.imaging.proxy.ids;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a response from the IDS web app
 * 
 * @author VHAISWWERFEJ
 *
 */
public class IDSService 
implements Comparable<IDSService>
{	
	private String version;
	private String applicationType;
	private String applicationPath;
	private List<IDSOperation> operations = new ArrayList<IDSOperation>();
	
	public IDSService()
	{
		super();
		version = "";
		applicationType = applicationPath = "";
	}
	
	public IDSService(String applicationType, String version, 
			String applicationPath) {
		super();
		this.version = version;
		this.applicationType = applicationType;
		this.applicationPath = applicationPath;
	}
	
	public void addOperation(IDSOperation operation)
	{
		operations.add(operation);
	}

	public List<IDSOperation> getOperations() {
		return operations;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getApplicationPath() {
		return applicationPath;
	}

	public void setApplicationPath(String applicationPath) {
		this.applicationPath = applicationPath;
	}

	public String getApplicationType() {
		return applicationType;
	}

	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}
	
	public IDSOperation getOperationByType(String operationType)
	{
		for(IDSOperation operation : operations)
		{
			if(operationType.equals(operation.getOperationType()))
			{
				return operation;
			}
		}
		return null;
	}


	@Override
	public int compareTo(IDSService that) {
		int diff = this.applicationPath.compareTo(that.applicationPath);
		if(diff != 0)
			return diff;
		try
		{
			double thisV = Double.parseDouble(this.version);
			double thatV = Double.parseDouble(that.version);
			if(thisV < thatV)
				return -1;
			if(thisV > thatV)
				return 1;
			return 0;
		}
		catch(Exception ex) {}
		return this.version.compareTo(that.version);
	}

	@Override
	public String toString() 
	{
		StringBuilder sb = new StringBuilder();		
		sb.append("Application Type: " + applicationType + " [" + version + "]\n");
		sb.append("Application Path: " + applicationPath + "\n");
		for(IDSOperation operation : operations)
		{
			sb.append("\t" + operation + "\n");
		}
		return sb.toString();
	}

}
