package edu.harvard.hms.dbmi.avillach.picsure.client;

import edu.harvard.hms.dbmi.avillach.picsure.client.api.IPicSureConnection;
import edu.harvard.hms.dbmi.avillach.picsure.client.api.IPicSureConnectionAPI;

import java.net.MalformedURLException;
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
        URL tempEndpoint = null;
        // make sure the endpoint ends with a "/"
        if (url.getFile().endsWith("/")) {
            tempEndpoint = url;
        } else {
            try {
                tempEndpoint = new URL(url, url.getFile() + "/");
            } catch (MalformedURLException e) {
                // TODO: throw error
                e.printStackTrace();
            }
        }
        this.ENDPOINT = tempEndpoint;
        this.TOKEN = token;
        this.AllowSelfSigned = allowSelfSignedSSL;
        this.singltonApiObj = new PicSureConnectionAPI(url, token, allowSelfSignedSSL);
    }

    public String getTOKEN() {
        return this.TOKEN;
    }

    public URL getENDPOINT() {
        return this.ENDPOINT;
    }

    /**
     * Class function for use in jShell to print help instructions on the screen for this object's use.
     * @since   1.0
     */
    public void help() {
        // for jShell
    }


    public String about(String resourceId) {
        return null;
    }     // for jShell
    public String getResources() {
        return null;
    }               // for jShell

    public List<UUID> list() {
        return this.singltonApiObj.resources();
//        return new ArrayList<UUID>();
    }

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