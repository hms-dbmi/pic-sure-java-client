package edu.harvard.hms.dbmi.avillach.picsure.client;

import java.util.UUID;

public interface IResourceConnection {
    public UUID RESOURCE_UUID = null;
    
    public void init(IConnection connection, UUID resource_uuid);
    public void help();

}
