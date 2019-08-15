package edu.harvard.hms.dbmi.avillach.picsure.client.impl;

import edu.harvard.hms.dbmi.avillach.picsure.client.IPicSureConnectionAPI;

import java.net.URL;
import java.util.UUID;

public class PicSureConnectionAPI implements IPicSureConnectionAPI {
    public final URL ENDPOINT;
    public final String TOKEN;

    public PicSureConnectionAPI(URL url, String token) {
        this.ENDPOINT = url;
        this.TOKEN = token;
        this.init(url, token, false);
    }
    public PicSureConnectionAPI(URL url, String token, boolean allowSelfSignedSSL) {
        this.ENDPOINT = url;
        this.TOKEN = token;
        this.init(url, token, allowSelfSignedSSL);
    }


    private void init(URL url, String token, boolean allowSelfSignedSSL) {
    }

    public String info(UUID resource_uuid) {
        return null;
    }

    public String search(UUID resource_uuid, String query) {
        return null;
    }

    public String asynchQuery(UUID resource_uuid, String query) {
        return null;
    }

    public String syncQuery(UUID resource_uuid, String query) {
        return null;
    }

    public String queryStatus(UUID resource_uuid, UUID query_uuid) {
        return null;
    }

    public String queryResults(UUID resource_uuid, UUID query_uuid) {
        return null;
    }
}
