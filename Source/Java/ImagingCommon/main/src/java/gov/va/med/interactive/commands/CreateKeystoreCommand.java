/**
 * 
 */
package gov.va.med.interactive.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.KeyStore.ProtectionParameter;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import gov.va.med.interactive.Command;
import gov.va.med.interactive.CommandParametersDescription;
import gov.va.med.interactive.CommandProcessor;
import gov.va.med.interactive.CommandTypeValidationException;

/**
 * An example command that has parameters.
 * 
 * @author vhaiswbeckec
 *
 */
public class CreateKeystoreCommand
extends Command<Object>
{
	private static final CommandParametersDescription<?>[] commandParametersDescription = 
		new CommandParametersDescription[]
		{
			new CommandParametersDescription<String>("keystore", String.class, true),
			new CommandParametersDescription<String>("storepass", String.class, true),
			new CommandParametersDescription<String>("alias", String.class, true),
			new CommandParametersDescription<String>("dname", String.class, true)
		};
	/**
	 * The required static method that describes to the command factory what the parameters to the command are.
	 */
	public static CommandParametersDescription<?>[] getCommandParametersDescription()
	{
		return commandParametersDescription;
	}

	/**
	 * The required constructor if the command takes any parameters.
	 * If the command takes no arguments then a no-arg constructor is sufficient.
	 * @param commandParameterValues
	 */
	public CreateKeystoreCommand(String[] commandParameterValues)
	{
		super(commandParameterValues);
	}

	@SuppressWarnings("unused")
	private String getKeystore() 
	throws CommandTypeValidationException
	{
		return (String)getCommandParametersDescription("keystore").getValue(getCommandParameterValues()[0]);
	}
	
	@SuppressWarnings("unused")
	private String getStorepass()
	throws CommandTypeValidationException
	{
		return (String)getCommandParametersDescription("storepass").getValue(getCommandParameterValues()[1]);
	}
	
	@SuppressWarnings("unused")
	private String getAlias()
	throws CommandTypeValidationException
	{
		return (String)getCommandParametersDescription("alias").getValue(getCommandParameterValues()[2]);
	}
	
	@SuppressWarnings("unused")
	private String getDname()
	throws CommandTypeValidationException
	{
		return (String)getCommandParametersDescription("dname").getValue(getCommandParameterValues()[3]);
	}
	
	/**
	 * @see gov.va.med.interactive.Command#processCommand(gov.va.med.interactive.CommandProcessor, java.lang.Object)
	 */
	@Override
	public void processCommand(CommandProcessor<Object> processor, Object config)
	throws Exception
	{
		createKeystore(getKeystore(), getStorepass(), getAlias(), getDname());
	}

	private static void createKeystore(String keystore, String storepass, String alias, String dname) 
	throws KeyStoreException, NoSuchAlgorithmException, CertificateException, CommandTypeValidationException, IOException, InvalidKeyException, SignatureException
	{
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		File keystoreFile = new File(keystore);
		FileOutputStream outStream = new FileOutputStream(keystoreFile);
		
		// loading the keystore from a null stream creates a new instance
		ks.load( null, storepass.toCharArray() );
		
		KeyPairGenerator dsaKeyPairGenerator = KeyPairGenerator.getInstance("RSA");
		dsaKeyPairGenerator.initialize(1024);
		KeyPair keyPair = dsaKeyPairGenerator.generateKeyPair();
		
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();
		
		Signature certificateSignature = Signature.getInstance("MD5withRSA");
		SignedObject so = new SignedObject(publicKey, privateKey, certificateSignature);
		
		Certificate publicCertificate = null;
		Certificate[] publicCertificateChain = new Certificate[]{};
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		
		KeyStore.Entry privateKeyEntry = new KeyStore.PrivateKeyEntry(privateKey, publicCertificateChain);
		
		ProtectionParameter protection = new KeyStore.PasswordProtection(storepass.toCharArray());
		ks.setEntry(alias, privateKeyEntry, protection);
		
		//ks.setKeyEntry(getAlias(), key, chain);
		ks.store( outStream, storepass.toCharArray() );
	}
	
	public static void main(String[] argv)
	{
		try
		{
			createKeystore("/temp/junk.jks", "password", "alias", "CN=vhacvixdevclu1, OU=VistA Imaging, O=US Veterans Administration, L=Silver Spring, S=Maryland, C=US");
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}
}
