//=========================================================================
// Copyright (C) 2000-2008, Laurel Bridge Software, Inc.
// 160 East Main St.
// Newark, Delaware 19711 USA
// All Rights Reserved
//=========================================================================
//
// WARNING: This file is generated from CINFO.java. Do not hand edit.
//
// This is the source file for the Component Info (CInfo) class generated
// for the Listen component.
//
package gov.va.med.imaging.dicom.dcftoolkit.common.mediainterchange;

import com.lbs.DCF.DCFException;

/**
*	Component Info class for Listen.
*/
public class CINFO
	extends com.lbs.DCF.ComponentInfo
{
	private static CINFO	instance_ = null;

	/**
	* static initializer
	* Constructs the singleton instance, and attempts to initialize it.
	* If AppControl comes up after this, it will ask for a reinitialization.
	*/
	static
	{
		try
		{
			instance_ = new CINFO();
		}
		catch ( DCFException e )
		{
			instance_ = null;
		}
	}

	/**
	* Method to ensure that the CINFO class is loaded and initialized.
	* You only need to call this if you are not calling another static
	* or instance method.
	*/
	public static void setup()
	{
	}

	/**
	*	Default constructor.
	*/
	private CINFO()
		throws DCFException
	{
		super( "DCS", "java_lib", "/apps/defaults/Listen" ); // if AppControl not initialized ignores filepath (last param), and goes for cfg/components/java_lib/DCS file
	}


	/**
	* Return the singleton
	*/
	public static CINFO instance()
	{
		return instance_;
	}

	/**
	*	Test debug flags for Listen.
	*	Calling this method is equivalent to calling:
	*	 CINFO.instance().testComponentDebugFlags().
	*	
	*	@param mask the bit mask for flags.
	*	@return true if any flags in the bit mask are currently set.
	*/
	public static boolean testDebugFlags(int mask )
	{
		return instance_.testComponentDebugFlags(mask);
	}

	/**
	*	Return the configuration group for this component. If AppControl
	*	has been initialized, returns the group "java_app/Listen"
	*	under the process configuration group. Otherwise, return default	
	*	settings from the group /components/java_app/Listen.
	*	If default settings are not available under the cfg/components group,
	*	getDefaultConfig is called.
	*	Calling this method is equivalent to calling:
	*	CINFO.instance().getComponentConfig().
	*	@return the CFGGroup for this component
	*	@throws CDSException if the group can not be found.
	*/
	public static com.lbs.CDS.CFGGroup getConfig()
		throws com.lbs.CDS.CDSException
	{
		return instance_.getComponentConfig();
	}

	/**
	 *	Return the burned-in configuration data for this component. Note that this
	 *	may not be the same data returned by getConfig or getComponentConfig.
	 *	@return CFGGroup for this component created from compiled-in data.
	 */
	public com.lbs.CDS.CFGGroup getDefaultConfig()
		throws com.lbs.CDS.CDSException, com.lbs.CDS.NotFoundException
	{
		String cfg_data = cfg_data_.replace( "$DCF_VAR{DICOMPORT}", "2000" );
		cfg_data = cfg_data.replace( "$DCF_FUNC{fix_path,$DCF_VAR{DCF_LOG}}", com.lbs.DCF.Framework.DCF_LOG().replace( "\\", "/" ) );
		cfg_data = cfg_data.replace( "$DCF_FUNC{fix_path,$DCF_VAR{DCF_TMP}}", com.lbs.DCF.Framework.DCF_TMP().replace( "\\", "/" ) );
		cfg_data = cfg_data.replace( "$DCF_VAR{DCF_ROOT}", com.lbs.DCF.Framework.DCF_ROOT().replace( "\\", "/" ) );
		cfg_data = cfg_data.replace( "$DCF_VAR{DCF_CFG}", com.lbs.DCF.Framework.DCF_CFG().replace( "\\", "/" ) );
		cfg_data = cfg_data.replace( "$DCF_VAR{DCF_LIB}", com.lbs.DCF.Framework.DCF_LIB().replace( "\\", "/" ) );
		cfg_data = cfg_data.replace( "$DCF_VAR{DCF_LOG}", com.lbs.DCF.Framework.DCF_LOG().replace( "\\", "/" ) );
		cfg_data = cfg_data.replace( "$DCF_VAR{DCF_TMP}", com.lbs.DCF.Framework.DCF_TMP().replace( "\\", "/" ) );
		cfg_data = cfg_data.replace( "$DCF_VAR{DCF_USER_ROOT}", com.lbs.DCF.Framework.DCF_USER_ROOT().replace( "\\", "/" ) );

		com.lbs.CDS.CFGGroup g = com.lbs.CDS_a.CFGDB_a.loadGroupFromString( cfg_data );
		com.lbs.CDS.CFGGroup gg = g.getGroup("Listen");
		return gg;
	}

	//
	// Component debug flags for Listen.
	//
public static final int df_SHOW_CONSTRUCTORS=0x0001;
public static final int df_SHOW_DESTRUCTORS=0x0002;
public static final int df_SHOW_GENERAL_FLOW=0x0004;
public static final int df_SIMULATE_HARDWARE=0x0008;
public static final int df_SHOW_CFG_INFO=0x0010;
public static final int df_SHOW_EXC_THROW=0x0020;


	private static final String cfg_data_ = "\n"
+"#==============================================================================\n"
+"# per-instance information for the Listen component\n"
+"#==============================================================================\n"
+"[ Listen ]\n"
+"debug_flags = 0x00014\n"
+"image_directory = c:/tmp/scp_images\n"
+"#\n"
+"# if true, storeObject will create a new unique identifier to use as the\n"
+"# filename. Currently, this will not change the sop instance uid in the\n"
+"# data set. This must be false for the automated store test to work.\n"
+"make_new_uids = NO\n"
+"\n"
+"### This is still in question.  Does 3.3.22c need this any longer?\n"
+"# if true, demostrate the DataSetByteReader class for making\n"
+"# a a decoded, and re-encoded network C-Store-Request look like\n"
+"# a ReadableByteChannel or InputStream object.\n"
+"#\n"
+"use_byte_reader = YES\n"
+"\n"
+"#\n"
+"# un-comment one of the following transfer syntax uids.\n"
+"# this will determine the format of files written to disk\n"
+"# by the example implementation adapter DicomDataService_a.\n"
+"#\n"
+"# implicit-little-endian\n"
+"#transfer_syntax_uid = 1.2.840.10008.1.2\n"
+"# explicit-little-endian\n"
+"transfer_syntax_uid = 1.2.840.10008.1.2.1\n"
+"# explicit-big-endian\n"
+"#transfer_syntax_uid = 1.2.840.10008.1.2.2\n"
+"\n"
+"###Custom VistA Imaging Configuration\n"
+"move_to_disk = NO\n"
+"#\n"
+"# If YES, both AETitles will be checked against Persistance for incoming DICOM\n"
+"#	Association Requests.  This will restrict acceptance to only those AETitles\n"
+"#	in Persistance.\n"
+"authenticate_aetitles = YES\n"
+"#\n"
+"# If true, the elements in the DICOM message or DataSet are checked against their VR Type.\n"
+"validate_QRSCP_incoming = True\n"
+"validate_QRSCP_outgoing = True\n"
+"validate_STORESCU_incoming = True\n"
+"validate_STORESCU_Outgoing = True\n"
+"#\n"
+"# Set the default configured value to represent VistA Imaging. \n"
+"implementation_class_uid = 1.2.840.113754.2.1.3.0\n"
+"#\n"
+"# Set the default configured value to represent VistA Imaging.\n"
+"implementation_version_name = VA_DICOM V3.0\n"
+"#\n"
+"# Set the default configured value to be used if a value is not recieved\n"
+"#	from Persistance. This occurs for C-Find-Rsp Dimse message.\n"
+"# ***This configuration variable is currently disabled.  Do not use.\n"
+"backup_move_scp_aetitle = DICOM_QR\n"
+"#\n"
+"# If True, SubOperations Status messages will be returned to C-Move SCU.\n"
+"#	This occurs for a C-Move-Rsp Dimse message.\n"
+"enableSubOperations = True\n"
+"#\n"
+"#Set value for PDU Timeout.  This occurs for C-Store-SCU Dimse message.\n"
+"pdutimeout = 780\n"
+"";


}
