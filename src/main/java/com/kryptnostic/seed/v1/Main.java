package com.kryptnostic.seed.v1;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import com.kryptnostic.kodex.v1.crypto.keys.Keystores;
import com.squareup.okhttp.OkHttpClient;

public class Main {
    private static final OkHttpClient      client       = new OkHttpClient();
    static final String                    DEFAULT_HOST = "https://api.kryptnostic.com";
    // static final String DEFAULT_HOST = "https://api.krypt.int";

    static {
        client.setReadTimeout( 0, TimeUnit.MILLISECONDS );
        client.setConnectTimeout( 0, TimeUnit.MILLISECONDS );
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance( "TLS" );
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance( TrustManagerFactory
                    .getDefaultAlgorithm() );
            trustManagerFactory.init( Keystores.loadKeystoreFromResource(
                    "security/rhizome.jks",
                    "example".toCharArray() ) );
            sslContext.init( null, trustManagerFactory.getTrustManagers(), null );

            // disableSSL( sslContext );

            client.setSslSocketFactory( sslContext.getSocketFactory() );
        } catch (
                NoSuchAlgorithmException
                | KeyManagementException
                | KeyStoreException
                | CertificateException
                | IOException e ) {
            e.printStackTrace();
        }
    }
    
    public static void main( String[] args ) {
        System.out.println("Hello world!");
    }
}
