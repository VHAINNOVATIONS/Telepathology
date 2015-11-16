/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 11, 2009
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswlouthj
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
package gov.va.med.imaging.exchange.business.dicom;

import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.StringUtil;
import gov.va.med.imaging.exchange.business.dicom.exceptions.DicomException;
import gov.va.med.imaging.facade.configuration.AbstractBaseFacadeConfiguration;
import gov.va.med.imaging.facade.configuration.EncryptedConfigurationPropertyString;
import gov.va.med.imaging.facade.configuration.FacadeConfigurationFactory;
import gov.va.med.imaging.facade.configuration.HiddenBooleanConfigurationField;
import gov.va.med.imaging.facade.configuration.HiddenConfigurationField;
import gov.va.med.imaging.facade.configuration.HiddenStringConfigurationField;
import gov.va.med.imaging.facade.configuration.exceptions.CannotLoadConfigurationException;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

public class DicomServerConfiguration 
extends AbstractBaseFacadeConfiguration 
{
	final static Logger logger = Logger.getLogger(DicomServerConfiguration.class);

	// These could be made new parameters for P116 -- see main method; Note: new parameters require VixInstaller (GUI) change too
	private final static String theVAImplementationClassUID = "1.2.840.113754.2.1.3.0"; // This value is assigned by ISO organizations
	private final static String theVAImplementationVersionName = "VA_DICOM V3.0"; // This value assigned by VistA Imaging
	private final static int theDicomListenerPort = 60090; // port for non Store SCP listener (like Q/R SCP)
	private final static int thePDUTimeout = 300;
	private final static int theMoveQueueCapacity = 16;
	private final static String theQueryLimit = "500";
	
	@HiddenConfigurationField
	private List<InstrumentConfig> instruments = new ArrayList<InstrumentConfig>();

	@HiddenConfigurationField
	private List<ModalityConfig> modalities = new ArrayList<ModalityConfig>();

	@HiddenConfigurationField
	private DGWEmailInfo dgwEmailInfo = new DGWEmailInfo("", "", "", "", null,"");

	@HiddenConfigurationField
	private List<UIDActionConfig> uidActions = new ArrayList<UIDActionConfig>();

	@HiddenConfigurationField
	private HashMap<String, InstrumentConfig> instrumentByPort = new HashMap<String, InstrumentConfig>();

	@HiddenConfigurationField
	private HashMap<String, UIDActionConfig> oldSOPClasses = new HashMap<String, UIDActionConfig>();
	
	@HiddenConfigurationField
	private HashMap<String, UIDActionConfig> newSOPClasses = new HashMap<String, UIDActionConfig>();

	@HiddenConfigurationField
	private HashMap<String, UIDActionConfig> unknownSOPClasses = new HashMap<String, UIDActionConfig>();
	
	//Note: Eventually, this object is passed to XMLEncoder to create and save a XML encoded configuration file
	//	that represents this object.  Found issue with XMLEncoder.  The XMLEncoder does not pass boolean properties
	//	assigned to a default value before the config file creation.  If you want to make sure your new property
	//	appears in the config file, test it with the main().
    private String siteId;
    private EncryptedConfigurationPropertyString accessCode;
    private EncryptedConfigurationPropertyString verifyCode;
	private boolean dicomEnabled = false;
	private boolean archiveEnabled = false;
	private boolean iconProcessingEnabled = false;
    private String dicomCorrectFolder;	// {"vixcache"}  or "c:/temp/" + "DCorrect";
    private String dicomDebugFolder;	// {"vixcache"}  or "c:/temp/" + "DDebug";
    private String cannedIconFolder;	// {"vixconfig"}/Images ;
	private boolean dicomDebugDumpEnabled = true; // make sure its auto off in x minutes if on!!
	private Integer dicomDebugDumpMinutes; // number of minutes after which debug enabled must be auto shutoff
	private Long dicomDebugDumpStartMillies; // Millies stamp when system started
	
	@HiddenConfigurationField
    private volatile String hostName;
	
	@HiddenStringConfigurationField
    private String fakeHostName;
    private int moveQueueCapacity = theMoveQueueCapacity;
    private ArrayList<String> removedElements;
    private ArrayList<String> addedQueryElements;
    private String queryLimit = theQueryLimit;
    private String legacyGatewayAddress;
    private int legacyGatewayPort;
	private HashMap<String, VistaCredentials> aeTitleToVistaCredentialsMap;

	@HiddenConfigurationField
	private boolean dicomStarted = false;
	
	@HiddenBooleanConfigurationField
	private boolean ignoreSopSwitch = false; // for testing purposes only: if true sends all SOPs to new DB! (default False)
	
	@HiddenBooleanConfigurationField
	private boolean loadFalseStats = false;
	private boolean formatPatientIDwithDashes = true;
	private boolean moveSubOperationsEnabled = true;
	private String implementationClassUID;
	private String implementationVersionName;
	private boolean authenticateAETitles = true;
	private String applicationName;
	private int pduTimeout;
	private int dicomListenerPort;

	private ImporterPurgeDelays importerPurgeDelays = new ImporterPurgeDelays();
	
	private Object readResolve() 
	{
		instruments = new ArrayList<InstrumentConfig>();
		modalities = new ArrayList<ModalityConfig>();
		dgwEmailInfo = new DGWEmailInfo("", "", "", "", null,"");
		uidActions = new ArrayList<UIDActionConfig>();
		instrumentByPort = new HashMap<String, InstrumentConfig>();
		oldSOPClasses = new HashMap<String, UIDActionConfig>();
		newSOPClasses = new HashMap<String, UIDActionConfig>();
		unknownSOPClasses = new HashMap<String, UIDActionConfig>();
		
		if (importerPurgeDelays==null)
		{
			importerPurgeDelays = new ImporterPurgeDelays();
		}
		
	    return this;
	}

	@Override
	public AbstractBaseFacadeConfiguration loadDefaultConfiguration()
	{
		this.dicomEnabled = false;
		this.archiveEnabled = false;
		this.iconProcessingEnabled = false;
		this.dicomDebugDumpEnabled = false;
		this.dicomDebugDumpMinutes = 30;
		this.dicomDebugDumpStartMillies = System.currentTimeMillis(); // set in serverLifecycleEvent of  DicomLifecycleListener
	    this.fakeHostName = null;
	    this.removedElements = null;
	    this.addedQueryElements = null;
	    this.dicomStarted = false;
		this.ignoreSopSwitch = false;
		this.loadFalseStats = false;
		this.formatPatientIDwithDashes = false;
		this.moveSubOperationsEnabled = false;
		this.implementationClassUID = theVAImplementationClassUID;
		this.implementationVersionName = theVAImplementationVersionName;
		this.dicomListenerPort = theDicomListenerPort;
		this.pduTimeout = thePDUTimeout;
		this.applicationName = "HDIG";
		this.importerPurgeDelays = new ImporterPurgeDelays();

		return this;
	}
	
	public static synchronized DicomServerConfiguration getConfiguration()
	{
		try
		{
			return FacadeConfigurationFactory.getConfigurationFactory().getConfiguration(
					DicomServerConfiguration.class);
		}
		catch(CannotLoadConfigurationException clcX)
		{
			// no need to log, already logged
			return null;
		}
	}

    public static void main(String[] args) {
    	// Run this from ...\Tomcat 6.0\lib\ folder:
    	// java -cp ./*; CoreValueObjects-0.1.jar gov.va.med.imaging.exchange.business.dicom.DicomServerConfiguration
    	//               “<siteID>” "localhost" "60100" "<accessCode>" "<verifyCode>" “DLE=true” “AE=true” “IPE=true”
    	// input example: "660" "localhost" "60100" "boating1" "boating1." "DLE=true" "AE=false" "IPE=true"
        if (args.length != 8)
        {
            printUsage();
            return;
        }
        DicomServerConfiguration config = getConfiguration();
        config.setSiteId(args[0]);
        config.setLegacyGatewayAddress(args[1]);
        config.setLegacyGatewayPort(Integer.parseInt(args[2]));
        config.setAccessCode(new EncryptedConfigurationPropertyString(args[3]));
        config.setVerifyCode(new EncryptedConfigurationPropertyString(args[4]));
        String path= System.getenv("vixcache");			// <x:/vixcache>
        if (path.length() < 4)
        	path = "c:/temp/";
        if (!(path.endsWith("/") || path.endsWith("\\")))
        	path += "/";
        config.setDicomCorrectFolder(path + "DCorrect");
        config.setDicomDebugFolder(path + "DDebug");
        config.setDicomDebugDumpEnabled(false);
        checkAndMakeDirs(config.getDicomCorrectFolder());
        checkAndMakeDirs(config.getDicomDebugFolder());

        String cannedIconPath= System.getenv("vixconfig");	// <x:/vixconfig/>
        if (cannedIconPath.length() < 4)
        	path = "c:/vixconfig/";
        if (!(cannedIconPath.endsWith("/") || cannedIconPath.endsWith("\\")))
        	cannedIconPath += "/";
        cannedIconPath += "Images/";
        config.setCannedIconFolder(cannedIconPath);
        
		config.setDicomEnabled(!args[5].contains("DLE=false"));
		config.setArchiveEnabled(!args[6].contains("AE=false"));
		config.setIconProcessingEnabled(!args[7].contains("IPE=false"));
		// These could be made new parameters for P116, Note: new parameters require VixInstaller (GUI) change
		config.setImplementationClassUID(theVAImplementationClassUID); 
		config.setImplementationVersionName(theVAImplementationVersionName); 
		config.setDicomListenerPort(theDicomListenerPort);
		config.setFormatPatientIDwithDashes(false);
		config.setMoveSubOperationsEnabled(true);
		
        // Store the configuration
        config.storeConfiguration();
    }

    private static void checkAndMakeDirs(String folderPath)
    {
		File folder = new File(folderPath);
		if (!folder.exists())
		{
			folder.mkdirs();
		}
   	
    }
    private static void printUsage() {
        System.out.println("This program requires eight arguments:");
        System.out.println("  * The site ID");
        System.out.println("  * The legacy gateway address");
        System.out.println("  * The legacy gateway port");
        System.out.println("  * The access code for the gateway service account");
        System.out.println("  * The verify code for the gateway service account");
        System.out.println("  * DICOM Listener Enabled setting ('DLE=true')");
        System.out.println("  * Archival Enabled setting ('AE=true')");
        System.out.println("  * Icon Processing Enabled setting ('IPE=true')");

     }

	public boolean isDicomStarted()
	{
		return dicomStarted;
	}

	public void setDicomStarted(boolean dicomStarted)
	{
		this.dicomStarted = dicomStarted;
	}
	
	public String getSiteId()
	{
		return siteId;
	}

	public void setSiteId(String siteId)
	{
		this.siteId = siteId;
	}

	public EncryptedConfigurationPropertyString getAccessCode()
	{
		return accessCode;
	}

	public void setAccessCode(EncryptedConfigurationPropertyString accessCode)
	{
		this.accessCode = accessCode;
	}

	public EncryptedConfigurationPropertyString getVerifyCode()
	{
		return verifyCode;
	}

	public void setVerifyCode(EncryptedConfigurationPropertyString verifyCode)
	{
		this.verifyCode = verifyCode;
	}
	

	public boolean isDicomEnabled() {
		return this.dicomEnabled;
	}
	public void setDicomEnabled(boolean dicomEnabled)
	{
		this.dicomEnabled = dicomEnabled;
	}
	
	/**
	 * Get Fake hostname value from application.properties file.
	 *
	 * @return represents a hostname to a different machine that is known by the VistA HIS.  This 
	 * is created for a testing environment only.
	 */
    public String getFakeHostName(){
        return this.fakeHostName;
    }
	public void setFakeHostName(String fakeHostName)
	{
		this.fakeHostName = fakeHostName;
	}
	
	public String getHostName()
	{
		if (hostName == null)
		{
			if (getFakeHostName() != null && !getFakeHostName().equals(""))
			{
				hostName = getFakeHostName();
			}
			else
			{
				try
				{
					InetAddress localMachine = InetAddress.getLocalHost();	
					hostName = localMachine.getHostName();
				}
				catch(java.net.UnknownHostException uhe)
				{
					logger.error("Couldn't retrieve host name: ", uhe);
				}
			}
		}
		
		return (hostName + "").trim();
	}
    
	/**
	 * Get the MoveQueueCapacity value from application.properties file.  The C-Move process
	 * uses a Producer/Consumer pattern.  There is a Queue between the producer and consumer.
	 * Changing this value changes the size of the Queue.  This optional value can affect the 
	 * performance the application.
	 *
	 * @return represents a configured value for the MoveQueueCapacity.  If this value is not set, 
	 * there is a default value in the code.
	 * 
	 */
	public int getMoveQueueCapacity() {
		return this.moveQueueCapacity;
	}
	public void setMoveQueueCapacity(int moveQueueCapacity)
	{
		this.moveQueueCapacity = moveQueueCapacity;
	}

	/**
	 * @return the removeElements
	 */
	public ArrayList<String> getRemovedElements() {
		return this.removedElements;
	}	
	public void setRemovedElements(ArrayList<String> removeElements)
	{
		this.removedElements = removeElements;
	}

	/**
	 * @return the added query elements
	 */
	public ArrayList<String> getAddedQueryElements(){
		return this.addedQueryElements;
	}
	public void setAddedQueryElements(ArrayList<String> addQueryElements)
	{
		this.addedQueryElements = addQueryElements;
	}
	
	public DicomServerConfiguration()
	{
		aeTitleToVistaCredentialsMap = new HashMap<String, VistaCredentials>();
	}

	public String getLegacyGatewayAddress()
	{
		return legacyGatewayAddress;
	}

	public void setLegacyGatewayAddress(String legacyGatewayAddress)
	{
		this.legacyGatewayAddress = legacyGatewayAddress;
	}

	public int getLegacyGatewayPort()
	{
		return legacyGatewayPort;
	}

	public void setLegacyGatewayPort(int legacyGatewayPort)
	{
		this.legacyGatewayPort = legacyGatewayPort;
	}

	public List<InstrumentConfig> getInstruments()
	{
		return instruments;
	}

	public void setInstruments(List<InstrumentConfig> instruments)
	{
		this.instruments = instruments;
		
		// Clear any previous data, then build our lookup of instrument nicknames by port here
		instrumentByPort.clear();
		
		for (InstrumentConfig instrument : instruments)
		{
			instrumentByPort.put(String.valueOf(instrument.getPort()), instrument);
		}
	}
	
	public InstrumentConfig getInstrumentByPort(int port)
	{
		return instrumentByPort.get(String.valueOf(port));
	}

	public List<ModalityConfig> getModalities()
	{
		return modalities;
	}

	public void setModalities(List<ModalityConfig> modalities)
	{
		this.modalities = modalities;
	}
	public DGWEmailInfo getDgwEmailInfo()
	{
		return dgwEmailInfo;
	}

	public void setDgwEmailInfo(DGWEmailInfo dgwEI)
	{
		this.dgwEmailInfo = dgwEI;
	}
	
	public boolean isCurrentStorageSOPClass(String sopClassUid) throws DicomException
	{
		return oldSOPClasses.containsKey(sopClassUid);
	}

	public boolean isNewStorageSOPClass(String sopClassUid) throws DicomException
	{
		return newSOPClasses.containsKey(sopClassUid);
	}

	public boolean isUnknownStorageSOPClass(String sopClassUid) throws DicomException
	{
		return unknownSOPClasses.containsKey(sopClassUid);
	}

	public void setUidActions(List<UIDActionConfig> uidActions)
	{
		this.uidActions = uidActions;
		
		// Clear any previous data, then build our lookup of old and new SOP classes here
		oldSOPClasses.clear();
		newSOPClasses.clear();
		unknownSOPClasses.clear();
		
		for (UIDActionConfig action : uidActions)
		{
			String actionUid = action.getUid();
			String actionCode = action.getActionCode();
			
			if ("1".equals(actionCode))
			{
				this.oldSOPClasses.put(actionUid, action);
			}
			else if ("2".equals(actionCode))
			{
				this.newSOPClasses.put(actionUid, action);
			}
			else // "3"
			{
				this.unknownSOPClasses.put(actionUid, action);
			}
			String aComment = action.getActionComment();
			if (aComment==null || (aComment.length()==0)) {
				action.setActionComment("Unknown");
				action.setIconFilename("magsensitive.JPG"); // neutral for all -- just in lack of real generic icon!
			} else {
				String[] acomfields = StringUtil.split(aComment, StringUtil.STICK);
				action.setActionComment(acomfields[0]);
				if (acomfields.length>1) {
					action.setIconFilename(acomfields[1]);
				} else
					action.setIconFilename("createIcon");
			}
		}
	}
	public UIDActionConfig getSopUIDActionConfiguration (String sopClassUID) {

		for (UIDActionConfig action : uidActions)
		{
			if (sopClassUID.equals(action.getUid())) {
				return action;
			}
		}
		return null;
	}

	public String getDicomCorrectFolder() {
		return dicomCorrectFolder;
	}

	public void setDicomCorrectFolder(String dicomCorrectFolder) {
		this.dicomCorrectFolder = dicomCorrectFolder;
	}

	public String getDicomDebugFolder() {
		return dicomDebugFolder;
	}

	public void setDicomDebugFolder(String dicomDebugFolder) {
		this.dicomDebugFolder = dicomDebugFolder;
	}

	public boolean isDicomDebugDumpEnabled() {
		return dicomDebugDumpEnabled;
	}

	public void setDicomDebugDumpEnabled(boolean dicomDebugDumpEnabled) {
		this.dicomDebugDumpEnabled = dicomDebugDumpEnabled;
	}

	public boolean isArchiveEnabled() {
		return archiveEnabled;
	}

	public void setArchiveEnabled(boolean archiveEnabled) {
		this.archiveEnabled = archiveEnabled;
	}

	public boolean isIconProcessingEnabled() {
		return iconProcessingEnabled;
	}

	public void setIconProcessingEnabled(boolean iconProcessingEnabled) {
		this.iconProcessingEnabled = iconProcessingEnabled;
	}

	public String getCannedIconFolder() {
		return cannedIconFolder;
	}

	public void setCannedIconFolder(String cannedIconFolder) {
		this.cannedIconFolder = cannedIconFolder;
	}

	public RoutingToken getRoutingToken() {
		RoutingToken routingToken = null;
		try 
		{
			routingToken = RoutingTokenImpl.createVARadiologySite(DicomServerConfiguration.getConfiguration().getSiteId());
		} 
		catch (RoutingTokenFormatException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return routingToken;
	}

	public Integer isDicomDebugDumpMinutes() {
		return dicomDebugDumpMinutes;
	}

	public void setDicomDebugDumpMinutes(Integer dicomDebugDumpMinutes) {
		this.dicomDebugDumpMinutes = dicomDebugDumpMinutes;
	}

	public Long getDicomDebugDumpStartMillies() {
		return dicomDebugDumpStartMillies;
	}

	public void setDicomDebugDumpStartMillies(Long dicomDebugDumpStartMillies) {
		this.dicomDebugDumpStartMillies = dicomDebugDumpStartMillies;
	}

	public Integer getDicomDebugDumpMinutes() {
		return dicomDebugDumpMinutes;
	}

	public boolean isIgnoreSopSwitch() {
		return ignoreSopSwitch;
	}
	public void setIgnoreSopSwitch(Boolean iSSwitch) {
		this.ignoreSopSwitch=iSSwitch;
	}
	
	/**
	 * @return the loadFalseStats
	 */
	public boolean isLoadFalseStats() {
		return loadFalseStats;
	}

	/**
	 * @param loadFalseStats the loadFalseStats to set
	 */
	public void setLoadFalseStats(boolean loadFalseStats) {
		this.loadFalseStats = loadFalseStats;
	}

	/**
	 * @return the formatSSNwithDashes
	 */
	public boolean isFormatPatientIDwithDashes() {
		return formatPatientIDwithDashes;
	}

	/**
	 * @param formatSSNwithDashes the formatSSNwithDashes to set
	 */
	public void setFormatPatientIDwithDashes(boolean formatPatientIDwithDashes) {
		this.formatPatientIDwithDashes = formatPatientIDwithDashes;
	}
	
	/**
	 * @return the implementationClassUID
	 */
	public String getImplementationClassUID() {
		return implementationClassUID;
	}

	/**
	 * @param implementationClassUID the implementationClassUID to set
	 */
	public void setImplementationClassUID(String implementationClassUID) {
		this.implementationClassUID = implementationClassUID;
	}

	/**
	 * @return the implementationVersionName
	 */
	public String getImplementationVersionName() {
		return implementationVersionName;
	}

	/**
	 * @param implementationVersionName the implementationVersionName to set
	 */
	public void setImplementationVersionName(String implementationVersionName) {
		this.implementationVersionName = implementationVersionName;
	}

	/**
	 * @return the pduTimeout
	 */
	public int getPduTimeout() {
		return pduTimeout;
	}

	/**
	 * @param pduTimeout the pduTimeout to set
	 */
	public void setPduTimeout(int pduTimeout) {
		this.pduTimeout = pduTimeout;
	}

	/**
	 * @return the applicationName
	 */
	public String getApplicationName() {
		return applicationName;
	}

	/**
	 * @param applicationName the applicationName to set
	 */
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	/**
	 * @return the authenticateAETitles
	 */
	public boolean isAuthenticateAETitles() {
		return authenticateAETitles;
	}

	/**
	 * @param authenticateAETitles the authenticateAETitles to set
	 */
	public void setAuthenticateAETitles(boolean authenticateAETitles) {
		this.authenticateAETitles = authenticateAETitles;
	}

	/**
	 * @return the dicomListenerPort
	 */
	public int getDicomListenerPort() {
		return dicomListenerPort;
	}

	/**
	 * @param dicomListenerPort the dicomListenerPort to set
	 */
	public void setDicomListenerPort(int dicomListenerPort) {
		this.dicomListenerPort = dicomListenerPort;
	}

	/**
	 * @return the queryLimit
	 */
	public String getQueryLimit() {
		return queryLimit;
	}

	/**
	 * @param queryLimit the queryLimit to set
	 */
	public void setQueryLimit(String queryLimit) {
		this.queryLimit = queryLimit;
	}

	/**
	 * @return the moveSubOperationsEnabled
	 */
	public boolean isMoveSubOperationsEnabled() {
		return moveSubOperationsEnabled;
	}

	/**
	 * @param moveSubOperationsEnabled the moveSubOperationsEnabled to set
	 */
	public void setMoveSubOperationsEnabled(boolean moveSubOperationsEnabled) {
		this.moveSubOperationsEnabled = moveSubOperationsEnabled;
	}

	/**
	 * 
	 * @return represents the identifier of the HDIG.  The identifier is based on the hostname only and contains
	 * a _hdig extension.  The hostname is filtered from the FQDN if necessary.  This is used when it is necessary 
	 * to avoid confusion with the Legacy DICOM Gateway that is running on the same machine.  
	 * An example would be vhaiswimgtest_hdig.
	 */
	public String getHDIGSpecificHostIdentifier(){
		String[] fqdn = StringUtil.split(this.hostName, StringUtil.PERIOD);
		String hdigHost = fqdn[0];
		if(hdigHost != null){
			hdigHost = hdigHost.trim();
			hdigHost = hdigHost.concat("_hdig");
		}
		return hdigHost;
	}


	public String getAccessCodeString()
	{
		String accessCodeString = "";
		if (getAccessCode() != null)
		{
			accessCodeString = getAccessCode().toString() + "";
		}
		
		return accessCodeString;
	}

	public String getVerifyCodeString()
	{
		String verifyCodeString = "";
		if (getVerifyCode() != null)
		{
			verifyCodeString = getVerifyCode().toString() + "";
		}
		
		return verifyCodeString;
	}

	public void setImporterPurgeDelays(ImporterPurgeDelays importerPurgeDelays) {
		this.importerPurgeDelays = importerPurgeDelays;
	}

	public ImporterPurgeDelays getImporterPurgeDelays() {
		return importerPurgeDelays;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	
}
