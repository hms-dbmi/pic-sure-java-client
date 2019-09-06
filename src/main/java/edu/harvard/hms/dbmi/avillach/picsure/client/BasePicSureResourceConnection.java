package edu.harvard.hms.dbmi.avillach.picsure.client;

import java.net.URL;
import java.util.UUID;

public abstract class BasePicSureResourceConnection {
    private final UUID RESOURCE_UUID;
    private final URL ENDPOINT_URL;
    private final String TOKEN;
    protected final IPicSureConnection protectedConnectionObj;
    protected final IPicSureConnectionAPI protectedApiObj;


    public BasePicSureResourceConnection(IPicSureConnection connection, UUID resource_uuid) {
        this.protectedConnectionObj = connection;
        this.protectedApiObj = connection.getApiObject();
        this.RESOURCE_UUID = resource_uuid;
        this.ENDPOINT_URL = this.protectedConnectionObj.getENDPOINT();
        this.TOKEN = this.protectedConnectionObj.getTOKEN();
    }

    /**
     * Class function for use in jShell to print help instructions on the screen for this object's use.
     * @since   1.0
     */
    public void help() {
        // for jShell
    }
    public IPicSureConnection getConnection() { return this.protectedConnectionObj; }
    public IPicSureConnectionAPI getApiObject() { return this.protectedApiObj; }
    public UUID getResourceUUID() { return this.RESOURCE_UUID; }
    public URL getEndpointUrl() { return this.ENDPOINT_URL; }
    public String getToken() { return this.TOKEN; }
}
