/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Dec 01, 2007
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWLOUTHJ
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
package gov.va.med.imaging.exchange.business.storage;

import gov.va.med.imaging.exchange.business.PersistentEntity;
import gov.va.med.imaging.exchange.business.storage.RetentionPolicyProviderMapping;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class RetentionPolicy implements PersistentEntity, Serializable
{
	private static final long serialVersionUID = 1L;

	//
	// Fields
	//
	private int id;
	private String archiveDurationYears;
	private String archiveDurationTrigger;
	private int minimumArchiveCopies;
	private int minimumOffsiteCopies;
	private String displayName;
	private String businessKey;
	private boolean isActive;
	private List<RetentionPolicyProviderMapping> retentionPolicyProviderMappings; 

	//
	// Default constructor
	//
	public RetentionPolicy()
	{
		retentionPolicyProviderMappings = new ArrayList<RetentionPolicyProviderMapping>();
	}
	
	//
	// Additional constructors
	//
	public RetentionPolicy(int id, String displayName, String archiveDurationYears,
			String archiveDurationTrigger, int minimumArchiveCopies,
			int minimumOffsiteCopies, String businessKey,
			boolean isActive) 
	{
		this();
		this.id = id;
		this.archiveDurationYears = archiveDurationYears;
		this.archiveDurationTrigger = archiveDurationTrigger;
		this.minimumArchiveCopies = minimumArchiveCopies;
		this.minimumOffsiteCopies = minimumOffsiteCopies;
		this.displayName = displayName;
		this.businessKey = businessKey;
		this.isActive = isActive;
	}
	
	
	public String getArchiveDurationTrigger() {
		return archiveDurationTrigger;
	}

	//
	// Properties
	//
    public int getId() {
		return id;
	}

    public void setId(int id) {
		this.id = id;
	}

	public String getArchiveDurationYears() {
		return archiveDurationYears;
	}

	public String getBusinessKey() {
		return businessKey;
	}

	public String getDisplayName() {
		return displayName;
	}

	public int getMinimumArchiveCopies() {
		return minimumArchiveCopies;
	}

	public int getMinimumOffsiteCopies() {
		return minimumOffsiteCopies;
	}

	public List<RetentionPolicyProviderMapping> getRetentionPolicyProviderMappings() {
		return retentionPolicyProviderMappings;
	}

	public List<RetentionPolicyProviderMapping> getRetentionPolicyProviderMappingsForAcquiringPlace(int acquiringPlaceId) 
	{
		List<RetentionPolicyProviderMapping> mappings = new ArrayList<RetentionPolicyProviderMapping>();
		
		for (RetentionPolicyProviderMapping mapping : retentionPolicyProviderMappings)
		{
			if (mapping.getPlaceId() == acquiringPlaceId)
			{
				mappings.add(mapping);
			}
		}
		
		return mappings;
		
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public void setArchiveDurationTrigger(String archiveDurationTrigger) {
		this.archiveDurationTrigger = archiveDurationTrigger;
	}

	public void setArchiveDurationYears(String archiveDurationYears) {
		this.archiveDurationYears = archiveDurationYears;
	}

	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setMinimumArchiveCopies(int minimumArchiveCopies) {
		this.minimumArchiveCopies = minimumArchiveCopies;
	}

	public void setMinimumOffsiteCopies(int minimumOffsiteCopies) {
		this.minimumOffsiteCopies = minimumOffsiteCopies;
	}

	public void setRetentionPolicyProviderMappings(
			List<RetentionPolicyProviderMapping> retentionPolicyProviderMappings) {
		this.retentionPolicyProviderMappings = retentionPolicyProviderMappings;
	}

	
}
