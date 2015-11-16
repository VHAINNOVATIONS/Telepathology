package gov.va.med.imaging.proxy.ssl;

/*
 * NOTE: this class is almost a complete copy of the Apache Common version found at:
 * /httpclient/src/contrib/org/apache/commons/httpclient/contrib/ssl/AuthSSLProtocolSocketFactory.java
 * with some minor rewrite to use log4J. 
 * 
 * $Header: /cvs/ImagingExchangeBaseWebProxy/main/src/java/gov/va/med/imaging/proxy/ssl/AuthSSLProtocolSocketFactory.java,v 1.2 2010/11/18 15:46:24 vhaiswbeckec Exp $
 * $Revision: 1.2 $
 * $Date: 2010/11/18 15:46:24 $
 *
 * ====================================================================
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import javax.net.SocketFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * <p>
 * AuthSSLProtocolSocketFactory can be used to validate the identity of the
 * HTTPS server against a list of trusted certificates and to authenticate to
 * the HTTPS server using a private key.
 * </p>
 * 
 * <p>
 * AuthSSLProtocolSocketFactory will enable server authentication when supplied
 * with a {@link KeyStore truststore} file containg one or several trusted
 * certificates. The client secure socket will reject the connection during the
 * SSL session handshake if the target HTTPS server attempts to authenticate
 * itself with a non-trusted certificate.
 * </p>
 * 
 * <p>
 * Use JDK keytool utility to import a trusted certificate and generate a
 * truststore file:
 * 
 * <pre>
 *     keytool -import -alias &quot;my server cert&quot; -file server.crt -keystore my.truststore
 * </pre>
 * 
 * </p>
 * 
 * <p>
 * AuthSSLProtocolSocketFactory will enable client authentication when supplied
 * with a {@link KeyStore keystore} file containg a private key/public
 * certificate pair. The client secure socket will use the private key to
 * authenticate itself to the target HTTPS server during the SSL session
 * handshake if requested to do so by the server. The target HTTPS server will
 * in its turn verify the certificate presented by the client in order to
 * establish client's authenticity
 * </p>
 * 
 * <p>
 * Use the following sequence of actions to generate a keystore file
 * </p>
 * <ul>
 * <li>
 * <p>
 * Use JDK keytool utility to generate a new key
 * 
 * <pre>
 * keytool -genkey -v -alias &quot;my client key&quot; -validity 365 -keystore my.keystore
 * </pre>
 * 
 * For simplicity use the same password for the key as that of the keystore
 * </p>
 * </li>
 * <li>
 * <p>
 * Issue a certificate signing request (CSR)
 * 
 * <pre>
 * keytool -certreq -alias &quot;my client key&quot; -file mycertreq.csr -keystore my.keystore
 * </pre>
 * 
 * </p>
 * </li>
 * <li>
 * <p>
 * Send the certificate request to the trusted Certificate Authority for
 * signature. One may choose to act as her own CA and sign the certificate
 * request using a PKI tool, such as OpenSSL.
 * </p>
 * </li>
 * <li>
 * <p>
 * Import the trusted CA root certificate
 * 
 * <pre>
 * keytool -import -alias &quot;my trusted ca&quot; -file caroot.crt -keystore my.keystore
 * </pre>
 * 
 * </p>
 * </li>
 * <li>
 * <p>
 * Import the PKCS#7 file containg the complete certificate chain
 * 
 * <pre>
 * keytool -import -alias &quot;my client key&quot; -file mycert.p7 -keystore my.keystore
 * </pre>
 * 
 * </p>
 * </li>
 * <li>
 * <p>
 * Verify the content the resultant keystore file
 * 
 * <pre>
 * keytool -list -v -keystore my.keystore
 * </pre>
 * 
 * </p>
 * </li>
 * </ul>
 * <p>
 * Example of using custom protocol socket factory for a specific host:
 * 
 * <pre>
 * Protocol authhttps = new Protocol(&quot;https&quot;, new AuthSSLProtocolSocketFactory(new URL(&quot;file:my.keystore&quot;), &quot;mypassword&quot;,
 * 	new URL(&quot;file:my.truststore&quot;), &quot;mypassword&quot;), 443);
 * 
 * HttpClient client = new HttpClient();
 * client.getHostConfiguration().setHost(&quot;localhost&quot;, 443, authhttps);
 * // use relative url only
 * GetMethod httpget = new GetMethod(&quot;/&quot;);
 * client.executeMethod(httpget);
 * </pre>
 * 
 * </p>
 * <p>
 * Example of using custom protocol socket factory per default instead of the
 * standard one:
 * 
 * <pre>
 * Protocol authhttps = new Protocol(&quot;https&quot;, new AuthSSLProtocolSocketFactory(new URL(&quot;file:my.keystore&quot;), &quot;mypassword&quot;,
 * 	new URL(&quot;file:my.truststore&quot;), &quot;mypassword&quot;), 443);
 * Protocol.registerProtocol(&quot;https&quot;, authhttps);
 * 
 * HttpClient client = new HttpClient();
 * GetMethod httpget = new GetMethod(&quot;https://localhost/&quot;);
 * client.executeMethod(httpget);
 * </pre>
 * 
 * </p>
 * 
 * @author <a href="mailto:oleg -at- ural.ru">Oleg Kalnichevski</a>
 * 
 *         <p>
 *         DISCLAIMER: HttpClient developers DO NOT actively support this
 *         component. The component is provided as a reference material, which
 *         may be inappropriate for use without additional customization.
 *         </p>
 */

public class AuthSSLProtocolSocketFactory
	implements SecureProtocolSocketFactory
{
	private Logger log = Logger.getLogger(this.getClass());

	private final URL keystoreUrl;
	private final String keystorePassword;
	private final URL truststoreUrl;
	private final String truststorePassword;
	private SSLContext sslcontext = null;

	/**
	 * Constructor for AuthSSLProtocolSocketFactory. Either a keystore or
	 * truststore file must be given. Otherwise SSL context initialization error
	 * will result.
	 * 
	 * @param keystoreUrl
	 *            URL of the keystore file. May be <tt>null</tt> if HTTPS client
	 *            authentication is not to be used.
	 * @param keystorePassword
	 *            Password to unlock the keystore. IMPORTANT: this
	 *            implementation assumes that the same password is used to
	 *            protect the key and the keystore itself.
	 * @param truststoreUrl
	 *            URL of the truststore file. May be <tt>null</tt> if HTTPS
	 *            server authentication is not to be used.
	 * @param truststorePassword
	 *            Password to unlock the truststore.
	 */
	public AuthSSLProtocolSocketFactory(final URL keystoreUrl, final String keystorePassword, final URL truststoreUrl,
		final String truststorePassword)
	{
		super();
		this.keystoreUrl = keystoreUrl;
		this.keystorePassword = keystorePassword;
		this.truststoreUrl = truststoreUrl;
		this.truststorePassword = truststorePassword;
	}

	private static KeyStore createKeyStore(final URL url, final String password) 
	throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException
	{
		if (url == null)
			throw new IllegalArgumentException("Keystore url may not be null");

		Logger.getLogger(AuthSSLProtocolSocketFactory.class).debug("Initializing key store");
		KeyStore keystore = KeyStore.getInstance("jks");
		InputStream is = null;
		try
		{
			is = url.openStream();
			keystore.load(is, password != null ? password.toCharArray() : null);
		}
		finally
		{
			if (is != null)
				is.close();
		}
		return keystore;
	}

	private static KeyManager[] createKeyManagers(final KeyStore keystore, final String password)
		throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException
	{
		if (keystore == null)
			throw new IllegalArgumentException("Keystore may not be null");

		Logger.getLogger(AuthSSLProtocolSocketFactory.class).debug("Initializing key manager");
		KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmfactory.init(keystore, password != null ? password.toCharArray() : null);
		return kmfactory.getKeyManagers();
	}

	private static TrustManager[] createTrustManagers(final KeyStore keystore) throws KeyStoreException,
		NoSuchAlgorithmException
	{
		if (keystore == null)
			throw new IllegalArgumentException("Keystore may not be null");

		Logger.getLogger(AuthSSLProtocolSocketFactory.class).debug("Initializing trust manager");
		TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmfactory.init(keystore);
		TrustManager[] trustmanagers = tmfactory.getTrustManagers();
		for (int i = 0; i < trustmanagers.length; i++)
		{
			if (trustmanagers[i] instanceof X509TrustManager)
			{
				trustmanagers[i] = new AuthSSLX509TrustManager((X509TrustManager) trustmanagers[i]);
			}
		}
		return trustmanagers;
	}

	private SSLContext createSSLContext()
	{
		try
		{
			KeyManager[] keymanagers = null;
			TrustManager[] trustmanagers = null;
			if (this.keystoreUrl != null)
			{
				KeyStore keystore = createKeyStore(this.keystoreUrl, this.keystorePassword);
				if (Logger.getLogger(AuthSSLProtocolSocketFactory.class).isDebugEnabled())
					logKeystoreContents("keystore", keystore);

				keymanagers = createKeyManagers(keystore, this.keystorePassword);
			}
			if (this.truststoreUrl != null)
			{
				KeyStore truststore = createKeyStore(this.truststoreUrl, this.truststorePassword);
				if (Logger.getLogger(AuthSSLProtocolSocketFactory.class).isDebugEnabled())
					logKeystoreContents("truststore", truststore);

				trustmanagers = createTrustManagers(truststore);
			}
			SSLContext sslcontext = SSLContext.getInstance("SSL");
			sslcontext.init(keymanagers, trustmanagers, null);
			return sslcontext;
		}
		catch (NoSuchAlgorithmException e)
		{
			Logger.getLogger(AuthSSLProtocolSocketFactory.class).error(e.getMessage(), e);
			throw new AuthSSLInitializationError("Unsupported algorithm exception: " + e.getMessage());
		}
		catch (KeyStoreException e)
		{
			Logger.getLogger(AuthSSLProtocolSocketFactory.class).error(e.getMessage(), e);
			throw new AuthSSLInitializationError("Keystore exception: " + e.getMessage());
		}
		catch (GeneralSecurityException e)
		{
			Logger.getLogger(AuthSSLProtocolSocketFactory.class).error(e.getMessage(), e);
			throw new AuthSSLInitializationError("Key management exception: " + e.getMessage());
		}
		catch (IOException e)
		{
			Logger.getLogger(AuthSSLProtocolSocketFactory.class).error(e.getMessage(), e);
			throw new AuthSSLInitializationError("I/O error reading keystore/truststore file: " + e.getMessage());
		}
	}

	/**
	 * 
	 * @param keystoreName
	 * @param keystore
	 * @throws KeyStoreException
	 */
	private void logKeystoreContents(String keystoreName, KeyStore keystore) throws KeyStoreException
	{
		Logger.getLogger(AuthSSLProtocolSocketFactory.class).debug("Keystore : '" + keystoreName + "':");
		for (Enumeration<String> aliases = keystore.aliases(); aliases.hasMoreElements();)
		{
			String alias = (String) aliases.nextElement();
			Certificate[] certs = keystore.getCertificateChain(alias);
			if (certs != null)
			{
				Logger.getLogger(AuthSSLProtocolSocketFactory.class).debug("Certificate Chain '" + alias + "':");
				for (Certificate cert : certs)
					logCertificateContents(cert);
			}
			else
			{
				Certificate cert = keystore.getCertificate(alias);
				Logger.getLogger(AuthSSLProtocolSocketFactory.class).debug(
					"Trusted Certificate Authority '" + alias + "':");
				logCertificateContents(cert);
			}
		}
	}

	private void logCertificateContents(Certificate cert)
	{
		if (cert instanceof X509Certificate)
		{
			X509Certificate x509Cert = (X509Certificate) cert;
			Logger.getLogger(AuthSSLProtocolSocketFactory.class).debug(" X509 Certificate :");
			Logger.getLogger(AuthSSLProtocolSocketFactory.class).debug("  Subject DN: " + x509Cert.getSubjectDN());
			Logger.getLogger(AuthSSLProtocolSocketFactory.class).debug(
				"  Signature Algorithm: " + x509Cert.getSigAlgName());
			Logger.getLogger(AuthSSLProtocolSocketFactory.class).debug(
				"  Signature: " + x509Cert.getPublicKey().toString());
			Logger.getLogger(AuthSSLProtocolSocketFactory.class).debug("  Valid from: " + x509Cert.getNotBefore());
			Logger.getLogger(AuthSSLProtocolSocketFactory.class).debug("  Valid until: " + x509Cert.getNotAfter());
			Logger.getLogger(AuthSSLProtocolSocketFactory.class).debug("  Issuer: " + x509Cert.getIssuerDN());
		}
		else
			Logger.getLogger(AuthSSLProtocolSocketFactory.class).debug(" Certificate :" + cert.getType());
	}

	private SSLContext getSSLContext()
	{
		if (this.sslcontext == null)
		{
			this.sslcontext = createSSLContext();
		}
		return this.sslcontext;
	}

	/**
	 * Attempts to get a new socket connection to the given host within the
	 * given time limit.
	 * <p>
	 * To circumvent the limitations of older JREs that do not support connect
	 * timeout a controller thread is executed. The controller thread attempts
	 * to create a new socket within the given limit of time. If socket
	 * constructor does not return until the timeout expires, the controller
	 * terminates and throws an {@link ConnectTimeoutException}
	 * </p>
	 * 
	 * @param host
	 *            the host name/IP
	 * @param port
	 *            the port on the host
	 * @param clientHost
	 *            the local host name/IP to bind the socket to
	 * @param clientPort
	 *            the port on the local machine
	 * @param params
	 *            {@link HttpConnectionParams Http connection parameters}
	 * 
	 * @return Socket a new socket
	 * 
	 * @throws IOException
	 *             if an I/O error occurs while creating the socket
	 * @throws UnknownHostException
	 *             if the IP address of the host cannot be determined
	 */
	public Socket createSocket(
		final String host, final int port, 
		final InetAddress localAddress, final int localPort,
		final HttpConnectionParams params) 
	throws IOException, UnknownHostException, ConnectTimeoutException
	{
		if (params == null)
			throw new IllegalArgumentException("Parameters may not be null");

		int timeout = params.getConnectionTimeout();
		SocketFactory socketfactory = getSSLContext().getSocketFactory();
		if (timeout == 0)
		{
			return socketfactory.createSocket(host, port, localAddress, localPort);
		}
		else
		{
			Socket socket = socketfactory.createSocket();
			SocketAddress localaddr = new InetSocketAddress(localAddress, localPort);
			SocketAddress remoteaddr = new InetSocketAddress(host, port);
			socket.bind(localaddr);
			socket.connect(remoteaddr, timeout);
			return socket;
		}
	}

	/**
	 * @see SecureProtocolSocketFactory#createSocket(java.lang.String,int,java.net.InetAddress,int)
	 */
	public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort) 
	throws IOException, UnknownHostException
	{
		return getSSLContext().getSocketFactory().createSocket(host, port, clientHost, clientPort);
	}

	/**
	 * @see SecureProtocolSocketFactory#createSocket(java.lang.String,int)
	 */
	public Socket createSocket(String host, int port) 
	throws IOException, UnknownHostException
	{
		return getSSLContext().getSocketFactory().createSocket(host, port);
	}

	/**
	 * @see SecureProtocolSocketFactory#createSocket(java.net.Socket,java.lang.String,int,boolean)
	 */
	public Socket createSocket(Socket socket, String host, int port, boolean autoClose) 
	throws IOException, UnknownHostException
	{
		return getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);
	}
}