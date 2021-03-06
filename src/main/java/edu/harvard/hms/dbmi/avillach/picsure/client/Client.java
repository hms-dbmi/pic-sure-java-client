package edu.harvard.hms.dbmi.avillach.picsure.client;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * The Client class is entry-point into accessing a
 * PIC-SURE network and resources that are hosted on it.
 * @author  Nick Benik
 * @version %I%, %G%
 * @since   1.0
 */
public class Client {
    protected URL endpointURL = null;
    protected String token = null;


    public static Connection connect(URL url, String token) {
        return new Connection(url, token, false);
    }


    public static Connection connect(URL url, String token, Boolean allowSelfSignedSSL) {
        return new Connection(url, token, allowSelfSignedSSL);
    }


    public static Connection connect(String url, String token) {
        URL urlObj = null;
        try {
            urlObj = new URL(url);
            return new Connection(urlObj, token, false);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Connection connect(String url, String token, Boolean allowSelfSignedSSL) {
        URL urlObj = null;
        try {
            urlObj = new URL(url);
            return new Connection(urlObj, token, allowSelfSignedSSL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
