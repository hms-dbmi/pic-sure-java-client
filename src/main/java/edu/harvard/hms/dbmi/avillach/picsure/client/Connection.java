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

/**
 * Connection class instances are used to connect to a PIC-SURE network.
 * A Connection class also allows listing all PIC-SURE Resources that are
 * hosted on the currently connected network and getting detail information
 * about each of those resources.
 * @author  Nick Benik
 * @version %I%, %G%
 * @since   1.0
 */
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