package edu.harvard.hms.dbmi.avillach.picsure.client;

import java.util.UUID;

public abstract class BasePicSureAdapter {
    private IPicSureConnection refConnectionObj;
    private IPicSureConnectionAPI refApiObj;
    private UUID resourceUuid;

    public BasePicSureAdapter(Connection connection, UUID resource_uuid) {
        this.refConnectionObj = (IPicSureConnection) connection;
        this.refApiObj = (PicSureConnectionAPI) connection.getApiObject();
        this.resourceUuid = resource_uuid;
    }

    public IPicSureConnection getConnection() {
        return this.refConnectionObj;
    }

    public IPicSureConnectionAPI getApiObject() {
        return this.refApiObj;
    }

    public BasePicSureResourceConnection useResource(UUID resource_uuid) {
        return null;
    }
}
