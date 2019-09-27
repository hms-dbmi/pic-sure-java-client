package edu.harvard.hms.dbmi.avillach.picsure.client;

import edu.harvard.dbmi.avillach.domain.QueryRequest;
import edu.harvard.dbmi.avillach.domain.ResourceInfo;
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

    public String about(String resourceId) {    // for jShell
        return null;
    }
    public String list() {                  // for jShell
        return null;
    }

    public List<UUID> getResources() {
        return this.singltonApiObj.resources();
    }

    public ResourceInfo getResourceInfo(String uuid, QueryRequest qr) {
        return this.singltonApiObj.resourceInfo(UUID.fromString(uuid), qr);
    }

    public IPicSureConnectionAPI getApiObject() {
        return this.singltonApiObj;
    }
}