package edu.harvard.hms.dbmi.avillach.picsure.client;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Connection implements IPicSureConnection {
    public final URL ENDPOINT;
    public final String TOKEN;

    private boolean AllowSelfSigned = false;
    private IPicSureConnectionAPI singltonApiObj;

    protected Connection(URL url, String token, boolean allowSelfSignedSSL) {
        // this is the final processing constructor
        this.ENDPOINT = url;
        this.TOKEN = token;
        this.AllowSelfSigned = allowSelfSignedSSL;
    }

    public String getTOKEN() {
        return TOKEN;
    }

    public URL getENDPOINT() {
        return ENDPOINT;
    }

    public void help() {}                                       // for jShell
    public String about(String resourceId) {
        return null;
    }     // for jShell
    public String getResources() {
        return null;
    }               // for jShell

    public List<UUID> list() { return new ArrayList<UUID>(); }

    public String getInfo(String uuid) {
        return null;
    }

    public IPicSureConnectionAPI getApiObject() {
        if (singltonApiObj == null) {
            singltonApiObj = new PicSureConnectionAPI(this.ENDPOINT, this.TOKEN, this.AllowSelfSigned);
        }
        return singltonApiObj;
    }
}