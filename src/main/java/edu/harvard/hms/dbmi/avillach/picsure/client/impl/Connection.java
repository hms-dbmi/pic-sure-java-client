package edu.harvard.hms.dbmi.avillach.picsure.client.impl;

import edu.harvard.hms.dbmi.avillach.picsure.client.IConnection;
import edu.harvard.hms.dbmi.avillach.picsure.client.IPicSureConnectionAPI;

import java.net.URL;

public class Connection implements IConnection {

    public final URL ENDPOINT;
    public final String TOKEN;
    private boolean AllowSelfSigned = false;

    protected Connection(URL url, String token, boolean allowSelfSignedSSL) {
        // this is the final processing constructor
        this.ENDPOINT = url;
        this.TOKEN = token;
        this.AllowSelfSigned = allowSelfSignedSSL;
    }

    public void help() {
    }

    public String about(String resourceId) {
        return null;
    }

    public void list() {
    }

    public String getInfo(String uuid) {
        return null;
    }

    public String getResources() {
        return null;
    }

    public IPicSureConnectionAPI _api_obj() {
        IPicSureConnectionAPI ret = new PicSureConnectionAPI(this.ENDPOINT, this.TOKEN, this.AllowSelfSigned);
        return ret;
    }
}