package edu.harvard.hms.dbmi.avillach.picsure.client;

import java.net.URL;
import java.util.UUID;

public abstract class BasePicSureResourceConnection {
    public final UUID RESOURCE_UUID;
    public final URL ENDPOINT_URL;
    public final String TOKEN;
    protected final IPicSureConnection protectedConnectionObj;
    protected final IPicSureConnectionAPI protectedApiObj;


    public BasePicSureResourceConnection(BasePicSureAdapter adapter, UUID resource_uuid) {
        this.protectedConnectionObj = adapter.getConnection();
        this.protectedApiObj = adapter.getApiObject();
        this.RESOURCE_UUID = resource_uuid;
        this.ENDPOINT_URL = this.protectedConnectionObj.getENDPOINT();
        this.TOKEN = this.protectedConnectionObj.getTOKEN();
    }

    public void help() {}               // for jShell

}
