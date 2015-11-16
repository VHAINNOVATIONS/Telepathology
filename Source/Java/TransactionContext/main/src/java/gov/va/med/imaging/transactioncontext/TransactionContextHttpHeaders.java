package gov.va.med.imaging.transactioncontext;

/**
 * A list of the standard headers that are used by HTTP based protocols
 * for transaction related information.
 * 
 * @author VHAISWBECKEC
 *
 */
public interface TransactionContextHttpHeaders
{
	public final static String httpHeaderDuz = "xxx-duz";
	public final static String httpHeaderFullName = "xxx-fullname";
	public final static String httpHeaderSiteName = "xxx-sitename";
	public final static String httpHeaderSiteNumber = "xxx-sitenumber";
	public final static String httpHeaderSSN = "xxx-ssn";
	public final static String httpHeaderPurposeOfUse = "xxx-purpose-of-use";
	public final static String httpHeaderChecksum = "xxx-checksum";
	public final static String httpHeaderTransactionId = "xxx-transaction-id";
	// used for checksums of the data inside a zip stream for Federation
	public final static String httpHeaderImageChecksum = "xxx-image-checksum";
	public final static String httpHeaderTXTChecksum = "xxx-txt-checksum";
	public final static String httpHeaderImageQuality = "xxx-image-quality";
	public final static String httpHeaderVistaImageFormat = "xxx-image-format";
	public final static String httpHeaderImageSize = "xxx-image-length";
	public final static String httpHeaderTxtSize = "xxx-txt-length";
	public final static String httpHeaderContentTypeWithSubType = "xxx-contentTypeWithSubType";
	public final static String httpHeaderBrokerSecurityTokenId = "xxx-securityToken";
	public final static String httpHeaderCacheLocationId = "xxx-cacheLocationId";
	public final static String httpHeaderUserDivision = "xxx-userDivision";
	public final static String httpHeaderMachineName = "xxx-machineName";
	public final static String httpHeaderPatientId = "xxx-patient-id";
	public final static String httpHeaderClientVersion = "xxx-client-version";
	public final static String httpHeaderRequestingVixSiteNumber = "xxx-requestingVixSiteNumber";
	public final static String httpHeaderAllowAddFederationCompression = "xxx-AllowAddFederationCompression";
	public final static String httpHeaderOptionContext = "xxx-option-context";

	// request parameters that may be passed to explicitly set the router behavior
	public final static String httpRequestParameterProtocolOverride = "protocolOverride";
	public final static String httpRequestParameterTargetSite = "targetSite";
}
