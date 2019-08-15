package edu.harvard.hms.dbmi.avillach.picsure.client.impl;

import edu.harvard.hms.dbmi.avillach.picsure.client.IClient;
import edu.harvard.hms.dbmi.avillach.picsure.client.IConnection;

import java.net.MalformedURLException;
import java.net.URL;

public class Client implements IClient {
    protected URL endpointURL = null;
    protected String token = null;

    public void version() {}

    public void help() {}


    public IConnection connect(URL url, String token) {
        return constructConnection(url, token, false);
    }
    public IConnection connect(URL url, String token, Boolean allowSelfSignedSSL) {
        return constructConnection(url, token, allowSelfSignedSSL);
    }
    public IConnection connect(String url, String token) {
        URL urlObj = null;
        try {
            urlObj = new URL(url);
            return constructConnection(urlObj, token, false);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public IConnection connect(String url, String token, Boolean allowSelfSignedSSL) {
        URL urlObj = null;
        try {
            urlObj = new URL(url);
            return constructConnection(urlObj, token, allowSelfSignedSSL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
    private IConnection constructConnection(URL url, String token, Boolean allowSelfSignedSSL) {
        return new Connection(url,token,allowSelfSignedSSL);
    }
}
