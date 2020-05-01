package dm.sime.com.kharetati.util;

import android.net.SSLCertificateSocketFactory;
import android.net.SSLSessionCache;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

public class ClientSSLSocketFactory extends SSLCertificateSocketFactory {
    public static SSLContext sslContext;

    /**
     * @param handshakeTimeoutMillis
     * @deprecated
     */
    public ClientSSLSocketFactory(int handshakeTimeoutMillis) {
        super(handshakeTimeoutMillis);
    }

    public static SSLSocketFactory getSocketFactory(){
        try
        {
            X509TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {}

                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {}

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {

                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {

                }

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            sslContext = SSLContext.getInstance("TLSv1.1");
            sslContext.init(null, new TrustManager[] { tm }, null);

            SSLSocketFactory ssf = ClientSSLSocketFactory.getDefault(10000, new SSLSessionCache(ApplicationController.getInstance()));

            return ssf;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }
}