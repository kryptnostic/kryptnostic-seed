package com.kryptnostic.seed.v1;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Collection;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import retrofit.client.OkClient;

import com.kryptnostic.api.v1.client.DefaultKryptnosticClient;
import com.kryptnostic.api.v1.client.DefaultKryptnosticServicesFactory;
import com.kryptnostic.api.v1.client.InMemoryStore;
import com.kryptnostic.api.v1.client.KryptnosticRestAdapter;
import com.kryptnostic.api.v1.security.IrisConnection;
import com.kryptnostic.directory.v1.principal.UserKey;
import com.kryptnostic.kodex.v1.client.KryptnosticClient;
import com.kryptnostic.kodex.v1.client.KryptnosticConnection;
import com.kryptnostic.kodex.v1.client.KryptnosticServicesFactory;
import com.kryptnostic.kodex.v1.crypto.keys.Keystores;
import com.kryptnostic.kodex.v1.exceptions.types.IrisException;
import com.kryptnostic.kodex.v1.exceptions.types.ResourceNotFoundException;
import com.kryptnostic.storage.v1.StorageClient;
import com.squareup.okhttp.OkHttpClient;

public class Main {
    static final String       DEFAULT_HOST = "https://api.kryptnostic.com/v1";

    /**
     * Basic demo credentials
     */
    static final String       REALM        = "krypt";
    static final String       USERNAME     = "demo";
    static final String       PASSWORD     = "demo";

    static final OkHttpClient okHttpClient = new OkHttpClient();

    /**
     * Simple demo that prints a list of documents
     * 
     * @param args
     */
    public static void main( String[] args ) {
        System.out.println( "Hello world!" );

        KryptnosticClient kcli = null;
        try {
            kcli = createClient();
        } catch ( IrisException e ) {
            e.printStackTrace();
        } catch ( ResourceNotFoundException e ) {
            e.printStackTrace();
        }
        if ( kcli != null ) {
            // list your documents
            listObjects( kcli );
        } else {
            System.out.println( "There was an error creating the Kryptnostic Client" );
        }

        /**
         * note: This version of OkHttp connection pool does not timeout the connection pool, so there may be a delay in
         * termination at this point we will upgrade OkHttp to resolve this issue, see more here:
         * https://github.com/square/okhttp/issues/1306
         */
    }

    /**
     * Use a Kryptnostic client to list a users' objects
     * 
     * @param kcli
     */
    private static void listObjects( KryptnosticClient kcli ) {
        StorageClient storage = kcli.getStorageClient();
        Collection<String> objIds = storage.getObjectIds();
        for ( String id : objIds ) {
            System.out.println( "Found object with id: " + id );
        }
    }

    /**
     * Create a basic working Kryptnostic client
     * 
     * @return
     * @throws IrisException
     * @throws ResourceNotFoundException
     */
    private static KryptnosticClient createClient() throws IrisException, ResourceNotFoundException {
        UserKey userKey = new UserKey( REALM, USERNAME );

        // Set up http transport
        OkClient okClient = createOkClient();

        // Create Kryptnostic connection with an InMemoryStore
        // You can use FileStore to cache user files to disk
        KryptnosticConnection connection = new IrisConnection(
                DEFAULT_HOST,
                userKey,
                PASSWORD,
                new InMemoryStore(),
                okClient );
        // The factory sets up the proper http endpoints used to interact with Kryptnostic's API
        KryptnosticServicesFactory factory = new DefaultKryptnosticServicesFactory( KryptnosticRestAdapter.create(
                okClient,
                connection ) );

        KryptnosticClient kcli = new DefaultKryptnosticClient( factory, connection );
        return kcli;
    }

    /**
     * Create an HTTP client that trusts https://api.kryptnostic.com
     * 
     * @return
     */
    private static OkClient createOkClient() {
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance( "TLS" );
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance( TrustManagerFactory
                    .getDefaultAlgorithm() );
            trustManagerFactory.init( Keystores.loadKeystoreFromResource(
                    "security/rhizome.jks",
                    "example".toCharArray() ) );
            sslContext.init( null, trustManagerFactory.getTrustManagers(), null );

            okHttpClient.setSslSocketFactory( sslContext.getSocketFactory() );
        } catch (
                NoSuchAlgorithmException
                | KeyManagementException
                | KeyStoreException
                | CertificateException
                | IOException e ) {
            e.printStackTrace();
        }
        return new OkClient( okHttpClient );
    }
}
