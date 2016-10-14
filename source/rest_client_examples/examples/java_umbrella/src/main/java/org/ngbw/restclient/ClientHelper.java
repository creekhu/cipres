package org.ngbw.restclient; 

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.jersey.client.filter.HttpBasicAuthFilter;
import org.glassfish.jersey.media.multipart.internal.MultiPartWriter;



/*
	Gets a configured client.
	Todo: I think all of this could be done just once and stored in the session.
*/
public class ClientHelper 
{
	private static final Log log = LogFactory.getLog(ClientHelper.class.getName());

	public static Client getClient(String username, String password) 
	{
		return getPermissiveClient(username, password);
	}

	private static Client getPermissiveClient(String username, String password) 
	{
		/*
			This code creates a client that will ignore bad ssl certificates. 
		*/
		TrustManager[ ] certs = new TrustManager[ ] 
		{
			new X509TrustManager() 
			{
				@Override
				public X509Certificate[] getAcceptedIssuers() 
				{ return null; }

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException 
				{ }

				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException 
				{ }
			}
		};

		SSLContext ctx = null;
		try 
		{
			ctx = SSLContext.getInstance("TLS");
			ctx.init(null, certs, new SecureRandom());
			log.debug("Initialized new ssl context for client.");
		} 
		catch (java.security.GeneralSecurityException ex) 
		{ }
		ClientBuilder cb = ClientBuilder.newBuilder().sslContext(ctx).hostnameVerifier(
				new HostnameVerifier() 
				{
					@Override
					public boolean verify(String hostname, SSLSession session) { return true; }
				});

		Client client = cb.build();
		if (username != null)
		{
			client.register(new HttpBasicAuthFilter(username, password));
		}
		client.register(MultiPartWriter.class);
		return client; 
	}




	/*
		If you don't want to ignore bad certs, this is all you'd need to do:
	*/

	private static Client getStrictClient(String username, String password) 
	{
		Client client = ClientBuilder.newClient();
		if (username != null)
		{
			client.register(new HttpBasicAuthFilter(username, password));
		}
		client.register(MultiPartWriter.class);
		return client;
	}


}
