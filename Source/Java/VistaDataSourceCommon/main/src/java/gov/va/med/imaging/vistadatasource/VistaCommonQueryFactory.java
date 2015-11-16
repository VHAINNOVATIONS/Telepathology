package gov.va.med.imaging.vistadatasource;

import gov.va.med.imaging.datasource.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.exchange.business.PassthroughInputMethod;
import gov.va.med.imaging.exchange.business.PassthroughParameter;
import gov.va.med.imaging.exchange.business.PassthroughParameterType;
import gov.va.med.imaging.protocol.vista.exceptions.MissingCredentialsException;
import gov.va.med.imaging.protocol.vista.exceptions.VistaConnectionException;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.EncryptionUtils;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class VistaCommonQueryFactory
{
	public final static String MENU_SUBSCRIPT = "200.03";
	public final static String DELEGATE_SUBSCRIPT = "200.19";

	// rpc calls made to other packages
	private final static String RPC_GET_TREATING_LIST = "VAFCTFU GET TREATING LIST";
	private final static String RPC_CONVERT_ICN_TO_DFN = "VAFCTFU CONVERT ICN TO DFN";
	private final static String RPC_SIGNON = "XUS SIGNON SETUP";
	private final static String RPC_AV_CODE = "XUS AV CODE";
	private final static String RPC_CREATE_CONTEXT = "XWB CREATE CONTEXT";
	private final static String RPC_GET_VARIABLE_VALUE = "XWB GET VARIABLE VALUE";
	private final static String RPC_DDR_FILER = "DDR FILER";
	private final static String RPC_DG_SENSITIVE_RECORD_ACCESS = "DG SENSITIVE RECORD ACCESS";
	private final static String RPC_DG_SENSITIVE_RECORD_BULLETIN = "DG SENSITIVE RECORD BULLETIN";
	private final static String RPC_DG_CHK_PAT_DIV_MEANS_TEST = "DG CHK PAT/DIV MEANS TEST";
	
	private final static String RPC_XUS_GET_DIVISION = "XUS DIVISION GET";
	private final static String RPC_XUS_SET_DIVISION = "XUS DIVISION SET";
	
	private final static String RPC_XWB_GET_BROKER_INFO = "XWB GET BROKER INFO";
	private final static String RPC_XWB_IM_HERE = "XWB IM HERE";
	private final static String RPC_XUS_GET_USER_INFO = "XUS GET USER INFO";
	private final static String RPC_XUS_CHANGE_VERIFY_CODE = "XUS CVC";
	private final static String RPC_XUS_GET_WELCOME_MESSAGE = "XUS INTRO MSG";
	
	private final static String RPC_ORWCIRN_FACLIST = "ORWCIRN FACLIST"; // CPRS rpc to get treating sites
	
	
	// MAG rpc calls
	private final static String RPC_MAG_LOGOFF = "MAGG LOGOFF";
	
	private static Logger getLogger()
	{
		return Logger.getLogger(VistaCommonQueryFactory.class);
	}
	
	/**
	 * Validate that the TransactionContext contains enough of the fields
	 * to do a local login to Vista.
	 * 
	 * @param transactionContext
	 * @throws InvalidCredentialsException
	 */
	public static void validateLocalCredentials(TransactionContext transactionContext) 
	throws MissingCredentialsException
    {
	    if( transactionContext.getAccessCode() == null || transactionContext.getAccessCode().length() < 1)
			throw new MissingCredentialsException("Security context must include the access code to make a local connection and it does not.");
	    if( transactionContext.getVerifyCode() == null || transactionContext.getVerifyCode().length() < 1 )
			throw new MissingCredentialsException("Security context must include the verify code to make a local connection and it does not.");
    }

	/**
	 * Validate that the TransactionContext contains enough of the fields
	 * to do a remote login to Vista.
	 * 
	 * @param transactionContext
	 * @throws InvalidCredentialsException
	 */
	public static void validateRemoteCredentials(TransactionContext transactionContext) 
	throws InvalidCredentialsException
    {
		if(transactionContext == null)
			throw new InvalidCredentialsException("Transaction context must have principal (security context) to make a remote connection and it does not.");
		
		if( transactionContext.getDuz() == null || transactionContext.getDuz().length() < 1 )
			throw new InvalidCredentialsException("Security context must include the DUZ to make a remote connection and it does not.");
		
		if( transactionContext.getSsn() == null || transactionContext.getSsn().length() < 1 )
			throw new InvalidCredentialsException("Security context must include the SSN to make a remote connection and it does not.");
		
		if(transactionContext.getFullName() == null || transactionContext.getFullName().length() < 1)
			throw new InvalidCredentialsException("Security context must include the full name to make a remote connection and it does not.");
    }
	
	public static void validateBseCredentials(TransactionContext transactionContext)
	throws InvalidCredentialsException
	{
		if(transactionContext == null)
			throw new InvalidCredentialsException("Transaction context must have principal (security context) to make a remote BSE connection and it does not.");
		
		if( transactionContext.getBrokerSecurityToken() == null || transactionContext.getBrokerSecurityToken().length() < 1 )
			throw new InvalidCredentialsException("Security context must include the Broker Security Token to make a remote BSE connection and it does not.");
	}
	
	/**
	 * Build an RPC_SIGNON VistaQuery with the no login credentials.  A local login
	 * does this first then does a RPC_AV_CODE.  This method validates that the AV
	 * codes exist in the transaction context though it does not use them.
	 * 
	 * @param transactionContext
	 * @return
	 * @param transactionContext
	 * @return
	 * @throws InvalidCredentialsException
	 */
	public static VistaQuery createLocalSignonVistaQuery(TransactionContext transactionContext)
	throws MissingCredentialsException
    {
		validateLocalCredentials(transactionContext);
		
	    VistaQuery vm = new VistaQuery(RPC_SIGNON);
		
		return vm;
    }
	
	/**
	 * Build an RPC_SIGNON VistaQuery with the remote login credentials from the
	 * transaction context.
	 * 
	 * @param transactionContext
	 * @return
	 */
	public static VistaQuery createRemoteSignonVistaQuery(TransactionContext transactionContext)
	throws InvalidCredentialsException
    {
		validateRemoteCredentials(transactionContext);
			
	    VistaQuery vm = new VistaQuery(RPC_SIGNON);
		
		String queryParameter = 
			"-31^DVBA_" + 
			"^" + transactionContext.getSsn() + 
			"^" + transactionContext.getFullName() + 
			"^" + transactionContext.getSiteName() + 
			"^" + transactionContext.getSiteNumber() + 
			"^" + transactionContext.getDuz() + 
			"^No Phone";
			
		vm.addParameter( VistaQuery.LITERAL, queryParameter );
	    return vm;
    }
	
	/**
	 * When doing a CAPRI login, an empty XUS SIGNON SETUP rpc must be called first to prime the
	 * system.  Not really sure why, but thats how it is - don't apply logic where it doesn't belong!
	 * @return
	 */
	public static VistaQuery createRemoteSignonEmptyVistaQuery()
	{
		VistaQuery vm = new VistaQuery(RPC_SIGNON);
		return vm;
	}
	
	public static VistaQuery createBseSignonVistaQuery(TransactionContext transactionContext)
	throws InvalidCredentialsException
	{
		validateBseCredentials(transactionContext);
		
	    VistaQuery vm = new VistaQuery(RPC_SIGNON);
	    
	    String queryParameter = "-35^" + 
	    	EncryptionUtils.encrypt(transactionContext.getBrokerSecurityToken());
			
		vm.addParameter( VistaQuery.LITERAL, queryParameter );
	    return vm;
	}
	
	/**
	 * 
	 * @param transactionContext
	 * @return
	 * @throws InvalidCredentialsException
	 */
	public static VistaQuery createAVCodeVistaQuery(TransactionContext transactionContext)
	throws MissingCredentialsException
    {
		validateLocalCredentials(transactionContext);
		
	    VistaQuery loginQuery = new VistaQuery(RPC_AV_CODE);
	    loginQuery.addEncryptedParameter(VistaQuery.LITERAL, 
	    	transactionContext.getAccessCode() + ';' + transactionContext.getVerifyCode() 
	    );
	    return loginQuery;
    }
	
	/**
	 * 
	 * @param userSSN
	 * @return
	 * @throws Exception
	 */
	public static VistaQuery createGetDUZVistaQuery(String userSSN)
	{
		getLogger().info("getDUZ TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		
		VistaQuery msg = new VistaQuery(RPC_GET_VARIABLE_VALUE);
		String arg = "$O(^VA(200,\"SSN\",\"" + userSSN + "\",0))";
		msg.addParameter(VistaQuery.REFERENCE, arg);
		
		return msg;
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 * @throws VistaConnectionException
	 */
	public static VistaQuery createGetContextIENVistaQuery(String context) 
	{
		getLogger().info("getContextIEN TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		
		VistaQuery msg = new VistaQuery(RPC_GET_VARIABLE_VALUE);
		String arg = "$O(^DIC(19,\"B\",\"" + context + "\",0))";
		msg.addParameter(VistaQuery.REFERENCE, arg);
		return msg;
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static VistaQuery createSetContextVistaQuery(String context) 
	{
		getLogger().info("setContext(" + context + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		
        VistaQuery vm = new VistaQuery(RPC_CREATE_CONTEXT);
        vm.addEncryptedParameter(VistaQuery.LITERAL, context);
        
        return vm;
	}
	
	/**
	 * 
	 * @param patientIdentifier
	 * @return
	 */
	public static VistaQuery createGetTreatingSitesVistaQuery(String patientDfn) 
	{
		getLogger().info("getTreatingSites(" + patientDfn + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		
		VistaQuery vm = new VistaQuery(RPC_GET_TREATING_LIST);
		vm.addParameter(VistaQuery.LITERAL, patientDfn);
		
		return vm;
	}
	
	public static VistaQuery createIsPatientRestrictedVistaQuery(String patientDfn) 
	{		
		getLogger().info("isPatientRestricted(" + patientDfn + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");		
		VistaQuery vm = new VistaQuery(RPC_DG_SENSITIVE_RECORD_ACCESS);
		vm.addParameter(VistaQuery.LITERAL, patientDfn);		
		return vm;
	}
	
	public static VistaQuery createLogRestrictedAccessQuery(String patientDfn)
	{
		VistaQuery msg = new VistaQuery(RPC_DG_SENSITIVE_RECORD_BULLETIN);
		msg.addParameter(VistaQuery.LITERAL, patientDfn);
		return msg;
	}	
	
	/**
	 * 
	 * @param subscript
	 * @param contextIEN
	 * @param remoteDuz
	 * @return
	 */
	public static VistaQuery createAssignOptionVistaquery(String subscript, String contextIEN, String remoteDuz)
    {
	    VistaQuery msg = new VistaQuery(RPC_DDR_FILER);
		msg.addParameter(VistaQuery.LITERAL, "ADD");
		String arg = subscript + "^.01^+1," + remoteDuz + ",^"
				+ contextIEN;
		Map<String, String> lst = new HashMap<String, String>();
		lst.put("1", arg);
		msg.addParameter(VistaQuery.LIST, lst);
	    return msg;
    }

	/**
	 * 
	 * @param subscript
	 * @param contextIen
	 * @return
	 */
	public static VistaQuery createGetOptionVistaQuery(String subscript, String contextIen, String remoteDuz)
    {
	    VistaQuery msg = new VistaQuery(RPC_GET_VARIABLE_VALUE);
		String arg = "$O(^VA(200," + remoteDuz;
		if (subscript.equals(DELEGATE_SUBSCRIPT)) {
			arg += ",19.5," + contextIen + ",-1))";
		} else if (subscript.equals(MENU_SUBSCRIPT)) {
			arg += ",203,\"B\"," + contextIen + ",0))";
		}
		msg.addParameter(VistaQuery.REFERENCE, arg);
	    return msg;
    }

	/**
	 * 
	 * @param subscript
	 * @param optNum
	 * @param remoteDuz
	 * @return
	 */
	public static VistaQuery createRemoveOptionVistaQuery(String subscript, String optNum, String remoteDuz)
    {
	    VistaQuery msg = new VistaQuery(RPC_DDR_FILER);
		msg.addParameter(VistaQuery.LITERAL, "EDIT");
		String arg = subscript + "^.01^" + optNum + "," + remoteDuz
				+ ",^@";
		HashMap<String, String> lst = new HashMap<String, String>();
		lst.put("1", arg);
		msg.addParameter(VistaQuery.LIST, lst);
		// msg.addParameter(VistaQuery.LIST,".x",arg);
	    return msg;
    }
	
	/**
	 * Create query to logoff from VistA (close Imaging sessions on server)
	 * @return
	 */
	public static VistaQuery createMagLogoffQuery()
	{
		VistaQuery msg = new VistaQuery(RPC_MAG_LOGOFF);
		return msg;
	}
	
	/**
	 * 
	 * @@param patientICN
	 * @@return
	 * @@throws MethodException
	 */
	public static VistaQuery createGetPatientDFNVistaQuery(String patientICN) 
	{
		VistaQuery vm = new VistaQuery(RPC_CONVERT_ICN_TO_DFN);
		vm.addParameter(VistaQuery.LITERAL, patientICN);
		return vm;
	}
	
	public static VistaQuery createGetDivisionQuery()
	{
		VistaQuery vm = new VistaQuery(RPC_XUS_GET_DIVISION);		
		return vm;
	}
	
	public static VistaQuery createSetDivisionQuery(String division)
	{
		VistaQuery vm = new VistaQuery(RPC_XUS_SET_DIVISION);
		vm.addParameter(VistaQuery.LITERAL, division);
		return vm;
	}
	
	public static VistaQuery createPassthroughQuery(PassthroughInputMethod method)
	{
		VistaQuery vm = new VistaQuery(method.getMethodName());
		
		for(PassthroughParameter parameter : method.getParameters())
		{
			PassthroughParameterType parameterType = parameter.getParameterType();
			if(parameterType == PassthroughParameterType.list)
			{
				Map<String, String> map = new HashMap<String, String>();
				String [] values = parameter.getMultipleValues();
				if(values != null)
				{
					for(String value : values)
					{
						map.put(map.size() + "", value);
					}
				}
				vm.addParameter(getVistaQueryType(parameterType), map);
			}
			else
			{
				// literal and reference look the same
				vm.addParameter(getVistaQueryType(parameterType), parameter.getValue());
			}
		}
		
		return vm;		
	}
	
	private static int getVistaQueryType(PassthroughParameterType parameterType)
	{
		if(parameterType == PassthroughParameterType.literal)
		{
			return VistaQuery.LITERAL;
		}
		else if(parameterType == PassthroughParameterType.list)
		{
			return VistaQuery.LIST;
		}
		else if(parameterType == PassthroughParameterType.reference)
		{
			return VistaQuery.REFERENCE;
		}
		return VistaQuery.LITERAL;
	}
	
	public static VistaQuery createGetVistaTimeout()
	{
		VistaQuery vq = new VistaQuery(RPC_XWB_GET_BROKER_INFO);
		return vq;
	}
	
	public static VistaQuery createKeepAliveQuery()
	{
		VistaQuery vq = new VistaQuery(RPC_XWB_IM_HERE);
		return vq;
	}
	
	public static VistaQuery createGetUserInformationQuery()
	{
		VistaQuery vq = new VistaQuery(RPC_XUS_GET_USER_INFO);
		return vq;
	}
	
	public static VistaQuery createChangeVerifyCodeQuery(String oldVerifyCode, 
			String newVerifyCode, String confirmNewVerifyCode)
	{
		VistaQuery vq = new VistaQuery(RPC_XUS_CHANGE_VERIFY_CODE);
		StringBuilder parameter = new StringBuilder();
		parameter.append(EncryptionUtils.encrypt(oldVerifyCode));
		parameter.append(StringUtils.CARET);
		parameter.append(EncryptionUtils.encrypt(newVerifyCode));
		parameter.append(StringUtils.CARET);
		parameter.append(EncryptionUtils.encrypt(confirmNewVerifyCode));
		vq.addEncryptedParameter(VistaQuery.LITERAL, parameter.toString());		
		return vq;
		
	}
	
	public static VistaQuery createWelcomeMessageQuery()
	{
		VistaQuery vq = new VistaQuery(RPC_XUS_GET_WELCOME_MESSAGE);
		return vq;
	}
	
	public static VistaQuery createMeansTestVistaQuery(String patientDfn) 
	{		
		getLogger().info("meansTest(" + patientDfn + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");		
		VistaQuery vm = new VistaQuery(RPC_DG_CHK_PAT_DIV_MEANS_TEST);
		vm.addParameter(VistaQuery.LITERAL, patientDfn);		
		return vm;
	}

	public static VistaQuery createGetUserSsnQuery(String duz)
	{
		VistaQuery vm = new VistaQuery(RPC_GET_VARIABLE_VALUE);
		String arg = "@\"^VA(200," + duz + ",1)\"";
        vm.addParameter(VistaQuery.REFERENCE, arg);
		return vm;
	}

	public static VistaQuery createCprsGetTreatingSitesVistaQuery(String patientDfn)
	{
		getLogger().info("createCprsGetTreatingSitesVistaQuery(" + patientDfn + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		
		VistaQuery vm = new VistaQuery(RPC_ORWCIRN_FACLIST);
		vm.addParameter(VistaQuery.LITERAL, patientDfn);
		
		return vm;
	}
}
