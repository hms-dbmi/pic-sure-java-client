package edu.harvard.hms.dbmi.avillach.picsure.client.api;

import java.net.URL;
import java.util.UUID;

/**
 * BasePicSureResourceConnection is the core functionality which needs to
 * implimented by a PIC-SURE Resource in its PicSureAdapter class.
 * @author  Nick Benik
 * @version %I%, %G%
 * @since   1.0
 * @see BasePicSureAdapter#useResource(IPicSureConnection, UUID)
 */

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


    public IPicSureConnection getConnection() {
        return this.protectedConnectionObj;
    }


    public IPicSureConnectionAPI getApiObject() {
        return this.protectedApiObj;
    }


    public UUID getResourceUUID() {
        return this.RESOURCE_UUID;
    }


    public URL getEndpointUrl() {
        return this.ENDPOINT_URL;
    }


    public String getToken() {
        return this.TOKEN;
    }
}
